/*
 * Decompiled with CFR 0.152.
 */
package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.IItem;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import java.util.Arrays;
import scripting.NPCScriptManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleTrade;
import server.maps.FieldLimitType;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.shops.HiredMerchant;
import server.shops.IMaplePlayerShop;
import server.shops.MapleMiniGame;
import server.shops.MaplePlayerShop;
import server.shops.MaplePlayerShopItem;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.PlayerShopPacket;

public class PlayerInteractionHandler {
    private static final byte CREATE = 0;
    private static final byte INVITE_TRADE = 2;
    private static final byte DENY_TRADE = 3;
    private static final byte VISIT = 4;
    private static final byte CHAT = 6;
    private static final byte EXIT = 10;
    private static final byte OPEN = 11;
    private static final byte SET_ITEMS = 14;
    private static final byte SET_MESO = 15;
    private static final byte CONFIRM_TRADE = 16;
    private static final byte TRADE_SOMETHING = 19;
    private static final byte PLAYER_SHOP_ADD_ITEM = 20;
    private static final byte BUY_ITEM_PLAYER_SHOP = 21;
    private static final byte MERCHANT_EXIT = 28;
    private static final byte ADD_ITEM = 31;
    private static final byte BUY_ITEM_STORE = 32;
    private static final byte BUY_ITEM_HIREDMERCHANT = 34;
    private static final byte REMOVE_ITEM = 36;
    private static final byte MAINTANCE_OFF = 37;
    private static final byte MAINTANCE_ORGANISE = 38;
    private static final byte CLOSE_MERCHANT = 39;
    private static final byte MERCHANT_GETMESO = 40;
    private static final byte ADMIN_STORE_NAMECHANGE = 43;
    private static final byte VIEW_MERCHANT_VISITOR = 44;
    private static final byte VIEW_MERCHANT_BLACKLIST = 45;
    private static final byte MERCHANT_BLACKLIST_ADD = 46;
    private static final byte MERCHANT_BLACKLIST_REMOVE = 47;
    private static final byte REQUEST_TIE = 48;
    private static final byte ANSWER_TIE = 49;
    private static final byte GIVE_UP = 50;
    private static final byte REQUEST_REDO = 52;
    private static final byte ANSWER_REDO = 53;
    private static final byte EXIT_AFTER_GAME = 54;
    private static final byte CANCEL_EXIT = 55;
    private static final byte READY = 56;
    private static final byte UN_READY = 57;
    private static final byte EXPEL = 58;
    private static final byte START = 59;
    private static final byte SKIP = 61;
    private static final byte MOVE_OMOK = 62;
    private static final byte SELECT_CARD = 66;

    public static final void PlayerInteraction(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        byte action = slea.readByte();
        switch (action) {
            case 0: {
                byte createType = slea.readByte();
                if (createType == 3) {
                    MapleTrade.startTrade(chr);
                    break;
                }
                if (createType != 1 && createType != 2 && createType != 4 && createType != 5) break;
                if (createType == 4 && !chr.isAdmin()) {
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    return;
                }
                if (chr.getMap().getMapObjectsInRange(chr.getPosition(), 20000.0, Arrays.asList(MapleMapObjectType.SHOP, MapleMapObjectType.HIRED_MERCHANT)).size() != 0) {
                    chr.dropMessage(1, "You may not establish a store here.");
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    return;
                }
                if ((createType == 1 || createType == 2) && FieldLimitType.Minigames.check(chr.getMap().getFieldLimit())) {
                    chr.dropMessage(1, "You may not use minigames here.");
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    return;
                }
                String desc = slea.readMapleAsciiString();
                String pass = "";
                if (slea.readByte() > 0 && (createType == 1 || createType == 2)) {
                    pass = slea.readMapleAsciiString();
                }
                if (createType == 1 || createType == 2) {
                    int itemId;
                    byte piece = slea.readByte();
                    int n = itemId = createType == 1 ? 4080000 + piece : 4080100;
                    if (!chr.haveItem(itemId) || c.getPlayer().getMapId() >= 910000001 && c.getPlayer().getMapId() <= 910000022) {
                        return;
                    }
                    MapleMiniGame game = new MapleMiniGame(chr, itemId, desc, pass, createType);
                    game.setPieceType(piece);
                    chr.setPlayerShop(game);
                    game.setAvailable(true);
                    game.setOpen(true);
                    game.send(c);
                    chr.getMap().addMapObject(game);
                    game.update();
                    break;
                }
                IItem shop = c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte)slea.readShort());
                if (shop == null || shop.getQuantity() <= 0 || shop.getItemId() != slea.readInt() || c.getPlayer().getMapId() < 910000001 || c.getPlayer().getMapId() > 910000022) {
                    return;
                }
                if (createType == 4) {
                    MaplePlayerShop mps = new MaplePlayerShop(chr, shop.getItemId(), desc);
                    chr.setPlayerShop(mps);
                    chr.getMap().addMapObject(mps);
                    c.getSession().write((Object)PlayerShopPacket.getPlayerStore(chr, true));
                    break;
                }
                HiredMerchant merch = new HiredMerchant(chr, shop.getItemId(), desc);
                chr.setPlayerShop(merch);
                chr.getMap().addMapObject(merch);
                c.getSession().write((Object)PlayerShopPacket.getHiredMerch(chr, merch, true));
                break;
            }
            case 2: {
                MapleTrade.inviteTrade(chr, chr.getMap().getCharacterById(slea.readInt()));
                break;
            }
            case 3: {
                MapleTrade.declineTrade(chr);
                break;
            }
            case 4: {
                if (chr.getTrade() != null && chr.getTrade().getPartner() != null) {
                    MapleTrade.visitTrade(chr, chr.getTrade().getPartner().getChr());
                    break;
                }
                if (chr.getMap() == null) break;
                int obid = slea.readInt();
                MapleMapObject ob = chr.getMap().getMapObject(obid, MapleMapObjectType.HIRED_MERCHANT);
                if (ob == null) {
                    ob = chr.getMap().getMapObject(obid, MapleMapObjectType.SHOP);
                }
                if (!(ob instanceof IMaplePlayerShop) || chr.getPlayerShop() != null) break;
                IMaplePlayerShop ips = (IMaplePlayerShop)((Object)ob);
                if (ob instanceof HiredMerchant) {
                    HiredMerchant merchant = (HiredMerchant)ips;
                    if (merchant.isOwner(chr)) {
                        merchant.setOpen(false);
                        merchant.removeAllVisitors(16, 0);
                        chr.setPlayerShop(ips);
                        c.getSession().write((Object)PlayerShopPacket.getHiredMerch(chr, merchant, false));
                        merchant.SendMsg(c);
                        break;
                    }
                    if (!merchant.isOpen() || !merchant.isAvailable()) {
                        chr.dropMessage(1, "\u9019\u500b\u5546\u5e97\u5728\u6574\u7406\u6216\u8005\u662f\u6c92\u518d\u8ca9\u8ce3\u6771\u897f");
                        break;
                    }
                    if (ips.getFreeSlot() == -1) {
                        chr.dropMessage(1, "\u5546\u5e97\u4eba\u6578\u5df2\u7d93\u6eff\u4e86,\u8acb\u7a0d\u5f8c\u518d\u9032\u5165");
                        break;
                    }
                    if (merchant.isInBlackList(chr.getName())) {
                        chr.dropMessage(1, "\u4f60\u88ab\u9019\u5bb6\u5546\u5e97\u52a0\u5165\u9ed1\u540d\u55ae\u4e86,\u6240\u4ee5\u4e0d\u80fd\u9032\u5165");
                        break;
                    }
                    chr.setPlayerShop(ips);
                    merchant.addVisitor(chr);
                    c.getSession().write((Object)PlayerShopPacket.getHiredMerch(chr, merchant, false));
                    merchant.SendMsg(c);
                    break;
                }
                if (ips instanceof MaplePlayerShop && ((MaplePlayerShop)ips).isBanned(chr.getName())) {
                    chr.dropMessage(1, "\u4f60\u88ab\u9019\u5bb6\u5546\u5e97\u52a0\u5165\u9ed1\u540d\u55ae\u4e86,\u6240\u4ee5\u4e0d\u80fd\u9032\u5165.");
                    return;
                }
                if (ips.getFreeSlot() < 0 || ips.getVisitorSlot(chr) > -1 || !ips.isOpen() || !ips.isAvailable()) {
                    c.getSession().write((Object)PlayerShopPacket.getMiniGameFull());
                    break;
                }
                if (slea.available() > 0L && slea.readByte() > 0) {
                    String pass = slea.readMapleAsciiString();
                    if (!pass.equals(ips.getPassword())) {
                        c.getPlayer().dropMessage(1, "\u4f60\u8f38\u5165\u7684\u5bc6\u78bc\u932f\u8aa4,\u8acb\u91cd\u65b0\u518d\u8a66\u4e00\u6b21.");
                        return;
                    }
                } else if (ips.getPassword().length() > 0) {
                    c.getPlayer().dropMessage(1, "\u4f60\u8f38\u5165\u7684\u5bc6\u78bc\u932f\u8aa4,\u8acb\u91cd\u65b0\u518d\u8a66\u4e00\u6b21.");
                    return;
                }
                chr.setPlayerShop(ips);
                ips.addVisitor(chr);
                if (ips instanceof MapleMiniGame) {
                    ((MapleMiniGame)ips).send(c);
                    break;
                }
                c.getSession().write((Object)PlayerShopPacket.getPlayerStore(chr, false));
                break;
            }
            case 6: {
                slea.readInt();
                if (chr.getTrade() != null) {
                    chr.getTrade().chat(slea.readMapleAsciiString());
                    break;
                }
                if (chr.getPlayerShop() == null) break;
                IMaplePlayerShop ips = chr.getPlayerShop();
                String Msg = slea.readMapleAsciiString();
                ips.broadcastToVisitors(PlayerShopPacket.shopChat(chr.getName() + " : " + Msg, ips.getVisitorSlot(chr)));
                if (ips.getShopType() != 1) break;
                ((HiredMerchant)ips).addMsg(chr.getName() + " : " + Msg, ips.getVisitorSlot(chr));
                break;
            }
            case 10: {
                if (chr.getTrade() != null) {
                    MapleTrade.cancelTrade(chr.getTrade(), chr.getClient());
                    break;
                }
                IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips == null) {
                    return;
                }
                if (!ips.isAvailable() || ips.isOwner(chr) && ips.getShopType() != 1) {
                    ips.closeShop(false, ips.isAvailable());
                } else {
                    ips.removeVisitor(chr);
                }
                chr.setPlayerShop(null);
                NPCScriptManager.getInstance().dispose(c);
                c.getSession().write((Object)MaplePacketCreator.enableActions());
                break;
            }
            case 11: {
                IMaplePlayerShop shop = chr.getPlayerShop();
                if (shop == null || !shop.isOwner(chr) || shop.getShopType() >= 3) break;
                if (chr.getMap().allowPersonalShop()) {
                    if (c.getChannelServer().isShutdown()) {
                        chr.dropMessage(1, "\u4f3a\u670d\u5668\u5373\u5c07\u95dc\u9589\u6240\u4ee5\u4e0d\u80fd\u6574\u7406\u5546\u5e97.");
                        c.getSession().write((Object)MaplePacketCreator.enableActions());
                        shop.closeShop(shop.getShopType() == 1, false);
                        return;
                    }
                    if (shop.getShopType() == 1) {
                        HiredMerchant merchant = (HiredMerchant)shop;
                        merchant.setStoreid(c.getChannelServer().addMerchant(merchant));
                        merchant.setOpen(true);
                        merchant.setAvailable(true);
                        chr.getMap().broadcastMessage(PlayerShopPacket.spawnHiredMerchant(merchant));
                        chr.setPlayerShop(null);
                        break;
                    }
                    if (shop.getShopType() != 2) break;
                    shop.setOpen(true);
                    shop.setAvailable(true);
                    shop.update();
                    break;
                }
                c.getSession().close();
                break;
            }
            case 14: {
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                MapleInventoryType ivType = MapleInventoryType.getByType(slea.readByte());
                IItem item = chr.getInventory(ivType).getItem((byte)slea.readShort());
                short quantity = slea.readShort();
                byte targetSlot = slea.readByte();
                if (chr.getTrade() == null || item == null || (quantity > item.getQuantity() || quantity < 0) && !GameConstants.isThrowingStar(item.getItemId()) && !GameConstants.isBullet(item.getItemId())) break;
                chr.getTrade().setItems(c, item, targetSlot, quantity);
                break;
            }
            case 15: {
                MapleTrade trade = chr.getTrade();
                if (trade == null) break;
                trade.setMeso(slea.readInt());
                break;
            }
            case 16: {
                if (chr.getTrade() == null) break;
                MapleTrade.completeTrade(chr);
                break;
            }
            case 28: {
                break;
            }
            case 20: 
            case 31: {
                MapleInventoryType type = MapleInventoryType.getByType(slea.readByte());
                byte slot = (byte)slea.readShort();
                short bundles = slea.readShort();
                short perBundle = slea.readShort();
                int price = slea.readInt();
                IItem ivItem = chr.getInventory(type).getItem(slot);
                if (price <= 0 || bundles <= 0 && !GameConstants.isRechargable(ivItem.getItemId()) || perBundle <= 0) {
                    return;
                }
                IMaplePlayerShop shop = chr.getPlayerShop();
                if (shop == null || !shop.isOwner(chr) || shop instanceof MapleMiniGame) {
                    return;
                }
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                if (ivItem == null) break;
                long check = bundles * perBundle;
                if (check > 32767L || check <= 0L) {
                    return;
                }
                short bundles_perbundle = (short)(bundles * perBundle);
                if (ivItem.getQuantity() < bundles_perbundle && (GameConstants.isRechargable(ivItem.getItemId()) || bundles_perbundle != 1)) break;
                byte flag = ivItem.getFlag();
                if (ItemFlag.UNTRADEABLE.check(flag) || ItemFlag.LOCK.check(flag)) {
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    return;
                }
                if ((ii.isDropRestricted(ivItem.getItemId()) || ii.isAccountShared(ivItem.getItemId())) && !ItemFlag.KARMA_EQ.check(flag) && !ItemFlag.KARMA_USE.check(flag)) {
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    return;
                }
                if (bundles_perbundle >= 50 && GameConstants.isUpgradeScroll(ivItem.getItemId())) {
                    c.setMonitored(true);
                }
                if (GameConstants.isThrowingStar(ivItem.getItemId()) || GameConstants.isBullet(ivItem.getItemId())) {
                    MapleInventoryManipulator.removeFromSlot(c, type, slot, ivItem.getQuantity(), true);
                    IItem sellItem = ivItem.copy();
                    shop.addItem(new MaplePlayerShopItem(sellItem, (short)1, price));
                } else {
                    MapleInventoryManipulator.removeFromSlot(c, type, slot, bundles_perbundle, true);
                    IItem sellItem = ivItem.copy();
                    sellItem.setQuantity(perBundle);
                    shop.addItem(new MaplePlayerShopItem(sellItem, bundles, price));
                }
                c.getSession().write((Object)PlayerShopPacket.shopItemUpdate(shop));
                break;
            }
            case 21: 
            case 32: 
            case 34: {
                if (chr.getTrade() != null) {
                    MapleTrade.completeTrade(chr);
                    break;
                }
                byte item = slea.readByte();
                short quantity = slea.readShort();
                IMaplePlayerShop shop = chr.getPlayerShop();
                if (shop == null || shop.isOwner(chr) || shop instanceof MapleMiniGame || item >= shop.getItems().size()) {
                    return;
                }
                MaplePlayerShopItem tobuy = shop.getItems().get(item);
                if (tobuy == null) {
                    return;
                }
                long check = tobuy.bundles * quantity;
                long check2 = tobuy.price * quantity;
                long check3 = tobuy.item.getQuantity() * quantity;
                if (check <= 0L || check2 > Integer.MAX_VALUE || check2 <= 0L || check3 > 32767L || check3 < 0L) {
                    return;
                }
                if (tobuy.bundles < quantity || tobuy.bundles % quantity != 0 && GameConstants.isEquip(tobuy.item.getItemId()) || (long)chr.getMeso() - check2 < 0L || (long)chr.getMeso() - check2 > Integer.MAX_VALUE || (long)shop.getMeso() + check2 < 0L || (long)shop.getMeso() + check2 > Integer.MAX_VALUE) {
                    return;
                }
                if (quantity >= 50 && tobuy.item.getItemId() == 2340000) {
                    c.setMonitored(true);
                }
                shop.buy(c, item, quantity);
                shop.broadcastToVisitors(PlayerShopPacket.shopItemUpdate(shop));
                break;
            }
            case 36: {
                short slot = slea.readShort();
                IMaplePlayerShop shop = chr.getPlayerShop();
                if (shop == null || !shop.isOwner(chr) || shop instanceof MapleMiniGame || shop.getItems().size() <= 0 || shop.getItems().size() <= slot || slot < 0) {
                    return;
                }
                MaplePlayerShopItem item = shop.getItems().get(slot);
                if (item != null && item.bundles > 0) {
                    IItem item_get = item.item.copy();
                    long check = item.bundles * item.item.getQuantity();
                    if (check <= 0L || check > 32767L) {
                        return;
                    }
                    item_get.setQuantity((short)check);
                    if (item_get.getQuantity() >= 50 && GameConstants.isUpgradeScroll(item.item.getItemId())) {
                        c.setMonitored(true);
                    }
                    if (MapleInventoryManipulator.checkSpace(c, item_get.getItemId(), item_get.getQuantity(), item_get.getOwner())) {
                        MapleInventoryManipulator.addFromDrop(c, item_get, false);
                        item.bundles = 0;
                        shop.removeFromSlot(slot);
                    }
                }
                c.getSession().write((Object)PlayerShopPacket.shopItemUpdate(shop));
                break;
            }
            case 37: {
                IMaplePlayerShop shop = chr.getPlayerShop();
                if (shop == null || !(shop instanceof HiredMerchant) || !shop.isOwner(chr)) break;
                if (shop.getItems().size() == 0) {
                    boolean save = false;
                    if (chr.getMeso() + shop.getMeso() < 0) {
                        save = true;
                    } else if (shop.getMeso() > 0) {
                        chr.gainMeso(shop.getMeso(), false);
                        shop.setMeso(0);
                    }
                    c.getSession().write((Object)PlayerShopPacket.shopErrorMessage(17, 0));
                    shop.removeAllVisitors(16, 0);
                    shop.closeShop(save, true);
                    break;
                }
                shop.setOpen(true);
                chr.setPlayerShop(null);
                break;
            }
            case 38: {
                IMaplePlayerShop imps = chr.getPlayerShop();
                if (imps == null || !imps.isOwner(chr) || imps instanceof MapleMiniGame) break;
                for (int i = 0; i < imps.getItems().size(); ++i) {
                    if (imps.getItems().get((int)i).bundles != 0) continue;
                    imps.getItems().remove(i);
                }
                if (chr.getMeso() + imps.getMeso() < 0) {
                    c.getSession().write((Object)PlayerShopPacket.shopItemUpdate(imps));
                    break;
                }
                chr.gainMeso(imps.getMeso(), false);
                imps.setMeso(0);
                c.getSession().write((Object)PlayerShopPacket.shopItemUpdate(imps));
                break;
            }
            case 39: {
                IMaplePlayerShop merchant = chr.getPlayerShop();
                if (merchant == null || merchant.getShopType() != 1 || !merchant.isOwner(chr)) break;
                boolean save = false;
                if (chr.getMeso() + merchant.getMeso() < 0) {
                    save = true;
                } else {
                    if (merchant.getMeso() > 0) {
                        chr.gainMeso(merchant.getMeso(), false);
                    }
                    merchant.setMeso(0);
                    if (merchant.getItems().size() > 0) {
                        for (MaplePlayerShopItem items : merchant.getItems()) {
                            if (items.bundles <= 0) continue;
                            IItem item_get = items.item.copy();
                            item_get.setQuantity((short)(items.bundles * items.item.getQuantity()));
                            if (MapleInventoryManipulator.addFromDrop(c, item_get, false)) {
                                items.bundles = 0;
                                continue;
                            }
                            save = true;
                            break;
                        }
                    }
                }
                if (save) {
                    c.getSession().write((Object)MaplePacketCreator.serverNotice(1, "\u8acb\u53bb\u627e\u5bcc\u862d\u5fb7\u91cc\u9818\u53d6\u4f60\u7684\u88dd\u5099\u548c\u6953\u5e63"));
                }
                merchant.removeAllVisitors(10, 1);
                c.getSession().write((Object)PlayerShopPacket.shopErrorMessage(21, 0));
                merchant.closeShop(save, true);
                chr.setPlayerShop(null);
                break;
            }
            case 40: {
                IMaplePlayerShop merchant = chr.getPlayerShop();
                System.out.println(merchant.getShopType());
                if (merchant == null || merchant.getShopType() != 1 || !merchant.isOwner(chr)) break;
                if (chr.getMeso() + merchant.getMeso() < 0) {
                    c.getSession().write((Object)MaplePacketCreator.serverNotice(1, "\u60a8\u8eab\u4e0a\u7684\u6953\u5e63\u592a\u591a\u4e86"));
                    break;
                }
                if (merchant.getMeso() > 0) {
                    chr.gainMeso(merchant.getMeso(), false);
                }
                merchant.setMeso(0);
                c.getSession().write((Object)PlayerShopPacket.shopItemUpdate(merchant));
                break;
            }
            case 19: 
            case 43: {
                break;
            }
            case 44: {
                IMaplePlayerShop merchant = chr.getPlayerShop();
                if (merchant == null || merchant.getShopType() != 1 || !merchant.isOwner(chr)) break;
                ((HiredMerchant)merchant).sendVisitor(c);
                break;
            }
            case 45: {
                IMaplePlayerShop merchant = chr.getPlayerShop();
                if (merchant == null || merchant.getShopType() != 1 || !merchant.isOwner(chr)) break;
                ((HiredMerchant)merchant).sendBlackList(c);
                break;
            }
            case 46: {
                IMaplePlayerShop merchant = chr.getPlayerShop();
                if (merchant == null || merchant.getShopType() != 1 || !merchant.isOwner(chr)) break;
                ((HiredMerchant)merchant).addBlackList(slea.readMapleAsciiString());
                break;
            }
            case 47: {
                IMaplePlayerShop merchant = chr.getPlayerShop();
                if (merchant == null || merchant.getShopType() != 1 || !merchant.isOwner(chr)) break;
                ((HiredMerchant)merchant).removeBlackList(slea.readMapleAsciiString());
                break;
            }
            case 50: {
                MapleMiniGame game;
                IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips == null || !(ips instanceof MapleMiniGame) || (game = (MapleMiniGame)ips).isOpen()) break;
                game.broadcastToVisitors(PlayerShopPacket.getMiniGameResult(game, 0, game.getVisitorSlot(chr)));
                game.nextLoser();
                game.setOpen(true);
                game.update();
                game.checkExitAfterGame();
                break;
            }
            case 58: {
                IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips == null || !(ips instanceof MapleMiniGame) || !((MapleMiniGame)ips).isOpen()) break;
                ips.removeAllVisitors(3, 1);
                break;
            }
            case 56: 
            case 57: {
                MapleMiniGame game;
                IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips == null || !(ips instanceof MapleMiniGame) || (game = (MapleMiniGame)ips).isOwner(chr) || !game.isOpen()) break;
                game.setReady(game.getVisitorSlot(chr));
                game.broadcastToVisitors(PlayerShopPacket.getMiniGameReady(game.isReady(game.getVisitorSlot(chr))));
                break;
            }
            case 59: {
                MapleMiniGame game;
                IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips == null || !(ips instanceof MapleMiniGame) || !(game = (MapleMiniGame)ips).isOwner(chr) || !game.isOpen()) break;
                for (int i = 1; i < ips.getSize(); ++i) {
                    if (game.isReady(i)) continue;
                    return;
                }
                game.setGameType();
                game.shuffleList();
                if (game.getGameType() == 1) {
                    game.broadcastToVisitors(PlayerShopPacket.getMiniGameStart(game.getLoser()));
                } else {
                    game.broadcastToVisitors(PlayerShopPacket.getMatchCardStart(game, game.getLoser()));
                }
                game.setOpen(false);
                game.update();
                break;
            }
            case 48: {
                MapleMiniGame game;
                IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips == null || !(ips instanceof MapleMiniGame) || (game = (MapleMiniGame)ips).isOpen()) break;
                if (game.isOwner(chr)) {
                    game.broadcastToVisitors(PlayerShopPacket.getMiniGameRequestTie(), false);
                } else {
                    game.getMCOwner().getClient().getSession().write((Object)PlayerShopPacket.getMiniGameRequestTie());
                }
                game.setRequestedTie(game.getVisitorSlot(chr));
                break;
            }
            case 49: {
                MapleMiniGame game;
                IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips == null || !(ips instanceof MapleMiniGame) || (game = (MapleMiniGame)ips).isOpen() || game.getRequestedTie() <= -1 || game.getRequestedTie() == game.getVisitorSlot(chr)) break;
                if (slea.readByte() > 0) {
                    game.broadcastToVisitors(PlayerShopPacket.getMiniGameResult(game, 1, game.getRequestedTie()));
                    game.nextLoser();
                    game.setOpen(true);
                    game.update();
                    game.checkExitAfterGame();
                } else {
                    game.broadcastToVisitors(PlayerShopPacket.getMiniGameDenyTie());
                }
                game.setRequestedTie(-1);
                break;
            }
            case 52: {
                MapleMiniGame game;
                IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips == null || !(ips instanceof MapleMiniGame) || (game = (MapleMiniGame)ips).isOpen()) break;
                if (game.isOwner(chr)) {
                    game.broadcastToVisitors(PlayerShopPacket.getMiniGameRequestREDO(), false);
                } else {
                    game.getMCOwner().getClient().getSession().write((Object)PlayerShopPacket.getMiniGameRequestREDO());
                }
                game.setRequestedTie(game.getVisitorSlot(chr));
                break;
            }
            case 53: {
                MapleMiniGame game;
                IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips == null || !(ips instanceof MapleMiniGame) || (game = (MapleMiniGame)ips).isOpen()) break;
                if (slea.readByte() > 0) {
                    ips.broadcastToVisitors(PlayerShopPacket.getMiniGameSkip1(ips.getVisitorSlot(chr)));
                    game.nextLoser();
                } else {
                    game.broadcastToVisitors(PlayerShopPacket.getMiniGameDenyTie());
                }
                game.setRequestedTie(-1);
                break;
            }
            case 61: {
                MapleMiniGame game;
                IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips == null || !(ips instanceof MapleMiniGame) || (game = (MapleMiniGame)ips).isOpen()) break;
                ips.broadcastToVisitors(PlayerShopPacket.getMiniGameSkip(ips.getVisitorSlot(chr)));
                game.nextLoser();
                break;
            }
            case 62: {
                MapleMiniGame game;
                IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips == null || !(ips instanceof MapleMiniGame) || (game = (MapleMiniGame)ips).isOpen()) break;
                game.setPiece(slea.readInt(), slea.readInt(), slea.readByte(), chr);
                break;
            }
            case 66: {
                MapleMiniGame game;
                IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips == null || !(ips instanceof MapleMiniGame) || (game = (MapleMiniGame)ips).isOpen()) break;
                if (slea.readByte() != game.getTurn()) {
                    game.broadcastToVisitors(PlayerShopPacket.shopChat("\u4e0d\u80fd\u653e\u5728\u901a\u904e " + chr.getName() + ". \u5931\u6557\u8005: " + game.getLoser() + " \u904a\u5ba2: " + game.getVisitorSlot(chr) + " \u662f\u5426\u70ba\u771f: " + game.getTurn(), game.getVisitorSlot(chr)));
                    return;
                }
                byte slot = slea.readByte();
                int turn = game.getTurn();
                int fs = game.getFirstSlot();
                if (turn == 1) {
                    game.setFirstSlot(slot);
                    if (game.isOwner(chr)) {
                        game.broadcastToVisitors(PlayerShopPacket.getMatchCardSelect(turn, slot, fs, turn), false);
                    } else {
                        game.getMCOwner().getClient().getSession().write((Object)PlayerShopPacket.getMatchCardSelect(turn, slot, fs, turn));
                    }
                    game.setTurn(0);
                    return;
                }
                if (fs > 0 && game.getCardId(fs + 1) == game.getCardId(slot + 1)) {
                    game.broadcastToVisitors(PlayerShopPacket.getMatchCardSelect(turn, slot, fs, game.isOwner(chr) ? 2 : 3));
                    game.setPoints(game.getVisitorSlot(chr));
                } else {
                    game.broadcastToVisitors(PlayerShopPacket.getMatchCardSelect(turn, slot, fs, game.isOwner(chr) ? 0 : 1));
                    game.nextLoser();
                }
                game.setTurn(1);
                game.setFirstSlot(0);
                break;
            }
            case 54: {
                MapleMiniGame game;
                IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips == null || !(ips instanceof MapleMiniGame) || (game = (MapleMiniGame)ips).isOpen()) break;
                game.broadcastToVisitors(PlayerShopPacket.getMiniGameResult(game, 0, game.getVisitorSlot(chr)));
                game.nextLoser();
                game.setOpen(true);
                game.update();
                game.checkExitAfterGame();
                break;
            }
            case 55: {
                MapleMiniGame game;
                IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips == null || !(ips instanceof MapleMiniGame) || (game = (MapleMiniGame)ips).isOpen()) break;
                game.setExitAfter(chr);
                game.broadcastToVisitors(PlayerShopPacket.getMiniGameExitAfter(game.isExitAfter(chr)));
                break;
            }
        }
    }
}

