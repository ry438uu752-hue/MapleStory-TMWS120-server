/*
 * Decompiled with CFR 0.152.
 */
package server;

import client.ISkill;
import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleCoolDownValueHolder;
import client.MapleDisease;
import client.MapleStat;
import client.PlayerStats;
import client.SkillFactory;
import client.inventory.IItem;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import constants.GameConstants;
import handling.channel.ChannelServer;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import provider.MapleData;
import provider.MapleDataTool;
import server.MapleCarnivalFactory;
import server.MapleInventoryManipulator;
import server.Randomizer;
import server.Timer;
import server.life.MapleMonster;
import server.maps.MapleDoor;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleMist;
import server.maps.MapleSummon;
import server.maps.SummonMovementType;
import tools.MaplePacketCreator;
import tools.Pair;

public class MapleStatEffect
implements Serializable {
    private static final long serialVersionUID = 9179541993413738569L;
    private byte mastery;
    private byte mhpR;
    private byte mmpR;
    private byte mobCount;
    private byte attackCount;
    private byte bulletCount;
    private short hp;
    private short mp;
    private short watk;
    private short matk;
    private short wdef;
    private short mdef;
    private short acc;
    private short avoid;
    private short hands;
    private short speed;
    private short jump;
    private short mpCon;
    private short hpCon;
    private short damage;
    private short prop;
    private short ehp;
    private short emp;
    private short ewatk;
    private short ewdef;
    private short emdef;
    private double hpR;
    private double mpR;
    private int duration;
    private int sourceid;
    private int moveTo;
    private int x;
    private int y;
    private int z;
    private int itemCon;
    private int itemConNo;
    private int bulletConsume;
    private int moneyCon;
    private int cooldown;
    private int morphId = 0;
    private int expinc;
    private boolean overTime;
    private boolean skill;
    private boolean partyBuff = true;
    private List<Pair<MapleBuffStat, Integer>> statups;
    private Map<MonsterStatus, Integer> monsterStatus;
    private Point lt;
    private Point rb;
    private int expBuff;
    private int itemup;
    private int mesoup;
    private int cashup;
    private int berserk;
    private int illusion;
    private int booster;
    private int berserk2;
    private int cp;
    private int nuffSkill;
    private int exp;
    private byte level;
    private List<MapleDisease> cureDebuffs;

    public static final MapleStatEffect loadSkillEffectFromData(MapleData source, int skillid, boolean overtime, byte level) {
        return MapleStatEffect.loadFromData(source, skillid, true, overtime, level);
    }

    public static final MapleStatEffect loadItemEffectFromData(MapleData source, int itemid) {
        return MapleStatEffect.loadFromData(source, itemid, false, false, (byte)1);
    }

    private static final void addBuffStatPairToListIfNotZero(List<Pair<MapleBuffStat, Integer>> list, MapleBuffStat buffstat, Integer val) {
        if (val != 0) {
            list.add(new Pair<MapleBuffStat, Integer>(buffstat, val));
        }
    }

    private static MapleStatEffect loadFromData(MapleData source, int sourceid, boolean skill, boolean overTime, byte level) {
        MapleStatEffect ret = new MapleStatEffect();
        ret.sourceid = sourceid;
        ret.skill = skill;
        ret.level = level;
        if (source == null) {
            return ret;
        }
        ret.duration = MapleDataTool.getIntConvert("time", source, -1);
        ret.hp = (short)MapleDataTool.getInt("hp", source, 0);
        ret.hpR = (double)MapleDataTool.getInt("hpR", source, 0) / 100.0;
        ret.mp = (short)MapleDataTool.getInt("mp", source, 0);
        ret.mpR = (double)MapleDataTool.getInt("mpR", source, 0) / 100.0;
        ret.mhpR = (byte)MapleDataTool.getInt("mhpR", source, 0);
        ret.mmpR = (byte)MapleDataTool.getInt("mmpR", source, 0);
        ret.mpCon = (short)MapleDataTool.getInt("mpCon", source, 0);
        ret.hpCon = (short)MapleDataTool.getInt("hpCon", source, 0);
        ret.prop = (short)MapleDataTool.getInt("prop", source, 100);
        ret.cooldown = MapleDataTool.getInt("cooltime", source, 0);
        ret.exp = MapleDataTool.getInt("exp", source, 0);
        ret.expinc = MapleDataTool.getInt("expinc", source, 0);
        ret.morphId = MapleDataTool.getInt("morph", source, 0);
        ret.cp = MapleDataTool.getInt("cp", source, 0);
        ret.nuffSkill = MapleDataTool.getInt("nuffSkill", source, 0);
        ret.mobCount = (byte)MapleDataTool.getInt("mobCount", source, 1);
        if (skill) {
            switch (sourceid) {
                case 1100002: 
                case 1100003: 
                case 1200002: 
                case 1200003: 
                case 1300002: 
                case 1300003: 
                case 3100001: 
                case 3200001: 
                case 11101002: 
                case 13101002: {
                    ret.mobCount = (byte)6;
                }
            }
        }
        if (!ret.skill && ret.duration > -1) {
            ret.overTime = true;
        } else {
            ret.duration *= 1000;
            ret.overTime = overTime || ret.isMorph() || ret.isPirateMorph() || ret.isFinalAttack();
        }
        ArrayList<Pair<MapleBuffStat, Integer>> statups = new ArrayList<Pair<MapleBuffStat, Integer>>();
        ret.mastery = (byte)MapleDataTool.getInt("mastery", source, 0);
        ret.watk = (short)MapleDataTool.getInt("pad", source, 0);
        ret.wdef = (short)MapleDataTool.getInt("pdd", source, 0);
        ret.matk = (short)MapleDataTool.getInt("mad", source, 0);
        ret.mdef = (short)MapleDataTool.getInt("mdd", source, 0);
        ret.ehp = (short)MapleDataTool.getInt("emhp", source, 0);
        ret.emp = (short)MapleDataTool.getInt("emmp", source, 0);
        ret.ewatk = (short)MapleDataTool.getInt("epad", source, 0);
        ret.ewdef = (short)MapleDataTool.getInt("epdd", source, 0);
        ret.emdef = (short)MapleDataTool.getInt("emdd", source, 0);
        ret.acc = (short)MapleDataTool.getIntConvert("acc", source, 0);
        ret.avoid = (short)MapleDataTool.getInt("eva", source, 0);
        ret.speed = (short)MapleDataTool.getInt("speed", source, 0);
        ret.jump = (short)MapleDataTool.getInt("jump", source, 0);
        ret.expBuff = MapleDataTool.getInt("expBuff", source, 0);
        ret.cashup = MapleDataTool.getInt("cashBuff", source, 0);
        ret.itemup = MapleDataTool.getInt("itemupbyitem", source, 0);
        ret.mesoup = MapleDataTool.getInt("mesoupbyitem", source, 0);
        ret.berserk = MapleDataTool.getInt("berserk", source, 0);
        ret.berserk2 = MapleDataTool.getInt("berserk2", source, 0);
        ret.booster = MapleDataTool.getInt("booster", source, 0);
        ret.illusion = MapleDataTool.getInt("illusion", source, 0);
        ArrayList<MapleDisease> cure = new ArrayList<MapleDisease>(5);
        if (MapleDataTool.getInt("poison", source, 0) > 0) {
            cure.add(MapleDisease.POISON);
        }
        if (MapleDataTool.getInt("seal", source, 0) > 0) {
            cure.add(MapleDisease.SEAL);
        }
        if (MapleDataTool.getInt("darkness", source, 0) > 0) {
            cure.add(MapleDisease.DARKNESS);
        }
        if (MapleDataTool.getInt("weakness", source, 0) > 0) {
            cure.add(MapleDisease.WEAKEN);
        }
        if (MapleDataTool.getInt("curse", source, 0) > 0) {
            cure.add(MapleDisease.CURSE);
        }
        ret.cureDebuffs = cure;
        MapleData ltd = source.getChildByPath("lt");
        if (ltd != null) {
            ret.lt = (Point)ltd.getData();
            ret.rb = (Point)source.getChildByPath("rb").getData();
        }
        ret.x = MapleDataTool.getInt("x", source, 0);
        ret.y = MapleDataTool.getInt("y", source, 0);
        ret.z = MapleDataTool.getInt("z", source, 0);
        ret.damage = (short)MapleDataTool.getIntConvert("damage", source, 100);
        ret.attackCount = (byte)MapleDataTool.getIntConvert("attackCount", source, 1);
        ret.bulletCount = (byte)MapleDataTool.getIntConvert("bulletCount", source, 1);
        ret.bulletConsume = MapleDataTool.getIntConvert("bulletConsume", source, 0);
        ret.moneyCon = MapleDataTool.getIntConvert("moneyCon", source, 0);
        ret.itemCon = MapleDataTool.getInt("itemCon", source, 0);
        ret.itemConNo = MapleDataTool.getInt("itemConNo", source, 0);
        ret.moveTo = MapleDataTool.getInt("moveTo", source, -1);
        EnumMap<MonsterStatus, Integer> monsterStatus = new EnumMap<MonsterStatus, Integer>(MonsterStatus.class);
        if (ret.overTime && ret.getSummonMovementType() == null) {
            MapleStatEffect.addBuffStatPairToListIfNotZero(statups, MapleBuffStat.WATK, Integer.valueOf(ret.watk));
            MapleStatEffect.addBuffStatPairToListIfNotZero(statups, MapleBuffStat.WDEF, Integer.valueOf(ret.wdef));
            MapleStatEffect.addBuffStatPairToListIfNotZero(statups, MapleBuffStat.MATK, Integer.valueOf(ret.matk));
            MapleStatEffect.addBuffStatPairToListIfNotZero(statups, MapleBuffStat.MDEF, Integer.valueOf(ret.mdef));
            MapleStatEffect.addBuffStatPairToListIfNotZero(statups, MapleBuffStat.ACC, Integer.valueOf(ret.acc));
            MapleStatEffect.addBuffStatPairToListIfNotZero(statups, MapleBuffStat.AVOID, Integer.valueOf(ret.avoid));
            MapleStatEffect.addBuffStatPairToListIfNotZero(statups, MapleBuffStat.SPEED, Integer.valueOf(ret.speed));
            MapleStatEffect.addBuffStatPairToListIfNotZero(statups, MapleBuffStat.JUMP, Integer.valueOf(ret.jump));
            MapleStatEffect.addBuffStatPairToListIfNotZero(statups, MapleBuffStat.MAXHP, Integer.valueOf(ret.mhpR));
            MapleStatEffect.addBuffStatPairToListIfNotZero(statups, MapleBuffStat.MAXMP, Integer.valueOf(ret.mmpR));
            MapleStatEffect.addBuffStatPairToListIfNotZero(statups, MapleBuffStat.EXPRATE, ret.expBuff);
            MapleStatEffect.addBuffStatPairToListIfNotZero(statups, MapleBuffStat.ACASH_RATE, ret.cashup);
            MapleStatEffect.addBuffStatPairToListIfNotZero(statups, MapleBuffStat.DROP_RATE, ret.itemup * 200);
            MapleStatEffect.addBuffStatPairToListIfNotZero(statups, MapleBuffStat.MESO_RATE, ret.mesoup * 200);
            MapleStatEffect.addBuffStatPairToListIfNotZero(statups, MapleBuffStat.BERSERK_FURY, ret.berserk2);
            MapleStatEffect.addBuffStatPairToListIfNotZero(statups, MapleBuffStat.PYRAMID_PQ, ret.berserk);
            MapleStatEffect.addBuffStatPairToListIfNotZero(statups, MapleBuffStat.BOOSTER, ret.booster);
            MapleStatEffect.addBuffStatPairToListIfNotZero(statups, MapleBuffStat.ILLUSION, ret.illusion);
            MapleStatEffect.addBuffStatPairToListIfNotZero(statups, MapleBuffStat.ENHANCED_WATK, Integer.valueOf(ret.ewatk));
            MapleStatEffect.addBuffStatPairToListIfNotZero(statups, MapleBuffStat.ENHANCED_WDEF, Integer.valueOf(ret.ewdef));
            MapleStatEffect.addBuffStatPairToListIfNotZero(statups, MapleBuffStat.ENHANCED_MDEF, Integer.valueOf(ret.emdef));
            MapleStatEffect.addBuffStatPairToListIfNotZero(statups, MapleBuffStat.ENHANCED_MAXHP, Integer.valueOf(ret.ehp));
            MapleStatEffect.addBuffStatPairToListIfNotZero(statups, MapleBuffStat.ENHANCED_MAXMP, Integer.valueOf(ret.ehp));
        }
        if (skill) {
            switch (sourceid) {
                case 2001002: 
                case 12001001: 
                case 22111001: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MAGIC_GUARD, ret.x));
                    break;
                }
                case 2301003: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.INVINCIBLE, ret.x));
                    break;
                }
                case 35001002: 
                case 35120000: {
                    ret.duration = 0x6DDD00;
                    break;
                }
                case 9001004: {
                    ret.duration = 0x6DDD00;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DARKSIGHT, ret.x));
                    break;
                }
                case 4001003: 
                case 0x421211: 
                case 0xC7E7CE: 
                case 14001003: 
                case 30001001: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DARKSIGHT, ret.x));
                    break;
                }
                case 4211003: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.PICKPOCKET, ret.x));
                    break;
                }
                case 4211005: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MESOGUARD, ret.x));
                    break;
                }
                case 4111001: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MESOUP, ret.x));
                    break;
                }
                case 4111002: 
                case 14111000: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SHADOWPARTNER, ret.x));
                    break;
                }
                case 11101002: 
                case 21120002: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.FINAL_MEL_ATTACK, ret.x));
                    break;
                }
                case 13101002: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.FINALATTACK, ret.x));
                    break;
                }
                case 8001: 
                case 2311002: 
                case 3101004: 
                case 3201004: 
                case 10008001: 
                case 13101003: 
                case 20008001: 
                case 20018001: 
                case 30008001: 
                case 33101003: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SOULARROW, ret.x));
                    break;
                }
                case 1211003: 
                case 1211004: 
                case 1211005: 
                case 1211006: 
                case 1211007: 
                case 1211008: 
                case 1221003: 
                case 1221004: 
                case 11111007: 
                case 15101006: 
                case 21111005: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.WK_CHARGE, ret.x));
                    break;
                }
                case 12101005: 
                case 22121001: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.ELEMENT_RESET, ret.x));
                    break;
                }
                case 3121008: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.CONCENTRATE, ret.x));
                    break;
                }
                case 5110001: 
                case 15100004: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.ENERGY_CHARGE, 0));
                    break;
                }
                case 0x10CCCC: 
                case 1101005: 
                case 1201004: 
                case 1201005: 
                case 1301004: 
                case 1301005: 
                case 2111005: 
                case 2211005: 
                case 3101002: 
                case 3201002: 
                case 4101003: 
                case 4201002: 
                case 4301002: 
                case 5101006: 
                case 5201003: 
                case 11101001: 
                case 12101004: 
                case 13101001: 
                case 14101002: 
                case 15101002: 
                case 21001003: 
                case 22141002: 
                case 32101005: 
                case 33001003: 
                case 35001003: 
                case 35101006: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.BOOSTER, ret.x));
                    break;
                }
                case 5121009: 
                case 15111005: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SPEED_INFUSION, ret.x));
                    break;
                }
                case 4321000: {
                    ret.duration = 1000;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DASH_SPEED, 100 + ret.x));
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DASH_JUMP, ret.y));
                    break;
                }
                case 5001005: 
                case 15001003: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DASH_SPEED, ret.x));
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DASH_JUMP, ret.y));
                    break;
                }
                case 1101007: 
                case 1201007: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.POWERGUARD, ret.x));
                    break;
                }
                case 32111004: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.CONVERSION, ret.x));
                    break;
                }
                case 8003: 
                case 1301007: 
                case 9001008: 
                case 10008003: 
                case 20008003: 
                case 20018003: 
                case 30008003: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MAXHP, ret.x));
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MAXMP, ret.y));
                    break;
                }
                case 1001: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.RECOVERY, ret.x));
                    break;
                }
                case 1111002: 
                case 11111001: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.COMBO, 1));
                    break;
                }
                case 21120007: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.COMBO_BARRIER, ret.x));
                    break;
                }
                case 5211006: 
                case 5220011: 
                case 22151002: {
                    ret.duration = 0x6DDD00;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.HOMING_BEACON, ret.x));
                    break;
                }
                case 4341003: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.HOMING_BEACON, ret.x));
                    break;
                }
                case 1011: 
                case 10001011: 
                case 20001011: 
                case 20011011: 
                case 30001011: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.BERSERK_FURY, 1));
                    break;
                }
                case 1010: 
                case 10001010: 
                case 20001010: 
                case 20011010: 
                case 30001010: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DIVINE_BODY, 1));
                    break;
                }
                case 1311006: {
                    ret.hpR = (double)(-ret.x) / 100.0;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DRAGON_ROAR, ret.y));
                    break;
                }
                case 4341007: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.THORNS, ret.x << 8 | ret.y));
                    break;
                }
                case 4341002: {
                    ret.duration = 60000;
                    ret.hpR = (double)(-ret.x) / 100.0;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.FINAL_CUT, ret.y));
                    break;
                }
                case 4331002: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MIRROR_IMAGE, ret.x));
                    break;
                }
                case 4331003: {
                    ret.duration = 60000;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.OWL_SPIRIT, ret.y));
                    break;
                }
                case 1311008: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DRAGONBLOOD, ret.x));
                    break;
                }
                case 1121000: 
                case 1221000: 
                case 1321000: 
                case 2121000: 
                case 2221000: 
                case 2321000: 
                case 3121000: 
                case 3221000: 
                case 4121000: 
                case 4221000: 
                case 4341000: 
                case 5121000: 
                case 5221000: 
                case 21121000: 
                case 22171000: 
                case 32121007: 
                case 33121007: 
                case 35121007: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MAPLE_WARRIOR, ret.x));
                    break;
                }
                case 15111006: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SPARK, ret.x));
                    break;
                }
                case 8002: 
                case 3121002: 
                case 3221002: 
                case 10008002: 
                case 20008002: 
                case 20018002: 
                case 30008002: 
                case 33121004: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SHARP_EYES, ret.x << 8 | ret.y));
                    break;
                }
                case 22151003: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MAGIC_RESISTANCE, ret.x));
                    break;
                }
                case 21101003: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.BODY_PRESSURE, ret.x));
                    break;
                }
                case 21000000: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.ARAN_COMBO, 100));
                    break;
                }
                case 21100005: 
                case 32101004: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.COMBO_DRAIN, ret.x));
                    break;
                }
                case 21111001: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SMART_KNOCKBACK, ret.x));
                    break;
                }
                case 22131001: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MAGIC_SHIELD, ret.x));
                    break;
                }
                case 22181003: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SOUL_STONE, 1));
                    break;
                }
                case 4001002: 
                case 14001002: {
                    monsterStatus.put(MonsterStatus.WATK, ret.x);
                    monsterStatus.put(MonsterStatus.WDEF, ret.y);
                    break;
                }
                case 5221009: {
                    monsterStatus.put(MonsterStatus.HYPNOTIZE, 1);
                    break;
                }
                case 1201006: {
                    monsterStatus.put(MonsterStatus.WATK, ret.x);
                    monsterStatus.put(MonsterStatus.WDEF, ret.y);
                    break;
                }
                case 1111005: 
                case 1111006: 
                case 1111008: 
                case 1211002: 
                case 3101005: 
                case 4121008: 
                case 4201004: 
                case 4211002: 
                case 4221007: 
                case 4331005: 
                case 5101002: 
                case 5101003: 
                case 5111002: 
                case 5121004: 
                case 5121005: 
                case 5121007: 
                case 5201004: 
                case 15101005: 
                case 22151001: 
                case 32111010: 
                case 32121004: 
                case 33101001: 
                case 33101002: 
                case 33111002: 
                case 33121002: 
                case 35101003: 
                case 35111015: {
                    monsterStatus.put(MonsterStatus.STUN, 1);
                    break;
                }
                case 4321002: {
                    monsterStatus.put(MonsterStatus.DARKNESS, 1);
                    break;
                }
                case 4121003: 
                case 4221003: 
                case 33121005: {
                    monsterStatus.put(MonsterStatus.SHOWDOWN, ret.x);
                    monsterStatus.put(MonsterStatus.MDEF, ret.x);
                    monsterStatus.put(MonsterStatus.WDEF, ret.x);
                    break;
                }
                case 2121006: 
                case 2201004: 
                case 2211002: 
                case 2211006: 
                case 2221007: 
                case 3211003: 
                case 5211005: 
                case 21120006: 
                case 22121000: {
                    monsterStatus.put(MonsterStatus.FREEZE, 1);
                    ret.duration *= 2;
                    break;
                }
                case 2101003: 
                case 2201003: 
                case 12101001: 
                case 22141003: {
                    monsterStatus.put(MonsterStatus.SPEED, ret.x);
                    break;
                }
                case 2101005: 
                case 2111006: 
                case 2121003: 
                case 2221003: 
                case 3111003: 
                case 22161002: {
                    monsterStatus.put(MonsterStatus.POISON, 1);
                    break;
                }
                case 4121004: 
                case 4221004: {
                    monsterStatus.put(MonsterStatus.NINJA_AMBUSH, Integer.valueOf(ret.damage));
                    break;
                }
                case 2311005: {
                    monsterStatus.put(MonsterStatus.DOOM, 1);
                    break;
                }
                case 32111006: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.REAPER, 1));
                    break;
                }
                case 3111002: 
                case 3211002: 
                case 4341006: 
                case 5211001: 
                case 5220002: 
                case 13111004: 
                case 33111003: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.PUPPET, 1));
                    break;
                }
                case 3111005: 
                case 3211005: 
                case 33111005: 
                case 35111002: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SUMMON, 1));
                    monsterStatus.put(MonsterStatus.STUN, 1);
                    break;
                }
                case 2121005: 
                case 3221005: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SUMMON, 1));
                    monsterStatus.put(MonsterStatus.FREEZE, 1);
                    break;
                }
                case 1321007: 
                case 2221005: 
                case 2311006: 
                case 2321003: 
                case 3121006: 
                case 5211002: 
                case 11001004: 
                case 12001004: 
                case 12111004: 
                case 13001004: 
                case 14001005: 
                case 15001004: 
                case 35111001: 
                case 35111004: 
                case 35111005: 
                case 35111009: 
                case 35111010: 
                case 35121009: 
                case 35121011: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SUMMON, 1));
                    break;
                }
                case 2311003: 
                case 9001002: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.HOLY_SYMBOL, ret.x));
                    break;
                }
                case 2111004: 
                case 2211004: 
                case 12111002: {
                    monsterStatus.put(MonsterStatus.SEAL, 1);
                    break;
                }
                case 4111003: 
                case 14111001: {
                    monsterStatus.put(MonsterStatus.SHADOW_WEB, 1);
                    break;
                }
                case 4121006: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SPIRIT_CLAW, 0));
                    break;
                }
                case 2121004: 
                case 2221004: 
                case 2321004: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.INFINITY, ret.x));
                    break;
                }
                case 0x111AEA: 
                case 1221002: 
                case 1321002: 
                case 21121003: 
                case 32121005: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.STANCE, Integer.valueOf(ret.prop)));
                    break;
                }
                case 1005: 
                case 10001005: 
                case 20001005: 
                case 20011005: 
                case 30001005: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.ECHO_OF_HERO, ret.x));
                    break;
                }
                case 1026: 
                case 10001026: 
                case 20001026: 
                case 20011026: 
                case 30001026: {
                    ret.duration = 0x6DDD00;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SOARING, 1));
                    break;
                }
                case 2121002: 
                case 2221002: 
                case 2321002: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MANA_REFLECTION, 1));
                    break;
                }
                case 2321005: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.HOLY_SHIELD, ret.x));
                    break;
                }
                case 3121007: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.HAMSTRING, ret.x));
                    monsterStatus.put(MonsterStatus.SPEED, ret.x);
                    break;
                }
                case 3221006: 
                case 33111004: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.BLIND, ret.x));
                    monsterStatus.put(MonsterStatus.ACC, ret.x);
                    break;
                }
                case 33121006: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MAXHP, ret.x));
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.WATK, ret.y));
                    break;
                }
                case 32001003: 
                case 32120000: {
                    ret.duration = 0x6DDD00;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DARK_AURA, ret.x));
                    break;
                }
                case 32101002: 
                case 32110000: {
                    ret.duration = 0x6DDD00;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.BLUE_AURA, ret.x));
                    break;
                }
                case 32101003: 
                case 32120001: {
                    ret.duration = 0x6DDD00;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.YELLOW_AURA, ret.x));
                    break;
                }
                case 33101004: {
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.RAINING_MINES, ret.x));
                    break;
                }
                case 35101007: {
                    ret.duration = 0x6DDD00;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.PERFECT_ARMOR, ret.x));
                    break;
                }
                case 35121006: {
                    ret.duration = 0x6DDD00;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SATELLITESAFE_PROC, ret.x));
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SATELLITESAFE_ABSORB, ret.y));
                    break;
                }
                case 35001001: 
                case 35101009: 
                case 35111007: {
                    ret.duration = 8000;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MECH_CHANGE, Integer.valueOf(level)));
                    break;
                }
                case 35101002: 
                case 35121013: {
                    ret.duration = 5000;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MECH_CHANGE, Integer.valueOf(level)));
                    break;
                }
                case 35121005: {
                    ret.duration = 0x6DDD00;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MECH_CHANGE, Integer.valueOf(level)));
                    break;
                }
            }
        }
        if (ret.isMonsterRiding()) {
            statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MONSTER_RIDING, 1));
        }
        if (ret.isMorph() || ret.isPirateMorph()) {
            statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MORPH, ret.getMorph()));
        }
        ret.monsterStatus = monsterStatus;
        statups.trimToSize();
        ret.statups = statups;
        return ret;
    }

    public final void applyPassive(MapleCharacter applyto, MapleMapObject obj) {
        if (this.makeChanceResult()) {
            switch (this.sourceid) {
                case 0x200B20: 
                case 2200000: 
                case 2300000: {
                    int absorbMp;
                    if (obj == null || obj.getType() != MapleMapObjectType.MONSTER) {
                        return;
                    }
                    MapleMonster mob2 = (MapleMonster)obj;
                    if (mob2.getStats().isBoss() || (absorbMp = Math.min((int)((double)mob2.getMobMaxMp() * ((double)this.getX() / 70.0)), mob2.getMp())) <= 0) break;
                    mob2.setMp(mob2.getMp() - absorbMp);
                    applyto.getStat().setMp((short)(applyto.getStat().getMp() + absorbMp));
                    applyto.getClient().getSession().write((Object)MaplePacketCreator.showOwnBuffEffect(this.sourceid, 1));
                    applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.showBuffeffect(applyto.getId(), this.sourceid, 1), false);
                }
            }
        }
    }

    public final boolean applyTo(MapleCharacter chr) {
        return this.applyTo(chr, chr, true, null, this.duration);
    }

    public final boolean applyTo(MapleCharacter chr, Point pos) {
        return this.applyTo(chr, chr, true, pos, this.duration);
    }

    private final boolean applyTo(MapleCharacter applyfrom, MapleCharacter applyto, boolean primary, Point pos) {
        return this.applyTo(applyfrom, applyto, primary, pos, this.duration);
    }

    public final boolean applyTo(MapleCharacter applyfrom, MapleCharacter applyto, boolean primary, Point pos, int newDuration) {
        block52: {
            block53: {
                block54: {
                    block51: {
                        SummonMovementType summonMovementType;
                        MapleCarnivalFactory.MCSkill skil;
                        if (this.isHeal() && (applyfrom.getMapId() == 749040100 || applyto.getMapId() == 749040100)) {
                            return false;
                        }
                        if (this.sourceid == 4341006 && applyfrom.getBuffedValue(MapleBuffStat.MIRROR_IMAGE) == null) {
                            applyfrom.getClient().getSession().write((Object)MaplePacketCreator.enableActions());
                            return false;
                        }
                        if (this.sourceid == 33101004 && applyfrom.getMap().isTown()) {
                            applyfrom.dropMessage(5, "You may not use this skill in towns.");
                            applyfrom.getClient().getSession().write((Object)MaplePacketCreator.enableActions());
                            return false;
                        }
                        int hpchange = this.calcHPChange(applyfrom, primary);
                        int mpchange = this.calcMPChange(applyfrom, primary);
                        PlayerStats stat = applyto.getStat();
                        if (primary) {
                            if (this.itemConNo != 0 && !applyto.isClone()) {
                                MapleInventoryManipulator.removeById(applyto.getClient(), GameConstants.getInventoryType(this.itemCon), this.itemCon, this.itemConNo, false, true);
                            }
                        } else if (!primary && this.isResurrection()) {
                            hpchange = stat.getMaxHp();
                            applyto.setStance(0);
                        }
                        if (this.isDispel() && this.makeChanceResult()) {
                            applyto.dispelDebuffs();
                        } else if (this.isHeroWill()) {
                            applyto.dispelDebuff(MapleDisease.SEDUCE);
                        } else if (this.cureDebuffs.size() > 0) {
                            for (MapleDisease debuff : this.cureDebuffs) {
                                applyfrom.dispelDebuff(debuff);
                            }
                        } else if (this.isMPRecovery()) {
                            int toDecreaseHP = stat.getMaxHp() / 100 * 10;
                            if (stat.getHp() > toDecreaseHP) {
                                hpchange += -toDecreaseHP;
                                mpchange += toDecreaseHP / 100 * this.getY();
                            } else {
                                hpchange = stat.getHp() == 1 ? 0 : stat.getHp() - 1;
                            }
                        }
                        ArrayList<Pair<MapleStat, Integer>> hpmpupdate = new ArrayList<Pair<MapleStat, Integer>>(2);
                        if (hpchange != 0) {
                            if (hpchange < 0 && -hpchange > stat.getHp() && !applyto.hasDisease(MapleDisease.ZOMBIFY)) {
                                return false;
                            }
                            stat.setHp(stat.getHp() + hpchange);
                        }
                        if (mpchange != 0) {
                            if (mpchange < 0 && -mpchange > stat.getMp()) {
                                return false;
                            }
                            stat.setMp(stat.getMp() + mpchange);
                            hpmpupdate.add(new Pair<MapleStat, Integer>(MapleStat.MP, Integer.valueOf(stat.getMp())));
                        }
                        hpmpupdate.add(new Pair<MapleStat, Integer>(MapleStat.HP, Integer.valueOf(stat.getHp())));
                        applyto.getClient().getSession().write((Object)MaplePacketCreator.updatePlayerStats(hpmpupdate, true, applyto.getJob()));
                        if (this.expinc != 0) {
                            applyto.gainExp(this.expinc, true, true, false);
                        } else if (GameConstants.isMonsterCard(this.sourceid)) {
                            applyto.getMonsterBook().addCard(applyto.getClient(), this.sourceid);
                        } else if (this.isSpiritClaw() && !applyto.isClone()) {
                            MapleInventory use = applyto.getInventory(MapleInventoryType.USE);
                            for (int i = 0; i < use.getSlotLimit(); ++i) {
                                IItem item = use.getItem((byte)i);
                                if (item == null || !GameConstants.isThrowingStar(item.getItemId()) || item.getQuantity() < 200) continue;
                                MapleInventoryManipulator.removeById(applyto.getClient(), MapleInventoryType.USE, item.getItemId(), 200, false, true);
                                break;
                            }
                        } else if (this.cp != 0 && applyto.getCarnivalParty() != null) {
                            applyto.getCarnivalParty().addCP(applyto, this.cp);
                            applyto.CPUpdate(false, applyto.getAvailableCP(), applyto.getTotalCP(), 0);
                            for (MapleCharacter chr : applyto.getMap().getCharactersThreadsafe()) {
                                chr.CPUpdate(true, applyto.getCarnivalParty().getAvailableCP(), applyto.getCarnivalParty().getTotalCP(), applyto.getCarnivalParty().getTeam());
                            }
                        } else if (this.nuffSkill != 0 && applyto.getParty() != null && (skil = MapleCarnivalFactory.getInstance().getSkill(this.nuffSkill)) != null) {
                            MapleDisease dis = skil.getDisease();
                            for (MapleCharacter chr : applyto.getMap().getCharactersThreadsafe()) {
                                if (chr.getParty() != null && chr.getParty().getId() == applyto.getParty().getId() || !skil.targetsAll && !Randomizer.nextBoolean()) continue;
                                if (dis == null) {
                                    chr.dispel();
                                } else if (skil.getSkill() == null) {
                                    chr.giveDebuff(dis, 1, 30000L, MapleDisease.getByDisease(dis), 1);
                                } else {
                                    chr.giveDebuff(dis, skil.getSkill());
                                }
                                if (skil.targetsAll) continue;
                                break;
                            }
                        }
                        if (this.overTime && !this.isEnergyCharge()) {
                            this.applyBuffEffect(applyfrom, applyto, primary, newDuration);
                        }
                        if (this.skill) {
                            this.removeMonsterBuff(applyfrom);
                        }
                        if (primary) {
                            if ((this.overTime || this.isHeal()) && !this.isEnergyCharge()) {
                                this.applyBuff(applyfrom, newDuration);
                            }
                            if (this.isMonsterBuff()) {
                                this.applyMonsterBuff(applyfrom);
                            }
                        }
                        if ((summonMovementType = this.getSummonMovementType()) == null) break block51;
                        MapleSummon tosummon = new MapleSummon(applyfrom, this, new Point(pos == null ? applyfrom.getPosition() : pos), summonMovementType);
                        if (!tosummon.isPuppet()) {
                            applyfrom.getCheatTracker().resetSummonAttack();
                        }
                        applyfrom.getMap().spawnSummon(tosummon);
                        applyfrom.getSummons().put(this.sourceid, tosummon);
                        tosummon.addHP((short)this.x);
                        if (this.isBeholder()) {
                            tosummon.addHP((short)1);
                        }
                        if (this.sourceid != 4341006) break block52;
                        applyfrom.cancelEffectFromBuffStat(MapleBuffStat.MIRROR_IMAGE);
                        break block52;
                    }
                    if (!this.isMagicDoor()) break block53;
                    MapleDoor door = new MapleDoor(applyto, new Point(applyto.getPosition()), this.sourceid);
                    if (door.getTownPortal() == null) break block54;
                    applyto.getMap().spawnDoor(door);
                    applyto.addDoor(door);
                    MapleDoor townDoor = new MapleDoor(door);
                    applyto.addDoor(townDoor);
                    door.getTown().spawnDoor(townDoor);
                    if (applyto.getParty() == null) break block52;
                    applyto.silentPartyUpdate();
                    break block52;
                }
                applyto.dropMessage(5, "\u4f60\u53ef\u80fd\u6c92\u8fa6\u6cd5\u4f7f\u7528\u50b3\u9001\u5011\u56e0\u70ba\u6751\u838a\u5167\u7981\u6b62..");
                break block52;
            }
            if (this.isMist()) {
                Rectangle bounds = this.calculateBoundingBox(pos != null ? pos : new Point(applyfrom.getPosition()), applyfrom.isFacingLeft());
                MapleMist mist = new MapleMist(bounds, applyfrom, this);
                applyfrom.getMap().spawnMist(mist, this.getDuration(), false);
            } else if (this.isTimeLeap()) {
                for (MapleCoolDownValueHolder i : applyto.getCooldowns()) {
                    if (i.skillId == 5121010) continue;
                    applyto.removeCooldown(i.skillId);
                    applyto.getClient().getSession().write((Object)MaplePacketCreator.skillCooldown(i.skillId, 0));
                }
            } else {
                for (WeakReference<MapleCharacter> chrz : applyto.getClones()) {
                    if (chrz.get() == null) continue;
                    this.applyTo((MapleCharacter)chrz.get(), (MapleCharacter)chrz.get(), primary, pos, newDuration);
                }
            }
        }
        return true;
    }

    public final boolean applyReturnScroll(MapleCharacter applyto) {
        if (this.moveTo != -1 && applyto.getMap().getReturnMapId() != applyto.getMapId()) {
            MapleMap target;
            if (this.moveTo == 999999999) {
                target = applyto.getMap().getReturnMap();
            } else {
                target = ChannelServer.getInstance(applyto.getClient().getChannel()).getMapFactory().getMap(this.moveTo);
                if (target.getId() / 10000000 != 60 && applyto.getMapId() / 10000000 != 61 && target.getId() / 10000000 != 21 && applyto.getMapId() / 10000000 != 20 && target.getId() / 10000000 != applyto.getMapId() / 10000000) {
                    return false;
                }
            }
            applyto.changeMap(target, target.getPortal(0));
            return true;
        }
        return false;
    }

    private final boolean isSoulStone() {
        return this.skill && this.sourceid == 22181003;
    }

    private final void applyBuff(MapleCharacter applyfrom, int newDuration) {
        block9: {
            block8: {
                if (!this.isSoulStone()) break block8;
                if (applyfrom.getParty() == null) break block9;
                int membrs = 0;
                for (MapleCharacter chr : applyfrom.getMap().getCharactersThreadsafe()) {
                    if (chr.getParty() == null || !chr.getParty().equals(applyfrom.getParty()) || !chr.isAlive()) continue;
                    ++membrs;
                }
                ArrayList<MapleCharacter> awarded = new ArrayList<MapleCharacter>();
                while (awarded.size() < Math.min(membrs, this.y)) {
                    for (MapleCharacter chr : applyfrom.getMap().getCharactersThreadsafe()) {
                        if (!chr.isAlive() || !chr.getParty().equals(applyfrom.getParty()) || awarded.contains(chr) || Randomizer.nextInt(this.y) != 0) continue;
                        awarded.add(chr);
                    }
                }
                for (MapleCharacter chr : awarded) {
                    this.applyTo(applyfrom, chr, false, null, newDuration);
                    chr.getClient().getSession().write((Object)MaplePacketCreator.showOwnBuffEffect(this.sourceid, 2));
                    chr.getMap().broadcastMessage(chr, MaplePacketCreator.showBuffeffect(chr.getId(), this.sourceid, 2), false);
                }
                break block9;
            }
            if (this.isPartyBuff() && (applyfrom.getParty() != null || this.isGmBuff())) {
                Rectangle bounds = this.calculateBoundingBox(applyfrom.getPosition(), applyfrom.isFacingLeft());
                List<MapleMapObject> affecteds = applyfrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(MapleMapObjectType.PLAYER));
                for (MapleMapObject affectedmo : affecteds) {
                    MapleCharacter affected = (MapleCharacter)affectedmo;
                    if (affected == applyfrom || !this.isGmBuff() && !applyfrom.getParty().equals(affected.getParty())) continue;
                    if (this.isResurrection() && !affected.isAlive() || !this.isResurrection() && affected.isAlive()) {
                        this.applyTo(applyfrom, affected, false, null, newDuration);
                        affected.getClient().getSession().write((Object)MaplePacketCreator.showOwnBuffEffect(this.sourceid, 2));
                        affected.getMap().broadcastMessage(affected, MaplePacketCreator.showBuffeffect(affected.getId(), this.sourceid, 2), false);
                    }
                    if (!this.isTimeLeap()) continue;
                    for (MapleCoolDownValueHolder i : affected.getCooldowns()) {
                        if (i.skillId == 5121010) continue;
                        affected.removeCooldown(i.skillId);
                        affected.getClient().getSession().write((Object)MaplePacketCreator.skillCooldown(i.skillId, 0));
                    }
                }
            }
        }
    }

    private final void removeMonsterBuff(MapleCharacter applyfrom) {
        ArrayList<MonsterStatus> cancel = new ArrayList<MonsterStatus>();
        switch (this.sourceid) {
            case 1111007: {
                cancel.add(MonsterStatus.WDEF);
                cancel.add(MonsterStatus.WEAPON_DEFENSE_UP);
                break;
            }
            case 1211009: {
                cancel.add(MonsterStatus.MDEF);
                cancel.add(MonsterStatus.MAGIC_DEFENSE_UP);
                break;
            }
            case 1311007: {
                cancel.add(MonsterStatus.WATK);
                cancel.add(MonsterStatus.WEAPON_ATTACK_UP);
                cancel.add(MonsterStatus.MATK);
                cancel.add(MonsterStatus.MAGIC_ATTACK_UP);
                break;
            }
            default: {
                return;
            }
        }
        Rectangle bounds = this.calculateBoundingBox(applyfrom.getPosition(), applyfrom.isFacingLeft());
        List<MapleMapObject> affected = applyfrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(MapleMapObjectType.MONSTER));
        byte i = 0;
        for (MapleMapObject mo : affected) {
            if (this.makeChanceResult()) {
                for (MonsterStatus stat : cancel) {
                    ((MapleMonster)mo).cancelStatus(stat);
                }
            }
            if (++i < this.mobCount) continue;
            break;
        }
    }

    private final void applyMonsterBuff(MapleCharacter applyfrom) {
        Rectangle bounds = this.calculateBoundingBox(applyfrom.getPosition(), applyfrom.isFacingLeft());
        List<MapleMapObject> affected = applyfrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(MapleMapObjectType.MONSTER));
        byte i = 0;
        for (MapleMapObject mo : affected) {
            if (this.makeChanceResult()) {
                for (Map.Entry<MonsterStatus, Integer> stat : this.getMonsterStati().entrySet()) {
                    ((MapleMonster)mo).applyStatus(applyfrom, new MonsterStatusEffect(stat.getKey(), stat.getValue(), this.sourceid, null, false), this.isPoison(), this.getDuration() + i * 113, false);
                }
            }
            if (++i < this.mobCount) continue;
            break;
        }
    }

    private final Rectangle calculateBoundingBox(Point posFrom, boolean facingLeft) {
        Point myrb;
        Point mylt;
        if (this.lt == null || this.rb == null) {
            return new Rectangle(posFrom.x, posFrom.y, facingLeft ? 1 : -1, 1);
        }
        if (facingLeft) {
            mylt = new Point(this.lt.x + posFrom.x, this.lt.y + posFrom.y);
            myrb = new Point(this.rb.x + posFrom.x, this.rb.y + posFrom.y);
        } else {
            myrb = new Point(this.lt.x * -1 + posFrom.x, this.rb.y + posFrom.y);
            mylt = new Point(this.rb.x * -1 + posFrom.x, this.lt.y + posFrom.y);
        }
        return new Rectangle(mylt.x, mylt.y, myrb.x - mylt.x, myrb.y - mylt.y);
    }

    public final void setDuration(int d) {
        this.duration = d;
    }

    public final int getEXP() {
        return this.exp;
    }

    public final void silentApplyBuff(MapleCharacter chr, long starttime) {
        MapleSummon tosummon;
        int localDuration = this.alchemistModifyVal(chr, this.duration, false);
        chr.registerEffect(this, starttime, Timer.BuffTimer.getInstance().schedule(new CancelEffectAction(chr, this, starttime), starttime + (long)localDuration - System.currentTimeMillis()));
        SummonMovementType summonMovementType = this.getSummonMovementType();
        if (summonMovementType != null && !(tosummon = new MapleSummon(chr, this, chr.getPosition(), summonMovementType)).isPuppet()) {
            chr.getCheatTracker().resetSummonAttack();
            chr.getMap().spawnSummon(tosummon);
            chr.getSummons().put(this.sourceid, tosummon);
            tosummon.addHP((short)this.x);
            if (this.isBeholder()) {
                tosummon.addHP((short)1);
            }
        }
    }

    public final void applyComboBuff(MapleCharacter applyto, short combo) {
        List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.ARAN_COMBO, Integer.valueOf(combo)));
        applyto.getClient().getSession().write((Object)MaplePacketCreator.giveBuff(this.sourceid, 99999, stat, this));
        long starttime = System.currentTimeMillis();
        applyto.registerEffect(this, starttime, null);
    }

    public final void applyEnergyBuff(MapleCharacter applyto, boolean infinity) {
        List<Pair<MapleBuffStat, Integer>> stat = this.statups;
        long starttime = System.currentTimeMillis();
        if (infinity) {
            applyto.getClient().getSession().write((Object)MaplePacketCreator.giveEnergyChargeTest(0, this.duration / 1000));
            applyto.registerEffect(this, starttime, null);
        } else {
            applyto.cancelEffect(this, true, -1L);
            applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveEnergyChargeTest(applyto.getId(), 10000, this.duration / 1000), false);
            CancelEffectAction cancelAction = new CancelEffectAction(applyto, this, starttime);
            ScheduledFuture<?> schedule = Timer.BuffTimer.getInstance().schedule(cancelAction, starttime + (long)this.duration - System.currentTimeMillis());
            this.statups = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.ENERGY_CHARGE, 10000));
            applyto.registerEffect(this, starttime, schedule);
            this.statups = stat;
        }
    }

    private final void applyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, boolean primary, int newDuration) {
        int localDuration = newDuration;
        if (primary) {
            localDuration = this.alchemistModifyVal(applyfrom, localDuration, false);
            applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.showBuffeffect(applyto.getId(), this.sourceid, 1), false);
        }
        List<Pair<MapleBuffStat, Integer>> localstatups = this.statups;
        boolean normal = true;
        switch (this.sourceid) {
            case 4321000: 
            case 5001005: 
            case 5121009: 
            case 15001003: 
            case 15111005: {
                applyto.getClient().getSession().write((Object)MaplePacketCreator.givePirate(this.statups, localDuration / 1000, this.sourceid));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignPirate(this.statups, localDuration / 1000, applyto.getId(), this.sourceid), false);
                normal = false;
                break;
            }
            case 5211006: 
            case 5220011: 
            case 22151002: {
                if (applyto.getLinkMid() <= 0) {
                    return;
                }
                applyto.getClient().getSession().write((Object)MaplePacketCreator.cancelHoming());
                applyto.getClient().getSession().write((Object)MaplePacketCreator.giveHoming(this.sourceid, applyto.getLinkMid()));
                normal = false;
                break;
            }
            case 4001003: 
            case 0x421211: 
            case 0xC7E7CE: 
            case 14001003: {
                List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DARKSIGHT, 0));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 32001003: 
            case 32120000: {
                List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DARK_AURA, 1));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
                applyto.cancelEffectFromBuffStat(MapleBuffStat.BLUE_AURA);
                applyto.cancelEffectFromBuffStat(MapleBuffStat.YELLOW_AURA);
                break;
            }
            case 32101002: 
            case 32110000: {
                List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.BLUE_AURA, 1));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
                applyto.cancelEffectFromBuffStat(MapleBuffStat.YELLOW_AURA);
                applyto.cancelEffectFromBuffStat(MapleBuffStat.DARK_AURA);
                break;
            }
            case 32101003: 
            case 32120001: {
                List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.YELLOW_AURA, 1));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
                applyto.cancelEffectFromBuffStat(MapleBuffStat.BLUE_AURA);
                applyto.cancelEffectFromBuffStat(MapleBuffStat.DARK_AURA);
                break;
            }
            case 35001001: 
            case 35101002: 
            case 35101009: 
            case 35111007: 
            case 35121005: 
            case 35121013: {
                List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MECH_CHANGE, 1));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 1111002: 
            case 11111001: {
                List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.COMBO, 1));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 3101004: 
            case 3201004: 
            case 13101003: {
                List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SOULARROW, 0));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 4111002: 
            case 14111000: {
                List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SHADOWPARTNER, 0));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 15111006: {
                localstatups = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SPARK, this.x));
                applyto.getClient().getSession().write((Object)MaplePacketCreator.giveBuff(this.sourceid, localDuration, localstatups, this));
                normal = false;
                break;
            }
            case 4341002: {
                localstatups = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.FINAL_CUT, this.y));
                applyto.getClient().getSession().write((Object)MaplePacketCreator.giveBuff(this.sourceid, localDuration, localstatups, this));
                normal = false;
                break;
            }
            case 4331003: {
                localstatups = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.OWL_SPIRIT, this.y));
                applyto.getClient().getSession().write((Object)MaplePacketCreator.giveBuff(this.sourceid, localDuration, localstatups, this));
                normal = false;
                break;
            }
            case 4331002: {
                List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MIRROR_IMAGE, 0));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 1121010: {
                applyto.handleOrbconsume();
                break;
            }
            default: {
                if (this.isMorph() || this.isPirateMorph()) {
                    List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MORPH, this.getMorph(applyto)));
                    applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
                    break;
                }
                if (this.isMonsterRiding()) {
                    int mountid = MapleStatEffect.parseMountInfo(applyto, this.sourceid);
                    int mountid2 = MapleStatEffect.parseMountInfo_Pure(applyto, this.sourceid);
                    if (mountid == 0 || mountid2 == 0) {
                        return;
                    }
                    List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MONSTER_RIDING, 0));
                    applyto.getClient().getSession().write((Object)MaplePacketCreator.cancelBuff(null));
                    applyto.getClient().getSession().write((Object)MaplePacketCreator.giveMount(mountid, this.sourceid, stat));
                    applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.showMonsterRiding(applyto.getId(), stat, mountid, this.sourceid), false);
                    normal = false;
                    break;
                }
                if (this.isSoaring()) {
                    localstatups = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SOARING, 1));
                    applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), localstatups, this), false);
                    applyto.getClient().getSession().write((Object)MaplePacketCreator.giveBuff(this.sourceid, localDuration, localstatups, this));
                    normal = false;
                    break;
                }
                if (this.isBerserkFury() || this.berserk2 > 0) {
                    List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.BERSERK_FURY, 1));
                    applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
                    break;
                }
                if (!this.isDivineBody()) break;
                List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DIVINE_BODY, 1));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
            }
        }
        if (!this.isMonsterRiding_()) {
            applyto.cancelEffect(this, true, -1L, localstatups);
        }
        if (normal && this.statups.size() > 0) {
            applyto.getClient().getSession().write((Object)MaplePacketCreator.giveBuff(this.skill ? this.sourceid : -this.sourceid, localDuration, this.statups, this));
        }
        long starttime = System.currentTimeMillis();
        CancelEffectAction cancelAction = new CancelEffectAction(applyto, this, starttime);
        ScheduledFuture<?> schedule = Timer.BuffTimer.getInstance().schedule(cancelAction, starttime + (long)localDuration - System.currentTimeMillis());
        applyto.registerEffect(this, starttime, schedule, localstatups);
    }

    public static final int parseMountInfo(MapleCharacter player, int skillid) {
        switch (skillid) {
            case 1004: 
            case 10001004: 
            case 20001004: 
            case 20011004: 
            case 30001004: {
                if (player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-118) != null && player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-119) != null) {
                    return player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-118).getItemId();
                }
                return MapleStatEffect.parseMountInfo_Pure(player, skillid);
            }
        }
        return GameConstants.getMountItem(skillid);
    }

    public static final int parseMountInfo_Pure(MapleCharacter player, int skillid) {
        switch (skillid) {
            case 1004: 
            case 11004: 
            case 10001004: 
            case 20001004: 
            case 20011004: 
            case 20021004: 
            case 80001000: {
                if (player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-18) != null && player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-19) != null) {
                    return player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-18).getItemId();
                }
                return 0;
            }
        }
        return GameConstants.getMountItem(skillid);
    }

    private final int calcHPChange(MapleCharacter applyfrom, boolean primary) {
        int hpchange = 0;
        if (this.hp != 0) {
            if (!this.skill) {
                hpchange = primary ? (hpchange += this.alchemistModifyVal(applyfrom, this.hp, true)) : (hpchange += this.hp);
                if (applyfrom.hasDisease(MapleDisease.ZOMBIFY)) {
                    hpchange /= 2;
                }
            } else {
                hpchange += MapleStatEffect.makeHealHP((double)this.hp / 100.0, applyfrom.getStat().getTotalMagic(), 3.0, 5.0);
                if (applyfrom.hasDisease(MapleDisease.ZOMBIFY)) {
                    hpchange = -hpchange;
                }
            }
        }
        if (this.hpR != 0.0) {
            hpchange += (int)((double)applyfrom.getStat().getCurrentMaxHp() * this.hpR) / (applyfrom.hasDisease(MapleDisease.ZOMBIFY) ? 2 : 1);
        }
        if (primary && this.hpCon != 0) {
            hpchange -= this.hpCon;
        }
        switch (this.sourceid) {
            case 4211001: {
                PlayerStats stat = applyfrom.getStat();
                int v42 = this.getY() + 100;
                int v38 = Randomizer.rand(1, 100) + 100;
                hpchange = (int)(((double)(v38 * stat.getLuk()) * 0.033 + (double)stat.getDex()) * (double)v42 * 0.002);
                hpchange += MapleStatEffect.makeHealHP((double)this.getY() / 100.0, applyfrom.getStat().getTotalLuk(), 2.3, 3.5);
            }
        }
        return hpchange;
    }

    private static final int makeHealHP(double rate, double stat, double lowerfactor, double upperfactor) {
        return (int)(Math.random() * (double)((int)(stat * upperfactor * rate) - (int)(stat * lowerfactor * rate) + 1) + (double)((int)(stat * lowerfactor * rate)));
    }

    private static final int getElementalAmp(int job) {
        switch (job) {
            case 211: 
            case 212: {
                return 2110001;
            }
            case 221: 
            case 222: {
                return 2210001;
            }
            case 1211: 
            case 1212: {
                return 12110001;
            }
            case 2215: 
            case 2216: 
            case 2217: 
            case 2218: {
                return 22150000;
            }
        }
        return -1;
    }

    private final int calcMPChange(MapleCharacter applyfrom, boolean primary) {
        int mpchange = 0;
        if (this.mp != 0) {
            mpchange = primary ? (mpchange += this.alchemistModifyVal(applyfrom, this.mp, true)) : (mpchange += this.mp);
        }
        if (this.mpR != 0.0) {
            mpchange += (int)((double)applyfrom.getStat().getCurrentMaxMp() * this.mpR);
        }
        if (primary && this.mpCon != 0) {
            ISkill amp;
            byte ampLevel;
            double mod = 1.0;
            int ElemSkillId = MapleStatEffect.getElementalAmp(applyfrom.getJob());
            if (ElemSkillId != -1 && (ampLevel = applyfrom.getSkillLevel(amp = SkillFactory.getSkill(ElemSkillId))) > 0) {
                MapleStatEffect ampStat = amp.getEffect(ampLevel);
                mod = (double)ampStat.getX() / 100.0;
            }
            Integer Concentrate = applyfrom.getBuffedSkill_X(MapleBuffStat.CONCENTRATE);
            int percent_off = applyfrom.getStat().mpconReduce + (Concentrate == null ? 0 : Concentrate);
            mpchange = applyfrom.getBuffedValue(MapleBuffStat.INFINITY) != null ? 0 : (int)((double)mpchange - (double)(this.mpCon - this.mpCon * percent_off / 100) * mod);
        }
        return mpchange;
    }

    private final int alchemistModifyVal(MapleCharacter chr, int val, boolean withX) {
        if (!this.skill) {
            int offset = chr.getStat().RecoveryUP;
            MapleStatEffect alchemistEffect = this.getAlchemistEffect(chr);
            offset = alchemistEffect != null ? (offset += withX ? alchemistEffect.getX() : alchemistEffect.getY()) : (offset += 100);
            return val * offset / 100;
        }
        return val;
    }

    private final MapleStatEffect getAlchemistEffect(MapleCharacter chr) {
        switch (chr.getJob()) {
            case 411: 
            case 412: {
                ISkill al = SkillFactory.getSkill(4110000);
                if (chr.getSkillLevel(al) <= 0) {
                    return null;
                }
                return al.getEffect(chr.getSkillLevel(al));
            }
            case 1411: 
            case 1412: {
                ISkill al = SkillFactory.getSkill(14110003);
                if (chr.getSkillLevel(al) <= 0) {
                    return null;
                }
                return al.getEffect(chr.getSkillLevel(al));
            }
        }
        if (GameConstants.isResist(chr.getJob())) {
            ISkill al = SkillFactory.getSkill(30000002);
            if (chr.getSkillLevel(al) <= 0) {
                return null;
            }
            return al.getEffect(chr.getSkillLevel(al));
        }
        return null;
    }

    public final void setSourceId(int newid) {
        this.sourceid = newid;
    }

    private final boolean isGmBuff() {
        switch (this.sourceid) {
            case 1005: 
            case 9001000: 
            case 9001001: 
            case 9001002: 
            case 9001003: 
            case 9001005: 
            case 9001008: 
            case 10001005: 
            case 20001005: 
            case 20011005: 
            case 30001005: {
                return true;
            }
        }
        return false;
    }

    private final boolean isEnergyCharge() {
        return this.skill && (this.sourceid == 5110001 || this.sourceid == 15100004);
    }

    private final boolean isMonsterBuff() {
        switch (this.sourceid) {
            case 1201006: 
            case 2101003: 
            case 2111004: 
            case 2201003: 
            case 2211004: 
            case 2311005: 
            case 4111003: 
            case 4121004: 
            case 4221004: 
            case 4321002: 
            case 12101001: 
            case 12111002: 
            case 14111001: 
            case 22121000: 
            case 22141003: 
            case 22151001: 
            case 22161002: {
                return this.skill;
            }
        }
        return false;
    }

    public final void setPartyBuff(boolean pb) {
        this.partyBuff = pb;
    }

    private final boolean isPartyBuff() {
        if (this.lt == null || this.rb == null || !this.partyBuff) {
            return this.isSoulStone();
        }
        switch (this.sourceid) {
            case 1211003: 
            case 1211004: 
            case 1211005: 
            case 1211006: 
            case 1211007: 
            case 1211008: 
            case 1221003: 
            case 1221004: 
            case 4311001: 
            case 11111007: 
            case 12101005: {
                return false;
            }
        }
        return true;
    }

    public final boolean isHeal() {
        return this.sourceid == 2301002 || this.sourceid == 9101000;
    }

    public final boolean isResurrection() {
        return this.sourceid == 9001005 || this.sourceid == 2321006;
    }

    public final boolean isTimeLeap() {
        return this.sourceid == 5121010;
    }

    public final short getHp() {
        return this.hp;
    }

    public final short getMp() {
        return this.mp;
    }

    public final byte getMastery() {
        return this.mastery;
    }

    public final short getWatk() {
        return this.watk;
    }

    public final short getMatk() {
        return this.matk;
    }

    public final short getWdef() {
        return this.wdef;
    }

    public final short getMdef() {
        return this.mdef;
    }

    public final short getAcc() {
        return this.acc;
    }

    public final short getAvoid() {
        return this.avoid;
    }

    public final short getHands() {
        return this.hands;
    }

    public final short getSpeed() {
        return this.speed;
    }

    public final short getJump() {
        return this.jump;
    }

    public final int getDuration() {
        return this.duration;
    }

    public final boolean isOverTime() {
        return this.overTime;
    }

    public final List<Pair<MapleBuffStat, Integer>> getStatups() {
        return this.statups;
    }

    public final boolean sameSource(MapleStatEffect effect) {
        return effect != null && this.sourceid == effect.sourceid && this.skill == effect.skill;
    }

    public final int getX() {
        return this.x;
    }

    public final int getY() {
        return this.y;
    }

    public final int getZ() {
        return this.z;
    }

    public final short getDamage() {
        return this.damage;
    }

    public final byte getAttackCount() {
        return this.attackCount;
    }

    public final byte getBulletCount() {
        return this.bulletCount;
    }

    public final int getBulletConsume() {
        return this.bulletConsume;
    }

    public final byte getMobCount() {
        return this.mobCount;
    }

    public final int getMoneyCon() {
        return this.moneyCon;
    }

    public final int getCooldown() {
        return this.cooldown;
    }

    public final Map<MonsterStatus, Integer> getMonsterStati() {
        return this.monsterStatus;
    }

    public final int getBerserk() {
        return this.berserk;
    }

    public final boolean isHide() {
        return this.skill && this.sourceid == 9001004;
    }

    public final boolean isDragonBlood() {
        return this.skill && this.sourceid == 1311008;
    }

    public final boolean isBerserk() {
        return this.skill && this.sourceid == 1320006;
    }

    public final boolean isBeholder() {
        return this.skill && this.sourceid == 1321007;
    }

    public final boolean isMPRecovery() {
        return this.skill && this.sourceid == 5101005;
    }

    public final boolean isMonsterRiding_() {
        return this.skill && (this.sourceid == 1004 || this.sourceid == 10001004 || this.sourceid == 20001004 || this.sourceid == 20011004 || this.sourceid == 30001004);
    }

    public final boolean isMonsterRiding() {
        return this.skill && (this.isMonsterRiding_() || GameConstants.getMountItem(this.sourceid) != 0);
    }

    public final boolean isMagicDoor() {
        return this.skill && (this.sourceid == 2311002 || this.sourceid == 8001 || this.sourceid == 10008001 || this.sourceid == 20008001 || this.sourceid == 20018001 || this.sourceid == 30008001);
    }

    public final boolean isMesoGuard() {
        return this.skill && this.sourceid == 4211005;
    }

    public final boolean isCharge() {
        switch (this.sourceid) {
            case 1211003: 
            case 1211008: 
            case 11111007: 
            case 12101005: 
            case 15101006: 
            case 21111005: {
                return this.skill;
            }
        }
        return false;
    }

    public final boolean isPoison() {
        switch (this.sourceid) {
            case 2101005: 
            case 2111003: 
            case 2111006: 
            case 2121003: 
            case 2221003: 
            case 3111003: 
            case 12111005: 
            case 22161002: {
                return this.skill;
            }
        }
        return false;
    }

    private final boolean isMist() {
        return this.skill && (this.sourceid == 2111003 || this.sourceid == 4221006 || this.sourceid == 12111005 || this.sourceid == 14111006 || this.sourceid == 22161003);
    }

    private final boolean isSpiritClaw() {
        return this.skill && this.sourceid == 4121006;
    }

    private final boolean isDispel() {
        return this.skill && (this.sourceid == 2311001 || this.sourceid == 9001000);
    }

    private final boolean isHeroWill() {
        switch (this.sourceid) {
            case 1121011: 
            case 1221012: 
            case 1321010: 
            case 2121008: 
            case 2221008: 
            case 2321009: 
            case 3121009: 
            case 3221008: 
            case 4121009: 
            case 4221008: 
            case 4341008: 
            case 5121008: 
            case 5221010: 
            case 21121008: 
            case 22171004: 
            case 32121008: 
            case 33121008: 
            case 35121008: {
                return this.skill;
            }
        }
        return false;
    }

    public final boolean isAranCombo() {
        return this.sourceid == 21000000;
    }

    public final boolean isCombo() {
        switch (this.sourceid) {
            case 1111002: 
            case 11111001: {
                return this.skill;
            }
        }
        return false;
    }

    public final boolean isPirateMorph() {
        switch (this.sourceid) {
            case 5111005: 
            case 5121003: 
            case 15111002: {
                return this.skill;
            }
        }
        return false;
    }

    public final boolean isMorph() {
        return this.morphId > 0;
    }

    public final int getMorph() {
        switch (this.sourceid) {
            case 5111005: 
            case 15111002: {
                return 1000;
            }
            case 5121003: {
                return 1001;
            }
            case 5101007: {
                return 1002;
            }
            case 13111005: {
                return 1003;
            }
        }
        return this.morphId;
    }

    public final boolean isDivineBody() {
        switch (this.sourceid) {
            case 1010: 
            case 10001010: 
            case 20001010: 
            case 20011010: 
            case 30001010: {
                return this.skill;
            }
        }
        return false;
    }

    public final boolean isBerserkFury() {
        switch (this.sourceid) {
            case 1011: 
            case 10001011: 
            case 20001011: 
            case 20011011: 
            case 30001011: {
                return this.skill;
            }
        }
        return false;
    }

    public final int getMorph(MapleCharacter chr) {
        int morph = this.getMorph();
        switch (morph) {
            case 1000: 
            case 1001: 
            case 1003: {
                return morph + (chr.getGender() == 1 ? 100 : 0);
            }
        }
        return morph;
    }

    public final byte getLevel() {
        return this.level;
    }

    public final SummonMovementType getSummonMovementType() {
        if (!this.skill) {
            return null;
        }
        switch (this.sourceid) {
            case 3111002: 
            case 3211002: 
            case 4341006: 
            case 5211001: 
            case 5220002: 
            case 13111004: 
            case 33111003: 
            case 35111002: 
            case 35111004: 
            case 35111005: 
            case 35121009: 
            case 35121011: {
                return SummonMovementType.STATIONARY;
            }
            case 2311006: 
            case 3111005: 
            case 3121006: 
            case 3211005: 
            case 3221005: 
            case 33111005: {
                return SummonMovementType.CIRCLE_FOLLOW;
            }
            case 5211002: {
                return SummonMovementType.CIRCLE_STATIONARY;
            }
            case 32111006: {
                return SummonMovementType.WALK_STATIONARY;
            }
            case 1321007: 
            case 2121005: 
            case 2221005: 
            case 2321003: 
            case 11001004: 
            case 12001004: 
            case 12111004: 
            case 13001004: 
            case 14001005: 
            case 15001004: 
            case 35111001: 
            case 35111009: 
            case 35111010: {
                return SummonMovementType.FOLLOW;
            }
        }
        return null;
    }

    public final boolean isSkill() {
        return this.skill;
    }

    public final int getSourceId() {
        return this.sourceid;
    }

    public final boolean isSoaring() {
        switch (this.sourceid) {
            case 1026: 
            case 10001026: 
            case 20001026: 
            case 20011026: 
            case 30001026: {
                return this.skill;
            }
        }
        return false;
    }

    public final boolean isFinalAttack() {
        switch (this.sourceid) {
            case 11101002: 
            case 13101002: 
            case 21120002: {
                return this.skill;
            }
        }
        return false;
    }

    public final boolean makeChanceResult() {
        return this.prop == 100 || Randomizer.nextInt(99) < this.prop;
    }

    public final short getProb() {
        return this.prop;
    }

    public static class CancelEffectAction
    implements Runnable {
        private final MapleStatEffect effect;
        private final WeakReference<MapleCharacter> target;
        private final long startTime;

        public CancelEffectAction(MapleCharacter target, MapleStatEffect effect, long startTime) {
            this.effect = effect;
            this.target = new WeakReference<MapleCharacter>(target);
            this.startTime = startTime;
        }

        @Override
        public void run() {
            MapleCharacter realTarget = (MapleCharacter)this.target.get();
            if (realTarget != null && !realTarget.isClone()) {
                realTarget.cancelEffect(this.effect, false, this.startTime);
            }
        }
    }
}

