/*
 * Decompiled with CFR 0.152.
 */
package server.maps;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Equip;
import client.inventory.IItem;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import constants.GameConstants;
import database.DatabaseConnection;
import handling.MaplePacket;
import handling.channel.ChannelServer;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.World;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import scripting.EventManager;
import server.MapleCarnivalFactory;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.MapleSquad;
import server.MapleStatEffect;
import server.Randomizer;
import server.SpeedRunner;
import server.Timer;
import server.events.MapleEvent;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.life.MapleNPC;
import server.life.MonsterDropEntry;
import server.life.MonsterGlobalDropEntry;
import server.life.SpawnPoint;
import server.life.SpawnPointAreaBoss;
import server.life.Spawns;
import server.maps.FieldLimitType;
import server.maps.MapScriptMethods;
import server.maps.MapleDoor;
import server.maps.MapleDragon;
import server.maps.MapleFoothold;
import server.maps.MapleFootholdTree;
import server.maps.MapleMapEffect;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleMist;
import server.maps.MapleNodes;
import server.maps.MapleReactor;
import server.maps.MapleReactorFactory;
import server.maps.MapleReactorStats;
import server.maps.MapleSummon;
import server.maps.SpeedRunType;
import server.maps.SummonMovementType;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.StringUtil;
import tools.packet.MobPacket;
import tools.packet.PetPacket;

public final class MapleMap {
    private final Map<MapleMapObjectType, LinkedHashMap<Integer, MapleMapObject>> mapobjects;
    private final Map<MapleMapObjectType, ReentrantReadWriteLock> mapobjectlocks;
    private final List<MapleCharacter> characters = new ArrayList<MapleCharacter>();
    private final ReentrantReadWriteLock charactersLock = new ReentrantReadWriteLock();
    private int runningOid = 100000;
    private final Lock runningOidLock = new ReentrantLock();
    private final List<Spawns> monsterSpawn = new ArrayList<Spawns>();
    private final AtomicInteger spawnedMonstersOnMap = new AtomicInteger(0);
    private final Map<Integer, MaplePortal> portals = new HashMap<Integer, MaplePortal>();
    private MapleFootholdTree footholds = null;
    private float monsterRate;
    private float recoveryRate;
    private MapleMapEffect mapEffect;
    private byte channel;
    private short decHP = 0;
    private short createMobInterval = (short)9000;
    private int consumeItemCoolTime = 0;
    private int protectItem = 0;
    private int decHPInterval = 10000;
    private int mapid;
    private int returnMapId;
    private int timeLimit;
    private int fieldLimit;
    private int maxRegularSpawn = 0;
    private int fixedMob;
    private int forcedReturnMap = 999999999;
    private int lvForceMove = 0;
    private int lvLimit = 0;
    private int permanentWeather = 0;
    private boolean town;
    private boolean clock;
    private boolean personalShop;
    private boolean everlast = false;
    private boolean dropsDisabled = false;
    private boolean gDropsDisabled = false;
    private boolean soaring = false;
    private boolean squadTimer = false;
    private boolean isSpawns = true;
    private String mapName;
    private String streetName;
    private String onUserEnter;
    private String onFirstUserEnter;
    private String speedRunLeader = "";
    private List<Integer> dced = new ArrayList<Integer>();
    private ScheduledFuture<?> squadSchedule;
    private long speedRunStart = 0L;
    private long lastSpawnTime = 0L;
    private long lastHurtTime = 0L;
    private MapleNodes nodes;
    private MapleSquad.MapleSquadType squad;
    private Map<String, Integer> environment = new LinkedHashMap<String, Integer>();

    public MapleMap(int mapid, int channel, int returnMapId, float monsterRate) {
        this.mapid = mapid;
        this.channel = (byte)channel;
        this.returnMapId = returnMapId;
        if (this.returnMapId == 999999999) {
            this.returnMapId = mapid;
        }
        this.monsterRate = monsterRate;
        EnumMap objsMap = new EnumMap(MapleMapObjectType.class);
        EnumMap<MapleMapObjectType, ReentrantReadWriteLock> objlockmap = new EnumMap<MapleMapObjectType, ReentrantReadWriteLock>(MapleMapObjectType.class);
        for (MapleMapObjectType type : MapleMapObjectType.values()) {
            objsMap.put(type, new LinkedHashMap());
            objlockmap.put(type, new ReentrantReadWriteLock());
        }
        this.mapobjects = Collections.unmodifiableMap(objsMap);
        this.mapobjectlocks = Collections.unmodifiableMap(objlockmap);
    }

    public final void setSpawns(boolean fm) {
        this.isSpawns = fm;
    }

    public final boolean getSpawns() {
        return this.isSpawns;
    }

    public final void setFixedMob(int fm) {
        this.fixedMob = fm;
    }

    public final void setForceMove(int fm) {
        this.lvForceMove = fm;
    }

    public final int getForceMove() {
        return this.lvForceMove;
    }

    public final void setLevelLimit(int fm) {
        this.lvLimit = fm;
    }

    public final int getLevelLimit() {
        return this.lvLimit;
    }

    public final void setReturnMapId(int rmi) {
        this.returnMapId = rmi;
    }

    public final void setSoaring(boolean b) {
        this.soaring = b;
    }

    public final boolean canSoar() {
        return this.soaring;
    }

    public final void toggleDrops() {
        this.dropsDisabled = !this.dropsDisabled;
    }

    public final void setDrops(boolean b) {
        this.dropsDisabled = b;
    }

    public final void toggleGDrops() {
        this.gDropsDisabled = !this.gDropsDisabled;
    }

    public final int getId() {
        return this.mapid;
    }

    public final MapleMap getReturnMap() {
        return ChannelServer.getInstance(this.channel).getMapFactory().getMap(this.returnMapId);
    }

    public final int getReturnMapId() {
        return this.returnMapId;
    }

    public final int getForcedReturnId() {
        return this.forcedReturnMap;
    }

    public final MapleMap getForcedReturnMap() {
        return ChannelServer.getInstance(this.channel).getMapFactory().getMap(this.forcedReturnMap);
    }

    public final void setForcedReturnMap(int map) {
        this.forcedReturnMap = map;
    }

    public final float getRecoveryRate() {
        return this.recoveryRate;
    }

    public final void setRecoveryRate(float recoveryRate) {
        this.recoveryRate = recoveryRate;
    }

    public final int getFieldLimit() {
        return this.fieldLimit;
    }

    public final void setFieldLimit(int fieldLimit) {
        this.fieldLimit = fieldLimit;
    }

    public final void setCreateMobInterval(short createMobInterval) {
        this.createMobInterval = createMobInterval;
    }

    public final void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public final void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public final String getMapName() {
        return this.mapName;
    }

    public final String getStreetName() {
        return this.streetName;
    }

    public final void setFirstUserEnter(String onFirstUserEnter2) {
        this.onFirstUserEnter = onFirstUserEnter2;
    }

    public final void setUserEnter(String onUserEnter2) {
        this.onUserEnter = onUserEnter2;
    }

    public final boolean hasClock() {
        return this.clock;
    }

    public final void setClock(boolean hasClock) {
        this.clock = hasClock;
    }

    public final boolean isTown() {
        return this.town;
    }

    public final void setTown(boolean town) {
        this.town = town;
    }

    public final boolean allowPersonalShop() {
        return this.personalShop;
    }

    public final void setPersonalShop(boolean personalShop) {
        this.personalShop = personalShop;
    }

    public final void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public final void setEverlast(boolean everlast) {
        this.everlast = everlast;
    }

    public final boolean getEverlast() {
        return this.everlast;
    }

    public final int getHPDec() {
        return this.decHP;
    }

    public final void setHPDec(int delta) {
        if (delta > 0 || this.mapid == 749040100) {
            this.lastHurtTime = System.currentTimeMillis();
        }
        this.decHP = (short)delta;
    }

    public final int getHPDecInterval() {
        return this.decHPInterval;
    }

    public final void setHPDecInterval(int delta) {
        this.decHPInterval = delta;
    }

    public final int getHPDecProtect() {
        return this.protectItem;
    }

    public final void setHPDecProtect(int delta) {
        this.protectItem = delta;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final int getCurrentPartyId() {
        this.charactersLock.readLock().lock();
        try {
            for (MapleCharacter chr : this.characters) {
                if (chr.getPartyId() == -1) continue;
                int n = chr.getPartyId();
                return n;
            }
        }
        finally {
            this.charactersLock.readLock().unlock();
        }
        return -1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void addMapObject(MapleMapObject mapobject) {
        int newOid;
        this.runningOidLock.lock();
        try {
            newOid = ++this.runningOid;
        }
        finally {
            this.runningOidLock.unlock();
        }
        mapobject.setObjectId(newOid);
        this.mapobjectlocks.get((Object)mapobject.getType()).writeLock().lock();
        try {
            this.mapobjects.get((Object)mapobject.getType()).put(newOid, mapobject);
        }
        finally {
            this.mapobjectlocks.get((Object)mapobject.getType()).writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void spawnAndAddRangedMapObject(MapleMapObject mapobject, DelayedPacketCreation packetbakery, SpawnCondition condition) {
        this.addMapObject(mapobject);
        this.charactersLock.readLock().lock();
        try {
            for (MapleCharacter chr : this.characters) {
                if (condition != null && !condition.canSpawn(chr) || chr.isClone() || !(chr.getPosition().distanceSq(mapobject.getPosition()) <= (double)GameConstants.maxViewRangeSq())) continue;
                packetbakery.sendPackets(chr.getClient());
                chr.addVisibleMapObject(mapobject);
            }
        }
        finally {
            this.charactersLock.readLock().unlock();
        }
    }

    public final void removeMapObject(MapleMapObject obj) {
        this.mapobjectlocks.get((Object)obj.getType()).writeLock().lock();
        try {
            this.mapobjects.get((Object)obj.getType()).remove(obj.getObjectId());
        }
        finally {
            this.mapobjectlocks.get((Object)obj.getType()).writeLock().unlock();
        }
    }

    public final Point calcPointBelow(Point initial) {
        MapleFoothold fh = this.footholds.findBelow(initial);
        if (fh == null) {
            return null;
        }
        int dropY = fh.getY1();
        if (!fh.isWall() && fh.getY1() != fh.getY2()) {
            double s1 = Math.abs(fh.getY2() - fh.getY1());
            double s2 = Math.abs(fh.getX2() - fh.getX1());
            dropY = fh.getY2() < fh.getY1() ? fh.getY1() - (int)(Math.cos(Math.atan(s2 / s1)) * ((double)Math.abs(initial.x - fh.getX1()) / Math.cos(Math.atan(s1 / s2)))) : fh.getY1() + (int)(Math.cos(Math.atan(s2 / s1)) * ((double)Math.abs(initial.x - fh.getX1()) / Math.cos(Math.atan(s1 / s2))));
        }
        return new Point(initial.x, dropY);
    }

    public final Point calcDropPos(Point initial, Point fallback) {
        Point ret = this.calcPointBelow(new Point(initial.x, initial.y - 50));
        if (ret == null) {
            return fallback;
        }
        return ret;
    }

    private void dropFromMonster(MapleCharacter chr, MapleMonster mob2) {
        Item idrop;
        if (mob2 == null || chr == null || ChannelServer.getInstance(this.channel) == null || this.dropsDisabled || mob2.dropsDisabled() || chr.getPyramidSubway() != null) {
            return;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        byte droptype = (byte)(mob2.getStats().isExplosiveReward() ? 3 : (mob2.getStats().isFfaLoot() ? 2 : (chr.getParty() != null ? 1 : 0)));
        int mobpos = mob2.getPosition().x;
        int cmServerrate = ChannelServer.getInstance(this.channel).getMesoRate();
        int chServerrate = ChannelServer.getInstance(this.channel).getDropRate();
        int caServerrate = ChannelServer.getInstance(this.channel).getCashRate();
        int d = 1;
        Point pos = new Point(0, mob2.getPosition().y);
        double showdown = 100.0;
        MonsterStatusEffect mse = mob2.getBuff(MonsterStatus.SHOWDOWN);
        if (mse != null) {
            showdown += (double)mse.getX().intValue();
        }
        MapleMonsterInformationProvider mi = MapleMonsterInformationProvider.getInstance();
        List<MonsterDropEntry> dropEntry = mi.retrieveDrop(mob2.getId());
        Collections.shuffle(dropEntry);
        for (MonsterDropEntry de : dropEntry) {
            if (de.itemId == mob2.getStolen() || Randomizer.nextInt(999999) >= (int)((double)(de.chance * chServerrate * chr.getDropMod()) * (chr.getStat().dropBuff / 100.0) * (showdown / 100.0))) continue;
            pos.x = droptype == 3 ? mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2))) : mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
            if (de.itemId == 0) {
                int mesos = Randomizer.nextInt(1 + Math.abs(de.Maximum - de.Minimum)) + de.Minimum;
                if (mesos > 0) {
                    this.spawnMobMesoDrop((int)((double)mesos * (chr.getStat().mesoBuff / 100.0) * (double)chr.getDropMod() * (double)cmServerrate), this.calcDropPos(pos, mob2.getPosition()), mob2, chr, false, droptype);
                }
            } else {
                if (GameConstants.getInventoryType(de.itemId) == MapleInventoryType.EQUIP) {
                    idrop = ii.randomizeStats((Equip)ii.getEquipById(de.itemId));
                } else {
                    int range = Math.abs(de.Maximum - de.Minimum);
                    idrop = new Item(de.itemId, (short)0, (short)(de.Maximum != 1 ? Randomizer.nextInt(range <= 0 ? 1 : range) + de.Minimum : 1), (byte)0);
                }
                this.spawnMobDrop(idrop, this.calcDropPos(pos, mob2.getPosition()), mob2, chr, droptype, de.questid);
            }
            d = (byte)(d + 1);
        }
        ArrayList<MonsterGlobalDropEntry> globalEntry = new ArrayList<MonsterGlobalDropEntry>(mi.getGlobalDrop());
        Collections.shuffle(globalEntry);
        int cashz = (mob2.getStats().isBoss() && mob2.getStats().getHPDisplayType() == 0 ? 20 : 1) * caServerrate;
        int cashModifier = (int)(mob2.getStats().isBoss() ? 0L : (long)(mob2.getMobExp() / 1000) + mob2.getMobMaxHp() / 10000L);
        for (MonsterGlobalDropEntry de : globalEntry) {
            if (Randomizer.nextInt(999999) >= de.chance || !(de.continent < 0 || de.continent < 10 && this.mapid / 100000000 == de.continent || de.continent < 100 && this.mapid / 10000000 == de.continent) && (de.continent >= 1000 || this.mapid / 1000000 != de.continent)) continue;
            pos.x = droptype == 3 ? mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2))) : mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
            if (de.itemId == 0) {
                chr.modifyCSPoints(1, (int)((double)(Randomizer.nextInt(cashz) + cashz + cashModifier) * (chr.getStat().cashBuff / 100.0) * (double)chr.getCashMod()), true);
                continue;
            }
            if (this.gDropsDisabled) continue;
            idrop = GameConstants.getInventoryType(de.itemId) == MapleInventoryType.EQUIP ? ii.randomizeStats((Equip)ii.getEquipById(de.itemId)) : new Item(de.itemId, (short)0, (short)(de.Maximum != 1 ? Randomizer.nextInt(de.Maximum - de.Minimum) + de.Minimum : 1), (byte)0);
            this.spawnMobDrop(idrop, this.calcDropPos(pos, mob2.getPosition()), mob2, chr, de.onlySelf ? (byte)0 : droptype, de.questid);
            d = (byte)(d + 1);
        }
    }

    public void removeMonster(MapleMonster monster) {
        this.spawnedMonstersOnMap.decrementAndGet();
        this.broadcastMessage(MobPacket.killMonster(monster.getObjectId(), 0));
        this.removeMapObject(monster);
    }

    private void killMonster(MapleMonster monster) {
        this.spawnedMonstersOnMap.decrementAndGet();
        monster.setHp(0L);
        monster.spawnRevives(this);
        this.broadcastMessage(MobPacket.killMonster(monster.getObjectId(), 1));
        this.removeMapObject(monster);
    }

    public final void killMonster(MapleMonster monster, MapleCharacter chr, boolean withDrops, boolean second, byte animation) {
        this.killMonster(monster, chr, withDrops, second, animation, 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void killMonster(final MapleMonster monster, final MapleCharacter chr, boolean withDrops, boolean second, byte animation, int lastSkill) {
        MapleMonster mons;
        if (!(monster.getId() != 8810122 && monster.getId() != 8810018 || second)) {
            Timer.MapTimer.getInstance().schedule(new Runnable(){

                @Override
                public void run() {
                    MapleMap.this.killMonster(monster, chr, true, true, (byte)1);
                    MapleMap.this.killAllMonsters(true);
                }
            }, 3000L);
            return;
        }
        if (monster.getId() == 8820014) {
            this.killMonster(8820000);
        } else if (monster.getId() == 9300166) {
            animation = (byte)4;
        }
        this.spawnedMonstersOnMap.decrementAndGet();
        this.removeMapObject(monster);
        int dropOwner = monster.killBy(chr, lastSkill);
        this.broadcastMessage(MobPacket.killMonster(monster.getObjectId(), animation));
        if (monster.getBuffToGive() > -1) {
            int buffid = monster.getBuffToGive();
            MapleStatEffect buff = MapleItemInformationProvider.getInstance().getItemEffect(buffid);
            this.charactersLock.readLock().lock();
            try {
                for (MapleCharacter mc : this.characters) {
                    if (!mc.isAlive()) continue;
                    buff.applyTo(mc);
                    switch (monster.getId()) {
                        case 8810018: 
                        case 8810122: 
                        case 8820001: {
                            mc.getClient().getSession().write((Object)MaplePacketCreator.showOwnBuffEffect(buffid, 11));
                            this.broadcastMessage(mc, MaplePacketCreator.showBuffeffect(mc.getId(), buffid, 11), false);
                        }
                    }
                }
            }
            finally {
                this.charactersLock.readLock().unlock();
            }
        }
        int mobid = monster.getId();
        SpeedRunType type = SpeedRunType.NULL;
        MapleSquad sqd = this.getSquadByMap();
        if (mobid == 8810018 && this.mapid == 240060200) {
            World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "\u7d93\u904e\u7121\u6578\u6b21\u7684\u6311\u6230\uff0c" + chr.getName() + "\u6240\u5e36\u9818\u7684\u968a\u4f0d\u7d42\u65bc\u64ca\u7834\u4e86\u95c7\u9ed1\u9f8d\u738b\u7684\u9060\u5f81\u968a\uff01\u4f60\u5011\u624d\u662f\u9f8d\u4e4b\u6797\u7684\u771f\u6b63\u82f1\u96c4~").getBytes());
            FileoutputUtil.log("Logs/Log_Horntail.rtf", this.MapDebug_Log());
            if (this.speedRunStart > 0L) {
                type = SpeedRunType.Horntail;
            }
            if (sqd != null) {
                this.doShrine(true);
            }
        } else if (mobid == 8810122 && this.mapid == 240060201) {
            World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "\u7d93\u904e\u7121\u6578\u6b21\u7684\u6311\u6230\uff0c" + chr.getName() + "\u6240\u5e36\u9818\u7684\u968a\u4f0d\u6700\u7d42\u64ca\u7834\u4e86\u6df7\u5c6f\u95c7\u9ed1\u9f8d\u738b\u7684\u9060\u5f81\u968a\uff01\u4f60\u5011\u624d\u662f\u9f8d\u4e4b\u6797\u7684\u771f\u6b63\u82f1\u96c4~").getBytes());
            FileoutputUtil.log("Logs/Log_Horntail.rtf", this.MapDebug_Log());
            if (this.speedRunStart > 0L) {
                type = SpeedRunType.ChaosHT;
            }
            if (sqd != null) {
                this.doShrine(true);
            }
        } else if (mobid == 8500002 && this.mapid == 220080001) {
            if (this.speedRunStart > 0L) {
                type = SpeedRunType.Papulatus;
            }
        } else if (mobid == 9400266 && this.mapid == 802000111) {
            if (this.speedRunStart > 0L) {
                type = SpeedRunType.Nameless_Magic_Monster;
            }
            if (sqd != null) {
                this.doShrine(true);
            }
        } else if (mobid == 9400265 && this.mapid == 802000211) {
            if (this.speedRunStart > 0L) {
                type = SpeedRunType.Vergamot;
            }
            if (sqd != null) {
                this.doShrine(true);
            }
        } else if (mobid == 9400270 && this.mapid == 802000411) {
            if (this.speedRunStart > 0L) {
                type = SpeedRunType.Dunas;
            }
            if (sqd != null) {
                this.doShrine(true);
            }
        } else if (mobid == 9400273 && this.mapid == 802000611) {
            if (this.speedRunStart > 0L) {
                type = SpeedRunType.Nibergen;
            }
            if (sqd != null) {
                this.doShrine(true);
            }
        } else if (mobid == 9400294 && this.mapid == 802000711) {
            if (this.speedRunStart > 0L) {
                type = SpeedRunType.Dunas_2;
            }
            if (sqd != null) {
                this.doShrine(true);
            }
        } else if (mobid == 9400296 && this.mapid == 802000803) {
            if (this.speedRunStart > 0L) {
                type = SpeedRunType.Core_Blaze;
            }
            if (sqd != null) {
                this.doShrine(true);
            }
        } else if (mobid == 9400289 && this.mapid == 802000821) {
            if (this.speedRunStart > 0L) {
                type = SpeedRunType.Aufhaven;
            }
            if (sqd != null) {
                this.doShrine(true);
            }
        } else if ((mobid == 9420549 || mobid == 9420544) && this.mapid == 551030200) {
            if (this.speedRunStart > 0L) {
                type = mobid == 9420549 ? SpeedRunType.Scarlion : SpeedRunType.Targa;
            }
        } else if (mobid == 8820001 && this.mapid == 270050100) {
            World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, chr.getName() + " \u7d93\u904e\u5e36\u9818\u7684\u968a\u4f0d\u7d93\u904e\u7121\u6578\u6b21\u7684\u6311\u6230\uff0c\u7d42\u65bc\u64ca\u7834\u4e86\u6642\u9593\u7684\u5bf5\u5152\uff0d\u76ae\u5361\u4e18\u7684\u9060\u5f81\u968a\uff01\u4f60\u5011\u624d\u662f\u6642\u9593\u795e\u6bbf\u7684\u771f\u6b63\u82f1\u96c4~").getBytes());
            if (this.speedRunStart > 0L) {
                type = SpeedRunType.Pink_Bean;
            }
            if (sqd != null) {
                this.doShrine(true);
            }
            FileoutputUtil.log("Logs/Log_Pinkbean.rtf", this.MapDebug_Log());
        } else if (mobid == 8800002 && this.mapid == 280030000) {
            FileoutputUtil.log("Logs/Log_Zakum.rtf", this.MapDebug_Log());
            if (this.speedRunStart > 0L) {
                type = SpeedRunType.Zakum;
            }
            if (sqd != null) {
                this.doShrine(true);
            }
        } else if (mobid == 8800102 && this.mapid == 280030001) {
            FileoutputUtil.log("Logs/Log_Zakum.rtf", this.MapDebug_Log());
            if (this.speedRunStart > 0L) {
                type = SpeedRunType.Chaos_Zakum;
            }
            if (sqd != null) {
                this.doShrine(true);
            }
        } else if (mobid >= 8800003 && mobid <= 8800010) {
            boolean makeZakReal = true;
            List<MapleMonster> list = this.getAllMonstersThreadsafe();
            for (MapleMonster mapleMonster : list) {
                if (mapleMonster.getId() < 8800003 || mapleMonster.getId() > 8800010) continue;
                makeZakReal = false;
                break;
            }
            if (makeZakReal) {
                for (MapleMapObject mapleMapObject : list) {
                    MapleMonster mons3 = (MapleMonster)mapleMapObject;
                    if (mons3.getId() != 8800000) continue;
                    Point pos = mons3.getPosition();
                    this.killAllMonsters(true);
                    this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8800000), pos);
                    break;
                }
            }
        } else if (mobid >= 8800103 && mobid <= 8800110) {
            boolean makeZakReal = true;
            List<MapleMonster> list = this.getAllMonstersThreadsafe();
            for (MapleMonster mapleMonster : list) {
                if (mapleMonster.getId() < 8800103 || mapleMonster.getId() > 8800110) continue;
                makeZakReal = false;
                break;
            }
            if (makeZakReal) {
                for (MapleMonster mapleMonster : list) {
                    if (mapleMonster.getId() != 8800100) continue;
                    Point pos = mapleMonster.getPosition();
                    this.killAllMonsters(true);
                    this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8800100), pos);
                    break;
                }
            }
        }
        if (type != SpeedRunType.NULL && this.speedRunStart > 0L && this.speedRunLeader.length() > 0) {
            long endTime = System.currentTimeMillis();
            String time = StringUtil.getReadableMillis(this.speedRunStart, endTime);
            this.broadcastMessage(MaplePacketCreator.serverNotice(5, this.speedRunLeader + "'\u9060\u5f81\u968a\u82b1\u4e86 " + time + " \u6642\u9593\u6253\u6557\u4e86 " + (Object)((Object)type) + "!"));
            this.getRankAndAdd(this.speedRunLeader, time, type, endTime - this.speedRunStart, sqd == null ? null : sqd.getMembers());
            this.endSpeedRun();
        }
        if (mobid == 8820008) {
            for (MapleMapObject mapleMapObject : this.getAllMonstersThreadsafe()) {
                mons = (MapleMonster)mapleMapObject;
                if (mons.getLinkOid() == monster.getObjectId()) continue;
                this.killMonster(mons, chr, false, false, animation);
            }
        } else if (mobid >= 8820010 && mobid <= 8820014) {
            for (MapleMapObject mapleMapObject : this.getAllMonstersThreadsafe()) {
                mons = (MapleMonster)mapleMapObject;
                if (mons.getId() == 8820000 || mons.getObjectId() == monster.getObjectId() || mons.getLinkOid() == monster.getObjectId()) continue;
                this.killMonster(mons, chr, false, false, animation);
            }
        }
        if (withDrops) {
            MapleCharacter drop = null;
            if (dropOwner <= 0) {
                drop = chr;
            } else {
                drop = this.getCharacterById(dropOwner);
                if (drop == null) {
                    drop = chr;
                }
            }
            this.dropFromMonster(drop, monster);
        }
    }

    public List<MapleReactor> getAllReactor() {
        return this.getAllReactorsThreadsafe();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MapleReactor> getAllReactorsThreadsafe() {
        ArrayList<MapleReactor> ret = new ArrayList<MapleReactor>();
        this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject mmo : this.mapobjects.get((Object)MapleMapObjectType.REACTOR).values()) {
                ret.add((MapleReactor)mmo);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().unlock();
        }
        return ret;
    }

    public List<MapleMapObject> getAllDoor() {
        return this.getAllDoorsThreadsafe();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MapleMapObject> getAllDoorsThreadsafe() {
        ArrayList<MapleMapObject> ret = new ArrayList<MapleMapObject>();
        this.mapobjectlocks.get((Object)MapleMapObjectType.DOOR).readLock().lock();
        try {
            for (MapleMapObject mmo : this.mapobjects.get((Object)MapleMapObjectType.DOOR).values()) {
                ret.add(mmo);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.DOOR).readLock().unlock();
        }
        return ret;
    }

    public List<MapleMapObject> getAllMerchant() {
        return this.getAllHiredMerchantsThreadsafe();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MapleMapObject> getAllHiredMerchantsThreadsafe() {
        ArrayList<MapleMapObject> ret = new ArrayList<MapleMapObject>();
        this.mapobjectlocks.get((Object)MapleMapObjectType.HIRED_MERCHANT).readLock().lock();
        try {
            for (MapleMapObject mmo : this.mapobjects.get((Object)MapleMapObjectType.HIRED_MERCHANT).values()) {
                ret.add(mmo);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.HIRED_MERCHANT).readLock().unlock();
        }
        return ret;
    }

    public List<MapleMonster> getAllMonster() {
        return this.getAllMonstersThreadsafe();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MapleMonster> getAllMonstersThreadsafe() {
        ArrayList<MapleMonster> ret = new ArrayList<MapleMonster>();
        this.mapobjectlocks.get((Object)MapleMapObjectType.MONSTER).readLock().lock();
        try {
            for (MapleMapObject mmo : this.mapobjects.get((Object)MapleMapObjectType.MONSTER).values()) {
                ret.add((MapleMonster)mmo);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.MONSTER).readLock().unlock();
        }
        return ret;
    }

    public final void killAllMonsters(boolean animate) {
        for (MapleMapObject mapleMapObject : this.getAllMonstersThreadsafe()) {
            MapleMonster monster = (MapleMonster)mapleMapObject;
            this.spawnedMonstersOnMap.decrementAndGet();
            monster.setHp(0L);
            this.broadcastMessage(MobPacket.killMonster(monster.getObjectId(), animate ? 1 : 0));
            this.removeMapObject(monster);
            monster.killed();
        }
    }

    public final void killMonster(int monsId) {
        for (MapleMapObject mapleMapObject : this.getAllMonstersThreadsafe()) {
            if (((MapleMonster)mapleMapObject).getId() != monsId) continue;
            this.spawnedMonstersOnMap.decrementAndGet();
            this.removeMapObject(mapleMapObject);
            this.broadcastMessage(MobPacket.killMonster(mapleMapObject.getObjectId(), 1));
            break;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String MapDebug_Log() {
        StringBuilder sb = new StringBuilder("Defeat time : ");
        sb.append(FileoutputUtil.CurrentReadable_Time());
        sb.append(" | Mapid : ").append(this.mapid);
        this.charactersLock.readLock().lock();
        try {
            sb.append(" Users [").append(this.characters.size()).append("] | ");
            for (MapleCharacter mc : this.characters) {
                sb.append(mc.getName()).append(", ");
            }
        }
        finally {
            this.charactersLock.readLock().unlock();
        }
        return sb.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void limitReactor(int rid, int num) {
        ArrayList<MapleReactor> toDestroy = new ArrayList<MapleReactor>();
        LinkedHashMap<Integer, Integer> contained = new LinkedHashMap<Integer, Integer>();
        this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : this.mapobjects.get((Object)MapleMapObjectType.REACTOR).values()) {
                MapleReactor mr = (MapleReactor)obj;
                if (contained.containsKey(mr.getReactorId())) {
                    if ((Integer)contained.get(mr.getReactorId()) >= num) {
                        toDestroy.add(mr);
                        continue;
                    }
                    contained.put(mr.getReactorId(), (Integer)contained.get(mr.getReactorId()) + 1);
                    continue;
                }
                contained.put(mr.getReactorId(), 1);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().unlock();
        }
        for (MapleReactor mr : toDestroy) {
            this.destroyReactor(mr.getObjectId());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void destroyReactors(int first, int last) {
        ArrayList<MapleReactor> toDestroy = new ArrayList<MapleReactor>();
        this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : this.mapobjects.get((Object)MapleMapObjectType.REACTOR).values()) {
                MapleReactor mr = (MapleReactor)obj;
                if (mr.getReactorId() < first || mr.getReactorId() > last) continue;
                toDestroy.add(mr);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().unlock();
        }
        for (MapleReactor mr : toDestroy) {
            this.destroyReactor(mr.getObjectId());
        }
    }

    public final void destroyReactor(int oid) {
        final MapleReactor reactor = this.getReactorByOid(oid);
        this.broadcastMessage(MaplePacketCreator.destroyReactor(reactor));
        reactor.setAlive(false);
        this.removeMapObject(reactor);
        reactor.setTimerActive(false);
        if (reactor.getDelay() > 0) {
            Timer.MapTimer.getInstance().schedule(new Runnable(){

                @Override
                public final void run() {
                    MapleMap.this.respawnReactor(reactor);
                }
            }, reactor.getDelay());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void reloadReactors() {
        ArrayList<MapleReactor> toSpawn = new ArrayList<MapleReactor>();
        this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : this.mapobjects.get((Object)MapleMapObjectType.REACTOR).values()) {
                MapleReactor reactor = (MapleReactor)obj;
                this.broadcastMessage(MaplePacketCreator.destroyReactor(reactor));
                reactor.setAlive(false);
                reactor.setTimerActive(false);
                toSpawn.add(reactor);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().unlock();
        }
        for (MapleReactor r : toSpawn) {
            this.removeMapObject(r);
            if (r.getReactorId() == 9980000 || r.getReactorId() == 9980001) continue;
            this.respawnReactor(r);
        }
    }

    public final void resetReactors() {
        this.setReactorState((byte)0);
    }

    public final void setReactorState() {
        this.setReactorState((byte)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void setReactorState(byte state) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : this.mapobjects.get((Object)MapleMapObjectType.REACTOR).values()) {
                ((MapleReactor)obj).forceHitReactor(state);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    public final void shuffleReactors() {
        this.shuffleReactors(0, 9999999);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void shuffleReactors(int first, int last) {
        MapleReactor mr;
        ArrayList<Point> points = new ArrayList<Point>();
        this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : this.mapobjects.get((Object)MapleMapObjectType.REACTOR).values()) {
                mr = (MapleReactor)obj;
                if (mr.getReactorId() < first || mr.getReactorId() > last) continue;
                points.add(mr.getPosition());
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().unlock();
        }
        Collections.shuffle(points);
        this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : this.mapobjects.get((Object)MapleMapObjectType.REACTOR).values()) {
                mr = (MapleReactor)obj;
                if (mr.getReactorId() < first || mr.getReactorId() > last) continue;
                mr.setPosition((Point)points.remove(points.size() - 1));
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void updateMonsterController(MapleMonster monster) {
        if (!monster.isAlive()) {
            return;
        }
        if (monster.getController() != null) {
            if (monster.getController().getMap() != this) {
                monster.getController().stopControllingMonster(monster);
            } else {
                return;
            }
        }
        int mincontrolled = -1;
        MapleCharacter newController = null;
        this.charactersLock.readLock().lock();
        try {
            for (MapleCharacter chr : this.characters) {
                if (chr.isHidden() || chr.isClone() || chr.getControlledSize() >= mincontrolled && mincontrolled != -1) continue;
                mincontrolled = chr.getControlledSize();
                newController = chr;
            }
        }
        finally {
            this.charactersLock.readLock().unlock();
        }
        if (newController != null) {
            if (monster.isFirstAttack()) {
                newController.controlMonster(monster, true);
                monster.setControllerHasAggro(true);
                monster.setControllerKnowsAboutAggro(true);
            } else {
                newController.controlMonster(monster, false);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final MapleMapObject getMapObject(int oid, MapleMapObjectType type) {
        this.mapobjectlocks.get((Object)type).readLock().lock();
        try {
            MapleMapObject mapleMapObject = this.mapobjects.get((Object)type).get(oid);
            return mapleMapObject;
        }
        finally {
            this.mapobjectlocks.get((Object)type).readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final boolean containsNPC(int npcid) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.NPC).readLock().lock();
        try {
            for (MapleMapObject obj : this.mapobjects.get((Object)MapleMapObjectType.NPC).values()) {
                MapleNPC mapleNPC = (MapleNPC)obj;
                if (mapleNPC.getId() != npcid) continue;
                boolean bl = true;
                return bl;
            }
            boolean bl = false;
            return bl;
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.NPC).readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MapleNPC getNPCById(int id) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.NPC).readLock().lock();
        try {
            for (MapleMapObject obj : this.mapobjects.get((Object)MapleMapObjectType.NPC).values()) {
                MapleNPC mapleNPC = (MapleNPC)obj;
                if (mapleNPC.getId() != id) continue;
                MapleNPC mapleNPC2 = mapleNPC;
                return mapleNPC2;
            }
            MapleNPC mapleNPC = null;
            return mapleNPC;
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.NPC).readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MapleMonster getMonsterById(int id) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.MONSTER).readLock().lock();
        try {
            MapleMonster ret = null;
            for (MapleMapObject obj : this.mapobjects.get((Object)MapleMapObjectType.MONSTER).values()) {
                MapleMonster mapleMonster = (MapleMonster)obj;
                if (mapleMonster.getId() != id) continue;
                ret = mapleMonster;
                break;
            }
            MapleMonster mapleMonster = ret;
            return mapleMonster;
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.MONSTER).readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int countMonsterById(int id) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.MONSTER).readLock().lock();
        try {
            int ret = 0;
            for (MapleMapObject obj : this.mapobjects.get((Object)MapleMapObjectType.MONSTER).values()) {
                MapleMonster mapleMonster = (MapleMonster)obj;
                if (mapleMonster.getId() != id) continue;
                ++ret;
            }
            int n = ret;
            return n;
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.MONSTER).readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MapleReactor getReactorById(int id) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().lock();
        try {
            MapleReactor ret = null;
            for (MapleMapObject obj : this.mapobjects.get((Object)MapleMapObjectType.REACTOR).values()) {
                MapleReactor mapleReactor = (MapleReactor)obj;
                if (mapleReactor.getReactorId() != id) continue;
                ret = mapleReactor;
                break;
            }
            MapleReactor mapleReactor = ret;
            return mapleReactor;
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    public final MapleMonster getMonsterByOid(int oid) {
        MapleMapObject mmo = this.getMapObject(oid, MapleMapObjectType.MONSTER);
        if (mmo == null) {
            return null;
        }
        return (MapleMonster)mmo;
    }

    public final MapleNPC getNPCByOid(int oid) {
        MapleMapObject mmo = this.getMapObject(oid, MapleMapObjectType.NPC);
        if (mmo == null) {
            return null;
        }
        return (MapleNPC)mmo;
    }

    public final MapleReactor getReactorByOid(int oid) {
        MapleMapObject mmo = this.getMapObject(oid, MapleMapObjectType.REACTOR);
        if (mmo == null) {
            return null;
        }
        return (MapleReactor)mmo;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final MapleReactor getReactorByName(String name) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : this.mapobjects.get((Object)MapleMapObjectType.REACTOR).values()) {
                MapleReactor mr = (MapleReactor)obj;
                if (!mr.getName().equalsIgnoreCase(name)) continue;
                MapleReactor mapleReactor = mr;
                return mapleReactor;
            }
            return null;
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    public final void spawnNpc(int id, Point pos) {
        MapleNPC npc = MapleLifeFactory.getNPC(id);
        npc.setPosition(pos);
        npc.setCy(pos.y);
        npc.setRx0(pos.x + 50);
        npc.setRx1(pos.x - 50);
        npc.setFh(this.getFootholds().findBelow(pos).getId());
        npc.setCustom(true);
        this.addMapObject(npc);
        this.broadcastMessage(MaplePacketCreator.spawnNPC(npc, true));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void removeNpc(int npcid) {
        this.mapobjectlocks.get((Object)MapleMapObjectType.NPC).writeLock().lock();
        try {
            Iterator<MapleMapObject> itr = this.mapobjects.get((Object)MapleMapObjectType.NPC).values().iterator();
            while (itr.hasNext()) {
                MapleNPC npc = (MapleNPC)itr.next();
                if (!npc.isCustom() || npc.getId() != npcid) continue;
                this.broadcastMessage(MaplePacketCreator.removeNPC(npc.getObjectId()));
                itr.remove();
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.NPC).writeLock().unlock();
        }
    }

    public final void spawnMonster_sSack(MapleMonster mob2, Point pos, int spawnType) {
        Point spos = this.calcPointBelow(new Point(pos.x, pos.y - 1));
        mob2.setPosition(spos);
        this.spawnMonster(mob2, spawnType);
    }

    public final void spawnMonsterOnGroundBelow(MapleMonster mob2, Point pos) {
        this.spawnMonster_sSack(mob2, pos, -2);
    }

    public final int spawnMonsterWithEffectBelow(MapleMonster mob2, Point pos, int effect) {
        Point spos = this.calcPointBelow(new Point(pos.x, pos.y - 1));
        return this.spawnMonsterWithEffect(mob2, effect, spos);
    }

    public final void spawnZakum(int x, int y) {
        int[] zakpart;
        Point pos = new Point(x, y);
        MapleMonster mainb = MapleLifeFactory.getMonster(8800000);
        Point spos = this.calcPointBelow(new Point(pos.x, pos.y - 1));
        mainb.setPosition(spos);
        mainb.setFake(true);
        this.spawnFakeMonster(mainb);
        for (int i : zakpart = new int[]{8800003, 8800004, 8800005, 8800006, 8800007, 8800008, 8800009, 8800010}) {
            MapleMonster part = MapleLifeFactory.getMonster(i);
            part.setPosition(spos);
            this.spawnMonster(part, -2);
        }
        if (this.squadSchedule != null) {
            this.cancelSquadSchedule();
            this.broadcastMessage(MaplePacketCreator.stopClock());
            this.broadcastMessage(MaplePacketCreator.getClock(36000000));
        }
    }

    public final void spawnChaosZakum(int x, int y) {
        int[] zakpart;
        Point pos = new Point(x, y);
        MapleMonster mainb = MapleLifeFactory.getMonster(8800100);
        Point spos = this.calcPointBelow(new Point(pos.x, pos.y - 1));
        mainb.setPosition(spos);
        mainb.setFake(true);
        this.spawnFakeMonster(mainb);
        for (int i : zakpart = new int[]{8800103, 8800104, 8800105, 8800106, 8800107, 8800108, 8800109, 8800110}) {
            MapleMonster part = MapleLifeFactory.getMonster(i);
            part.setPosition(spos);
            this.spawnMonster(part, -2);
        }
        if (this.squadSchedule != null) {
            this.cancelSquadSchedule();
            this.broadcastMessage(MaplePacketCreator.stopClock());
            this.broadcastMessage(MaplePacketCreator.getClock(36000000));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MapleMist> getAllMistsThreadsafe() {
        ArrayList<MapleMist> ret = new ArrayList<MapleMist>();
        this.mapobjectlocks.get((Object)MapleMapObjectType.MIST).readLock().lock();
        try {
            for (MapleMapObject mmo : this.mapobjects.get((Object)MapleMapObjectType.MIST).values()) {
                ret.add((MapleMist)mmo);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.MIST).readLock().unlock();
        }
        return ret;
    }

    public final void spawnFakeMonsterOnGroundBelow(MapleMonster mob2, Point pos) {
        Point spos = this.calcPointBelow(new Point(pos.x, pos.y - 1));
        --spos.y;
        mob2.setPosition(spos);
        this.spawnFakeMonster(mob2);
    }

    private void checkRemoveAfter(final MapleMonster monster) {
        int ra = monster.getStats().getRemoveAfter();
        if (ra > 0) {
            Timer.MapTimer.getInstance().schedule(new Runnable(){

                @Override
                public final void run() {
                    if (monster != null && monster == MapleMap.this.getMapObject(monster.getObjectId(), monster.getType())) {
                        MapleMap.this.killMonster(monster);
                    }
                }
            }, ra * 1000);
        }
    }

    public final void spawnRevives(final MapleMonster monster, final int oid) {
        monster.setMap(this);
        this.checkRemoveAfter(monster);
        monster.setLinkOid(oid);
        this.spawnAndAddRangedMapObject(monster, new DelayedPacketCreation(){

            @Override
            public final void sendPackets(MapleClient c) {
                c.getSession().write((Object)MobPacket.spawnMonster(monster, -2, 0, oid));
            }
        }, null);
        this.updateMonsterController(monster);
        this.spawnedMonstersOnMap.incrementAndGet();
    }

    public final void spawnMonster(final MapleMonster monster, final int spawnType) {
        monster.setMap(this);
        this.checkRemoveAfter(monster);
        this.spawnAndAddRangedMapObject(monster, new DelayedPacketCreation(){

            @Override
            public final void sendPackets(MapleClient c) {
                c.getSession().write((Object)MobPacket.spawnMonster(monster, spawnType, 0, 0));
            }
        }, null);
        this.updateMonsterController(monster);
        this.spawnedMonstersOnMap.incrementAndGet();
    }

    public final int spawnMonsterWithEffect(final MapleMonster monster, final int effect, Point pos) {
        try {
            monster.setMap(this);
            monster.setPosition(pos);
            this.spawnAndAddRangedMapObject(monster, new DelayedPacketCreation(){

                @Override
                public final void sendPackets(MapleClient c) {
                    c.getSession().write((Object)MobPacket.spawnMonster(monster, -2, effect, 0));
                }
            }, null);
            this.updateMonsterController(monster);
            this.spawnedMonstersOnMap.incrementAndGet();
            return monster.getObjectId();
        }
        catch (Exception e) {
            return -1;
        }
    }

    public final void spawnFakeMonster(final MapleMonster monster) {
        monster.setMap(this);
        monster.setFake(true);
        this.spawnAndAddRangedMapObject(monster, new DelayedPacketCreation(){

            @Override
            public final void sendPackets(MapleClient c) {
                c.getSession().write((Object)MobPacket.spawnMonster(monster, -2, 252, 0));
            }
        }, null);
        this.updateMonsterController(monster);
        this.spawnedMonstersOnMap.incrementAndGet();
    }

    public final void spawnReactor(final MapleReactor reactor) {
        reactor.setMap(this);
        this.spawnAndAddRangedMapObject(reactor, new DelayedPacketCreation(){

            @Override
            public final void sendPackets(MapleClient c) {
                c.getSession().write((Object)MaplePacketCreator.spawnReactor(reactor));
            }
        }, null);
    }

    private void respawnReactor(MapleReactor reactor) {
        reactor.setState((byte)0);
        reactor.setAlive(true);
        this.spawnReactor(reactor);
    }

    public final void spawnDoor(final MapleDoor door) {
        this.spawnAndAddRangedMapObject(door, new DelayedPacketCreation(){

            @Override
            public final void sendPackets(MapleClient c) {
                c.getSession().write((Object)MaplePacketCreator.spawnDoor(door.getOwner().getId(), door.getTargetPosition(), false));
                if (door.getOwner().getParty() != null && (door.getOwner() == c.getPlayer() || door.getOwner().getParty().containsMembers(new MaplePartyCharacter(c.getPlayer())))) {
                    c.getSession().write((Object)MaplePacketCreator.partyPortal(door.getTown().getId(), door.getTarget().getId(), door.getSkill(), door.getTargetPosition()));
                }
                c.getSession().write((Object)MaplePacketCreator.spawnPortal(door.getTown().getId(), door.getTarget().getId(), door.getSkill(), door.getTargetPosition()));
                c.getSession().write((Object)MaplePacketCreator.enableActions());
            }
        }, new SpawnCondition(){

            @Override
            public final boolean canSpawn(MapleCharacter chr) {
                return door.getTarget().getId() == chr.getMapId() || door.getOwnerId() == chr.getId() || door.getOwner() != null && door.getOwner().getParty() != null && door.getOwner().getParty().getMemberById(chr.getId()) != null;
            }
        });
    }

    public final void spawnSummon(final MapleSummon summon) {
        summon.updateMap(this);
        this.spawnAndAddRangedMapObject(summon, new DelayedPacketCreation(){

            @Override
            public void sendPackets(MapleClient c) {
                if (summon != null) {
                    c.getSession().write((Object)MaplePacketCreator.spawnSummon(summon, true));
                }
            }
        }, null);
    }

    public final void spawnDragon(final MapleDragon summon) {
        this.spawnAndAddRangedMapObject(summon, new DelayedPacketCreation(){

            @Override
            public void sendPackets(MapleClient c) {
                c.getSession().write((Object)MaplePacketCreator.spawnDragon(summon));
            }
        }, null);
    }

    public final void spawnMist(final MapleMist mist, final int duration, boolean fake) {
        ScheduledFuture<?> poisonSchedule;
        this.spawnAndAddRangedMapObject(mist, new DelayedPacketCreation(){

            @Override
            public void sendPackets(MapleClient c) {
                mist.sendSpawnData(c);
            }
        }, null);
        Timer.MapTimer tMan = Timer.MapTimer.getInstance();
        switch (mist.isPoisonMist()) {
            case 1: {
                final MapleCharacter owner = this.getCharacterById(mist.getOwnerId());
                poisonSchedule = tMan.register(new Runnable(){

                    @Override
                    public void run() {
                        int d = 0;
                        for (MapleMapObject mo : MapleMap.this.getMapObjectsInRect(mist.getBox(), Collections.singletonList(MapleMapObjectType.MONSTER))) {
                            if (!mist.makeChanceResult() || ((MapleMonster)mo).isBuffed(MonsterStatus.POISON) || ((MapleMonster)mo).getHp() <= 1L) continue;
                            ((MapleMonster)mo).applyStatus(owner, new MonsterStatusEffect(MonsterStatus.POISON, 1, mist.getSourceSkill().getId(), null, false), true, (long)duration, false, d);
                            d += 137;
                        }
                    }
                }, 2000L, 2500L);
                break;
            }
            case 2: {
                poisonSchedule = tMan.register(new Runnable(){

                    @Override
                    public void run() {
                        for (MapleMapObject mo : MapleMap.this.getMapObjectsInRect(mist.getBox(), Collections.singletonList(MapleMapObjectType.PLAYER))) {
                            if (!mist.makeChanceResult()) continue;
                            MapleCharacter chr = (MapleCharacter)mo;
                            chr.addMP((int)((double)mist.getSource().getX() * ((double)chr.getStat().getMaxMp() / 100.0)));
                        }
                    }
                }, 2000L, 2500L);
                break;
            }
            default: {
                poisonSchedule = null;
            }
        }
        tMan.schedule(new Runnable(){

            @Override
            public void run() {
                MapleMap.this.broadcastMessage(MaplePacketCreator.removeMist(mist.getObjectId(), false));
                MapleMap.this.removeMapObject(mist);
                if (poisonSchedule != null) {
                    poisonSchedule.cancel(false);
                }
            }
        }, duration);
    }

    public final void disappearingItemDrop(MapleMapObject dropper, MapleCharacter owner, IItem item, Point pos) {
        Point droppos = this.calcDropPos(pos, pos);
        MapleMapItem drop = new MapleMapItem(item, droppos, dropper, owner, (byte)1, false);
        this.broadcastMessage(MaplePacketCreator.dropItemFromMapObject(drop, dropper.getPosition(), droppos, (byte)3), drop.getPosition());
    }

    public final void spawnMesoDrop(int meso, Point position, final MapleMapObject dropper, MapleCharacter owner, boolean playerDrop, byte droptype) {
        final Point droppos = this.calcDropPos(position, position);
        final MapleMapItem mdrop = new MapleMapItem(meso, droppos, dropper, owner, droptype, playerDrop);
        this.spawnAndAddRangedMapObject(mdrop, new DelayedPacketCreation(){

            @Override
            public void sendPackets(MapleClient c) {
                c.getSession().write((Object)MaplePacketCreator.dropItemFromMapObject(mdrop, dropper.getPosition(), droppos, (byte)1));
            }
        }, null);
        if (!this.everlast) {
            mdrop.registerExpire(120000L);
            if (droptype == 0 || droptype == 1) {
                mdrop.registerFFA(30000L);
            }
        }
    }

    public final void spawnMobMesoDrop(int meso, final Point position, final MapleMapObject dropper, MapleCharacter owner, boolean playerDrop, byte droptype) {
        final MapleMapItem mdrop = new MapleMapItem(meso, position, dropper, owner, droptype, playerDrop);
        this.spawnAndAddRangedMapObject(mdrop, new DelayedPacketCreation(){

            @Override
            public void sendPackets(MapleClient c) {
                c.getSession().write((Object)MaplePacketCreator.dropItemFromMapObject(mdrop, dropper.getPosition(), position, (byte)1));
            }
        }, null);
        mdrop.registerExpire(120000L);
        if (droptype == 0 || droptype == 1) {
            mdrop.registerFFA(30000L);
        }
    }

    public final void spawnMobDrop(IItem idrop, final Point dropPos, final MapleMonster mob2, MapleCharacter chr, byte droptype, final short questid) {
        final MapleMapItem mdrop = new MapleMapItem(idrop, dropPos, mob2, chr, droptype, false, questid);
        this.spawnAndAddRangedMapObject(mdrop, new DelayedPacketCreation(){

            @Override
            public void sendPackets(MapleClient c) {
                if (questid <= 0 || c.getPlayer().getQuestStatus(questid) == 1) {
                    c.getSession().write((Object)MaplePacketCreator.dropItemFromMapObject(mdrop, mob2.getPosition(), dropPos, (byte)1));
                }
            }
        }, null);
        mdrop.registerExpire(120000L);
        if (droptype == 0 || droptype == 1) {
            mdrop.registerFFA(30000L);
        }
        this.activateItemReactors(mdrop, chr.getClient());
    }

    public final void spawnRandDrop() {
        if (this.mapid != 910000000 || this.channel != 1) {
            return;
        }
        this.mapobjectlocks.get((Object)MapleMapObjectType.ITEM).readLock().lock();
        try {
            for (MapleMapObject o : this.mapobjects.get((Object)MapleMapObjectType.ITEM).values()) {
                if (!((MapleMapItem)o).isRandDrop()) continue;
                return;
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.ITEM).readLock().unlock();
        }
        Timer.MapTimer.getInstance().schedule(new Runnable(){

            @Override
            public void run() {
                Point pos = new Point(Randomizer.nextInt(800) + 531, -806);
                int theItem = Randomizer.nextInt(1000);
                int itemid = 0;
                itemid = theItem < 950 ? GameConstants.normalDrops[Randomizer.nextInt(GameConstants.normalDrops.length)] : (theItem < 990 ? GameConstants.rareDrops[Randomizer.nextInt(GameConstants.rareDrops.length)] : GameConstants.superDrops[Randomizer.nextInt(GameConstants.superDrops.length)]);
                MapleMap.this.spawnAutoDrop(itemid, pos);
            }
        }, 20000L);
    }

    public final void spawnAutoDrop(int itemid, final Point pos) {
        Item idrop = null;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        idrop = GameConstants.getInventoryType(itemid) == MapleInventoryType.EQUIP ? ii.randomizeStats((Equip)ii.getEquipById(itemid)) : new Item(itemid, (short)0, (short)1, (byte)0);
        final MapleMapItem mdrop = new MapleMapItem(pos, idrop);
        this.spawnAndAddRangedMapObject(mdrop, new DelayedPacketCreation(){

            @Override
            public void sendPackets(MapleClient c) {
                c.getSession().write((Object)MaplePacketCreator.dropItemFromMapObject(mdrop, pos, pos, (byte)1));
            }
        }, null);
        this.broadcastMessage(MaplePacketCreator.dropItemFromMapObject(mdrop, pos, pos, (byte)0));
        mdrop.registerExpire(120000L);
    }

    public final void spawnItemDrop(final MapleMapObject dropper, MapleCharacter owner, IItem item, Point pos, boolean ffaDrop, boolean playerDrop) {
        final Point droppos = this.calcDropPos(pos, pos);
        final MapleMapItem drop = new MapleMapItem(item, droppos, dropper, owner, (byte)2, playerDrop);
        this.spawnAndAddRangedMapObject(drop, new DelayedPacketCreation(){

            @Override
            public void sendPackets(MapleClient c) {
                c.getSession().write((Object)MaplePacketCreator.dropItemFromMapObject(drop, dropper.getPosition(), droppos, (byte)1));
            }
        }, null);
        this.broadcastMessage(MaplePacketCreator.dropItemFromMapObject(drop, dropper.getPosition(), droppos, (byte)0));
        if (!this.everlast) {
            drop.registerExpire(120000L);
            this.activateItemReactors(drop, owner.getClient());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void activateItemReactors(MapleMapItem drop, MapleClient c) {
        IItem item = drop.getItem();
        this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject o : this.mapobjects.get((Object)MapleMapObjectType.REACTOR).values()) {
                MapleReactor react = (MapleReactor)o;
                if (react.getReactorType() != 100 || !GameConstants.isCustomReactItem(react.getReactorId(), item.getItemId(), react.getReactItem().getLeft()) || react.getReactItem().getRight().intValue() != item.getQuantity() || !react.getArea().contains(drop.getPosition()) || react.isTimerActive()) continue;
                Timer.MapTimer.getInstance().schedule(new ActivateItemReactor(drop, react, c), 5000L);
                react.setTimerActive(true);
                break;
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    public int getItemsSize() {
        return this.mapobjects.get((Object)MapleMapObjectType.ITEM).size();
    }

    public List<MapleMapItem> getAllItems() {
        return this.getAllItemsThreadsafe();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MapleMapItem> getAllItemsThreadsafe() {
        ArrayList<MapleMapItem> ret = new ArrayList<MapleMapItem>();
        this.mapobjectlocks.get((Object)MapleMapObjectType.ITEM).readLock().lock();
        try {
            for (MapleMapObject mmo : this.mapobjects.get((Object)MapleMapObjectType.ITEM).values()) {
                ret.add((MapleMapItem)mmo);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.ITEM).readLock().unlock();
        }
        return ret;
    }

    public final void returnEverLastItem(MapleCharacter chr) {
        for (MapleMapObject mapleMapObject : this.getAllItemsThreadsafe()) {
            MapleMapItem item = (MapleMapItem)mapleMapObject;
            if (item.getOwner() != chr.getId()) continue;
            item.setPickedUp(true);
            this.broadcastMessage(MaplePacketCreator.removeItemFromMap(item.getObjectId(), 2, chr.getId()), item.getPosition());
            if (item.getMeso() > 0) {
                chr.gainMeso(item.getMeso(), false);
            } else {
                MapleInventoryManipulator.addFromDrop(chr.getClient(), item.getItem(), false);
            }
            this.removeMapObject(item);
        }
        this.spawnRandDrop();
    }

    public final void talkMonster(String msg, int itemId, int objectid) {
        if (itemId > 0) {
            this.startMapEffect(msg, itemId, false);
        }
        this.broadcastMessage(MobPacket.talkMonster(objectid, itemId, msg));
        this.broadcastMessage(MobPacket.removeTalkMonster(objectid));
    }

    public final void startMapEffect(String msg, int itemId) {
        this.startMapEffect(msg, itemId, false);
    }

    public final void startMapEffect(String msg, int itemId, boolean jukebox) {
        if (this.mapEffect != null) {
            return;
        }
        this.mapEffect = new MapleMapEffect(msg, itemId);
        this.mapEffect.setJukebox(jukebox);
        this.broadcastMessage(this.mapEffect.makeStartData());
        Timer.MapTimer.getInstance().schedule(new Runnable(){

            @Override
            public void run() {
                MapleMap.this.broadcastMessage(MapleMap.this.mapEffect.makeDestroyData());
                MapleMap.this.mapEffect = null;
            }
        }, jukebox ? 300000L : 30000L);
    }

    public final void startExtendedMapEffect(final String msg, final int itemId) {
        this.broadcastMessage(MaplePacketCreator.startMapEffect(msg, itemId, true));
        Timer.MapTimer.getInstance().schedule(new Runnable(){

            @Override
            public void run() {
                MapleMap.this.broadcastMessage(MaplePacketCreator.removeMapEffect());
                MapleMap.this.broadcastMessage(MaplePacketCreator.startMapEffect(msg, itemId, false));
            }
        }, 60000L);
    }

    public final void startJukebox(String msg, int itemId) {
        this.startMapEffect(msg, itemId, true);
    }

    public final void addPlayer(MapleCharacter chr) {
        MapleStatEffect stat;
        this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).writeLock().lock();
        try {
            this.mapobjects.get((Object)MapleMapObjectType.PLAYER).put(chr.getObjectId(), chr);
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.PLAYER).writeLock().unlock();
        }
        this.charactersLock.writeLock().lock();
        try {
            this.characters.add(chr);
        }
        finally {
            this.charactersLock.writeLock().unlock();
        }
        if (this.mapid == 109080000 || this.mapid == 109080001 || this.mapid == 109080002 || this.mapid == 109080003 || this.mapid == 109080010 || this.mapid == 109080011 || this.mapid == 109080012) {
            chr.setCoconutTeam(this.getAndSwitchTeam() ? 0 : 1);
        }
        if (!chr.isHidden()) {
            this.broadcastMessage(chr, MaplePacketCreator.spawnPlayerMapobject(chr), false);
            if (chr.isGM() && this.speedRunStart > 0L) {
                this.endSpeedRun();
                this.broadcastMessage(MaplePacketCreator.serverNotice(5, "The speed run has ended."));
            }
        }
        if (!chr.isClone()) {
            if (!this.onFirstUserEnter.equals("") && this.getCharactersSize() == 1) {
                MapScriptMethods.startScript_FirstUser(chr.getClient(), this.onFirstUserEnter);
            }
            this.sendObjectPlacement(chr);
            chr.getClient().getSession().write((Object)MaplePacketCreator.spawnPlayerMapobject(chr));
            if (!this.onUserEnter.equals("")) {
                MapScriptMethods.startScript_User(chr.getClient(), this.onUserEnter);
            }
            switch (this.mapid) {
                case 109030001: 
                case 109040000: 
                case 109060001: 
                case 109080000: 
                case 109080010: {
                    chr.getClient().getSession().write((Object)MaplePacketCreator.showEventInstructions());
                    break;
                }
                case 809000101: 
                case 809000201: {
                    chr.getClient().getSession().write((Object)MaplePacketCreator.showEquipEffect());
                }
            }
        }
        for (MaplePet pet : chr.getPets()) {
            if (!pet.getSummoned()) continue;
            this.broadcastMessage(chr, PetPacket.showPet(chr, pet, false, false), false);
        }
        if (chr.getParty() != null && !chr.isClone()) {
            chr.silentPartyUpdate();
            chr.getClient().getSession().write((Object)MaplePacketCreator.updateParty(chr.getClient().getChannel(), chr.getParty(), PartyOperation.SILENT_UPDATE, null));
            chr.updatePartyMemberHP();
            chr.receivePartyMemberHP();
        }
        if ((stat = chr.getStatForBuff(MapleBuffStat.SUMMON)) != null && !chr.isClone()) {
            MapleSummon summon = chr.getSummons().get(stat.getSourceId());
            summon.setPosition(chr.getPosition());
            try {
                summon.setFh(this.getFootholds().findBelow(chr.getPosition()).getId());
            }
            catch (NullPointerException e) {
                summon.setFh(0);
            }
            chr.addVisibleMapObject(summon);
        }
        if (this.mapEffect != null) {
            this.mapEffect.sendStartData(chr.getClient());
        }
        if (this.timeLimit > 0 && this.getForcedReturnMap() != null && !chr.isClone()) {
            chr.startMapTimeLimitTask(this.timeLimit, this.getForcedReturnMap());
        }
        if (chr.getBuffedValue(MapleBuffStat.MONSTER_RIDING) != null && FieldLimitType.Mount.check(this.fieldLimit)) {
            chr.cancelBuffStats(MapleBuffStat.MONSTER_RIDING);
        }
        if (!chr.isClone()) {
            if (chr.getEventInstance() != null && chr.getEventInstance().isTimerStarted() && !chr.isClone()) {
                chr.getClient().getSession().write((Object)MaplePacketCreator.getClock((int)(chr.getEventInstance().getTimeLeft() / 1000L)));
            }
            if (this.hasClock()) {
                Calendar cal = Calendar.getInstance();
                chr.getClient().getSession().write((Object)MaplePacketCreator.getClockTime(cal.get(11), cal.get(12), cal.get(13)));
            }
            if (chr.getCarnivalParty() != null && chr.getEventInstance() != null) {
                chr.getEventInstance().onMapLoad(chr);
            }
            MapleEvent.mapLoad(chr, this.channel);
            if (this.getSquadBegin() != null && this.getSquadBegin().getTimeLeft() > 0L && this.getSquadBegin().getStatus() == 1) {
                chr.getClient().getSession().write((Object)MaplePacketCreator.getClock((int)(this.getSquadBegin().getTimeLeft() / 1000L)));
            }
            if (this.mapid / 1000 != 105100 && this.mapid / 100 != 8020003 && this.mapid / 100 != 8020008) {
                MapleSquad sqd = this.getSquadByMap();
                if (!this.squadTimer && sqd != null && chr.getName().equals(sqd.getLeaderName()) && !chr.isClone()) {
                    this.doShrine(false);
                    this.squadTimer = true;
                }
            }
            if (this.getNumMonsters() > 0 && (this.mapid == 280030001 || this.mapid == 240060201 || this.mapid == 280030000 || this.mapid == 240060200 || this.mapid == 220080001 || this.mapid == 541020800 || this.mapid == 541010100)) {
                String music = "Bgm09/TimeAttack";
                switch (this.mapid) {
                    case 240060200: 
                    case 240060201: {
                        music = "Bgm14/HonTale";
                        break;
                    }
                    case 280030000: 
                    case 280030001: {
                        music = "Bgm06/FinalFight";
                        break;
                    }
                    case 200090000: 
                    case 200090010: {
                        music = "Bgm04/ArabPirate";
                    }
                }
                chr.getClient().getSession().write((Object)MaplePacketCreator.musicChange(music));
            }
            for (WeakReference<MapleCharacter> chrz : chr.getClones()) {
                if (chrz.get() == null) continue;
                ((MapleCharacter)chrz.get()).setPosition(new Point(chr.getPosition()));
                ((MapleCharacter)chrz.get()).setMap(this);
                this.addPlayer((MapleCharacter)chrz.get());
            }
            if (this.mapid == 914000000) {
                chr.getClient().getSession().write((Object)MaplePacketCreator.temporaryStats_Aran());
            } else if (this.mapid == 105100300 && chr.getLevel() >= 91) {
                chr.getClient().getSession().write((Object)MaplePacketCreator.temporaryStats_Balrog(chr));
            } else if (this.mapid == 140090000 || this.mapid == 105100301 || this.mapid == 105100401 || this.mapid == 105100100) {
                chr.getClient().getSession().write((Object)MaplePacketCreator.temporaryStats_Reset());
            }
        }
        if (GameConstants.isEvan(chr.getJob()) && chr.getJob() >= 2200 && chr.getBuffedValue(MapleBuffStat.MONSTER_RIDING) == null) {
            if (chr.getDragon() == null) {
                chr.makeDragon();
            }
            this.spawnDragon(chr.getDragon());
            if (!chr.isClone()) {
                this.updateMapObjectVisibility(chr, chr.getDragon());
            }
        }
        if (this.mapid == 10000 && chr.getJob() == 0 || this.mapid == 130030000 && chr.getJob() == 1000 || this.mapid == 914000000 && chr.getJob() == 2000 || this.mapid != 900010000 || chr.getJob() == 2001) {
            // empty if block
        }
        if (this.permanentWeather > 0) {
            chr.getClient().getSession().write((Object)MaplePacketCreator.startMapEffect("", this.permanentWeather, false));
        }
        if (this.getPlatforms().size() > 0) {
            chr.getClient().getSession().write((Object)MaplePacketCreator.getMovingPlatforms(this));
        }
        if (this.environment.size() > 0) {
            chr.getClient().getSession().write((Object)MaplePacketCreator.getUpdateEnvironment(this));
        }
        if (this.isTown()) {
            chr.cancelEffectFromBuffStat(MapleBuffStat.RAINING_MINES);
        }
    }

    public int getNumItems() {
        this.mapobjectlocks.get((Object)MapleMapObjectType.ITEM).readLock().lock();
        try {
            int n = this.mapobjects.get((Object)MapleMapObjectType.ITEM).size();
            return n;
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.ITEM).readLock().unlock();
        }
    }

    public int getNumMonsters() {
        this.mapobjectlocks.get((Object)MapleMapObjectType.MONSTER).readLock().lock();
        try {
            int n = this.mapobjects.get((Object)MapleMapObjectType.MONSTER).size();
            return n;
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.MONSTER).readLock().unlock();
        }
    }

    public void doShrine(final boolean spawned) {
        if (this.squadSchedule != null) {
            this.cancelSquadSchedule();
        }
        final int mode = this.mapid == 280030000 ? 1 : (this.mapid == 280030001 ? 2 : (this.mapid == 240060200 || this.mapid == 240060201 ? 3 : 0));
        MapleSquad sqd = this.getSquadByMap();
        EventManager em = this.getEMByMap();
        if (sqd != null && em != null && this.getCharactersSize() > 0) {
            Runnable run;
            final String leaderName = sqd.getLeaderName();
            final String state = em.getProperty("state");
            MapleMap returnMapa = this.getForcedReturnMap();
            if (returnMapa == null || returnMapa.getId() == this.mapid) {
                returnMapa = this.getReturnMap();
            }
            if (mode == 1) {
                this.broadcastMessage(MaplePacketCreator.showZakumShrine(spawned, 5));
            } else if (mode == 2) {
                this.broadcastMessage(MaplePacketCreator.showChaosZakumShrine(spawned, 5));
            } else if (mode == 3) {
                this.broadcastMessage(MaplePacketCreator.showChaosHorntailShrine(spawned, 5));
            } else {
                this.broadcastMessage(MaplePacketCreator.showHorntailShrine(spawned, 5));
            }
            if (mode == 1 || spawned) {
                this.broadcastMessage(MaplePacketCreator.getClock(300));
            }
            final MapleMap returnMapz = returnMapa;
            if (!spawned) {
                final List<MapleMonster> monsterz = this.getAllMonstersThreadsafe();
                final ArrayList<Integer> monsteridz = new ArrayList<Integer>();
                for (MapleMapObject mapleMapObject : monsterz) {
                    monsteridz.add(mapleMapObject.getObjectId());
                }
                run = new Runnable(){

                    @Override
                    public void run() {
                        MapleSquad sqnow = MapleMap.this.getSquadByMap();
                        if (MapleMap.this.getCharactersSize() > 0 && MapleMap.this.getNumMonsters() == monsterz.size() && sqnow != null && sqnow.getStatus() == 2 && sqnow.getLeaderName().equals(leaderName) && MapleMap.this.getEMByMap().getProperty("state").equals(state)) {
                            boolean passed = monsterz.isEmpty();
                            for (MapleMapObject mapleMapObject : MapleMap.this.getAllMonstersThreadsafe()) {
                                Iterator iterator = monsteridz.iterator();
                                while (iterator.hasNext()) {
                                    int i = (Integer)iterator.next();
                                    if (mapleMapObject.getObjectId() != i) continue;
                                    passed = true;
                                    break;
                                }
                                if (!passed) continue;
                                break;
                            }
                            if (passed) {
                                MaplePacket packet = mode == 1 ? MaplePacketCreator.showZakumShrine(spawned, 0) : (mode == 2 ? MaplePacketCreator.showChaosZakumShrine(spawned, 0) : MaplePacketCreator.showHorntailShrine(spawned, 0));
                                for (MapleCharacter chr : MapleMap.this.getCharactersThreadsafe()) {
                                    chr.getClient().getSession().write((Object)packet);
                                    chr.changeMap(returnMapz, returnMapz.getPortal(0));
                                }
                                MapleMap.this.checkStates("");
                                MapleMap.this.resetFully();
                            }
                        }
                    }
                };
            } else {
                run = new Runnable(){

                    @Override
                    public void run() {
                        MapleSquad sqnow = MapleMap.this.getSquadByMap();
                        if (MapleMap.this.getCharactersSize() > 0 && sqnow != null && sqnow.getStatus() == 2 && sqnow.getLeaderName().equals(leaderName) && MapleMap.this.getEMByMap().getProperty("state").equals(state)) {
                            MaplePacket packet = mode == 1 ? MaplePacketCreator.showZakumShrine(spawned, 0) : (mode == 2 ? MaplePacketCreator.showChaosZakumShrine(spawned, 0) : MaplePacketCreator.showHorntailShrine(spawned, 0));
                            for (MapleCharacter chr : MapleMap.this.getCharactersThreadsafe()) {
                                chr.getClient().getSession().write((Object)packet);
                                chr.changeMap(returnMapz, returnMapz.getPortal(0));
                            }
                            MapleMap.this.checkStates("");
                            MapleMap.this.resetFully();
                        }
                    }
                };
            }
            this.squadSchedule = Timer.MapTimer.getInstance().schedule(run, 300000L);
        }
    }

    public final MapleSquad getSquadByMap() {
        MapleSquad.MapleSquadType zz = null;
        switch (this.mapid) {
            case 105100300: 
            case 105100400: {
                zz = MapleSquad.MapleSquadType.bossbalrog;
                break;
            }
            case 280030000: {
                zz = MapleSquad.MapleSquadType.zak;
                break;
            }
            case 280030001: {
                zz = MapleSquad.MapleSquadType.chaoszak;
                break;
            }
            case 240060200: {
                zz = MapleSquad.MapleSquadType.horntail;
                break;
            }
            case 240060201: {
                zz = MapleSquad.MapleSquadType.chaosht;
                break;
            }
            case 270050100: {
                zz = MapleSquad.MapleSquadType.pinkbean;
                break;
            }
            case 802000111: {
                zz = MapleSquad.MapleSquadType.nmm_squad;
                break;
            }
            case 802000211: {
                zz = MapleSquad.MapleSquadType.vergamot;
                break;
            }
            case 802000311: {
                zz = MapleSquad.MapleSquadType.tokyo_2095;
                break;
            }
            case 802000411: {
                zz = MapleSquad.MapleSquadType.dunas;
                break;
            }
            case 802000611: {
                zz = MapleSquad.MapleSquadType.nibergen_squad;
                break;
            }
            case 802000711: {
                zz = MapleSquad.MapleSquadType.dunas2;
                break;
            }
            case 802000801: 
            case 802000802: 
            case 802000803: {
                zz = MapleSquad.MapleSquadType.core_blaze;
                break;
            }
            case 802000821: 
            case 802000823: {
                zz = MapleSquad.MapleSquadType.aufheben;
                break;
            }
            case 211070100: 
            case 211070101: 
            case 211070110: {
                zz = MapleSquad.MapleSquadType.vonleon;
                break;
            }
            case 551030200: {
                zz = MapleSquad.MapleSquadType.scartar;
                break;
            }
            case 271040100: {
                zz = MapleSquad.MapleSquadType.cygnus;
                break;
            }
            default: {
                return null;
            }
        }
        return ChannelServer.getInstance(this.channel).getMapleSquad(zz);
    }

    public final MapleSquad getSquadBegin() {
        if (this.squad != null) {
            return ChannelServer.getInstance(this.channel).getMapleSquad(this.squad);
        }
        return null;
    }

    public final EventManager getEMByMap() {
        String em = null;
        switch (this.mapid) {
            case 105100400: {
                em = "BossBalrog_EASY";
                break;
            }
            case 105100300: {
                em = "BossBalrog_NORMAL";
                break;
            }
            case 280030000: {
                em = "ZakumBattle";
                break;
            }
            case 240060200: {
                em = "HorntailBattle";
                break;
            }
            case 280030001: {
                em = "ChaosZakum";
                break;
            }
            case 240060201: {
                em = "ChaosHorntail";
                break;
            }
            case 270050100: {
                em = "PinkBeanBattle";
                break;
            }
            case 802000111: {
                em = "NamelessMagicMonster";
                break;
            }
            case 802000211: {
                em = "Vergamot";
                break;
            }
            case 802000311: {
                em = "2095_tokyo";
                break;
            }
            case 802000411: {
                em = "Dunas";
                break;
            }
            case 802000611: {
                em = "Nibergen";
                break;
            }
            case 802000711: {
                em = "Dunas2";
                break;
            }
            case 802000801: 
            case 802000802: 
            case 802000803: {
                em = "CoreBlaze";
                break;
            }
            case 802000821: 
            case 802000823: {
                em = "Aufhaven";
                break;
            }
            case 211070100: 
            case 211070101: 
            case 211070110: {
                em = "VonLeonBattle";
                break;
            }
            case 551030200: {
                em = "ScarTarBattle";
                break;
            }
            case 271040100: {
                em = "CygnusBattle";
                break;
            }
            case 262030300: {
                em = "HillaBattle";
                break;
            }
            case 262031300: {
                em = "DarkHillaBattle";
                break;
            }
            case 272020110: 
            case 272030400: {
                em = "ArkariumBattle";
                break;
            }
            case 955000100: 
            case 955000200: 
            case 955000300: {
                em = "AswanOffSeason";
                break;
            }
            case 280030100: {
                em = "ZakumBattle";
                break;
            }
            case 272020200: {
                em = "Akayile";
                break;
            }
            case 689013000: {
                em = "PinkZakum";
                break;
            }
            case 703200400: {
                em = "0AllBoss";
                break;
            }
            default: {
                return null;
            }
        }
        return ChannelServer.getInstance(this.channel).getEventSM().getEventManager(em);
    }

    public final void removePlayer(MapleCharacter chr) {
        if (this.everlast) {
            this.returnEverLastItem(chr);
        }
        this.charactersLock.writeLock().lock();
        try {
            this.characters.remove(chr);
        }
        finally {
            this.charactersLock.writeLock().unlock();
        }
        this.removeMapObject(chr);
        chr.checkFollow();
        this.broadcastMessage(MaplePacketCreator.removePlayerFromMap(chr.getId()));
        if (!chr.isClone()) {
            ArrayList<MapleMonster> update = new ArrayList<MapleMonster>();
            Iterator<MapleMonster> controlled = chr.getControlled().iterator();
            while (controlled.hasNext()) {
                MapleMonster mapleMonster = controlled.next();
                if (mapleMonster == null) continue;
                mapleMonster.setController(null);
                mapleMonster.setControllerHasAggro(false);
                mapleMonster.setControllerKnowsAboutAggro(false);
                controlled.remove();
                update.add(mapleMonster);
            }
            for (MapleMonster mapleMonster : update) {
                this.updateMonsterController(mapleMonster);
            }
            chr.leaveMap();
            this.checkStates(chr.getName());
            if (this.mapid == 109020001) {
                chr.canTalk(true);
            }
            for (WeakReference<MapleCharacter> chrz : chr.getClones()) {
                if (chrz.get() == null) continue;
                this.removePlayer((MapleCharacter)chrz.get());
            }
        }
        chr.cancelEffectFromBuffStat(MapleBuffStat.PUPPET);
        chr.cancelEffectFromBuffStat(MapleBuffStat.REAPER);
        boolean cancelSummons = false;
        for (MapleSummon mapleSummon : chr.getSummons().values()) {
            if (mapleSummon.getMovementType() == SummonMovementType.STATIONARY || mapleSummon.getMovementType() == SummonMovementType.CIRCLE_STATIONARY || mapleSummon.getMovementType() == SummonMovementType.WALK_STATIONARY) {
                cancelSummons = true;
                continue;
            }
            this.broadcastMessage(MaplePacketCreator.removeSummon(mapleSummon, true));
            mapleSummon.setChangedMap(true);
            this.removeMapObject(mapleSummon);
        }
        if (cancelSummons) {
            chr.cancelEffectFromBuffStat(MapleBuffStat.SUMMON);
        }
        if (chr.getDragon() != null) {
            this.removeMapObject(chr.getDragon());
        }
    }

    public final void broadcastMessage(MaplePacket packet) {
        this.broadcastMessage(null, packet, Double.POSITIVE_INFINITY, null);
    }

    public final void broadcastMessage(MapleCharacter source, MaplePacket packet, boolean repeatToSource) {
        this.broadcastMessage(repeatToSource ? null : source, packet, Double.POSITIVE_INFINITY, source.getPosition());
    }

    public final void broadcastMessage(MaplePacket packet, Point rangedFrom) {
        this.broadcastMessage(null, packet, GameConstants.maxViewRangeSq(), rangedFrom);
    }

    public final void broadcastMessage(MapleCharacter source, MaplePacket packet, Point rangedFrom) {
        this.broadcastMessage(source, packet, GameConstants.maxViewRangeSq(), rangedFrom);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void broadcastMessage(MapleCharacter source, MaplePacket packet, double rangeSq, Point rangedFrom) {
        this.charactersLock.readLock().lock();
        try {
            for (MapleCharacter chr : this.characters) {
                if (chr == source) continue;
                if (rangeSq < Double.POSITIVE_INFINITY) {
                    if (!(rangedFrom.distanceSq(chr.getPosition()) <= rangeSq)) continue;
                    chr.getClient().getSession().write((Object)packet);
                    continue;
                }
                chr.getClient().getSession().write((Object)packet);
            }
        }
        finally {
            this.charactersLock.readLock().unlock();
        }
    }

    private void sendObjectPlacement(MapleCharacter c) {
        if (c == null || c.isClone()) {
            return;
        }
        for (MapleMapObject mapleMapObject : this.getAllMonstersThreadsafe()) {
            this.updateMonsterController((MapleMonster)mapleMapObject);
        }
        for (MapleMapObject mapleMapObject : this.getMapObjectsInRange(c.getPosition(), GameConstants.maxViewRangeSq(), GameConstants.rangedMapobjectTypes)) {
            if (mapleMapObject.getType() == MapleMapObjectType.REACTOR && !((MapleReactor)mapleMapObject).isAlive()) continue;
            mapleMapObject.sendSpawnData(c.getClient());
            c.addVisibleMapObject(mapleMapObject);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final List<MapleMapObject> getMapObjectsInRange(Point from, double rangeSq) {
        ArrayList<MapleMapObject> ret = new ArrayList<MapleMapObject>();
        for (MapleMapObjectType type : MapleMapObjectType.values()) {
            this.mapobjectlocks.get((Object)type).readLock().lock();
            try {
                for (MapleMapObject mmo : this.mapobjects.get((Object)type).values()) {
                    if (!(from.distanceSq(mmo.getPosition()) <= rangeSq)) continue;
                    ret.add(mmo);
                }
            }
            finally {
                this.mapobjectlocks.get((Object)type).readLock().unlock();
            }
        }
        return ret;
    }

    public List<MapleMapObject> getItemsInRange(Point from, double rangeSq) {
        return this.getMapObjectsInRange(from, rangeSq, Arrays.asList(MapleMapObjectType.ITEM));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final List<MapleMapObject> getMapObjectsInRange(Point from, double rangeSq, List<MapleMapObjectType> MapObject_types) {
        ArrayList<MapleMapObject> ret = new ArrayList<MapleMapObject>();
        for (MapleMapObjectType type : MapObject_types) {
            this.mapobjectlocks.get((Object)type).readLock().lock();
            try {
                for (MapleMapObject mmo : this.mapobjects.get((Object)type).values()) {
                    if (!(from.distanceSq(mmo.getPosition()) <= rangeSq)) continue;
                    ret.add(mmo);
                }
            }
            finally {
                this.mapobjectlocks.get((Object)type).readLock().unlock();
            }
        }
        return ret;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final List<MapleMapObject> getMapObjectsInRect(Rectangle box, List<MapleMapObjectType> MapObject_types) {
        ArrayList<MapleMapObject> ret = new ArrayList<MapleMapObject>();
        for (MapleMapObjectType type : MapObject_types) {
            this.mapobjectlocks.get((Object)type).readLock().lock();
            try {
                for (MapleMapObject mmo : this.mapobjects.get((Object)type).values()) {
                    if (!box.contains(mmo.getPosition())) continue;
                    ret.add(mmo);
                }
            }
            finally {
                this.mapobjectlocks.get((Object)type).readLock().unlock();
            }
        }
        return ret;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final List<MapleCharacter> getPlayersInRectAndInList(Rectangle box, List<MapleCharacter> chrList) {
        LinkedList<MapleCharacter> character = new LinkedList<MapleCharacter>();
        this.charactersLock.readLock().lock();
        try {
            for (MapleCharacter a : this.characters) {
                if (!chrList.contains(a) || !box.contains(a.getPosition())) continue;
                character.add(a);
            }
        }
        finally {
            this.charactersLock.readLock().unlock();
        }
        return character;
    }

    public final void addPortal(MaplePortal myPortal) {
        this.portals.put(myPortal.getId(), myPortal);
    }

    public final MaplePortal getPortal(String portalname) {
        for (MaplePortal port : this.portals.values()) {
            if (!port.getName().equals(portalname)) continue;
            return port;
        }
        return null;
    }

    public final MaplePortal getPortal(int portalid) {
        return this.portals.get(portalid);
    }

    public final void resetPortals() {
        for (MaplePortal port : this.portals.values()) {
            port.setPortalState(true);
        }
    }

    public final void setFootholds(MapleFootholdTree footholds) {
        this.footholds = footholds;
    }

    public final MapleFootholdTree getFootholds() {
        return this.footholds;
    }

    public final void loadMonsterRate(boolean first) {
        int spawnSize = this.monsterSpawn.size();
        this.maxRegularSpawn = Math.round((float)spawnSize * this.monsterRate);
        if (this.maxRegularSpawn < 2) {
            this.maxRegularSpawn = 2;
        } else if (this.maxRegularSpawn > spawnSize) {
            this.maxRegularSpawn = spawnSize - spawnSize / 15;
        }
        if (this.fixedMob > 0) {
            this.maxRegularSpawn = this.fixedMob;
        }
        LinkedList<Spawns> newSpawn = new LinkedList<Spawns>();
        LinkedList<Spawns> newBossSpawn = new LinkedList<Spawns>();
        for (Spawns s : this.monsterSpawn) {
            if (s.getCarnivalTeam() >= 2) continue;
            if (s.getMonster().getStats().isBoss()) {
                newBossSpawn.add(s);
                continue;
            }
            newSpawn.add(s);
        }
        this.monsterSpawn.clear();
        this.monsterSpawn.addAll(newBossSpawn);
        this.monsterSpawn.addAll(newSpawn);
        if (first && spawnSize > 0) {
            this.lastSpawnTime = System.currentTimeMillis();
            if (GameConstants.isForceRespawn(this.mapid)) {
                this.createMobInterval = (short)15000;
            }
        }
    }

    public final SpawnPoint addMonsterSpawn(MapleMonster monster, int mobTime, byte carnivalTeam, String msg) {
        Point newpos = this.calcPointBelow(monster.getPosition());
        --newpos.y;
        SpawnPoint sp = new SpawnPoint(monster, newpos, mobTime, carnivalTeam, msg);
        if (carnivalTeam > -1) {
            this.monsterSpawn.add(0, sp);
        } else {
            this.monsterSpawn.add(sp);
        }
        return sp;
    }

    public final void addAreaMonsterSpawn(MapleMonster monster, Point pos1, Point pos2, Point pos3, int mobTime, String msg) {
        pos1 = this.calcPointBelow(pos1);
        pos2 = this.calcPointBelow(pos2);
        pos3 = this.calcPointBelow(pos3);
        if (pos1 != null) {
            --pos1.y;
        }
        if (pos2 != null) {
            --pos2.y;
        }
        if (pos3 != null) {
            --pos3.y;
        }
        if (pos1 == null && pos2 == null && pos3 == null) {
            System.out.println("WARNING: mapid " + this.mapid + ", monster " + monster.getId() + " could not be spawned.");
            return;
        }
        if (pos1 != null) {
            if (pos2 == null) {
                pos2 = new Point(pos1);
            }
            if (pos3 == null) {
                pos3 = new Point(pos1);
            }
        } else if (pos2 != null) {
            if (pos1 == null) {
                pos1 = new Point(pos2);
            }
            if (pos3 == null) {
                pos3 = new Point(pos2);
            }
        } else if (pos3 != null) {
            if (pos1 == null) {
                pos1 = new Point(pos3);
            }
            if (pos2 == null) {
                pos2 = new Point(pos3);
            }
        }
        this.monsterSpawn.add(new SpawnPointAreaBoss(monster, pos1, pos2, pos3, mobTime, msg));
    }

    public final List<MapleCharacter> getCharacters() {
        return this.getCharactersThreadsafe();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final List<MapleCharacter> getCharactersThreadsafe() {
        ArrayList<MapleCharacter> chars = new ArrayList<MapleCharacter>();
        this.charactersLock.readLock().lock();
        try {
            for (MapleCharacter mc : this.characters) {
                chars.add(mc);
            }
        }
        finally {
            this.charactersLock.readLock().unlock();
        }
        return chars;
    }

    public final MapleCharacter getCharacterById_InMap(int id) {
        return this.getCharacterById(id);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final MapleCharacter getCharacterById(int id) {
        this.charactersLock.readLock().lock();
        try {
            for (MapleCharacter mc : this.characters) {
                if (mc.getId() != id) continue;
                MapleCharacter mapleCharacter = mc;
                return mapleCharacter;
            }
        }
        finally {
            this.charactersLock.readLock().unlock();
        }
        return null;
    }

    public final void updateMapObjectVisibility(MapleCharacter chr, MapleMapObject mo) {
        if (chr == null || chr.isClone()) {
            return;
        }
        if (!chr.isMapObjectVisible(mo)) {
            if (mo.getType() == MapleMapObjectType.SUMMON || mo.getPosition().distanceSq(chr.getPosition()) <= (double)GameConstants.maxViewRangeSq()) {
                chr.addVisibleMapObject(mo);
                mo.sendSpawnData(chr.getClient());
            }
        } else if (mo.getType() != MapleMapObjectType.SUMMON && mo.getPosition().distanceSq(chr.getPosition()) > (double)GameConstants.maxViewRangeSq()) {
            chr.removeVisibleMapObject(mo);
            mo.sendDestroyData(chr.getClient());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void moveMonster(MapleMonster monster, Point reportedPos) {
        monster.setPosition(reportedPos);
        this.charactersLock.readLock().lock();
        try {
            for (MapleCharacter mc : this.characters) {
                this.updateMapObjectVisibility(mc, monster);
            }
        }
        finally {
            this.charactersLock.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void movePlayer(MapleCharacter player, Point newPosition) {
        player.setPosition(newPosition);
        if (!player.isClone()) {
            try {
                Collection<MapleMapObject> visibleObjects = player.getAndWriteLockVisibleMapObjects();
                ArrayList<MapleMapObject> copy = new ArrayList<MapleMapObject>(visibleObjects);
                for (MapleMapObject mo : copy) {
                    if (mo != null && this.getMapObject(mo.getObjectId(), mo.getType()) == mo) {
                        this.updateMapObjectVisibility(player, mo);
                        continue;
                    }
                    if (mo == null) continue;
                    visibleObjects.remove(mo);
                }
                for (MapleMapObject mo : this.getMapObjectsInRange(player.getPosition(), GameConstants.maxViewRangeSq())) {
                    if (mo == null || player.isMapObjectVisible(mo)) continue;
                    mo.sendSpawnData(player.getClient());
                    visibleObjects.add(mo);
                }
            }
            finally {
                player.unlockWriteVisibleMapObjects();
            }
        }
    }

    public MaplePortal findClosestSpawnpoint(Point from) {
        MaplePortal closest = null;
        double shortestDistance = Double.POSITIVE_INFINITY;
        for (MaplePortal portal : this.portals.values()) {
            double distance = portal.getPosition().distanceSq(from);
            if (portal.getType() < 0 || portal.getType() > 2 || !(distance < shortestDistance) || portal.getTargetMapId() != 999999999) continue;
            closest = portal;
            shortestDistance = distance;
        }
        return closest;
    }

    public String spawnDebug() {
        StringBuilder sb = new StringBuilder("Mapobjects in map : ");
        sb.append(this.getMapObjectSize());
        sb.append(" spawnedMonstersOnMap: ");
        sb.append(this.spawnedMonstersOnMap);
        sb.append(" spawnpoints: ");
        sb.append(this.monsterSpawn.size());
        sb.append(" maxRegularSpawn: ");
        sb.append(this.maxRegularSpawn);
        sb.append(" actual monsters: ");
        sb.append(this.getNumMonsters());
        return sb.toString();
    }

    public int characterSize() {
        return this.characters.size();
    }

    public final int getMapObjectSize() {
        return this.mapobjects.size() + this.getCharactersSize() - this.characters.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final int getCharactersSize() {
        int ret = 0;
        this.charactersLock.readLock().lock();
        try {
            for (MapleCharacter chr : this.characters) {
                if (chr.isClone()) continue;
                ++ret;
            }
        }
        finally {
            this.charactersLock.readLock().unlock();
        }
        return ret;
    }

    public Collection<MaplePortal> getPortals() {
        return Collections.unmodifiableCollection(this.portals.values());
    }

    public int getSpawnedMonstersOnMap() {
        return this.spawnedMonstersOnMap.get();
    }

    public void respawn(boolean force) {
        block4: {
            block5: {
                this.lastSpawnTime = System.currentTimeMillis();
                if (!force) break block5;
                int numShouldSpawn = this.monsterSpawn.size() - this.spawnedMonstersOnMap.get();
                if (numShouldSpawn <= 0) break block4;
                int spawned = 0;
                for (Spawns spawnPoint : this.monsterSpawn) {
                    spawnPoint.spawnMonster(this);
                    if (++spawned < numShouldSpawn) continue;
                    break block4;
                }
                break block4;
            }
            int numShouldSpawn = this.maxRegularSpawn - this.spawnedMonstersOnMap.get();
            if (numShouldSpawn > 0) {
                int spawned = 0;
                ArrayList<Spawns> randomSpawn = new ArrayList<Spawns>(this.monsterSpawn);
                Collections.shuffle(randomSpawn);
                for (Spawns spawnPoint : randomSpawn) {
                    if (spawnPoint.shouldSpawn() || GameConstants.isForceRespawn(this.mapid)) {
                        spawnPoint.spawnMonster(this);
                        ++spawned;
                    }
                    if (spawned < numShouldSpawn) continue;
                    break;
                }
            }
        }
    }

    public String getSnowballPortal() {
        int[] teamss = new int[2];
        for (MapleCharacter chr : this.getCharactersThreadsafe()) {
            if (chr.getPosition().y > -80) {
                teamss[0] = teamss[0] + 1;
                continue;
            }
            teamss[1] = teamss[1] + 1;
        }
        if (teamss[0] > teamss[1]) {
            return "st01";
        }
        return "st00";
    }

    public boolean isDisconnected(int id) {
        return this.dced.contains(id);
    }

    public void addDisconnected(int id) {
        this.dced.add(id);
    }

    public void resetDisconnected() {
        this.dced.clear();
    }

    public void startSpeedRun() {
        MapleSquad squad = this.getSquadByMap();
        if (squad != null) {
            for (MapleCharacter chr : this.getCharactersThreadsafe()) {
                if (!chr.getName().equals(squad.getLeaderName())) continue;
                this.startSpeedRun(chr.getName());
                return;
            }
        }
    }

    public void startSpeedRun(String leader) {
        this.speedRunStart = System.currentTimeMillis();
        this.speedRunLeader = leader;
    }

    public void endSpeedRun() {
        this.speedRunStart = 0L;
        this.speedRunLeader = "";
    }

    public void getRankAndAdd(String leader, String time, SpeedRunType type, long timz, Collection<String> squad) {
        try {
            StringBuilder rett = new StringBuilder();
            if (squad != null) {
                for (String chr : squad) {
                    rett.append(chr);
                    rett.append(",");
                }
            }
            String z = rett.toString();
            if (squad != null) {
                z = z.substring(0, z.length() - 1);
            }
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO speedruns(`type`, `leader`, `timestring`, `time`, `members`) VALUES (?,?,?,?,?)");
            ps.setString(1, type.name());
            ps.setString(2, leader);
            ps.setString(3, time);
            ps.setLong(4, timz);
            ps.setString(5, z);
            ps.executeUpdate();
            ps.close();
            if (SpeedRunner.getInstance().getSpeedRunData(type) == null) {
                SpeedRunner.getInstance().addSpeedRunData(type, SpeedRunner.getInstance().addSpeedRunData(new StringBuilder("#rThese are the speedrun times for " + (Object)((Object)type) + ".#k\r\n\r\n"), new HashMap<Integer, String>(), z, leader, 1, time));
            } else {
                SpeedRunner.getInstance().removeSpeedRunData(type);
                SpeedRunner.getInstance().loadSpeedRunData(type);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getSpeedRunStart() {
        return this.speedRunStart;
    }

    public final void disconnectAll() {
        for (MapleCharacter chr : this.getCharactersThreadsafe()) {
            if (chr.isGM()) continue;
            chr.getClient().disconnect(true, false);
            chr.getClient().getSession().close();
        }
    }

    public List<MapleNPC> getAllNPCs() {
        return this.getAllNPCsThreadsafe();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MapleNPC> getAllNPCsThreadsafe() {
        ArrayList<MapleNPC> ret = new ArrayList<MapleNPC>();
        this.mapobjectlocks.get((Object)MapleMapObjectType.NPC).readLock().lock();
        try {
            for (MapleMapObject mmo : this.mapobjects.get((Object)MapleMapObjectType.NPC).values()) {
                ret.add((MapleNPC)mmo);
            }
        }
        finally {
            this.mapobjectlocks.get((Object)MapleMapObjectType.NPC).readLock().unlock();
        }
        return ret;
    }

    public final void resetNPCs() {
        List<MapleNPC> npcs = this.getAllNPCsThreadsafe();
        for (MapleNPC npc : npcs) {
            if (!npc.isCustom()) continue;
            this.broadcastMessage(MaplePacketCreator.spawnNPC(npc, false));
            this.removeMapObject(npc);
        }
    }

    public final void resetFully() {
        this.resetFully(true);
    }

    public final void resetFully(boolean respawn) {
        this.killAllMonsters(false);
        this.reloadReactors();
        this.removeDrops();
        this.resetNPCs();
        this.resetSpawns();
        this.resetDisconnected();
        this.endSpeedRun();
        this.cancelSquadSchedule();
        this.resetPortals();
        this.environment.clear();
        if (respawn) {
            this.respawn(true);
        }
    }

    public final void cancelSquadSchedule() {
        this.squadTimer = false;
        if (this.squadSchedule != null) {
            this.squadSchedule.cancel(false);
            this.squadSchedule = null;
        }
    }

    public final void removeDrops() {
        List<MapleMapItem> items = this.getAllItemsThreadsafe();
        for (MapleMapItem i : items) {
            i.expire(this);
        }
    }

    public final void resetAllSpawnPoint(int mobid, int mobTime) {
        LinkedList<Spawns> sss = new LinkedList<Spawns>(this.monsterSpawn);
        this.resetFully();
        this.monsterSpawn.clear();
        for (Spawns s : sss) {
            MapleMonster newMons = MapleLifeFactory.getMonster(mobid);
            MapleMonster oldMons = s.getMonster();
            newMons.setCy(oldMons.getCy());
            newMons.setF(oldMons.getF());
            newMons.setFh(oldMons.getFh());
            newMons.setRx0(oldMons.getRx0());
            newMons.setRx1(oldMons.getRx1());
            newMons.setPosition(new Point(oldMons.getPosition()));
            newMons.setHide(oldMons.isHidden());
            this.addMonsterSpawn(newMons, mobTime, (byte)-1, null);
        }
        this.loadMonsterRate(true);
    }

    public final void resetSpawns() {
        boolean changed = false;
        Iterator<Spawns> sss = this.monsterSpawn.iterator();
        while (sss.hasNext()) {
            if (sss.next().getCarnivalId() <= -1) continue;
            sss.remove();
            changed = true;
        }
        this.setSpawns(true);
        if (changed) {
            this.loadMonsterRate(true);
        }
    }

    public final boolean makeCarnivalSpawn(int team, MapleMonster newMons, int num) {
        MapleNodes.MonsterPoint ret = null;
        for (MapleNodes.MonsterPoint mp : this.nodes.getMonsterPoints()) {
            if (mp.team != team && mp.team != -1) continue;
            Point newpos = this.calcPointBelow(new Point(mp.x, mp.y));
            --newpos.y;
            boolean found = false;
            for (Spawns s : this.monsterSpawn) {
                if (s.getCarnivalId() <= -1 || mp.team != -1 && s.getCarnivalTeam() != mp.team || s.getPosition().x != newpos.x || s.getPosition().y != newpos.y) continue;
                found = true;
                break;
            }
            if (found) continue;
            ret = mp;
            break;
        }
        if (ret != null) {
            newMons.setCy(ret.cy);
            newMons.setF(0);
            newMons.setFh(ret.fh);
            newMons.setRx0(ret.x + 50);
            newMons.setRx1(ret.x - 50);
            newMons.setPosition(new Point(ret.x, ret.y));
            newMons.setHide(false);
            SpawnPoint sp = this.addMonsterSpawn(newMons, 1, (byte)team, null);
            sp.setCarnival(num);
        }
        return ret != null;
    }

    public final boolean makeCarnivalReactor(int team, int num) {
        MapleReactor old = this.getReactorByName(team + "" + num);
        if (old != null && old.getState() < 5) {
            return false;
        }
        Point guardz = null;
        List<MapleReactor> react = this.getAllReactorsThreadsafe();
        for (Pair<Point, Integer> guard : this.nodes.getGuardians()) {
            if ((Integer)guard.right != team && (Integer)guard.right != -1) continue;
            boolean found = false;
            for (MapleReactor r : react) {
                if (r.getPosition().x != ((Point)guard.left).x || r.getPosition().y != ((Point)guard.left).y || r.getState() >= 5) continue;
                found = true;
                break;
            }
            if (found) continue;
            guardz = (Point)guard.left;
            break;
        }
        if (guardz != null) {
            MapleReactorStats stats = MapleReactorFactory.getReactor(9980000 + team);
            MapleReactor my = new MapleReactor(stats, 9980000 + team);
            stats.setFacingDirection((byte)0);
            my.setPosition(guardz);
            my.setState((byte)1);
            my.setDelay(0);
            my.setName(team + "" + num);
            this.spawnReactor(my);
            MapleCarnivalFactory.MCSkill skil = MapleCarnivalFactory.getInstance().getGuardian(num);
            for (MapleMonster mons : this.getAllMonstersThreadsafe()) {
                if (mons.getCarnivalTeam() != team) continue;
                skil.getSkill().applyEffect(null, mons, false);
            }
        }
        return guardz != null;
    }

    public final void blockAllPortal() {
        for (MaplePortal p : this.portals.values()) {
            p.setPortalState(false);
        }
    }

    public boolean getAndSwitchTeam() {
        return this.getCharactersSize() % 2 != 0;
    }

    public void setSquad(MapleSquad.MapleSquadType s) {
        this.squad = s;
    }

    public int getChannel() {
        return this.channel;
    }

    public int getConsumeItemCoolTime() {
        return this.consumeItemCoolTime;
    }

    public void setConsumeItemCoolTime(int ciit) {
        this.consumeItemCoolTime = ciit;
    }

    public void setPermanentWeather(int pw) {
        this.permanentWeather = pw;
    }

    public int getPermanentWeather() {
        return this.permanentWeather;
    }

    public void checkStates(String chr) {
        MapleSquad sqd = this.getSquadByMap();
        EventManager em = this.getEMByMap();
        int size = this.getCharactersSize();
        if (sqd != null) {
            sqd.removeMember(chr);
            if (em != null) {
                if (sqd.getLeaderName().equals(chr)) {
                    em.setProperty("leader", "false");
                }
                if (chr.equals("") || size == 0) {
                    sqd.clear();
                    em.setProperty("state", "0");
                    em.setProperty("leader", "true");
                    this.cancelSquadSchedule();
                }
            }
        }
        if (em != null && em.getProperty("state") != null && size == 0) {
            em.setProperty("state", "0");
            if (em.getProperty("leader") != null) {
                em.setProperty("leader", "true");
            }
        }
        if (this.speedRunStart > 0L && this.speedRunLeader.equalsIgnoreCase(chr)) {
            if (size > 0) {
                this.broadcastMessage(MaplePacketCreator.serverNotice(5, "The leader is not in the map! Your speedrun has failed"));
            }
            this.endSpeedRun();
        }
    }

    public void setNodes(MapleNodes mn) {
        this.nodes = mn;
    }

    public final List<MapleNodes.MaplePlatform> getPlatforms() {
        return this.nodes.getPlatforms();
    }

    public Collection<MapleNodes.MapleNodeInfo> getNodes() {
        return this.nodes.getNodes();
    }

    public MapleNodes.MapleNodeInfo getNode(int index) {
        return this.nodes.getNode(index);
    }

    public final List<Rectangle> getAreas() {
        return this.nodes.getAreas();
    }

    public final Rectangle getArea(int index) {
        return this.nodes.getArea(index);
    }

    public final void changeEnvironment(String ms, int type) {
        this.broadcastMessage(MaplePacketCreator.environmentChange(ms, type));
    }

    public final void toggleEnvironment(String ms) {
        if (this.environment.containsKey(ms)) {
            this.moveEnvironment(ms, this.environment.get(ms) == 1 ? 2 : 1);
        } else {
            this.moveEnvironment(ms, 1);
        }
    }

    public final void moveEnvironment(String ms, int type) {
        this.broadcastMessage(MaplePacketCreator.environmentMove(ms, type));
        this.environment.put(ms, type);
    }

    public final Map<String, Integer> getEnvironment() {
        return this.environment;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final int getNumPlayersInArea(int index) {
        int ret = 0;
        this.charactersLock.readLock().lock();
        try {
            Iterator<MapleCharacter> ltr = this.characters.iterator();
            while (ltr.hasNext()) {
                if (!this.getArea(index).contains(ltr.next().getPosition())) continue;
                ++ret;
            }
        }
        finally {
            this.charactersLock.readLock().unlock();
        }
        return ret;
    }

    public void broadcastGMMessage(MapleCharacter source, MaplePacket packet, boolean repeatToSource) {
        this.broadcastGMMessage(repeatToSource ? null : source, packet, Double.POSITIVE_INFINITY, source.getPosition());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void broadcastGMMessage(MapleCharacter source, MaplePacket packet, double rangeSq, Point rangedFrom) {
        this.charactersLock.readLock().lock();
        try {
            if (source == null) {
                for (MapleCharacter chr : this.characters) {
                    if (!chr.isStaff()) continue;
                    chr.getClient().getSession().write((Object)packet);
                }
            } else {
                for (MapleCharacter chr : this.characters) {
                    if (chr == source || chr.getGMLevel() < source.getGMLevel()) continue;
                    chr.getClient().getSession().write((Object)packet);
                }
            }
        }
        finally {
            this.charactersLock.readLock().unlock();
        }
    }

    public final List<Pair<Integer, Integer>> getMobsToSpawn() {
        return this.nodes.getMobsToSpawn();
    }

    public final List<Integer> getSkillIds() {
        return this.nodes.getSkillIds();
    }

    public final boolean canSpawn() {
        return this.lastSpawnTime > 0L && this.isSpawns && this.lastSpawnTime + (long)this.createMobInterval < System.currentTimeMillis();
    }

    public final boolean canHurt() {
        if (this.lastHurtTime > 0L && this.lastHurtTime + (long)this.decHPInterval < System.currentTimeMillis()) {
            this.lastHurtTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    private static interface SpawnCondition {
        public boolean canSpawn(MapleCharacter var1);
    }

    private static interface DelayedPacketCreation {
        public void sendPackets(MapleClient var1);
    }

    private class ActivateItemReactor
    implements Runnable {
        private MapleMapItem mapitem;
        private MapleReactor reactor;
        private MapleClient c;

        public ActivateItemReactor(MapleMapItem mapitem, MapleReactor reactor, MapleClient c) {
            this.mapitem = mapitem;
            this.reactor = reactor;
            this.c = c;
        }

        @Override
        public void run() {
            if (this.mapitem != null && this.mapitem == MapleMap.this.getMapObject(this.mapitem.getObjectId(), this.mapitem.getType())) {
                if (this.mapitem.isPickedUp()) {
                    this.reactor.setTimerActive(false);
                    return;
                }
                this.mapitem.expire(MapleMap.this);
                this.reactor.hitReactor(this.c);
                this.reactor.setTimerActive(false);
                if (this.reactor.getDelay() > 0) {
                    Timer.MapTimer.getInstance().schedule(new Runnable(){

                        @Override
                        public void run() {
                            ActivateItemReactor.this.reactor.forceHitReactor((byte)0);
                        }
                    }, this.reactor.getDelay());
                }
            } else {
                this.reactor.setTimerActive(false);
            }
        }
    }
}

