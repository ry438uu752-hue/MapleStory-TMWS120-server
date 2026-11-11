/*
 * Decompiled with CFR 0.152.
 */
package client.status;

import client.status.MonsterStatus;
import java.util.concurrent.ScheduledFuture;
import server.life.MobSkill;

public class MonsterStatusEffect {
    private MonsterStatus stati;
    private final int skill;
    private final MobSkill mobskill;
    private final boolean monsterSkill;
    private Integer x;
    private ScheduledFuture<?> cancelTask;
    private ScheduledFuture<?> poisonSchedule;
    private ScheduledFuture<?> poisonStatusSchedule;

    public MonsterStatusEffect(MonsterStatus stat, Integer x, int skillId, MobSkill mobskill, boolean monsterSkill) {
        this.stati = stat;
        this.skill = skillId;
        this.monsterSkill = monsterSkill;
        this.mobskill = mobskill;
        this.x = x;
    }

    public final MonsterStatus getStati() {
        return this.stati;
    }

    public final Integer getX() {
        return this.x;
    }

    public final void setValue(MonsterStatus status, Integer newVal) {
        this.stati = status;
        this.x = newVal;
    }

    public final int getSkill() {
        return this.skill;
    }

    public final MobSkill getMobSkill() {
        return this.mobskill;
    }

    public final boolean isMonsterSkill() {
        return this.monsterSkill;
    }

    public final void setCancelTask(ScheduledFuture<?> cancelTask) {
        this.cancelTask = cancelTask;
    }

    public final void setPoisonSchedule(ScheduledFuture<?> poisonSchedule) {
        this.poisonSchedule = poisonSchedule;
    }

    public final void setPoisonStatusSchedule(ScheduledFuture<?> poisonStatusSchedule) {
        this.poisonStatusSchedule = poisonStatusSchedule;
    }

    public final void cancelTask() {
        if (this.cancelTask != null) {
            this.cancelTask.cancel(false);
        }
        this.cancelTask = null;
    }

    public final void cancelPoisonSchedule() {
        if (this.poisonSchedule != null) {
            try {
                this.poisonSchedule.cancel(false);
            }
            catch (NullPointerException nullPointerException) {
                // empty catch block
            }
        }
        this.poisonSchedule = null;
        if (this.poisonStatusSchedule != null) {
            try {
                this.poisonStatusSchedule.cancel(false);
            }
            catch (NullPointerException nullPointerException) {
                // empty catch block
            }
        }
        this.poisonStatusSchedule = null;
    }
}

