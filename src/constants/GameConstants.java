/*
 * Decompiled with CFR 0.152.
 */
package constants;

import client.inventory.MapleInventoryType;
import client.inventory.MapleWeaponType;
import client.status.MonsterStatus;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import server.Randomizer;
import server.maps.MapleMapObjectType;
import tools.FileoutputUtil;

public class GameConstants {
    public static final List<MapleMapObjectType> rangedMapobjectTypes = Collections.unmodifiableList(Arrays.asList(MapleMapObjectType.ITEM, MapleMapObjectType.MONSTER, MapleMapObjectType.DOOR, MapleMapObjectType.REACTOR, MapleMapObjectType.SUMMON, MapleMapObjectType.NPC, MapleMapObjectType.MIST));
    private static final int[] exp = new int[]{0, 15, 34, 57, 92, 135, 372, 560, 840, 1242, 1716, 2360, 3216, 4200, 5460, 7050, 8840, 11040, 13716, 16680, 20216, 24402, 28980, 34320, 40512, 47216, 54900, 63666, 73080, 83720, 95700, 108480, 122760, 138666, 155540, 174216, 194832, 216600, 240500, 266682, 294216, 324240, 356916, 391160, 428280, 468450, 510420, 555680, 604416, 655200, 709716, 748608, 789631, 832902, 878545, 926689, 977471, 1031036, 1087536, 1147132, 1209994, 1276301, 1346242, 1420016, 1497832, 1579913, 1666492, 1757815, 1854143, 1955750, 2062925, 2175973, 2295216, 2410993, 2553663, 2693603, 2841212, 2996910, 3161140, 3334370, 0x35AAA5, 3709829, 3913127, 4127566, 4353756, 4592341, 4844001, 5109452, 5389449, 5684790, 5996316, 6324914, 6671519, 7037118, 7422752, 7829518, 8258575, 8711144, 9188514, 9692044, 10223168, 10783397, 11374327, 11997640, 12655110, 13348610, 14080113, 14851703, 15665576, 16524049, 17429566, 18384706, 19392187, 20454878, 21575805, 22758159, 24005306, 25320796, 26708375, 28171993, 29715818, 31344244, 33061908, 34873700, 36784778, 38800583, 40926854, 43169645, 45535341, 48030677, 50662758, 53439077, 56367538, 59456479, 62714694, 66151459, 69776558, 73600313, 77633610, 81887931, 86375389, 91108760, 96101520, 101367883, 106922842, 112782213, 118962678, 125481832, 132358236, 139611467, 147262175, 155332142, 163844343, 172823012, 182293713, 192283408, 202820538, 213935103, 225658746, 238024845, 251068606, 264827165, 279339693, 294647508, 310794191, 327825712, 345790561, 364739883, 384727628, 405810702, 428049128, 451506220, 476248760, 502347192, 529875818, 558913012, 589541445, 621848316, 655925603, 691870326, 729784819, 769777027, 811960808, 856456260, 903390063, 952895838, 1005114529, 1060194805, 1118293480, 1179575962, 1244216724, 1312399800, 1384319309, 1460180007, 1540197871, 1624600714, 1713628833, 1807535693, 1906588648, 2011069705, 2121276324};
    private static final int[] closeness = new int[]{0, 1, 3, 6, 14, 31, 60, 108, 181, 287, 434, 632, 891, 1224, 1642, 2161, 2793, 3557, 4467, 5542, 6801, 8263, 9950, 11882, 14084, 16578, 19391, 22547, 26074, 30000};
    private static final int[] mountexp = new int[]{0, 6, 25, 50, 105, 134, 196, 254, 263, 315, 367, 430, 543, 587, 679, 725, 897, 1146, 1394, 1701, 2247, 2543, 2898, 3156, 3313, 3584, 3923, 4150, 4305, 4550};
    public static final int[] itemBlock = new int[]{2340000, 2049100, 4001129, 2040037, 2040006, 2040007, 2040303, 2040403, 2040506, 2040507, 2040603, 2040709, 2040710, 2040711, 2040806, 2040903, 2041024, 2041025, 2043003, 2043103, 2043203, 2043303, 2043703, 2043803, 2044003, 2044103, 2044203, 2044303, 0x1F31F3, 2044503, 2044603, 2044908, 2044815, 2044019, 0x1F331F, 1004001, 4007008, 1004002, 5152053, 5150040};
    public static final int[] cashBlock = new int[]{0};
    public static final int OMOK_SCORE = 122200;
    public static final int MATCH_SCORE = 122210;
    public static final int[] blockedSkills = new int[]{4341003};
    public static final String MASTER = "%&HYGEomgLOL";
    public static final String[] RESERVED = new String[]{"Rental"};
    public static final int[] goldrewards = new int[]{2340000, 1, 2070018, 1, 1402037, 1, 2290096, 1, 2290049, 1, 2290041, 1, 2290047, 1, 2290095, 1, 2290017, 1, 2290075, 1, 2290085, 1, 2290116, 1, 1302059, 3, 2049100, 1, 2340000, 1, 1092049, 1, 1102041, 1, 1432018, 3, 1022047, 3, 3010051, 1, 3010020, 1, 2040914, 1, 1432011, 3, 1442020, 3, 1382035, 3, 1372010, 3, 1332027, 3, 1302056, 3, 1402005, 3, 1472053, 3, 1462018, 3, 1452017, 3, 1422013, 3, 1322029, 3, 1412010, 3, 1472051, 1, 1482013, 1, 1492013, 1, 1382050, 1, 1382045, 1, 1382047, 1, 1382048, 1, 1382046, 1, 1332032, 4, 1482025, 3, 4001011, 4, 4001010, 4, 4001009, 4, 2030008, 5, 1442018, 3, 2040900, 4, 2000005, 10, 2000004, 10, 4280000, 4};
    public static final int[] silverrewards = new int[]{3010041, 1, 1002452, 3, 1002455, 3, 2290084, 1, 2290048, 1, 2290040, 1, 2290046, 1, 2290074, 1, 2290064, 1, 2290094, 1, 2290022, 1, 2290056, 1, 2290066, 1, 2290020, 1, 1102082, 1, 1302049, 1, 2340000, 1, 1102041, 1, 1452019, 2, 4001116, 3, 4001012, 3, 1022060, 2, 1432011, 3, 1442020, 3, 1382035, 3, 1372010, 3, 1332027, 3, 1302056, 3, 1402005, 3, 1472053, 3, 1462018, 3, 1452017, 3, 1422013, 3, 1322029, 3, 1412010, 3, 1002587, 3, 1402044, 1, 2101013, 4, 1442046, 1, 1422031, 1, 1332054, 3, 1012056, 3, 1022047, 3, 3012002, 1, 1442012, 3, 1442018, 3, 1432010, 3, 1432036, 1, 2000005, 10, 2000004, 10, 4280001, 4};
    public static int[] eventCommonReward = new int[]{0, 40, 1, 10, 4031019, 5, 4280000, 3, 4280001, 4, 5490000, 3, 5490001, 4};
    public static int[] eventUncommonReward = new int[]{2, 4, 3, 4, 5160000, 5, 5160001, 5, 5160002, 5, 5160003, 5, 5160004, 5, 5160005, 5, 5160006, 5, 5160007, 5, 5160008, 5, 5160009, 5, 5160010, 5, 5160011, 5, 5160012, 5, 5160013, 5, 5240017, 5, 5240000, 5, 4080000, 5, 4080001, 5, 4080002, 5, 4080003, 5, 4080004, 5, 4080005, 5, 4080006, 5, 4080007, 5, 4080008, 5, 4080009, 5, 4080010, 5, 4080011, 5, 4080100, 5, 4031019, 5, 5121003, 5, 5150000, 5, 5150001, 5, 5150002, 1, 5150003, 1, 5150004, 1, 5150005, 2, 5150006, 2, 5150007, 2, 5150008, 2, 5150009, 14, 2022459, 5, 2022460, 5, 2022461, 5, 2022462, 5, 2022463, 5, 2450000, 2, 5152000, 5, 5152001, 5};
    public static int[] eventRareReward = new int[]{4031019, 5, 2049100, 5, 2049401, 10, 2049301, 20, 2049400, 3, 2340000, 1, 3010130, 5, 3010131, 5, 3010132, 5, 3010133, 5, 3010136, 5, 3010116, 5, 3010117, 5, 3010118, 5, 1112405, 1, 1112413, 1, 1112414, 1, 2040211, 1, 2040212, 1, 2049000, 2, 2049001, 2, 2049002, 2, 2049003, 2, 1012058, 2, 1012059, 2, 1012060, 2, 1012061, 2};
    public static int[] eventSuperReward = new int[]{4031019, 5, 2022121, 10, 4031307, 50, 3010127, 10, 3010128, 10, 3010137, 10, 2049300, 10, 1012139, 10, 1012140, 10, 1012141, 10};
    public static int[] fishingReward = new int[]{0, 90, 1, 80, 1302021, 3, 1072238, 1, 1072239, 1, 2049100, 1, 2049301, 3, 2049401, 3, 2000004, 5, 4031627, 5, 4031628, 3, 4031630, 5, 4031631, 3, 4031632, 1, 2000005, 5, 4031633, 5, 4031634, 5, 4031635, 5, 4031636, 5, 4031637, 5, 2000004, 5, 4031638, 5, 4031639, 3, 4031640, 3, 4031641, 3, 4031642, 3, 2000005, 5, 4031643, 3, 4031644, 3, 4031645, 3, 4031646, 3, 4031647, 3, 2000004, 5, 4031648, 3, 4031629, 1, 3010075, 2, 3010196, 2, 2000005, 5, 2049300, 1, 2049400, 1, 1112680, 5, 2101120, 2, 2000004, 5};
    public static int[] Equipments_Bonus = new int[]{0x111EE1};
    public static int[] blockedMaps = new int[]{109050000, 280030000, 240060200, 280090000, 280030001, 240060201, 950101100, 950101010};
    public static final int[] normalDrops = new int[]{4001009, 4001010, 4001011, 4001012, 4001013, 4001014, 4001021, 4001038, 4001039, 4001040, 4001041, 4001042, 4001043, 4001038, 4001039, 4001040, 4001041, 4001042, 4001043, 4001038, 4001039, 4001040, 4001041, 4001042, 4001043, 4000164, 2000000, 2000003, 2000004, 2000005, 4000019, 4000000, 4000016, 4000006, 2100121, 4000029, 4000064, 5110000, 4000306, 4032181, 4006001, 4006000, 2050004, 3994102, 3994103, 3994104, 3994105, 2430007, 4000164, 2000000, 2000003, 2000004, 2000005, 4000019, 4000000, 4000016, 4000006, 2100121, 4000029, 4000064, 5110000, 4000306, 4032181, 4006001, 4006000, 2050004, 3994102, 3994103, 3994104, 3994105, 2430007, 4000164, 2000000, 2000003, 2000004, 2000005, 4000019, 4000000, 4000016, 4000006, 2100121, 4000029, 4000064, 5110000, 4000306, 4032181, 4006001, 4006000, 2050004, 3994102, 3994103, 3994104, 3994105, 2430007};
    public static final int[] rareDrops = new int[]{2049100, 2049301, 2049401, 2022326, 2022193, 2049000, 2049001, 2049002};
    public static final int[] superDrops = new int[]{2040804, 2049400, 2049100};
    public static int[] owlItems = new int[]{1082002, 2070005, 2070006, 1022047, 1102041, 2044705, 2340000, 2040017, 1092030, 2040804};

    public static int getExpNeededForLevel(int level) {
        if (level < 0 || level >= exp.length) {
            return Integer.MAX_VALUE;
        }
        return exp[level];
    }

    public static int getClosenessNeededForLevel(int level) {
        return closeness[level - 1];
    }

    public static int getMountExpNeededForLevel(int level) {
        return mountexp[level - 1];
    }

    public static int getBookLevel(int level) {
        return 5 * level * (level + 1);
    }

    public static int getTimelessRequiredEXP(int level) {
        return 70 + level * 10;
    }

    public static int getReverseRequiredEXP(int level) {
        return 60 + level * 5;
    }

    public static int maxViewRangeSq() {
        return 800000;
    }

    public static boolean isJobFamily(int baseJob, int currentJob) {
        return currentJob >= baseJob && currentJob / 100 == baseJob / 100;
    }

    public static boolean isKOC(int job) {
        return job >= 1000 && job < 2000;
    }

    public static boolean isEvan(int job) {
        return job == 2001 || job >= 2200 && job <= 2218;
    }

    public static boolean isAran(int job) {
        return job >= 2000 && job <= 2112 && job != 2001;
    }

    public static boolean isResist(int job) {
        return job >= 3000 && job <= 3512;
    }

    public static boolean isAdventurer(int job) {
        return job >= 0 && job < 1000;
    }

    public static boolean isRecoveryIncSkill(int id) {
        switch (id) {
            case 1110000: 
            case 1210000: 
            case 2000000: 
            case 4100002: 
            case 4200001: 
            case 11110000: {
                return true;
            }
        }
        return false;
    }

    public static boolean isLinkedAranSkill(int id) {
        return GameConstants.getLinkedAranSkill(id) != id;
    }

    public static int getLinkedAranSkill(int id) {
        switch (id) {
            case 21110007: 
            case 21110008: {
                return 21110002;
            }
            case 21120009: 
            case 21120010: {
                return 21120002;
            }
            case 4321001: {
                return 4321000;
            }
            case 33101006: 
            case 33101007: {
                return 33101005;
            }
            case 33101008: {
                return 33101004;
            }
            case 35101009: 
            case 35101010: {
                return 35100008;
            }
            case 35111009: 
            case 35111010: {
                return 35111001;
            }
        }
        return id;
    }

    public static int getBOF_ForJob(int job) {
        if (GameConstants.isAdventurer(job)) {
            return 12;
        }
        if (GameConstants.isKOC(job)) {
            return 10000012;
        }
        if (GameConstants.isResist(job)) {
            return 30000012;
        }
        if (GameConstants.isEvan(job)) {
            return 20010012;
        }
        return 20000012;
    }

    public static boolean isElementAmp_Skill(int skill) {
        switch (skill) {
            case 2110001: 
            case 2210001: 
            case 12110001: 
            case 22150000: {
                return true;
            }
        }
        return false;
    }

    public static int getMPEaterForJob(int job) {
        switch (job) {
            case 210: 
            case 211: 
            case 212: {
                return 0x200B20;
            }
            case 220: 
            case 221: 
            case 222: {
                return 2200000;
            }
            case 230: 
            case 231: 
            case 232: {
                return 2300000;
            }
        }
        return 0x200B20;
    }

    public static int getJobShortValue(int job) {
        if (job >= 1000) {
            job -= job / 1000 * 1000;
        }
        if ((job /= 100) == 4) {
            job *= 2;
        } else if (job == 3) {
            ++job;
        } else if (job == 5) {
            job += 11;
        }
        return job;
    }

    public static boolean isPyramidSkill(int skill) {
        switch (skill) {
            case 1020: 
            case 10001020: 
            case 20001020: 
            case 20011020: 
            case 30001020: {
                return true;
            }
        }
        return false;
    }

    public static boolean isMulungSkill(int skill) {
        switch (skill) {
            case 1009: 
            case 1010: 
            case 1011: 
            case 10001009: 
            case 10001010: 
            case 10001011: 
            case 20001009: 
            case 20001010: 
            case 20001011: 
            case 20011009: 
            case 20011010: 
            case 20011011: 
            case 30001009: 
            case 30001010: 
            case 30001011: {
                return true;
            }
        }
        return false;
    }

    public static boolean isThrowingStar(int itemId) {
        return itemId / 10000 == 207;
    }

    public static boolean isBullet(int itemId) {
        return itemId / 10000 == 233;
    }

    public static boolean isRechargable(int itemId) {
        return GameConstants.isThrowingStar(itemId) || GameConstants.isBullet(itemId);
    }

    public static boolean isOverall(int itemId) {
        return itemId / 10000 == 105;
    }

    public static boolean isPet(int itemId) {
        return itemId / 10000 == 500;
    }

    public static boolean isArrowForCrossBow(int itemId) {
        return itemId >= 2061000 && itemId < 2062000;
    }

    public static boolean isArrowForBow(int itemId) {
        return itemId >= 2060000 && itemId < 2061000;
    }

    public static boolean isMagicWeapon(int itemId) {
        int s = itemId / 10000;
        return s == 137 || s == 138;
    }

    public static boolean isWeapon(int itemId) {
        return itemId >= 1300000 && itemId < 1500000;
    }

    public static MapleInventoryType getInventoryType(int itemId) {
        byte type = (byte)(itemId / 1000000);
        if (type < 1 || type > 5) {
            return MapleInventoryType.UNDEFINED;
        }
        return MapleInventoryType.getByType(type);
    }

    public static MapleWeaponType getWeaponType(int itemId) {
        int cat = itemId / 10000;
        switch (cat %= 100) {
            case 30: {
                return MapleWeaponType.SWORD1H;
            }
            case 31: {
                return MapleWeaponType.AXE1H;
            }
            case 32: {
                return MapleWeaponType.BLUNT1H;
            }
            case 33: {
                return MapleWeaponType.DAGGER;
            }
            case 34: {
                return MapleWeaponType.KATARA;
            }
            case 37: {
                return MapleWeaponType.WAND;
            }
            case 38: {
                return MapleWeaponType.STAFF;
            }
            case 40: {
                return MapleWeaponType.SWORD2H;
            }
            case 41: {
                return MapleWeaponType.AXE2H;
            }
            case 42: {
                return MapleWeaponType.BLUNT2H;
            }
            case 43: {
                return MapleWeaponType.SPEAR;
            }
            case 44: {
                return MapleWeaponType.POLE_ARM;
            }
            case 45: {
                return MapleWeaponType.BOW;
            }
            case 46: {
                return MapleWeaponType.CROSSBOW;
            }
            case 47: {
                return MapleWeaponType.CLAW;
            }
            case 48: {
                return MapleWeaponType.KNUCKLE;
            }
            case 49: {
                return MapleWeaponType.GUN;
            }
        }
        return MapleWeaponType.NOT_A_WEAPON;
    }

    public static boolean isShield(int itemId) {
        int cat = itemId / 10000;
        return (cat %= 100) == 9;
    }

    public static boolean isEquip(int itemId) {
        return itemId / 1000000 == 1;
    }

    public static boolean isCleanSlate(int itemId) {
        return itemId / 100 == 20490;
    }

    public static boolean isAccessoryScroll(int itemId) {
        return itemId / 100 == 20492;
    }

    public static boolean isChaosScroll(int itemId) {
        if (itemId >= 2049105 && itemId <= 2049110) {
            return false;
        }
        return itemId / 100 == 20491;
    }

    public static int getChaosNumber(int itemId) {
        return itemId == 2049116 ? 10 : 5;
    }

    public static boolean isEquipScroll(int scrollId) {
        return scrollId / 100 == 20493;
    }

    public static boolean isPotentialScroll(int scrollId) {
        return scrollId / 100 == 20494;
    }

    public static boolean isSpecialScroll(int scrollId) {
        switch (scrollId) {
            case 2040727: 
            case 2041058: {
                return true;
            }
        }
        return false;
    }

    public static boolean isTwoHanded(int itemId) {
        switch (GameConstants.getWeaponType(itemId)) {
            case AXE2H: 
            case GUN: 
            case KNUCKLE: 
            case BLUNT2H: 
            case BOW: 
            case CLAW: 
            case CROSSBOW: 
            case POLE_ARM: 
            case SPEAR: 
            case SWORD2H: {
                return true;
            }
        }
        return false;
    }

    public static boolean isTownScroll(int id) {
        return id >= 2030000 && id < 2040000;
    }

    public static boolean isUpgradeScroll(int id) {
        return id >= 2040000 && id < 2050000;
    }

    public static boolean isGun(int id) {
        return id >= 1492000 && id < 1500000;
    }

    public static boolean isUse(int id) {
        return id >= 2000000 && id <= 2490000;
    }

    public static boolean isSummonSack(int id) {
        return id / 10000 == 210;
    }

    public static boolean isMonsterCard(int id) {
        return id / 10000 == 238;
    }

    public static boolean isSpecialCard(int id) {
        return id / 1000 >= 2388;
    }

    public static int getCardShortId(int id) {
        return id % 10000;
    }

    public static boolean isSpear(int itemId) {
        return GameConstants.getWeaponType(itemId) == MapleWeaponType.SPEAR || GameConstants.getWeaponType(itemId) == MapleWeaponType.POLE_ARM;
    }

    public static boolean isGem(int id) {
        return id >= 4250000 && id <= 4251402;
    }

    public static boolean isOtherGem(int id) {
        switch (id) {
            case 1032062: 
            case 1142156: 
            case 1142157: 
            case 2040727: 
            case 2041058: 
            case 4001174: 
            case 4001175: 
            case 4001176: 
            case 4001177: 
            case 4001178: 
            case 4001179: 
            case 4001180: 
            case 4001181: 
            case 4001182: 
            case 4001183: 
            case 4001184: 
            case 4001185: 
            case 4001186: 
            case 4031980: 
            case 4032312: 
            case 4032334: {
                return true;
            }
        }
        return false;
    }

    public static boolean isCustomQuest(int id) {
        return id > 99999;
    }

    public static int getTaxAmount(int meso) {
        if (meso >= 100000000) {
            return (int)Math.round(0.06 * (double)meso);
        }
        if (meso >= 25000000) {
            return (int)Math.round(0.05 * (double)meso);
        }
        if (meso >= 10000000) {
            return (int)Math.round(0.04 * (double)meso);
        }
        if (meso >= 5000000) {
            return (int)Math.round(0.03 * (double)meso);
        }
        if (meso >= 1000000) {
            return (int)Math.round(0.018 * (double)meso);
        }
        if (meso >= 100000) {
            return (int)Math.round(0.008 * (double)meso);
        }
        return 0;
    }

    public static int EntrustedStoreTax(int meso) {
        if (meso >= 100000000) {
            return (int)Math.round(0.03 * (double)meso);
        }
        if (meso >= 25000000) {
            return (int)Math.round(0.025 * (double)meso);
        }
        if (meso >= 10000000) {
            return (int)Math.round(0.02 * (double)meso);
        }
        if (meso >= 5000000) {
            return (int)Math.round(0.015 * (double)meso);
        }
        if (meso >= 1000000) {
            return (int)Math.round(0.009 * (double)meso);
        }
        if (meso >= 100000) {
            return (int)Math.round(0.004 * (double)meso);
        }
        return 0;
    }

    public static short getSummonAttackDelay(int id) {
        switch (id) {
            case 2121005: 
            case 2221005: 
            case 2311006: 
            case 2321003: 
            case 3111005: 
            case 3121006: 
            case 3211005: 
            case 3221005: 
            case 11001004: 
            case 12001004: 
            case 13001004: 
            case 14001005: 
            case 15001004: {
                return 3030;
            }
            case 5211001: 
            case 5211002: 
            case 5220002: {
                return 1530;
            }
            case 1321007: 
            case 3111002: 
            case 3211002: 
            case 4341006: 
            case 35111002: 
            case 35111011: 
            case 35121009: 
            case 35121010: {
                return 0;
            }
        }
        return 0;
    }

    public static short getAttackDelay(int id) {
        switch (id) {
            case 4321001: {
                return 40;
            }
            case 3121004: 
            case 4221001: 
            case 5201006: 
            case 5221004: 
            case 13111002: 
            case 33121009: {
                return 120;
            }
            case 13101005: {
                return 360;
            }
            case 2301002: 
            case 5001003: {
                return 390;
            }
            case 0x111AEE: 
            case 1221007: 
            case 1321003: 
            case 5001001: 
            case 15001001: {
                return 450;
            }
            case 4201005: 
            case 5211004: 
            case 5211005: {
                return 480;
            }
            case 0: 
            case 1001004: 
            case 1001005: 
            case 1311005: 
            case 5111002: 
            case 11001002: 
            case 11001003: 
            case 15101005: {
                return 570;
            }
            case 311004: 
            case 1121008: 
            case 1211002: 
            case 1221009: 
            case 1311003: 
            case 1311004: 
            case 2101005: 
            case 2121003: 
            case 2121006: 
            case 2221003: 
            case 3101005: 
            case 3111003: 
            case 3111006: 
            case 3201005: 
            case 3211003: 
            case 3211004: 
            case 3211006: 
            case 3221001: 
            case 4001334: 
            case 4001344: 
            case 4101005: 
            case 4111004: 
            case 4111005: 
            case 4121007: 
            case 4201004: 
            case 4211004: 
            case 5101004: 
            case 5201001: 
            case 5221007: 
            case 11111004: 
            case 12101002: 
            case 13111000: 
            case 14001004: 
            case 14111002: 
            case 14111005: 
            case 15101003: {
                return 600;
            }
            case 1311001: 
            case 1311002: 
            case 2221006: 
            case 4221007: 
            case 5001002: 
            case 5201004: 
            case 5211000: 
            case 15001002: {
                return 660;
            }
            case 2001004: 
            case 2001005: 
            case 2121001: 
            case 2201004: 
            case 2201005: 
            case 2211002: 
            case 2221001: 
            case 2301005: 
            case 2321001: 
            case 2321007: 
            case 4121008: 
            case 4211006: 
            case 5101002: 
            case 5121005: 
            case 5211006: 
            case 5221008: 
            case 11101004: 
            case 12001003: 
            case 12111006: {
                return 750;
            }
            case 2111006: 
            case 2211006: 
            case 15111007: {
                return 810;
            }
            case 2111002: 
            case 4211002: 
            case 5101003: 
            case 13111006: {
                return 900;
            }
            case 2311004: 
            case 5121003: {
                return 930;
            }
            case 13111007: {
                return 960;
            }
            case 4121003: 
            case 4221003: 
            case 14101006: {
                return 1020;
            }
            case 12101006: {
                return 1050;
            }
            case 5121001: {
                return 1060;
            }
            case 1311006: 
            case 2211003: {
                return 1140;
            }
            case 11111006: {
                return 1230;
            }
            case 12111005: {
                return 1260;
            }
            case 2111003: {
                return 1320;
            }
            case 5111006: 
            case 15111003: {
                return 1500;
            }
            case 5121007: 
            case 15111004: {
                return 1830;
            }
            case 5121004: 
            case 5221003: {
                return 2160;
            }
            case 2321008: {
                return 2700;
            }
            case 2121007: 
            case 2221007: 
            case 10001011: {
                return 3060;
            }
        }
        return 330;
    }

    public static byte gachaponRareItem(int id) {
        switch (id) {
            default: 
        }
        return 0;
    }

    public static boolean isDragonItem(int itemId) {
        switch (itemId) {
            case 1302059: 
            case 1312031: 
            case 1322052: 
            case 1332049: 
            case 1332050: 
            case 1342010: 
            case 1372032: 
            case 1382036: 
            case 1402036: 
            case 1412026: 
            case 1422028: 
            case 1432038: 
            case 1442045: 
            case 1452044: 
            case 1462039: 
            case 1472051: 
            case 1472052: {
                return true;
            }
        }
        return false;
    }

    public static boolean isReverseItem(int itemId) {
        switch (itemId) {
            case 1002790: 
            case 1002791: 
            case 1002792: 
            case 1002793: 
            case 1002794: 
            case 0x100E00: 
            case 0x100E01: 
            case 1052162: 
            case 1052163: 
            case 1052164: 
            case 1072361: 
            case 1072362: 
            case 1072363: 
            case 1072364: 
            case 1072365: 
            case 1082239: 
            case 1082240: 
            case 1082241: 
            case 1082242: 
            case 1082243: 
            case 1302086: 
            case 1312038: 
            case 1322061: 
            case 1332075: 
            case 1332076: 
            case 1342012: 
            case 1372045: 
            case 1382059: 
            case 1402047: 
            case 1412034: 
            case 1422038: 
            case 1432049: 
            case 1442067: 
            case 1452059: 
            case 1462051: 
            case 1472071: 
            case 1482024: 
            case 1492025: {
                return true;
            }
        }
        return false;
    }

    public static boolean isTimelessItem(int itemId) {
        switch (itemId) {
            case 1002776: 
            case 1002777: 
            case 1002778: 
            case 1002779: 
            case 1002780: 
            case 1032031: 
            case 1052155: 
            case 1052156: 
            case 1052157: 
            case 1052158: 
            case 1052159: 
            case 1072355: 
            case 1072356: 
            case 1072357: 
            case 1072358: 
            case 1072359: 
            case 1082234: 
            case 1082235: 
            case 1082236: 
            case 1082237: 
            case 1082238: 
            case 1092057: 
            case 1092058: 
            case 1092059: 
            case 1102172: 
            case 1122011: 
            case 1122012: 
            case 1302081: 
            case 1312037: 
            case 1322060: 
            case 1332073: 
            case 1332074: 
            case 1342011: 
            case 1372044: 
            case 1382057: 
            case 1402046: 
            case 1412033: 
            case 1422037: 
            case 1432047: 
            case 1442063: 
            case 1452057: 
            case 1462050: 
            case 1472068: 
            case 1482023: 
            case 1492023: {
                return true;
            }
        }
        return false;
    }

    public static boolean isRing(int itemId) {
        return itemId >= 1112000 && itemId < 1113000;
    }

    public static boolean isEffectRing(int itemid) {
        return GameConstants.isFriendshipRing(itemid) || GameConstants.isCrushRing(itemid);
    }

    public static boolean isMarriageRing(int itemId) {
        switch (itemId) {
            case 1112803: 
            case 1112806: 
            case 1112807: 
            case 1112809: {
                return true;
            }
        }
        return false;
    }

    public static boolean isFriendshipRing(int itemId) {
        switch (itemId) {
            case 1049000: 
            case 1112800: 
            case 1112801: 
            case 1112802: 
            case 1112810: 
            case 1112811: 
            case 1112812: {
                return true;
            }
        }
        return false;
    }

    public static boolean isCrushRing(int itemId) {
        switch (itemId) {
            case 1048000: 
            case 1112001: 
            case 1112002: 
            case 1112003: 
            case 1112005: 
            case 1112006: 
            case 1112007: 
            case 1112012: 
            case 1112015: {
                return true;
            }
        }
        return false;
    }

    public static int Equipment_Bonus_EXP(int itemid) {
        switch (itemid) {
            case 0x111EE1: {
                return 10;
            }
        }
        return 0;
    }

    public static int getExpForLevel(int i, int itemId) {
        if (GameConstants.isReverseItem(itemId)) {
            return GameConstants.getReverseRequiredEXP(i);
        }
        if (GameConstants.getMaxLevel(itemId) > 0) {
            return GameConstants.getTimelessRequiredEXP(i);
        }
        return 0;
    }

    public static int getMaxLevel(int itemId) {
        if (GameConstants.isTimelessItem(itemId)) {
            return 5;
        }
        if (GameConstants.isReverseItem(itemId)) {
            return 3;
        }
        switch (itemId) {
            case 1302108: 
            case 1302109: 
            case 1312040: 
            case 1312041: 
            case 1322066: 
            case 1322067: 
            case 1332082: 
            case 1332083: 
            case 1372047: 
            case 1372048: 
            case 1382063: 
            case 1382064: 
            case 1402054: 
            case 1402055: 
            case 1412036: 
            case 1412037: 
            case 1422040: 
            case 1422041: 
            case 1432051: 
            case 1432052: 
            case 1442072: 
            case 1442073: 
            case 1452063: 
            case 1452064: 
            case 1462057: 
            case 1462058: 
            case 1472078: 
            case 1472079: 
            case 1482035: 
            case 1482036: {
                return 1;
            }
            case 1072376: {
                return 2;
            }
        }
        return 0;
    }

    public static int getStatChance() {
        return 25;
    }

    public static MonsterStatus getStatFromWeapon(int itemid) {
        switch (itemid) {
            case 1302109: 
            case 1312041: 
            case 1322067: 
            case 1332083: 
            case 1372048: 
            case 1382064: 
            case 1402055: 
            case 1412037: 
            case 1422041: 
            case 1432052: 
            case 1442073: 
            case 1452064: 
            case 1462058: 
            case 1472079: 
            case 1482035: {
                return MonsterStatus.ACC;
            }
            case 1302108: 
            case 1312040: 
            case 1322066: 
            case 1332082: 
            case 1372047: 
            case 1382063: 
            case 1402054: 
            case 1412036: 
            case 1422040: 
            case 1432051: 
            case 1442072: 
            case 1452063: 
            case 1462057: 
            case 1472078: 
            case 1482036: {
                return MonsterStatus.SPEED;
            }
        }
        return null;
    }

    public static int getXForStat(MonsterStatus stat) {
        switch (stat) {
            case ACC: {
                return -70;
            }
            case SPEED: {
                return -50;
            }
        }
        return 0;
    }

    public static int getSkillForStat(MonsterStatus stat) {
        switch (stat) {
            case ACC: {
                return 3221006;
            }
            case SPEED: {
                return 3121007;
            }
        }
        return 0;
    }

    public static int getSkillBook(int job) {
        if (job >= 2210 && job <= 2218) {
            return job - 2209;
        }
        switch (job) {
            case 3210: 
            case 3310: 
            case 3510: {
                return 1;
            }
            case 3211: 
            case 3311: 
            case 3511: {
                return 2;
            }
            case 3212: 
            case 3312: 
            case 3512: {
                return 3;
            }
        }
        return 0;
    }

    public static int getSkillBookForSkill(int skillid) {
        return GameConstants.getSkillBook(skillid / 10000);
    }

    public static int getMountItem(int sourceid) {
        switch (sourceid) {
            case 5221006: {
                return 1932000;
            }
            case 33001001: {
                return 1932015;
            }
            case 35001002: 
            case 35120000: {
                return 1932016;
            }
            case 1013: 
            case 1046: 
            case 10001013: 
            case 10001046: 
            case 20001013: 
            case 0x1313116: 
            case 20011013: 
            case 20011046: 
            case 30001013: 
            case 30001046: {
                return 1932001;
            }
            case 1015: 
            case 1048: 
            case 10001015: 
            case 0x989A98: 
            case 20001015: 
            case 0x1313118: 
            case 20011015: 
            case 20011048: 
            case 30001015: 
            case 30001048: {
                return 1932002;
            }
            case 1007: 
            case 1016: 
            case 1017: 
            case 1027: 
            case 10001016: 
            case 10001017: 
            case 10001027: 
            case 20001016: 
            case 20001017: 
            case 0x1313103: 
            case 20011016: 
            case 20011017: 
            case 20011027: 
            case 30001016: 
            case 30001017: 
            case 30001027: {
                return 1932007;
            }
            case 1018: 
            case 10001018: 
            case 20001018: 
            case 20011018: 
            case 30001018: {
                return 1932003;
            }
            case 1019: 
            case 10001019: 
            case 20001019: 
            case 20011019: 
            case 30001019: {
                return 1932005;
            }
            case 1025: 
            case 10001025: 
            case 0x1313101: 
            case 20011025: 
            case 30001025: {
                return 1932006;
            }
            case 1028: 
            case 10001028: 
            case 20001028: 
            case 20011028: 
            case 30001028: {
                return 1932008;
            }
            case 1029: 
            case 10001029: 
            case 20001029: 
            case 20011029: 
            case 30001029: {
                return 1932009;
            }
            case 1030: 
            case 10001030: 
            case 20001030: 
            case 20011030: 
            case 30001030: {
                return 1932011;
            }
            case 1031: 
            case 10001031: 
            case 20001031: 
            case 20011031: 
            case 30001031: {
                return 1932010;
            }
            case 1034: 
            case 0x989A8A: 
            case 20001034: 
            case 20011034: 
            case 30001034: {
                return 1932014;
            }
            case 1035: 
            case 10001035: 
            case 20001035: 
            case 20011035: 
            case 30001035: {
                return 1932012;
            }
            case 1036: 
            case 10001036: 
            case 20001036: 
            case 20011036: 
            case 30001036: {
                return 1932017;
            }
            case 1037: 
            case 10001037: 
            case 20001037: 
            case 20011037: 
            case 30001037: {
                return 1932018;
            }
            case 1038: 
            case 10001038: 
            case 20001038: 
            case 20011038: 
            case 30001038: {
                return 1932019;
            }
            case 1039: 
            case 10001039: 
            case 20001039: 
            case 20011039: 
            case 30001039: {
                return 1932020;
            }
            case 1040: 
            case 10001040: 
            case 0x1313110: 
            case 20011040: 
            case 30001040: {
                return 1932021;
            }
            case 1042: 
            case 10001042: 
            case 0x1313112: 
            case 20011042: 
            case 30001042: {
                return 1932022;
            }
            case 1044: 
            case 10001044: 
            case 0x1313114: 
            case 20011044: 
            case 30001044: {
                return 1932023;
            }
            case 1045: 
            case 10001045: 
            case 0x1313115: 
            case 20011045: 
            case 30001045: {
                return 1932030;
            }
            case 1049: 
            case 0x989A99: 
            case 0x1313119: 
            case 20011049: 
            case 30001049: {
                return 1932025;
            }
            case 1050: 
            case 0x989A9A: 
            case 0x131311A: 
            case 20011050: 
            case 30001050: {
                return 1932004;
            }
            case 1051: 
            case 10001051: 
            case 0x131311B: 
            case 20011051: 
            case 30001051: {
                return 1932026;
            }
            case 1052: 
            case 10001052: 
            case 0x131311C: 
            case 20011052: 
            case 30001052: {
                return 1932027;
            }
            case 1053: 
            case 10001053: 
            case 0x131311D: 
            case 20011053: 
            case 30001053: {
                return 1932028;
            }
            case 1054: 
            case 10001054: 
            case 0x131311E: 
            case 20011054: 
            case 30001054: {
                return 1932029;
            }
            case 1069: 
            case 10001069: 
            case 20001069: 
            case 20011069: 
            case 30001069: {
                return 1932038;
            }
            case 1096: 
            case 10001096: 
            case 20001096: 
            case 20011096: 
            case 30001096: {
                return 1932045;
            }
            case 1101: 
            case 10001101: 
            case 20001101: 
            case 20011101: 
            case 30001101: {
                return 1932046;
            }
            case 1102: 
            case 10001102: 
            case 20001102: 
            case 20011102: 
            case 30001102: {
                return 1932047;
            }
        }
        return 0;
    }

    public static boolean isKatara(int itemId) {
        return itemId / 10000 == 134;
    }

    public static boolean isDagger(int itemId) {
        return itemId / 10000 == 133;
    }

    public static boolean isApplicableSkill(int skil) {
        return skil < 40000000 && (skil % 10000 < 8000 || skil % 10000 > 8003);
    }

    public static boolean isApplicableSkill_(int skil) {
        return skil >= 90000000 || skil % 10000 >= 8000 && skil % 10000 <= 8003;
    }

    public static boolean isTablet(int itemId) {
        return itemId / 1000 == 2047;
    }

    public static int getSuccessTablet(int scrollId, int level) {
        if (scrollId % 1000 / 100 == 2) {
            switch (level) {
                case 0: {
                    return 70;
                }
                case 1: {
                    return 55;
                }
                case 2: {
                    return 43;
                }
                case 3: {
                    return 33;
                }
                case 4: {
                    return 26;
                }
                case 5: {
                    return 20;
                }
                case 6: {
                    return 16;
                }
                case 7: {
                    return 12;
                }
                case 8: {
                    return 10;
                }
            }
            return 7;
        }
        if (scrollId % 1000 / 100 == 3) {
            switch (level) {
                case 0: {
                    return 70;
                }
                case 1: {
                    return 35;
                }
                case 2: {
                    return 18;
                }
                case 3: {
                    return 12;
                }
            }
            return 7;
        }
        switch (level) {
            case 0: {
                return 70;
            }
            case 1: {
                return 50;
            }
            case 2: {
                return 36;
            }
            case 3: {
                return 26;
            }
            case 4: {
                return 19;
            }
            case 5: {
                return 14;
            }
            case 6: {
                return 10;
            }
        }
        return 7;
    }

    public static int getCurseTablet(int scrollId, int level) {
        if (scrollId % 1000 / 100 == 2) {
            switch (level) {
                case 0: {
                    return 10;
                }
                case 1: {
                    return 12;
                }
                case 2: {
                    return 16;
                }
                case 3: {
                    return 20;
                }
                case 4: {
                    return 26;
                }
                case 5: {
                    return 33;
                }
                case 6: {
                    return 43;
                }
                case 7: {
                    return 55;
                }
                case 8: {
                    return 70;
                }
            }
            return 100;
        }
        if (scrollId % 1000 / 100 == 3) {
            switch (level) {
                case 0: {
                    return 12;
                }
                case 1: {
                    return 18;
                }
                case 2: {
                    return 35;
                }
                case 3: {
                    return 70;
                }
            }
            return 100;
        }
        switch (level) {
            case 0: {
                return 10;
            }
            case 1: {
                return 14;
            }
            case 2: {
                return 19;
            }
            case 3: {
                return 26;
            }
            case 4: {
                return 36;
            }
            case 5: {
                return 50;
            }
            case 6: {
                return 70;
            }
        }
        return 100;
    }

    public static boolean isAccessory(int itemId) {
        return itemId >= 1010000 && itemId < 1040000 || itemId >= 1122000 && itemId < 1153000 || itemId >= 1112000 && itemId < 1113000;
    }

    public static boolean potentialIDFits(int potentialID, int newstate, int i) {
        if (newstate == 7) {
            return i == 0 || Randomizer.nextInt(10) == 0 ? potentialID >= 30000 : potentialID >= 20000 && potentialID < 30000;
        }
        if (newstate == 6) {
            return i == 0 || Randomizer.nextInt(10) == 0 ? potentialID >= 20000 && potentialID < 30000 : potentialID >= 10000 && potentialID < 20000;
        }
        if (newstate == 5) {
            return i == 0 || Randomizer.nextInt(10) == 0 ? potentialID >= 10000 && potentialID < 20000 : potentialID < 10000;
        }
        return false;
    }

    public static boolean optionTypeFits(int optionType, int itemId) {
        switch (optionType) {
            case 10: {
                return GameConstants.isWeapon(itemId);
            }
            case 11: {
                return !GameConstants.isWeapon(itemId);
            }
            case 20: {
                return itemId / 10000 == 109;
            }
            case 21: {
                return itemId / 10000 == 180;
            }
            case 40: {
                return GameConstants.isAccessory(itemId);
            }
            case 51: {
                return itemId / 10000 == 100;
            }
            case 52: {
                return itemId / 10000 == 110;
            }
            case 53: {
                return itemId / 10000 == 104 || itemId / 10000 == 105 || itemId / 10000 == 106;
            }
            case 54: {
                return itemId / 10000 == 108;
            }
            case 55: {
                return itemId / 10000 == 107;
            }
            case 90: {
                return false;
            }
        }
        return true;
    }

    public static final boolean isMountItemAvailable(int mountid, int jobid) {
        return jobid == 900 || mountid / 10000 != 190 || !(GameConstants.isKOC(jobid) ? mountid < 1902005 || mountid > 1902007 : (GameConstants.isAdventurer(jobid) ? mountid < 1902000 || mountid > 1902002 : (GameConstants.isAran(jobid) ? mountid < 1902015 || mountid > 1902018 : GameConstants.isEvan(jobid) && (mountid < 1902040 || mountid > 1902042))));
    }

    public static boolean isEvanDragonItem(int itemId) {
        return itemId >= 1940000 && itemId < 1980000;
    }

    public static boolean canScroll(int itemId) {
        return itemId / 100000 != 19 && itemId / 100000 != 16;
    }

    public static boolean canHammer(int itemId) {
        switch (itemId) {
            case 1122000: 
            case 0x111F1C: {
                return false;
            }
        }
        return GameConstants.canScroll(itemId);
    }

    public static int getMasterySkill(int job) {
        if (job >= 1410 && job <= 1412) {
            return 14100000;
        }
        if (job >= 410 && job <= 412) {
            return 4100000;
        }
        if (job >= 520 && job <= 522) {
            return 5200000;
        }
        return 0;
    }

    public static int getExpRate_Below10(int job) {
        if (GameConstants.isEvan(job)) {
            return 1;
        }
        if (GameConstants.isAran(job) || GameConstants.isKOC(job)) {
            return 5;
        }
        return 1;
    }

    public static int getExpRate_Quest(int level) {
        return 1;
    }

    public static String getCashBlockedMsg(int id) {
        switch (id) {
            case 5062000: {
                return "This item may only be purchased at the PlayerNPC in FM.";
            }
        }
        return "This item is blocked from the Cash Shop.";
    }

    public static boolean isCustomReactItem(int rid, int iid, int original) {
        if (rid == 2008006) {
            return iid == Calendar.getInstance().get(7) + 4001055;
        }
        return iid == original;
    }

    public static int getJobNumber(int jobz) {
        int job = jobz % 1000;
        if (job / 100 == 0) {
            return 0;
        }
        if (job / 10 == 0) {
            return 1;
        }
        return 2 + job % 10;
    }

    public static boolean isForceRespawn(int mapid) {
        switch (mapid) {
            case 925100100: {
                return true;
            }
        }
        return mapid / 100000 == 9800 && (mapid % 10 == 1 || mapid % 1000 == 100);
    }

    public static int getFishingTime(boolean vip, boolean gm) {
        return gm ? 1000 : (vip ? 30000 : 60000);
    }

    public static int getCustomSpawnID(int summoner, int def) {
        switch (summoner) {
            case 9400589: 
            case 9400748: {
                return 9400706;
            }
        }
        return def;
    }

    public static boolean canForfeit(int questid) {
        switch (questid) {
            case 20000: 
            case 20010: 
            case 20015: 
            case 20020: {
                return false;
            }
        }
        return true;
    }

    public static boolean isEventMap(int mapid) {
        return mapid >= 109010000 && mapid < 109050000 || mapid > 109050001 && mapid < 109090000 || mapid >= 809040000 && mapid <= 809040100;
    }

    public static int getCurrentDate() {
        String time = FileoutputUtil.CurrentReadable_Time();
        return Integer.parseInt(time.substring(0, 4) + time.substring(5, 7) + time.substring(8, 10) + time.substring(11, 13));
    }
}

