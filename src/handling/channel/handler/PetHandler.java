/*
 * Decompiled with CFR 0.152.
 */
package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleDisease;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.PetCommand;
import client.inventory.PetDataFactory;
import constants.GameConstants;
import handling.channel.handler.InventoryHandler;
import handling.channel.handler.MovementParse;
import handling.world.MaplePartyCharacter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.life.MapleMonster;
import server.maps.FieldLimitType;
import server.maps.MapleMapItem;
import server.movement.LifeMovementFragment;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.PetPacket;

public class PetHandler {
    public static final void SpawnPet(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        chr.updateTick(slea.readInt());
        byte slot = slea.readByte();
        slea.skip(1);
        chr.spawnPet(slot, slea.readByte() > 0);
    }

    public static final void Pet_AutoPotion(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        slea.skip(13);
        byte slot = slea.readByte();
        if (chr == null || !chr.isAlive() || chr.getMapId() == 749040100 || chr.getMap() == null || chr.hasDisease(MapleDisease.POTION)) {
            return;
        }
        IItem toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        if (toUse == null || toUse.getQuantity() < 1) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        long time = System.currentTimeMillis();
        if (chr.getNextConsume() > time) {
            chr.dropMessage(5, "You may not use this item yet.");
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        if (!FieldLimitType.PotionUse.check(chr.getMap().getFieldLimit()) || chr.getMapId() == 610030600) {
            if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyTo(chr)) {
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                if (chr.getMap().getConsumeItemCoolTime() > 0) {
                    chr.setNextConsume(time + (long)(chr.getMap().getConsumeItemCoolTime() * 1000));
                }
            }
        } else {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
        }
    }

    public static final void PetChat(int petid, short command, String text, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null || chr.getPetIndex(petid) < 0) {
            return;
        }
        chr.getMap().broadcastMessage(chr, PetPacket.petChat(chr.getId(), command, text, chr.getPetIndex(petid)), true);
    }

    public static final void PetCommand(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        byte petIndex = chr.getPetIndex(slea.readInt());
        if (petIndex == -1) {
            return;
        }
        MaplePet pet = chr.getPet(petIndex);
        if (pet == null) {
            return;
        }
        slea.skip(5);
        byte command = slea.readByte();
        PetCommand petCommand = PetDataFactory.getPetCommand(pet.getPetItemId(), command);
        boolean success = false;
        if (Randomizer.nextInt(99) <= petCommand.getProbability()) {
            success = true;
            if (pet.getCloseness() < 30000) {
                int newCloseness = pet.getCloseness() + petCommand.getIncrease();
                if (newCloseness > 30000) {
                    newCloseness = 30000;
                }
                pet.setCloseness(newCloseness);
                if (newCloseness >= GameConstants.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                    pet.setLevel(pet.getLevel() + 1);
                    c.getSession().write((Object)PetPacket.showOwnPetLevelUp(petIndex));
                    chr.getMap().broadcastMessage(PetPacket.showPetLevelUp(chr, petIndex));
                }
                c.getSession().write((Object)PetPacket.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem((byte)pet.getInventoryPosition())));
            }
        }
        chr.getMap().broadcastMessage(chr, PetPacket.commandResponse(chr.getId(), command, petIndex, success, false), true);
    }

    public static final void PetFood(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        byte previousFullness = 100;
        MaplePet pet = null;
        if (chr == null) {
            return;
        }
        for (MaplePet pets : chr.getPets()) {
            if (!pets.getSummoned() || pets.getFullness() >= previousFullness) continue;
            previousFullness = pets.getFullness();
            pet = pets;
        }
        if (pet == null) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        slea.skip(6);
        int itemId = slea.readInt();
        boolean gainCloseness = false;
        if (Randomizer.nextInt(99) <= 50) {
            gainCloseness = true;
        }
        if (pet.getFullness() < 100) {
            int newFullness = pet.getFullness() + 30;
            if (newFullness > 100) {
                newFullness = 100;
            }
            pet.setFullness(newFullness);
            byte index = chr.getPetIndex(pet);
            if (gainCloseness && pet.getCloseness() < 30000) {
                int newCloseness = pet.getCloseness() + 1;
                if (newCloseness > 30000) {
                    newCloseness = 30000;
                }
                pet.setCloseness(newCloseness);
                if (newCloseness >= GameConstants.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                    pet.setLevel(pet.getLevel() + 1);
                    c.getSession().write((Object)PetPacket.showOwnPetLevelUp(index));
                    chr.getMap().broadcastMessage(PetPacket.showPetLevelUp(chr, index));
                }
            }
            c.getSession().write((Object)PetPacket.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem((byte)pet.getInventoryPosition())));
            chr.getMap().broadcastMessage(c.getPlayer(), PetPacket.commandResponse(chr.getId(), (byte)1, index, true, true), true);
        } else {
            if (gainCloseness) {
                int newCloseness = pet.getCloseness() - 1;
                if (newCloseness < 0) {
                    newCloseness = 0;
                }
                pet.setCloseness(newCloseness);
                if (newCloseness < GameConstants.getClosenessNeededForLevel(pet.getLevel())) {
                    pet.setLevel(pet.getLevel() - 1);
                }
            }
            c.getSession().write((Object)PetPacket.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem((byte)pet.getInventoryPosition())));
            chr.getMap().broadcastMessage(chr, PetPacket.commandResponse(chr.getId(), (byte)1, chr.getPetIndex(pet), false, true), true);
        }
        MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, itemId, 1, true, false);
        c.getSession().write((Object)MaplePacketCreator.enableActions());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static final void MovePet(SeekableLittleEndianAccessor slea, MapleCharacter chr) {
        int petId = (int)slea.readLong();
        slea.skip(8);
        List<LifeMovementFragment> res = MovementParse.parseMovement(slea, 3);
        if (res != null && chr != null && res.size() != 0) {
            byte slot = chr.getPetIndex(petId);
            if (slot == -1) {
                return;
            }
            chr.getPet(slot).updatePosition(res);
            chr.getMap().broadcastMessage(chr, PetPacket.movePet(chr.getId(), petId, slot, res), false);
            if (chr.getPlayerShop() != null || chr.getConversation() > 0 || chr.getTrade() != null) {
                return;
            }
            if (chr.getStat().hasVac && (chr.getStat().hasMeso || chr.getStat().hasItem)) {
                List<MapleMapItem> objects = chr.getMap().getAllItems();
                for (MapleMapItem mapitem : objects) {
                    Lock lock = mapitem.getLock();
                    lock.lock();
                    try {
                        if (mapitem.isPickedUp() || mapitem.getOwner() != chr.getId() && mapitem.isPlayerDrop() || mapitem.getOwner() != chr.getId() && (!mapitem.isPlayerDrop() && mapitem.getDropType() == 0 || mapitem.isPlayerDrop() && chr.getMap().getEverlast()) || !mapitem.isPlayerDrop() && mapitem.getDropType() == 1 && mapitem.getOwner() != chr.getId() && (chr.getParty() == null || chr.getParty().getMemberById(mapitem.getOwner()) == null)) continue;
                        if (mapitem.getMeso() > 0 && chr.getStat().hasMeso) {
                            if (chr.getParty() != null && mapitem.getOwner() != chr.getId()) {
                                LinkedList<MapleCharacter> toGive = new LinkedList<MapleCharacter>();
                                for (MaplePartyCharacter mem : chr.getParty().getMembers()) {
                                    MapleCharacter m = chr.getMap().getCharacterById(mem.getId());
                                    if (m == null) continue;
                                    toGive.add(m);
                                }
                                for (MapleCharacter m : toGive) {
                                    m.gainMeso(mapitem.getMeso() / toGive.size() + (m.getStat().hasPartyBonus ? (int)((double)mapitem.getMeso() / 20.0) : 0), true, true);
                                }
                            } else {
                                chr.gainMeso(mapitem.getMeso(), true, true);
                            }
                            InventoryHandler.removeItem_Pet(chr, mapitem, slot);
                            continue;
                        }
                        if (!chr.getStat().hasItem || !MapleItemInformationProvider.getInstance().isPickupBlocked(mapitem.getItem().getItemId())) continue;
                        if (InventoryHandler.useItem(chr.getClient(), mapitem.getItemId())) {
                            InventoryHandler.removeItem_Pet(chr, mapitem, slot);
                            continue;
                        }
                        if (!MapleInventoryManipulator.checkSpace(chr.getClient(), mapitem.getItem().getItemId(), mapitem.getItem().getQuantity(), mapitem.getItem().getOwner())) continue;
                        if (mapitem.getItem().getQuantity() >= 50 && GameConstants.isUpgradeScroll(mapitem.getItem().getItemId())) {
                            chr.getClient().setMonitored(true);
                        }
                        if (!MapleInventoryManipulator.addFromDrop(chr.getClient(), mapitem.getItem(), true, mapitem.getDropper() instanceof MapleMonster)) continue;
                        InventoryHandler.removeItem_Pet(chr, mapitem, slot);
                    }
                    finally {
                        lock.unlock();
                    }
                }
            }
        }
    }
}

