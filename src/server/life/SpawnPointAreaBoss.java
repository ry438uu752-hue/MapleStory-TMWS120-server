/*
 * Decompiled with CFR 0.152.
 */
package server.life;

import java.awt.Point;
import java.util.concurrent.atomic.AtomicBoolean;
import server.Randomizer;
import server.life.MapleMonster;
import server.life.MonsterListener;
import server.life.Spawns;
import server.maps.MapleMap;
import tools.MaplePacketCreator;

public class SpawnPointAreaBoss
extends Spawns {
    private MapleMonster monster;
    private Point pos1;
    private Point pos2;
    private Point pos3;
    private long nextPossibleSpawn;
    private int mobTime;
    private AtomicBoolean spawned = new AtomicBoolean(false);
    private String msg;

    public SpawnPointAreaBoss(MapleMonster monster, Point pos1, Point pos2, Point pos3, int mobTime, String msg) {
        this.monster = monster;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.pos3 = pos3;
        this.mobTime = mobTime < 0 ? -1 : mobTime * 1000;
        this.msg = msg;
        this.nextPossibleSpawn = System.currentTimeMillis();
    }

    @Override
    public final MapleMonster getMonster() {
        return this.monster;
    }

    @Override
    public final byte getCarnivalTeam() {
        return -1;
    }

    @Override
    public final int getCarnivalId() {
        return -1;
    }

    @Override
    public final boolean shouldSpawn() {
        if (this.mobTime < 0) {
            return false;
        }
        if (this.spawned.get()) {
            return false;
        }
        return this.nextPossibleSpawn <= System.currentTimeMillis();
    }

    @Override
    public final Point getPosition() {
        int rand = Randomizer.nextInt(3);
        return rand == 0 ? this.pos1 : (rand == 1 ? this.pos2 : this.pos3);
    }

    @Override
    public final MapleMonster spawnMonster(MapleMap map) {
        MapleMonster mob2 = new MapleMonster(this.monster);
        mob2.setPosition(this.getPosition());
        this.spawned.set(true);
        mob2.addListener(new MonsterListener(){

            @Override
            public void monsterKilled() {
                SpawnPointAreaBoss.this.nextPossibleSpawn = System.currentTimeMillis();
                if (SpawnPointAreaBoss.this.mobTime > 0) {
                    SpawnPointAreaBoss.this.nextPossibleSpawn = SpawnPointAreaBoss.this.nextPossibleSpawn + (long)SpawnPointAreaBoss.this.mobTime;
                }
                SpawnPointAreaBoss.this.spawned.set(false);
            }
        });
        map.spawnMonster(mob2, -2);
        if (this.msg != null) {
            map.broadcastMessage(MaplePacketCreator.serverNotice(6, this.msg));
        }
        return mob2;
    }

    @Override
    public final int getMobTime() {
        return this.mobTime;
    }
}

