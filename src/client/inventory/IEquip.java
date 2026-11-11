/*
 * Decompiled with CFR 0.152.
 */
package client.inventory;

import client.inventory.IItem;

public interface IEquip
extends IItem {
    public static final int ARMOR_RATIO = 350000;
    public static final int WEAPON_RATIO = 700000;

    public byte getUpgradeSlots();

    public byte getLevel();

    public byte getViciousHammer();

    public int getItemEXP();

    public int getExpPercentage();

    public int getEquipLevel();

    public int getEquipExp();

    public int getEquipExpForLevel();

    public int getBaseLevel();

    public short getStr();

    public short getDex();

    public short getInt();

    public short getLuk();

    public short getHp();

    public short getMp();

    public short getWatk();

    public short getMatk();

    public short getWdef();

    public short getMdef();

    public short getAcc();

    public short getAvoid();

    public short getHands();

    public short getSpeed();

    public short getJump();

    public int getDurability();

    public byte getEnhance();

    public byte getState();

    public short getPotential1();

    public short getPotential2();

    public short getPotential3();

    public short getHpR();

    public short getMpR();

    public static enum ScrollResult {
        SUCCESS,
        FAIL,
        CURSE;

    }
}

