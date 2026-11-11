/*
 * Decompiled with CFR 0.152.
 */
package client.anticheat;

public enum CheatingOffense {
    FAST_SUMMON_ATTACK(5, 6000L, 10, 2),
    FASTATTACK(5, 6000L, 50, 3),
    FASTATTACK2(5, 9000L, 20, 3),
    MOVE_MONSTERS(1, 30000L),
    FAST_HP_MP_REGEN(5, 20000L, 10, 0),
    SAME_DAMAGE(2, 30000L, 150, 2),
    ATTACK_WITHOUT_GETTING_HIT(1, 30000L, 1200, 3),
    HIGH_DAMAGE_MAGIC(5, 30000L, -1, 3),
    HIGH_DAMAGE_MAGIC_2(10, 30000L, 2, 1),
    HIGH_DAMAGE(5, 30000L, -1, 3),
    HIGH_DAMAGE_2(10, 30000L, 2, 1),
    EXCEED_DAMAGE_CAP(5, 60000L, 800, 1),
    ATTACK_FARAWAY_MONSTER(5, 60000L, 150, 2),
    ATTACK_FARAWAY_MONSTER_SUMMON(5, 60000L, 200, 2),
    REGEN_HIGH_HP(1, 30000L, 1000, 2),
    REGEN_HIGH_MP(1, 30000L, 1000, 2),
    ITEMVAC_CLIENT(5, 5000L, 10, 3),
    ITEMVAC_SERVER(3, 5000L, 100, 3),
    PET_ITEMVAC_CLIENT(5, 10000L, 20, 3),
    PET_ITEMVAC_SERVER(3, 10000L, 100, 3),
    USING_FARAWAY_PORTAL(1, 60000L, 100, 0),
    AST_TAKE_DAMAG(1, 60000L, 100),
    HIGH_AVOID(20, 180000L, 100, 3),
    HIGH_JUMP(1, 60000L),
    MISMATCHING_BULLETCOUNT(1, 300000L),
    ETC_EXPLOSION(1, 300000L),
    ATTACKING_WHILE_DEAD(1, 300000L, -1, 1),
    USING_UNAVAILABLE_ITEM(1, 300000L),
    FAMING_SELF(1, 300000L),
    FAMING_UNDER_15(1, 0L),
    EXPLODING_NONEXISTANT(1, 300000L),
    SUMMON_HACK(1, 300000L, 50, 3),
    SUMMON_HACK_MOBS(1, 300000L, 50, 3),
    ARAN_COMBO_HACK(1, 600000L, 50),
    HEAL_ATTACKING_UNDEAD(20, 30000L, 100, 1);

    private final byte points;
    private final long validityDuration;
    private final int autobancount;
    private byte bantype = 0;

    public final byte getPoints() {
        return this.points;
    }

    public final long getValidityDuration() {
        return this.validityDuration;
    }

    public final boolean shouldAutoban(int count) {
        if (this.autobancount == -1) {
            return false;
        }
        return count >= this.autobancount;
    }

    public final byte getBanType() {
        return this.bantype;
    }

    public final void setEnabled(boolean enabled) {
        this.bantype = (byte)(enabled ? 1 : 0);
    }

    public final boolean isEnabled() {
        return this.bantype >= 1;
    }

    private CheatingOffense(int points, long validityDuration) {
        this(points, validityDuration, -1, 1);
    }

    private CheatingOffense(int points, long validityDuration, int autobancount) {
        this(points, validityDuration, autobancount, 1);
    }

    private CheatingOffense(int points, long validityDuration, int autobancount, int bantype) {
        this.points = (byte)points;
        this.validityDuration = validityDuration;
        this.autobancount = autobancount;
        this.bantype = (byte)bantype;
    }
}

