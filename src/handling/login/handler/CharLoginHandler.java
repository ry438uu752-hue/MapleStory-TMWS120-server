/*
 * Decompiled with CFR 0.152.
 */
package handling.login.handler;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import client.inventory.IItem;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import handling.channel.ChannelServer;
import handling.login.LoginInformationProvider;
import handling.login.LoginServer;
import handling.login.LoginWorker;
import handling.login.handler.AutoRegister;
import java.util.Calendar;
import java.util.List;
import server.MapleItemInformationProvider;
import server.ServerProperties;
import server.quest.MapleQuest;
import tools.KoreanDateUtil;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.LoginPacket;

public class CharLoginHandler {
    private static final boolean loginFailCount(MapleClient c) {
        c.loginAttempt = (short)(c.loginAttempt + 1);
        return c.loginAttempt > 5;
    }

    public static final void Welcome(MapleClient c) {
    }

    public static final void login(SeekableLittleEndianAccessor slea, MapleClient c) {
        String login = slea.readMapleAsciiString();
        String pwd = slea.readMapleAsciiString();
        c.setAccountName(login);
        boolean ipBan = c.hasBannedIP();
        boolean macBan = c.hasBannedMac();
        int loginok = 0;
        if (!(!Boolean.parseBoolean(ServerProperties.getProperty("tms.AutoRegister")) || !AutoRegister.autoRegister || AutoRegister.getAccountExists(login) || c.hasBannedIP() && c.hasBannedMac())) {
            if (pwd.equalsIgnoreCase("disconnect") || pwd.equalsIgnoreCase("fixme")) {
                c.getSession().write((Object)MaplePacketCreator.serverNotice(1, "This password is invalid."));
                c.getSession().write((Object)LoginPacket.getLoginFailed(1));
                return;
            }
            AutoRegister.createAccount(login, pwd, c.getSession().getRemoteAddress().toString());
            if (AutoRegister.success) {
                // empty if block
            }
        }
        loginok = c.login(login, pwd, ipBan || macBan);
        Calendar tempbannedTill = c.getTempBanCalendar();
        if (loginok == 0 && (ipBan || macBan) && !c.isGm()) {
            loginok = 3;
            if (macBan) {
                MapleCharacter.ban(c.getSession().getRemoteAddress().toString().split(":")[0], "Enforcing account ban, account " + login, false, 4, false);
            }
        }
        if (loginok != 0) {
            if (!CharLoginHandler.loginFailCount(c)) {
                c.getSession().write((Object)LoginPacket.getLoginFailed(loginok));
            }
        } else if (tempbannedTill.getTimeInMillis() != 0L) {
            if (!CharLoginHandler.loginFailCount(c)) {
                c.getSession().write((Object)LoginPacket.getTempBan(KoreanDateUtil.getTempBanTimestamp(tempbannedTill.getTimeInMillis()), c.getBanReason()));
            }
        } else {
            c.loginAttempt = 0;
            LoginWorker.registerClient(c);
        }
    }

    public static final void SetGenderRequest(SeekableLittleEndianAccessor slea, MapleClient c) {
        String username = slea.readMapleAsciiString();
        String password = slea.readMapleAsciiString();
        if (!c.getAccountName().equals(username) || c.getSecondPassword() != null) {
            c.getSession().close();
            return;
        }
        c.setGender(slea.readByte());
        c.setSecondPassword(password);
        c.updateSecondPassword();
        c.updateGender();
        c.getSession().write((Object)LoginPacket.getGenderChanged(c));
        c.updateLoginState(0, c.getSessionIPAddress());
    }

    public static final void ServerListRequest(MapleClient c) {
        c.getSession().write((Object)LoginPacket.getServerList(0, LoginServer.getServerName(), LoginServer.getLoad()));
        c.getSession().write((Object)LoginPacket.getEndOfServerList());
    }

    public static final void ServerStatusRequest(MapleClient c) {
        int userLimit;
        int numPlayer = LoginServer.getUsersOn();
        if (numPlayer >= (userLimit = LoginServer.getUserLimit())) {
            c.getSession().write((Object)LoginPacket.getServerStatus(2));
        } else if (numPlayer * 2 >= userLimit) {
            c.getSession().write((Object)LoginPacket.getServerStatus(1));
        } else {
            c.getSession().write((Object)LoginPacket.getServerStatus(0));
        }
    }

    public static final void CharlistRequest(SeekableLittleEndianAccessor slea, MapleClient c) {
        slea.readByte();
        byte server = slea.readByte();
        int channel = slea.readByte() + 1;
        c.setWorld(server);
        c.setChannel(channel);
        List<MapleCharacter> chars = c.loadCharacters(server);
        if (chars != null) {
            c.getSession().write((Object)LoginPacket.getCharList(c.getSecondPassword() != null, chars, c.getCharacterSlots()));
        } else {
            c.getSession().close();
        }
    }

    public static final void CheckCharName(String name, MapleClient c) {
        c.getSession().write((Object)LoginPacket.charNameResponse(name, !MapleCharacterUtil.canCreateChar(name) || LoginInformationProvider.getInstance().isForbiddenName(name)));
    }

    public static final void CreateChar(SeekableLittleEndianAccessor slea, MapleClient c) {
        String name = slea.readMapleAsciiString();
        int JobType = slea.readInt();
        short db = slea.readShort();
        int face = slea.readInt();
        int hair = slea.readInt();
        boolean hairColor = false;
        boolean skinColor = false;
        int top = slea.readInt();
        int bottom = slea.readInt();
        int shoes = slea.readInt();
        int weapon = slea.readInt();
        byte gender = c.getGender();
        if (gender == 0) {
            if (face != 20100 && face != 20401 && face != 20402) {
                return;
            }
            if (hair != 30030 && hair != 30027 && hair != 30000) {
                return;
            }
            if (top != 1040002 && top != 1040006 && top != 1040010 && top != 1042167 && top != 1042180) {
                return;
            }
            if (bottom != 1060002 && bottom != 1060006 && bottom != 1062115 && bottom != 1060138) {
                return;
            }
        } else if (gender == 1) {
            if (face != 21002 && face != 21700 && face != 21201) {
                return;
            }
            if (hair != 31002 && hair != 31047 && hair != 31057) {
                return;
            }
            if (top != 1041002 && top != 1041006 && top != 1041010 && top != 1041011 && top != 1042167 && top != 1042180) {
                return;
            }
            if (bottom != 1061002 && bottom != 1061008 && bottom != 1062115 && bottom != 1061160) {
                return;
            }
        } else {
            return;
        }
        if (shoes != 1072001 && shoes != 1072005 && shoes != 1072037 && shoes != 1072038 && shoes != 1072383 && shoes != 1072418) {
            return;
        }
        if (weapon != 1302000 && weapon != 1322005 && weapon != 1312004 && weapon != 1442079 && weapon != 1302132) {
            return;
        }
        MapleCharacter newchar = MapleCharacter.getDefault(c, JobType);
        newchar.setWorld((byte)c.getWorld());
        newchar.setFace(face);
        newchar.setHair(hair + 0);
        newchar.setGender(gender);
        newchar.setName(name);
        newchar.setSkinColor((byte)0);
        MapleInventory equip = newchar.getInventory(MapleInventoryType.EQUIPPED);
        MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();
        IItem item = li.getEquipById(top);
        item.setPosition((short)-5);
        equip.addFromDB(item);
        item = li.getEquipById(bottom);
        item.setPosition((short)-6);
        equip.addFromDB(item);
        item = li.getEquipById(shoes);
        item.setPosition((short)-7);
        equip.addFromDB(item);
        item = li.getEquipById(weapon);
        item.setPosition((short)-11);
        equip.addFromDB(item);
        switch (JobType) {
            case 0: {
                newchar.setQuestAdd(MapleQuest.getInstance(20022), (byte)1, "1");
                newchar.setQuestAdd(MapleQuest.getInstance(20010), (byte)1, null);
                newchar.setQuestAdd(MapleQuest.getInstance(20000), (byte)1, null);
                newchar.setQuestAdd(MapleQuest.getInstance(20015), (byte)1, null);
                newchar.setQuestAdd(MapleQuest.getInstance(20020), (byte)1, null);
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161047, (short)0, (short)1, (byte)0));
                break;
            }
            case 1: {
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161001, (short)0, (short)1, (byte)0));
                break;
            }
            case 2: {
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161048, (short)0, (short)1, (byte)0));
                break;
            }
            case 3: {
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161052, (short)0, (short)1, (byte)0));
            }
        }
        if (MapleCharacterUtil.canCreateChar(name) && !LoginInformationProvider.getInstance().isForbiddenName(name)) {
            MapleCharacter.saveNewCharToDB(newchar, JobType, JobType == 1 && db > 0);
            c.getSession().write((Object)LoginPacket.addNewCharEntry(newchar, true));
            c.createdChar(newchar.getId());
        } else {
            c.getSession().write((Object)LoginPacket.addNewCharEntry(newchar, false));
        }
    }

    public static final void DeleteChar(SeekableLittleEndianAccessor slea, MapleClient c) {
        slea.readByte();
        String Secondpw_Client = null;
        Secondpw_Client = slea.readMapleAsciiString();
        int Character_ID = slea.readInt();
        if (!c.login_Auth(Character_ID)) {
            c.getSession().write((Object)LoginPacket.secondPwError((byte)20));
            return;
        }
        byte state = 0;
        if (c.getSecondPassword() != null) {
            if (Secondpw_Client == null) {
                c.getSession().close();
                return;
            }
            if (!c.CheckSecondPassword(Secondpw_Client)) {
                state = (byte)16;
            }
        }
        if (state == 0) {
            state = (byte)c.deleteCharacter(Character_ID);
        }
        c.getSession().write((Object)LoginPacket.deleteCharResponse(Character_ID, state));
    }

    public static final void Character_WithoutSecondPassword(SeekableLittleEndianAccessor slea, MapleClient c) {
        int charId = slea.readInt();
        if (!c.login_Auth(charId)) {
            c.getSession().close();
            return;
        }
        if (c.getIdleTask() != null) {
            c.getIdleTask().cancel(true);
        }
        c.updateLoginState(1, c.getSessionIPAddress());
        c.getSession().write((Object)MaplePacketCreator.getServerIP(Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1]), charId));
    }

    public static final void Character_WithSecondPassword(SeekableLittleEndianAccessor slea, MapleClient c) {
        String password = slea.readMapleAsciiString();
        int charId = slea.readInt();
        if (CharLoginHandler.loginFailCount(c) || c.getSecondPassword() == null || !c.login_Auth(charId)) {
            c.getSession().close();
            return;
        }
        if (c.CheckSecondPassword(password)) {
            c.updateMacs(slea.readMapleAsciiString());
            if (c.getIdleTask() != null) {
                c.getIdleTask().cancel(true);
            }
            c.updateLoginState(1, c.getSessionIPAddress());
            c.getSession().write((Object)MaplePacketCreator.getServerIP(Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1]), charId));
        } else {
            c.getSession().write((Object)LoginPacket.secondPwError((byte)20));
        }
    }
}

