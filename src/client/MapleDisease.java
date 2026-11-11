/*
 * Decompiled with CFR 0.152.
 */
package client;

import java.io.Serializable;
import server.Randomizer;

public enum MapleDisease implements Serializable
{
    POTION(0x80000000000L, true),
    SHADOW(0x100000000000L, true),
    BLIND(0x200000000000L, true),
    FREEZE(0x8000000000000L, true),
    SLOW(1L),
    MORPH(2L),
    SEDUCE(128L),
    ZOMBIFY(16384L),
    REVERSE_DIRECTION(524288L),
    WEIRD_FLAME(0x8000000L),
    STUN(0x2000000000000L),
    POISON(0x4000000000000L),
    SEAL(0x8000000000000L),
    DARKNESS(0x10000000000000L),
    WEAKEN(0x4000000000000000L),
    CURSE(Long.MIN_VALUE);

    private static final long serialVersionUID = 0L;
    private long i;
    private boolean first;

    private MapleDisease(long i) {
        this.i = i;
        this.first = false;
    }

    private MapleDisease(long i, boolean first) {
        this.i = i;
        this.first = first;
    }

    public boolean isFirst() {
        return this.first;
    }

    public long getValue() {
        return this.i;
    }

    public static final MapleDisease getRandom() {
        MapleDisease[] mapleDiseaseArray = MapleDisease.values();
        while (true) {
            for (int n2 = 0; n2 < mapleDiseaseArray.length; ++n2) {
                MapleDisease dis = mapleDiseaseArray[n2];
                if (Randomizer.nextInt(mapleDiseaseArray.length) == 0) {
                    return dis;
                }
            }
        }
    }

    public static final MapleDisease getBySkill(int skill) {
        switch (skill) {
            case 120: {
                return SEAL;
            }
            case 121: {
                return DARKNESS;
            }
            case 122: {
                return WEAKEN;
            }
            case 123: {
                return STUN;
            }
            case 124: {
                return CURSE;
            }
            case 125: {
                return POISON;
            }
            case 126: {
                return SLOW;
            }
            case 128: {
                return SEDUCE;
            }
            case 132: {
                return REVERSE_DIRECTION;
            }
            case 133: {
                return ZOMBIFY;
            }
            case 134: {
                return POTION;
            }
            case 135: {
                return SHADOW;
            }
            case 136: {
                return BLIND;
            }
            case 137: {
                return FREEZE;
            }
        }
        return null;
    }

    public static final int getByDisease(MapleDisease skill) {
        switch (skill) {
            case SEAL: {
                return 120;
            }
            case DARKNESS: {
                return 121;
            }
            case WEAKEN: {
                return 122;
            }
            case STUN: {
                return 123;
            }
            case CURSE: {
                return 124;
            }
            case POISON: {
                return 125;
            }
            case SLOW: {
                return 126;
            }
            case SEDUCE: {
                return 128;
            }
            case REVERSE_DIRECTION: {
                return 132;
            }
            case ZOMBIFY: {
                return 133;
            }
            case POTION: {
                return 134;
            }
            case SHADOW: {
                return 135;
            }
            case BLIND: {
                return 136;
            }
            case FREEZE: {
                return 137;
            }
        }
        return 0;
    }
}

