/*
 * Decompiled with CFR 0.152.
 */
package handling.world.family;

import client.MapleCharacter;
import handling.world.family.MapleFamily;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MapleFamilyCharacter
implements Serializable {
    public static final long serialVersionUID = 2058609046116597760L;
    private int level;
    private int id;
    private int channel = -1;
    private int jobid;
    private int familyid;
    private int seniorid;
    private int currentrep;
    private int totalrep;
    private int junior1;
    private int junior2;
    private boolean online;
    private String name;
    private List<Integer> pedigree = new ArrayList<Integer>();
    private int descendants = 0;

    public MapleFamilyCharacter(MapleCharacter c, int fid, int sid, int j1, int j2) {
        this.name = c.getName();
        this.level = c.getLevel();
        this.id = c.getId();
        this.channel = c.getClient().getChannel();
        this.jobid = c.getJob();
        this.familyid = fid;
        this.junior1 = j1;
        this.junior2 = j2;
        this.seniorid = sid;
        this.currentrep = c.getCurrentRep();
        this.totalrep = c.getTotalRep();
        this.online = true;
    }

    public MapleFamilyCharacter(int _id, int _lv, String _name, int _channel, int _job, int _fid, int _sid, int _jr1, int _jr2, int _crep, int _trep, boolean _on) {
        this.level = _lv;
        this.id = _id;
        this.name = _name;
        if (_on) {
            this.channel = _channel;
        }
        this.jobid = _job;
        this.online = _on;
        this.familyid = _fid;
        this.seniorid = _sid;
        this.currentrep = _crep;
        this.totalrep = _trep;
        this.junior1 = _jr1;
        this.junior2 = _jr2;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int l) {
        this.level = l;
    }

    public int getId() {
        return this.id;
    }

    public void setChannel(int ch) {
        this.channel = ch;
    }

    public int getChannel() {
        return this.channel;
    }

    public int getJobId() {
        return this.jobid;
    }

    public void setJobId(int job) {
        this.jobid = job;
    }

    public int getCurrentRep() {
        return this.currentrep;
    }

    public void setCurrentRep(int cr) {
        this.currentrep = cr;
    }

    public int getTotalRep() {
        return this.totalrep;
    }

    public void setTotalRep(int tr) {
        this.totalrep = tr;
    }

    public int getJunior1() {
        return this.junior1;
    }

    public int getJunior2() {
        return this.junior2;
    }

    public void setJunior1(int trs) {
        this.junior1 = trs;
    }

    public void setJunior2(int trs) {
        this.junior2 = trs;
    }

    public int getSeniorId() {
        return this.seniorid;
    }

    public void setSeniorId(int si) {
        this.seniorid = si;
    }

    public int getFamilyId() {
        return this.familyid;
    }

    public void setFamilyId(int fi) {
        this.familyid = fi;
    }

    public boolean isOnline() {
        return this.online;
    }

    public String getName() {
        return this.name;
    }

    public boolean equals(Object other) {
        if (!(other instanceof MapleFamilyCharacter)) {
            return false;
        }
        MapleFamilyCharacter o = (MapleFamilyCharacter)other;
        return o.getId() == this.id && o.getName().equals(this.name);
    }

    public void setOnline(boolean f) {
        this.online = f;
    }

    public List<MapleFamilyCharacter> getAllJuniors(MapleFamily fam) {
        MapleFamilyCharacter chr;
        ArrayList<MapleFamilyCharacter> ret = new ArrayList<MapleFamilyCharacter>();
        ret.add(this);
        if (this.junior1 > 0 && (chr = fam.getMFC(this.junior1)) != null) {
            ret.addAll(chr.getAllJuniors(fam));
        }
        if (this.junior2 > 0 && (chr = fam.getMFC(this.junior2)) != null) {
            ret.addAll(chr.getAllJuniors(fam));
        }
        return ret;
    }

    public List<MapleFamilyCharacter> getOnlineJuniors(MapleFamily fam) {
        MapleFamilyCharacter chr2;
        MapleFamilyCharacter chr;
        ArrayList<MapleFamilyCharacter> ret = new ArrayList<MapleFamilyCharacter>();
        ret.add(this);
        if (this.junior1 > 0 && (chr = fam.getMFC(this.junior1)) != null) {
            if (chr.isOnline()) {
                ret.add(chr);
            }
            if (chr.getJunior1() > 0 && (chr2 = fam.getMFC(chr.getJunior1())) != null && chr2.isOnline()) {
                ret.add(chr2);
            }
            if (chr.getJunior2() > 0 && (chr2 = fam.getMFC(chr.getJunior2())) != null && chr2.isOnline()) {
                ret.add(chr2);
            }
        }
        if (this.junior2 > 0 && (chr = fam.getMFC(this.junior2)) != null) {
            if (chr.isOnline()) {
                ret.add(chr);
            }
            if (chr.getJunior1() > 0 && (chr2 = fam.getMFC(chr.getJunior1())) != null && chr2.isOnline()) {
                ret.add(chr2);
            }
            if (chr.getJunior2() > 0 && (chr2 = fam.getMFC(chr.getJunior2())) != null && chr2.isOnline()) {
                ret.add(chr2);
            }
        }
        return ret;
    }

    public List<Integer> getPedigree() {
        return this.pedigree;
    }

    public void resetPedigree(MapleFamily fam) {
        MapleFamilyCharacter chr;
        this.pedigree = new ArrayList<Integer>();
        this.pedigree.add(this.id);
        if (this.seniorid > 0 && (chr = fam.getMFC(this.seniorid)) != null) {
            this.pedigree.add(this.seniorid);
            if (chr.getSeniorId() > 0) {
                this.pedigree.add(chr.getSeniorId());
            }
            if (chr.getJunior1() > 0 && chr.getJunior1() != this.id) {
                this.pedigree.add(chr.getJunior1());
            } else if (chr.getJunior2() > 0 && chr.getJunior2() != this.id) {
                this.pedigree.add(chr.getJunior2());
            }
        }
        if (this.junior1 > 0 && (chr = fam.getMFC(this.junior1)) != null) {
            this.pedigree.add(this.junior1);
            if (chr.getJunior1() > 0) {
                this.pedigree.add(chr.getJunior1());
            }
            if (chr.getJunior2() > 0) {
                this.pedigree.add(chr.getJunior2());
            }
        }
        if (this.junior2 > 0 && (chr = fam.getMFC(this.junior2)) != null) {
            this.pedigree.add(this.junior2);
            if (chr.getJunior1() > 0) {
                this.pedigree.add(chr.getJunior1());
            }
            if (chr.getJunior2() > 0) {
                this.pedigree.add(chr.getJunior2());
            }
        }
    }

    public int getDescendants() {
        return this.descendants;
    }

    public int resetDescendants(MapleFamily fam) {
        MapleFamilyCharacter chr;
        this.descendants = 0;
        if (this.junior1 > 0 && (chr = fam.getMFC(this.junior1)) != null) {
            this.descendants += 1 + chr.resetDescendants(fam);
        }
        if (this.junior2 > 0 && (chr = fam.getMFC(this.junior2)) != null) {
            this.descendants += 1 + chr.resetDescendants(fam);
        }
        return this.descendants;
    }

    public int resetGenerations(MapleFamily fam) {
        int ret;
        MapleFamilyCharacter chr;
        int descendants1 = 0;
        int descendants2 = 0;
        if (this.junior1 > 0 && (chr = fam.getMFC(this.junior1)) != null) {
            descendants1 = chr.resetGenerations(fam);
        }
        if (this.junior2 > 0 && (chr = fam.getMFC(this.junior2)) != null) {
            descendants2 = chr.resetGenerations(fam);
        }
        ret = Math.max(descendants1, descendants2);
        return ret + (ret > 0 ? 1 : 0);
    }

    public int getNoJuniors() {
        int ret = 0;
        if (this.junior1 > 0) {
            ++ret;
        }
        if (this.junior2 > 0) {
            ++ret;
        }
        return ret;
    }
}

