/*
 * Decompiled with CFR 0.152.
 */
package server.life;

import java.awt.Point;
import server.life.MapleMonster;
import server.maps.MapleMap;

public abstract class Spawns {
    public abstract MapleMonster getMonster();

    public abstract byte getCarnivalTeam();

    public abstract boolean shouldSpawn();

    public abstract int getCarnivalId();

    public abstract MapleMonster spawnMonster(MapleMap var1);

    public abstract int getMobTime();

    public abstract Point getPosition();
}

