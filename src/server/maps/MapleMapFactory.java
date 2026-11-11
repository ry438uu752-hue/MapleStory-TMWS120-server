/*
 * Decompiled with CFR 0.152.
 */
package server.maps;

import database.DatabaseConnection;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.PortalFactory;
import server.life.AbstractLoadedMapleLife;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleNPC;
import server.maps.MapleFoothold;
import server.maps.MapleFootholdTree;
import server.maps.MapleMap;
import server.maps.MapleNodes;
import server.maps.MapleReactor;
import server.maps.MapleReactorFactory;
import server.maps.MapleReactorStats;
import tools.StringUtil;

public class MapleMapFactory {
    private static final MapleDataProvider source = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Map.wz"));
    private static final MapleData nameData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/String.wz")).getData("Map.img");
    private final Map<Integer, MapleMap> maps = new HashMap<Integer, MapleMap>();
    private final Map<Integer, MapleMap> instanceMap = new HashMap<Integer, MapleMap>();
    private static final Map<Integer, MapleNodes> mapInfos = new HashMap<Integer, MapleNodes>();
    private final ReentrantLock lock = new ReentrantLock(true);
    private int channel;

    public final MapleMap getMap(int mapid) {
        return this.getMap(mapid, true, true, true);
    }

    public final MapleMap getMap(int mapid, boolean respawns, boolean npcs) {
        return this.getMap(mapid, respawns, npcs, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final MapleMap getMap(int mapid, boolean respawns, boolean npcs, boolean reactors) {
        Integer omapid = mapid;
        MapleMap map = this.maps.get(omapid);
        if (map == null) {
            this.lock.lock();
            try {
                MapleData mobRate;
                map = this.maps.get(omapid);
                if (map != null) {
                    MapleMap mapleMap = map;
                    return mapleMap;
                }
                MapleData mapData = source.getData(this.getMapName(mapid));
                MapleData link = mapData.getChildByPath("info/link");
                if (link != null) {
                    mapData = source.getData(this.getMapName(MapleDataTool.getIntConvert("info/link", mapData)));
                }
                float monsterRate = 0.0f;
                if (respawns && (mobRate = mapData.getChildByPath("info/mobRate")) != null) {
                    monsterRate = ((Float)mobRate.getData()).floatValue();
                }
                map = new MapleMap(mapid, this.channel, MapleDataTool.getInt("info/returnMap", mapData), monsterRate);
                PortalFactory portalFactory = new PortalFactory();
                for (MapleData portal : mapData.getChildByPath("portal")) {
                    map.addPortal(portalFactory.makePortal(MapleDataTool.getInt(portal.getChildByPath("pt")), portal));
                }
                LinkedList<MapleFoothold> allFootholds = new LinkedList<MapleFoothold>();
                Point lBound = new Point();
                Point uBound = new Point();
                for (MapleData footRoot : mapData.getChildByPath("foothold")) {
                    Iterator iterator = footRoot.iterator();
                    while (iterator.hasNext()) {
                        MapleData footCat = (MapleData)iterator.next();
                        for (MapleData footHold : footCat) {
                            MapleFoothold fh = new MapleFoothold(new Point(MapleDataTool.getInt(footHold.getChildByPath("x1")), MapleDataTool.getInt(footHold.getChildByPath("y1"))), new Point(MapleDataTool.getInt(footHold.getChildByPath("x2")), MapleDataTool.getInt(footHold.getChildByPath("y2"))), Integer.parseInt(footHold.getName()));
                            fh.setPrev((short)MapleDataTool.getInt(footHold.getChildByPath("prev")));
                            fh.setNext((short)MapleDataTool.getInt(footHold.getChildByPath("next")));
                            if (fh.getX1() < lBound.x) {
                                lBound.x = fh.getX1();
                            }
                            if (fh.getX2() > uBound.x) {
                                uBound.x = fh.getX2();
                            }
                            if (fh.getY1() < lBound.y) {
                                lBound.y = fh.getY1();
                            }
                            if (fh.getY2() > uBound.y) {
                                uBound.y = fh.getY2();
                            }
                            allFootholds.add(fh);
                        }
                    }
                }
                MapleFootholdTree fTree = new MapleFootholdTree(lBound, uBound);
                for (MapleFoothold foothold : allFootholds) {
                    fTree.insert(foothold);
                }
                map.setFootholds(fTree);
                int bossid = -1;
                String msg = null;
                if (mapData.getChildByPath("info/timeMob") != null) {
                    bossid = MapleDataTool.getInt(mapData.getChildByPath("info/timeMob/id"), 0);
                    msg = MapleDataTool.getString(mapData.getChildByPath("info/timeMob/message"), null);
                }
                try {
                    Connection con = DatabaseConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement("SELECT * FROM spawns WHERE mid = ?");
                    ps.setInt(1, omapid);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        int id = rs.getInt("idd");
                        int f = rs.getInt("f");
                        boolean hide = false;
                        String type = rs.getString("type");
                        int _fh = rs.getInt("fh");
                        int cy = rs.getInt("cy");
                        int rx0 = rs.getInt("rx0");
                        int rx1 = rs.getInt("rx1");
                        int x = rs.getInt("x");
                        int y = rs.getInt("y");
                        int mobTime = rs.getInt("mobtime");
                        AbstractLoadedMapleLife myLife = this.loadLife(id, f, hide, _fh, cy, rx0, rx1, x, y, type);
                        if (!type.equals("n")) continue;
                        map.addMapObject(myLife);
                    }
                }
                catch (SQLException con) {
                    // empty catch block
                }
                for (MapleData life : mapData.getChildByPath("life")) {
                    String type = MapleDataTool.getString(life.getChildByPath("type"));
                    if (!npcs && type.equals("n")) continue;
                    AbstractLoadedMapleLife myLife = this.loadLife(life, MapleDataTool.getString(life.getChildByPath("id")), type);
                    if (myLife instanceof MapleMonster) {
                        MapleMonster mob2 = (MapleMonster)myLife;
                        map.addMonsterSpawn(mob2, MapleDataTool.getInt("mobTime", life, 0), (byte)MapleDataTool.getInt("team", life, -1), mob2.getId() == bossid ? msg : null);
                        continue;
                    }
                    if (myLife == null) continue;
                    map.addMapObject(myLife);
                }
                this.addAreaBossSpawn(map);
                map.setCreateMobInterval((short)MapleDataTool.getInt(mapData.getChildByPath("info/createMobInterval"), 9000));
                map.loadMonsterRate(true);
                map.setNodes(this.loadNodes(mapid, mapData));
                if (reactors && mapData.getChildByPath("reactor") != null) {
                    for (MapleData reactor : mapData.getChildByPath("reactor")) {
                        String id = MapleDataTool.getString(reactor.getChildByPath("id"));
                        if (id == null) continue;
                        map.spawnReactor(this.loadReactor(reactor, id, (byte)MapleDataTool.getInt(reactor.getChildByPath("f"), 0)));
                    }
                }
                try {
                    map.setMapName(MapleDataTool.getString("mapName", nameData.getChildByPath(this.getMapStringName(omapid)), ""));
                    map.setStreetName(MapleDataTool.getString("streetName", nameData.getChildByPath(this.getMapStringName(omapid)), ""));
                }
                catch (Exception e) {
                    map.setMapName("");
                    map.setStreetName("");
                }
                map.setClock(mapData.getChildByPath("clock") != null);
                map.setEverlast(MapleDataTool.getInt(mapData.getChildByPath("info/everlast"), 0) > 0);
                map.setTown(MapleDataTool.getInt(mapData.getChildByPath("info/town"), 0) > 0);
                map.setSoaring(MapleDataTool.getInt(mapData.getChildByPath("info/needSkillForFly"), 0) > 0);
                map.setPersonalShop(MapleDataTool.getInt(mapData.getChildByPath("info/personalShop"), 0) > 0);
                map.setForceMove(MapleDataTool.getInt(mapData.getChildByPath("info/lvForceMove"), 0));
                map.setHPDec(MapleDataTool.getInt(mapData.getChildByPath("info/decHP"), 0));
                map.setHPDecInterval(MapleDataTool.getInt(mapData.getChildByPath("info/decHPInterval"), 10000));
                map.setHPDecProtect(MapleDataTool.getInt(mapData.getChildByPath("info/protectItem"), 0));
                map.setForcedReturnMap(MapleDataTool.getInt(mapData.getChildByPath("info/forcedReturn"), 999999999));
                map.setTimeLimit(MapleDataTool.getInt(mapData.getChildByPath("info/timeLimit"), -1));
                map.setFieldLimit(MapleDataTool.getInt(mapData.getChildByPath("info/fieldLimit"), 0));
                map.setFirstUserEnter(MapleDataTool.getString(mapData.getChildByPath("info/onFirstUserEnter"), ""));
                map.setUserEnter(MapleDataTool.getString(mapData.getChildByPath("info/onUserEnter"), ""));
                map.setRecoveryRate(MapleDataTool.getFloat(mapData.getChildByPath("info/recovery"), 1.0f));
                map.setFixedMob(MapleDataTool.getInt(mapData.getChildByPath("info/fixedMobCapacity"), 0));
                map.setConsumeItemCoolTime(MapleDataTool.getInt(mapData.getChildByPath("info/consumeItemCoolTime"), 0));
                this.maps.put(omapid, map);
            }
            finally {
                this.lock.unlock();
            }
        }
        return map;
    }

    public MapleMap getInstanceMap(int instanceid) {
        return this.instanceMap.get(instanceid);
    }

    public void removeInstanceMap(int instanceid) {
        if (this.isInstanceMapLoaded(instanceid)) {
            this.getInstanceMap(instanceid).checkStates("");
            this.instanceMap.remove(instanceid);
        }
    }

    public void removeMap(int instanceid) {
        if (this.isMapLoaded(instanceid)) {
            this.getMap(instanceid).checkStates("");
            this.maps.remove(instanceid);
        }
    }

    public MapleMap CreateInstanceMap(int mapid, boolean respawns, boolean npcs, boolean reactors, int instanceid) {
        MapleData mobRate;
        if (this.isInstanceMapLoaded(instanceid)) {
            return this.getInstanceMap(instanceid);
        }
        MapleData mapData = source.getData(this.getMapName(mapid));
        MapleData link = mapData.getChildByPath("info/link");
        if (link != null) {
            mapData = source.getData(this.getMapName(MapleDataTool.getIntConvert("info/link", mapData)));
        }
        float monsterRate = 0.0f;
        if (respawns && (mobRate = mapData.getChildByPath("info/mobRate")) != null) {
            monsterRate = ((Float)mobRate.getData()).floatValue();
        }
        MapleMap map = new MapleMap(mapid, this.channel, MapleDataTool.getInt("info/returnMap", mapData), monsterRate);
        PortalFactory portalFactory = new PortalFactory();
        for (MapleData portal : mapData.getChildByPath("portal")) {
            map.addPortal(portalFactory.makePortal(MapleDataTool.getInt(portal.getChildByPath("pt")), portal));
        }
        LinkedList<MapleFoothold> allFootholds = new LinkedList<MapleFoothold>();
        Point lBound = new Point();
        Point uBound = new Point();
        for (MapleData footRoot : mapData.getChildByPath("foothold")) {
            Iterator iterator = footRoot.iterator();
            while (iterator.hasNext()) {
                MapleData footCat = (MapleData)iterator.next();
                for (MapleData footHold : footCat) {
                    MapleFoothold fh = new MapleFoothold(new Point(MapleDataTool.getInt(footHold.getChildByPath("x1")), MapleDataTool.getInt(footHold.getChildByPath("y1"))), new Point(MapleDataTool.getInt(footHold.getChildByPath("x2")), MapleDataTool.getInt(footHold.getChildByPath("y2"))), Integer.parseInt(footHold.getName()));
                    fh.setPrev((short)MapleDataTool.getInt(footHold.getChildByPath("prev")));
                    fh.setNext((short)MapleDataTool.getInt(footHold.getChildByPath("next")));
                    if (fh.getX1() < lBound.x) {
                        lBound.x = fh.getX1();
                    }
                    if (fh.getX2() > uBound.x) {
                        uBound.x = fh.getX2();
                    }
                    if (fh.getY1() < lBound.y) {
                        lBound.y = fh.getY1();
                    }
                    if (fh.getY2() > uBound.y) {
                        uBound.y = fh.getY2();
                    }
                    allFootholds.add(fh);
                }
            }
        }
        MapleFootholdTree fTree = new MapleFootholdTree(lBound, uBound);
        for (MapleFoothold fh : allFootholds) {
            fTree.insert(fh);
        }
        map.setFootholds(fTree);
        int bossid = -1;
        String msg = null;
        if (mapData.getChildByPath("info/timeMob") != null) {
            bossid = MapleDataTool.getInt(mapData.getChildByPath("info/timeMob/id"), 0);
            msg = MapleDataTool.getString(mapData.getChildByPath("info/timeMob/message"), null);
        }
        for (MapleData life : mapData.getChildByPath("life")) {
            String type = MapleDataTool.getString(life.getChildByPath("type"));
            if (!npcs && type.equals("n")) continue;
            AbstractLoadedMapleLife myLife = this.loadLife((MapleData)life, MapleDataTool.getString(life.getChildByPath("id")), type);
            if (myLife instanceof MapleMonster) {
                MapleMonster mob2 = (MapleMonster)myLife;
                map.addMonsterSpawn(mob2, MapleDataTool.getInt("mobTime", (MapleData)life, 0), (byte)MapleDataTool.getInt("team", (MapleData)life, -1), mob2.getId() == bossid ? msg : null);
                continue;
            }
            map.addMapObject(myLife);
        }
        this.addAreaBossSpawn(map);
        map.setCreateMobInterval((short)MapleDataTool.getInt(mapData.getChildByPath("info/createMobInterval"), 9000));
        map.loadMonsterRate(true);
        map.setNodes(this.loadNodes(mapid, mapData));
        if (reactors && mapData.getChildByPath("reactor") != null) {
            for (MapleData reactor : mapData.getChildByPath("reactor")) {
                String id = MapleDataTool.getString(reactor.getChildByPath("id"));
                if (id == null) continue;
                map.spawnReactor(this.loadReactor(reactor, id, (byte)MapleDataTool.getInt(reactor.getChildByPath("f"), 0)));
            }
        }
        try {
            map.setMapName(MapleDataTool.getString("mapName", nameData.getChildByPath(this.getMapStringName(mapid)), ""));
            map.setStreetName(MapleDataTool.getString("streetName", nameData.getChildByPath(this.getMapStringName(mapid)), ""));
        }
        catch (Exception e) {
            map.setMapName("");
            map.setStreetName("");
        }
        map.setClock(MapleDataTool.getInt(mapData.getChildByPath("info/clock"), 0) > 0);
        map.setEverlast(MapleDataTool.getInt(mapData.getChildByPath("info/everlast"), 0) > 0);
        map.setTown(MapleDataTool.getInt(mapData.getChildByPath("info/town"), 0) > 0);
        map.setSoaring(MapleDataTool.getInt(mapData.getChildByPath("info/needSkillForFly"), 0) > 0);
        map.setForceMove(MapleDataTool.getInt(mapData.getChildByPath("info/lvForceMove"), 0));
        map.setHPDec(MapleDataTool.getInt(mapData.getChildByPath("info/decHP"), 0));
        map.setHPDecInterval(MapleDataTool.getInt(mapData.getChildByPath("info/decHPInterval"), 10000));
        map.setHPDecProtect(MapleDataTool.getInt(mapData.getChildByPath("info/protectItem"), 0));
        map.setForcedReturnMap(MapleDataTool.getInt(mapData.getChildByPath("info/forcedReturn"), 999999999));
        map.setTimeLimit(MapleDataTool.getInt(mapData.getChildByPath("info/timeLimit"), -1));
        map.setFieldLimit(MapleDataTool.getInt(mapData.getChildByPath("info/fieldLimit"), 0));
        map.setFirstUserEnter(MapleDataTool.getString(mapData.getChildByPath("info/onFirstUserEnter"), ""));
        map.setUserEnter(MapleDataTool.getString(mapData.getChildByPath("info/onUserEnter"), ""));
        map.setRecoveryRate(MapleDataTool.getFloat(mapData.getChildByPath("info/recovery"), 1.0f));
        map.setFixedMob(MapleDataTool.getInt(mapData.getChildByPath("info/fixedMobCapacity"), 0));
        map.setConsumeItemCoolTime(MapleDataTool.getInt(mapData.getChildByPath("info/consumeItemCoolTime"), 0));
        this.instanceMap.put(instanceid, map);
        return map;
    }

    public int getLoadedMaps() {
        return this.maps.size();
    }

    public boolean isMapLoaded(int mapId) {
        return this.maps.containsKey(mapId);
    }

    public boolean isInstanceMapLoaded(int instanceid) {
        return this.instanceMap.containsKey(instanceid);
    }

    public void clearLoadedMap() {
        this.maps.clear();
    }

    public Collection<MapleMap> getAllMaps() {
        return this.maps.values();
    }

    public Collection<MapleMap> getAllInstanceMaps() {
        return this.instanceMap.values();
    }

    private AbstractLoadedMapleLife loadLife(MapleData life, String id, String type) {
        AbstractLoadedMapleLife myLife = MapleLifeFactory.getLife(Integer.parseInt(id), type);
        if (myLife == null) {
            return null;
        }
        myLife.setCy(MapleDataTool.getInt(life.getChildByPath("cy")));
        MapleData dF = life.getChildByPath("f");
        if (dF != null) {
            myLife.setF(MapleDataTool.getInt(dF));
        }
        myLife.setFh(MapleDataTool.getInt(life.getChildByPath("fh")));
        myLife.setRx0(MapleDataTool.getInt(life.getChildByPath("rx0")));
        myLife.setRx1(MapleDataTool.getInt(life.getChildByPath("rx1")));
        myLife.setPosition(new Point(MapleDataTool.getInt(life.getChildByPath("x")), MapleDataTool.getInt(life.getChildByPath("y"))));
        if (MapleDataTool.getInt("hide", life, 0) == 1 && myLife instanceof MapleNPC) {
            myLife.setHide(true);
        }
        return myLife;
    }

    private AbstractLoadedMapleLife loadLife(int id, int f, boolean hide, int fh, int cy, int rx0, int rx1, int x, int y, String type) {
        AbstractLoadedMapleLife myLife = MapleLifeFactory.getLife(id, type);
        myLife.setCy(cy);
        myLife.setF(f);
        myLife.setFh(fh);
        myLife.setRx0(rx0);
        myLife.setRx1(rx1);
        myLife.setPosition(new Point(x, y));
        myLife.setHide(hide);
        return myLife;
    }

    private final MapleReactor loadReactor(MapleData reactor, String id, byte FacingDirection) {
        MapleReactorStats stats = MapleReactorFactory.getReactor(Integer.parseInt(id));
        MapleReactor myReactor = new MapleReactor(stats, Integer.parseInt(id));
        stats.setFacingDirection(FacingDirection);
        myReactor.setPosition(new Point(MapleDataTool.getInt(reactor.getChildByPath("x")), MapleDataTool.getInt(reactor.getChildByPath("y"))));
        myReactor.setDelay(MapleDataTool.getInt(reactor.getChildByPath("reactorTime")) * 1000);
        myReactor.setState((byte)0);
        myReactor.setName(MapleDataTool.getString(reactor.getChildByPath("name"), ""));
        return myReactor;
    }

    private String getMapName(int mapid) {
        String mapName = StringUtil.getLeftPaddedStr(Integer.toString(mapid), '0', 9);
        StringBuilder builder = new StringBuilder("Map/Map");
        builder.append(mapid / 100000000);
        builder.append("/");
        builder.append(mapName);
        builder.append(".img");
        mapName = builder.toString();
        return mapName;
    }

    private String getMapStringName(int mapid) {
        StringBuilder builder = new StringBuilder();
        if (mapid < 100000000) {
            builder.append("maple");
        } else if (mapid >= 100000000 && mapid < 200000000 || mapid / 100000 == 5540) {
            builder.append("victoria");
        } else if (mapid >= 200000000 && mapid < 300000000) {
            builder.append("ossyria");
        } else if (mapid >= 300000000 && mapid < 400000000) {
            builder.append("elin");
        } else if (mapid >= 500000000 && mapid < 510000000) {
            builder.append("thai");
        } else if (mapid >= 540000000 && mapid < 600000000) {
            builder.append("SG");
        } else if (mapid >= 600000000 && mapid < 620000000) {
            builder.append("MasteriaGL");
        } else if (mapid >= 670000000 && mapid < 677000000 || mapid >= 678000000 && mapid < 682000000) {
            builder.append("global");
        } else if (mapid >= 677000000 && mapid < 678000000) {
            builder.append("Episode1GL");
        } else if (mapid >= 682000000 && mapid < 683000000) {
            builder.append("HalloweenGL");
        } else if (mapid >= 683000000 && mapid < 684000000) {
            builder.append("event");
        } else if (mapid >= 684000000 && mapid < 685000000) {
            builder.append("event_5th");
        } else if (mapid >= 700000000 && mapid < 700000300) {
            builder.append("wedding");
        } else if (mapid >= 701000000 && mapid < 701020000) {
            builder.append("china");
        } else if (mapid >= 800000000 && mapid < 900000000) {
            builder.append("jp");
        } else if (mapid >= 700000000 && mapid < 782000002) {
            builder.append("chinese");
        } else {
            builder.append("etc");
        }
        builder.append("/");
        builder.append(mapid);
        return builder.toString();
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    private void addAreaBossSpawn(MapleMap map) {
        int monsterid = -1;
        int mobtime = -1;
        String msg = null;
        Point pos1 = null;
        Point pos2 = null;
        Point pos3 = null;
        switch (map.getId()) {
            case 104000400: {
                mobtime = 2700;
                monsterid = 2220000;
                msg = "\u7d05\u5bf6\u738b\u51fa\u73fe\u4e86,\u5feb\u9003\u963f";
                pos1 = new Point(439, 185);
                pos2 = new Point(301, -85);
                pos3 = new Point(107, -355);
                break;
            }
            case 101030404: {
                mobtime = 2700;
                monsterid = 3220000;
                msg = "\u6a39\u5996\u738b\u51fa\u73fe\u4e86,\u5c0f\u5fc3\u88ab\u4ed6\u7684\u6a39\u679d";
                pos1 = new Point(867, 1282);
                pos2 = new Point(810, 1570);
                pos3 = new Point(838, 2197);
                break;
            }
            case 110040000: {
                mobtime = 1200;
                monsterid = 5220001;
                msg = "\u5de8\u5c45\u87f9\u51fa\u73fe\u4e86,\u5c0f\u5fc3\u4ed6\u5927\u9257\u5b50!!";
                pos1 = new Point(-355, 179);
                pos2 = new Point(-1283, -113);
                pos3 = new Point(-571, -593);
                break;
            }
            case 250010304: {
                mobtime = 2100;
                monsterid = 7220000;
                msg = "\u6d41\u6d6a\u718a\u51fa\u73fe\u4e86,\u6211\u6703\u653e\u96f7\u96fb\u5594";
                pos1 = new Point(-210, 33);
                pos2 = new Point(-234, 393);
                pos3 = new Point(-654, 33);
                break;
            }
            case 200010300: {
                mobtime = 1200;
                monsterid = 8220000;
                msg = "\u827e\u8389\u5091\u51fa\u73fe\u4e86";
                pos1 = new Point(665, 83);
                pos2 = new Point(672, -217);
                pos3 = new Point(-123, -217);
                break;
            }
            case 250010503: {
                mobtime = 1800;
                monsterid = 7220002;
                msg = "\u55b5\u4ed9\u602a\u4eba\u51fa\u73fe\u4e86,\u5c0f\u5fc3\u88ab\u4ed6\u9b45\u60d1";
                pos1 = new Point(-303, 543);
                pos2 = new Point(227, 543);
                pos3 = new Point(719, 543);
                break;
            }
            case 222010310: {
                mobtime = 2700;
                monsterid = 7220001;
                msg = "\u4e5d\u5c3e\u5996\u72d0\u51fa\u73fe\u4e86";
                pos1 = new Point(-169, -147);
                pos2 = new Point(-517, 93);
                pos3 = new Point(247, 93);
                break;
            }
            case 107000300: {
                mobtime = 1800;
                monsterid = 6220000;
                msg = "\u6cbc\u6fa4\u5de8\u9c77\u51fa\u73fe\u4e86";
                pos1 = new Point(710, 118);
                pos2 = new Point(95, 119);
                pos3 = new Point(-535, 120);
                break;
            }
            case 100040105: {
                mobtime = 1800;
                monsterid = 5220002;
                msg = "\u6bad\u5c4d\u7334\u738b\u51fa\u73fe\u4e86";
                pos1 = new Point(1000, 278);
                pos2 = new Point(557, 278);
                pos3 = new Point(95, 278);
                break;
            }
            case 100040106: {
                mobtime = 1800;
                monsterid = 5220002;
                msg = "\u6bad\u5c4d\u7334\u738b\u51fa\u73fe\u4e86";
                pos1 = new Point(1000, 278);
                pos2 = new Point(557, 278);
                pos3 = new Point(95, 278);
                break;
            }
            case 220050100: {
                mobtime = 1500;
                monsterid = 5220003;
                msg = "\u5e03\u5495\u3002\u3002\u5e03\u5495\u3002\u3002\u5495\u5495\u9418\u51fa\u73fe\u4e86";
                pos1 = new Point(-467, 1032);
                pos2 = new Point(532, 1032);
                pos3 = new Point(-47, 1032);
                break;
            }
            case 221040301: {
                mobtime = 2400;
                monsterid = 6220001;
                msg = "\u845b\u96f7\u91d1\u525b\u51fa\u73fe\u4e86";
                pos1 = new Point(-4134, 416);
                pos2 = new Point(-4283, 776);
                pos3 = new Point(-3292, 776);
                break;
            }
            case 240040401: {
                mobtime = 7200;
                monsterid = 8220003;
                msg = "\u5bd2\u971c\u51b0\u9f8d\u51fa\u73fe\u4e86";
                pos1 = new Point(-15, 2481);
                pos2 = new Point(127, 1634);
                pos3 = new Point(159, 1142);
                break;
            }
            case 260010201: {
                mobtime = 3600;
                monsterid = 0x312221;
                msg = "\u4ed9\u4eba\u9577\u8001\u51fa\u73fe\u4e86";
                pos1 = new Point(-215, 275);
                pos2 = new Point(298, 275);
                pos3 = new Point(592, 275);
                break;
            }
            case 261030000: {
                mobtime = 2700;
                monsterid = 8220002;
                msg = "\u5947\u7f8e\u62c9\u51fa\u73fe\u4e86,\u5c0f\u5fc3\u4ed6\u7684BB\u5f48";
                pos1 = new Point(-1094, -405);
                pos2 = new Point(-772, -116);
                pos3 = new Point(-108, 181);
                break;
            }
            case 230020100: {
                mobtime = 2700;
                monsterid = 0x406460;
                msg = "\u706b\u868c\u6bbc\u51fa\u73fe\u4e86";
                pos1 = new Point(-291, -20);
                pos2 = new Point(-272, -500);
                pos3 = new Point(-462, 640);
                break;
            }
            default: {
                return;
            }
        }
        if (monsterid > 0) {
            map.addAreaMonsterSpawn(MapleLifeFactory.getMonster(monsterid), pos1, pos2, pos3, mobtime, msg);
        }
    }

    private MapleNodes loadNodes(int mapid, MapleData mapData) {
        MapleNodes nodeInfo = mapInfos.get(mapid);
        if (nodeInfo == null) {
            nodeInfo = new MapleNodes(mapid);
            if (mapData.getChildByPath("nodeInfo") != null) {
                for (MapleData node : mapData.getChildByPath("nodeInfo")) {
                    try {
                        if (node.getName().equals("start")) {
                            nodeInfo.setNodeStart(MapleDataTool.getInt((MapleData)node, 0));
                            continue;
                        }
                        if (node.getName().equals("end")) {
                            nodeInfo.setNodeEnd(MapleDataTool.getInt((MapleData)node, 0));
                            continue;
                        }
                        ArrayList<Integer> edges = new ArrayList<Integer>();
                        if (node.getChildByPath("edge") != null) {
                            for (MapleData edge : node.getChildByPath("edge")) {
                                edges.add(MapleDataTool.getInt(edge, -1));
                            }
                        }
                        MapleNodes.MapleNodeInfo mni = new MapleNodes.MapleNodeInfo(Integer.parseInt(node.getName()), MapleDataTool.getIntConvert("key", (MapleData)node, 0), MapleDataTool.getIntConvert("x", (MapleData)node, 0), MapleDataTool.getIntConvert("y", (MapleData)node, 0), MapleDataTool.getIntConvert("attr", (MapleData)node, 0), edges);
                        nodeInfo.addNode(mni);
                    }
                    catch (NumberFormatException edges) {}
                }
                nodeInfo.sortNodes();
            }
            for (int i = 1; i <= 7; ++i) {
                if (mapData.getChildByPath(String.valueOf(i)) == null || mapData.getChildByPath(i + "/obj") == null) continue;
                for (MapleData node : mapData.getChildByPath(i + "/obj")) {
                    int sn_count = MapleDataTool.getIntConvert("SN_count", node, 0);
                    String name = MapleDataTool.getString("name", node, "");
                    int speed = MapleDataTool.getIntConvert("speed", node, 0);
                    if (sn_count <= 0 || speed <= 0 || name.equals("")) continue;
                    ArrayList<Integer> SN = new ArrayList<Integer>();
                    for (int x = 0; x < sn_count; ++x) {
                        SN.add(MapleDataTool.getIntConvert("SN" + x, node, 0));
                    }
                    MapleNodes.MaplePlatform mni = new MapleNodes.MaplePlatform(name, MapleDataTool.getIntConvert("start", node, 2), speed, MapleDataTool.getIntConvert("x1", node, 0), MapleDataTool.getIntConvert("y1", node, 0), MapleDataTool.getIntConvert("x2", node, 0), MapleDataTool.getIntConvert("y2", node, 0), MapleDataTool.getIntConvert("r", node, 0), SN);
                    nodeInfo.addPlatform(mni);
                }
            }
            if (mapData.getChildByPath("area") != null) {
                for (MapleData area : mapData.getChildByPath("area")) {
                    int x1 = MapleDataTool.getInt(area.getChildByPath("x1"));
                    int y1 = MapleDataTool.getInt(area.getChildByPath("y1"));
                    int x2 = MapleDataTool.getInt(area.getChildByPath("x2"));
                    int y2 = MapleDataTool.getInt(area.getChildByPath("y2"));
                    Rectangle mapArea = new Rectangle(x1, y1, x2 - x1, y2 - y1);
                    nodeInfo.addMapleArea(mapArea);
                }
            }
            if (mapData.getChildByPath("monsterCarnival") != null) {
                MapleData mc = mapData.getChildByPath("monsterCarnival");
                if (mc.getChildByPath("mobGenPos") != null) {
                    for (MapleData area : mc.getChildByPath("mobGenPos")) {
                        nodeInfo.addMonsterPoint(MapleDataTool.getInt(area.getChildByPath("x")), MapleDataTool.getInt(area.getChildByPath("y")), MapleDataTool.getInt(area.getChildByPath("fh")), MapleDataTool.getInt(area.getChildByPath("cy")), MapleDataTool.getInt("team", area, -1));
                    }
                }
                if (mc.getChildByPath("mob") != null) {
                    for (MapleData area : mc.getChildByPath("mob")) {
                        nodeInfo.addMobSpawn(MapleDataTool.getInt(area.getChildByPath("id")), MapleDataTool.getInt(area.getChildByPath("spendCP")));
                    }
                }
                if (mc.getChildByPath("guardianGenPos") != null) {
                    for (MapleData area : mc.getChildByPath("guardianGenPos")) {
                        nodeInfo.addGuardianSpawn(new Point(MapleDataTool.getInt(area.getChildByPath("x")), MapleDataTool.getInt(area.getChildByPath("y"))), MapleDataTool.getInt("team", area, -1));
                    }
                }
                if (mc.getChildByPath("skill") != null) {
                    for (MapleData area : mc.getChildByPath("skill")) {
                        nodeInfo.addSkillId(MapleDataTool.getInt(area));
                    }
                }
            }
            mapInfos.put(mapid, nodeInfo);
        }
        return nodeInfo;
    }
}

