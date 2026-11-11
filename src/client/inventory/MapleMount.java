/*
 * Decompiled with CFR 0.152.
 */
package client.inventory;

import client.MapleBuffStat;
import client.MapleCharacter;
import database.DatabaseConnection;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ScheduledFuture;
import server.Randomizer;
import server.Timer;
import tools.MaplePacketCreator;

public class MapleMount
implements Serializable {
    private static final long serialVersionUID = 9179541993413738569L;
    private int itemid;
    private int skillid;
    private int exp;
    private byte fatigue;
    private byte level;
    private transient boolean changed = false;
    private long lastFatigue = 0L;
    private transient ScheduledFuture<?> tirednessSchedule = null;
    private transient WeakReference<MapleCharacter> owner;

    public MapleMount(MapleCharacter owner, int id, int skillid, byte fatigue, byte level, int exp) {
        this.itemid = id;
        this.skillid = skillid;
        this.fatigue = fatigue;
        this.level = level;
        this.exp = exp;
        this.owner = new WeakReference<MapleCharacter>(owner);
    }

    public void saveMount(int charid) throws SQLException {
        if (!this.changed) {
            return;
        }
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("UPDATE mountdata set `Level` = ?, `Exp` = ?, `Fatigue` = ? WHERE characterid = ?");
        ps.setByte(1, this.level);
        ps.setInt(2, this.exp);
        ps.setByte(3, this.fatigue);
        ps.setInt(4, charid);
        ps.executeUpdate();
        ps.close();
    }

    public int getItemId() {
        return this.itemid;
    }

    public int getSkillId() {
        return this.skillid;
    }

    public byte getFatigue() {
        return this.fatigue;
    }

    public int getExp() {
        return this.exp;
    }

    public byte getLevel() {
        return this.level;
    }

    public void setItemId(int c) {
        this.changed = true;
        this.itemid = c;
    }

    public void setFatigue(byte amount) {
        this.changed = true;
        this.fatigue = (byte)(this.fatigue + amount);
        if (this.fatigue < 0) {
            this.fatigue = 0;
        }
    }

    public void setExp(int c) {
        this.changed = true;
        this.exp = c;
    }

    public void setLevel(byte c) {
        this.changed = true;
        this.level = c;
    }

    public void increaseFatigue() {
        this.changed = true;
        this.fatigue = (byte)(this.fatigue + 1);
        if (this.fatigue > 100 && this.owner.get() != null) {
            ((MapleCharacter)this.owner.get()).cancelEffectFromBuffStat(MapleBuffStat.MONSTER_RIDING);
        }
        this.update();
    }

    public final boolean canTire(long now) {
        return this.lastFatigue > 0L && this.lastFatigue + 30000L < now;
    }

    public void startSchedule() {
        Timer.BuffTimer timerManager = Timer.BuffTimer.getInstance();
        this.tirednessSchedule = timerManager.getInstance().register(new Runnable(){

            @Override
            public void run() {
                MapleMount.this.increaseFatigue();
            }
        }, 30000L, 30000L);
    }

    public void cancelSchedule() {
        if (this.tirednessSchedule != null) {
            this.tirednessSchedule.cancel(false);
        }
    }

    public void increaseExp() {
        int e = this.level >= 1 && this.level <= 7 ? Randomizer.nextInt(10) + 15 : (this.level >= 8 && this.level <= 15 ? Randomizer.nextInt(13) + 7 : (this.level >= 16 && this.level <= 24 ? Randomizer.nextInt(23) + 9 : Randomizer.nextInt(28) + 12));
        this.setExp(this.exp + e);
    }

    public void update() {
        MapleCharacter chr = (MapleCharacter)this.owner.get();
        if (chr != null) {
            chr.getMap().broadcastMessage(MaplePacketCreator.updateMount(chr, false));
        }
    }
}

