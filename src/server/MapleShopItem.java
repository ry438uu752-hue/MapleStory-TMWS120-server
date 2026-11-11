/*
 * Decompiled with CFR 0.152.
 */
package server;

public class MapleShopItem {
    private short buyable;
    private int itemId;
    private int price;
    private int reqItem;
    private int reqItemQ;

    public MapleShopItem(short buyable, int itemId, int price, int reqItem, int reqItemQ) {
        this.buyable = buyable;
        this.itemId = itemId;
        this.price = price;
        this.reqItem = reqItem;
        this.reqItemQ = reqItemQ;
    }

    public short getBuyable() {
        return this.buyable;
    }

    public int getItemId() {
        return this.itemId;
    }

    public int getPrice() {
        return this.price;
    }

    public int getReqItem() {
        return this.reqItem;
    }

    public int getReqItemQ() {
        return this.reqItemQ;
    }
}

