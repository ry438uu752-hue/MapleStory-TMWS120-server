/*
 * Decompiled with CFR 0.152.
 */
package handling.channel;

import client.MapleCharacter;
import constants.ServerConstants;
import handling.ByteArrayMaplePacket;
import handling.MaplePacket;
import handling.MapleServerHandler;
import handling.channel.PlayerStorage;
import handling.login.LoginServer;
import handling.mina.MapleCodecFactory;
import handling.world.CheaterData;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.ByteBufferAllocator;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoFilter;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoServiceConfig;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;
import scripting.EventScriptManager;
import server.MapleSquad;
import server.ServerProperties;
import server.events.MapleCoconut;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.events.MapleFitness;
import server.events.MapleOla;
import server.events.MapleOxQuiz;
import server.events.MapleSnowball;
import server.life.PlayerNPC;
import server.maps.MapleMapFactory;
import server.shops.HiredMerchant;
import tools.CollectionUtil;
import tools.ConcurrentEnumMap;
import tools.MaplePacketCreator;

public class ChannelServer
implements Serializable {
    public static long serverStartTime;
    private int expRate;
    private int mesoRate;
    private int dropRate;
    private int cashRate;
    private short port = (short)8585;
    private static final short DEFAULT_PORT = 8585;
    private int channel;
    private int running_MerchantID = 0;
    private int flags = 0;
    private String serverMessage;
    private String key;
    private String ip;
    private String serverName;
    private boolean shutdown = false;
    private boolean finishedShutdown = false;
    private boolean MegaphoneMuteState = false;
    private boolean adminOnly = false;
    private PlayerStorage players;
    private MapleServerHandler serverHandler;
    private IoAcceptor acceptor;
    private final MapleMapFactory mapFactory;
    private EventScriptManager eventSM;
    private static final Map<Integer, ChannelServer> instances;
    private final Map<MapleSquad.MapleSquadType, MapleSquad> mapleSquads = new ConcurrentEnumMap<MapleSquad.MapleSquadType, MapleSquad>(MapleSquad.MapleSquadType.class);
    private final Map<Integer, HiredMerchant> merchants = new HashMap<Integer, HiredMerchant>();
    private final Map<Integer, PlayerNPC> playerNPCs = new HashMap<Integer, PlayerNPC>();
    private final ReentrantReadWriteLock merchLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock squadLock = new ReentrantReadWriteLock();
    private int eventmap = -1;
    private final Map<MapleEventType, MapleEvent> events = new EnumMap<MapleEventType, MapleEvent>(MapleEventType.class);
    private boolean debugMode = false;

    private ChannelServer(String key, int channel) {
        this.key = key;
        this.channel = channel;
        this.mapFactory = new MapleMapFactory();
        this.mapFactory.setChannel(channel);
    }

    public static Set<Integer> getAllInstance() {
        return new HashSet<Integer>(instances.keySet());
    }

    public final void loadEvents() {
        if (this.events.size() != 0) {
            return;
        }
        this.events.put(MapleEventType.CokePlay, new MapleCoconut(this.channel, MapleEventType.CokePlay.mapids));
        this.events.put(MapleEventType.Coconut, new MapleCoconut(this.channel, MapleEventType.Coconut.mapids));
        this.events.put(MapleEventType.Fitness, new MapleFitness(this.channel, MapleEventType.Fitness.mapids));
        this.events.put(MapleEventType.OlaOla, new MapleOla(this.channel, MapleEventType.OlaOla.mapids));
        this.events.put(MapleEventType.OxQuiz, new MapleOxQuiz(this.channel, MapleEventType.OxQuiz.mapids));
        this.events.put(MapleEventType.Snowball, new MapleSnowball(this.channel, MapleEventType.Snowball.mapids));
    }

    public final void run_startup_configurations() {
        this.setChannel(this.channel);
        try {
            this.expRate = Integer.parseInt(ServerProperties.getProperty("tms.Exp"));
            this.mesoRate = Integer.parseInt(ServerProperties.getProperty("tms.Meso"));
            this.dropRate = Integer.parseInt(ServerProperties.getProperty("tms.Drop"));
            this.cashRate = Integer.parseInt(ServerProperties.getProperty("tms.Cash"));
            this.serverMessage = ServerProperties.getProperty("tms.ServerMessage");
            this.serverName = ServerProperties.getProperty("tms.ServerName");
            this.flags = Integer.parseInt(ServerProperties.getProperty("tms.WFlags", "0"));
            this.adminOnly = Boolean.parseBoolean(ServerProperties.getProperty("tms.Admin", "false"));
            this.eventSM = new EventScriptManager(this, ServerProperties.getProperty("tms.Events").split(","));
            this.port = Short.parseShort(ServerProperties.getProperty("tms.Port" + this.channel, String.valueOf(8585 + this.channel)));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.ip = ServerProperties.getProperty("tms.IP") + ":" + this.port;
        ByteBuffer.setUseDirectBuffers((boolean)false);
        ByteBuffer.setAllocator((ByteBufferAllocator)new SimpleByteBufferAllocator());
        this.acceptor = new SocketAcceptor();
        SocketAcceptorConfig acceptor_config = new SocketAcceptorConfig();
        acceptor_config.getSessionConfig().setTcpNoDelay(true);
        acceptor_config.setDisconnectOnUnbind(true);
        acceptor_config.getFilterChain().addLast("codec", (IoFilter)new ProtocolCodecFilter((ProtocolCodecFactory)new MapleCodecFactory()));
        this.players = new PlayerStorage(this.channel);
        this.loadEvents();
        try {
            this.serverHandler = new MapleServerHandler(this.channel, false);
            this.acceptor.bind((SocketAddress)new InetSocketAddress(this.port), (IoHandler)this.serverHandler, (IoServiceConfig)acceptor_config);
            System.out.println("Channel " + this.channel + ": Listening on port " + this.port + "");
            this.eventSM.init();
        }
        catch (IOException e) {
            System.out.println("Binding to port " + this.port + " failed (ch: " + this.getChannel() + ")" + e);
        }
    }

    public final void shutdown(Object threadToNotify) {
        if (this.finishedShutdown) {
            return;
        }
        this.broadcastPacket(MaplePacketCreator.serverNotice(0, "\u9019\u500b\u983b\u9053\u6b63\u5728\u95dc\u9589\u4e2d."));
        this.shutdown = true;
        System.out.println("Channel " + this.channel + ", Saving hired merchants...");
        this.closeAllMerchant();
        System.out.println("Channel " + this.channel + ", Saving characters...");
        this.getPlayerStorage().disconnectAll();
        System.out.println("Channel " + this.channel + ", Unbinding...");
        this.acceptor.unbindAll();
        this.acceptor = null;
        instances.remove(this.channel);
        LoginServer.removeChannel(this.channel);
        this.setFinishShutdown();
    }

    public final void unbind() {
        this.acceptor.unbindAll();
    }

    public final boolean hasFinishedShutdown() {
        return this.finishedShutdown;
    }

    public final MapleMapFactory getMapFactory() {
        return this.mapFactory;
    }

    public static final ChannelServer newInstance(String key, int channel) {
        return new ChannelServer(key, channel);
    }

    public static final ChannelServer getInstance(int channel) {
        return instances.get(channel);
    }

    public final void addPlayer(MapleCharacter chr) {
        this.getPlayerStorage().registerPlayer(chr);
        chr.getClient().getSession().write((Object)MaplePacketCreator.serverMessage(this.serverMessage));
    }

    public final PlayerStorage getPlayerStorage() {
        if (this.players == null) {
            this.players = new PlayerStorage(this.channel);
        }
        return this.players;
    }

    public final void removePlayer(MapleCharacter chr) {
        this.getPlayerStorage().deregisterPlayer(chr);
    }

    public final void removePlayer(int idz, String namez) {
        this.getPlayerStorage().deregisterPlayer(idz, namez);
    }

    public final String getServerMessage() {
        return this.serverMessage;
    }

    public final void setServerMessage(String newMessage) {
        this.serverMessage = newMessage;
        this.broadcastPacket(MaplePacketCreator.serverMessage(this.serverMessage));
    }

    public final void broadcastPacket(MaplePacket data) {
        this.getPlayerStorage().broadcastPacket(data);
    }

    public final void broadcastSmegaPacket(MaplePacket data) {
        this.getPlayerStorage().broadcastSmegaPacket(data);
    }

    public final void broadcastGMPacket(MaplePacket data) {
        this.getPlayerStorage().broadcastGMPacket(data);
    }

    public final int getExpRate() {
        return this.expRate;
    }

    public final void setExpRate(int expRate) {
        this.expRate = expRate;
    }

    public final int getCashRate() {
        return this.cashRate;
    }

    public final void setCashRate(int cashRate) {
        this.cashRate = cashRate;
    }

    public final int getChannel() {
        return this.channel;
    }

    public final void setChannel(int channel) {
        instances.put(channel, this);
        LoginServer.addChannel(channel);
    }

    public static final Collection<ChannelServer> getAllInstances() {
        return Collections.unmodifiableCollection(instances.values());
    }

    public final String getIP() {
        return this.ip;
    }

    public final boolean isShutdown() {
        return this.shutdown;
    }

    public final int getLoadedMaps() {
        return this.mapFactory.getLoadedMaps();
    }

    public final EventScriptManager getEventSM() {
        return this.eventSM;
    }

    public final void reloadEvents() {
        this.eventSM.cancel();
        this.eventSM = new EventScriptManager(this, ServerProperties.getProperty("K\uff0e").split(","));
        this.eventSM.init();
    }

    public final int getMesoRate() {
        return this.mesoRate;
    }

    public final void setMesoRate(int mesoRate) {
        this.mesoRate = mesoRate;
    }

    public final int getDropRate() {
        return this.dropRate;
    }

    public final void setDropRate(int dropRate) {
        this.dropRate = dropRate;
    }

    public static final void startChannel_Main() {
        serverStartTime = System.currentTimeMillis();
        for (int i = 0; i < Integer.parseInt(ServerProperties.getProperty("tms.Count", "0")); ++i) {
            ChannelServer.newInstance(ServerConstants.Channel_Key[i], i + 1).run_startup_configurations();
        }
    }

    public static final void startChannel(int channel) {
        serverStartTime = System.currentTimeMillis();
        for (int i = 0; i < Integer.parseInt(ServerProperties.getProperty("tms.Count", "0")); ++i) {
            if (channel != i + 1) continue;
            ChannelServer.newInstance(ServerConstants.Channel_Key[i], i + 1).run_startup_configurations();
            break;
        }
    }

    public Map<MapleSquad.MapleSquadType, MapleSquad> getAllSquads() {
        return Collections.unmodifiableMap(this.mapleSquads);
    }

    public final MapleSquad getMapleSquad(String type) {
        return this.getMapleSquad(MapleSquad.MapleSquadType.valueOf(type.toLowerCase()));
    }

    public final MapleSquad getMapleSquad(MapleSquad.MapleSquadType type) {
        return this.mapleSquads.get((Object)type);
    }

    public final boolean addMapleSquad(MapleSquad squad, String type) {
        MapleSquad.MapleSquadType types = MapleSquad.MapleSquadType.valueOf(type.toLowerCase());
        if (types != null && !this.mapleSquads.containsKey((Object)types)) {
            this.mapleSquads.put(types, squad);
            squad.scheduleRemoval();
            return true;
        }
        return false;
    }

    public final boolean removeMapleSquad(MapleSquad.MapleSquadType types) {
        if (types != null && this.mapleSquads.containsKey((Object)types)) {
            this.mapleSquads.remove((Object)types);
            return true;
        }
        return false;
    }

    public final void closeAllMerchant() {
        this.merchLock.writeLock().lock();
        try {
            Iterator<HiredMerchant> merchants_ = this.merchants.values().iterator();
            while (merchants_.hasNext()) {
                merchants_.next().closeShop(true, false);
                merchants_.remove();
            }
        }
        finally {
            this.merchLock.writeLock().unlock();
        }
    }

    public final int addMerchant(HiredMerchant hMerchant) {
        this.merchLock.writeLock().lock();
        int runningmer = 0;
        try {
            runningmer = this.running_MerchantID;
            this.merchants.put(this.running_MerchantID, hMerchant);
            ++this.running_MerchantID;
        }
        finally {
            this.merchLock.writeLock().unlock();
        }
        return runningmer;
    }

    public final void removeMerchant(HiredMerchant hMerchant) {
        this.merchLock.writeLock().lock();
        try {
            this.merchants.remove(hMerchant.getStoreId());
        }
        finally {
            this.merchLock.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final boolean containsMerchant(int accid) {
        boolean contains = false;
        this.merchLock.readLock().lock();
        try {
            Iterator<HiredMerchant> itr = this.merchants.values().iterator();
            while (itr.hasNext()) {
                if (itr.next().getOwnerAccId() != accid) continue;
                contains = true;
                break;
            }
        }
        finally {
            this.merchLock.readLock().unlock();
        }
        return contains;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final List<HiredMerchant> searchMerchant(int itemSearch) {
        LinkedList<HiredMerchant> list = new LinkedList<HiredMerchant>();
        this.merchLock.readLock().lock();
        try {
            for (HiredMerchant hm : this.merchants.values()) {
                if (hm.searchItem(itemSearch).size() <= 0) continue;
                list.add(hm);
            }
        }
        finally {
            this.merchLock.readLock().unlock();
        }
        return list;
    }

    public final void toggleMegaphoneMuteState() {
        this.MegaphoneMuteState = !this.MegaphoneMuteState;
    }

    public final boolean getMegaphoneMuteState() {
        return this.MegaphoneMuteState;
    }

    public int getEvent() {
        return this.eventmap;
    }

    public final void setEvent(int ze) {
        this.eventmap = ze;
    }

    public MapleEvent getEvent(MapleEventType t) {
        return this.events.get((Object)t);
    }

    public final Collection<PlayerNPC> getAllPlayerNPC() {
        return this.playerNPCs.values();
    }

    public final PlayerNPC getPlayerNPC(int id) {
        return this.playerNPCs.get(id);
    }

    public final void addPlayerNPC(PlayerNPC npc) {
        if (this.playerNPCs.containsKey(npc.getId())) {
            this.removePlayerNPC(npc);
        }
        this.playerNPCs.put(npc.getId(), npc);
        this.getMapFactory().getMap(npc.getMapId()).addMapObject(npc);
    }

    public final void removePlayerNPC(PlayerNPC npc) {
        if (this.playerNPCs.containsKey(npc.getId())) {
            this.playerNPCs.remove(npc.getId());
            this.getMapFactory().getMap(npc.getMapId()).removeMapObject(npc);
        }
    }

    public final String getServerName() {
        return this.serverName;
    }

    public final void setServerName(String sn) {
        this.serverName = sn;
    }

    public final int getPort() {
        return this.port;
    }

    public static final Set<Integer> getChannelServer() {
        return new HashSet<Integer>(instances.keySet());
    }

    public final void setShutdown() {
        this.shutdown = true;
        System.out.println("Channel " + this.channel + " has set to shutdown.");
    }

    public final void setFinishShutdown() {
        this.finishedShutdown = true;
        System.out.println("Channel " + this.channel + " has finished shutdown.");
    }

    public final boolean isAdminOnly() {
        return this.adminOnly;
    }

    public static final int getChannelCount() {
        return instances.size();
    }

    public final MapleServerHandler getServerHandler() {
        return this.serverHandler;
    }

    public final int getTempFlag() {
        return this.flags;
    }

    public static Map<Integer, Integer> getChannelLoad() {
        HashMap<Integer, Integer> ret = new HashMap<Integer, Integer>();
        for (ChannelServer cs : instances.values()) {
            ret.put(cs.getChannel(), cs.getConnectedClients());
        }
        return ret;
    }

    public int getConnectedClients() {
        return this.getPlayerStorage().getConnectedClients();
    }

    public List<CheaterData> getCheaters() {
        List<CheaterData> cheaters = this.getPlayerStorage().getCheaters();
        Collections.sort(cheaters);
        return CollectionUtil.copyFirst(cheaters, 20);
    }

    public void broadcastMessage(byte[] message) {
        this.broadcastPacket(new ByteArrayMaplePacket(message));
    }

    public void broadcastSmega(byte[] message) {
        this.broadcastSmegaPacket(new ByteArrayMaplePacket(message));
    }

    public void broadcastGMMessage(byte[] message) {
        this.broadcastGMPacket(new ByteArrayMaplePacket(message));
    }

    public void saveAll() {
        int ppl = 0;
        for (MapleCharacter chr : this.players.getAllCharacters()) {
            ++ppl;
            chr.saveToDB(false, false);
        }
        System.out.println("[\u81ea\u52d5\u5b58\u6a94] \u5df2\u7d93\u5c07\u983b\u9053 " + this.channel + " \u7684 " + ppl + " \u500b\u73a9\u5bb6\u4fdd\u5b58\u5230\u6578\u64da\u4e2d.");
    }

    static {
        instances = new HashMap<Integer, ChannelServer>();
    }
}

