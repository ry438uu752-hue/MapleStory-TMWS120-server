/*
 * Decompiled with CFR 0.152.
 */
package handling.world.family;

import client.MapleCharacter;
import database.DatabaseConnection;
import handling.MaplePacket;
import handling.world.World;
import handling.world.family.MapleFamilyCharacter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import tools.MaplePacketCreator;
import tools.packet.FamilyPacket;

public class MapleFamily
implements Serializable {
    public static final long serialVersionUID = 6322150443228168192L;
    private final Map<Integer, MapleFamilyCharacter> members = new ConcurrentHashMap<Integer, MapleFamilyCharacter>();
    private String leadername = null;
    private String notice;
    private int id;
    private int leaderid;
    private int generations = 0;
    private boolean proper = true;
    private boolean bDirty = false;
    private boolean changed = false;

    public MapleFamily(int fid) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM families WHERE familyid = ?");
            ps.setInt(1, fid);
            ResultSet rs = ps.executeQuery();
            if (!rs.first()) {
                rs.close();
                ps.close();
                this.id = -1;
                return;
            }
            this.id = fid;
            this.leaderid = rs.getInt("leaderid");
            this.notice = rs.getString("notice");
            rs.close();
            ps.close();
            ps = con.prepareStatement("SELECT id, name, level, job, seniorid, junior1, junior2, currentrep, totalrep FROM characters WHERE familyid = ?");
            ps.setInt(1, fid);
            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getInt("id") == this.leaderid) {
                    this.leadername = rs.getString("name");
                }
                this.members.put(rs.getInt("id"), new MapleFamilyCharacter(rs.getInt("id"), rs.getShort("level"), rs.getString("name"), -1, rs.getInt("job"), fid, rs.getInt("seniorid"), rs.getInt("junior1"), rs.getInt("junior2"), rs.getInt("currentrep"), rs.getInt("totalrep"), false));
            }
            rs.close();
            ps.close();
            if (this.leadername == null || this.members.size() < 2) {
                System.err.println("Leader " + this.leaderid + " isn't in family " + this.id + ".  Impossible... family is disbanding.");
                this.writeToDB(true);
                this.proper = false;
                return;
            }
            for (MapleFamilyCharacter mfc : this.members.values()) {
                MapleFamilyCharacter mfc2;
                if (mfc.getJunior1() > 0 && (this.getMFC(mfc.getJunior1()) == null || mfc.getId() == mfc.getJunior1())) {
                    mfc.setJunior1(0);
                }
                if (mfc.getJunior2() > 0 && (this.getMFC(mfc.getJunior2()) == null || mfc.getId() == mfc.getJunior2() || mfc.getJunior1() == mfc.getJunior2())) {
                    mfc.setJunior2(0);
                }
                if (mfc.getSeniorId() > 0 && (this.getMFC(mfc.getSeniorId()) == null || mfc.getId() == mfc.getSeniorId())) {
                    mfc.setSeniorId(0);
                }
                if (mfc.getJunior2() > 0 && mfc.getJunior1() <= 0) {
                    mfc.setJunior1(mfc.getJunior2());
                    mfc.setJunior2(0);
                }
                if (mfc.getJunior1() > 0) {
                    mfc2 = this.getMFC(mfc.getJunior1());
                    if (mfc2.getJunior1() == mfc.getId()) {
                        mfc2.setJunior1(0);
                    }
                    if (mfc2.getJunior2() == mfc.getId()) {
                        mfc2.setJunior2(0);
                    }
                    if (mfc2.getSeniorId() != mfc.getId()) {
                        mfc2.setSeniorId(mfc.getId());
                    }
                }
                if (mfc.getJunior2() <= 0) continue;
                mfc2 = this.getMFC(mfc.getJunior2());
                if (mfc2.getJunior1() == mfc.getId()) {
                    mfc2.setJunior1(0);
                }
                if (mfc2.getJunior2() == mfc.getId()) {
                    mfc2.setJunior2(0);
                }
                if (mfc2.getSeniorId() == mfc.getId()) continue;
                mfc2.setSeniorId(mfc.getId());
            }
            this.resetPedigree();
            this.resetDescendants();
            this.resetGens();
        }
        catch (SQLException se) {
            System.err.println("unable to read family information from sql");
            se.printStackTrace();
        }
    }

    public int getGens() {
        return this.generations;
    }

    public void resetPedigree() {
        for (MapleFamilyCharacter mfc : this.members.values()) {
            mfc.resetPedigree(this);
        }
        this.bDirty = true;
    }

    public void resetGens() {
        MapleFamilyCharacter mfc = this.getMFC(this.leaderid);
        if (mfc != null) {
            this.generations = mfc.resetGenerations(this);
        }
        this.bDirty = true;
    }

    public void resetDescendants() {
        MapleFamilyCharacter mfc = this.getMFC(this.leaderid);
        if (mfc != null) {
            mfc.resetDescendants(this);
        }
        this.bDirty = true;
    }

    public boolean isProper() {
        return this.proper;
    }

    public static final Collection<MapleFamily> loadAll() {
        ArrayList<MapleFamily> ret = new ArrayList<MapleFamily>();
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT familyid FROM families");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                MapleFamily g = new MapleFamily(rs.getInt("familyid"));
                if (g.getId() <= 0) continue;
                ret.add(g);
            }
            rs.close();
            ps.close();
        }
        catch (SQLException se) {
            System.err.println("unable to read family information from sql");
            se.printStackTrace();
        }
        return ret;
    }

    public final void writeToDB(boolean bDisband) {
        try {
            Connection con = DatabaseConnection.getConnection();
            if (!bDisband) {
                if (this.changed) {
                    PreparedStatement ps = con.prepareStatement("UPDATE families SET notice = ? WHERE familyid = ?");
                    ps.setString(1, this.notice);
                    ps.setInt(2, this.id);
                    ps.execute();
                    ps.close();
                }
                this.changed = false;
            } else {
                PreparedStatement ps = con.prepareStatement("DELETE FROM families WHERE familyid = ?");
                ps.setInt(1, this.id);
                ps.execute();
                ps.close();
            }
        }
        catch (SQLException se) {
            System.err.println("Error saving family to SQL");
            se.printStackTrace();
        }
    }

    public final int getId() {
        return this.id;
    }

    public final int getLeaderId() {
        return this.leaderid;
    }

    public final String getNotice() {
        if (this.notice == null) {
            return "";
        }
        return this.notice;
    }

    public final String getLeaderName() {
        return this.leadername;
    }

    public final void broadcast(MaplePacket packet, List<Integer> cids) {
        this.broadcast(packet, -1, FCOp.NONE, cids);
    }

    public final void broadcast(MaplePacket packet, int exception, List<Integer> cids) {
        this.broadcast(packet, exception, FCOp.NONE, cids);
    }

    public final void broadcast(MaplePacket packet, int exceptionId, FCOp bcop, List<Integer> cids) {
        this.buildNotifications();
        if (this.members.size() < 2) {
            this.bDirty = true;
            return;
        }
        for (MapleFamilyCharacter mgc : this.members.values()) {
            if (cids != null && !cids.contains(mgc.getId())) continue;
            if (bcop == FCOp.DISBAND) {
                if (mgc.isOnline()) {
                    World.Family.setFamily(0, 0, 0, 0, mgc.getCurrentRep(), mgc.getTotalRep(), mgc.getId());
                    continue;
                }
                MapleFamily.setOfflineFamilyStatus(0, 0, 0, 0, mgc.getCurrentRep(), mgc.getTotalRep(), mgc.getId());
                continue;
            }
            if (!mgc.isOnline() || mgc.getId() == exceptionId) continue;
            World.Broadcast.sendFamilyPacket(mgc.getId(), packet, exceptionId, this.id);
        }
    }

    private final void buildNotifications() {
        if (!this.bDirty) {
            return;
        }
        Iterator<Map.Entry<Integer, MapleFamilyCharacter>> toRemove = this.members.entrySet().iterator();
        while (toRemove.hasNext()) {
            MapleFamilyCharacter mfc = toRemove.next().getValue();
            if (mfc.getJunior1() > 0 && this.getMFC(mfc.getJunior1()) == null) {
                mfc.setJunior1(0);
            }
            if (mfc.getJunior2() > 0 && this.getMFC(mfc.getJunior2()) == null) {
                mfc.setJunior2(0);
            }
            if (mfc.getSeniorId() > 0 && this.getMFC(mfc.getSeniorId()) == null) {
                mfc.setSeniorId(0);
            }
            if (mfc.getFamilyId() == this.id) continue;
            toRemove.remove();
        }
        if (this.members.size() < 2 && World.Family.getFamily(this.id) != null) {
            World.Family.disbandFamily(this.id);
        }
        this.bDirty = false;
    }

    public final void setOnline(int cid, boolean online2, int channel) {
        MapleFamilyCharacter mgc = this.getMFC(cid);
        if (mgc != null && mgc.getFamilyId() == this.id) {
            if (mgc.isOnline() != online2) {
                this.broadcast(FamilyPacket.familyLoggedIn(online2, mgc.getName()), cid, mgc.getId() == this.leaderid ? null : mgc.getPedigree());
            }
            mgc.setOnline(online2);
            mgc.setChannel((byte)channel);
        }
        this.bDirty = true;
    }

    public final int setRep(int cid, int addrep, int oldLevel) {
        MapleFamilyCharacter mgc = this.getMFC(cid);
        if (mgc != null && mgc.getFamilyId() == this.id) {
            if (oldLevel > mgc.getLevel()) {
                addrep /= 2;
            }
            if (mgc.isOnline()) {
                ArrayList<Integer> dummy = new ArrayList<Integer>();
                dummy.add(mgc.getId());
                this.broadcast(FamilyPacket.changeRep(addrep), -1, dummy);
                World.Family.setFamily(this.id, mgc.getSeniorId(), mgc.getJunior1(), mgc.getJunior2(), mgc.getCurrentRep() + addrep, mgc.getTotalRep() + addrep, mgc.getId());
            } else {
                MapleFamily.setOfflineFamilyStatus(this.id, mgc.getSeniorId(), mgc.getJunior1(), mgc.getJunior2(), mgc.getCurrentRep() + addrep, mgc.getTotalRep() + addrep, mgc.getId());
            }
            return mgc.getSeniorId();
        }
        return 0;
    }

    public final MapleFamilyCharacter addFamilyMemberInfo(MapleCharacter mc, int seniorid, int junior1, int junior2) {
        MapleFamilyCharacter ret = new MapleFamilyCharacter(mc, this.id, seniorid, junior1, junior2);
        this.members.put(mc.getId(), ret);
        ret.resetPedigree(this);
        this.bDirty = true;
        ArrayList<Integer> toRemove = new ArrayList<Integer>();
        for (int i = 0; i < ret.getPedigree().size(); ++i) {
            if (ret.getPedigree().get(i).intValue() == ret.getId()) continue;
            MapleFamilyCharacter mfc = this.getMFC(ret.getPedigree().get(i));
            if (mfc == null) {
                toRemove.add(i);
                continue;
            }
            mfc.resetPedigree(this);
        }
        Iterator iterator = toRemove.iterator();
        while (iterator.hasNext()) {
            int i = (Integer)iterator.next();
            ret.getPedigree().remove(i);
        }
        return ret;
    }

    public final int addFamilyMember(MapleFamilyCharacter mgc) {
        mgc.setFamilyId(this.id);
        this.members.put(mgc.getId(), mgc);
        mgc.resetPedigree(this);
        this.bDirty = true;
        for (int i : mgc.getPedigree()) {
            this.getMFC(i).resetPedigree(this);
        }
        return 1;
    }

    public final void leaveFamily(int id) {
        this.leaveFamily(this.getMFC(id), true);
    }

    public final void leaveFamily(MapleFamilyCharacter mgc, boolean skipLeader) {
        this.bDirty = true;
        if (mgc.getId() == this.leaderid && !skipLeader) {
            this.leadername = null;
            World.Family.disbandFamily(this.id);
        } else {
            MapleFamilyCharacter mfc;
            MapleFamilyCharacter j;
            if (mgc.getJunior1() > 0 && (j = this.getMFC(mgc.getJunior1())) != null) {
                j.setSeniorId(0);
                this.splitFamily(j.getId(), j);
            }
            if (mgc.getJunior2() > 0 && (j = this.getMFC(mgc.getJunior2())) != null) {
                j.setSeniorId(0);
                this.splitFamily(j.getId(), j);
            }
            if (mgc.getSeniorId() > 0 && (mfc = this.getMFC(mgc.getSeniorId())) != null) {
                if (mfc.getJunior1() == mgc.getId()) {
                    mfc.setJunior1(0);
                } else {
                    mfc.setJunior2(0);
                }
            }
            ArrayList<Integer> dummy = new ArrayList<Integer>();
            dummy.add(mgc.getId());
            this.broadcast(null, -1, FCOp.DISBAND, dummy);
            this.resetPedigree();
        }
        this.members.remove(mgc.getId());
        this.bDirty = true;
    }

    public final void memberLevelJobUpdate(MapleCharacter mgc) {
        MapleFamilyCharacter member = this.getMFC(mgc.getId());
        if (member != null) {
            int old_level = member.getLevel();
            int old_job = member.getJobId();
            member.setJobId(mgc.getJob());
            member.setLevel(mgc.getLevel());
            if (old_level != mgc.getLevel()) {
                this.broadcast(MaplePacketCreator.sendLevelup(true, mgc.getLevel(), mgc.getName()), mgc.getId(), mgc.getId() == this.leaderid ? null : member.getPedigree());
            }
            if (old_job != mgc.getJob()) {
                this.broadcast(MaplePacketCreator.sendJobup(true, mgc.getJob(), mgc.getName()), mgc.getId(), mgc.getId() == this.leaderid ? null : member.getPedigree());
            }
        }
    }

    public final void disbandFamily() {
        this.writeToDB(true);
    }

    public final MapleFamilyCharacter getMFC(int cid) {
        return this.members.get(cid);
    }

    public int getMemberSize() {
        return this.members.size();
    }

    public static void setOfflineFamilyStatus(int familyid, int seniorid, int junior1, int junior2, int currentrep, int totalrep, int cid) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE characters SET familyid = ?, seniorid = ?, junior1 = ?, junior2 = ?, currentrep = ?, totalrep = ? WHERE id = ?");
            ps.setInt(1, familyid);
            ps.setInt(2, seniorid);
            ps.setInt(3, junior1);
            ps.setInt(4, junior2);
            ps.setInt(5, currentrep);
            ps.setInt(6, totalrep);
            ps.setInt(7, cid);
            ps.execute();
            ps.close();
        }
        catch (SQLException se) {
            System.out.println("SQLException: " + se.getLocalizedMessage());
            se.printStackTrace();
        }
    }

    public static int createFamily(int leaderId) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO families (`leaderid`) VALUES (?)", 1);
            ps.setInt(1, leaderId);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return 0;
            }
            int ret = rs.getInt(1);
            rs.close();
            ps.close();
            return ret;
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void mergeFamily(MapleFamily newfam, MapleFamily oldfam) {
        for (MapleFamilyCharacter mgc : oldfam.members.values()) {
            mgc.setFamilyId(newfam.getId());
            if (mgc.isOnline()) {
                World.Family.setFamily(newfam.getId(), mgc.getSeniorId(), mgc.getJunior1(), mgc.getJunior2(), mgc.getCurrentRep(), mgc.getTotalRep(), mgc.getId());
            } else {
                MapleFamily.setOfflineFamilyStatus(newfam.getId(), mgc.getSeniorId(), mgc.getJunior1(), mgc.getJunior2(), mgc.getCurrentRep(), mgc.getTotalRep(), mgc.getId());
            }
            newfam.members.put(mgc.getId(), mgc);
            newfam.setOnline(mgc.getId(), mgc.isOnline(), mgc.getChannel());
        }
        newfam.resetPedigree();
        World.Family.disbandFamily(oldfam.getId());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean splitFamily(int splitId, MapleFamilyCharacter def) {
        MapleFamilyCharacter leader = this.getMFC(splitId);
        if (leader == null && (leader = def) == null) {
            return false;
        }
        try {
            List<MapleFamilyCharacter> all = leader.getAllJuniors(this);
            if (all.size() <= 1) {
                this.leaveFamily(leader, false);
                boolean bl = true;
                return bl;
            }
            int newId = MapleFamily.createFamily(leader.getId());
            if (newId <= 0) {
                boolean bl = false;
                return bl;
            }
            for (MapleFamilyCharacter mgc : all) {
                mgc.setFamilyId(newId);
                MapleFamily.setOfflineFamilyStatus(newId, mgc.getSeniorId(), mgc.getJunior1(), mgc.getJunior2(), mgc.getCurrentRep(), mgc.getTotalRep(), mgc.getId());
                this.members.remove(mgc.getId());
            }
            MapleFamily newfam = World.Family.getFamily(newId);
            for (MapleFamilyCharacter mgc : all) {
                if (mgc.isOnline()) {
                    World.Family.setFamily(newId, mgc.getSeniorId(), mgc.getJunior1(), mgc.getJunior2(), mgc.getCurrentRep(), mgc.getTotalRep(), mgc.getId());
                }
                newfam.setOnline(mgc.getId(), mgc.isOnline(), mgc.getChannel());
            }
        }
        finally {
            if (this.members.size() <= 1) {
                World.Family.disbandFamily(this.id);
                return true;
            }
        }
        this.bDirty = true;
        return false;
    }

    public final void setNotice(String notice) {
        this.changed = true;
        this.notice = notice;
    }

    public static enum FCOp {
        NONE,
        DISBAND;

    }
}

