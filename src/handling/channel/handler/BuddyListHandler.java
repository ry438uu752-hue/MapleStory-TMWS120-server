/*
 * Decompiled with CFR 0.152.
 */
package handling.channel.handler;

import client.BuddyList;
import client.BuddylistEntry;
import client.CharacterNameAndId;
import client.MapleCharacter;
import client.MapleClient;
import database.DatabaseConnection;
import handling.channel.ChannelServer;
import handling.world.World;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

public class BuddyListHandler {
    private static final void nextPendingRequest(MapleClient c) {
        CharacterNameAndId pendingBuddyRequest = c.getPlayer().getBuddylist().pollPendingRequest();
        if (pendingBuddyRequest != null) {
            c.getSession().write((Object)MaplePacketCreator.requestBuddylistAdd(pendingBuddyRequest.getId(), pendingBuddyRequest.getName(), pendingBuddyRequest.getLevel(), pendingBuddyRequest.getJob()));
        }
    }

    private static final CharacterIdNameBuddyCapacity getCharacterIdAndNameFromDatabase(String name, String group) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE name LIKE ?");
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        CharacterIdNameBuddyCapacity ret = null;
        if (rs.next() && rs.getInt("gm") == 0) {
            ret = new CharacterIdNameBuddyCapacity(rs.getInt("id"), rs.getString("name"), rs.getInt("level"), rs.getInt("job"), group, rs.getInt("buddyCapacity"));
        }
        rs.close();
        ps.close();
        return ret;
    }

    public static final void BuddyOperation(SeekableLittleEndianAccessor slea, MapleClient c) {
        byte mode = slea.readByte();
        BuddyList buddylist = c.getPlayer().getBuddylist();
        if (mode == 1) {
            String addName = slea.readMapleAsciiString();
            String groupName = slea.readMapleAsciiString();
            BuddylistEntry ble = buddylist.get(addName);
            if (addName.length() > 13 || groupName.length() > 16) {
                return;
            }
            if (ble != null && (ble.getGroup().equals(groupName) || !ble.isVisible())) {
                c.getSession().write((Object)MaplePacketCreator.buddylistMessage((byte)11));
            } else if (ble != null && ble.isVisible()) {
                ble.setGroup(groupName);
                c.getSession().write((Object)MaplePacketCreator.updateBuddylist(buddylist.getBuddies()));
            } else if (buddylist.isFull()) {
                c.getSession().write((Object)MaplePacketCreator.buddylistMessage((byte)11));
            } else {
                try {
                    CharacterIdNameBuddyCapacity charWithId = null;
                    int channel = World.Find.findChannel(addName);
                    MapleCharacter otherChar = null;
                    if (channel > 0) {
                        otherChar = ChannelServer.getInstance(channel).getPlayerStorage().getCharacterByName(addName);
                        if (!otherChar.isGM() || c.getPlayer().isGM()) {
                            charWithId = new CharacterIdNameBuddyCapacity(otherChar.getId(), otherChar.getName(), otherChar.getLevel(), otherChar.getJob(), groupName, otherChar.getBuddylist().getCapacity());
                        }
                    } else {
                        charWithId = BuddyListHandler.getCharacterIdAndNameFromDatabase(addName, groupName);
                    }
                    if (charWithId != null) {
                        BuddyList.BuddyAddResult buddyAddResult = null;
                        if (channel > 0) {
                            buddyAddResult = World.Buddy.requestBuddyAdd(addName, c.getChannel(), c.getPlayer().getId(), c.getPlayer().getName(), c.getPlayer().getLevel(), c.getPlayer().getJob());
                        } else {
                            Connection con = DatabaseConnection.getConnection();
                            PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) as buddyCount FROM buddies WHERE characterid = ? AND pending = 0");
                            ps.setInt(1, charWithId.getId());
                            ResultSet rs = ps.executeQuery();
                            if (!rs.next()) {
                                ps.close();
                                rs.close();
                                throw new RuntimeException("Result set expected");
                            }
                            int count = rs.getInt("buddyCount");
                            if (count >= charWithId.getBuddyCapacity()) {
                                buddyAddResult = BuddyList.BuddyAddResult.BUDDYLIST_FULL;
                            }
                            rs.close();
                            ps.close();
                            ps = con.prepareStatement("SELECT pending FROM buddies WHERE characterid = ? AND buddyid = ?");
                            ps.setInt(1, charWithId.getId());
                            ps.setInt(2, c.getPlayer().getId());
                            rs = ps.executeQuery();
                            if (rs.next()) {
                                buddyAddResult = BuddyList.BuddyAddResult.ALREADY_ON_LIST;
                            }
                            rs.close();
                            ps.close();
                        }
                        if (buddyAddResult == BuddyList.BuddyAddResult.BUDDYLIST_FULL) {
                            c.getSession().write((Object)MaplePacketCreator.buddylistMessage((byte)12));
                        } else {
                            int displayChannel = -1;
                            int otherCid = charWithId.getId();
                            if (buddyAddResult == BuddyList.BuddyAddResult.ALREADY_ON_LIST && channel > 0) {
                                displayChannel = channel;
                                BuddyListHandler.notifyRemoteChannel(c, channel, otherCid, groupName, BuddyList.BuddyOperation.ADDED);
                            } else if (buddyAddResult != BuddyList.BuddyAddResult.ALREADY_ON_LIST && channel > 0) {
                                Connection con = DatabaseConnection.getConnection();
                                PreparedStatement ps = con.prepareStatement("INSERT INTO buddies (`characterid`, `buddyid`, `groupname`, `pending`) VALUES (?, ?, ?, 1)");
                                ps.setInt(1, charWithId.getId());
                                ps.setInt(2, c.getPlayer().getId());
                                ps.setString(3, groupName);
                                ps.executeUpdate();
                                ps.close();
                            }
                            buddylist.put(new BuddylistEntry(charWithId.getName(), otherCid, groupName, displayChannel, true, charWithId.getLevel(), charWithId.getJob()));
                            c.getSession().write((Object)MaplePacketCreator.updateBuddylist(buddylist.getBuddies()));
                        }
                    } else {
                        c.getSession().write((Object)MaplePacketCreator.buddylistMessage((byte)15));
                    }
                }
                catch (SQLException e) {
                    System.err.println("SQL THROW" + e);
                }
            }
            BuddyListHandler.nextPendingRequest(c);
        } else if (mode == 2) {
            int otherCid = slea.readInt();
            if (!buddylist.isFull()) {
                try {
                    PreparedStatement ps;
                    Connection con;
                    int channel = World.Find.findChannel(otherCid);
                    String otherName = null;
                    int otherLevel = 0;
                    int otherJob = 0;
                    if (channel < 0) {
                        con = DatabaseConnection.getConnection();
                        ps = con.prepareStatement("SELECT name, level, job FROM characters WHERE id = ?");
                        ps.setInt(1, otherCid);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            otherName = rs.getString("name");
                            otherLevel = rs.getInt("level");
                            otherJob = rs.getInt("job");
                        }
                        rs.close();
                        ps.close();
                    } else {
                        MapleCharacter otherChar = ChannelServer.getInstance(channel).getPlayerStorage().getCharacterById(otherCid);
                        otherName = otherChar.getName();
                        otherLevel = otherChar.getLevel();
                        otherJob = otherChar.getJob();
                    }
                    if (otherName != null) {
                        buddylist.put(new BuddylistEntry(otherName, otherCid, "\u5176\u4ed6", channel, true, otherLevel, otherJob));
                        c.getSession().write((Object)MaplePacketCreator.updateBuddylist(buddylist.getBuddies()));
                        BuddyListHandler.notifyRemoteChannel(c, channel, otherCid, "\u5176\u4ed6", BuddyList.BuddyOperation.ADDED);
                    }
                    con = DatabaseConnection.getConnection();
                    ps = con.prepareStatement("DELETE FROM buddies WHERE pending = 1 AND characterid = ? AND buddyid = ?");
                    ps.setInt(1, c.getPlayer().getId());
                    ps.setInt(2, otherCid);
                    ps.executeUpdate();
                    ps.close();
                }
                catch (SQLException e) {
                    System.err.println("SQL THROW" + e);
                }
            } else {
                c.getSession().write((Object)MaplePacketCreator.buddylistMessage((byte)11));
            }
            BuddyListHandler.nextPendingRequest(c);
        } else if (mode == 3) {
            int otherCid = slea.readInt();
            BuddylistEntry blz = buddylist.get(otherCid);
            if (blz != null && blz.isVisible()) {
                BuddyListHandler.notifyRemoteChannel(c, World.Find.findChannel(otherCid), otherCid, blz.getGroup(), BuddyList.BuddyOperation.DELETED);
            }
            buddylist.remove(otherCid);
            c.getSession().write((Object)MaplePacketCreator.updateBuddylist(c.getPlayer().getBuddylist().getBuddies()));
            BuddyListHandler.nextPendingRequest(c);
        } else {
            System.out.println("Unknown buddylist: " + slea.toString());
        }
    }

    private static final void notifyRemoteChannel(MapleClient c, int remoteChannel, int otherCid, String group, BuddyList.BuddyOperation operation) {
        MapleCharacter player = c.getPlayer();
        if (remoteChannel > 0) {
            World.Buddy.buddyChanged(otherCid, player.getId(), player.getName(), c.getChannel(), operation, player.getLevel(), player.getJob(), group);
        }
    }

    private static final class CharacterIdNameBuddyCapacity
    extends CharacterNameAndId {
        private int buddyCapacity;

        public CharacterIdNameBuddyCapacity(int id, String name, int level, int job, String group, int buddyCapacity) {
            super(id, name, level, job, group);
            this.buddyCapacity = buddyCapacity;
        }

        public int getBuddyCapacity() {
            return this.buddyCapacity;
        }
    }
}

