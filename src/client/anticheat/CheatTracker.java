/*
 * Decompiled with CFR 0.152.
 */
package client.anticheat;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.anticheat.CheatingOffense;
import client.anticheat.CheatingOffenseEntry;
import client.anticheat.CheatingOffensePersister;
import constants.GameConstants;
import handling.world.World;
import java.awt.Point;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import server.AutobanManager;
import server.Timer;
import tools.MaplePacketCreator;
import tools.StringUtil;

public class CheatTracker {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock rL = this.lock.readLock();
    private final Lock wL = this.lock.writeLock();
    private final Map<CheatingOffense, CheatingOffenseEntry> offenses = new LinkedHashMap<CheatingOffense, CheatingOffenseEntry>();
    private final WeakReference<MapleCharacter> chr;
    private int lastAttackTickCount = 0;
    private byte Attack_tickResetCount = 0;
    private long Server_ClientAtkTickDiff = 0L;
    private long lastDamage = 0L;
    private long takingDamageSince;
    private int numSequentialDamage = 0;
    private long lastDamageTakenTime = 0L;
    private byte numZeroDamageTaken = 0;
    private int numSequentialSummonAttack = 0;
    private long summonSummonTime = 0L;
    private int numSameDamage = 0;
    private Point lastMonsterMove;
    private int monsterMoveCount;
    private int attacksWithoutHit = 0;
    private byte dropsPerSecond = 0;
    private long lastDropTime = 0L;
    private byte msgsPerSecond = 0;
    private long lastMsgTime = 0L;
    private ScheduledFuture<?> invalidationTask;
    private int gm_message = 100;
    private int lastTickCount = 0;
    private int tickSame = 0;
    private long lastASmegaTime = 0L;
    private long[] lastTime = new long[6];

    public CheatTracker(MapleCharacter chr) {
        this.chr = new WeakReference<MapleCharacter>(chr);
        this.invalidationTask = Timer.CheatTimer.getInstance().register(new InvalidationTask(), 60000L);
        this.takingDamageSince = System.currentTimeMillis();
    }

    public final void checkAttack(int skillId, int tickcount) {
        long STime_TC;
        short AtkDelay = GameConstants.getAttackDelay(skillId);
        if (tickcount - this.lastAttackTickCount < AtkDelay) {
            this.registerOffense(CheatingOffense.FASTATTACK);
        }
        if (this.Server_ClientAtkTickDiff - (STime_TC = System.currentTimeMillis() - (long)tickcount) > 450L) {
            this.registerOffense(CheatingOffense.FASTATTACK2);
        }
        this.Attack_tickResetCount = (byte)(this.Attack_tickResetCount + 1);
        if (this.Attack_tickResetCount >= (AtkDelay <= 200 ? (byte)2 : 4)) {
            this.Attack_tickResetCount = 0;
            this.Server_ClientAtkTickDiff = STime_TC;
        }
        ((MapleCharacter)this.chr.get()).updateTick(tickcount);
        this.lastAttackTickCount = tickcount;
    }

    public final void checkTakeDamage(int damage) {
        ++this.numSequentialDamage;
        this.lastDamageTakenTime = System.currentTimeMillis();
        if (this.lastDamageTakenTime - this.takingDamageSince / 500L < (long)this.numSequentialDamage) {
            // empty if block
        }
        if (this.lastDamageTakenTime - this.takingDamageSince > 4500L) {
            this.takingDamageSince = this.lastDamageTakenTime;
            this.numSequentialDamage = 0;
        }
        if (damage == 0) {
            this.numZeroDamageTaken = (byte)(this.numZeroDamageTaken + 1);
            if (this.numZeroDamageTaken >= 35) {
                this.numZeroDamageTaken = 0;
                this.registerOffense(CheatingOffense.HIGH_AVOID);
            }
        } else if (damage != -1) {
            this.numZeroDamageTaken = 0;
        }
    }

    public final void checkSameDamage(int dmg) {
        if (dmg > 2000 && this.lastDamage == (long)dmg) {
            ++this.numSameDamage;
            if (this.numSameDamage > 5) {
                this.numSameDamage = 0;
                this.registerOffense(CheatingOffense.SAME_DAMAGE, this.numSameDamage + " times: " + dmg);
            }
        } else {
            this.lastDamage = dmg;
            this.numSameDamage = 0;
        }
    }

    public final void checkMoveMonster(Point pos) {
        double dis = Math.abs(pos.distance(this.lastMonsterMove));
        if (pos == this.lastMonsterMove) {
            ++this.monsterMoveCount;
            if (this.monsterMoveCount > 15) {
                this.registerOffense(CheatingOffense.MOVE_MONSTERS);
            }
        } else if (dis > 1500.0) {
            ++this.monsterMoveCount;
            if (this.monsterMoveCount > 15) {
                this.registerOffense(CheatingOffense.MOVE_MONSTERS);
            }
        } else {
            this.lastMonsterMove = pos;
            this.monsterMoveCount = 1;
        }
    }

    public final void resetSummonAttack() {
        this.summonSummonTime = System.currentTimeMillis();
        this.numSequentialSummonAttack = 0;
    }

    public final boolean checkSummonAttack() {
        ++this.numSequentialSummonAttack;
        if ((System.currentTimeMillis() - this.summonSummonTime) / 2001L < (long)this.numSequentialSummonAttack) {
            this.registerOffense(CheatingOffense.FAST_SUMMON_ATTACK);
            return false;
        }
        return true;
    }

    public final void checkDrop() {
        this.checkDrop(false);
    }

    public final void checkDrop(boolean dc) {
        if (System.currentTimeMillis() - this.lastDropTime < 1000L) {
            this.dropsPerSecond = (byte)(this.dropsPerSecond + 1);
            if (this.dropsPerSecond >= (dc ? (byte)32 : 16) && this.chr.get() != null) {
                ((MapleCharacter)this.chr.get()).getClient().setMonitored(true);
            }
        } else {
            this.dropsPerSecond = 0;
        }
        this.lastDropTime = System.currentTimeMillis();
    }

    public boolean canAvatarSmega2() {
        if (this.lastASmegaTime + 10000L > System.currentTimeMillis() && this.chr.get() != null && !((MapleCharacter)this.chr.get()).isGM()) {
            return false;
        }
        this.lastASmegaTime = System.currentTimeMillis();
        return true;
    }

    public synchronized boolean GMSpam(int limit, int type) {
        if (type < 0 || this.lastTime.length < type) {
            type = 1;
        }
        if (System.currentTimeMillis() < (long)limit + this.lastTime[type]) {
            return true;
        }
        this.lastTime[type] = System.currentTimeMillis();
        return false;
    }

    public final void checkMsg() {
        this.msgsPerSecond = System.currentTimeMillis() - this.lastMsgTime < 1000L ? (byte)(this.msgsPerSecond + 1) : (byte)0;
        this.lastMsgTime = System.currentTimeMillis();
    }

    public final int getAttacksWithoutHit() {
        return this.attacksWithoutHit;
    }

    public final void setAttacksWithoutHit(boolean increase) {
        this.attacksWithoutHit = increase ? ++this.attacksWithoutHit : 0;
    }

    public final void registerOffense(CheatingOffense offense) {
        this.registerOffense(offense, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void registerOffense(CheatingOffense offense, String param) {
        MapleCharacter chrhardref = (MapleCharacter)this.chr.get();
        if (chrhardref == null || !offense.isEnabled() || chrhardref.isClone() || chrhardref.isGM()) {
            return;
        }
        CheatingOffenseEntry entry = null;
        this.rL.lock();
        try {
            entry = this.offenses.get((Object)offense);
        }
        finally {
            this.rL.unlock();
        }
        if (entry != null && entry.isExpired()) {
            this.expireEntry(entry);
            entry = null;
        }
        if (entry == null) {
            entry = new CheatingOffenseEntry(offense, chrhardref.getId());
        }
        if (param != null) {
            entry.setParam(param);
        }
        entry.incrementCount();
        if (offense.shouldAutoban(entry.getCount())) {
            byte type = offense.getBanType();
            if (type == 1) {
                AutobanManager.getInstance().autoban(chrhardref.getClient(), StringUtil.makeEnumHumanReadable(offense.name()));
            } else if (type == 2) {
                chrhardref.getClient().getSession().close();
            }
            this.gm_message = 100;
            return;
        }
        this.wL.lock();
        try {
            this.offenses.put(offense, entry);
        }
        finally {
            this.wL.unlock();
        }
        switch (offense) {
            case HIGH_DAMAGE_MAGIC: 
            case HIGH_DAMAGE_MAGIC_2: 
            case HIGH_DAMAGE: 
            case HIGH_DAMAGE_2: 
            case ATTACK_FARAWAY_MONSTER: 
            case ATTACK_FARAWAY_MONSTER_SUMMON: 
            case SAME_DAMAGE: {
                --this.gm_message;
                if (this.gm_message != 0) break;
                System.out.println(MapleCharacterUtil.makeMapleReadable(chrhardref.getName()) + "\u7591\u4f3c\u4f7f\u7528\u5916\u639b");
                World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[\u7ba1\u7406\u54e1\u8a0a\u606f] " + MapleCharacterUtil.makeMapleReadable(chrhardref.getName()) + " suspected of hacking! " + StringUtil.makeEnumHumanReadable(offense.name()) + (param == null ? "" : " - " + param)).getBytes());
                AutobanManager.getInstance().autoban(chrhardref.getClient(), StringUtil.makeEnumHumanReadable(offense.name()));
                this.gm_message = 100;
            }
        }
        CheatingOffensePersister.getInstance().persistEntry(entry);
    }

    public void updateTick(int newTick) {
        this.tickSame = newTick == this.lastTickCount ? ++this.tickSame : 0;
        this.lastTickCount = newTick;
    }

    public final void expireEntry(CheatingOffenseEntry coe) {
        this.wL.lock();
        try {
            this.offenses.remove((Object)coe.getOffense());
        }
        finally {
            this.wL.unlock();
        }
    }

    public final int getPoints() {
        CheatingOffenseEntry[] offenses_copy;
        int ret = 0;
        this.rL.lock();
        try {
            offenses_copy = this.offenses.values().toArray(new CheatingOffenseEntry[this.offenses.size()]);
        }
        finally {
            this.rL.unlock();
        }
        for (CheatingOffenseEntry entry : offenses_copy) {
            if (entry.isExpired()) {
                this.expireEntry(entry);
                continue;
            }
            ret += entry.getPoints();
        }
        return ret;
    }

    public final Map<CheatingOffense, CheatingOffenseEntry> getOffenses() {
        return Collections.unmodifiableMap(this.offenses);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final String getSummary() {
        StringBuilder ret = new StringBuilder();
        ArrayList<CheatingOffenseEntry> offenseList = new ArrayList<CheatingOffenseEntry>();
        this.rL.lock();
        try {
            for (CheatingOffenseEntry entry : this.offenses.values()) {
                if (entry.isExpired()) continue;
                offenseList.add(entry);
            }
        }
        finally {
            this.rL.unlock();
        }
        Collections.sort(offenseList, new Comparator<CheatingOffenseEntry>(){

            @Override
            public final int compare(CheatingOffenseEntry o1, CheatingOffenseEntry o2) {
                int anotherVal;
                int thisVal = o1.getPoints();
                return thisVal < (anotherVal = o2.getPoints()) ? 1 : (thisVal == anotherVal ? 0 : -1);
            }
        });
        int to = Math.min(offenseList.size(), 4);
        for (int x = 0; x < to; ++x) {
            ret.append(StringUtil.makeEnumHumanReadable(((CheatingOffenseEntry)offenseList.get(x)).getOffense().name()));
            ret.append(": ");
            ret.append(((CheatingOffenseEntry)offenseList.get(x)).getCount());
            if (x == to - 1) continue;
            ret.append(" ");
        }
        return ret.toString();
    }

    public final void dispose() {
        if (this.invalidationTask != null) {
            this.invalidationTask.cancel(false);
        }
        this.invalidationTask = null;
    }

    private final class InvalidationTask
    implements Runnable {
        private InvalidationTask() {
        }

        @Override
        public final void run() {
            CheatingOffenseEntry[] offenses_copy;
            CheatTracker.this.rL.lock();
            try {
                offenses_copy = CheatTracker.this.offenses.values().toArray(new CheatingOffenseEntry[CheatTracker.this.offenses.size()]);
            }
            finally {
                CheatTracker.this.rL.unlock();
            }
            for (CheatingOffenseEntry offense : offenses_copy) {
                if (!offense.isExpired()) continue;
                CheatTracker.this.expireEntry(offense);
            }
            if (CheatTracker.this.chr.get() == null) {
                CheatTracker.this.dispose();
            }
        }
    }
}

