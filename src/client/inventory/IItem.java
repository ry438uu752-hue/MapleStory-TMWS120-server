/*
 * Decompiled with CFR 0.152.
 */
package client.inventory;

import client.inventory.MaplePet;
import client.inventory.MapleRing;

public interface IItem
extends Comparable<IItem> {
    public byte getType();

    public short getPosition();

    public byte getFlag();

    public short getQuantity();

    public String getOwner();

    public String getGMLog();

    public int getItemId();

    public MaplePet getPet();

    public int getUniqueId();

    public long getInventoryId();

    public int getExp();

    public void setExp(int var1);

    public IItem copy();

    public long getExpiration();

    public void setFlag(byte var1);

    public void setUniqueId(int var1);

    public void setInventoryId(long var1);

    public void setPosition(short var1);

    public void setExpiration(long var1);

    public void setOwner(String var1);

    public void setGMLog(String var1);

    public void setQuantity(short var1);

    public void setGiftFrom(String var1);

    public String getGiftFrom();

    public MapleRing getRing();
}

