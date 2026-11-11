/*
 * Decompiled with CFR 0.152.
 */
package server.life;

import java.lang.ref.WeakReference;
import server.life.MapleMonster;

public class SummonAttackEntry {
    private WeakReference<MapleMonster> mob;
    private int damage;

    public SummonAttackEntry(MapleMonster mob2, int damage) {
        this.mob = new WeakReference<MapleMonster>(mob2);
        this.damage = damage;
    }

    public MapleMonster getMonster() {
        return (MapleMonster)this.mob.get();
    }

    public int getDamage() {
        return this.damage;
    }
}

