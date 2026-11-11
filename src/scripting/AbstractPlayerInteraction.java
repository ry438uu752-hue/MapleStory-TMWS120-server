/*
 * Decompiled with CFR 0.152.
 */
package scripting;

import client.ISkill;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleQuestStatus;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import constants.GameConstants;
import handling.channel.ChannelServer;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.World;
import handling.world.guild.MapleGuild;
import java.awt.Point;
import java.util.List;
import scripting.EventInstanceManager;
import scripting.EventManager;
import scripting.NPCScriptManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.maps.Event_DojoAgent;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleReactor;
import server.maps.SavedLocationType;
import server.quest.MapleQuest;
import tools.MaplePacketCreator;
import tools.packet.PetPacket;
import tools.packet.UIPacket;

public abstract class AbstractPlayerInteraction {
    private MapleClient c;

    public AbstractPlayerInteraction(MapleClient c) {
        this.c = c;
    }

    public final MapleClient getClient() {
        return this.c;
    }

    public final MapleClient getC() {
        return this.c;
    }

    public MapleCharacter getChar() {
        return this.c.getPlayer();
    }

    public final ChannelServer getChannelServer() {
        return this.c.getChannelServer();
    }

    public final MapleCharacter getPlayer() {
        return this.c.getPlayer();
    }

    public final EventManager getEventManager(String event) {
        return this.c.getChannelServer().getEventSM().getEventManager(event);
    }

    public final EventInstanceManager getEventInstance() {
        return this.c.getPlayer().getEventInstance();
    }

    public final void warp(int map) {
        MapleMap mapz = this.getWarpMap(map);
        try {
            this.c.getPlayer().changeMap(mapz, mapz.getPortal(Randomizer.nextInt(mapz.getPortals().size())));
        }
        catch (Exception e) {
            this.c.getPlayer().changeMap(mapz, mapz.getPortal(0));
        }
    }

    public final void warp_Instanced(int map) {
        MapleMap mapz = this.getMap_Instanced(map);
        try {
            this.c.getPlayer().changeMap(mapz, mapz.getPortal(Randomizer.nextInt(mapz.getPortals().size())));
        }
        catch (Exception e) {
            this.c.getPlayer().changeMap(mapz, mapz.getPortal(0));
        }
    }

    public final void warp(int map, int portal) {
        MapleMap mapz = this.getWarpMap(map);
        if (portal != 0 && map == this.c.getPlayer().getMapId()) {
            Point portalPos = new Point(this.c.getPlayer().getMap().getPortal(portal).getPosition());
            if (portalPos.distanceSq(this.getPlayer().getPosition()) < 90000.0) {
                this.c.getSession().write((Object)MaplePacketCreator.instantMapWarp((byte)portal));
                this.c.getPlayer().checkFollow();
                this.c.getPlayer().getMap().movePlayer(this.c.getPlayer(), portalPos);
            } else {
                this.c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
            }
        } else {
            this.c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
        }
    }

    public final void warpS(int map, int portal) {
        MapleMap mapz = this.getWarpMap(map);
        this.c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
    }

    public final void warp(int map, String portal) {
        MapleMap mapz = this.getWarpMap(map);
        if (map == 109060000 || map == 109060002 || map == 109060004) {
            portal = mapz.getSnowballPortal();
        }
        if (map == this.c.getPlayer().getMapId()) {
            Point portalPos = new Point(this.c.getPlayer().getMap().getPortal(portal).getPosition());
            if (portalPos.distanceSq(this.getPlayer().getPosition()) < 90000.0) {
                this.c.getPlayer().checkFollow();
                this.c.getSession().write((Object)MaplePacketCreator.instantMapWarp((byte)this.c.getPlayer().getMap().getPortal(portal).getId()));
                this.c.getPlayer().getMap().movePlayer(this.c.getPlayer(), new Point(this.c.getPlayer().getMap().getPortal(portal).getPosition()));
            } else {
                this.c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
            }
        } else {
            this.c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
        }
    }

    public final void warpS(int map, String portal) {
        MapleMap mapz = this.getWarpMap(map);
        if (map == 109060000 || map == 109060002 || map == 109060004) {
            portal = mapz.getSnowballPortal();
        }
        this.c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
    }

    public final void warpMap(int mapid, int portal) {
        MapleMap map = this.getMap(mapid);
        for (MapleCharacter chr : this.c.getPlayer().getMap().getCharactersThreadsafe()) {
            chr.changeMap(map, map.getPortal(portal));
        }
    }

    public final void playPortalSE() {
        this.c.getSession().write((Object)MaplePacketCreator.showOwnBuffEffect(0, 7));
    }

    private final MapleMap getWarpMap(int map) {
        return ChannelServer.getInstance(this.c.getChannel()).getMapFactory().getMap(map);
    }

    public final MapleMap getMap() {
        return this.c.getPlayer().getMap();
    }

    public final MapleMap getMap(int map) {
        return this.getWarpMap(map);
    }

    public final MapleMap getMap_Instanced(int map) {
        return this.c.getPlayer().getEventInstance() == null ? this.getMap(map) : this.c.getPlayer().getEventInstance().getMapInstance(map);
    }

    public void spawnMonster(int id, int qty) {
        this.spawnMob(id, qty, new Point(this.c.getPlayer().getPosition()));
    }

    public final void spawnMobOnMap(int id, int qty, int x, int y, int map) {
        for (int i = 0; i < qty; ++i) {
            this.getMap(map).spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), new Point(x, y));
        }
    }

    public final void spawnMob(int id, int qty, int x, int y) {
        this.spawnMob(id, qty, new Point(x, y));
    }

    public final void spawnMob(int id, int x, int y) {
        this.spawnMob(id, 1, new Point(x, y));
    }

    private final void spawnMob(int id, int qty, Point pos) {
        for (int i = 0; i < qty; ++i) {
            this.c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), pos);
        }
    }

    public final void killMob(int ids) {
        this.c.getPlayer().getMap().killMonster(ids);
    }

    public final void killAllMob() {
        this.c.getPlayer().getMap().killAllMonsters(true);
    }

    public final void addHP(int delta) {
        this.c.getPlayer().addHP(delta);
    }

    public final int getPlayerStat(String type) {
        if (type.equals("LVL")) {
            return this.c.getPlayer().getLevel();
        }
        if (type.equals("STR")) {
            return this.c.getPlayer().getStat().getStr();
        }
        if (type.equals("DEX")) {
            return this.c.getPlayer().getStat().getDex();
        }
        if (type.equals("INT")) {
            return this.c.getPlayer().getStat().getInt();
        }
        if (type.equals("LUK")) {
            return this.c.getPlayer().getStat().getLuk();
        }
        if (type.equals("HP")) {
            return this.c.getPlayer().getStat().getHp();
        }
        if (type.equals("MP")) {
            return this.c.getPlayer().getStat().getMp();
        }
        if (type.equals("MAXHP")) {
            return this.c.getPlayer().getStat().getMaxHp();
        }
        if (type.equals("MAXMP")) {
            return this.c.getPlayer().getStat().getMaxMp();
        }
        if (type.equals("RAP")) {
            return this.c.getPlayer().getRemainingAp();
        }
        if (type.equals("RSP")) {
            return this.c.getPlayer().getRemainingSp();
        }
        if (type.equals("GID")) {
            return this.c.getPlayer().getGuildId();
        }
        if (type.equals("GRANK")) {
            return this.c.getPlayer().getGuildRank();
        }
        if (type.equals("ARANK")) {
            return this.c.getPlayer().getAllianceRank();
        }
        if (type.equals("GM")) {
            return this.c.getPlayer().isGM() ? 1 : 0;
        }
        if (type.equals("ADMIN")) {
            return this.c.getPlayer().isAdmin() ? 1 : 0;
        }
        if (type.equals("GENDER")) {
            return this.c.getPlayer().getGender();
        }
        if (type.equals("FACE")) {
            return this.c.getPlayer().getFace();
        }
        if (type.equals("HAIR")) {
            return this.c.getPlayer().getHair();
        }
        return -1;
    }

    public final String getName() {
        return this.c.getPlayer().getName();
    }

    public final boolean haveItem(int itemid) {
        return this.haveItem(itemid, 1);
    }

    public final boolean haveItem(int itemid, int quantity) {
        return this.haveItem(itemid, quantity, false, true);
    }

    public final boolean haveItem(int itemid, int quantity, boolean checkEquipped, boolean greaterOrEquals) {
        return this.c.getPlayer().haveItem(itemid, quantity, checkEquipped, greaterOrEquals);
    }

    public final boolean canHold() {
        for (int i = 1; i <= 5; ++i) {
            if (this.c.getPlayer().getInventory(MapleInventoryType.getByType((byte)i)).getNextFreeSlot() > -1) continue;
            return false;
        }
        return true;
    }

    public final boolean canHold(int itemid) {
        return this.c.getPlayer().getInventory(GameConstants.getInventoryType(itemid)).getNextFreeSlot() > -1;
    }

    public final boolean canHold(int itemid, int quantity) {
        return MapleInventoryManipulator.checkSpace(this.c, itemid, quantity, "");
    }

    public final MapleQuestStatus getQuestRecord(int id) {
        return this.c.getPlayer().getQuestNAdd(MapleQuest.getInstance(id));
    }

    public final byte getQuestStatus(int id) {
        return this.c.getPlayer().getQuestStatus(id);
    }

    public final boolean isQuestActive(int id) {
        return this.getQuestStatus(id) == 1;
    }

    public final boolean isQuestFinished(int id) {
        return this.getQuestStatus(id) == 2;
    }

    public final void showQuestMsg(String msg) {
        this.c.getSession().write((Object)MaplePacketCreator.showQuestMsg(msg));
    }

    public final void forceStartQuest(int id, String data) {
        MapleQuest.getInstance(id).forceStart(this.c.getPlayer(), 0, data);
    }

    public final void forceStartQuest(int id, int data, boolean filler) {
        MapleQuest.getInstance(id).forceStart(this.c.getPlayer(), 0, filler ? String.valueOf(data) : null);
    }

    public void forceStartQuest(int id) {
        MapleQuest.getInstance(id).forceStart(this.c.getPlayer(), 0, null);
    }

    public void forceCompleteQuest(int id) {
        MapleQuest.getInstance(id).forceComplete(this.getPlayer(), 0);
    }

    public void spawnNpc(int npcId) {
        this.c.getPlayer().getMap().spawnNpc(npcId, this.c.getPlayer().getPosition());
    }

    public final void spawnNpc(int npcId, int x, int y) {
        this.c.getPlayer().getMap().spawnNpc(npcId, new Point(x, y));
    }

    public final void spawnNpc(int npcId, Point pos) {
        this.c.getPlayer().getMap().spawnNpc(npcId, pos);
    }

    public final void removeNpc(int mapid, int npcId) {
        this.c.getChannelServer().getMapFactory().getMap(mapid).removeNpc(npcId);
    }

    public final void forceStartReactor(int mapid, int id) {
        MapleMap map = this.c.getChannelServer().getMapFactory().getMap(mapid);
        for (MapleMapObject mapleMapObject : map.getAllReactorsThreadsafe()) {
            MapleReactor react = (MapleReactor)mapleMapObject;
            if (react.getReactorId() != id) continue;
            react.forceStartReactor(this.c);
            break;
        }
    }

    public final void destroyReactor(int mapid, int id) {
        MapleMap map = this.c.getChannelServer().getMapFactory().getMap(mapid);
        for (MapleMapObject mapleMapObject : map.getAllReactorsThreadsafe()) {
            MapleReactor react = (MapleReactor)mapleMapObject;
            if (react.getReactorId() != id) continue;
            react.hitReactor(this.c);
            break;
        }
    }

    public final void hitReactor(int mapid, int id) {
        MapleMap map = this.c.getChannelServer().getMapFactory().getMap(mapid);
        for (MapleMapObject mapleMapObject : map.getAllReactorsThreadsafe()) {
            MapleReactor react = (MapleReactor)mapleMapObject;
            if (react.getReactorId() != id) continue;
            react.hitReactor(this.c);
            break;
        }
    }

    public final int getJob() {
        return this.c.getPlayer().getJob();
    }

    public final void gainNX(int amount) {
        this.c.getPlayer().modifyCSPoints(1, amount, true);
    }

    public final void gainItemPeriod(int id, short quantity, int period) {
        this.gainItem(id, quantity, false, period, -1, "");
    }

    public final void gainItemPeriod(int id, short quantity, long period, String owner) {
        this.gainItem(id, quantity, false, period, -1, owner);
    }

    public final void gainItem(int id, short quantity) {
        this.gainItem(id, quantity, false, 0L, -1, "");
    }

    public final void gainItem(int id, short quantity, boolean randomStats) {
        this.gainItem(id, quantity, randomStats, 0L, -1, "");
    }

    public final void gainItem(int id, short quantity, boolean randomStats, int slots) {
        this.gainItem(id, quantity, randomStats, 0L, slots, "");
    }

    public final void gainItem(int id, short quantity, long period) {
        this.gainItem(id, quantity, false, period, -1, "");
    }

    public final void gainItem(int id, short quantity, boolean randomStats, long period, int slots) {
        this.gainItem(id, quantity, randomStats, period, slots, "");
    }

    public final void gainItem(int id, short quantity, boolean randomStats, long period, int slots, String owner) {
        this.gainItem(id, quantity, randomStats, period, slots, owner, this.c);
    }

    public final void gainItem(int id, short quantity, boolean randomStats, long period, int slots, String owner, MapleClient cg) {
        if (quantity >= 0) {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            MapleInventoryType type = GameConstants.getInventoryType(id);
            if (!MapleInventoryManipulator.checkSpace(cg, id, quantity, "")) {
                return;
            }
            if (type.equals((Object)MapleInventoryType.EQUIP) && !GameConstants.isThrowingStar(id) && !GameConstants.isBullet(id)) {
                Equip item = (Equip)(randomStats ? ii.randomizeStats((Equip)ii.getEquipById(id)) : ii.getEquipById(id));
                if (period > 0L) {
                    item.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
                }
                if (slots > 0) {
                    item.setUpgradeSlots((byte)(item.getUpgradeSlots() + slots));
                }
                if (owner != null) {
                    item.setOwner(owner);
                }
                String name = ii.getName(id);
                if (id / 10000 == 114 && name != null && name.length() > 0) {
                    String msg = "\u4f60\u5df2\u7372\u5f97\u7a31\u865f <" + name + ">";
                    cg.getPlayer().dropMessage(-1, msg);
                    cg.getPlayer().dropMessage(5, msg);
                }
                MapleInventoryManipulator.addbyItem(cg, item.copy());
            } else {
                MapleInventoryManipulator.addById(cg, id, quantity, owner == null ? "" : owner, null, period);
            }
        } else {
            MapleInventoryManipulator.removeById(cg, GameConstants.getInventoryType(id), id, -quantity, true, false);
        }
        cg.getSession().write((Object)MaplePacketCreator.getShowItemGain(id, quantity, true));
    }

    public final void changeMusic(String songName) {
        this.getPlayer().getMap().broadcastMessage(MaplePacketCreator.musicChange(songName));
    }

    public final void worldMessage(int type, String message) {
        World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(type, message).getBytes());
    }

    public final void playerMessage(String message) {
        this.playerMessage(5, message);
    }

    public final void mapMessage(String message) {
        this.mapMessage(5, message);
    }

    public final void guildMessage(String message) {
        this.guildMessage(5, message);
    }

    public final void playerMessage(int type, String message) {
        this.c.getSession().write((Object)MaplePacketCreator.serverNotice(type, message));
    }

    public final void mapMessage(int type, String message) {
        this.c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.serverNotice(type, message));
    }

    public final void guildMessage(int type, String message) {
        if (this.getPlayer().getGuildId() > 0) {
            World.Guild.guildPacket(this.getPlayer().getGuildId(), MaplePacketCreator.serverNotice(type, message));
        }
    }

    public final MapleGuild getGuild() {
        return this.getGuild(this.getPlayer().getGuildId());
    }

    public final MapleGuild getGuild(int guildid) {
        return World.Guild.getGuild(guildid);
    }

    public final MapleParty getParty() {
        return this.c.getPlayer().getParty();
    }

    public final int getCurrentPartyId(int mapid) {
        return this.getMap(mapid).getCurrentPartyId();
    }

    public final boolean isLeader() {
        if (this.getParty() == null) {
            return false;
        }
        return this.getParty().getLeader().getId() == this.c.getPlayer().getId();
    }

    public final boolean isAllPartyMembersAllowedJob(int job) {
        if (this.c.getPlayer().getParty() == null) {
            return false;
        }
        for (MaplePartyCharacter mem : this.c.getPlayer().getParty().getMembers()) {
            if (mem.getJobId() / 100 == job) continue;
            return false;
        }
        return true;
    }

    public final boolean allMembersHere() {
        if (this.c.getPlayer().getParty() == null) {
            return false;
        }
        for (MaplePartyCharacter mem : this.c.getPlayer().getParty().getMembers()) {
            MapleCharacter chr = this.c.getPlayer().getMap().getCharacterById(mem.getId());
            if (chr != null) continue;
            return false;
        }
        return true;
    }

    public final void warpParty(int mapId) {
        if (this.getPlayer().getParty() == null || this.getPlayer().getParty().getMembers().size() == 1) {
            this.warp(mapId, 0);
            return;
        }
        MapleMap target = this.getMap(mapId);
        int cMap = this.getPlayer().getMapId();
        for (MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = this.getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar == null || curChar.getMapId() != cMap && curChar.getEventInstance() != this.getPlayer().getEventInstance()) continue;
            curChar.changeMap(target, target.getPortal(0));
        }
    }

    public final void warpParty(int mapId, int portal) {
        if (this.getPlayer().getParty() == null || this.getPlayer().getParty().getMembers().size() == 1) {
            if (portal < 0) {
                this.warp(mapId);
            } else {
                this.warp(mapId, portal);
            }
            return;
        }
        boolean rand = portal < 0;
        MapleMap target = this.getMap(mapId);
        int cMap = this.getPlayer().getMapId();
        for (MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = this.getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar == null || curChar.getMapId() != cMap && curChar.getEventInstance() != this.getPlayer().getEventInstance()) continue;
            if (rand) {
                try {
                    curChar.changeMap(target, target.getPortal(Randomizer.nextInt(target.getPortals().size())));
                }
                catch (Exception e) {
                    curChar.changeMap(target, target.getPortal(0));
                }
                continue;
            }
            curChar.changeMap(target, target.getPortal(portal));
        }
    }

    public final void warpParty_Instanced(int mapId) {
        if (this.getPlayer().getParty() == null || this.getPlayer().getParty().getMembers().size() == 1) {
            this.warp_Instanced(mapId);
            return;
        }
        MapleMap target = this.getMap_Instanced(mapId);
        int cMap = this.getPlayer().getMapId();
        for (MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = this.getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar == null || curChar.getMapId() != cMap && curChar.getEventInstance() != this.getPlayer().getEventInstance()) continue;
            curChar.changeMap(target, target.getPortal(0));
        }
    }

    public void gainMeso(int gain) {
        this.c.getPlayer().gainMeso(gain, true, false, true);
    }

    public void gainExp(int gain) {
        this.c.getPlayer().gainExp(gain, true, true, true);
    }

    public void gainExpR(int gain) {
        this.c.getPlayer().gainExp(gain * this.c.getChannelServer().getExpRate(), true, true, true);
    }

    public final void givePartyItems(int id, short quantity, List<MapleCharacter> party) {
        for (MapleCharacter chr : party) {
            if (quantity >= 0) {
                MapleInventoryManipulator.addById(chr.getClient(), id, quantity);
            } else {
                MapleInventoryManipulator.removeById(chr.getClient(), GameConstants.getInventoryType(id), id, -quantity, true, false);
            }
            chr.getClient().getSession().write((Object)MaplePacketCreator.getShowItemGain(id, quantity, true));
        }
    }

    public final void givePartyItems(int id, short quantity) {
        this.givePartyItems(id, quantity, false);
    }

    public final void givePartyItems(int id, short quantity, boolean removeAll) {
        if (this.getPlayer().getParty() == null || this.getPlayer().getParty().getMembers().size() == 1) {
            this.gainItem(id, (short)(removeAll ? -this.getPlayer().itemQuantity(id) : quantity));
            return;
        }
        for (MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = this.getMap().getCharacterById(chr.getId());
            if (curChar == null) continue;
            this.gainItem(id, (short)(removeAll ? -curChar.itemQuantity(id) : quantity), false, 0L, 0, "", curChar.getClient());
        }
    }

    public final void givePartyExp(int amount, List<MapleCharacter> party) {
        for (MapleCharacter chr : party) {
            chr.gainExp(amount * this.c.getChannelServer().getExpRate(), true, true, true);
        }
    }

    public final void givePartyExp(int amount) {
        if (this.getPlayer().getParty() == null || this.getPlayer().getParty().getMembers().size() == 1) {
            this.gainExp(amount * this.c.getChannelServer().getExpRate());
            return;
        }
        for (MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = this.getMap().getCharacterById(chr.getId());
            if (curChar == null) continue;
            curChar.gainExp(amount * this.c.getChannelServer().getExpRate(), true, true, true);
        }
    }

    public final void givePartyNX(int amount, List<MapleCharacter> party) {
        for (MapleCharacter chr : party) {
            chr.modifyCSPoints(1, amount, true);
        }
    }

    public final void givePartyNX(int amount) {
        if (this.getPlayer().getParty() == null || this.getPlayer().getParty().getMembers().size() == 1) {
            this.gainNX(amount);
            return;
        }
        for (MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = this.getMap().getCharacterById(chr.getId());
            if (curChar == null) continue;
            curChar.modifyCSPoints(1, amount, true);
        }
    }

    public final void endPartyQuest(int amount, List<MapleCharacter> party) {
        for (MapleCharacter chr : party) {
            chr.endPartyQuest(amount);
        }
    }

    public final void endPartyQuest(int amount) {
        if (this.getPlayer().getParty() == null || this.getPlayer().getParty().getMembers().size() == 1) {
            this.getPlayer().endPartyQuest(amount);
            return;
        }
        for (MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = this.getMap().getCharacterById(chr.getId());
            if (curChar == null) continue;
            curChar.endPartyQuest(amount);
        }
    }

    public final void removeFromParty(int id, List<MapleCharacter> party) {
        for (MapleCharacter chr : party) {
            int possesed = chr.getInventory(GameConstants.getInventoryType(id)).countById(id);
            if (possesed <= 0) continue;
            MapleInventoryManipulator.removeById(this.c, GameConstants.getInventoryType(id), id, possesed, true, false);
            chr.getClient().getSession().write((Object)MaplePacketCreator.getShowItemGain(id, (short)(-possesed), true));
        }
    }

    public final void removeFromParty(int id) {
        this.givePartyItems(id, (short)0, true);
    }

    public final void useSkill(int skill, int level) {
        if (level <= 0) {
            return;
        }
        SkillFactory.getSkill(skill).getEffect(level).applyTo(this.c.getPlayer());
    }

    public final void useItem(int id) {
        MapleItemInformationProvider.getInstance().getItemEffect(id).applyTo(this.c.getPlayer());
        this.c.getSession().write((Object)UIPacket.getStatusMsg(id));
    }

    public final void cancelItem(int id) {
        this.c.getPlayer().cancelEffect(MapleItemInformationProvider.getInstance().getItemEffect(id), false, -1L);
    }

    public final int getMorphState() {
        return this.c.getPlayer().getMorphState();
    }

    public final void removeAll(int id) {
        this.c.getPlayer().removeAll(id);
    }

    public final void gainCloseness(int closeness, int index) {
        MaplePet pet = this.getPlayer().getPet(index);
        if (pet != null) {
            pet.setCloseness(pet.getCloseness() + closeness);
            this.getClient().getSession().write((Object)PetPacket.updatePet(pet, this.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte)pet.getInventoryPosition())));
        }
    }

    public final void gainClosenessAll(int closeness) {
        for (MaplePet pet : this.getPlayer().getPets()) {
            if (pet == null) continue;
            pet.setCloseness(pet.getCloseness() + closeness);
            this.getClient().getSession().write((Object)PetPacket.updatePet(pet, this.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte)pet.getInventoryPosition())));
        }
    }

    public final void resetMap(int mapid) {
        this.getMap(mapid).resetFully();
    }

    public final void openNpc(int id) {
        NPCScriptManager.getInstance().start(this.getClient(), id);
    }

    public final void openNpc(MapleClient cg, int id) {
        NPCScriptManager.getInstance().start(cg, id);
    }

    public final int getMapId() {
        return this.c.getPlayer().getMap().getId();
    }

    public final boolean haveMonster(int mobid) {
        for (MapleMapObject mapleMapObject : this.c.getPlayer().getMap().getAllMonstersThreadsafe()) {
            MapleMonster mob2 = (MapleMonster)mapleMapObject;
            if (mob2.getId() != mobid) continue;
            return true;
        }
        return false;
    }

    public final int getChannelNumber() {
        return this.c.getChannel();
    }

    public final int getMonsterCount(int mapid) {
        return this.c.getChannelServer().getMapFactory().getMap(mapid).getNumMonsters();
    }

    public final void teachSkill(int id, byte level, byte masterlevel) {
        this.getPlayer().changeSkillLevel(SkillFactory.getSkill(id), level, masterlevel);
    }

    public final void teachSkill(int id, byte level) {
        ISkill skil = SkillFactory.getSkill(id);
        if (this.getPlayer().getSkillLevel(skil) > level) {
            level = this.getPlayer().getSkillLevel(skil);
        }
        this.getPlayer().changeSkillLevel(skil, level, skil.getMaxLevel());
    }

    public final int getPlayerCount(int mapid) {
        return this.c.getChannelServer().getMapFactory().getMap(mapid).getCharactersSize();
    }

    public final void dojo_getUp() {
        this.c.getSession().write((Object)MaplePacketCreator.updateInfoQuest(1207, "pt=1;min=4;belt=1;tuto=1"));
        this.c.getSession().write((Object)MaplePacketCreator.Mulung_DojoUp2());
        this.c.getSession().write((Object)MaplePacketCreator.instantMapWarp((byte)6));
    }

    public final boolean dojoAgent_NextMap(boolean dojo, boolean fromresting) {
        if (dojo) {
            return Event_DojoAgent.warpNextMap(this.c.getPlayer(), fromresting, this.c.getPlayer().getMap());
        }
        return Event_DojoAgent.warpNextMap_Agent(this.c.getPlayer(), fromresting);
    }

    public final boolean dojoAgent_NextMap(boolean dojo, boolean fromresting, int mapid) {
        if (dojo) {
            return Event_DojoAgent.warpNextMap(this.c.getPlayer(), fromresting, this.getMap(mapid));
        }
        return Event_DojoAgent.warpNextMap_Agent(this.c.getPlayer(), fromresting);
    }

    public final int dojo_getPts() {
        return this.c.getPlayer().getDojo();
    }

    public final MapleEvent getEvent(String loc) {
        return this.c.getChannelServer().getEvent(MapleEventType.valueOf(loc));
    }

    public final int getSavedLocation(String loc) {
        Integer ret = this.c.getPlayer().getSavedLocation(SavedLocationType.fromString(loc));
        if (ret == null || ret == -1) {
            return 100000000;
        }
        return ret;
    }

    public final void saveLocation(String loc) {
        this.c.getPlayer().saveLocation(SavedLocationType.fromString(loc));
    }

    public final void saveReturnLocation(String loc) {
        this.c.getPlayer().saveLocation(SavedLocationType.fromString(loc), this.c.getPlayer().getMap().getReturnMap().getId());
    }

    public final void clearSavedLocation(String loc) {
        this.c.getPlayer().clearSavedLocation(SavedLocationType.fromString(loc));
    }

    public final void summonMsg(String msg) {
        if (!this.c.getPlayer().hasSummon()) {
            this.playerSummonHint(true);
        }
        this.c.getSession().write((Object)UIPacket.summonMessage(msg));
    }

    public final void summonMsg(int type) {
        if (!this.c.getPlayer().hasSummon()) {
            this.playerSummonHint(true);
        }
        this.c.getSession().write((Object)UIPacket.summonMessage(type));
    }

    public final void showInstruction(String msg, int width, int height) {
        this.c.getSession().write((Object)MaplePacketCreator.sendHint(msg, width, height));
    }

    public final void playerSummonHint(boolean summon) {
        this.c.getPlayer().setHasSummon(summon);
        this.c.getSession().write((Object)UIPacket.summonHelper(summon));
    }

    public final String getInfoQuest(int id) {
        return this.c.getPlayer().getInfoQuest(id);
    }

    public final void updateInfoQuest(int id, String data) {
        this.c.getPlayer().updateInfoQuest(id, data);
    }

    public final boolean getEvanIntroState(String data) {
        return this.getInfoQuest(22013).equals(data);
    }

    public final void updateEvanIntroState(String data) {
        this.updateInfoQuest(22013, data);
    }

    public final void Aran_Start() {
        this.c.getSession().write((Object)UIPacket.Aran_Start());
    }

    public final void evanTutorial(String data, int v1) {
        this.c.getSession().write((Object)MaplePacketCreator.getEvanTutorial(data));
    }

    public final void AranTutInstructionalBubble(String data) {
        this.c.getSession().write((Object)UIPacket.AranTutInstructionalBalloon(data));
    }

    public final void ShowWZEffect(String data) {
        this.c.getSession().write((Object)UIPacket.AranTutInstructionalBalloon(data));
    }

    public final void showWZEffect(String data) {
        this.c.getSession().write((Object)UIPacket.ShowWZEffect(data));
    }

    public final void EarnTitleMsg(String data) {
        this.c.getSession().write((Object)UIPacket.EarnTitleMsg(data));
    }

    public final void MovieClipIntroUI(boolean enabled) {
        this.c.getSession().write((Object)UIPacket.IntroDisableUI(enabled));
        this.c.getSession().write((Object)UIPacket.IntroLock(enabled));
    }

    public MapleInventoryType getInvType(int i) {
        return MapleInventoryType.getByType((byte)i);
    }

    public String getItemName(int id) {
        return MapleItemInformationProvider.getInstance().getName(id);
    }

    public void gainPet(int id, String name, int level, int closeness, int fullness, long period, short flag) {
        if (id > 5000200 || id < 5000000) {
            id = 5000000;
        }
        if (level > 30) {
            level = 30;
        }
        if (closeness > 30000) {
            closeness = 30000;
        }
        if (fullness > 100) {
            fullness = 100;
        }
        try {
            MapleInventoryManipulator.addById(this.c, id, (short)1, "", MaplePet.createPet(id, name, level, closeness, fullness, MapleInventoryIdentifier.getInstance(), id == 5000054 ? (int)period : 0, flag), 45L);
        }
        catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public void removeSlot(int invType, byte slot, short quantity) {
        MapleInventoryManipulator.removeFromSlot(this.c, this.getInvType(invType), slot, quantity, true);
    }

    public void gainGP(int gp) {
        if (this.getPlayer().getGuildId() <= 0) {
            return;
        }
        World.Guild.gainGP(this.getPlayer().getGuildId(), gp);
    }

    public int getGP() {
        if (this.getPlayer().getGuildId() <= 0) {
            return 0;
        }
        return World.Guild.getGP(this.getPlayer().getGuildId());
    }

    public void showMapEffect(String path) {
        this.getClient().getSession().write((Object)UIPacket.MapEff(path));
    }

    public int itemQuantity(int itemid) {
        return this.getPlayer().itemQuantity(itemid);
    }

    public EventInstanceManager getDisconnected(String event) {
        EventManager em = this.getEventManager(event);
        if (em == null) {
            return null;
        }
        for (EventInstanceManager eim : em.getInstances()) {
            if (!eim.isDisconnected(this.c.getPlayer()) || eim.getPlayerCount() <= 0) continue;
            return eim;
        }
        return null;
    }

    public boolean isAllReactorState(int reactorId, int state) {
        boolean ret = false;
        for (MapleReactor r : this.getMap().getAllReactorsThreadsafe()) {
            if (r.getReactorId() != reactorId) continue;
            ret = r.getState() == state;
        }
        return ret;
    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public void spawnMonster(int id) {
        this.spawnMonster(id, 1, new Point(this.getPlayer().getPosition()));
    }

    public void spawnMonster(int id, int x, int y) {
        this.spawnMonster(id, 1, new Point(x, y));
    }

    public void spawnMonster(int id, int qty, int x, int y) {
        this.spawnMonster(id, qty, new Point(x, y));
    }

    public void spawnMonster(int id, int qty, Point pos) {
        for (int i = 0; i < qty; ++i) {
            this.getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), pos);
        }
    }

    public void sendNPCText(String text, int npc) {
        this.getMap().broadcastMessage(MaplePacketCreator.getNPCTalk(npc, (byte)0, text, "00 00", (byte)0));
    }

    public boolean getTempFlag(int flag) {
        return (this.c.getChannelServer().getTempFlag() & flag) == flag;
    }
}

