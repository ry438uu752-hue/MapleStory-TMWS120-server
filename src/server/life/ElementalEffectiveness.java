/*
 * Decompiled with CFR 0.152.
 */
package server.life;

public enum ElementalEffectiveness {
    NORMAL,
    IMMUNE,
    STRONG,
    WEAK;


    public static ElementalEffectiveness getByNumber(int num) {
        switch (num) {
            case 1: {
                return IMMUNE;
            }
            case 2: {
                return STRONG;
            }
            case 3: {
                return WEAK;
            }
        }
        throw new IllegalArgumentException("Unkown effectiveness: " + num);
    }
}

