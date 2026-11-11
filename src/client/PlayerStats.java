/*
 * Decompiled with CFR 0.152.
 */
package client;

import client.ISkill;
import client.MapleBuffStat;
import client.MapleCharacter;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.IEquip;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import client.inventory.MapleWeaponType;
import constants.GameConstants;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.StructPotentialItem;
import server.StructSetItem;
import tools.MaplePacketCreator;
import tools.data.output.MaplePacketLittleEndianWriter;

public class PlayerStats
implements Serializable {
    private static final long serialVersionUID = -679541993413738569L;
    private transient WeakReference<MapleCharacter> chr;
    private Map<Integer, Integer> setHandling = new HashMap<Integer, Integer>();
    private List<Equip> durabilityHandling = new ArrayList<Equip>();
    private List<Equip> equipLevelHandling = new ArrayList<Equip>();
    private transient float shouldHealHP;
    private transient float shouldHealMP;
    public short str;
    public short dex;
    public short luk;
    public short int_;
    public short hp;
    public short maxhp;
    public short mp;
    public short maxmp;
    private transient short passive_sharpeye_percent;
    private transient short localmaxhp;
    private transient short localmaxmp;
    private transient byte passive_mastery;
    private transient byte passive_sharpeye_rate;
    private transient int localstr;
    private transient int localdex;
    private transient int localluk;
    private transient int localint_;
    private transient int magic;
    private transient int watk;
    private transient int hands;
    private transient int accuracy;
    public transient boolean equippedWelcomeBackRing;
    public transient boolean equippedFairy;
    public transient boolean hasMeso;
    public transient boolean hasItem;
    public transient boolean hasVac;
    public transient boolean hasClone;
    public transient boolean hasPartyBonus;
    public transient boolean Berserk = false;
    public transient boolean isRecalc = false;
    public transient int equipmentBonusExp;
    public transient int expMod;
    public transient int dropMod;
    public transient int cashMod;
    public transient int levelBonus;
    public transient double expBuff;
    public transient double dropBuff;
    public transient double mesoBuff;
    public transient double cashBuff;
    public transient double dam_r;
    public transient double bossdam_r;
    public transient int recoverHP;
    public transient int recoverMP;
    public transient int mpconReduce;
    public transient int incMesoProp;
    public transient int incRewardProp;
    public transient int DAMreflect;
    public transient int DAMreflect_rate;
    public transient int mpRestore;
    public transient int hpRecover;
    public transient int hpRecoverProp;
    public transient int mpRecover;
    public transient int mpRecoverProp;
    public transient int RecoveryUP;
    public transient int incAllskill;
    private transient float speedMod;
    private transient float jumpMod;
    private transient float localmaxbasedamage;
    public transient int def;
    public transient int element_ice;
    public transient int element_fire;
    public transient int element_light;
    public transient int element_psn;
    public ReentrantLock lock = new ReentrantLock();

    public PlayerStats(MapleCharacter chr) {
        this.chr = new WeakReference<MapleCharacter>(chr);
    }

    public final void init() {
        this.recalcLocalStats();
        this.relocHeal();
    }

    public final short getStr() {
        return this.str;
    }

    public final short getDex() {
        return this.dex;
    }

    public final short getLuk() {
        return this.luk;
    }

    public final short getInt() {
        return this.int_;
    }

    public final void setStr(short str) {
        this.str = str;
        this.recalcLocalStats();
    }

    public final void setDex(short dex) {
        this.dex = dex;
        this.recalcLocalStats();
    }

    public final void setLuk(short luk) {
        this.luk = luk;
        this.recalcLocalStats();
    }

    public final void setInt(short int_) {
        this.int_ = int_;
        this.recalcLocalStats();
    }

    public final boolean setHp(int newhp) {
        return this.setHp(newhp, false);
    }

    public final boolean setHp(int newhp, boolean silent) {
        short oldHp = this.hp;
        int thp = newhp;
        if (thp < 0) {
            thp = 0;
        }
        if (thp > this.localmaxhp) {
            thp = this.localmaxhp;
        }
        this.hp = (short)thp;
        MapleCharacter chra = (MapleCharacter)this.chr.get();
        if (chra != null) {
            if (!silent) {
                chra.updatePartyMemberHP();
            }
            if (oldHp > this.hp && !chra.isAlive()) {
                chra.playerDead();
            }
        }
        return this.hp != oldHp;
    }

    public final boolean setMp(int newmp) {
        short oldMp = this.mp;
        int tmp = newmp;
        if (tmp < 0) {
            tmp = 0;
        }
        if (tmp > this.localmaxmp) {
            tmp = this.localmaxmp;
        }
        this.mp = (short)tmp;
        return this.mp != oldMp;
    }

    public final void setMaxHp(short hp) {
        this.maxhp = hp;
        this.recalcLocalStats();
    }

    public final void setMaxMp(short mp) {
        this.maxmp = mp;
        this.recalcLocalStats();
    }

    public final short getHp() {
        return this.hp;
    }

    public final short getMaxHp() {
        return this.maxhp;
    }

    public final short getMp() {
        return this.mp;
    }

    public final short getMaxMp() {
        return this.maxmp;
    }

    public final int getTotalDex() {
        return this.localdex;
    }

    public final int getTotalInt() {
        return this.localint_;
    }

    public final int getTotalStr() {
        return this.localstr;
    }

    public final int getTotalLuk() {
        return this.localluk;
    }

    public final int getTotalMagic() {
        return this.magic;
    }

    public final double getSpeedMod() {
        return this.speedMod;
    }

    public final double getJumpMod() {
        return this.jumpMod;
    }

    public final int getTotalWatk() {
        return this.watk;
    }

    public final short getCurrentMaxHp() {
        return this.localmaxhp;
    }

    public final short getCurrentMaxMp() {
        return this.localmaxmp;
    }

    public final int getHands() {
        return this.hands;
    }

    public final float getCurrentMaxBaseDamage() {
        return this.localmaxbasedamage;
    }

    public void recalcLocalStats() {
        this.recalcLocalStats(false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void recalcLocalStats(boolean first_login) {
        ISkill bx;
        MapleCharacter chra = (MapleCharacter)this.chr.get();
        if (chra == null) {
            return;
        }
        this.lock.lock();
        try {
            if (this.isRecalc) {
                return;
            }
            this.isRecalc = true;
        }
        finally {
            this.lock.unlock();
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        short oldmaxhp = this.localmaxhp;
        int localmaxhp_ = this.getMaxHp();
        int localmaxmp_ = this.getMaxMp();
        this.localdex = this.getDex();
        this.localint_ = this.getInt();
        this.localstr = this.getStr();
        this.localluk = this.getLuk();
        int speed = 100;
        int jump = 100;
        int percent_hp = 0;
        int percent_mp = 0;
        int percent_str = 0;
        int percent_dex = 0;
        int percent_int = 0;
        int percent_luk = 0;
        int percent_acc = 0;
        int percent_atk = 0;
        int percent_matk = 0;
        int added_sharpeye_rate = 0;
        int added_sharpeye_dmg = 0;
        this.magic = this.localint_;
        this.watk = 0;
        if (chra.getJob() == 500 || chra.getJob() >= 520 && chra.getJob() <= 522) {
            this.watk = 20;
        } else if (chra.getJob() == 400 || chra.getJob() >= 410 && chra.getJob() <= 412 || chra.getJob() >= 1400 && chra.getJob() <= 1412) {
            this.watk = 30;
        }
        this.dam_r = 0.0;
        this.bossdam_r = 0.0;
        this.expBuff = 100.0;
        this.cashBuff = 100.0;
        this.dropBuff = 100.0;
        this.mesoBuff = 100.0;
        this.recoverHP = 0;
        this.recoverMP = 0;
        this.mpconReduce = 0;
        this.incMesoProp = 0;
        this.incRewardProp = 0;
        this.DAMreflect = 0;
        this.DAMreflect_rate = 0;
        this.hpRecover = 0;
        this.hpRecoverProp = 0;
        this.mpRecover = 0;
        this.mpRecoverProp = 0;
        this.mpRestore = 0;
        this.equippedWelcomeBackRing = false;
        this.equippedFairy = false;
        this.hasMeso = false;
        this.hasItem = false;
        this.hasPartyBonus = false;
        this.hasVac = false;
        this.hasClone = false;
        boolean canEquipLevel = chra.getLevel() >= 120 && !GameConstants.isKOC(chra.getJob());
        this.equipmentBonusExp = 0;
        this.RecoveryUP = 0;
        this.dropMod = 1;
        this.expMod = 1;
        this.cashMod = 1;
        this.levelBonus = 0;
        this.incAllskill = 0;
        this.durabilityHandling.clear();
        this.equipLevelHandling.clear();
        this.setHandling.clear();
        this.element_fire = 100;
        this.element_ice = 100;
        this.element_light = 100;
        this.element_psn = 100;
        this.def = 100;
        for (IItem item : chra.getInventory(MapleInventoryType.EQUIPPED)) {
            IEquip equip = (IEquip)item;
            if (equip.getPosition() == -11 && GameConstants.isMagicWeapon(equip.getItemId())) {
                Map<String, Integer> eqstat = MapleItemInformationProvider.getInstance().getEquipStats(equip.getItemId());
                this.element_fire = eqstat.get("incRMAF");
                this.element_ice = eqstat.get("incRMAI");
                this.element_light = eqstat.get("incRMAL");
                this.element_psn = eqstat.get("incRMAS");
                this.def = eqstat.get("elemDefault");
            }
            this.accuracy += equip.getAcc();
            localmaxhp_ += equip.getHp();
            localmaxmp_ += equip.getMp();
            this.localdex += equip.getDex();
            this.localint_ += equip.getInt();
            this.localstr += equip.getStr();
            this.localluk += equip.getLuk();
            this.magic += equip.getMatk() + equip.getInt();
            this.watk += equip.getWatk();
            speed += equip.getSpeed();
            jump += equip.getJump();
            block3 : switch (equip.getItemId()) {
                case 1112427: {
                    added_sharpeye_rate += 5;
                    added_sharpeye_dmg += 20;
                    break;
                }
                case 1112428: {
                    added_sharpeye_rate += 10;
                    added_sharpeye_dmg += 10;
                    break;
                }
                case 1112429: {
                    added_sharpeye_rate += 5;
                    added_sharpeye_dmg += 20;
                    break;
                }
                case 1112127: {
                    this.equippedWelcomeBackRing = true;
                    break;
                }
                case 0x111EE1: {
                    this.equippedFairy = true;
                    break;
                }
                case 1812000: {
                    this.hasMeso = true;
                    break;
                }
                case 1812001: {
                    this.hasItem = true;
                    break;
                }
                default: {
                    for (int eb_bonus : GameConstants.Equipments_Bonus) {
                        if (equip.getItemId() != eb_bonus) continue;
                        this.equipmentBonusExp += GameConstants.Equipment_Bonus_EXP(eb_bonus);
                        break block3;
                    }
                }
            }
            percent_hp += equip.getHpR();
            percent_mp += equip.getMpR();
            int set = ii.getSetItemID(equip.getItemId());
            if (set > 0) {
                int value = 1;
                if (this.setHandling.get(set) != null) {
                    value += this.setHandling.get(set).intValue();
                }
                this.setHandling.put(set, value);
            }
            if (equip.getState() > 1) {
                int[] potentials = new int[]{equip.getPotential1(), equip.getPotential2(), equip.getPotential3()};
                for (int i = 0; i < potentials.length; ++i) {
                    StructPotentialItem pot;
                    int i2 = potentials[i];
                    if (i2 <= 0 || (pot = ii.getPotentialInfo(i2).get(ii.getReqLevel(equip.getItemId()) / 10)) == null) continue;
                    this.localstr += pot.incSTR;
                    this.localdex += pot.incDEX;
                    this.localint_ += pot.incINT;
                    this.localluk += pot.incLUK;
                    this.localmaxhp = (short)(this.localmaxhp + pot.incMHP);
                    this.localmaxmp = (short)(this.localmaxmp + pot.incMMP);
                    this.watk += pot.incPAD;
                    this.magic += pot.incINT + pot.incMAD;
                    speed += pot.incSpeed;
                    jump += pot.incJump;
                    this.accuracy += pot.incACC;
                    this.incAllskill += pot.incAllskill;
                    percent_hp += pot.incMHPr;
                    percent_mp += pot.incMMPr;
                    percent_str += pot.incSTRr;
                    percent_dex += pot.incDEXr;
                    percent_int += pot.incINTr;
                    percent_luk += pot.incLUKr;
                    percent_acc += pot.incACCr;
                    percent_atk += pot.incPADr;
                    percent_matk += pot.incMADr;
                    added_sharpeye_rate += pot.incCr;
                    added_sharpeye_dmg += pot.incCr;
                    if (!pot.boss) {
                        this.dam_r = Math.max((double)pot.incDAMr, this.dam_r);
                    } else {
                        this.bossdam_r = Math.max((double)pot.incDAMr, this.bossdam_r);
                    }
                    this.recoverHP += pot.RecoveryHP;
                    this.recoverMP += pot.RecoveryMP;
                    this.RecoveryUP += pot.RecoveryUP;
                    if (pot.HP > 0) {
                        this.hpRecover += pot.HP;
                        this.hpRecoverProp += pot.prop;
                    }
                    if (pot.MP > 0) {
                        this.mpRecover += pot.MP;
                        this.mpRecoverProp += pot.prop;
                    }
                    this.mpconReduce += pot.mpconReduce;
                    this.incMesoProp += pot.incMesoProp;
                    this.incRewardProp += pot.incRewardProp;
                    if (pot.DAMreflect > 0) {
                        this.DAMreflect += pot.DAMreflect;
                        this.DAMreflect_rate += pot.prop;
                    }
                    this.mpRestore += pot.mpRestore;
                    if (first_login || pot.skillID <= 0) continue;
                    chra.changeSkillLevel_Skip(SkillFactory.getSkill(this.getSkillByJob(pot.skillID, chra.getJob())), (byte)1, (byte)1);
                }
                if (equip.getDurability() > 0) {
                    this.durabilityHandling.add((Equip)equip);
                }
            }
            if (!canEquipLevel || GameConstants.getMaxLevel(equip.getItemId()) <= 0 || !(GameConstants.getStatFromWeapon(equip.getItemId()) == null ? equip.getEquipLevel() <= GameConstants.getMaxLevel(equip.getItemId()) : equip.getEquipLevel() < GameConstants.getMaxLevel(equip.getItemId()))) continue;
            this.equipLevelHandling.add((Equip)equip);
        }
        for (Map.Entry<Integer, Integer> entry : this.setHandling.entrySet()) {
            StructSetItem set = ii.getSetItem(entry.getKey());
            if (set == null) continue;
            Map<Integer, StructSetItem.SetItem> itemz = set.getItems();
            for (Map.Entry<Integer, StructSetItem.SetItem> ent : itemz.entrySet()) {
                if (ent.getKey() > entry.getValue()) continue;
                StructSetItem.SetItem se = ent.getValue();
                this.localstr += se.incSTR;
                this.localdex += se.incDEX;
                this.localint_ += se.incINT;
                this.localluk += se.incLUK;
                this.watk += se.incPAD;
                this.magic += se.incINT + se.incMAD;
                speed += se.incSpeed;
                this.accuracy += se.incACC;
                localmaxhp_ += se.incMHP;
                localmaxmp_ += se.incMMP;
            }
        }
        int hour = Calendar.getInstance().get(11);
        for (IItem item : chra.getInventory(MapleInventoryType.CASH)) {
            int itemId = item.getItemId();
            if (this.expMod < 3 && (itemId == 5211060 || itemId == 5211050 || itemId == 5211051 || itemId == 5211052 || itemId == 5211053 || itemId == 5211054)) {
                this.expMod = 3;
            } else if (this.expMod == 1 && itemId == 5210000) {
                this.expMod = 2;
            } else if (this.expMod == 1 && itemId == 5210001 && hour >= 0 && hour <= 6) {
                this.expMod = 2;
            } else if (this.expMod == 1 && itemId == 5210002 && hour >= 6 && hour <= 12) {
                this.expMod = 2;
            } else if (this.expMod == 1 && itemId == 5210003 && hour >= 12 && hour <= 18) {
                this.expMod = 2;
            } else if (this.expMod == 1 && itemId == 5210004 && hour >= 18 && hour <= 24) {
                this.expMod = 2;
            }
            if (this.dropMod == 1) {
                if (itemId == 5360015) {
                    this.dropMod = 2;
                } else if (itemId == 5360000 && hour >= 0 && hour <= 6) {
                    this.dropMod = 2;
                } else if (itemId == 5360001 && hour >= 6 && hour <= 12) {
                    this.dropMod = 2;
                } else if (itemId == 5360002 && hour >= 12 && hour <= 18) {
                    this.dropMod = 2;
                } else if (itemId == 5360003 && hour >= 18 && hour <= 24) {
                    this.dropMod = 2;
                }
            }
            if (itemId == 5650000) {
                this.hasPartyBonus = true;
                continue;
            }
            if (itemId == 5590001) {
                this.levelBonus = 10;
                continue;
            }
            if (this.levelBonus != 0 || itemId != 5590000) continue;
            this.levelBonus = 5;
        }
        for (IItem item : chra.getInventory(MapleInventoryType.ETC)) {
            switch (item.getItemId()) {
                case 4030003: {
                    this.hasVac = true;
                    break;
                }
                case 4030004: {
                    this.hasClone = true;
                    break;
                }
                case 4030005: {
                    this.cashMod = 2;
                }
            }
        }
        this.magic += chra.getSkillLevel(SkillFactory.getSkill(22000000));
        this.localstr = (int)((float)this.localstr + (float)(percent_str * this.localstr) / 100.0f);
        this.localdex = (int)((float)this.localdex + (float)(percent_dex * this.localdex) / 100.0f);
        int before_ = this.localint_;
        this.localint_ = (int)((float)this.localint_ + (float)(percent_int * this.localint_) / 100.0f);
        this.magic += this.localint_ - before_;
        this.localluk = (int)((float)this.localluk + (float)(percent_luk * this.localluk) / 100.0f);
        this.accuracy = (int)((float)this.accuracy + (float)(percent_acc * this.accuracy) / 100.0f);
        this.watk = (int)((float)this.watk + (float)(percent_atk * this.watk) / 100.0f);
        this.magic = (int)((float)this.magic + (float)(percent_matk * this.magic) / 100.0f);
        localmaxhp_ = (int)((float)localmaxhp_ + (float)(percent_hp * localmaxhp_) / 100.0f);
        localmaxmp_ = (int)((float)localmaxmp_ + (float)(percent_mp * localmaxmp_) / 100.0f);
        this.magic = Math.min(this.magic, 1999);
        Integer buff = chra.getBuffedValue(MapleBuffStat.MAPLE_WARRIOR);
        if (buff != null) {
            double d = buff.doubleValue() / 100.0;
            this.localstr = (int)((double)this.localstr + d * (double)this.str);
            this.localdex = (int)((double)this.localdex + d * (double)this.dex);
            this.localluk = (int)((double)this.localluk + d * (double)this.luk);
            int before = this.localint_;
            this.localint_ = (int)((double)this.localint_ + d * (double)this.int_);
            this.magic += this.localint_ - before;
        }
        if ((buff = chra.getBuffedValue(MapleBuffStat.ECHO_OF_HERO)) != null) {
            double d = buff.doubleValue() / 100.0;
            this.watk += (int)((double)this.watk * d);
            this.magic += (int)((double)this.magic * d);
        }
        if ((buff = chra.getBuffedValue(MapleBuffStat.ARAN_COMBO)) != null) {
            this.watk += buff / 10;
        }
        if ((buff = chra.getBuffedValue(MapleBuffStat.MAXHP)) != null) {
            localmaxhp_ = (int)((double)localmaxhp_ + buff.doubleValue() / 100.0 * (double)localmaxhp_);
        }
        if ((buff = chra.getBuffedValue(MapleBuffStat.CONVERSION)) != null) {
            localmaxhp_ = (int)((double)localmaxhp_ + buff.doubleValue() / 100.0 * (double)localmaxhp_);
        }
        if ((buff = chra.getBuffedValue(MapleBuffStat.MAXMP)) != null) {
            localmaxmp_ = (int)((double)localmaxmp_ + buff.doubleValue() / 100.0 * (double)localmaxmp_);
        }
        if ((buff = chra.getBuffedValue(MapleBuffStat.MP_BUFF)) != null) {
            localmaxmp_ = (int)((double)localmaxmp_ + buff.doubleValue() / 100.0 * (double)localmaxmp_);
        }
        if ((buff = chra.getBuffedValue(MapleBuffStat.ENHANCED_MAXHP)) != null) {
            localmaxhp_ += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(MapleBuffStat.ENHANCED_MAXMP)) != null) {
            localmaxmp_ += buff.intValue();
        }
        switch (chra.getJob()) {
            case 322: {
                ISkill expert = SkillFactory.getSkill(3220004);
                byte boostLevel = chra.getSkillLevel(expert);
                if (boostLevel <= 0) break;
                this.watk += expert.getEffect(boostLevel).getX();
                break;
            }
            case 312: {
                ISkill expert = SkillFactory.getSkill(3120005);
                byte boostLevel = chra.getSkillLevel(expert);
                if (boostLevel <= 0) break;
                this.watk += expert.getEffect(boostLevel).getX();
                break;
            }
            case 211: 
            case 212: {
                ISkill amp = SkillFactory.getSkill(2110001);
                byte level = chra.getSkillLevel(amp);
                if (level <= 0) break;
                this.dam_r *= (double)amp.getEffect(level).getY() / 100.0;
                this.bossdam_r *= (double)amp.getEffect(level).getY() / 100.0;
                break;
            }
            case 221: 
            case 222: {
                ISkill amp = SkillFactory.getSkill(2210001);
                byte level = chra.getSkillLevel(amp);
                if (level <= 0) break;
                this.dam_r *= (double)amp.getEffect(level).getY() / 100.0;
                this.bossdam_r *= (double)amp.getEffect(level).getY() / 100.0;
                break;
            }
            case 1211: 
            case 1212: {
                ISkill amp = SkillFactory.getSkill(12110001);
                byte level = chra.getSkillLevel(amp);
                if (level <= 0) break;
                this.dam_r *= (double)amp.getEffect(level).getY() / 100.0;
                this.bossdam_r *= (double)amp.getEffect(level).getY() / 100.0;
                break;
            }
            case 2215: 
            case 2216: 
            case 2217: 
            case 2218: {
                ISkill amp = SkillFactory.getSkill(22150000);
                byte level = chra.getSkillLevel(amp);
                if (level <= 0) break;
                this.dam_r *= (double)amp.getEffect(level).getY() / 100.0;
                this.bossdam_r *= (double)amp.getEffect(level).getY() / 100.0;
                break;
            }
            case 2112: {
                ISkill expert = SkillFactory.getSkill(21120001);
                byte boostLevel = chra.getSkillLevel(expert);
                if (boostLevel <= 0) break;
                this.watk += expert.getEffect(boostLevel).getX();
                break;
            }
        }
        ISkill blessoffairy = SkillFactory.getSkill(GameConstants.getBOF_ForJob(chra.getJob()));
        byte boflevel = chra.getSkillLevel(blessoffairy);
        if (boflevel > 0) {
            this.watk += blessoffairy.getEffect(boflevel).getX();
            this.magic += blessoffairy.getEffect(boflevel).getY();
            this.accuracy += blessoffairy.getEffect(boflevel).getX();
        }
        if ((buff = chra.getBuffedValue(MapleBuffStat.EXPRATE)) != null) {
            this.expBuff *= buff.doubleValue() / 100.0;
        }
        if ((buff = chra.getBuffedValue(MapleBuffStat.DROP_RATE)) != null) {
            this.dropBuff *= buff.doubleValue() / 100.0;
        }
        if ((buff = chra.getBuffedValue(MapleBuffStat.ACASH_RATE)) != null) {
            this.cashBuff *= buff.doubleValue() / 100.0;
        }
        if ((buff = chra.getBuffedValue(MapleBuffStat.MESO_RATE)) != null) {
            this.mesoBuff *= buff.doubleValue() / 100.0;
        }
        if ((buff = chra.getBuffedValue(MapleBuffStat.MESOUP)) != null) {
            this.mesoBuff *= buff.doubleValue() / 100.0;
        }
        if ((buff = chra.getBuffedValue(MapleBuffStat.ACC)) != null) {
            this.accuracy += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(MapleBuffStat.WATK)) != null) {
            this.watk += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(MapleBuffStat.ENHANCED_WATK)) != null) {
            this.watk += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(MapleBuffStat.MATK)) != null) {
            this.magic += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(MapleBuffStat.SPEED)) != null) {
            speed += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(MapleBuffStat.JUMP)) != null) {
            jump += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(MapleBuffStat.DASH_SPEED)) != null) {
            speed += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(MapleBuffStat.DASH_JUMP)) != null) {
            jump += buff.intValue();
        }
        if ((buff = chra.getBuffedValue(MapleBuffStat.DAMAGE_BUFF)) != null) {
            this.dam_r += buff.doubleValue();
            this.bossdam_r += buff.doubleValue();
        }
        if ((buff = chra.getBuffedSkill_Y(MapleBuffStat.FINAL_CUT)) != null) {
            this.dam_r *= buff.doubleValue() / 100.0;
            this.bossdam_r *= buff.doubleValue() / 100.0;
        }
        if ((buff = chra.getBuffedSkill_Y(MapleBuffStat.OWL_SPIRIT)) != null) {
            this.dam_r *= buff.doubleValue() / 100.0;
            this.bossdam_r *= buff.doubleValue() / 100.0;
        }
        if ((buff = chra.getBuffedValue(MapleBuffStat.BERSERK_FURY)) != null) {
            this.dam_r *= 2.0;
            this.bossdam_r *= 2.0;
        }
        if (chra.getSkillLevel(bx = SkillFactory.getSkill(1320006)) > 0) {
            this.dam_r *= (double)bx.getEffect(chra.getSkillLevel(bx)).getDamage() / 100.0;
            this.bossdam_r *= (double)bx.getEffect(chra.getSkillLevel(bx)).getDamage() / 100.0;
        }
        if ((buff = chra.getBuffedValue(MapleBuffStat.PYRAMID_PQ)) != null) {
            MapleStatEffect eff = chra.getStatForBuff(MapleBuffStat.PYRAMID_PQ);
            this.dam_r *= (double)eff.getBerserk() / 100.0;
            this.bossdam_r *= (double)eff.getBerserk() / 100.0;
        }
        if ((buff = chra.getBuffedValue(MapleBuffStat.WK_CHARGE)) != null) {
            MapleStatEffect eff = chra.getStatForBuff(MapleBuffStat.WK_CHARGE);
            this.dam_r *= (double)eff.getDamage() / 100.0;
            this.bossdam_r *= (double)eff.getDamage() / 100.0;
        }
        if ((buff = chra.getBuffedValue(MapleBuffStat.LIGHTNING_CHARGE)) != null) {
            MapleStatEffect eff = chra.getStatForBuff(MapleBuffStat.LIGHTNING_CHARGE);
            this.dam_r *= (double)eff.getDamage() / 100.0;
            this.bossdam_r *= (double)eff.getDamage() / 100.0;
        }
        if ((buff = chra.getBuffedSkill_X(MapleBuffStat.THORNS)) != null) {
            added_sharpeye_rate += buff.intValue();
        }
        if ((buff = chra.getBuffedSkill_Y(MapleBuffStat.THORNS)) != null) {
            added_sharpeye_dmg += buff - 100;
        }
        if ((buff = chra.getBuffedSkill_X(MapleBuffStat.SHARP_EYES)) != null) {
            added_sharpeye_rate += buff.intValue();
        }
        if ((buff = chra.getBuffedSkill_Y(MapleBuffStat.SHARP_EYES)) != null) {
            added_sharpeye_dmg += buff - 100;
        }
        if ((buff = chra.getBuffedValue(MapleBuffStat.CRITICAL_RATE_BUFF)) != null) {
            added_sharpeye_rate += buff.intValue();
        }
        if (speed > 140) {
            speed = 140;
        }
        if (jump > 123) {
            jump = 123;
        }
        this.speedMod = (float)speed / 100.0f;
        this.jumpMod = (float)jump / 100.0f;
        Integer mount = chra.getBuffedValue(MapleBuffStat.MONSTER_RIDING);
        if (mount != null) {
            this.jumpMod = 1.23f;
            switch (mount) {
                case 1: {
                    this.speedMod = 1.5f;
                    break;
                }
                case 2: {
                    this.speedMod = 1.7f;
                    break;
                }
                case 3: {
                    this.speedMod = 1.8f;
                    break;
                }
                default: {
                    System.err.println("Unhandeled monster riding level, Speedmod = " + this.speedMod + "");
                }
            }
        }
        this.hands = this.localdex + this.localint_ + this.localluk;
        this.localmaxhp = (short)Math.min(30000, Math.abs(Math.max(-30000, localmaxhp_)));
        this.localmaxmp = (short)Math.min(30000, Math.abs(Math.max(-30000, localmaxmp_)));
        this.CalcPassive_SharpEye(chra, added_sharpeye_rate, added_sharpeye_dmg);
        this.CalcPassive_Mastery(chra);
        if (first_login) {
            chra.silentEnforceMaxHpMp();
        } else {
            chra.enforceMaxHpMp();
        }
        this.localmaxbasedamage = this.calculateMaxBaseDamage(this.watk);
        if (oldmaxhp != 0 && oldmaxhp != this.localmaxhp) {
            chra.updatePartyMemberHP();
        }
        this.lock.lock();
        try {
            this.isRecalc = false;
        }
        finally {
            this.lock.unlock();
        }
    }

    public boolean checkEquipLevels(MapleCharacter chr, int gain) {
        boolean changed = false;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        ArrayList<Equip> all = new ArrayList<Equip>(this.equipLevelHandling);
        for (Equip eq : all) {
            int lvlz = eq.getEquipLevel();
            eq.setItemEXP(eq.getItemEXP() + gain);
            if (eq.getEquipLevel() > lvlz) {
                for (int i = eq.getEquipLevel() - lvlz; i > 0; --i) {
                    Map<Integer, List<Integer>> ins;
                    Map<Integer, Map<String, Integer>> inc = ii.getEquipIncrements(eq.getItemId());
                    if (inc != null && inc.containsKey(lvlz + i)) {
                        eq = ii.levelUpEquip(eq, inc.get(lvlz + i));
                    }
                    if (GameConstants.getStatFromWeapon(eq.getItemId()) != null || (ins = ii.getEquipSkills(eq.getItemId())) == null || !ins.containsKey(lvlz + i)) continue;
                    for (Integer z : ins.get(lvlz + i)) {
                        ISkill skil;
                        if (!(Math.random() < 0.1) || (skil = SkillFactory.getSkill(z)) == null || !skil.canBeLearnedBy(chr.getJob()) || chr.getSkillLevel(skil) >= chr.getMasterLevel(skil)) continue;
                        chr.changeSkillLevel(skil, (byte)(chr.getSkillLevel(skil) + 1), chr.getMasterLevel(skil));
                    }
                }
                changed = true;
            }
            chr.forceReAddItem(eq.copy(), MapleInventoryType.EQUIPPED);
        }
        if (changed) {
            chr.equipChanged();
            chr.getClient().getSession().write((Object)MaplePacketCreator.showItemLevelupEffect());
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.showForeignItemLevelupEffect(chr.getId()), false);
        }
        return changed;
    }

    public boolean checkEquipDurabilitys(MapleCharacter chr, int gain) {
        for (Equip item : this.durabilityHandling) {
            item.setDurability(item.getDurability() + gain);
            if (item.getDurability() >= 0) continue;
            item.setDurability(0);
        }
        ArrayList<Equip> all = new ArrayList<Equip>(this.durabilityHandling);
        for (Equip eqq : all) {
            if (eqq.getDurability() == 0) {
                if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                    chr.getClient().getSession().write((Object)MaplePacketCreator.getInventoryFull());
                    chr.getClient().getSession().write((Object)MaplePacketCreator.getShowInventoryFull());
                    return false;
                }
                this.durabilityHandling.remove(eqq);
                short pos = chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot();
                MapleInventoryManipulator.unequip(chr.getClient(), eqq.getPosition(), pos);
                chr.getClient().getSession().write((Object)MaplePacketCreator.updateSpecialItemUse(eqq, (byte)1, pos));
                continue;
            }
            chr.forceReAddItem(eqq.copy(), MapleInventoryType.EQUIPPED);
        }
        return true;
    }

    private final void CalcPassive_Mastery(MapleCharacter player) {
        int skil;
        if (player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11) == null) {
            this.passive_mastery = 0;
            return;
        }
        switch (GameConstants.getWeaponType(player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11).getItemId())) {
            case BOW: {
                skil = GameConstants.isKOC(player.getJob()) ? 13100000 : (GameConstants.isResist(player.getJob()) ? 33100000 : 3100000);
                break;
            }
            case CLAW: {
                skil = 4100000;
                break;
            }
            case KATARA: 
            case DAGGER: {
                skil = player.getJob() >= 430 && player.getJob() <= 434 ? 4300000 : 4200000;
                break;
            }
            case CROSSBOW: {
                skil = 3200000;
                break;
            }
            case AXE1H: 
            case AXE2H: {
                skil = 1100001;
                break;
            }
            case SWORD1H: 
            case SWORD2H: {
                skil = GameConstants.isKOC(player.getJob()) ? 11100000 : (player.getJob() > 112 ? 1200000 : 1100000);
                break;
            }
            case BLUNT1H: 
            case BLUNT2H: {
                skil = 1200001;
                break;
            }
            case POLE_ARM: {
                skil = GameConstants.isAran(player.getJob()) ? 21100000 : 1300001;
                break;
            }
            case SPEAR: {
                skil = 1300000;
                break;
            }
            case KNUCKLE: {
                skil = GameConstants.isKOC(player.getJob()) ? 15100001 : 5100001;
                break;
            }
            case GUN: {
                skil = GameConstants.isResist(player.getJob()) ? 35100000 : 5200000;
                break;
            }
            case STAFF: {
                skil = 32100006;
                break;
            }
            default: {
                this.passive_mastery = 0;
                return;
            }
        }
        if (player.getSkillLevel(skil) <= 0) {
            this.passive_mastery = 0;
            return;
        }
        this.passive_mastery = (byte)(player.getSkillLevel(skil) / 2 + player.getSkillLevel(skil) % 2);
    }

    private final void CalcPassive_SharpEye(MapleCharacter player, int added_sharpeye_rate, int added_sharpeye_dmg) {
        switch (player.getJob()) {
            case 410: 
            case 411: 
            case 412: {
                ISkill critSkill = SkillFactory.getSkill(4100001);
                byte critlevel = player.getSkillLevel(critSkill);
                if (critlevel <= 0) break;
                this.passive_sharpeye_percent = (short)(critSkill.getEffect(critlevel).getDamage() - 100 + added_sharpeye_dmg);
                this.passive_sharpeye_rate = (byte)(critSkill.getEffect(critlevel).getProb() + added_sharpeye_rate);
                return;
            }
            case 1410: 
            case 1411: 
            case 1412: {
                ISkill critSkill = SkillFactory.getSkill(14100001);
                byte critlevel = player.getSkillLevel(critSkill);
                if (critlevel <= 0) break;
                this.passive_sharpeye_percent = (short)(critSkill.getEffect(critlevel).getDamage() - 100 + added_sharpeye_dmg);
                this.passive_sharpeye_rate = (byte)(critSkill.getEffect(critlevel).getProb() + added_sharpeye_rate);
                return;
            }
            case 511: 
            case 512: {
                ISkill critSkill = SkillFactory.getSkill(5110000);
                byte critlevel = player.getSkillLevel(critSkill);
                if (critlevel <= 0) break;
                this.passive_sharpeye_percent = (short)(critSkill.getEffect(critlevel).getDamage() - 100 + added_sharpeye_dmg);
                this.passive_sharpeye_rate = (byte)(critSkill.getEffect(critlevel).getProb() + added_sharpeye_rate);
                return;
            }
            case 1511: 
            case 1512: {
                ISkill critSkill = SkillFactory.getSkill(15110000);
                byte critlevel = player.getSkillLevel(critSkill);
                if (critlevel <= 0) break;
                this.passive_sharpeye_percent = (short)(critSkill.getEffect(critlevel).getDamage() - 100 + added_sharpeye_dmg);
                this.passive_sharpeye_rate = (byte)(critSkill.getEffect(critlevel).getProb() + added_sharpeye_rate);
                return;
            }
            case 2111: 
            case 2112: {
                ISkill critSkill = SkillFactory.getSkill(21110000);
                byte critlevel = player.getSkillLevel(critSkill);
                if (critlevel <= 0) break;
                this.passive_sharpeye_percent = (short)(critSkill.getEffect(critlevel).getX() * critSkill.getEffect(critlevel).getDamage() + added_sharpeye_dmg);
                this.passive_sharpeye_rate = (byte)(critSkill.getEffect(critlevel).getX() * critSkill.getEffect(critlevel).getY() + added_sharpeye_rate);
                return;
            }
            case 300: 
            case 310: 
            case 311: 
            case 312: 
            case 320: 
            case 321: 
            case 322: {
                ISkill critSkill = SkillFactory.getSkill(3000001);
                byte critlevel = player.getSkillLevel(critSkill);
                if (critlevel <= 0) break;
                this.passive_sharpeye_percent = (short)(critSkill.getEffect(critlevel).getDamage() - 100 + added_sharpeye_dmg);
                this.passive_sharpeye_rate = (byte)(critSkill.getEffect(critlevel).getProb() + added_sharpeye_rate);
                return;
            }
            case 1300: 
            case 1310: 
            case 1311: 
            case 1312: {
                ISkill critSkill = SkillFactory.getSkill(13000000);
                byte critlevel = player.getSkillLevel(critSkill);
                if (critlevel <= 0) break;
                this.passive_sharpeye_percent = (short)(critSkill.getEffect(critlevel).getDamage() - 100 + added_sharpeye_dmg);
                this.passive_sharpeye_rate = (byte)(critSkill.getEffect(critlevel).getProb() + added_sharpeye_rate);
                return;
            }
            case 2214: 
            case 2215: 
            case 2216: 
            case 2217: 
            case 2218: {
                ISkill critSkill = SkillFactory.getSkill(22140000);
                byte critlevel = player.getSkillLevel(critSkill);
                if (critlevel <= 0) break;
                this.passive_sharpeye_percent = (short)(critSkill.getEffect(critlevel).getDamage() - 100 + added_sharpeye_dmg);
                this.passive_sharpeye_rate = (byte)(critSkill.getEffect(critlevel).getProb() + added_sharpeye_rate);
                return;
            }
        }
        this.passive_sharpeye_percent = (short)added_sharpeye_dmg;
        this.passive_sharpeye_rate = (byte)added_sharpeye_rate;
    }

    public final short passive_sharpeye_percent() {
        return this.passive_sharpeye_percent;
    }

    public final byte passive_sharpeye_rate() {
        return this.passive_sharpeye_rate;
    }

    public final byte passive_mastery() {
        return this.passive_mastery;
    }

    public final float calculateMaxBaseDamage(int watk) {
        float maxbasedamage;
        MapleCharacter chra = (MapleCharacter)this.chr.get();
        if (chra == null) {
            return 0.0f;
        }
        if (watk == 0) {
            maxbasedamage = 1.0f;
        } else {
            int secondarystat;
            int mainstat;
            IItem weapon_item = chra.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11);
            short job = chra.getJob();
            MapleWeaponType weapon = weapon_item == null ? MapleWeaponType.NOT_A_WEAPON : GameConstants.getWeaponType(weapon_item.getItemId());
            switch (weapon) {
                case BOW: 
                case CROSSBOW: {
                    mainstat = this.localdex;
                    secondarystat = this.localstr;
                    break;
                }
                case CLAW: 
                case KATARA: 
                case DAGGER: {
                    if (job >= 400 && job <= 434 || job >= 1400 && job <= 1412) {
                        mainstat = this.localluk;
                        secondarystat = this.localdex + this.localstr;
                        break;
                    }
                    mainstat = this.localstr;
                    secondarystat = this.localdex;
                    break;
                }
                case KNUCKLE: {
                    mainstat = this.localstr;
                    secondarystat = this.localdex;
                    break;
                }
                case GUN: {
                    mainstat = this.localdex;
                    secondarystat = this.localstr;
                    break;
                }
                case NOT_A_WEAPON: {
                    if (job >= 500 && job <= 522 || job >= 1500 && job <= 1512 || job >= 3500 && job <= 3512) {
                        mainstat = this.localstr;
                        secondarystat = this.localdex;
                        break;
                    }
                    mainstat = 0;
                    secondarystat = 0;
                    break;
                }
                default: {
                    mainstat = this.localstr;
                    secondarystat = this.localdex;
                }
            }
            maxbasedamage = (weapon.getMaxDamageMultiplier() * (float)mainstat + (float)secondarystat) * (float)watk / 100.0f;
        }
        return maxbasedamage;
    }

    public final float getHealHP() {
        return this.shouldHealHP;
    }

    public final float getHealMP() {
        return this.shouldHealMP;
    }

    public final void relocHeal() {
        byte lvl;
        ISkill effect;
        MapleCharacter chra = (MapleCharacter)this.chr.get();
        if (chra == null) {
            return;
        }
        short playerjob = chra.getJob();
        this.shouldHealHP = 10 + this.recoverHP;
        this.shouldHealMP = 3 + this.mpRestore + this.recoverMP;
        if (GameConstants.isJobFamily(200, playerjob)) {
            this.shouldHealMP += (float)chra.getSkillLevel(SkillFactory.getSkill(2000000)) / 10.0f * (float)chra.getLevel();
        } else if (GameConstants.isJobFamily(111, playerjob)) {
            effect = SkillFactory.getSkill(1110000);
            byte lvl2 = chra.getSkillLevel(effect);
            if (lvl2 > 0) {
                this.shouldHealMP += (float)effect.getEffect(lvl2).getMp();
            }
        } else if (GameConstants.isJobFamily(121, playerjob)) {
            effect = SkillFactory.getSkill(1210000);
            byte lvl3 = chra.getSkillLevel(effect);
            if (lvl3 > 0) {
                this.shouldHealMP += (float)effect.getEffect(lvl3).getMp();
            }
        } else if (GameConstants.isJobFamily(1111, playerjob)) {
            effect = SkillFactory.getSkill(11110000);
            byte lvl4 = chra.getSkillLevel(effect);
            if (lvl4 > 0) {
                this.shouldHealMP += (float)effect.getEffect(lvl4).getMp();
            }
        } else if (GameConstants.isJobFamily(410, playerjob)) {
            effect = SkillFactory.getSkill(4100002);
            byte lvl5 = chra.getSkillLevel(effect);
            if (lvl5 > 0) {
                this.shouldHealHP += (float)effect.getEffect(lvl5).getHp();
                this.shouldHealMP += (float)effect.getEffect(lvl5).getMp();
            }
        } else if (GameConstants.isJobFamily(420, playerjob) && (lvl = chra.getSkillLevel(effect = SkillFactory.getSkill(4200001))) > 0) {
            this.shouldHealHP += (float)effect.getEffect(lvl).getHp();
            this.shouldHealMP += (float)effect.getEffect(lvl).getMp();
        }
        if (chra.isGM()) {
            this.shouldHealHP += 1000.0f;
            this.shouldHealMP += 1000.0f;
        }
        if (chra.getChair() != 0) {
            this.shouldHealHP += 99.0f;
            this.shouldHealMP += 99.0f;
        } else {
            float recvRate = chra.getMap().getRecoveryRate();
            this.shouldHealHP *= recvRate;
            this.shouldHealMP *= recvRate;
        }
        this.shouldHealHP *= 2.0f;
        this.shouldHealMP *= 2.0f;
    }

    public final void connectData(MaplePacketLittleEndianWriter mplew) {
        mplew.writeShort(this.str);
        mplew.writeShort(this.dex);
        mplew.writeShort(this.int_);
        mplew.writeShort(this.luk);
        mplew.writeShort(this.hp);
        mplew.writeShort(this.maxhp);
        mplew.writeShort(this.mp);
        mplew.writeShort(this.maxmp);
    }

    public final int getSkillByJob(int skillID, int job) {
        if (GameConstants.isKOC(job)) {
            return skillID + 10000000;
        }
        if (GameConstants.isAran(job)) {
            return skillID + 20000000;
        }
        if (GameConstants.isEvan(job)) {
            return skillID + 20010000;
        }
        if (GameConstants.isResist(job)) {
            return skillID + 30000000;
        }
        return skillID;
    }
}

