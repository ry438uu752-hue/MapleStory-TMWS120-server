/*
 * Decompiled with CFR 0.152.
 */
package client;

public enum MapleStat {
    SKIN(1),
    FACE(2),
    HAIR(4),
    LEVEL(16),
    JOB(32),
    STR(64),
    DEX(128),
    INT(256),
    LUK(512),
    HP(1024),
    MAXHP(2048),
    MP(4096),
    MAXMP(8192),
    AVAILABLEAP(16384),
    AVAILABLESP(32768),
    EXP(65536),
    FAME(131072),
    MESO(262144),
    PET(0x180008),
    GACHAPONEXP(0x200000);

    private final int i;

    private MapleStat(int i) {
        this.i = i;
    }

    public int getValue() {
        return this.i;
    }

    public static final MapleStat getByValue(int value) {
        for (MapleStat stat : MapleStat.values()) {
            if (stat.i != value) continue;
            return stat;
        }
        return null;
    }

    public static enum Temp {
        STR(1),
        DEX(2),
        INT(4),
        LUK(8),
        WATK(16),
        WDEF(32),
        MATK(64),
        MDEF(128),
        ACC(256),
        AVOID(512),
        SPEED(1024),
        JUMP(2048);

        private final int i;

        private Temp(int i) {
            this.i = i;
        }

        public int getValue() {
            return this.i;
        }
    }
}

