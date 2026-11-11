/*
 * Decompiled with CFR 0.152.
 */
package server.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import tools.Pair;

public class MapleNodes {
    private Map<Integer, MapleNodeInfo> nodes = new LinkedHashMap<Integer, MapleNodeInfo>();
    private final List<Rectangle> areas = new ArrayList<Rectangle>();
    private List<MaplePlatform> platforms = new ArrayList<MaplePlatform>();
    private List<MonsterPoint> monsterPoints;
    private List<Integer> skillIds = new ArrayList<Integer>();
    private List<Pair<Integer, Integer>> mobsToSpawn;
    private List<Pair<Point, Integer>> guardiansToSpawn;
    private int nodeStart = -1;
    private int nodeEnd = -1;
    private int mapid;
    private boolean firstHighest = true;

    public MapleNodes(int mapid) {
        this.monsterPoints = new ArrayList<MonsterPoint>();
        this.mobsToSpawn = new ArrayList<Pair<Integer, Integer>>();
        this.guardiansToSpawn = new ArrayList<Pair<Point, Integer>>();
        this.mapid = mapid;
    }

    public void setNodeStart(int ns) {
        this.nodeStart = ns;
    }

    public void setNodeEnd(int ns) {
        this.nodeEnd = ns;
    }

    public void addNode(MapleNodeInfo mni) {
        this.nodes.put(mni.key, mni);
    }

    public Collection<MapleNodeInfo> getNodes() {
        return new ArrayList<MapleNodeInfo>(this.nodes.values());
    }

    public MapleNodeInfo getNode(int index) {
        int i = 1;
        for (MapleNodeInfo x : this.getNodes()) {
            if (i == index) {
                return x;
            }
            ++i;
        }
        return null;
    }

    private int getNextNode(MapleNodeInfo mni) {
        if (mni == null) {
            return -1;
        }
        this.addNode(mni);
        int ret = -1;
        for (int i : mni.edge) {
            if (this.nodes.get(i) != null) continue;
            if (ret != -1 && this.mapid / 100 == 9211204) {
                if (!this.firstHighest) {
                    ret = Math.min(ret, i);
                    continue;
                }
                this.firstHighest = false;
                ret = Math.max(ret, i);
                break;
            }
            ret = i;
        }
        return ret;
    }

    public void sortNodes() {
        if (this.nodes.size() <= 0 || this.nodeStart < 0) {
            return;
        }
        HashMap<Integer, MapleNodeInfo> unsortedNodes = new HashMap<Integer, MapleNodeInfo>(this.nodes);
        int nodeSize = unsortedNodes.size();
        this.nodes.clear();
        int nextNode = this.getNextNode((MapleNodeInfo)unsortedNodes.get(this.nodeStart));
        while (this.nodes.size() != nodeSize && nextNode >= 0) {
            nextNode = this.getNextNode((MapleNodeInfo)unsortedNodes.get(nextNode));
        }
    }

    public final void addMapleArea(Rectangle rec) {
        this.areas.add(rec);
    }

    public final List<Rectangle> getAreas() {
        return new ArrayList<Rectangle>(this.areas);
    }

    public final Rectangle getArea(int index) {
        return this.getAreas().get(index);
    }

    public final void addPlatform(MaplePlatform mp) {
        this.platforms.add(mp);
    }

    public final List<MaplePlatform> getPlatforms() {
        return new ArrayList<MaplePlatform>(this.platforms);
    }

    public final List<MonsterPoint> getMonsterPoints() {
        return this.monsterPoints;
    }

    public final void addMonsterPoint(int x, int y, int fh, int cy, int team) {
        this.monsterPoints.add(new MonsterPoint(x, y, fh, cy, team));
    }

    public final void addMobSpawn(int mobId, int spendCP) {
        this.mobsToSpawn.add(new Pair<Integer, Integer>(mobId, spendCP));
    }

    public final List<Pair<Integer, Integer>> getMobsToSpawn() {
        return this.mobsToSpawn;
    }

    public final void addGuardianSpawn(Point guardian, int team) {
        this.guardiansToSpawn.add(new Pair<Point, Integer>(guardian, team));
    }

    public final List<Pair<Point, Integer>> getGuardians() {
        return this.guardiansToSpawn;
    }

    public final List<Integer> getSkillIds() {
        return this.skillIds;
    }

    public final void addSkillId(int z) {
        this.skillIds.add(z);
    }

    public static class MonsterPoint {
        public int x;
        public int y;
        public int fh;
        public int cy;
        public int team;

        public MonsterPoint(int x, int y, int fh, int cy, int team) {
            this.x = x;
            this.y = y;
            this.fh = fh;
            this.cy = cy;
            this.team = team;
        }
    }

    public static class MaplePlatform {
        public String name;
        public int start;
        public int speed;
        public int x1;
        public int y1;
        public int x2;
        public int y2;
        public int r;
        public List<Integer> SN;

        public MaplePlatform(String name, int start, int speed, int x1, int y1, int x2, int y2, int r, List<Integer> SN) {
            this.name = name;
            this.start = start;
            this.speed = speed;
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.r = r;
            this.SN = SN;
        }
    }

    public static class MapleNodeInfo {
        public int node;
        public int key;
        public int x;
        public int y;
        public int attr;
        public List<Integer> edge;

        public MapleNodeInfo(int node, int key, int x, int y, int attr, List<Integer> edge) {
            this.node = node;
            this.key = key;
            this.x = x;
            this.y = y;
            this.attr = attr;
            this.edge = edge;
        }
    }
}

