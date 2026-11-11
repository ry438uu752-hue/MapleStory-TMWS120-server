/*
 * Decompiled with CFR 0.152.
 */
package client;

import client.BuddylistEntry;
import client.CharacterNameAndId;
import client.MapleClient;
import database.DatabaseConnection;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import tools.MaplePacketCreator;

public class BuddyList
implements Serializable {
    private static final long serialVersionUID = 1413738569L;
    private Map<Integer, BuddylistEntry> buddies = new LinkedHashMap<Integer, BuddylistEntry>();
    private byte capacity;
    private Deque<CharacterNameAndId> pendingRequests = new LinkedList<CharacterNameAndId>();

    public BuddyList(byte capacity) {
        this.capacity = capacity;
    }

    public boolean contains(int characterId) {
        return this.buddies.containsKey(characterId);
    }

    public boolean containsVisible(int characterId) {
        BuddylistEntry ble = this.buddies.get(characterId);
        if (ble == null) {
            return false;
        }
        return ble.isVisible();
    }

    public byte getCapacity() {
        return this.capacity;
    }

    public void setCapacity(byte capacity) {
        this.capacity = capacity;
    }

    public BuddylistEntry get(int characterId) {
        return this.buddies.get(characterId);
    }

    public BuddylistEntry get(String characterName) {
        String lowerCaseName = characterName.toLowerCase();
        for (BuddylistEntry ble : this.buddies.values()) {
            if (!ble.getName().toLowerCase().equals(lowerCaseName)) continue;
            return ble;
        }
        return null;
    }

    public void put(BuddylistEntry entry) {
        this.buddies.put(entry.getCharacterId(), entry);
    }

    public void remove(int characterId) {
        this.buddies.remove(characterId);
    }

    public Collection<BuddylistEntry> getBuddies() {
        return this.buddies.values();
    }

    public boolean isFull() {
        return this.buddies.size() >= this.capacity;
    }

    public int[] getBuddyIds() {
        int[] buddyIds = new int[this.buddies.size()];
        int i = 0;
        for (BuddylistEntry ble : this.buddies.values()) {
            buddyIds[i++] = ble.getCharacterId();
        }
        return buddyIds;
    }

    public void loadFromTransfer(Map<CharacterNameAndId, Boolean> data) {
        for (Map.Entry<CharacterNameAndId, Boolean> qs : data.entrySet()) {
            CharacterNameAndId buddyid = qs.getKey();
            boolean pair = qs.getValue();
            if (!pair) {
                this.pendingRequests.push(buddyid);
                continue;
            }
            this.put(new BuddylistEntry(buddyid.getName(), buddyid.getId(), buddyid.getGroup(), -1, true, buddyid.getLevel(), buddyid.getJob()));
        }
    }

    public void loadFromDb(int characterId) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT b.buddyid, b.pending, c.name as buddyname, c.job as buddyjob, c.level as buddylevel, b.groupname FROM buddies as b, characters as c WHERE c.id = b.buddyid AND b.characterid = ?");
        ps.setInt(1, characterId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int buddyid = rs.getInt("buddyid");
            String buddyname = rs.getString("buddyname");
            if (rs.getInt("pending") == 1) {
                this.pendingRequests.push(new CharacterNameAndId(buddyid, buddyname, rs.getInt("buddylevel"), rs.getInt("buddyjob"), rs.getString("groupname")));
                continue;
            }
            this.put(new BuddylistEntry(buddyname, buddyid, rs.getString("groupname"), -1, true, rs.getInt("buddylevel"), rs.getInt("buddyjob")));
        }
        rs.close();
        ps.close();
        ps = con.prepareStatement("DELETE FROM buddies WHERE pending = 1 AND characterid = ?");
        ps.setInt(1, characterId);
        ps.executeUpdate();
        ps.close();
    }

    public CharacterNameAndId pollPendingRequest() {
        return this.pendingRequests.pollLast();
    }

    public void addBuddyRequest(MapleClient c, int cidFrom, String nameFrom, int channelFrom, int levelFrom, int jobFrom) {
        this.put(new BuddylistEntry(nameFrom, cidFrom, "\u5176\u4ed6", channelFrom, false, levelFrom, jobFrom));
        if (this.pendingRequests.isEmpty()) {
            c.getSession().write((Object)MaplePacketCreator.requestBuddylistAdd(cidFrom, nameFrom, levelFrom, jobFrom));
        } else {
            this.pendingRequests.push(new CharacterNameAndId(cidFrom, nameFrom, levelFrom, jobFrom, "\u5176\u4ed6"));
        }
    }

    public static enum BuddyAddResult {
        BUDDYLIST_FULL,
        ALREADY_ON_LIST,
        OK;

    }

    public static enum BuddyOperation {
        ADDED,
        DELETED;

    }
}

