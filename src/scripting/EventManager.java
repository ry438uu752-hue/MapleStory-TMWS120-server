/*
 * Decompiled with CFR 0.152.
 */
package scripting;

import client.MapleCharacter;
import database.DatabaseConnection;
import handling.channel.ChannelServer;
import handling.world.MapleParty;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Invocable;
import javax.script.ScriptException;
import scripting.EventInstanceManager;
import server.MapleSquad;
import server.Randomizer;
import server.Timer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.OverrideMonsterStats;
import server.maps.MapleMap;
import server.maps.MapleMapFactory;
import server.maps.MapleMapObject;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;

public class EventManager {
    private static int[] eventChannel = new int[2];
    private Invocable iv;
    private int channel;
    private Map<String, EventInstanceManager> instances = new WeakHashMap<String, EventInstanceManager>();
    private Properties props = new Properties();
    private String name;

    public EventManager(ChannelServer cserv, Invocable iv, String name) {
        this.iv = iv;
        this.channel = cserv.getChannel();
        this.name = name;
    }

    public void cancel() {
        try {
            this.iv.invokeFunction("cancelSchedule", new Object[]{null});
        }
        catch (Exception ex) {
            System.out.println("Event name : " + this.name + ", method Name : cancelSchedule:\n" + ex);
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name : " + this.name + ", method Name : cancelSchedule:\n" + ex);
        }
    }

    public ScheduledFuture<?> schedule(final String methodName, long delay) {
        return Timer.EventTimer.getInstance().schedule(new Runnable(){

            @Override
            public void run() {
                try {
                    EventManager.this.iv.invokeFunction(methodName, new Object[]{null});
                }
                catch (Exception ex) {
                    System.out.println("Event name : " + EventManager.this.name + ", method Name : " + methodName + ":\n" + ex);
                    FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name : " + EventManager.this.name + ", method Name : " + methodName + ":\n" + ex);
                }
            }
        }, delay);
    }

    public ScheduledFuture<?> schedule(final String methodName, long delay, final EventInstanceManager eim) {
        return Timer.EventTimer.getInstance().schedule(new Runnable(){

            @Override
            public void run() {
                try {
                    EventManager.this.iv.invokeFunction(methodName, eim);
                }
                catch (Exception ex) {
                    System.out.println("Event name : " + EventManager.this.name + ", method Name : " + methodName + ":\n" + ex);
                    FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name : " + EventManager.this.name + ", method Name : " + methodName + ":\n" + ex);
                }
            }
        }, delay);
    }

    public ScheduledFuture<?> scheduleAtTimestamp(final String methodName, long timestamp) {
        return Timer.EventTimer.getInstance().scheduleAtTimestamp(new Runnable(){

            @Override
            public void run() {
                try {
                    EventManager.this.iv.invokeFunction(methodName, new Object[]{null});
                }
                catch (ScriptException ex) {
                    System.out.println("Event name : " + EventManager.this.name + ", method Name : " + methodName + ":\n" + ex);
                }
                catch (NoSuchMethodException ex) {
                    System.out.println("Event name : " + EventManager.this.name + ", method Name : " + methodName + ":\n" + ex);
                }
            }
        }, timestamp);
    }

    public int getChannel() {
        return this.channel;
    }

    public ChannelServer getChannelServer() {
        return ChannelServer.getInstance(this.channel);
    }

    public EventInstanceManager getInstance(String name) {
        return this.instances.get(name);
    }

    public Collection<EventInstanceManager> getInstances() {
        return Collections.unmodifiableCollection(this.instances.values());
    }

    public EventInstanceManager newInstance(String name) {
        EventInstanceManager ret = new EventInstanceManager(this, name, this.channel);
        this.instances.put(name, ret);
        return ret;
    }

    public void disposeInstance(String name) {
        MapleSquad squad;
        this.instances.remove(name);
        if (this.getProperty("state") != null && this.instances.size() == 0) {
            this.setProperty("state", "0");
        }
        if (this.getProperty("leader") != null && this.instances.size() == 0 && this.getProperty("leader").equals("false")) {
            this.setProperty("leader", "true");
        }
        if (this.name.equals("CWKPQ") && (squad = ChannelServer.getInstance(this.channel).getMapleSquad("CWKPQ")) != null) {
            squad.clear();
        }
    }

    public Invocable getIv() {
        return this.iv;
    }

    public void setProperty(String key, String value) {
        this.props.setProperty(key, value);
    }

    public String getProperty(String key) {
        return this.props.getProperty(key);
    }

    public final Properties getProperties() {
        return this.props;
    }

    public String getName() {
        return this.name;
    }

    public void startInstance() {
        try {
            this.iv.invokeFunction("setup", new Object[]{null});
        }
        catch (Exception ex) {
            ex.printStackTrace();
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name : " + this.name + ", method Name : setup:\n" + ex);
        }
    }

    public void startInstance(String mapid, MapleCharacter chr) {
        try {
            EventInstanceManager eim = (EventInstanceManager)this.iv.invokeFunction("setup", mapid);
            eim.registerCarnivalParty(chr, chr.getMap(), (byte)0);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name : " + this.name + ", method Name : setup:\n" + ex);
        }
    }

    public void startInstance_Party(String mapid, MapleCharacter chr) {
        try {
            EventInstanceManager eim = (EventInstanceManager)this.iv.invokeFunction("setup", mapid);
            eim.registerParty(chr.getParty(), chr.getMap());
        }
        catch (Exception ex) {
            ex.printStackTrace();
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name : " + this.name + ", method Name : setup:\n" + ex);
        }
    }

    public void startInstance(MapleCharacter character, String leader) {
        try {
            EventInstanceManager eim = (EventInstanceManager)this.iv.invokeFunction("setup", new Object[]{null});
            eim.registerPlayer(character);
            eim.setProperty("leader", leader);
            eim.setProperty("guildid", String.valueOf(character.getGuildId()));
            this.setProperty("guildid", String.valueOf(character.getGuildId()));
        }
        catch (Exception ex) {
            System.out.println("Event name : " + this.name + ", method Name : setup-Guild:\n" + ex);
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name : " + this.name + ", method Name : setup-Guild:\n" + ex);
        }
    }

    public void startInstance_CharID(MapleCharacter character) {
        try {
            EventInstanceManager eim = (EventInstanceManager)this.iv.invokeFunction("setup", character.getId());
            eim.registerPlayer(character);
        }
        catch (Exception ex) {
            System.out.println("Event name : " + this.name + ", method Name : setup-CharID:\n" + ex);
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name : " + this.name + ", method Name : setup-CharID:\n" + ex);
        }
    }

    public void startInstance(MapleCharacter character) {
        try {
            EventInstanceManager eim = (EventInstanceManager)this.iv.invokeFunction("setup", new Object[]{null});
            eim.registerPlayer(character);
        }
        catch (Exception ex) {
            System.out.println("Event name : " + this.name + ", method Name : setup-character:\n" + ex);
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name : " + this.name + ", method Name : setup-character:\n" + ex);
        }
    }

    public void startInstance(MapleParty party, MapleMap map) {
        try {
            EventInstanceManager eim = (EventInstanceManager)this.iv.invokeFunction("setup", party.getId());
            eim.registerParty(party, map);
        }
        catch (ScriptException ex) {
            System.out.println("Event name : " + this.name + ", method Name : setup-partyid:\n" + ex);
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name : " + this.name + ", method Name : setup-partyid:\n" + ex);
        }
        catch (Exception ex) {
            this.startInstance_NoID(party, map, ex);
        }
    }

    public void startInstance_NoID(MapleParty party, MapleMap map) {
        this.startInstance_NoID(party, map, null);
    }

    public void startInstance_NoID(MapleParty party, MapleMap map, Exception old) {
        try {
            EventInstanceManager eim = (EventInstanceManager)this.iv.invokeFunction("setup", new Object[]{null});
            eim.registerParty(party, map);
        }
        catch (Exception ex) {
            System.out.println("Event name : " + this.name + ", method Name : setup-party:\n" + ex);
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name : " + this.name + ", method Name : setup-party:\n" + ex + "\n" + (old == null ? "no old exception" : old));
        }
    }

    public void startInstance(EventInstanceManager eim, String leader) {
        try {
            this.iv.invokeFunction("setup", eim);
            eim.setProperty("leader", leader);
        }
        catch (Exception ex) {
            System.out.println("Event name : " + this.name + ", method Name : setup-leader:\n" + ex);
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name : " + this.name + ", method Name : setup-leader:\n" + ex);
        }
    }

    public void startInstance(MapleSquad squad, MapleMap map) {
        this.startInstance(squad, map, -1);
    }

    public void startInstance(MapleSquad squad, MapleMap map, int questID) {
        if (squad.getStatus() == 0) {
            return;
        }
        if (!squad.getLeader().isGM()) {
            if (squad.getMembers().size() < squad.getType().i) {
                squad.getLeader().dropMessage(5, "\u9019\u500b\u9060\u5f81\u968a\u81f3\u5c11\u8981\u6709 " + squad.getType().i + " \u4eba\u4ee5\u4e0a\u624d\u53ef\u4ee5\u958b\u6230.");
                return;
            }
            if (this.name.equals("CWKPQ") && squad.getJobs().size() < 5) {
                squad.getLeader().dropMessage(5, "The squad requires members from every type of job.");
                return;
            }
        }
        try {
            EventInstanceManager eim = (EventInstanceManager)this.iv.invokeFunction("setup", squad.getLeaderName());
            eim.registerSquad(squad, map, questID);
        }
        catch (Exception ex) {
            System.out.println("Event name : " + this.name + ", method Name : setup-squad:\n" + ex);
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Event name : " + this.name + ", method Name : setup-squad:\n" + ex);
        }
    }

    public void warpAllPlayer(int from, int to) {
        MapleMap tomap = this.getMapFactory().getMap(to);
        MapleMap frommap = this.getMapFactory().getMap(from);
        List<MapleCharacter> list = frommap.getCharactersThreadsafe();
        if (tomap != null && frommap != null && list != null && frommap.getCharactersSize() > 0) {
            for (MapleMapObject mapleMapObject : list) {
                ((MapleCharacter)mapleMapObject).changeMap(tomap, tomap.getPortal(0));
            }
        }
    }

    public int online() {
        Connection con = DatabaseConnection.getConnection();
        int count = 0;
        try {
            PreparedStatement ps = con.prepareStatement("SELECT count(*) as cc FROM accounts WHERE loggedin = 2");
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                count = re.getInt("cc");
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(EventInstanceManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return count;
    }

    public MapleMapFactory getMapFactory() {
        return this.getChannelServer().getMapFactory();
    }

    public OverrideMonsterStats newMonsterStats() {
        return new OverrideMonsterStats();
    }

    public List<MapleCharacter> newCharList() {
        return new ArrayList<MapleCharacter>();
    }

    public MapleMonster getMonster(int id) {
        return MapleLifeFactory.getMonster(id);
    }

    public void broadcastShip(int mapid, int effect) {
        this.getMapFactory().getMap(mapid).broadcastMessage(MaplePacketCreator.boatEffect(effect));
    }

    public void broadcastChangeMusic(int mapid) {
        this.getMapFactory().getMap(mapid).broadcastMessage(MaplePacketCreator.musicChange("Bgm04/ArabPirate"));
    }

    public void broadcastYellowMsg(String msg) {
        this.getChannelServer().broadcastPacket(MaplePacketCreator.yellowChat(msg));
    }

    public void broadcastServerMsg(int type, String msg, boolean weather) {
        if (!weather) {
            this.getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(type, msg));
        } else {
            for (MapleMap load : this.getMapFactory().getAllMaps()) {
                if (load.getCharactersSize() <= 0) continue;
                load.startMapEffect(msg, type);
            }
        }
    }

    public boolean scheduleRandomEvent() {
        boolean omg = false;
        for (int i = 0; i < eventChannel.length; ++i) {
            omg |= this.scheduleRandomEventInChannel(eventChannel[i]);
        }
        return omg;
    }

    public boolean scheduleRandomEventInChannel(int chz) {
        final ChannelServer cs = ChannelServer.getInstance(chz);
        if (cs == null || cs.getEvent() > -1) {
            return false;
        }
        MapleEventType t = null;
        block0: while (t == null) {
            for (MapleEventType x : MapleEventType.values()) {
                if (Randomizer.nextInt(MapleEventType.values().length) != 0 || x == MapleEventType.OxQuiz) continue;
                t = x;
                continue block0;
            }
        }
        String msg = MapleEvent.scheduleEvent(t, cs);
        if (msg.length() > 0) {
            this.broadcastYellowMsg(msg);
            return false;
        }
        Timer.EventTimer.getInstance().schedule(new Runnable(){

            @Override
            public void run() {
                if (cs.getEvent() >= 0) {
                    MapleEvent.setEvent(cs, true);
                }
            }
        }, 600000L);
        return true;
    }

    public void setWorldEvent() {
        for (int i = 0; i < eventChannel.length; ++i) {
            EventManager.eventChannel[i] = Randomizer.nextInt(ChannelServer.getAllInstances().size()) + i;
        }
    }
}

