/*
 * Decompiled with CFR 0.152.
 */
package tools;

import java.util.List;
import tools.Pair;

public class AttackPair {
    public int objectid;
    public List<Pair<Integer, Boolean>> attack;

    public AttackPair(int objectid, List<Pair<Integer, Boolean>> attack) {
        this.objectid = objectid;
        this.attack = attack;
    }
}

