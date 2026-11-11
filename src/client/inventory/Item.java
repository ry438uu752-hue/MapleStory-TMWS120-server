/*
 * Decompiled with CFR 0.152.
 */
package client.inventory;

import client.inventory.IItem;
import client.inventory.MaplePet;
import client.inventory.MapleRing;
import constants.GameConstants;
import java.io.Serializable;

public class Item
implements IItem,
Serializable {
    private final int id;
    private short position;
    private short quantity;
    private byte flag;
    private int exp;
    private long expiration = -1L;
    private long inventoryitemid = 0L;
    private MaplePet pet = null;
    private int uniqueid = -1;
    private String owner = "";
    private String GameMaster_log = null;
    private String giftFrom = "";
    protected MapleRing ring = null;

    public Item(int id, short position, short quantity, byte flag, int uniqueid) {
        this.id = id;
        this.position = position;
        this.quantity = quantity;
        this.flag = flag;
        this.uniqueid = uniqueid;
    }

    public Item(int id, short position, short quantity, byte flag) {
        this.id = id;
        this.position = position;
        this.quantity = quantity;
        this.flag = flag;
    }

    public Item(int id, byte position, short quantity) {
        this.id = id;
        this.position = position;
        this.quantity = quantity;
    }

    @Override
    public IItem copy() {
        Item ret = new Item(this.id, this.position, this.quantity, this.flag, this.uniqueid);
        ret.pet = this.pet;
        ret.owner = this.owner;
        ret.GameMaster_log = this.GameMaster_log;
        ret.expiration = this.expiration;
        ret.giftFrom = this.giftFrom;
        return ret;
    }

    @Override
    public final void setPosition(short position) {
        this.position = position;
        if (this.pet != null) {
            this.pet.setInventoryPosition(position);
        }
    }

    @Override
    public void setQuantity(short quantity) {
        this.quantity = quantity;
    }

    @Override
    public final int getItemId() {
        return this.id;
    }

    @Override
    public final short getPosition() {
        return this.position;
    }

    @Override
    public final byte getFlag() {
        return this.flag;
    }

    @Override
    public final short getQuantity() {
        return this.quantity;
    }

    @Override
    public byte getType() {
        return 2;
    }

    @Override
    public final String getOwner() {
        return this.owner;
    }

    @Override
    public final void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public final void setFlag(byte flag) {
        this.flag = flag;
    }

    @Override
    public final long getExpiration() {
        return this.expiration;
    }

    @Override
    public final void setExpiration(long expire) {
        this.expiration = expire;
    }

    @Override
    public final String getGMLog() {
        return this.GameMaster_log;
    }

    @Override
    public void setGMLog(String GameMaster_log) {
        this.GameMaster_log = GameMaster_log;
    }

    @Override
    public final int getUniqueId() {
        return this.uniqueid;
    }

    @Override
    public final void setUniqueId(int id) {
        this.uniqueid = id;
    }

    @Override
    public final long getInventoryId() {
        return this.inventoryitemid;
    }

    @Override
    public void setInventoryId(long ui) {
        this.inventoryitemid = ui;
    }

    @Override
    public final MaplePet getPet() {
        return this.pet;
    }

    public final void setPet(MaplePet pet) {
        this.pet = pet;
    }

    @Override
    public void setGiftFrom(String gf) {
        this.giftFrom = gf;
    }

    @Override
    public String getGiftFrom() {
        return this.giftFrom;
    }

    @Override
    public int getExp() {
        return this.exp;
    }

    @Override
    public void setExp(int exp) {
        this.exp = exp;
    }

    @Override
    public int compareTo(IItem other) {
        if (Math.abs(this.position) < Math.abs(other.getPosition())) {
            return -1;
        }
        if (Math.abs(this.position) == Math.abs(other.getPosition())) {
            return 0;
        }
        return 1;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof IItem)) {
            return false;
        }
        IItem ite = (IItem)obj;
        return this.uniqueid == ite.getUniqueId() && this.id == ite.getItemId() && this.quantity == ite.getQuantity() && Math.abs(this.position) == Math.abs(ite.getPosition());
    }

    public String toString() {
        return "Item: " + this.id + " quantity: " + this.quantity;
    }

    @Override
    public MapleRing getRing() {
        if (!GameConstants.isEffectRing(this.id) || this.getUniqueId() <= 0) {
            return null;
        }
        if (this.ring == null) {
            this.ring = MapleRing.loadFromDb(this.getUniqueId(), this.position < 0);
        }
        return this.ring;
    }

    public void setRing(MapleRing ring) {
        this.ring = ring;
    }
}

