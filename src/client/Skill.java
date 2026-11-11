/*
 * Decompiled with CFR 0.152.
 */
package client;

import client.ISkill;
import constants.GameConstants;
import java.util.ArrayList;
import java.util.List;
import provider.MapleData;
import provider.MapleDataTool;
import server.MapleStatEffect;
import server.life.Element;

public class Skill
implements ISkill {
    private String name = "";
    private final List<MapleStatEffect> effects = new ArrayList<MapleStatEffect>();
    private Element element;
    private byte level;
    private int id;
    private int animationTime;
    private int requiredSkill;
    private int masterLevel;
    private boolean action;
    private boolean invisible;
    private boolean chargeskill;
    private boolean timeLimited;

    public Skill(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public static final Skill loadFromData(int id, MapleData data) {
        Skill ret = new Skill(id);
        boolean isBuff = false;
        int skillType = MapleDataTool.getInt("skillType", data, -1);
        String elem = MapleDataTool.getString("elemAttr", data, null);
        ret.element = elem != null ? Element.getFromChar(elem.charAt(0)) : Element.NEUTRAL;
        ret.invisible = MapleDataTool.getInt("invisible", data, 0) > 0;
        ret.timeLimited = MapleDataTool.getInt("timeLimited", data, 0) > 0;
        ret.masterLevel = MapleDataTool.getInt("masterLevel", data, 0);
        MapleData effect = data.getChildByPath("effect");
        if (skillType != -1) {
            if (skillType == 2) {
                isBuff = true;
            }
        } else {
            MapleData action_ = data.getChildByPath("action");
            MapleData hit = data.getChildByPath("hit");
            MapleData ball = data.getChildByPath("ball");
            boolean action = false;
            if (action_ == null) {
                if (data.getChildByPath("prepare/action") != null) {
                    action = true;
                } else {
                    switch (id) {
                        case 3101005: 
                        case 4221001: 
                        case 4321000: 
                        case 4321001: 
                        case 4331001: 
                        case 5201001: 
                        case 5221009: {
                            action = true;
                        }
                    }
                }
            } else {
                action = true;
            }
            ret.action = action;
            isBuff = effect != null && hit == null && ball == null;
            isBuff |= action_ != null && MapleDataTool.getString("0", action_, "").equals("alert2");
            switch (id) {
                case 2111002: 
                case 2111003: 
                case 2121001: 
                case 2221001: 
                case 2301002: 
                case 2321001: 
                case 4211001: 
                case 12111005: {
                    isBuff = false;
                    break;
                }
                case 1004: 
                case 1026: 
                case 1111002: 
                case 1111007: 
                case 1211009: 
                case 1311007: 
                case 1320009: 
                case 4111001: 
                case 4211003: 
                case 4321000: 
                case 4331003: 
                case 4341002: 
                case 5001005: 
                case 5110001: 
                case 5111005: 
                case 5121003: 
                case 5121009: 
                case 5211001: 
                case 5211002: 
                case 5211006: 
                case 5220002: 
                case 5220011: 
                case 9001004: 
                case 9101004: 
                case 10001004: 
                case 10001026: 
                case 13111005: 
                case 15001003: 
                case 15100004: 
                case 15101006: 
                case 15111002: 
                case 15111005: 
                case 15111006: 
                case 20001004: 
                case 20001026: 
                case 20011004: 
                case 20011026: 
                case 21000000: 
                case 21101003: 
                case 22121001: 
                case 22131001: 
                case 22141002: 
                case 22151002: 
                case 22151003: 
                case 22171000: 
                case 22171004: 
                case 22181000: 
                case 22181003: 
                case 30001004: 
                case 30001026: 
                case 32001003: 
                case 32101002: 
                case 32101003: 
                case 32110000: 
                case 32120000: 
                case 32120001: 
                case 33111003: 
                case 35001001: 
                case 35001002: 
                case 35101002: 
                case 35101007: 
                case 35101009: 
                case 35111007: 
                case 35120000: 
                case 35121005: 
                case 35121006: 
                case 35121013: {
                    isBuff = true;
                }
            }
        }
        ret.chargeskill = data.getChildByPath("keydown") != null;
        for (MapleData level : data.getChildByPath("level")) {
            ret.effects.add(MapleStatEffect.loadSkillEffectFromData(level, id, isBuff, Byte.parseByte(level.getName())));
        }
        MapleData reqDataRoot = data.getChildByPath("req");
        if (reqDataRoot != null) {
            for (MapleData reqData : reqDataRoot.getChildren()) {
                ret.requiredSkill = Integer.parseInt(reqData.getName());
                ret.level = (byte)MapleDataTool.getInt(reqData, 1);
            }
        }
        ret.animationTime = 0;
        if (effect != null) {
            for (MapleData effectEntry : effect) {
                ret.animationTime += MapleDataTool.getIntConvert("delay", effectEntry, 0);
            }
        }
        return ret;
    }

    @Override
    public MapleStatEffect getEffect(int level) {
        if (this.effects.size() < level) {
            if (this.effects.size() > 0) {
                return this.effects.get(this.effects.size() - 1);
            }
            return null;
        }
        if (level <= 0) {
            return this.effects.get(0);
        }
        return this.effects.get(level - 1);
    }

    @Override
    public boolean getAction() {
        return this.action;
    }

    @Override
    public boolean isChargeSkill() {
        return this.chargeskill;
    }

    @Override
    public boolean isInvisible() {
        return this.invisible;
    }

    @Override
    public boolean hasRequiredSkill() {
        return this.level > 0;
    }

    @Override
    public int getRequiredSkillLevel() {
        return this.level;
    }

    @Override
    public int getRequiredSkillId() {
        return this.requiredSkill;
    }

    @Override
    public byte getMaxLevel() {
        return (byte)this.effects.size();
    }

    @Override
    public boolean canBeLearnedBy(int job) {
        int jid = job;
        int skillForJob = this.id / 10000;
        if (skillForJob == 0 || skillForJob == 1000 || skillForJob == 2000 || skillForJob == 2001 || skillForJob == 3000) {
            return true;
        }
        if (skillForJob == 2001 && GameConstants.isEvan(job)) {
            return true;
        }
        if (jid / 100 != skillForJob / 100) {
            return false;
        }
        if (jid / 1000 != skillForJob / 1000) {
            return false;
        }
        if (GameConstants.isAdventurer(skillForJob) && !GameConstants.isAdventurer(job)) {
            return false;
        }
        if (GameConstants.isKOC(skillForJob) && !GameConstants.isKOC(job)) {
            return false;
        }
        if (GameConstants.isAran(skillForJob) && !GameConstants.isAran(job)) {
            return false;
        }
        if (GameConstants.isEvan(skillForJob) && !GameConstants.isEvan(job)) {
            return false;
        }
        if (GameConstants.isResist(skillForJob) && !GameConstants.isResist(job)) {
            return false;
        }
        if (skillForJob / 10 % 10 > jid / 10 % 10) {
            return false;
        }
        return skillForJob % 10 <= jid % 10;
    }

    @Override
    public boolean isTimeLimited() {
        return this.timeLimited;
    }

    @Override
    public boolean isFourthJob() {
        if (this.id / 10000 >= 2212 && this.id / 10000 < 3000) {
            return this.id / 10000 % 10 >= 7;
        }
        if (this.id / 10000 >= 430 && this.id / 10000 <= 434) {
            return this.id / 10000 % 10 == 4 || this.getMasterLevel() > 0;
        }
        return this.id / 10000 % 10 == 2;
    }

    @Override
    public Element getElement() {
        return this.element;
    }

    @Override
    public int getAnimationTime() {
        return this.animationTime;
    }

    @Override
    public int getMasterLevel() {
        return this.masterLevel;
    }

    @Override
    public boolean isBeginnerSkill() {
        int jobId = this.id / 10000;
        return jobId == 0 || jobId == 1000 || jobId == 2000 || jobId == 2001 || jobId == 3000;
    }
}

