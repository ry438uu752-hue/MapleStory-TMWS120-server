/*
 * Decompiled with CFR 0.152.
 */
package server;

import client.inventory.IItem;
import java.util.ArrayList;
import java.util.List;

public class MerchItemPackage {
    private long sentTime;
    private int mesos = 0;
    private int packageid;
    private List<IItem> items = new ArrayList<IItem>();

    public void setItems(List<IItem> items) {
        this.items = items;
    }

    public List<IItem> getItems() {
        return this.items;
    }

    public void setSentTime(long sentTime) {
        this.sentTime = sentTime;
    }

    public long getSentTime() {
        return this.sentTime;
    }

    public int getMesos() {
        return this.mesos;
    }

    public void setMesos(int set) {
        this.mesos = set;
    }

    public int getPackageid() {
        return this.packageid;
    }

    public void setPackageid(int packageid) {
        this.packageid = packageid;
    }
}

