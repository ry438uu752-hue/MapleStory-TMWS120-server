/*
 * Decompiled with CFR 0.152.
 */
package tools.packet;

import client.MapleCharacter;
import client.MapleClient;
import handling.MaplePacket;
import handling.SendPacketOpcode;
import handling.login.LoginServer;
import java.util.List;
import java.util.Map;
import java.util.Set;
import server.Randomizer;
import tools.HexTool;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketHelper;

public class LoginPacket {
    public static final MaplePacket getHello(short mapleVersion, byte[] sendIv, byte[] recvIv) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);
        mplew.writeShort(14);
        mplew.writeShort(mapleVersion);
        mplew.writeMapleAsciiString("1");
        mplew.write(recvIv);
        mplew.write(sendIv);
        mplew.write(6);
        return mplew.getPacket();
    }

    public static final MaplePacket getPing() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);
        mplew.writeShort(SendPacketOpcode.PING.getValue());
        return mplew.getPacket();
    }

    public static final MaplePacket getLoginAUTH() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(13);
        mplew.writeShort(SendPacketOpcode.LOGIN_AUTH.getValue());
        int rand = Randomizer.nextInt(2);
        mplew.writeMapleAsciiString("MapLogin" + (rand == 0 ? "" : String.valueOf(rand)));
        return mplew.getPacket();
    }

    public static final MaplePacket StrangeDATA() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);
        mplew.writeShort(18);
        mplew.writeMapleAsciiString("30819F300D06092A864886F70D010101050003818D0030818902818100994F4E66B003A7843C944E67BE4375203DAA203C676908E59839C9BADE95F53E848AAFE61DB9C09E80F48675CA2696F4E897B7F18CCB6398D221C4EC5823D11CA1FB9764A78F84711B8B6FCA9F01B171A51EC66C02CDA9308887CEE8E59C4FF0B146BF71F697EB11EDCEBFCE02FB0101A7076A3FEB64F6F6022C8417EB6B87270203010001");
        return mplew.getPacket();
    }

    public static final MaplePacket getLoginFailed(int reason) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);
        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.write(reason);
        mplew.writeShort(0);
        return mplew.getPacket();
    }

    public static final MaplePacket getPermBan(byte reason) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);
        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.writeShort(2);
        mplew.write(0);
        mplew.write(reason);
        mplew.write(HexTool.getByteArrayFromHexString("01 01 01 01 00"));
        return mplew.getPacket();
    }

    public static final MaplePacket getTempBan(long timestampTill, byte reason) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(17);
        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.write(2);
        mplew.write(HexTool.getByteArrayFromHexString("00 00 00 00 00"));
        mplew.write(reason);
        mplew.writeLong(timestampTill);
        return mplew.getPacket();
    }

    public static final MaplePacket getGenderChanged(MapleClient client) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GENDER_SET.getValue());
        mplew.writeMapleAsciiString(client.getAccountName());
        return mplew.getPacket();
    }

    public static final MaplePacket getGenderNeeded(MapleClient client) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CHOOSE_GENDER.getValue());
        mplew.writeMapleAsciiString(client.getAccountName());
        return mplew.getPacket();
    }

    public static final MaplePacket getAuthSuccessRequest(MapleClient client) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.write(0);
        mplew.writeInt(client.getAccID());
        mplew.write(client.getGender());
        mplew.writeShort(client.isGm() ? 1 : 0);
        mplew.writeInt(0);
        mplew.writeMapleAsciiString(client.getAccountName());
        mplew.writeLong(0L);
        mplew.writeLong(0L);
        mplew.writeLong(0L);
        return mplew.getPacket();
    }

    public static final MaplePacket deleteCharResponse(int cid, int state) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.DELETE_CHAR_RESPONSE.getValue());
        mplew.writeInt(cid);
        mplew.write(state);
        return mplew.getPacket();
    }

    public static final MaplePacket secondPwError(byte mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
        mplew.writeShort(SendPacketOpcode.SECONDPW_ERROR.getValue());
        mplew.write(mode);
        return mplew.getPacket();
    }

    public static final MaplePacket getServerList(int serverId, String serverName, Map<Integer, Integer> channelLoad) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SERVERLIST.getValue());
        mplew.write(serverId);
        mplew.writeMapleAsciiString(serverName);
        mplew.write(LoginServer.getFlag());
        mplew.writeMapleAsciiString(LoginServer.getEventMessage());
        mplew.writeShort(100);
        mplew.writeShort(100);
        int lastChannel = 1;
        Set<Integer> channels = channelLoad.keySet();
        for (int i = 30; i > 0; --i) {
            if (!channels.contains(i)) continue;
            lastChannel = i;
            break;
        }
        mplew.write(lastChannel);
        for (int i = 1; i <= lastChannel; ++i) {
            int load = channels.contains(i) ? channelLoad.get(i) : 1200;
            mplew.writeMapleAsciiString(serverName + "-" + i);
            mplew.writeInt(load);
            mplew.write(serverId);
            mplew.writeShort(i - 1);
        }
        String[] bubbleMsg = LoginServer.getBubbleMessage().split(",");
        String[] bubbleMsgPos = LoginServer.getBubbleMessagePos().split(",");
        mplew.writeShort(bubbleMsg.length);
        for (int i = 0; i < bubbleMsg.length; ++i) {
            String[] Pos2 = bubbleMsgPos[i].split("\\.");
            mplew.writeShort(Integer.parseInt(Pos2[0]));
            mplew.writeShort(Integer.parseInt(Pos2[1]));
            mplew.writeMapleAsciiString(bubbleMsg[i]);
        }
        return mplew.getPacket();
    }

    public static final MaplePacket getEndOfServerList() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SERVERLIST.getValue());
        mplew.write(255);
        return mplew.getPacket();
    }

    public static final MaplePacket getServerStatus(int status) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SERVERSTATUS.getValue());
        mplew.writeShort(status);
        return mplew.getPacket();
    }

    public static final MaplePacket getCharList(boolean secondpw, List<MapleCharacter> chars, int charslots) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CHARLIST.getValue());
        mplew.write(0);
        mplew.writeInt(1000000);
        mplew.write(chars.size());
        for (MapleCharacter chr : chars) {
            LoginPacket.addCharEntry(mplew, chr, !chr.isGM() && chr.getLevel() >= 10);
        }
        mplew.writeShort(3);
        mplew.writeLong(charslots);
        mplew.writeInt(0);
        mplew.writeLong(-Randomizer.nextLong());
        return mplew.getPacket();
    }

    public static final MaplePacket addNewCharEntry(MapleCharacter chr, boolean worked) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ADD_NEW_CHAR_ENTRY.getValue());
        mplew.write(worked ? 0 : 1);
        LoginPacket.addCharEntry(mplew, chr, false);
        return mplew.getPacket();
    }

    public static final MaplePacket charNameResponse(String charname, boolean nameUsed) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CHAR_NAME_RESPONSE.getValue());
        mplew.writeMapleAsciiString(charname);
        mplew.write(nameUsed ? 1 : 0);
        return mplew.getPacket();
    }

    private static final void addCharEntry(MaplePacketLittleEndianWriter mplew, MapleCharacter chr, boolean ranking) {
        PacketHelper.addCharStats(mplew, chr);
        PacketHelper.addCharLook(mplew, chr, true);
        mplew.write(0);
        mplew.write(ranking ? 1 : 0);
        if (ranking) {
            mplew.writeInt(chr.getRank());
            mplew.writeInt(chr.getRankMove());
            mplew.writeInt(chr.getJobRank());
            mplew.writeInt(chr.getJobRankMove());
        }
    }
}

