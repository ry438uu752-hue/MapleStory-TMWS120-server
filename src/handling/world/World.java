/*
 * Decompiled with CFR 0.152.
 */
package handling.world;

import client.BuddyList;
import client.BuddylistEntry;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleCoolDownValueHolder;
import client.MapleDiseaseValueHolder;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.PetDataFactory;
import database.DatabaseConnection;
import handling.MaplePacket;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.channel.PlayerStorage;
import handling.world.CharacterIdChannelPair;
import handling.world.CharacterTransfer;
import handling.world.CheaterData;
import handling.world.MapleMessenger;
import handling.world.MapleMessengerCharacter;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.family.MapleFamily;
import handling.world.family.MapleFamilyCharacter;
import handling.world.guild.MapleBBSThread;
import handling.world.guild.MapleGuild;
import handling.world.guild.MapleGuildAlliance;
import handling.world.guild.MapleGuildCharacter;
import handling.world.guild.MapleGuildSummary;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import server.Timer;
import server.maps.MapleMap;
import server.maps.MapleMapItem;
import tools.CollectionUtil;
import tools.MaplePacketCreator;
import tools.packet.PetPacket;

public class World {
    public static boolean isShutDown = false;

    public static void init() {
        Find.findChannel(0);
        Guild.lock.toString();
        Alliance.lock.toString();
        Family.lock.toString();
        Messenger.getMessenger(0);
        Party.getParty(0);
    }

    public static String getStatus() throws RemoteException {
        StringBuilder ret = new StringBuilder();
        int totalUsers = 0;
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            ret.append("Channel ");
            ret.append(cs.getChannel());
            ret.append(": ");
            int channelUsers = cs.getConnectedClients();
            totalUsers += channelUsers;
            ret.append(channelUsers);
            ret.append(" users\n");
        }
        ret.append("Total users online: ");
        ret.append(totalUsers);
        ret.append("\n");
        return ret.toString();
    }

    public static Map<Integer, Integer> getConnected() {
        HashMap<Integer, Integer> ret = new HashMap<Integer, Integer>();
        int total = 0;
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            int curConnected = cs.getConnectedClients();
            ret.put(cs.getChannel(), curConnected);
            total += curConnected;
        }
        ret.put(0, total);
        return ret;
    }

    public static List<CheaterData> getCheaters() {
        ArrayList<CheaterData> allCheaters = new ArrayList<CheaterData>();
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            allCheaters.addAll(cs.getCheaters());
        }
        Collections.sort(allCheaters);
        return CollectionUtil.copyFirst(allCheaters, 10);
    }

    public static boolean isConnected(String charName) {
        return Find.findChannel(charName) > 0;
    }

    public static void toggleMegaphoneMuteState() {
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            cs.toggleMegaphoneMuteState();
        }
    }

    public static void ChannelChange_Data(CharacterTransfer Data, int characterid, int toChannel) {
        World.getStorage(toChannel).registerPendingPlayer(Data, characterid);
    }

    public static boolean isCharacterListConnected(List<String> charName) {
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            for (String c : charName) {
                if (cs.getPlayerStorage().getCharacterByName(c) == null) continue;
                return true;
            }
        }
        return false;
    }

    public static boolean hasMerchant(int accountID) {
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            if (!cs.containsMerchant(accountID)) continue;
            return true;
        }
        return false;
    }

    public static PlayerStorage getStorage(int channel) {
        if (channel == -20) {
            return CashShopServer.getPlayerStorageMTS();
        }
        if (channel == -10) {
            return CashShopServer.getPlayerStorage();
        }
        return ChannelServer.getInstance(channel).getPlayerStorage();
    }

    public static void registerRespawn() {
        Timer.WorldTimer.getInstance().register(new Respawn(), 3000L);
    }

    public static void handleMap(MapleMap map, int numTimes, int size) {
        if (map.getItemsSize() > 0) {
            for (MapleMapItem item : map.getAllItemsThreadsafe()) {
                if (item.shouldExpire()) {
                    item.expire(map);
                    continue;
                }
                if (!item.shouldFFA()) continue;
                item.setDropType((byte)2);
            }
        }
        if (map.characterSize() > 0) {
            if (map.canSpawn()) {
                map.respawn(false);
            }
            boolean hurt = map.canHurt();
            for (MapleCharacter chr : map.getCharactersThreadsafe()) {
                World.handleCooldowns(chr, numTimes, hurt);
            }
        }
    }

    public static void handleCooldowns(MapleCharacter chr, int numTimes, boolean hurt) {
        long now = System.currentTimeMillis();
        for (MapleCoolDownValueHolder mapleCoolDownValueHolder : chr.getCooldowns()) {
            if (mapleCoolDownValueHolder.startTime + mapleCoolDownValueHolder.length >= now) continue;
            int skil = mapleCoolDownValueHolder.skillId;
            chr.removeCooldown(skil);
            chr.getClient().getSession().write((Object)MaplePacketCreator.skillCooldown(skil, 0));
        }
        for (MapleDiseaseValueHolder mapleDiseaseValueHolder : chr.getAllDiseases()) {
            if (mapleDiseaseValueHolder.startTime + mapleDiseaseValueHolder.length >= now) continue;
            chr.dispelDebuff(mapleDiseaseValueHolder.disease);
        }
        if (numTimes % 20 == 0) {
            for (MaplePet maplePet : chr.getPets()) {
                int newFullness;
                if (!maplePet.getSummoned()) continue;
                if (maplePet.getPetItemId() == 5000054 && maplePet.getSecondsLeft() > 0) {
                    maplePet.setSecondsLeft(maplePet.getSecondsLeft() - 1);
                    if (maplePet.getSecondsLeft() <= 0) {
                        chr.unequipPet(maplePet, true, true);
                        return;
                    }
                }
                if ((newFullness = maplePet.getFullness() - PetDataFactory.getHunger(maplePet.getPetItemId())) <= 5) {
                    maplePet.setFullness(15);
                    chr.unequipPet(maplePet, true, true);
                    continue;
                }
                maplePet.setFullness(newFullness);
                chr.getClient().getSession().write((Object)PetPacket.updatePet(maplePet, chr.getInventory(MapleInventoryType.CASH).getItem(maplePet.getInventoryPosition())));
            }
        }
        if (hurt && chr.isAlive() && chr.getInventory(MapleInventoryType.EQUIPPED).findById(chr.getMap().getHPDecProtect()) == null) {
            if (chr.getMapId() == 749040100 && chr.getInventory(MapleInventoryType.CASH).findById(5451000) == null) {
                chr.addHP(-chr.getMap().getHPDec());
            } else if (chr.getMapId() != 749040100) {
                chr.addHP(-chr.getMap().getHPDec());
            }
        }
    }

    public static class Respawn
    implements Runnable {
        private int numTimes = 0;

        @Override
        public void run() {
            ++this.numTimes;
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleMap map : cserv.getMapFactory().getAllMaps()) {
                    World.handleMap(map, this.numTimes, map.getCharactersSize());
                }
                for (MapleMap map : cserv.getMapFactory().getAllInstanceMaps()) {
                    World.handleMap(map, this.numTimes, map.getCharactersSize());
                }
            }
        }
    }

    public static class Family {
        private static final Map<Integer, MapleFamily> families = new LinkedHashMap<Integer, MapleFamily>();
        private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        public static MapleFamily getFamily(int id) {
            MapleFamily ret = null;
            lock.readLock().lock();
            try {
                ret = families.get(id);
            }
            finally {
                lock.readLock().unlock();
            }
            if (ret == null) {
                lock.writeLock().lock();
                try {
                    ret = new MapleFamily(id);
                    if (ret == null || ret.getId() <= 0 || !ret.isProper()) {
                        MapleFamily mapleFamily = null;
                        return mapleFamily;
                    }
                    families.put(id, ret);
                }
                finally {
                    lock.writeLock().unlock();
                }
            }
            return ret;
        }

        public static void memberFamilyUpdate(MapleFamilyCharacter mfc, MapleCharacter mc) {
            MapleFamily f = Family.getFamily(mfc.getFamilyId());
            if (f != null) {
                f.memberLevelJobUpdate(mc);
            }
        }

        public static void setFamilyMemberOnline(MapleFamilyCharacter mfc, boolean bOnline, int channel) {
            MapleFamily f = Family.getFamily(mfc.getFamilyId());
            if (f != null) {
                f.setOnline(mfc.getId(), bOnline, channel);
            }
        }

        public static int setRep(int fid, int cid, int addrep, int oldLevel) {
            MapleFamily f = Family.getFamily(fid);
            if (f != null) {
                return f.setRep(cid, addrep, oldLevel);
            }
            return 0;
        }

        public static void save() {
            System.out.println("Saving families...");
            lock.writeLock().lock();
            try {
                for (MapleFamily a : families.values()) {
                    a.writeToDB(false);
                }
            }
            finally {
                lock.writeLock().unlock();
            }
        }

        public static void setFamily(int familyid, int seniorid, int junior1, int junior2, int currentrep, int totalrep, int cid) {
            int ch = Find.findChannel(cid);
            if (ch == -1) {
                return;
            }
            MapleCharacter mc = World.getStorage(ch).getCharacterById(cid);
            if (mc == null) {
                return;
            }
            boolean bDifferent = mc.getFamilyId() != familyid || mc.getSeniorId() != seniorid || mc.getJunior1() != junior1 || mc.getJunior2() != junior2;
            mc.setFamily(familyid, seniorid, junior1, junior2);
            mc.setCurrentRep(currentrep);
            mc.setTotalRep(totalrep);
            if (bDifferent) {
                mc.saveFamilyStatus();
            }
        }

        public static void familyPacket(int gid, MaplePacket message, int cid) {
            MapleFamily f = Family.getFamily(gid);
            if (f != null) {
                f.broadcast(message, -1, f.getMFC(cid).getPedigree());
            }
        }

        public static void disbandFamily(int gid) {
            MapleFamily g = Family.getFamily(gid);
            lock.writeLock().lock();
            try {
                if (g != null) {
                    g.disbandFamily();
                    families.remove(gid);
                }
            }
            finally {
                lock.writeLock().unlock();
            }
        }

        static {
            System.out.println("Loading Families :::");
            Collection<MapleFamily> allGuilds = MapleFamily.loadAll();
            for (MapleFamily g : allGuilds) {
                if (!g.isProper()) continue;
                families.put(g.getId(), g);
            }
        }
    }

    public static class Client {
        private static final ArrayList<MapleClient> clients = new ArrayList();

        public static void addClient(MapleClient c) {
            if (!clients.contains(c)) {
                clients.add(c);
            }
        }

        public static boolean removeClient(MapleClient c) {
            return clients.remove(c);
        }

        public static ArrayList<MapleClient> getClients() {
            return clients;
        }
    }

    public static class Alliance {
        private static final Map<Integer, MapleGuildAlliance> alliances = new LinkedHashMap<Integer, MapleGuildAlliance>();
        private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        public static MapleGuildAlliance getAlliance(int allianceid) {
            MapleGuildAlliance ret = null;
            lock.readLock().lock();
            try {
                ret = alliances.get(allianceid);
            }
            finally {
                lock.readLock().unlock();
            }
            if (ret == null) {
                lock.writeLock().lock();
                try {
                    ret = new MapleGuildAlliance(allianceid);
                    if (ret == null || ret.getId() <= 0) {
                        MapleGuildAlliance mapleGuildAlliance = null;
                        return mapleGuildAlliance;
                    }
                    alliances.put(allianceid, ret);
                }
                finally {
                    lock.writeLock().unlock();
                }
            }
            return ret;
        }

        public static int getAllianceLeader(int allianceid) {
            MapleGuildAlliance mga = Alliance.getAlliance(allianceid);
            if (mga != null) {
                return mga.getLeaderId();
            }
            return 0;
        }

        public static void updateAllianceRanks(int allianceid, String[] ranks) {
            MapleGuildAlliance mga = Alliance.getAlliance(allianceid);
            if (mga != null) {
                mga.setRank(ranks);
            }
        }

        public static void updateAllianceNotice(int allianceid, String notice) {
            MapleGuildAlliance mga = Alliance.getAlliance(allianceid);
            if (mga != null) {
                mga.setNotice(notice);
            }
        }

        public static boolean canInvite(int allianceid) {
            MapleGuildAlliance mga = Alliance.getAlliance(allianceid);
            if (mga != null) {
                return mga.getCapacity() > mga.getNoGuilds();
            }
            return false;
        }

        public static boolean changeAllianceLeader(int allianceid, int cid) {
            MapleGuildAlliance mga = Alliance.getAlliance(allianceid);
            if (mga != null) {
                return mga.setLeaderId(cid);
            }
            return false;
        }

        public static boolean changeAllianceRank(int allianceid, int cid, int change) {
            MapleGuildAlliance mga = Alliance.getAlliance(allianceid);
            if (mga != null) {
                return mga.changeAllianceRank(cid, change);
            }
            return false;
        }

        public static boolean changeAllianceCapacity(int allianceid) {
            MapleGuildAlliance mga = Alliance.getAlliance(allianceid);
            if (mga != null) {
                return mga.setCapacity();
            }
            return false;
        }

        public static boolean disbandAlliance(int allianceid) {
            MapleGuildAlliance mga = Alliance.getAlliance(allianceid);
            if (mga != null) {
                return mga.disband();
            }
            return false;
        }

        public static boolean addGuildToAlliance(int allianceid, int gid) {
            MapleGuildAlliance mga = Alliance.getAlliance(allianceid);
            if (mga != null) {
                return mga.addGuild(gid);
            }
            return false;
        }

        public static boolean removeGuildFromAlliance(int allianceid, int gid, boolean expelled) {
            MapleGuildAlliance mga = Alliance.getAlliance(allianceid);
            if (mga != null) {
                return mga.removeGuild(gid, expelled);
            }
            return false;
        }

        public static void sendGuild(int allianceid) {
            MapleGuildAlliance alliance = Alliance.getAlliance(allianceid);
            if (alliance != null) {
                Alliance.sendGuild(MaplePacketCreator.getAllianceUpdate(alliance), -1, allianceid);
                Alliance.sendGuild(MaplePacketCreator.getGuildAlliance(alliance), -1, allianceid);
            }
        }

        public static void sendGuild(MaplePacket packet, int exceptionId, int allianceid) {
            MapleGuildAlliance alliance = Alliance.getAlliance(allianceid);
            if (alliance != null) {
                for (int i = 0; i < alliance.getNoGuilds(); ++i) {
                    int gid = alliance.getGuildId(i);
                    if (gid <= 0 || gid == exceptionId) continue;
                    Guild.guildPacket(gid, packet);
                }
            }
        }

        public static boolean createAlliance(String alliancename, int cid, int cid2, int gid, int gid2) {
            int allianceid = MapleGuildAlliance.createToDb(cid, alliancename, gid, gid2);
            if (allianceid <= 0) {
                return false;
            }
            MapleGuild g = Guild.getGuild(gid);
            MapleGuild g_ = Guild.getGuild(gid2);
            g.setAllianceId(allianceid);
            g_.setAllianceId(allianceid);
            g.changeARank(true);
            g_.changeARank(false);
            MapleGuildAlliance alliance = Alliance.getAlliance(allianceid);
            Alliance.sendGuild(MaplePacketCreator.createGuildAlliance(alliance), -1, allianceid);
            Alliance.sendGuild(MaplePacketCreator.getAllianceInfo(alliance), -1, allianceid);
            Alliance.sendGuild(MaplePacketCreator.getGuildAlliance(alliance), -1, allianceid);
            Alliance.sendGuild(MaplePacketCreator.changeAlliance(alliance, true), -1, allianceid);
            return true;
        }

        public static void allianceChat(int gid, String name, int cid, String msg) {
            MapleGuildAlliance ga;
            MapleGuild g = Guild.getGuild(gid);
            if (g != null && (ga = Alliance.getAlliance(g.getAllianceId())) != null) {
                for (int i = 0; i < ga.getNoGuilds(); ++i) {
                    MapleGuild g_ = Guild.getGuild(ga.getGuildId(i));
                    if (g_ == null) continue;
                    g_.allianceChat(name, cid, msg);
                }
            }
        }

        public static void setNewAlliance(int gid, int allianceid) {
            MapleGuildAlliance alliance = Alliance.getAlliance(allianceid);
            MapleGuild guild = Guild.getGuild(gid);
            if (alliance != null && guild != null) {
                for (int i = 0; i < alliance.getNoGuilds(); ++i) {
                    if (gid == alliance.getGuildId(i)) {
                        guild.setAllianceId(allianceid);
                        guild.broadcast(MaplePacketCreator.getAllianceInfo(alliance));
                        guild.broadcast(MaplePacketCreator.getGuildAlliance(alliance));
                        guild.broadcast(MaplePacketCreator.changeAlliance(alliance, true));
                        guild.changeARank();
                        guild.writeToDB(false);
                        continue;
                    }
                    MapleGuild g_ = Guild.getGuild(alliance.getGuildId(i));
                    if (g_ == null) continue;
                    g_.broadcast(MaplePacketCreator.addGuildToAlliance(alliance, guild));
                    g_.broadcast(MaplePacketCreator.changeGuildInAlliance(alliance, guild, true));
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public static void setOldAlliance(int gid, boolean expelled, int allianceid) {
            MapleGuildAlliance alliance = Alliance.getAlliance(allianceid);
            MapleGuild g_ = Guild.getGuild(gid);
            if (alliance != null) {
                for (int i = 0; i < alliance.getNoGuilds(); ++i) {
                    MapleGuild guild = Guild.getGuild(alliance.getGuildId(i));
                    if (guild == null) {
                        if (gid == alliance.getGuildId(i)) continue;
                        alliance.removeGuild(gid, false);
                        continue;
                    }
                    if (g_ == null || gid == alliance.getGuildId(i)) {
                        guild.changeARank(5);
                        guild.setAllianceId(0);
                        guild.broadcast(MaplePacketCreator.disbandAlliance(allianceid));
                        continue;
                    }
                    if (g_ == null) continue;
                    guild.broadcast(MaplePacketCreator.serverNotice(5, "[" + g_.getName() + "] Guild has left the alliance."));
                    guild.broadcast(MaplePacketCreator.changeGuildInAlliance(alliance, g_, false));
                    guild.broadcast(MaplePacketCreator.removeGuildFromAlliance(alliance, g_, expelled));
                }
            }
            if (gid == -1) {
                lock.writeLock().lock();
                try {
                    alliances.remove(allianceid);
                }
                finally {
                    lock.writeLock().unlock();
                }
            }
        }

        public static List<MaplePacket> getAllianceInfo(int allianceid, boolean start) {
            ArrayList<MaplePacket> ret = new ArrayList<MaplePacket>();
            MapleGuildAlliance alliance = Alliance.getAlliance(allianceid);
            if (alliance != null) {
                if (start) {
                    ret.add(MaplePacketCreator.getAllianceInfo(alliance));
                    ret.add(MaplePacketCreator.getGuildAlliance(alliance));
                }
                ret.add(MaplePacketCreator.getAllianceUpdate(alliance));
            }
            return ret;
        }

        public static void save() {
            System.out.println("Saving alliances...");
            lock.writeLock().lock();
            try {
                for (MapleGuildAlliance a : alliances.values()) {
                    a.saveToDb();
                }
            }
            finally {
                lock.writeLock().unlock();
            }
        }

        static {
            System.out.println("Loading GuildAlliances :::");
            Collection<MapleGuildAlliance> allGuilds = MapleGuildAlliance.loadAll();
            for (MapleGuildAlliance g : allGuilds) {
                alliances.put(g.getId(), g);
            }
        }
    }

    public static class Find {
        private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        private static HashMap<Integer, Integer> idToChannel = new HashMap();
        private static HashMap<String, Integer> nameToChannel = new HashMap();

        public static void register(int id, String name, int channel) {
            lock.writeLock().lock();
            try {
                idToChannel.put(id, channel);
                nameToChannel.put(name.toLowerCase(), channel);
            }
            finally {
                lock.writeLock().unlock();
            }
        }

        public static void forceDeregister(int id) {
            lock.writeLock().lock();
            try {
                idToChannel.remove(id);
            }
            finally {
                lock.writeLock().unlock();
            }
        }

        public static void forceDeregister(String id) {
            lock.writeLock().lock();
            try {
                nameToChannel.remove(id.toLowerCase());
            }
            finally {
                lock.writeLock().unlock();
            }
        }

        public static void forceDeregister(int id, String name) {
            lock.writeLock().lock();
            try {
                idToChannel.remove(id);
                nameToChannel.remove(name.toLowerCase());
            }
            finally {
                lock.writeLock().unlock();
            }
        }

        public static int findChannel(int id) {
            Integer ret;
            lock.readLock().lock();
            try {
                ret = idToChannel.get(id);
            }
            finally {
                lock.readLock().unlock();
            }
            if (ret != null) {
                if (ret != -10 && ret != -20 && ChannelServer.getInstance(ret) == null) {
                    Find.forceDeregister(id);
                    return -1;
                }
                return ret;
            }
            return -1;
        }

        public static int findChannel(String st) {
            Integer ret;
            lock.readLock().lock();
            try {
                ret = nameToChannel.get(st.toLowerCase());
            }
            finally {
                lock.readLock().unlock();
            }
            if (ret != null) {
                if (ret != -10 && ret != -20 && ChannelServer.getInstance(ret) == null) {
                    Find.forceDeregister(st);
                    return -1;
                }
                return ret;
            }
            return -1;
        }

        public static CharacterIdChannelPair[] multiBuddyFind(int charIdFrom, int[] characterIds) {
            ArrayList<CharacterIdChannelPair> foundsChars = new ArrayList<CharacterIdChannelPair>(characterIds.length);
            for (int i : characterIds) {
                int channel = Find.findChannel(i);
                if (channel <= 0) continue;
                foundsChars.add(new CharacterIdChannelPair(i, channel));
            }
            Collections.sort(foundsChars);
            return foundsChars.toArray(new CharacterIdChannelPair[foundsChars.size()]);
        }
    }

    public static class Broadcast {
        public static void broadcastSmega(byte[] message) {
            for (ChannelServer cs : ChannelServer.getAllInstances()) {
                cs.broadcastSmega(message);
            }
        }

        public static void broadcastGMMessage(byte[] message) {
            for (ChannelServer cs : ChannelServer.getAllInstances()) {
                cs.broadcastGMMessage(message);
            }
        }

        public static void broadcastMessage(byte[] message) {
            for (ChannelServer cs : ChannelServer.getAllInstances()) {
                cs.broadcastMessage(message);
            }
        }

        public static void sendPacket(List<Integer> targetIds, MaplePacket packet, int exception) {
            for (int i : targetIds) {
                MapleCharacter c;
                int ch;
                if (i == exception || (ch = Find.findChannel(i)) < 0 || (c = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(i)) == null) continue;
                c.getClient().getSession().write((Object)packet);
            }
        }

        public static void sendGuildPacket(int targetIds, MaplePacket packet, int exception, int guildid) {
            if (targetIds == exception) {
                return;
            }
            int ch = Find.findChannel(targetIds);
            if (ch < 0) {
                return;
            }
            MapleCharacter c = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(targetIds);
            if (c != null && c.getGuildId() == guildid) {
                c.getClient().getSession().write((Object)packet);
            }
        }

        public static void sendFamilyPacket(int targetIds, MaplePacket packet, int exception, int guildid) {
            if (targetIds == exception) {
                return;
            }
            int ch = Find.findChannel(targetIds);
            if (ch < 0) {
                return;
            }
            MapleCharacter c = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(targetIds);
            if (c != null && c.getFamilyId() == guildid) {
                c.getClient().getSession().write((Object)packet);
            }
        }
    }

    public static class Guild {
        private static final Map<Integer, MapleGuild> guilds = new LinkedHashMap<Integer, MapleGuild>();
        private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        public static int createGuild(int leaderId, String name) {
            return MapleGuild.createGuild(leaderId, name);
        }

        public static MapleGuild getGuild(int id) {
            MapleGuild ret = null;
            lock.readLock().lock();
            try {
                ret = guilds.get(id);
            }
            finally {
                lock.readLock().unlock();
            }
            if (ret == null) {
                lock.writeLock().lock();
                try {
                    ret = new MapleGuild(id);
                    if (ret == null || ret.getId() <= 0 || !ret.isProper()) {
                        MapleGuild mapleGuild = null;
                        return mapleGuild;
                    }
                    guilds.put(id, ret);
                }
                finally {
                    lock.writeLock().unlock();
                }
            }
            return ret;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public static MapleGuild getGuildByName(String guildName) {
            lock.readLock().lock();
            try {
                for (MapleGuild g : guilds.values()) {
                    if (!g.getName().equalsIgnoreCase(guildName)) continue;
                    MapleGuild mapleGuild = g;
                    return mapleGuild;
                }
                return null;
            }
            finally {
                lock.readLock().unlock();
            }
        }

        public static MapleGuild getGuild(MapleCharacter mc) {
            return Guild.getGuild(mc.getGuildId());
        }

        public static void setGuildMemberOnline(MapleGuildCharacter mc, boolean bOnline, int channel) {
            MapleGuild g = Guild.getGuild(mc.getGuildId());
            if (g != null) {
                g.setOnline(mc.getId(), bOnline, channel);
            }
        }

        public static void guildPacket(int gid, MaplePacket message) {
            MapleGuild g = Guild.getGuild(gid);
            if (g != null) {
                g.broadcast(message);
            }
        }

        public static int addGuildMember(MapleGuildCharacter mc) {
            MapleGuild g = Guild.getGuild(mc.getGuildId());
            if (g != null) {
                return g.addGuildMember(mc);
            }
            return 0;
        }

        public static void leaveGuild(MapleGuildCharacter mc) {
            MapleGuild g = Guild.getGuild(mc.getGuildId());
            if (g != null) {
                g.leaveGuild(mc);
            }
        }

        public static void guildChat(int gid, String name, int cid, String msg) {
            MapleGuild g = Guild.getGuild(gid);
            if (g != null) {
                g.guildChat(name, cid, msg);
            }
        }

        public static void changeRank(int gid, int cid, int newRank) {
            MapleGuild g = Guild.getGuild(gid);
            if (g != null) {
                g.changeRank(cid, newRank);
            }
        }

        public static void expelMember(MapleGuildCharacter initiator, String name, int cid) {
            MapleGuild g = Guild.getGuild(initiator.getGuildId());
            if (g != null) {
                g.expelMember(initiator, name, cid);
            }
        }

        public static void setGuildNotice(int gid, String notice) {
            MapleGuild g = Guild.getGuild(gid);
            if (g != null) {
                g.setGuildNotice(notice);
            }
        }

        public static void memberLevelJobUpdate(MapleGuildCharacter mc) {
            MapleGuild g = Guild.getGuild(mc.getGuildId());
            if (g != null) {
                g.memberLevelJobUpdate(mc);
            }
        }

        public static void changeRankTitle(int gid, String[] ranks) {
            MapleGuild g = Guild.getGuild(gid);
            if (g != null) {
                g.changeRankTitle(ranks);
            }
        }

        public static void setGuildEmblem(int gid, short bg, byte bgcolor, short logo, byte logocolor) {
            MapleGuild g = Guild.getGuild(gid);
            if (g != null) {
                g.setGuildEmblem(bg, bgcolor, logo, logocolor);
            }
        }

        public static void disbandGuild(int gid) {
            MapleGuild g = Guild.getGuild(gid);
            lock.writeLock().lock();
            try {
                if (g != null) {
                    g.disbandGuild();
                    guilds.remove(gid);
                }
            }
            finally {
                lock.writeLock().unlock();
            }
        }

        public static void deleteGuildCharacter(int guildid, int charid) {
            MapleGuildCharacter mc;
            MapleGuild g = Guild.getGuild(guildid);
            if (g != null && (mc = g.getMGC(charid)) != null) {
                if (mc.getGuildRank() > 1) {
                    g.leaveGuild(mc);
                } else {
                    g.disbandGuild();
                }
            }
        }

        public static boolean increaseGuildCapacity(int gid) {
            MapleGuild g = Guild.getGuild(gid);
            if (g != null) {
                return g.increaseCapacity();
            }
            return false;
        }

        public static void gainGP(int gid, int amount) {
            MapleGuild g = Guild.getGuild(gid);
            if (g != null) {
                g.gainGP(amount);
            }
        }

        public static int getGP(int gid) {
            MapleGuild g = Guild.getGuild(gid);
            if (g != null) {
                return g.getGP();
            }
            return 0;
        }

        public static int getInvitedId(int gid) {
            MapleGuild g = Guild.getGuild(gid);
            if (g != null) {
                return g.getInvitedId();
            }
            return 0;
        }

        public static void setInvitedId(int gid, int inviteid) {
            MapleGuild g = Guild.getGuild(gid);
            if (g != null) {
                g.setInvitedId(inviteid);
            }
        }

        public static int getGuildLeader(String guildName) {
            MapleGuild mga = Guild.getGuildByName(guildName);
            if (mga != null) {
                return mga.getLeaderId();
            }
            return 0;
        }

        public static void save() {
            System.out.println("Saving guilds...");
            lock.writeLock().lock();
            try {
                for (MapleGuild a : guilds.values()) {
                    a.writeToDB(false);
                }
            }
            finally {
                lock.writeLock().unlock();
            }
        }

        public static List<MapleBBSThread> getBBS(int gid) {
            MapleGuild g = Guild.getGuild(gid);
            if (g != null) {
                return g.getBBS();
            }
            return null;
        }

        public static int addBBSThread(int guildid, String title, String text, int icon, boolean bNotice, int posterID) {
            MapleGuild g = Guild.getGuild(guildid);
            if (g != null) {
                return g.addBBSThread(title, text, icon, bNotice, posterID);
            }
            return -1;
        }

        public static final void editBBSThread(int guildid, int localthreadid, String title, String text, int icon, int posterID, int guildRank) {
            MapleGuild g = Guild.getGuild(guildid);
            if (g != null) {
                g.editBBSThread(localthreadid, title, text, icon, posterID, guildRank);
            }
        }

        public static final void deleteBBSThread(int guildid, int localthreadid, int posterID, int guildRank) {
            MapleGuild g = Guild.getGuild(guildid);
            if (g != null) {
                g.deleteBBSThread(localthreadid, posterID, guildRank);
            }
        }

        public static final void addBBSReply(int guildid, int localthreadid, String text, int posterID) {
            MapleGuild g = Guild.getGuild(guildid);
            if (g != null) {
                g.addBBSReply(localthreadid, text, posterID);
            }
        }

        public static final void deleteBBSReply(int guildid, int localthreadid, int replyid, int posterID, int guildRank) {
            MapleGuild g = Guild.getGuild(guildid);
            if (g != null) {
                g.deleteBBSReply(localthreadid, replyid, posterID, guildRank);
            }
        }

        public static void changeEmblem(int gid, int affectedPlayers, MapleGuildSummary mgs) {
            Broadcast.sendGuildPacket(affectedPlayers, MaplePacketCreator.guildEmblemChange(gid, mgs.getLogoBG(), mgs.getLogoBGColor(), mgs.getLogo(), mgs.getLogoColor()), -1, gid);
            Guild.setGuildAndRank(affectedPlayers, -1, -1, -1);
        }

        public static void setGuildAndRank(int cid, int guildid, int rank, int alliancerank) {
            boolean bDifferentGuild;
            int ch = Find.findChannel(cid);
            if (ch == -1) {
                return;
            }
            MapleCharacter mc = World.getStorage(ch).getCharacterById(cid);
            if (mc == null) {
                return;
            }
            if (guildid == -1 && rank == -1) {
                bDifferentGuild = true;
            } else {
                bDifferentGuild = guildid != mc.getGuildId();
                mc.setGuildId(guildid);
                mc.setGuildRank((byte)rank);
                mc.setAllianceRank((byte)alliancerank);
                mc.saveGuildStatus();
            }
            if (bDifferentGuild && ch > 0) {
                mc.getMap().broadcastMessage(mc, MaplePacketCreator.removePlayerFromMap(cid), false);
                mc.getMap().broadcastMessage(mc, MaplePacketCreator.spawnPlayerMapobject(mc), false);
            }
        }

        static {
            System.out.println("Loading Guilds :::");
            Collection<MapleGuild> allGuilds = MapleGuild.loadAll();
            for (MapleGuild g : allGuilds) {
                if (!g.isProper()) continue;
                guilds.put(g.getId(), g);
            }
        }
    }

    public static class Messenger {
        private static Map<Integer, MapleMessenger> messengers = new HashMap<Integer, MapleMessenger>();
        private static final AtomicInteger runningMessengerId = new AtomicInteger();

        public static MapleMessenger createMessenger(MapleMessengerCharacter chrfor) {
            int messengerid = runningMessengerId.getAndIncrement();
            MapleMessenger messenger = new MapleMessenger(messengerid, chrfor);
            messengers.put(messenger.getId(), messenger);
            return messenger;
        }

        public static void declineChat(String target, String namefrom) {
            MapleMessenger messenger;
            ChannelServer cs;
            MapleCharacter chr;
            int ch = Find.findChannel(target);
            if (ch > 0 && (chr = (cs = ChannelServer.getInstance(ch)).getPlayerStorage().getCharacterByName(target)) != null && (messenger = chr.getMessenger()) != null) {
                chr.getClient().getSession().write((Object)MaplePacketCreator.messengerNote(namefrom, 5, 0));
            }
        }

        public static MapleMessenger getMessenger(int messengerid) {
            return messengers.get(messengerid);
        }

        public static void leaveMessenger(int messengerid, MapleMessengerCharacter target) {
            MapleMessenger messenger = Messenger.getMessenger(messengerid);
            if (messenger == null) {
                throw new IllegalArgumentException("No messenger with the specified messengerid exists");
            }
            int position = messenger.getPositionByName(target.getName());
            messenger.removeMember(target);
            for (MapleMessengerCharacter mmc : messenger.getMembers()) {
                MapleCharacter chr;
                int ch;
                if (mmc == null || (ch = Find.findChannel(mmc.getId())) <= 0 || (chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(mmc.getName())) == null) continue;
                chr.getClient().getSession().write((Object)MaplePacketCreator.removeMessengerPlayer(position));
            }
        }

        public static void silentLeaveMessenger(int messengerid, MapleMessengerCharacter target) {
            MapleMessenger messenger = Messenger.getMessenger(messengerid);
            if (messenger == null) {
                throw new IllegalArgumentException("No messenger with the specified messengerid exists");
            }
            messenger.silentRemoveMember(target);
        }

        public static void silentJoinMessenger(int messengerid, MapleMessengerCharacter target) {
            MapleMessenger messenger = Messenger.getMessenger(messengerid);
            if (messenger == null) {
                throw new IllegalArgumentException("No messenger with the specified messengerid exists");
            }
            messenger.silentAddMember(target);
        }

        public static void updateMessenger(int messengerid, String namefrom, int fromchannel) {
            MapleMessenger messenger = Messenger.getMessenger(messengerid);
            int position = messenger.getPositionByName(namefrom);
            for (MapleMessengerCharacter messengerchar : messenger.getMembers()) {
                MapleCharacter chr;
                int ch;
                if (messengerchar == null || messengerchar.getName().equals(namefrom) || (ch = Find.findChannel(messengerchar.getName())) <= 0 || (chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(messengerchar.getName())) == null) continue;
                MapleCharacter from = ChannelServer.getInstance(fromchannel).getPlayerStorage().getCharacterByName(namefrom);
                chr.getClient().getSession().write((Object)MaplePacketCreator.updateMessengerPlayer(namefrom, from, position, fromchannel - 1));
            }
        }

        public static void joinMessenger(int messengerid, MapleMessengerCharacter target, String from, int fromchannel) {
            MapleMessenger messenger = Messenger.getMessenger(messengerid);
            if (messenger == null) {
                throw new IllegalArgumentException("No messenger with the specified messengerid exists");
            }
            messenger.addMember(target);
            int position = messenger.getPositionByName(target.getName());
            for (MapleMessengerCharacter messengerchar : messenger.getMembers()) {
                MapleCharacter chr;
                if (messengerchar == null) continue;
                int mposition = messenger.getPositionByName(messengerchar.getName());
                int ch = Find.findChannel(messengerchar.getName());
                if (ch <= 0 || (chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(messengerchar.getName())) == null) continue;
                if (!messengerchar.getName().equals(from)) {
                    MapleCharacter fromCh = ChannelServer.getInstance(fromchannel).getPlayerStorage().getCharacterByName(from);
                    chr.getClient().getSession().write((Object)MaplePacketCreator.addMessengerPlayer(from, fromCh, position, fromchannel - 1));
                    fromCh.getClient().getSession().write((Object)MaplePacketCreator.addMessengerPlayer(chr.getName(), chr, mposition, messengerchar.getChannel() - 1));
                    continue;
                }
                chr.getClient().getSession().write((Object)MaplePacketCreator.joinMessenger(mposition));
            }
        }

        public static void messengerChat(int messengerid, String chattext, String namefrom) {
            MapleMessenger messenger = Messenger.getMessenger(messengerid);
            if (messenger == null) {
                throw new IllegalArgumentException("No messenger with the specified messengerid exists");
            }
            for (MapleMessengerCharacter messengerchar : messenger.getMembers()) {
                int ch;
                if (messengerchar != null && !messengerchar.getName().equals(namefrom)) {
                    MapleCharacter chr;
                    ch = Find.findChannel(messengerchar.getName());
                    if (ch <= 0 || (chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(messengerchar.getName())) == null) continue;
                    chr.getClient().getSession().write((Object)MaplePacketCreator.messengerChat(chattext));
                    continue;
                }
                if (messengerchar == null || (ch = Find.findChannel(messengerchar.getName())) <= 0) continue;
                MapleCharacter mapleCharacter = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(messengerchar.getName());
            }
        }

        public static void messengerInvite(String sender, int messengerid, String target, int fromchannel, boolean gm) {
            int ch;
            if (World.isConnected(target) && (ch = Find.findChannel(target)) > 0) {
                MapleCharacter from = ChannelServer.getInstance(fromchannel).getPlayerStorage().getCharacterByName(sender);
                MapleCharacter targeter = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(target);
                if (targeter != null && targeter.getMessenger() == null) {
                    if (!targeter.isGM() || gm) {
                        targeter.getClient().getSession().write((Object)MaplePacketCreator.messengerInvite(sender, messengerid));
                        from.getClient().getSession().write((Object)MaplePacketCreator.messengerNote(target, 4, 1));
                    } else {
                        from.getClient().getSession().write((Object)MaplePacketCreator.messengerNote(target, 4, 0));
                    }
                } else {
                    from.getClient().getSession().write((Object)MaplePacketCreator.messengerChat(sender + " : " + target + " is already using Maple Messenger"));
                }
            }
        }

        static {
            runningMessengerId.set(1);
        }
    }

    public static class Buddy {
        public static void buddyChat(int[] recipientCharacterIds, int cidFrom, String nameFrom, String chattext) {
            for (int characterId : recipientCharacterIds) {
                MapleCharacter chr;
                int ch = Find.findChannel(characterId);
                if (ch <= 0 || (chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(characterId)) == null || !chr.getBuddylist().containsVisible(cidFrom)) continue;
                chr.getClient().getSession().write((Object)MaplePacketCreator.multiChat(nameFrom, chattext, 0));
            }
        }

        private static void updateBuddies(int characterId, int channel, int[] buddies, boolean offline, int gmLevel, boolean isHidden) {
            for (int buddy : buddies) {
                int mcChannel;
                BuddylistEntry ble;
                MapleCharacter chr;
                int ch = Find.findChannel(buddy);
                if (ch <= 0 || (chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(buddy)) == null || (ble = chr.getBuddylist().get(characterId)) == null || !ble.isVisible()) continue;
                if (offline || isHidden && chr.getGMLevel() < gmLevel) {
                    ble.setChannel(-1);
                    mcChannel = -1;
                } else {
                    ble.setChannel(channel);
                    mcChannel = channel - 1;
                }
                chr.getBuddylist().put(ble);
                chr.getClient().getSession().write((Object)MaplePacketCreator.updateBuddyChannel(ble.getCharacterId(), mcChannel));
            }
        }

        public static void buddyChanged(int cid, int cidFrom, String name, int channel, BuddyList.BuddyOperation operation, int level, int job, String group) {
            MapleCharacter addChar;
            int ch = Find.findChannel(cid);
            if (ch > 0 && (addChar = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(cid)) != null) {
                BuddyList buddylist = addChar.getBuddylist();
                switch (operation) {
                    case ADDED: {
                        if (!buddylist.contains(cidFrom)) break;
                        buddylist.put(new BuddylistEntry(name, cidFrom, group, channel, true, level, job));
                        addChar.getClient().getSession().write((Object)MaplePacketCreator.updateBuddyChannel(cidFrom, channel - 1));
                        break;
                    }
                    case DELETED: {
                        if (!buddylist.contains(cidFrom)) break;
                        buddylist.put(new BuddylistEntry(name, cidFrom, group, -1, buddylist.get(cidFrom).isVisible(), level, job));
                        addChar.getClient().getSession().write((Object)MaplePacketCreator.updateBuddyChannel(cidFrom, -1));
                    }
                }
            }
        }

        public static BuddyList.BuddyAddResult requestBuddyAdd(String addName, int channelFrom, int cidFrom, String nameFrom, int levelFrom, int jobFrom) {
            MapleCharacter addChar;
            int ch = Find.findChannel(cidFrom);
            if (ch > 0 && (addChar = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(addName)) != null) {
                BuddyList buddylist = addChar.getBuddylist();
                if (buddylist.isFull()) {
                    return BuddyList.BuddyAddResult.BUDDYLIST_FULL;
                }
                if (!buddylist.contains(cidFrom)) {
                    buddylist.addBuddyRequest(addChar.getClient(), cidFrom, nameFrom, channelFrom, levelFrom, jobFrom);
                } else if (buddylist.containsVisible(cidFrom)) {
                    return BuddyList.BuddyAddResult.ALREADY_ON_LIST;
                }
            }
            return BuddyList.BuddyAddResult.OK;
        }

        public static void loggedOn(String name, int characterId, int channel, int[] buddies, int gmLevel, boolean isHidden) {
            Buddy.updateBuddies(characterId, channel, buddies, false, gmLevel, isHidden);
        }

        public static void loggedOff(String name, int characterId, int channel, int[] buddies, int gmLevel, boolean isHidden) {
            Buddy.updateBuddies(characterId, channel, buddies, true, gmLevel, isHidden);
        }
    }

    public static class Party {
        private static Map<Integer, MapleParty> parties = new HashMap<Integer, MapleParty>();
        private static final AtomicInteger runningPartyId = new AtomicInteger();

        public static void partyChat(int partyid, String chattext, String namefrom) {
            MapleParty party = Party.getParty(partyid);
            if (party == null) {
                throw new IllegalArgumentException("no party with the specified partyid exists");
            }
            for (MaplePartyCharacter partychar : party.getMembers()) {
                MapleCharacter chr;
                int ch = Find.findChannel(partychar.getName());
                if (ch <= 0 || (chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(partychar.getName())) == null || chr.getName().equalsIgnoreCase(namefrom)) continue;
                chr.getClient().getSession().write((Object)MaplePacketCreator.multiChat(namefrom, chattext, 1));
            }
        }

        public static void updateParty(int partyid, PartyOperation operation, MaplePartyCharacter target) {
            MapleParty party = Party.getParty(partyid);
            if (party == null) {
                return;
            }
            switch (operation) {
                case JOIN: {
                    party.addMember(target);
                    break;
                }
                case EXPEL: 
                case LEAVE: {
                    party.removeMember(target);
                    break;
                }
                case DISBAND: {
                    Party.disbandParty(partyid);
                    break;
                }
                case SILENT_UPDATE: 
                case LOG_ONOFF: {
                    party.updateMember(target);
                    break;
                }
                case CHANGE_LEADER: 
                case CHANGE_LEADER_DC: {
                    party.setLeader(target);
                    break;
                }
                default: {
                    throw new RuntimeException("Unhandeled updateParty operation " + operation.name());
                }
            }
            for (MaplePartyCharacter partychar : party.getMembers()) {
                MapleCharacter chr;
                int ch = Find.findChannel(partychar.getName());
                if (ch <= 0 || (chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(partychar.getName())) == null) continue;
                if (operation == PartyOperation.DISBAND) {
                    chr.setParty(null);
                } else {
                    chr.setParty(party);
                }
                chr.getClient().getSession().write((Object)MaplePacketCreator.updateParty(chr.getClient().getChannel(), party, operation, target));
            }
            switch (operation) {
                case EXPEL: 
                case LEAVE: {
                    MapleCharacter chr;
                    int ch = Find.findChannel(target.getName());
                    if (ch <= 0 || (chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(target.getName())) == null) break;
                    chr.getClient().getSession().write((Object)MaplePacketCreator.updateParty(chr.getClient().getChannel(), party, operation, target));
                    chr.setParty(null);
                }
            }
        }

        public static MapleParty createParty(MaplePartyCharacter chrfor) {
            int partyid = runningPartyId.getAndIncrement();
            MapleParty party = new MapleParty(partyid, chrfor);
            parties.put(party.getId(), party);
            return party;
        }

        public static MapleParty getParty(int partyid) {
            return parties.get(partyid);
        }

        public static MapleParty disbandParty(int partyid) {
            return parties.remove(partyid);
        }

        static {
            Connection con = DatabaseConnection.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement("SELECT MAX(party)+2 FROM characters");
                ResultSet rs = ps.executeQuery();
                rs.next();
                runningPartyId.set(rs.getInt(1));
                rs.close();
                ps.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

