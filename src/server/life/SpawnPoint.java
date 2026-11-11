/*
 * Decompiled with CFR 0.152.
 */
package server.life;

import java.awt.Point;
import java.util.concurrent.atomic.AtomicInteger;
import server.MapleCarnivalFactory;
import server.life.MapleMonster;
import server.life.MonsterListener;
import server.life.Spawns;
import server.maps.MapleMap;
import server.maps.MapleReactor;
import tools.MaplePacketCreator;

public class SpawnPoint
extends Spawns {
    private MapleMonster monster;
    private Point pos;
    private long nextPossibleSpawn;
    private int mobTime;
    private int carnival = -1;
    private AtomicInteger spawnedMonsters = new AtomicInteger(0);
    private boolean immobile;
    private String msg;
    private byte carnivalTeam;

    public SpawnPoint(MapleMonster monster, Point pos, int mobTime, byte carnivalTeam, String msg) {
        this.monster = monster;
        this.pos = pos;
        this.mobTime = mobTime < 0 ? -1 : mobTime * 1000;
        this.carnivalTeam = carnivalTeam;
        this.msg = msg;
        this.immobile = !monster.getStats().getMobile();
        this.nextPossibleSpawn = System.currentTimeMillis();
    }

    public final void setCarnival(int c) {
        this.carnival = c;
    }

    @Override
    public final Point getPosition() {
        return this.pos;
    }

    @Override
    public final MapleMonster getMonster() {
        return this.monster;
    }

    @Override
    public final byte getCarnivalTeam() {
        return this.carnivalTeam;
    }

    @Override
    public final int getCarnivalId() {
        return this.carnival;
    }

    @Override
    public final boolean shouldSpawn() {
        if (this.mobTime < 0) {
            return false;
        }
        if ((this.mobTime != 0 || this.immobile) && this.spawnedMonsters.get() > 0 || this.spawnedMonsters.get() > 1) {
            return false;
        }
        return this.nextPossibleSpawn <= System.currentTimeMillis();
    }

    @Override
    public final MapleMonster spawnMonster(MapleMap map) {
        MapleMonster mob2 = new MapleMonster(this.monster);
        mob2.setPosition(this.pos);
        mob2.setCarnivalTeam(this.carnivalTeam);
        this.spawnedMonsters.incrementAndGet();
        mob2.addListener(new MonsterListener(){

            @Override
            public void monsterKilled() {
                SpawnPoint.this.nextPossibleSpawn = System.currentTimeMillis();
                if (SpawnPoint.this.mobTime > 0) {
                    SpawnPoint.this.nextPossibleSpawn = SpawnPoint.this.nextPossibleSpawn + (long)SpawnPoint.this.mobTime;
                }
                SpawnPoint.this.spawnedMonsters.decrementAndGet();
            }
        });
        map.spawnMonster(mob2, -2);
        if (this.carnivalTeam > -1) {
            for (MapleReactor r : map.getAllReactorsThreadsafe()) {
                if (!r.getName().startsWith(String.valueOf(this.carnivalTeam)) || r.getReactorId() != 9980000 + this.carnivalTeam || r.getState() >= 5) continue;
                int num = Integer.parseInt(r.getName().substring(1, 2));
                MapleCarnivalFactory.MCSkill skil = MapleCarnivalFactory.getInstance().getGuardian(num);
                if (skil == null) continue;
                skil.getSkill().applyEffect(null, mob2, false);
            }
        }
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

