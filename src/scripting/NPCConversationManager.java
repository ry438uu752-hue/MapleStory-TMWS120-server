/*
 * Decompiled with CFR 0.152.
 */
package scripting;

import client.ISkill;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import client.SkillEntry;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.IItem;
import client.inventory.ItemFlag;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import database.DatabaseConnection;
import handling.channel.ChannelServer;
import handling.channel.MapleGuildRanking;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.World;
import handling.world.guild.MapleGuild;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.script.Invocable;
import scripting.AbstractPlayerInteraction;
import scripting.EventInstanceManager;
import scripting.NPCScriptManager;
import server.MapleCarnivalChallenge;
import server.MapleCarnivalParty;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleShopFactory;
import server.MapleSquad;
import server.MapleStatEffect;
import server.Randomizer;
import server.SpeedRunner;
import server.StructPotentialItem;
import server.Timer;
import server.life.MapleMonster;
import server.maps.AramiaFireWorks;
import server.maps.Event_DojoAgent;
import server.maps.Event_PyramidSubway;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.SpeedRunType;
import server.quest.MapleQuest;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.StringUtil;
import tools.packet.PlayerShopPacket;

public class NPCConversationManager
extends AbstractPlayerInteraction {
    private MapleClient c;
    private int npc;
    private int questid;
    private String getText;
    private byte type;
    private byte lastMsg = (byte)-1;
    public boolean pendingDisposal = false;
    private Invocable iv;

    public NPCConversationManager(MapleClient c, int npc, int questid, byte type, Invocable iv) {
        super(c);
        this.c = c;
        this.npc = npc;
        this.questid = questid;
        this.type = type;
        this.iv = iv;
    }

    public Invocable getIv() {
        return this.iv;
    }

    public int getNpc() {
        return this.npc;
    }

    public int getQuest() {
        return this.questid;
    }

    public byte getType() {
        return this.type;
    }

    public void safeDispose() {
        this.pendingDisposal = true;
    }

    public void dispose() {
        NPCScriptManager.getInstance().dispose(this.c);
    }

    public void askMapSelection(String sel) {
        if (this.lastMsg > -1) {
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getMapSelection(this.npc, sel));
        this.lastMsg = (byte)15;
    }

    public void sendNext(String text) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimple(text);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)0, text, "00 01", (byte)0));
        this.lastMsg = 0;
    }

    public void sendNextS(String text, byte type) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimpleS(text, type);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)0, text, "00 01", type));
        this.lastMsg = 0;
    }

    public void sendPrev(String text) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimple(text);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)0, text, "01 00", (byte)0));
        this.lastMsg = 0;
    }

    public void sendPrevS(String text, byte type) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimpleS(text, type);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)0, text, "01 00", type));
        this.lastMsg = 0;
    }

    public void sendNextPrev(String text) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimple(text);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)0, text, "01 01", (byte)0));
        this.lastMsg = 0;
    }

    public void PlayerToNpc(String text) {
        this.sendNextPrevS(text, (byte)3);
    }

    public void sendNextPrevS(String text) {
        this.sendNextPrevS(text, (byte)3);
    }

    public void sendNextPrevS(String text, byte type) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimpleS(text, type);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)0, text, "01 01", type));
        this.lastMsg = 0;
    }

    public void sendOk(String text) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimple(text);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)0, text, "00 00", (byte)0));
        this.lastMsg = 0;
    }

    public void sendOkS(String text, byte type) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimpleS(text, type);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)0, text, "00 00", type));
        this.lastMsg = 0;
    }

    public void sendYesNo(String text) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimple(text);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)2, text, "", (byte)0));
        this.lastMsg = (byte)2;
    }

    public void sendYesNoS(String text, byte type) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimpleS(text, type);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)2, text, "", type));
        this.lastMsg = (byte)2;
    }

    public void sendAcceptDecline(String text) {
        this.askAcceptDecline(text);
    }

    public void sendAcceptDeclineNoESC(String text) {
        this.askAcceptDeclineNoESC(text);
    }

    public void askAcceptDecline(String text) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimple(text);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)12, text, "", (byte)0));
        this.lastMsg = (byte)12;
    }

    public void askAcceptDeclineNoESC(String text) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimple(text);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)13, text, "", (byte)0));
        this.lastMsg = (byte)13;
    }

    public void askAvatar(String text, int ... args) {
        if (this.lastMsg > -1) {
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalkStyle(this.npc, text, args));
        this.lastMsg = (byte)8;
    }

    public void sendSimple(String text) {
        if (this.lastMsg > -1) {
            return;
        }
        if (!text.contains("#L")) {
            this.sendNext(text);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)5, text, "", (byte)0));
        this.lastMsg = (byte)5;
    }

    public void sendSimpleS(String text, byte type) {
        if (this.lastMsg > -1) {
            return;
        }
        if (!text.contains("#L")) {
            this.sendNextS(text, type);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalk(this.npc, (byte)5, text, "", type));
        this.lastMsg = (byte)5;
    }

    public void sendStyle(String text, int[] styles) {
        if (this.lastMsg > -1) {
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalkStyle(this.npc, text, styles));
        this.lastMsg = (byte)8;
    }

    public void sendGetNumber(String text, int def, int min, int max) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimple(text);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalkNum(this.npc, text, def, min, max));
        this.lastMsg = (byte)4;
    }

    public void sendGetText(String text) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            this.sendSimple(text);
            return;
        }
        this.c.getSession().write((Object)MaplePacketCreator.getNPCTalkText(this.npc, text));
        this.lastMsg = (byte)3;
    }

    public void setGetText(String text) {
        this.getText = text;
    }

    public String getText() {
        return this.getText;
    }

    public void setHair(int hair) {
        this.getPlayer().setHair(hair);
        this.getPlayer().updateSingleStat(MapleStat.HAIR, hair);
        this.getPlayer().equipChanged();
    }

    public void setFace(int face) {
        this.getPlayer().setFace(face);
        this.getPlayer().updateSingleStat(MapleStat.FACE, face);
        this.getPlayer().equipChanged();
    }

    public void setSkin(int color) {
        this.getPlayer().setSkinColor((byte)color);
        this.getPlayer().updateSingleStat(MapleStat.SKIN, color);
        this.getPlayer().equipChanged();
    }

    public int setRandomAvatar(int ticket, int ... args_all) {
        if (!this.haveItem(ticket)) {
            return -1;
        }
        this.gainItem(ticket, (short)-1);
        int args = args_all[Randomizer.nextInt(args_all.length)];
        if (args < 100) {
            this.c.getPlayer().setSkinColor((byte)args);
            this.c.getPlayer().updateSingleStat(MapleStat.SKIN, args);
        } else if (args < 30000) {
            this.c.getPlayer().setFace(args);
            this.c.getPlayer().updateSingleStat(MapleStat.FACE, args);
        } else {
            this.c.getPlayer().setHair(args);
            this.c.getPlayer().updateSingleStat(MapleStat.HAIR, args);
        }
        this.c.getPlayer().equipChanged();
        return 1;
    }

    public int setAvatar(int ticket, int args) {
        if (!this.haveItem(ticket)) {
            return -1;
        }
        this.gainItem(ticket, (short)-1);
        if (args < 100) {
            this.c.getPlayer().setSkinColor((byte)args);
            this.c.getPlayer().updateSingleStat(MapleStat.SKIN, args);
        } else if (args < 30000) {
            this.c.getPlayer().setFace(args);
            this.c.getPlayer().updateSingleStat(MapleStat.FACE, args);
        } else {
            this.c.getPlayer().setHair(args);
            this.c.getPlayer().updateSingleStat(MapleStat.HAIR, args);
        }
        this.c.getPlayer().equipChanged();
        return 1;
    }

    public void sendStorage() {
        this.c.getPlayer().setConversation(4);
        this.c.getPlayer().getStorage().sendStorage(this.c, this.npc);
    }

    public void openShop(int id) {
        MapleShopFactory.getInstance().getShop(id).sendShop(this.c);
    }

    public int gainGachaponItem(int id, int quantity) {
        return this.gainGachaponItem(id, quantity, this.c.getPlayer().getMap().getStreetName() + " - " + this.c.getPlayer().getMap().getMapName(), false);
    }

    public int gainGachaponItem(int id, int quantity, boolean broad) {
        return this.gainGachaponItem(id, quantity, this.c.getPlayer().getMap().getStreetName() + " - " + this.c.getPlayer().getMap().getMapName(), broad);
    }

    public int gainGachaponItem(int id, int quantity, String msg, boolean broad) {
        try {
            if (!MapleItemInformationProvider.getInstance().itemExists(id)) {
                return -1;
            }
            IItem item = MapleInventoryManipulator.addbyId_Gachapon(this.c, id, (short)quantity);
            if (item == null) {
                return -1;
            }
            byte rareness = GameConstants.gachaponRareItem(item.getItemId());
            if (rareness > 0 || broad) {
                World.Broadcast.broadcastMessage(MaplePacketCreator.getGachaponMega("[" + msg + "] " + this.c.getPlayer().getName(), " : \u5f9e\u8f49\u86cb\u6a5f\u8f49\u5230\u4e86!", item, rareness, this.c.getChannel() - 1).getBytes());
            }
            return item.getItemId();
        }
        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void changeJob(int job) {
        this.c.getPlayer().changeJob(job);
    }

    public void startQuest(int id) {
        MapleQuest.getInstance(id).start(this.getPlayer(), this.npc);
    }

    public void completeQuest(int id) {
        MapleQuest.getInstance(id).complete(this.getPlayer(), this.npc);
    }

    public void forfeitQuest(int id) {
        MapleQuest.getInstance(id).forfeit(this.getPlayer());
    }

    public void forceStartQuest() {
        MapleQuest.getInstance(this.questid).forceStart(this.getPlayer(), this.getNpc(), null);
    }

    @Override
    public void forceStartQuest(int id) {
        MapleQuest.getInstance(id).forceStart(this.getPlayer(), this.getNpc(), null);
    }

    public void forceStartQuest(String customData) {
        MapleQuest.getInstance(this.questid).forceStart(this.getPlayer(), this.getNpc(), customData);
    }

    public void forceCompleteQuest() {
        MapleQuest.getInstance(this.questid).forceComplete(this.getPlayer(), this.getNpc());
    }

    @Override
    public void forceCompleteQuest(int id) {
        MapleQuest.getInstance(id).forceComplete(this.getPlayer(), this.getNpc());
    }

    public String getQuestCustomData() {
        return this.c.getPlayer().getQuestNAdd(MapleQuest.getInstance(this.questid)).getCustomData();
    }

    public void setQuestCustomData(String customData) {
        this.getPlayer().getQuestNAdd(MapleQuest.getInstance(this.questid)).setCustomData(customData);
    }

    public int getMeso() {
        return this.getPlayer().getMeso();
    }

    public void gainAp(int amount) {
        this.c.getPlayer().gainAp((short)amount);
    }

    public void expandInventory(byte type, int amt) {
        this.c.getPlayer().expandInventory(type, amt);
    }

    public void unequipEverything() {
        MapleInventory equipped = this.getPlayer().getInventory(MapleInventoryType.EQUIPPED);
        MapleInventory equip = this.getPlayer().getInventory(MapleInventoryType.EQUIP);
        LinkedList<Short> ids = new LinkedList<Short>();
        for (IItem item : equipped.list()) {
            ids.add(item.getPosition());
        }
        Iterator<Short> iterator = ids.iterator();
        while (iterator.hasNext()) {
            short id = (Short)((Object)iterator.next());
            MapleInventoryManipulator.unequip(this.getC(), id, equip.getNextFreeSlot());
        }
    }

    public final void clearSkills() {
        Map<ISkill, SkillEntry> skills = this.getPlayer().getSkills();
        for (Map.Entry<ISkill, SkillEntry> skill : skills.entrySet()) {
            this.getPlayer().changeSkillLevel(skill.getKey(), (byte)0, (byte)0);
        }
    }

    public boolean hasSkill(int skillid) {
        ISkill theSkill = SkillFactory.getSkill(skillid);
        if (theSkill != null) {
            return this.c.getPlayer().getSkillLevel(theSkill) > 0;
        }
        return false;
    }

    public void showEffect(boolean broadcast, String effect) {
        if (broadcast) {
            this.c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.showEffect(effect));
        } else {
            this.c.getSession().write((Object)MaplePacketCreator.showEffect(effect));
        }
    }

    public void playSound(boolean broadcast, String sound) {
        if (broadcast) {
            this.c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.playSound(sound));
        } else {
            this.c.getSession().write((Object)MaplePacketCreator.playSound(sound));
        }
    }

    public void environmentChange(boolean broadcast, String env) {
        if (broadcast) {
            this.c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.environmentChange(env, 2));
        } else {
            this.c.getSession().write((Object)MaplePacketCreator.environmentChange(env, 2));
        }
    }

    public void updateBuddyCapacity(int capacity) {
        this.c.getPlayer().setBuddyCapacity((byte)capacity);
    }

    public int getBuddyCapacity() {
        return this.c.getPlayer().getBuddyCapacity();
    }

    public int partyMembersInMap() {
        int inMap = 0;
        for (MapleCharacter char2 : this.getPlayer().getMap().getCharactersThreadsafe()) {
            if (char2.getParty() != this.getPlayer().getParty()) continue;
            ++inMap;
        }
        return inMap;
    }

    public List<MapleCharacter> getPartyMembers() {
        if (this.getPlayer().getParty() == null) {
            return null;
        }
        LinkedList<MapleCharacter> chars = new LinkedList<MapleCharacter>();
        for (MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            for (ChannelServer channel : ChannelServer.getAllInstances()) {
                MapleCharacter ch = channel.getPlayerStorage().getCharacterById(chr.getId());
                if (ch == null) continue;
                chars.add(ch);
            }
        }
        return chars;
    }

    public void warpPartyWithExp(int mapId, int exp) {
        MapleMap target = this.getMap(mapId);
        for (MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = this.c.getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
            if ((curChar.getEventInstance() != null || this.getPlayer().getEventInstance() != null) && curChar.getEventInstance() != this.getPlayer().getEventInstance()) continue;
            curChar.changeMap(target, target.getPortal(0));
            curChar.gainExp(exp, true, false, true);
        }
    }

    public void warpPartyWithExpMeso(int mapId, int exp, int meso) {
        MapleMap target = this.getMap(mapId);
        for (MaplePartyCharacter chr : this.getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = this.c.getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
            if ((curChar.getEventInstance() != null || this.getPlayer().getEventInstance() != null) && curChar.getEventInstance() != this.getPlayer().getEventInstance()) continue;
            curChar.changeMap(target, target.getPortal(0));
            curChar.gainExp(exp, true, false, true);
            curChar.gainMeso(meso, true);
        }
    }

    public MapleSquad getSquad(String type) {
        return this.c.getChannelServer().getMapleSquad(type);
    }

    public int getSquadAvailability(String type) {
        MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return -1;
        }
        return squad.getStatus();
    }

    public boolean registerSquad(String type, int minutes, String startText) {
        if (this.c.getChannelServer().getMapleSquad(type) == null) {
            MapleSquad squad = new MapleSquad(this.c.getChannel(), type, this.c.getPlayer(), minutes * 60 * 1000, startText);
            boolean ret = this.c.getChannelServer().addMapleSquad(squad, type);
            if (ret) {
                MapleMap map = this.c.getPlayer().getMap();
                map.broadcastMessage(MaplePacketCreator.getClock(minutes * 60));
                map.broadcastMessage(MaplePacketCreator.serverNotice(6, this.c.getPlayer().getName() + startText));
            } else {
                squad.clear();
            }
            return ret;
        }
        return false;
    }

    public boolean getSquadList(String type, byte type_) {
        MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return false;
        }
        if (type_ == 0 || type_ == 3) {
            this.sendNext(squad.getSquadMemberString(type_));
        } else if (type_ == 1) {
            this.sendSimple(squad.getSquadMemberString(type_));
        } else if (type_ == 2) {
            if (squad.getBannedMemberSize() > 0) {
                this.sendSimple(squad.getSquadMemberString(type_));
            } else {
                this.sendNext(squad.getSquadMemberString(type_));
            }
        }
        return true;
    }

    public byte isSquadLeader(String type) {
        MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return -1;
        }
        if (squad.getLeader() != null && squad.getLeader().getId() == this.c.getPlayer().getId()) {
            return 1;
        }
        return 0;
    }

    public boolean reAdd(String eim, String squad) {
        EventInstanceManager eimz = this.getDisconnected(eim);
        MapleSquad squadz = this.getSquad(squad);
        if (eimz != null && squadz != null) {
            squadz.reAddMember(this.getPlayer());
            eimz.registerPlayer(this.getPlayer());
            return true;
        }
        return false;
    }

    public void banMember(String type, int pos) {
        MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            squad.banMember(pos);
        }
    }

    public void acceptMember(String type, int pos) {
        MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            squad.acceptMember(pos);
        }
    }

    public String getReadableMillis(long startMillis, long endMillis) {
        return StringUtil.getReadableMillis(startMillis, endMillis);
    }

    public int addMember(String type, boolean join) {
        MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            return squad.addMember(this.c.getPlayer(), join);
        }
        return -1;
    }

    public byte isSquadMember(String type) {
        MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return -1;
        }
        if (squad.getMembers().contains(this.c.getPlayer())) {
            return 1;
        }
        if (squad.isBanned(this.c.getPlayer())) {
            return 2;
        }
        return 0;
    }

    public void resetReactors() {
        this.getPlayer().getMap().resetReactors();
    }

    public void genericGuildMessage(int code) {
        this.c.getSession().write((Object)MaplePacketCreator.genericGuildMessage((byte)code));
    }

    public void disbandGuild() {
        int gid = this.c.getPlayer().getGuildId();
        if (gid <= 0 || this.c.getPlayer().getGuildRank() != 1) {
            return;
        }
        World.Guild.disbandGuild(gid);
    }

    public void increaseGuildCapacity(boolean UseGP) {
        if (!UseGP) {
            if (this.c.getPlayer().getMeso() < 250000) {
                this.c.getSession().write((Object)MaplePacketCreator.serverNotice(1, "\u4f60\u6c92\u6709\u8db3\u5920\u7684\u9322"));
                return;
            }
            if (this.c.getPlayer().getGuild().getCapacity() >= 100) {
                this.c.getSession().write((Object)MaplePacketCreator.serverNotice(1, "\u516c\u6703\u4eba\u6578\u5df2\u7d93\u64f4\u5145\u8d85\u904e100"));
                return;
            }
            int gid = this.c.getPlayer().getGuildId();
            if (gid <= 0) {
                return;
            }
            World.Guild.increaseGuildCapacity(gid);
            this.c.getPlayer().gainMeso(-250000, true, false, true);
        } else {
            if (this.c.getPlayer().getGuild().getGP() < 2500) {
                this.c.getSession().write((Object)MaplePacketCreator.serverNotice(1, "\u516c\u6703\u7684GP\u4e0d\u8db3."));
                return;
            }
            int gid = this.c.getPlayer().getGuildId();
            if (gid <= 0) {
                return;
            }
            World.Guild.increaseGuildCapacity(gid);
            this.c.getPlayer().getGuild().gainGP(-2500);
        }
    }

    public void displayGuildRanks() {
        this.c.getSession().write((Object)MaplePacketCreator.showGuildRanks(this.npc, MapleGuildRanking.getInstance().getRank()));
    }

    public boolean removePlayerFromInstance() {
        if (this.c.getPlayer().getEventInstance() != null) {
            this.c.getPlayer().getEventInstance().removePlayer(this.c.getPlayer());
            return true;
        }
        return false;
    }

    public boolean isPlayerInstance() {
        return this.c.getPlayer().getEventInstance() != null;
    }

    public void changeStat(byte slot, int type, short amount) {
        Equip sel = (Equip)this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slot);
        switch (type) {
            case 0: {
                sel.setStr(amount);
                break;
            }
            case 1: {
                sel.setDex(amount);
                break;
            }
            case 2: {
                sel.setInt(amount);
                break;
            }
            case 3: {
                sel.setLuk(amount);
                break;
            }
            case 4: {
                sel.setHp(amount);
                break;
            }
            case 5: {
                sel.setMp(amount);
                break;
            }
            case 6: {
                sel.setWatk(amount);
                break;
            }
            case 7: {
                sel.setMatk(amount);
                break;
            }
            case 8: {
                sel.setWdef(amount);
                break;
            }
            case 9: {
                sel.setMdef(amount);
                break;
            }
            case 10: {
                sel.setAcc(amount);
                break;
            }
            case 11: {
                sel.setAvoid(amount);
                break;
            }
            case 12: {
                sel.setHands(amount);
                break;
            }
            case 13: {
                sel.setSpeed(amount);
                break;
            }
            case 14: {
                sel.setJump(amount);
                break;
            }
            case 15: {
                sel.setUpgradeSlots((byte)amount);
                break;
            }
            case 16: {
                sel.setViciousHammer((byte)amount);
                break;
            }
            case 17: {
                sel.setLevel((byte)amount);
                break;
            }
            case 18: {
                sel.setEnhance((byte)amount);
                break;
            }
            case 19: {
                sel.setPotential1(amount);
                break;
            }
            case 20: {
                sel.setPotential2(amount);
                break;
            }
            case 21: {
                sel.setPotential3(amount);
                break;
            }
            case 22: {
                sel.setOwner(this.getText());
                break;
            }
        }
        this.c.getPlayer().equipChanged();
    }

    public void killAllMonsters() {
        MapleMap map = this.c.getPlayer().getMap();
        double range = Double.POSITIVE_INFINITY;
        for (MapleMapObject monstermo : map.getMapObjectsInRange(this.c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
            MapleMonster mob2 = (MapleMonster)monstermo;
            if (!mob2.getStats().isBoss()) continue;
            map.killMonster(mob2, this.c.getPlayer(), false, false, (byte)1);
        }
    }

    public void giveMerchantMesos() {
        long mesos = 0L;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM hiredmerchants WHERE merchantid = ?");
            ps.setInt(1, this.getPlayer().getId());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
            } else {
                mesos = rs.getLong("mesos");
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("UPDATE hiredmerchants SET mesos = 0 WHERE merchantid = ?");
            ps.setInt(1, this.getPlayer().getId());
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException ex) {
            System.err.println("Error gaining mesos in hired merchant" + ex);
        }
        this.c.getPlayer().gainMeso((int)mesos, true);
    }

    public void dc() {
        MapleCharacter victim = this.c.getChannelServer().getPlayerStorage().getCharacterByName(this.c.getPlayer().getName().toString());
        victim.getClient().getSession().close();
        victim.getClient().disconnect(true, false);
    }

    public long getMerchantMesos() {
        long mesos = 0L;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM hiredmerchants WHERE merchantid = ?");
            ps.setInt(1, this.getPlayer().getId());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
            } else {
                mesos = rs.getLong("mesos");
            }
            rs.close();
            ps.close();
        }
        catch (SQLException ex) {
            System.err.println("Error gaining mesos in hired merchant" + ex);
        }
        return mesos;
    }

    public void openDuey() {
        this.c.getPlayer().setConversation(2);
        this.c.getSession().write((Object)MaplePacketCreator.sendDuey((byte)9, null));
    }

    public void openMerchantItemStore() {
        this.c.getPlayer().setConversation(3);
        this.c.getSession().write((Object)PlayerShopPacket.merchItemStore((byte)34));
    }

    public void sendRepairWindow() {
        this.c.getSession().write((Object)MaplePacketCreator.sendRepairWindow(this.npc));
    }

    public final int getDojoPoints() {
        return this.c.getPlayer().getDojo();
    }

    public final int getDojoRecord() {
        return this.c.getPlayer().getDojoRecord();
    }

    public void setDojoRecord(boolean reset) {
        this.c.getPlayer().setDojoRecord(reset);
    }

    public boolean start_DojoAgent(boolean dojo, boolean party) {
        if (dojo) {
            return Event_DojoAgent.warpStartDojo(this.c.getPlayer(), party);
        }
        return Event_DojoAgent.warpStartAgent(this.c.getPlayer(), party);
    }

    public boolean start_PyramidSubway(int pyramid) {
        if (pyramid >= 0) {
            return Event_PyramidSubway.warpStartPyramid(this.c.getPlayer(), pyramid);
        }
        return Event_PyramidSubway.warpStartSubway(this.c.getPlayer());
    }

    public boolean bonus_PyramidSubway(int pyramid) {
        if (pyramid >= 0) {
            return Event_PyramidSubway.warpBonusPyramid(this.c.getPlayer(), pyramid);
        }
        return Event_PyramidSubway.warpBonusSubway(this.c.getPlayer());
    }

    public final short getKegs() {
        return AramiaFireWorks.getInstance().getKegsPercentage();
    }

    public void giveKegs(int kegs) {
        AramiaFireWorks.getInstance().giveKegs(this.c.getPlayer(), kegs);
    }

    public final short getSunshines() {
        return AramiaFireWorks.getInstance().getSunsPercentage();
    }

    public void addSunshines(int kegs) {
        AramiaFireWorks.getInstance().giveSuns(this.c.getPlayer(), kegs);
    }

    public final short getDecorations() {
        return AramiaFireWorks.getInstance().getDecsPercentage();
    }

    public void addDecorations(int kegs) {
        try {
            AramiaFireWorks.getInstance().giveDecs(this.c.getPlayer(), kegs);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final MapleInventory getInventory(int type) {
        return this.c.getPlayer().getInventory(MapleInventoryType.getByType((byte)type));
    }

    public final MapleCarnivalParty getCarnivalParty() {
        return this.c.getPlayer().getCarnivalParty();
    }

    public final MapleCarnivalChallenge getNextCarnivalRequest() {
        return this.c.getPlayer().getNextCarnivalRequest();
    }

    public final MapleCarnivalChallenge getCarnivalChallenge(MapleCharacter chr) {
        return new MapleCarnivalChallenge(chr);
    }

    public void maxStats() {
        ArrayList<Pair<MapleStat, Integer>> statup = new ArrayList<Pair<MapleStat, Integer>>(2);
        this.c.getPlayer().getStat().setStr((short)Short.MAX_VALUE);
        this.c.getPlayer().getStat().setDex((short)Short.MAX_VALUE);
        this.c.getPlayer().getStat().setInt((short)Short.MAX_VALUE);
        this.c.getPlayer().getStat().setLuk((short)Short.MAX_VALUE);
        this.c.getPlayer().getStat().setMaxHp((short)30000);
        this.c.getPlayer().getStat().setMaxMp((short)30000);
        this.c.getPlayer().getStat().setHp(30000);
        this.c.getPlayer().getStat().setMp(30000);
        statup.add(new Pair<MapleStat, Integer>(MapleStat.STR, (int)Short.MAX_VALUE));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.DEX, (int)Short.MAX_VALUE));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.LUK, (int)Short.MAX_VALUE));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.INT, (int)Short.MAX_VALUE));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.HP, 30000));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.MAXHP, 30000));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.MP, 30000));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.MAXMP, 30000));
        this.c.getSession().write((Object)MaplePacketCreator.updatePlayerStats(statup, this.c.getPlayer().getJob()));
    }

    public Pair<String, Map<Integer, String>> getSpeedRun(String typ) {
        SpeedRunType type = SpeedRunType.valueOf(typ);
        if (SpeedRunner.getInstance().getSpeedRunData(type) != null) {
            return SpeedRunner.getInstance().getSpeedRunData(type);
        }
        return new Pair<String, Map<Integer, String>>("", new HashMap());
    }

    public boolean getSR(Pair<String, Map<Integer, String>> ma, int sel) {
        if (ma.getRight().get(sel) == null || ma.getRight().get(sel).length() <= 0) {
            this.dispose();
            return false;
        }
        this.sendOk(ma.getRight().get(sel));
        return true;
    }

    public Equip getEquip(int itemid) {
        return (Equip)MapleItemInformationProvider.getInstance().getEquipById(itemid);
    }

    public void setExpiration(Object statsSel, long expire) {
        if (statsSel instanceof Equip) {
            ((Equip)statsSel).setExpiration(System.currentTimeMillis() + expire * 24L * 60L * 60L * 1000L);
        }
    }

    public void setLock(Object statsSel) {
        if (statsSel instanceof Equip) {
            Equip eq = (Equip)statsSel;
            if (eq.getExpiration() == -1L) {
                eq.setFlag((byte)(eq.getFlag() | ItemFlag.LOCK.getValue()));
            } else {
                eq.setFlag((byte)(eq.getFlag() | ItemFlag.UNTRADEABLE.getValue()));
            }
        }
    }

    public boolean addFromDrop(Object statsSel) {
        if (statsSel instanceof IItem) {
            IItem it = (IItem)statsSel;
            return MapleInventoryManipulator.checkSpace(this.getClient(), it.getItemId(), it.getQuantity(), it.getOwner()) && MapleInventoryManipulator.addFromDrop(this.getClient(), it, false);
        }
        return false;
    }

    public boolean replaceItem(int slot, int invType, Object statsSel, int offset, String type) {
        return this.replaceItem(slot, invType, statsSel, offset, type, false);
    }

    public boolean replaceItem(int slot, int invType, Object statsSel, int offset, String type, boolean takeSlot) {
        MapleInventoryType inv = MapleInventoryType.getByType((byte)invType);
        if (inv == null) {
            return false;
        }
        IItem item = this.getPlayer().getInventory(inv).getItem((byte)slot);
        if (item == null || statsSel instanceof IItem) {
            item = (IItem)statsSel;
        }
        if (offset > 0) {
            if (inv != MapleInventoryType.EQUIP) {
                return false;
            }
            Equip eq = (Equip)item;
            if (takeSlot) {
                if (eq.getUpgradeSlots() < 1) {
                    return false;
                }
                eq.setUpgradeSlots((byte)(eq.getUpgradeSlots() - 1));
            }
            if (type.equalsIgnoreCase("Slots")) {
                eq.setUpgradeSlots((byte)(eq.getUpgradeSlots() + offset));
            } else if (type.equalsIgnoreCase("Level")) {
                eq.setLevel((byte)(eq.getLevel() + offset));
            } else if (type.equalsIgnoreCase("Hammer")) {
                eq.setViciousHammer((byte)(eq.getViciousHammer() + offset));
            } else if (type.equalsIgnoreCase("STR")) {
                eq.setStr((short)(eq.getStr() + offset));
            } else if (type.equalsIgnoreCase("DEX")) {
                eq.setDex((short)(eq.getDex() + offset));
            } else if (type.equalsIgnoreCase("INT")) {
                eq.setInt((short)(eq.getInt() + offset));
            } else if (type.equalsIgnoreCase("LUK")) {
                eq.setLuk((short)(eq.getLuk() + offset));
            } else if (type.equalsIgnoreCase("HP")) {
                eq.setHp((short)(eq.getHp() + offset));
            } else if (type.equalsIgnoreCase("MP")) {
                eq.setMp((short)(eq.getMp() + offset));
            } else if (type.equalsIgnoreCase("WATK")) {
                eq.setWatk((short)(eq.getWatk() + offset));
            } else if (type.equalsIgnoreCase("MATK")) {
                eq.setMatk((short)(eq.getMatk() + offset));
            } else if (type.equalsIgnoreCase("WDEF")) {
                eq.setWdef((short)(eq.getWdef() + offset));
            } else if (type.equalsIgnoreCase("MDEF")) {
                eq.setMdef((short)(eq.getMdef() + offset));
            } else if (type.equalsIgnoreCase("ACC")) {
                eq.setAcc((short)(eq.getAcc() + offset));
            } else if (type.equalsIgnoreCase("Avoid")) {
                eq.setAvoid((short)(eq.getAvoid() + offset));
            } else if (type.equalsIgnoreCase("Hands")) {
                eq.setHands((short)(eq.getHands() + offset));
            } else if (type.equalsIgnoreCase("Speed")) {
                eq.setSpeed((short)(eq.getSpeed() + offset));
            } else if (type.equalsIgnoreCase("Jump")) {
                eq.setJump((short)(eq.getJump() + offset));
            } else if (type.equalsIgnoreCase("ItemEXP")) {
                eq.setItemEXP(eq.getItemEXP() + offset);
            } else if (type.equalsIgnoreCase("Expiration")) {
                eq.setExpiration(eq.getExpiration() + (long)offset);
            } else if (type.equalsIgnoreCase("Flag")) {
                eq.setFlag((byte)(eq.getFlag() + offset));
            }
            if (eq.getExpiration() == -1L) {
                eq.setFlag((byte)(eq.getFlag() | ItemFlag.LOCK.getValue()));
            } else {
                eq.setFlag((byte)(eq.getFlag() | ItemFlag.UNTRADEABLE.getValue()));
            }
            item = eq.copy();
        }
        MapleInventoryManipulator.removeFromSlot(this.getClient(), inv, (short)slot, item.getQuantity(), false);
        return MapleInventoryManipulator.addFromDrop(this.getClient(), item, false);
    }

    public boolean replaceItem(int slot, int invType, Object statsSel, int upgradeSlots) {
        return this.replaceItem(slot, invType, statsSel, upgradeSlots, "Slots");
    }

    public boolean isCash(int itemId) {
        return MapleItemInformationProvider.getInstance().isCash(itemId);
    }

    public void buffGuild(int buff, int duration, String msg) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.getItemEffect(buff) != null && this.getPlayer().getGuildId() > 0) {
            MapleStatEffect mse = ii.getItemEffect(buff);
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                    if (chr.getGuildId() != this.getPlayer().getGuildId()) continue;
                    mse.applyTo(chr, chr, true, null, duration);
                    chr.dropMessage(5, "Your guild has gotten a " + msg + " buff.");
                }
            }
        }
    }

    public boolean createAlliance(String alliancename) {
        MapleParty pt = this.c.getPlayer().getParty();
        MapleCharacter otherChar = this.c.getChannelServer().getPlayerStorage().getCharacterById(pt.getMemberByIndex(1).getId());
        if (otherChar == null || otherChar.getId() == this.c.getPlayer().getId()) {
            return false;
        }
        try {
            return World.Alliance.createAlliance(alliancename, this.c.getPlayer().getId(), otherChar.getId(), this.c.getPlayer().getGuildId(), otherChar.getGuildId());
        }
        catch (Exception re) {
            re.printStackTrace();
            return false;
        }
    }

    public boolean addCapacityToAlliance() {
        try {
            MapleGuild gs = World.Guild.getGuild(this.c.getPlayer().getGuildId());
            if (gs != null && this.c.getPlayer().getGuildRank() == 1 && this.c.getPlayer().getAllianceRank() == 1 && World.Alliance.getAllianceLeader(gs.getAllianceId()) == this.c.getPlayer().getId() && World.Alliance.changeAllianceCapacity(gs.getAllianceId())) {
                this.gainMeso(-10000000);
                return true;
            }
        }
        catch (Exception re) {
            re.printStackTrace();
        }
        return false;
    }

    public boolean disbandAlliance() {
        try {
            MapleGuild gs = World.Guild.getGuild(this.c.getPlayer().getGuildId());
            if (gs != null && this.c.getPlayer().getGuildRank() == 1 && this.c.getPlayer().getAllianceRank() == 1 && World.Alliance.getAllianceLeader(gs.getAllianceId()) == this.c.getPlayer().getId() && World.Alliance.disbandAlliance(gs.getAllianceId())) {
                return true;
            }
        }
        catch (Exception re) {
            re.printStackTrace();
        }
        return false;
    }

    public byte getLastMsg() {
        return this.lastMsg;
    }

    public final void setLastMsg(byte last) {
        this.lastMsg = last;
    }

    public int getBossLog(String bossid) {
        return this.getPlayer().getBossLog(bossid);
    }

    public void setBossLog(String bossid) {
        this.getPlayer().setBossLog(bossid);
    }

    public final void maxAllSkills() {
        for (ISkill skil : SkillFactory.getAllSkills()) {
            if (!GameConstants.isApplicableSkill(skil.getId())) continue;
            this.teachSkill(skil.getId(), skil.getMaxLevel(), skil.getMaxLevel());
        }
    }

    public final void resetStats(int str, int dex, int z, int luk) {
        this.c.getPlayer().resetStats(str, dex, z, luk);
    }

    public final boolean dropItem(int slot, int invType, int quantity) {
        MapleInventoryType inv = MapleInventoryType.getByType((byte)invType);
        if (inv == null) {
            return false;
        }
        return MapleInventoryManipulator.drop(this.c, inv, (short)slot, (short)quantity, true);
    }

    public final List<Integer> getAllPotentialInfo() {
        return new ArrayList<Integer>(MapleItemInformationProvider.getInstance().getAllPotentialInfo().keySet());
    }

    public final String getPotentialInfo(int id) {
        List<StructPotentialItem> potInfo = MapleItemInformationProvider.getInstance().getPotentialInfo(id);
        StringBuilder builder = new StringBuilder("#b#ePOTENTIAL INFO FOR ID: ");
        builder.append(id);
        builder.append("#n#k\r\n\r\n");
        int minLevel = 1;
        int maxLevel = 10;
        for (StructPotentialItem item : potInfo) {
            builder.append("#eLevels ");
            builder.append(minLevel);
            builder.append("~");
            builder.append(maxLevel);
            builder.append(": #n");
            builder.append(item.toString());
            minLevel += 10;
            maxLevel += 10;
            builder.append("\r\n");
        }
        return builder.toString();
    }

    public final void sendRPS() {
        this.c.getSession().write((Object)MaplePacketCreator.getRPSMode((byte)8, -1, -1, -1));
    }

    public final void setQuestRecord(Object ch, int questid, String data) {
        ((MapleCharacter)ch).getQuestNAdd(MapleQuest.getInstance(questid)).setCustomData(data);
    }

    public final void doWeddingEffect(Object ch) {
        final MapleCharacter chr = (MapleCharacter)ch;
        this.getMap().broadcastMessage(MaplePacketCreator.yellowChat(this.getPlayer().getName() + "\uff0c\u4f60\u9858\u610f\u5a36 " + chr.getName() + " \u4f5c\u70ba\u4f60\u7684\u59bb\u5b50\u55ce\uff0c\u8207\u5979\u5728\u795e\u8056\u7684\u5a5a\u7d04\u4e2d\u5171\u540c\u751f\u6d3b\uff1f\u7121\u8ad6\u662f\u75be\u75c5\u6216\u5065\u5eb7\u3001\u8ca7\u7aae\u6216\u5bcc\u88d5\u3001\u7f8e\u8c8c\u6216\u5931\u8272\u3001\u9806\u5229\u6216\u5931\u610f\uff0c\u4f60\u90fd\u9858\u610f\u611b\u5979\u3001\u5b89\u6170\u5979\u3001\u5c0a\u656c\u5979\u3001\u4fdd\u8b77\u5979\uff1f\u4e26\u9858\u610f\u5728\u4f60\u5011\u4e00\u751f\u4e4b\u4e2d\u5c0d\u5979\u6c38\u9060\u5fe0\u5fc3\u4e0d\u8b8a\uff1f "));
        Timer.CloneTimer.getInstance().schedule(new Runnable(){

            @Override
            public void run() {
                if (chr == null || NPCConversationManager.this.getPlayer() == null) {
                    NPCConversationManager.this.warpMap(680000500, 0);
                } else {
                    NPCConversationManager.this.getMap().broadcastMessage(MaplePacketCreator.yellowChat(chr.getName() + "\uff0c\u4f60\u9858\u610f\u5ac1 " + NPCConversationManager.this.getPlayer().getName() + " \u4f5c\u70ba\u4f60\u7684\u4e08\u592b\u55ce\uff0c\u8207\u4ed6\u5728\u795e\u8056\u7684\u5a5a\u7d04\u4e2d\u5171\u540c\u751f\u6d3b\uff1f\u7121\u8ad6\u662f\u75be\u75c5\u6216\u5065\u5eb7\u3001\u8ca7\u7aae\u6216\u5bcc\u88d5\u3001\u7f8e\u8c8c\u6216\u5931\u8272\u3001\u9806\u5229\u6216\u5931\u610f\uff0c\u4f60\u90fd\u9858\u610f\u611b\u4ed6\u3001\u5b89\u6170\u4ed6\u3001\u5c0a\u656c\u4ed6\u3001\u4fdd\u8b77\u4ed6\uff1f\u4e26\u9858\u610f\u5728\u4f60\u5011\u4e00\u751f\u4e4b\u4e2d\u5c0d\u4ed6\u6c38\u9060\u5fe0\u5fc3\u4e0d\u8b8a\uff1f "));
                }
            }
        }, 10000L);
        Timer.CloneTimer.getInstance().schedule(new Runnable(){

            @Override
            public void run() {
                if (chr == null || NPCConversationManager.this.getPlayer() == null) {
                    if (NPCConversationManager.this.getPlayer() != null) {
                        NPCConversationManager.this.setQuestRecord(NPCConversationManager.this.getPlayer(), 160001, "3");
                        NPCConversationManager.this.setQuestRecord(NPCConversationManager.this.getPlayer(), 160002, "0");
                    } else if (chr != null) {
                        NPCConversationManager.this.setQuestRecord(chr, 160001, "3");
                        NPCConversationManager.this.setQuestRecord(chr, 160002, "0");
                    }
                    NPCConversationManager.this.warpMap(680000500, 0);
                } else {
                    NPCConversationManager.this.setQuestRecord(NPCConversationManager.this.getPlayer(), 160001, "2");
                    NPCConversationManager.this.setQuestRecord(chr, 160001, "2");
                    NPCConversationManager.this.sendNPCText(NPCConversationManager.this.getPlayer().getName() + " \u548c " + chr.getName() + ", \u795d\u798f\u4f60\u5011\u5728\u672a\u4f86\u7684\u8def\u4e0a\u6c38\u6046\u4e0d\u8b8a\u3001\u5e78\u798f\u7f8e\u6eff!", 9201002);
                    NPCConversationManager.this.getMap().startExtendedMapEffect("\u89aa\u543b\u4f60\u7684\u65b0\u5a18\uff0c " + NPCConversationManager.this.getPlayer().getName() + " !", 5120006);
                    if (chr.getGuildId() > 0) {
                        World.Guild.guildPacket(chr.getGuildId(), MaplePacketCreator.sendMarriage(false, chr.getName()));
                    }
                    if (chr.getFamilyId() > 0) {
                        World.Family.familyPacket(chr.getFamilyId(), MaplePacketCreator.sendMarriage(true, chr.getName()), chr.getId());
                    }
                    if (NPCConversationManager.this.getPlayer().getGuildId() > 0) {
                        World.Guild.guildPacket(NPCConversationManager.this.getPlayer().getGuildId(), MaplePacketCreator.sendMarriage(false, NPCConversationManager.this.getPlayer().getName()));
                    }
                    if (NPCConversationManager.this.getPlayer().getFamilyId() > 0) {
                        World.Family.familyPacket(NPCConversationManager.this.getPlayer().getFamilyId(), MaplePacketCreator.sendMarriage(true, chr.getName()), NPCConversationManager.this.getPlayer().getId());
                    }
                }
            }
        }, 20000L);
    }

    public void \u958b\u555f\u5c0f\u92fc\u73e0(int type) {
        this.c.getSession().write((Object)MaplePacketCreator.openBeans(this.getPlayer().getBeans(), type));
    }

    public void worldMessage(String text) {
        World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, text).getBytes());
    }

    public int getBeans() {
        return this.getClient().getPlayer().getBeans();
    }

    public void gainBeans(int s) {
        this.getPlayer().gainBeans(s);
        this.c.getSession().write((Object)MaplePacketCreator.updateBeans(this.c.getPlayer().getId(), s));
    }
}

