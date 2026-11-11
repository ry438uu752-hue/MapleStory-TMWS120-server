/*
 * Decompiled with CFR 0.152.
 */
package server;

import client.inventory.IItem;

public class MapleDueyActions {
    private String sender = null;
    private IItem item = null;
    private int mesos = 0;
    private int quantity = 1;
    private long sentTime;
    private int packageId = 0;

    public MapleDueyActions(int pId, IItem item) {
        this.item = item;
        this.quantity = item.getQuantity();
        this.packageId = pId;
    }

    public MapleDueyActions(int pId) {
        this.packageId = pId;
    }

    public String getSender() {
        return this.sender;
    }

    public void setSender(String name) {
        this.sender = name;
    }

    public IItem getItem() {
        return this.item;
    }

    public int getMesos() {
        return this.mesos;
    }

    public void setMesos(int set) {
        this.mesos = set;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public int getPackageId() {
        return this.packageId;
    }

    public void setSentTime(long sentTime) {
        this.sentTime = sentTime;
    }

    public long getSentTime() {
        return this.sentTime;
    }
}

