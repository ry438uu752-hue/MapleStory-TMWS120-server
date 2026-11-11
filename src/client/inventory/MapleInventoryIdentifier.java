/*
 * Decompiled with CFR 0.152.
 */
package client.inventory;

import database.DatabaseConnection;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MapleInventoryIdentifier
implements Serializable {
    private static final long serialVersionUID = 21830921831301L;
    private AtomicInteger runningUID;
    private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private Lock readLock = this.rwl.readLock();
    private Lock writeLock = this.rwl.writeLock();
    private static MapleInventoryIdentifier instance = new MapleInventoryIdentifier();

    public MapleInventoryIdentifier() {
        this.runningUID = new AtomicInteger(0);
        this.getNextUniqueId();
    }

    public static int getInstance() {
        return instance.getNextUniqueId();
    }

    public int getNextUniqueId() {
        if (this.grabRunningUID() <= 0) {
            this.setRunningUID(this.initUID());
        }
        this.incrementRunningUID();
        return this.grabRunningUID();
    }

    public int grabRunningUID() {
        this.readLock.lock();
        try {
            int n = this.runningUID.get();
            return n;
        }
        finally {
            this.readLock.unlock();
        }
    }

    public void incrementRunningUID() {
        this.setRunningUID(this.grabRunningUID() + 1);
    }

    public void setRunningUID(int rUID) {
        if (rUID < this.grabRunningUID()) {
            return;
        }
        this.writeLock.lock();
        try {
            this.runningUID.set(rUID);
        }
        finally {
            this.writeLock.unlock();
        }
    }

    public int initUID() {
        int ret = 0;
        if (this.grabRunningUID() > 0) {
            return this.grabRunningUID();
        }
        try {
            int[] ids = new int[4];
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT MAX(uniqueid) FROM inventoryitems");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ids[0] = rs.getInt(1) + 1;
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("SELECT MAX(petid) FROM pets");
            rs = ps.executeQuery();
            if (rs.next()) {
                ids[1] = rs.getInt(1) + 1;
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("SELECT MAX(ringid) FROM rings");
            rs = ps.executeQuery();
            if (rs.next()) {
                ids[2] = rs.getInt(1) + 1;
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("SELECT MAX(partnerringid) FROM rings");
            rs = ps.executeQuery();
            if (rs.next()) {
                ids[3] = rs.getInt(1) + 1;
            }
            rs.close();
            ps.close();
            for (int i = 0; i < 4; ++i) {
                if (ids[i] <= ret) continue;
                ret = ids[i];
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}

