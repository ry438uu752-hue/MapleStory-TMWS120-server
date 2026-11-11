/*
 * Decompiled with CFR 0.152.
 */
package scripting;

import client.MapleCharacter;
import client.MapleQuestStatus;
import handling.channel.ChannelServer;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.script.ScriptException;
import scripting.EventManager;
import server.MapleCarnivalParty;
import server.MapleItemInformationProvider;
import server.MapleSquad;
import server.Timer;
import server.life.MapleMonster;
import server.maps.MapleMap;
import server.maps.MapleMapFactory;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.packet.UIPacket;

public class EventInstanceManager {
    private List<MapleCharacter> chars = new LinkedList<MapleCharacter>();
    private List<Integer> dced = new LinkedList<Integer>();
    private List<MapleMonster> mobs = new LinkedList<MapleMonster>();
    private Map<Integer, Integer> killCount = new HashMap<Integer, Integer>();
    private EventManager em;
    private int channel;
    private String name;
    private Properties props = new Properties();
    private long timeStarted = 0L;
    private long eventTime = 0L;
    private List<Integer> mapIds = new LinkedList<Integer>();
    private List<Boolean> isInstanced = new LinkedList<Boolean>();
    private ScheduledFuture<?> eventTimer;
    private final ReentrantReadWriteLock mutex = new ReentrantReadWriteLock();
    private final Lock rL = this.mutex.readLock();
    private final Lock wL = this.mutex.writeLock();
    private boolean disposed = false;

    public EventInstanceManager(EventManager em, String name, int channel) {
        this.em = em;
        this.name = name;
        this.channel = channel;
    }

    public void registerPlayer(MapleCharacter chr) {
        if (this.disposed || chr == null) {
            return;
        }
        try {
            this.wL.lock();
            try {
                this.chars.add(chr);
            }
            finally {
                this.wL.unlock();
            }
            chr.setEventInstance(this);
            this.em.getIv().invokeFunction("playerEntry", this, chr);
        }
        catch (NullPointerException ex) {
            FileoutputUtil.outputFileError("Logs/Log_Script_Except.rtf", ex);
            ex.printStackTrace();
        }
        catch (Exception ex) {
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name" + this.em.getName() + ", Instance name : " + this.name + ", method Name : playerEntry:\n" + ex);
            System.out.println("Event name" + this.em.getName() + ", Instance name : " + this.name + ", method Name : playerEntry:\n" + ex);
        }
    }

    public void changedMap(MapleCharacter chr, int mapid) {
        if (this.disposed) {
            return;
        }
        try {
            this.em.getIv().invokeFunction("changedMap", this, chr, mapid);
        }
        catch (NullPointerException nullPointerException) {
        }
        catch (Exception ex) {
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name" + this.em.getName() + ", Instance name : " + this.name + ", method Name : changedMap:\n" + ex);
            System.out.println("Event name" + this.em.getName() + ", Instance name : " + this.name + ", method Name : changedMap:\n" + ex);
        }
    }

    public void timeOut(long delay, final EventInstanceManager eim) {
        if (this.disposed || eim == null) {
            return;
        }
        this.eventTimer = Timer.EventTimer.getInstance().schedule(new Runnable(){

            @Override
            public void run() {
                if (EventInstanceManager.this.disposed || eim == null || EventInstanceManager.this.em == null) {
                    return;
                }
                try {
                    EventInstanceManager.this.em.getIv().invokeFunction("scheduledTimeout", eim);
                }
                catch (Exception ex) {
                    FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name" + EventInstanceManager.this.em.getName() + ", Instance name : " + EventInstanceManager.this.name + ", method Name : scheduledTimeout:\n" + ex);
                    System.out.println("Event name" + EventInstanceManager.this.em.getName() + ", Instance name : " + EventInstanceManager.this.name + ", method Name : scheduledTimeout:\n" + ex);
                }
            }
        }, delay);
    }

    public void stopEventTimer() {
        this.eventTime = 0L;
        this.timeStarted = 0L;
        if (this.eventTimer != null) {
            this.eventTimer.cancel(false);
        }
    }

    public void restartEventTimer(long time) {
        try {
            if (this.disposed) {
                return;
            }
            this.timeStarted = System.currentTimeMillis();
            this.eventTime = time;
            if (this.eventTimer != null) {
                this.eventTimer.cancel(false);
            }
            this.eventTimer = null;
            int timesend = (int)time / 1000;
            for (MapleCharacter chr : this.getPlayers()) {
                chr.getClient().getSession().write((Object)MaplePacketCreator.getClock(timesend));
            }
            this.timeOut(time, this);
        }
        catch (Exception ex) {
            FileoutputUtil.outputFileError("Logs/Log_Script_Except.rtf", ex);
            System.out.println("Event name" + this.em.getName() + ", Instance name : " + this.name + ", method Name : restartEventTimer:\n");
            ex.printStackTrace();
        }
    }

    public void startEventTimer(long time) {
        this.restartEventTimer(time);
    }

    public boolean isTimerStarted() {
        return this.eventTime > 0L && this.timeStarted > 0L;
    }

    public long getTimeLeft() {
        return this.eventTime - (System.currentTimeMillis() - this.timeStarted);
    }

    public void registerParty(MapleParty party, MapleMap map) {
        if (this.disposed) {
            return;
        }
        for (MaplePartyCharacter pc : party.getMembers()) {
            MapleCharacter c = map.getCharacterById(pc.getId());
            this.registerPlayer(c);
        }
    }

    public void unregisterPlayer(MapleCharacter chr) {
        if (this.disposed) {
            chr.setEventInstance(null);
            return;
        }
        this.wL.lock();
        try {
            this.unregisterPlayer_NoLock(chr);
        }
        finally {
            this.wL.unlock();
        }
    }

    private boolean unregisterPlayer_NoLock(MapleCharacter chr) {
        MapleSquad squad;
        if (this.name.equals("CWKPQ") && (squad = ChannelServer.getInstance(this.channel).getMapleSquad("CWKPQ")) != null) {
            squad.removeMember(chr.getName());
            if (squad.getLeaderName().equals(chr.getName())) {
                this.em.setProperty("leader", "false");
            }
        }
        chr.setEventInstance(null);
        if (this.disposed) {
            return false;
        }
        if (this.chars.contains(chr)) {
            this.chars.remove(chr);
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final boolean disposeIfPlayerBelow(byte size, int towarp) {
        if (this.disposed) {
            return true;
        }
        MapleMap map = null;
        if (towarp > 0) {
            map = this.getMapFactory().getMap(towarp);
        }
        this.wL.lock();
        try {
            if (this.chars.size() <= size) {
                LinkedList<MapleCharacter> chrs = new LinkedList<MapleCharacter>(this.chars);
                for (MapleCharacter chr : chrs) {
                    this.unregisterPlayer_NoLock(chr);
                    if (towarp <= 0) continue;
                    chr.changeMap(map, map.getPortal(0));
                }
                this.dispose_NoLock();
                boolean bl = true;
                return bl;
            }
        }
        finally {
            this.wL.unlock();
        }
        return false;
    }

    public final void saveBossQuest(int points) {
        if (this.disposed) {
            return;
        }
        for (MapleCharacter chr : this.getPlayers()) {
            MapleQuestStatus record = chr.getQuestNAdd(MapleQuest.getInstance(150001));
            if (record.getCustomData() != null) {
                record.setCustomData(String.valueOf(points + Integer.parseInt(record.getCustomData())));
                continue;
            }
            record.setCustomData(String.valueOf(points));
        }
    }

    public List<MapleCharacter> getPlayers() {
        if (this.disposed) {
            return Collections.emptyList();
        }
        this.rL.lock();
        try {
            LinkedList<MapleCharacter> linkedList = new LinkedList<MapleCharacter>(this.chars);
            return linkedList;
        }
        finally {
            this.rL.unlock();
        }
    }

    public List<Integer> getDisconnected() {
        return this.dced;
    }

    public final int getPlayerCount() {
        if (this.disposed) {
            return 0;
        }
        return this.chars.size();
    }

    public void registerMonster(MapleMonster mob2) {
        if (this.disposed) {
            return;
        }
        this.mobs.add(mob2);
        mob2.setEventInstance(this);
    }

    public void unregisterMonster(MapleMonster mob2) {
        mob2.setEventInstance(null);
        if (this.disposed) {
            return;
        }
        this.mobs.remove(mob2);
        if (this.mobs.size() == 0) {
            try {
                this.em.getIv().invokeFunction("allMonstersDead", this);
            }
            catch (Exception ex) {
                FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name" + this.em.getName() + ", Instance name : " + this.name + ", method Name : allMonstersDead:\n" + ex);
                System.out.println("Event name" + this.em.getName() + ", Instance name : " + this.name + ", method Name : allMonstersDead:\n" + ex);
            }
        }
    }

    public void playerKilled(MapleCharacter chr) {
        if (this.disposed) {
            return;
        }
        try {
            this.em.getIv().invokeFunction("playerDead", this, chr);
        }
        catch (Exception ex) {
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name" + this.em.getName() + ", Instance name : " + this.name + ", method Name : playerDead:\n" + ex);
            System.out.println("Event name" + this.em.getName() + ", Instance name : " + this.name + ", method Name : playerDead:\n" + ex);
        }
    }

    public boolean revivePlayer(MapleCharacter chr) {
        if (this.disposed) {
            return false;
        }
        try {
            Object b = this.em.getIv().invokeFunction("playerRevive", this, chr);
            if (b instanceof Boolean) {
                return (Boolean)b;
            }
        }
        catch (Exception ex) {
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name" + this.em.getName() + ", Instance name : " + this.name + ", method Name : playerRevive:\n" + ex);
            System.out.println("Event name" + this.em.getName() + ", Instance name : " + this.name + ", method Name : playerRevive:\n" + ex);
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void playerDisconnected(MapleCharacter chr, int idz) {
        byte ret;
        if (this.disposed) {
            return;
        }
        try {
            ret = ((Double)this.em.getIv().invokeFunction("playerDisconnected", this, chr)).byteValue();
        }
        catch (Exception e) {
            ret = 0;
        }
        this.wL.lock();
        try {
            if (this.disposed) {
                return;
            }
            this.dced.add(idz);
            if (chr != null) {
                this.unregisterPlayer_NoLock(chr);
            }
            if (ret == 0) {
                if (this.getPlayerCount() <= 0) {
                    this.dispose_NoLock();
                }
            } else if (ret > 0 && this.getPlayerCount() < ret || ret < 0 && (this.isLeader(chr) || this.getPlayerCount() < ret * -1)) {
                LinkedList<MapleCharacter> chrs = new LinkedList<MapleCharacter>(this.chars);
                for (MapleCharacter player : chrs) {
                    if (player.getId() == idz) continue;
                    this.removePlayer(player);
                }
                this.dispose_NoLock();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            FileoutputUtil.outputFileError("Logs/Log_Script_Except.rtf", ex);
        }
        finally {
            this.wL.unlock();
        }
    }

    public void monsterKilled(MapleCharacter chr, MapleMonster mob2) {
        if (this.disposed) {
            return;
        }
        try {
            Integer kc = this.killCount.get(chr.getId());
            int inc = (Integer)this.em.getIv().invokeFunction("monsterValue", this, mob2.getId());
            if (this.disposed) {
                return;
            }
            kc = kc == null ? Integer.valueOf(inc) : Integer.valueOf(kc + inc);
            this.killCount.put(chr.getId(), kc);
            if (chr.getCarnivalParty() != null && (mob2.getStats().getPoint() > 0 || mob2.getStats().getCP() > 0)) {
                this.em.getIv().invokeFunction("monsterKilled", this, chr, mob2.getStats().getCP() > 0 ? mob2.getStats().getCP() : mob2.getStats().getPoint());
            }
        }
        catch (ScriptException ex) {
            System.out.println("Event name" + (this.em == null ? "null" : this.em.getName()) + ", Instance name : " + this.name + ", method Name : monsterValue:\n" + ex);
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name" + (this.em == null ? "null" : this.em.getName()) + ", Instance name : " + this.name + ", method Name : monsterValue:\n" + ex);
        }
        catch (NoSuchMethodException ex) {
            System.out.println("Event name" + (this.em == null ? "null" : this.em.getName()) + ", Instance name : " + this.name + ", method Name : monsterValue:\n" + ex);
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name" + (this.em == null ? "null" : this.em.getName()) + ", Instance name : " + this.name + ", method Name : monsterValue:\n" + ex);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            FileoutputUtil.outputFileError("Logs/Log_Script_Except.rtf", ex);
        }
    }

    public void monsterDamaged(MapleCharacter chr, MapleMonster mob2, int damage) {
        if (this.disposed || mob2.getId() != 9700037) {
            return;
        }
        try {
            this.em.getIv().invokeFunction("monsterDamaged", this, chr, mob2.getId(), damage);
        }
        catch (ScriptException ex) {
            System.out.println("Event name" + (this.em == null ? "null" : this.em.getName()) + ", Instance name : " + this.name + ", method Name : monsterValue:\n" + ex);
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name" + (this.em == null ? "null" : this.em.getName()) + ", Instance name : " + this.name + ", method Name : monsterValue:\n" + ex);
        }
        catch (NoSuchMethodException ex) {
            System.out.println("Event name" + (this.em == null ? "null" : this.em.getName()) + ", Instance name : " + this.name + ", method Name : monsterValue:\n" + ex);
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name" + (this.em == null ? "null" : this.em.getName()) + ", Instance name : " + this.name + ", method Name : monsterValue:\n" + ex);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            FileoutputUtil.outputFileError("Logs/Log_Script_Except.rtf", ex);
        }
    }

    public int getKillCount(MapleCharacter chr) {
        if (this.disposed) {
            return 0;
        }
        Integer kc = this.killCount.get(chr.getId());
        if (kc == null) {
            return 0;
        }
        return kc;
    }

    public void dispose_NoLock() {
        if (this.disposed || this.em == null) {
            return;
        }
        String emN = this.em.getName();
        try {
            this.disposed = true;
            for (MapleCharacter chr : this.chars) {
                chr.setEventInstance(null);
            }
            this.chars.clear();
            this.chars = null;
            for (MapleMonster mob2 : this.mobs) {
                mob2.setEventInstance(null);
            }
            this.mobs.clear();
            this.mobs = null;
            this.killCount.clear();
            this.killCount = null;
            this.dced.clear();
            this.dced = null;
            this.timeStarted = 0L;
            this.eventTime = 0L;
            this.props.clear();
            this.props = null;
            for (int i = 0; i < this.mapIds.size(); ++i) {
                if (!this.isInstanced.get(i).booleanValue()) continue;
                this.getMapFactory().removeInstanceMap(this.mapIds.get(i));
            }
            this.mapIds.clear();
            this.mapIds = null;
            this.isInstanced.clear();
            this.isInstanced = null;
            this.em.disposeInstance(this.name);
        }
        catch (Exception e) {
            System.out.println("Caused by : " + emN + " instance name: " + this.name + " method: dispose: " + e);
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name" + emN + ", Instance name : " + this.name + ", method Name : dispose:\n" + e);
        }
    }

    public void dispose() {
        this.wL.lock();
        try {
            this.dispose_NoLock();
        }
        finally {
            this.wL.unlock();
        }
    }

    public ChannelServer getChannelServer() {
        return ChannelServer.getInstance(this.channel);
    }

    public List<MapleMonster> getMobs() {
        return this.mobs;
    }

    public final void broadcastPlayerMsg(int type, String msg) {
        if (this.disposed) {
            return;
        }
        for (MapleCharacter chr : this.getPlayers()) {
            chr.getClient().getSession().write((Object)MaplePacketCreator.serverNotice(type, msg));
        }
    }

    public final MapleMap createInstanceMap(int mapid) {
        if (this.disposed) {
            return null;
        }
        int assignedid = this.getChannelServer().getEventSM().getNewInstanceMapId();
        this.mapIds.add(assignedid);
        this.isInstanced.add(true);
        return this.getMapFactory().CreateInstanceMap(mapid, true, true, true, assignedid);
    }

    public final MapleMap createInstanceMapS(int mapid) {
        if (this.disposed) {
            return null;
        }
        int assignedid = this.getChannelServer().getEventSM().getNewInstanceMapId();
        this.mapIds.add(assignedid);
        this.isInstanced.add(true);
        return this.getMapFactory().CreateInstanceMap(mapid, false, false, false, assignedid);
    }

    public final MapleMap setInstanceMap(int mapid) {
        if (this.disposed) {
            return this.getMapFactory().getMap(mapid);
        }
        this.mapIds.add(mapid);
        this.isInstanced.add(false);
        return this.getMapFactory().getMap(mapid);
    }

    public final MapleMapFactory getMapFactory() {
        return this.getChannelServer().getMapFactory();
    }

    public final MapleMap getMapInstance(int args) {
        if (this.disposed) {
            return null;
        }
        try {
            boolean instanced = false;
            int trueMapID = -1;
            if (args >= this.mapIds.size()) {
                trueMapID = args;
            } else {
                trueMapID = this.mapIds.get(args);
                instanced = this.isInstanced.get(args);
            }
            MapleMap map = null;
            if (!instanced) {
                map = this.getMapFactory().getMap(trueMapID);
                if (map == null) {
                    return null;
                }
                if (map.getCharactersSize() == 0 && this.em.getProperty("shuffleReactors") != null && this.em.getProperty("shuffleReactors").equals("true")) {
                    map.shuffleReactors();
                }
            } else {
                map = this.getMapFactory().getInstanceMap(trueMapID);
                if (map == null) {
                    return null;
                }
                if (map.getCharactersSize() == 0 && this.em.getProperty("shuffleReactors") != null && this.em.getProperty("shuffleReactors").equals("true")) {
                    map.shuffleReactors();
                }
            }
            return map;
        }
        catch (NullPointerException ex) {
            FileoutputUtil.outputFileError("Logs/Log_Script_Except.rtf", ex);
            ex.printStackTrace();
            return null;
        }
    }

    public final void schedule(final String methodName, long delay) {
        if (this.disposed) {
            return;
        }
        Timer.EventTimer.getInstance().schedule(new Runnable(){

            @Override
            public void run() {
                if (EventInstanceManager.this.disposed || EventInstanceManager.this == null || EventInstanceManager.this.em == null) {
                    return;
                }
                try {
                    EventInstanceManager.this.em.getIv().invokeFunction(methodName, EventInstanceManager.this);
                }
                catch (NullPointerException nullPointerException) {
                }
                catch (Exception ex) {
                    System.out.println("Event name" + EventInstanceManager.this.em.getName() + ", Instance name : " + EventInstanceManager.this.name + ", method Name : " + methodName + ":\n" + ex);
                    FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name" + EventInstanceManager.this.em.getName() + ", Instance name : " + EventInstanceManager.this.name + ", method Name(schedule) : " + methodName + " :\n" + ex);
                }
            }
        }, delay);
    }

    public final String getName() {
        return this.name;
    }

    public final void setProperty(String key, String value) {
        if (this.disposed) {
            return;
        }
        this.props.setProperty(key, value);
    }

    public final Object setProperty(String key, String value, boolean prev) {
        if (this.disposed) {
            return null;
        }
        return this.props.setProperty(key, value);
    }

    public final String getProperty(String key) {
        if (this.disposed) {
            return "";
        }
        return this.props.getProperty(key);
    }

    public final Properties getProperties() {
        return this.props;
    }

    public final void leftParty(MapleCharacter chr) {
        if (this.disposed) {
            return;
        }
        try {
            this.em.getIv().invokeFunction("leftParty", this, chr);
        }
        catch (Exception ex) {
            System.out.println("Event name" + this.em.getName() + ", Instance name : " + this.name + ", method Name : leftParty:\n" + ex);
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name" + this.em.getName() + ", Instance name : " + this.name + ", method Name : leftParty:\n" + ex);
        }
    }

    public final void disbandParty() {
        if (this.disposed) {
            return;
        }
        try {
            this.em.getIv().invokeFunction("disbandParty", this);
        }
        catch (Exception ex) {
            System.out.println("Event name" + this.em.getName() + ", Instance name : " + this.name + ", method Name : disbandParty:\n" + ex);
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name" + this.em.getName() + ", Instance name : " + this.name + ", method Name : disbandParty:\n" + ex);
        }
    }

    public final void finishPQ() {
        if (this.disposed) {
            return;
        }
        try {
            this.em.getIv().invokeFunction("clearPQ", this);
        }
        catch (Exception ex) {
            System.out.println("Event name" + this.em.getName() + ", Instance name : " + this.name + ", method Name : clearPQ:\n" + ex);
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name" + this.em.getName() + ", Instance name : " + this.name + ", method Name : clearPQ:\n" + ex);
        }
    }

    public final void removePlayer(MapleCharacter chr) {
        if (this.disposed) {
            return;
        }
        try {
            this.em.getIv().invokeFunction("playerExit", this, chr);
        }
        catch (Exception ex) {
            System.out.println("Event name" + this.em.getName() + ", Instance name : " + this.name + ", method Name : playerExit:\n" + ex);
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name" + this.em.getName() + ", Instance name : " + this.name + ", method Name : playerExit:\n" + ex);
        }
    }

    public final void registerCarnivalParty(MapleCharacter leader, MapleMap map, byte team) {
        if (this.disposed) {
            return;
        }
        leader.clearCarnivalRequests();
        LinkedList<MapleCharacter> characters = new LinkedList<MapleCharacter>();
        MapleParty party = leader.getParty();
        if (party == null) {
            return;
        }
        for (MaplePartyCharacter pc : party.getMembers()) {
            MapleCharacter c = map.getCharacterById(pc.getId());
            if (c == null) continue;
            characters.add(c);
            this.registerPlayer(c);
            c.resetCP();
        }
        MapleCarnivalParty carnivalParty = new MapleCarnivalParty(leader, characters, team);
        try {
            this.em.getIv().invokeFunction("registerCarnivalParty", this, carnivalParty);
        }
        catch (ScriptException ex) {
            System.out.println("Event name" + this.em.getName() + ", Instance name : " + this.name + ", method Name : registerCarnivalParty:\n" + ex);
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name" + this.em.getName() + ", Instance name : " + this.name + ", method Name : registerCarnivalParty:\n" + ex);
        }
        catch (NoSuchMethodException noSuchMethodException) {
            // empty catch block
        }
    }

    public void onMapLoad(MapleCharacter chr) {
        if (this.disposed) {
            return;
        }
        try {
            this.em.getIv().invokeFunction("onMapLoad", this, chr);
        }
        catch (ScriptException ex) {
            System.out.println("Event name" + this.em.getName() + ", Instance name : " + this.name + ", method Name : onMapLoad:\n" + ex);
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name" + this.em.getName() + ", Instance name : " + this.name + ", method Name : onMapLoad:\n" + ex);
        }
        catch (NoSuchMethodException noSuchMethodException) {
            // empty catch block
        }
    }

    public boolean isLeader(MapleCharacter chr) {
        return chr != null && chr.getParty() != null && chr.getParty().getLeader().getId() == chr.getId();
    }

    public void registerSquad(MapleSquad squad, MapleMap map, int questID) {
        if (this.disposed) {
            return;
        }
        int mapid = map.getId();
        for (String chr : squad.getMembers()) {
            MapleCharacter player = squad.getChar(chr);
            if (player == null || player.getMapId() != mapid) continue;
            if (questID > 0) {
                player.getQuestNAdd(MapleQuest.getInstance(questID)).setCustomData(String.valueOf(System.currentTimeMillis()));
            }
            this.registerPlayer(player);
        }
        squad.setStatus((byte)2);
        squad.getBeginMap().broadcastMessage(MaplePacketCreator.stopClock());
    }

    public boolean isDisconnected(MapleCharacter chr) {
        if (this.disposed) {
            return false;
        }
        return this.dced.contains(chr.getId());
    }

    public void removeDisconnected(int id) {
        if (this.disposed) {
            return;
        }
        this.dced.remove(id);
    }

    public EventManager getEventManager() {
        return this.em;
    }

    public void applyBuff(MapleCharacter chr, int id) {
        MapleItemInformationProvider.getInstance().getItemEffect(id).applyTo(chr);
        chr.getClient().getSession().write((Object)UIPacket.getStatusMsg(id));
    }
}

