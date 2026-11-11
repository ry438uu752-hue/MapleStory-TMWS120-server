/*
 * Decompiled with CFR 0.152.
 */
package client.inventory;

import client.inventory.IItem;
import client.inventory.InventoryException;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapleInventory
implements Iterable<IItem>,
Serializable {
    private Map<Short, IItem> inventory = new LinkedHashMap<Short, IItem>();
    private byte slotLimit = 0;
    private MapleInventoryType type;

    public MapleInventory(MapleInventoryType type) {
        this.type = type;
    }

    public void addSlot(byte slot) {
        this.slotLimit = (byte)(this.slotLimit + slot);
        if (this.slotLimit > 96) {
            this.slotLimit = (byte)96;
        }
    }

    public byte getSlotLimit() {
        return this.slotLimit;
    }

    public void setSlotLimit(byte slot) {
        if (slot > 96) {
            slot = (byte)96;
        }
        this.slotLimit = slot;
    }

    public IItem findById(int itemId) {
        for (IItem item : this.inventory.values()) {
            if (item.getItemId() != itemId) continue;
            return item;
        }
        return null;
    }

    public IItem findByUniqueId(int itemId) {
        for (IItem item : this.inventory.values()) {
            if (item.getUniqueId() != itemId) continue;
            return item;
        }
        return null;
    }

    public IItem findByInventoryId(long itemId, int itemI) {
        for (IItem item : this.inventory.values()) {
            if (item.getInventoryId() != itemId || item.getItemId() != itemI) continue;
            return item;
        }
        return this.findById(itemI);
    }

    public IItem findByInventoryIdOnly(long itemId, int itemI) {
        for (IItem item : this.inventory.values()) {
            if (item.getInventoryId() != itemId || item.getItemId() != itemI) continue;
            return item;
        }
        return null;
    }

    public int countById(int itemId) {
        int possesed = 0;
        for (IItem item : this.inventory.values()) {
            if (item.getItemId() != itemId) continue;
            possesed += item.getQuantity();
        }
        return possesed;
    }

    public List<IItem> listById(int itemId) {
        ArrayList<IItem> ret = new ArrayList<IItem>();
        for (IItem item : this.inventory.values()) {
            if (item.getItemId() != itemId) continue;
            ret.add(item);
        }
        if (ret.size() > 1) {
            Collections.sort(ret);
        }
        return ret;
    }

    public Collection<IItem> list() {
        return this.inventory.values();
    }

    public short addItem(IItem item) {
        short slotId = this.getNextFreeSlot();
        if (slotId < 0) {
            return -1;
        }
        this.inventory.put(slotId, item);
        item.setPosition(slotId);
        return slotId;
    }

    public void addFromDB(IItem item) {
        if (item.getPosition() < 0 && !this.type.equals((Object)MapleInventoryType.EQUIPPED)) {
            return;
        }
        this.inventory.put(item.getPosition(), item);
    }

    public void move(short sSlot, short dSlot, short slotMax) {
        if (dSlot > this.slotLimit) {
            return;
        }
        Item source = (Item)this.inventory.get(sSlot);
        Item target = (Item)this.inventory.get(dSlot);
        if (source == null) {
            throw new InventoryException("Trying to move empty slot");
        }
        if (target == null) {
            source.setPosition(dSlot);
            this.inventory.put(dSlot, source);
            this.inventory.remove(sSlot);
        } else if (target.getItemId() == source.getItemId() && !GameConstants.isThrowingStar(source.getItemId()) && !GameConstants.isBullet(source.getItemId()) && target.getOwner().equals(source.getOwner()) && target.getExpiration() == source.getExpiration()) {
            if (this.type.getType() == MapleInventoryType.EQUIP.getType() || this.type.getType() == MapleInventoryType.CASH.getType()) {
                this.swap(target, source);
            } else if (source.getQuantity() + target.getQuantity() > slotMax) {
                source.setQuantity((short)(source.getQuantity() + target.getQuantity() - slotMax));
                target.setQuantity(slotMax);
            } else {
                target.setQuantity((short)(source.getQuantity() + target.getQuantity()));
                this.inventory.remove(sSlot);
            }
        } else {
            this.swap(target, source);
        }
    }

    private void swap(IItem source, IItem target) {
        this.inventory.remove(source.getPosition());
        this.inventory.remove(target.getPosition());
        short swapPos = source.getPosition();
        source.setPosition(target.getPosition());
        target.setPosition(swapPos);
        this.inventory.put(source.getPosition(), source);
        this.inventory.put(target.getPosition(), target);
    }

    public IItem getItem(short slot) {
        return this.inventory.get(slot);
    }

    public void removeItem(short slot) {
        this.removeItem(slot, (short)1, false);
    }

    public void removeItem(short slot, short quantity, boolean allowZero) {
        IItem item = this.inventory.get(slot);
        if (item == null) {
            return;
        }
        item.setQuantity((short)(item.getQuantity() - quantity));
        if (item.getQuantity() < 0) {
            item.setQuantity((short)0);
        }
        if (item.getQuantity() == 0 && !allowZero) {
            this.removeSlot(slot);
        }
    }

    public void removeSlot(short slot) {
        this.inventory.remove(slot);
    }

    public boolean isFull() {
        return this.inventory.size() >= this.slotLimit;
    }

    public boolean isFull(int margin) {
        return this.inventory.size() + margin >= this.slotLimit;
    }

    public short getNextFreeSlot() {
        if (this.isFull()) {
            return -1;
        }
        for (short i = 1; i <= this.slotLimit; i = (short)(i + 1)) {
            if (this.inventory.keySet().contains(i)) continue;
            return i;
        }
        return -1;
    }

    public short getNumFreeSlot() {
        if (this.isFull()) {
            return 0;
        }
        int free = 0;
        for (short i = 1; i <= this.slotLimit; i = (short)(i + 1)) {
            if (this.inventory.keySet().contains(i)) continue;
            free = (byte)(free + 1);
        }
        return (short)free;
    }

    public MapleInventoryType getType() {
        return this.type;
    }

    @Override
    public Iterator<IItem> iterator() {
        return Collections.unmodifiableCollection(this.inventory.values()).iterator();
    }
}

