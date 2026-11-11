/*
 * Decompiled with CFR 0.152.
 */
package handling.cashshop.handler;

import client.MapleClient;
import client.inventory.Equip;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import server.MTSCart;
import server.MTSStorage;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.MTSCSPacket;

public class MTSOperation {
    /*
     * Enabled aggressive block sorting
     */
    public static void MTSOperation(SeekableLittleEndianAccessor slea, MapleClient c) {
        MTSCart cart;
        block31: {
            byte op;
            block38: {
                block37: {
                    block36: {
                        block35: {
                            block34: {
                                block33: {
                                    block32: {
                                        Equip eq;
                                        cart = MTSStorage.getInstance().getCart(c.getPlayer().getId());
                                        if (slea.available() <= 0L) {
                                            MTSOperation.doMTSPackets(cart, c);
                                            return;
                                        }
                                        op = slea.readByte();
                                        if (op != 1) break block32;
                                        byte invType = slea.readByte();
                                        if (invType != 1 && invType != 2) {
                                            c.getSession().write((Object)MTSCSPacket.getMTSFailSell());
                                            MTSOperation.doMTSPackets(cart, c);
                                            return;
                                        }
                                        int itemid = slea.readInt();
                                        if (slea.readByte() != 0) {
                                            c.getSession().write((Object)MTSCSPacket.getMTSFailSell());
                                            MTSOperation.doMTSPackets(cart, c);
                                            return;
                                        }
                                        slea.skip(8);
                                        short stars = 1;
                                        short quantity = 1;
                                        byte slot = 0;
                                        if (invType == 1) {
                                            slea.skip(32);
                                        } else {
                                            stars = (short)slea.readInt();
                                        }
                                        slea.readMapleAsciiString();
                                        if (invType == 1) {
                                            slea.skip(48);
                                            slot = (byte)slea.readInt();
                                            slea.skip(4);
                                        } else {
                                            slea.readShort();
                                            if (GameConstants.isThrowingStar(itemid) || GameConstants.isBullet(itemid)) {
                                                slea.skip(8);
                                            }
                                            slot = (byte)slea.readInt();
                                            if (GameConstants.isThrowingStar(itemid) || GameConstants.isBullet(itemid)) {
                                                quantity = stars;
                                                slea.skip(4);
                                            } else {
                                                quantity = (short)slea.readInt();
                                            }
                                        }
                                        int price = slea.readInt();
                                        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                                        MapleInventoryType type = GameConstants.getInventoryType(itemid);
                                        IItem item = c.getPlayer().getInventory(type).getItem(slot).copy();
                                        if (ii.isCash(itemid) || quantity <= 0 || item == null || item.getQuantity() <= 0 || item.getItemId() != itemid || item.getUniqueId() > 0 || item.getQuantity() < quantity || price < 110 || c.getPlayer().getMeso() < 5000 || cart.getNotYetSold().size() >= 10 || ii.isDropRestricted(itemid) || ii.isAccountShared(itemid) || item.getExpiration() > -1L || item.getFlag() > 0) {
                                            c.getSession().write((Object)MTSCSPacket.getMTSFailSell());
                                            MTSOperation.doMTSPackets(cart, c);
                                            return;
                                        }
                                        if (type == MapleInventoryType.EQUIP && ((eq = (Equip)item).getState() > 0 || eq.getEnhance() > 0 || eq.getDurability() > -1)) {
                                            c.getSession().write((Object)MTSCSPacket.getMTSFailSell());
                                            MTSOperation.doMTSPackets(cart, c);
                                            return;
                                        }
                                        if (quantity >= 50 && GameConstants.isUpgradeScroll(item.getItemId())) {
                                            c.setMonitored(true);
                                        }
                                        long expiration = System.currentTimeMillis() + 604800000L;
                                        item.setQuantity(quantity);
                                        MTSStorage.getInstance().addToBuyNow(cart, item, price, c.getPlayer().getId(), c.getPlayer().getName(), expiration);
                                        MapleInventoryManipulator.removeFromSlot(c, type, slot, quantity, false);
                                        c.getPlayer().gainMeso(-5000, false);
                                        c.getSession().write((Object)MTSCSPacket.getMTSConfirmSell());
                                        break block31;
                                    }
                                    if (op != 4) break block33;
                                    cart.changeInfo(slea.readInt(), slea.readInt(), slea.readInt());
                                    break block31;
                                }
                                if (op != 6) break block34;
                                if (MTSStorage.getInstance().removeFromBuyNow(slea.readInt(), c.getPlayer().getId(), true)) {
                                    c.getSession().write((Object)MTSCSPacket.getMTSConfirmCancel());
                                    MTSOperation.sendMTSPackets(cart, c, true);
                                    return;
                                }
                                c.getSession().write((Object)MTSCSPacket.getMTSFailCancel());
                                break block31;
                            }
                            if (op != 7) break block35;
                            int id = Integer.MAX_VALUE - slea.readInt();
                            if (id >= cart.getInventory().size()) {
                                c.getPlayer().dropMessage(1, "Please try it again later.");
                                MTSOperation.sendMTSPackets(cart, c, true);
                                return;
                            }
                            IItem item = cart.getInventory().get(id);
                            if (item != null && item.getQuantity() > 0 && MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
                                IItem item_ = item.copy();
                                short pos = MapleInventoryManipulator.addbyItem(c, item_, true);
                                if (pos >= 0) {
                                    if (item_.getPet() != null) {
                                        item_.getPet().setInventoryPosition(pos);
                                        c.getPlayer().addPet(item_.getPet());
                                    }
                                    cart.removeFromInventory(item);
                                    c.getSession().write((Object)MTSCSPacket.getMTSConfirmTransfer(item_.getQuantity(), pos));
                                    return;
                                }
                                c.getSession().write((Object)MTSCSPacket.getMTSFailBuy());
                                break block31;
                            } else {
                                c.getSession().write((Object)MTSCSPacket.getMTSFailBuy());
                            }
                            break block31;
                        }
                        if (op != 8) break block36;
                        int id = slea.readInt();
                        if (MTSStorage.getInstance().checkCart(id, c.getPlayer().getId()) && cart.addToCart(id)) {
                            c.getSession().write((Object)MTSCSPacket.addToCartMessage(false, false));
                            break block31;
                        } else {
                            c.getSession().write((Object)MTSCSPacket.addToCartMessage(true, false));
                        }
                        break block31;
                    }
                    if (op != 9) break block37;
                    int id = slea.readInt();
                    if (cart.getCart().contains(id)) {
                        cart.removeFromCart(id);
                        c.getSession().write((Object)MTSCSPacket.addToCartMessage(false, true));
                        break block31;
                    } else {
                        c.getSession().write((Object)MTSCSPacket.addToCartMessage(true, true));
                    }
                    break block31;
                }
                if (op != 15 && op != 16) break block38;
                MTSStorage.MTSItemInfo mts = MTSStorage.getInstance().getSingleItem(slea.readInt());
                if (mts != null && mts.getCharacterId() != c.getPlayer().getId()) {
                    if (c.getPlayer().getCSPoints(2) > mts.getRealPrice()) {
                        if (MTSStorage.getInstance().removeFromBuyNow(mts.getId(), c.getPlayer().getId(), false)) {
                            c.getPlayer().modifyCSPoints(2, -mts.getRealPrice(), false);
                            MTSStorage.getInstance().getCart(mts.getCharacterId()).increaseOwedNX(mts.getPrice());
                            c.getSession().write((Object)MTSCSPacket.getMTSConfirmBuy());
                            MTSOperation.sendMTSPackets(cart, c, true);
                            return;
                        }
                        c.getSession().write((Object)MTSCSPacket.getMTSFailBuy());
                        break block31;
                    } else {
                        c.getSession().write((Object)MTSCSPacket.getMTSFailBuy());
                    }
                    break block31;
                } else {
                    c.getSession().write((Object)MTSCSPacket.getMTSFailBuy());
                }
                break block31;
            }
            if (op == 3 || op == 17) {
                c.getPlayer().dropMessage(1, "\u6b64\u529f\u80fd\u66ab\u4e0d\u958b\u653e");
            } else if (c.getPlayer().isAdmin()) {
                // empty if block
            }
        }
        MTSOperation.doMTSPackets(cart, c);
    }

    public static void MTSUpdate(MTSCart cart, MapleClient c) {
        c.getPlayer().modifyCSPoints(2, MTSStorage.getInstance().getCart(c.getPlayer().getId()).getSetOwedNX(), false);
        c.getSession().write((Object)MTSCSPacket.getMTSWantedListingOver(0, 0));
        MTSOperation.doMTSPackets(cart, c);
    }

    private static void doMTSPackets(MTSCart cart, MapleClient c) {
        MTSOperation.sendMTSPackets(cart, c, false);
    }

    private static void sendMTSPackets(MTSCart cart, MapleClient c, boolean changed) {
        c.getSession().write((Object)MTSStorage.getInstance().getCurrentMTS(cart));
        c.getSession().write((Object)MTSStorage.getInstance().getCurrentNotYetSold(cart));
        c.getSession().write((Object)MTSStorage.getInstance().getCurrentTransfer(cart, changed));
        c.getSession().write((Object)MTSCSPacket.showMTSCash(c.getPlayer()));
        c.getSession().write((Object)MTSCSPacket.enableCSUse());
        MTSStorage.getInstance().checkExpirations();
    }
}

