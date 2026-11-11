/*
 * Decompiled with CFR 0.152.
 */
package server.life;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import server.life.BanishInfo;
import server.life.Element;
import server.life.ElementalEffectiveness;
import tools.Pair;

public class MapleMonsterStats {
    private byte cp;
    private byte selfDestruction_action;
    private byte tagColor;
    private byte tagBgColor;
    private byte rareItemDropLevel;
    private byte HPDisplayType;
    private short level;
    private short PhysicalDefense;
    private short MagicDefense;
    private short eva;
    private long hp;
    private int exp;
    private int mp;
    private int removeAfter;
    private int buffToGive;
    private int fixedDamage;
    private int selfDestruction_hp;
    private int dropItemPeriod;
    private int point;
    private boolean boss;
    private boolean undead;
    private boolean ffaLoot;
    private boolean firstAttack;
    private boolean isExplosiveReward;
    private boolean mobile;
    private boolean fly;
    private boolean onlyNormalAttack;
    private boolean friendly;
    private boolean noDoom;
    private String name;
    private Map<Element, ElementalEffectiveness> resistance = new HashMap<Element, ElementalEffectiveness>();
    private List<Integer> revives = new ArrayList<Integer>();
    private List<Pair<Integer, Integer>> skills = new ArrayList<Pair<Integer, Integer>>();
    private BanishInfo banish;

    public int getExp() {
        return this.exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public long getHp() {
        return this.hp;
    }

    public void setHp(long hp) {
        this.hp = hp;
    }

    public int getMp() {
        return this.mp;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }

    public short getLevel() {
        return this.level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

    public void setSelfD(byte selfDestruction_action) {
        this.selfDestruction_action = selfDestruction_action;
    }

    public byte getSelfD() {
        return this.selfDestruction_action;
    }

    public void setSelfDHP(int selfDestruction_hp) {
        this.selfDestruction_hp = selfDestruction_hp;
    }

    public int getSelfDHp() {
        return this.selfDestruction_hp;
    }

    public void setFixedDamage(int damage) {
        this.fixedDamage = damage;
    }

    public int getFixedDamage() {
        return this.fixedDamage;
    }

    public void setPhysicalDefense(short PhysicalDefense) {
        this.PhysicalDefense = PhysicalDefense;
    }

    public short getPhysicalDefense() {
        return this.PhysicalDefense;
    }

    public final void setMagicDefense(short MagicDefense) {
        this.MagicDefense = MagicDefense;
    }

    public final short getMagicDefense() {
        return this.MagicDefense;
    }

    public final void setEva(short eva) {
        this.eva = eva;
    }

    public final short getEva() {
        return this.eva;
    }

    public void setOnlyNormalAttack(boolean onlyNormalAttack) {
        this.onlyNormalAttack = onlyNormalAttack;
    }

    public boolean getOnlyNoramlAttack() {
        return this.onlyNormalAttack;
    }

    public BanishInfo getBanishInfo() {
        return this.banish;
    }

    public void setBanishInfo(BanishInfo banish) {
        this.banish = banish;
    }

    public int getRemoveAfter() {
        return this.removeAfter;
    }

    public void setRemoveAfter(int removeAfter) {
        this.removeAfter = removeAfter;
    }

    public byte getrareItemDropLevel() {
        return this.rareItemDropLevel;
    }

    public void setrareItemDropLevel(byte rareItemDropLevel) {
        this.rareItemDropLevel = rareItemDropLevel;
    }

    public void setBoss(boolean boss) {
        this.boss = boss;
    }

    public boolean isBoss() {
        return this.boss;
    }

    public void setFfaLoot(boolean ffaLoot) {
        this.ffaLoot = ffaLoot;
    }

    public boolean isFfaLoot() {
        return this.ffaLoot;
    }

    public void setExplosiveReward(boolean isExplosiveReward) {
        this.isExplosiveReward = isExplosiveReward;
    }

    public boolean isExplosiveReward() {
        return this.isExplosiveReward;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    public boolean getMobile() {
        return this.mobile;
    }

    public void setFly(boolean fly) {
        this.fly = fly;
    }

    public boolean getFly() {
        return this.fly;
    }

    public List<Integer> getRevives() {
        return this.revives;
    }

    public void setRevives(List<Integer> revives) {
        this.revives = revives;
    }

    public void setUndead(boolean undead) {
        this.undead = undead;
    }

    public boolean getUndead() {
        return this.undead;
    }

    public void setEffectiveness(Element e, ElementalEffectiveness ee) {
        this.resistance.put(e, ee);
    }

    public void removeEffectiveness(Element e) {
        this.resistance.remove((Object)e);
    }

    public ElementalEffectiveness getEffectiveness(Element e) {
        ElementalEffectiveness elementalEffectiveness = this.resistance.get((Object)e);
        if (elementalEffectiveness == null) {
            return ElementalEffectiveness.NORMAL;
        }
        return elementalEffectiveness;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getTagColor() {
        return this.tagColor;
    }

    public void setTagColor(int tagColor) {
        this.tagColor = (byte)tagColor;
    }

    public byte getTagBgColor() {
        return this.tagBgColor;
    }

    public void setTagBgColor(int tagBgColor) {
        this.tagBgColor = (byte)tagBgColor;
    }

    public void setSkills(List<Pair<Integer, Integer>> skill_) {
        for (Pair<Integer, Integer> skill : skill_) {
            this.skills.add(skill);
        }
    }

    public List<Pair<Integer, Integer>> getSkills() {
        return Collections.unmodifiableList(this.skills);
    }

    public byte getNoSkills() {
        return (byte)this.skills.size();
    }

    public boolean hasSkill(int skillId, int level) {
        for (Pair<Integer, Integer> skill : this.skills) {
            if (skill.getLeft() != skillId || skill.getRight() != level) continue;
            return true;
        }
        return false;
    }

    public void setFirstAttack(boolean firstAttack) {
        this.firstAttack = firstAttack;
    }

    public boolean isFirstAttack() {
        return this.firstAttack;
    }

    public void setCP(byte cp) {
        this.cp = cp;
    }

    public byte getCP() {
        return this.cp;
    }

    public void setPoint(int cp) {
        this.point = cp;
    }

    public int getPoint() {
        return this.point;
    }

    public void setFriendly(boolean friendly) {
        this.friendly = friendly;
    }

    public boolean isFriendly() {
        return this.friendly;
    }

    public void setNoDoom(boolean doom) {
        this.noDoom = doom;
    }

    public boolean isNoDoom() {
        return this.noDoom;
    }

    public void setBuffToGive(int buff) {
        this.buffToGive = buff;
    }

    public int getBuffToGive() {
        return this.buffToGive;
    }

    public byte getHPDisplayType() {
        return this.HPDisplayType;
    }

    public void setHPDisplayType(byte HPDisplayType) {
        this.HPDisplayType = HPDisplayType;
    }

    public int getDropItemPeriod() {
        return this.dropItemPeriod;
    }

    public void setDropItemPeriod(int d) {
        this.dropItemPeriod = d;
    }
}

