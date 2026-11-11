/*
 * Decompiled with CFR 0.152.
 */
package tools.wztosql;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import tools.Pair;
import tools.StringUtil;

public class MonsterDropCreator {
    private static final int COMMON_ETC_RATE = 600000;
    private static final int SUPER_BOSS_ITEM_RATE = 300000;
    private static final int POTION_RATE = 20000;
    private static final int ARROWS_RATE = 25000;
    private static int lastmonstercardid = 2388070;
    private static boolean addFlagData = false;
    protected static String monsterQueryData = "drop_data";
    protected static List<Pair<Integer, String>> itemNameCache = new ArrayList<Pair<Integer, String>>();
    protected static List<Pair<Integer, MobInfo>> mobCache = new ArrayList<Pair<Integer, MobInfo>>();
    protected static Map<Integer, Boolean> bossCache = new HashMap<Integer, Boolean>();

    public static void main(String[] args) throws FileNotFoundException, IOException, NotBoundException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException {
        MapleData data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "String.wz")).getData("MonsterBook.img");
        System.out.println("\u6e96\u5099\u63d0\u53d6\u6578\u64da!");
        System.out.println("\u6309\u4efb\u610f\u9375\u7e7c\u7e8c...");
        System.console().readLine();
        long currtime = System.currentTimeMillis();
        addFlagData = false;
        System.out.println("\u8f09\u5165: \u7269\u54c1\u540d\u7a31.");
        MonsterDropCreator.getAllItems();
        System.out.println("\u8f09\u5165: \u602a\u7269\u6578\u64da.");
        MonsterDropCreator.getAllMobs();
        StringBuilder sb = new StringBuilder();
        FileOutputStream out = new FileOutputStream("mobDrop.sql", true);
        for (Map.Entry<Integer, List<Integer>> e : MonsterDropCreator.getDropsNotInMonsterBook().entrySet()) {
            boolean first = true;
            sb.append("INSERT INTO ").append(monsterQueryData).append(" VALUES ");
            for (Integer monsterdrop : e.getValue()) {
                int monsterId;
                int itemid = monsterdrop;
                int rate = MonsterDropCreator.getChance(itemid, monsterId = e.getKey().intValue(), bossCache.containsKey(monsterId));
                if (rate <= 100000) {
                    switch (monsterId) {
                        case 9400121: {
                            rate *= 5;
                            break;
                        }
                        case 9400112: 
                        case 9400113: 
                        case 9400300: {
                            rate *= 10;
                        }
                    }
                }
                for (int i = 0; i < MonsterDropCreator.multipleDropsIncrement(itemid, monsterId); ++i) {
                    if (first) {
                        sb.append("(DEFAULT, ");
                        first = false;
                    } else {
                        sb.append(", (DEFAULT, ");
                    }
                    sb.append(monsterId).append(", ");
                    if (addFlagData) {
                        sb.append("'', ");
                    }
                    sb.append(itemid).append(", ");
                    sb.append("1, 1,");
                    sb.append("0, ");
                    int num = MonsterDropCreator.IncrementRate(itemid, i);
                    sb.append(num == -1 ? rate : num);
                    sb.append(")");
                    first = false;
                }
                sb.append("\n");
                sb.append("-- Name : ");
                MonsterDropCreator.retriveNLogItemName(sb, itemid);
                sb.append("\n");
            }
            sb.append(";");
            sb.append("\n");
            out.write(sb.toString().getBytes());
            sb.delete(0, Integer.MAX_VALUE);
        }
        System.out.println("\u8f09\u5165: Drops from String.wz/MonsterBook.img.");
        for (MapleData dataz : data.getChildren()) {
            int monsterId;
            int idtoLog = monsterId = Integer.parseInt(dataz.getName());
            boolean first = true;
            if (monsterId == 9400408) {
                idtoLog = 9400409;
            }
            if (dataz.getChildByPath("reward").getChildren().size() > 0) {
                sb.append("INSERT INTO ").append(monsterQueryData).append(" VALUES ");
                for (MapleData drop : dataz.getChildByPath("reward")) {
                    int itemid = MapleDataTool.getInt(drop);
                    int rate = MonsterDropCreator.getChance(itemid, idtoLog, bossCache.containsKey(idtoLog));
                    block20: for (Pair<Integer, MobInfo> Pair2 : mobCache) {
                        if (Pair2.getLeft() != monsterId) continue;
                        if (Pair2.getRight().getBoss() <= 0 || rate > 100000) break;
                        if (Pair2.getRight().rateItemDropLevel() == 2) {
                            rate *= 10;
                            break;
                        }
                        if (Pair2.getRight().rateItemDropLevel() == 3) {
                            switch (monsterId) {
                                case 8810018: {
                                    rate *= 48;
                                }
                                case 8800002: {
                                    rate *= 45;
                                    break;
                                }
                                default: {
                                    rate *= 30;
                                }
                            }
                        }
                        switch (monsterId) {
                            case 8860010: 
                            case 9400265: 
                            case 9400270: 
                            case 9400273: {
                                rate *= 10;
                                continue block20;
                            }
                            case 9400294: {
                                rate *= 24;
                                continue block20;
                            }
                            case 9420522: {
                                rate *= 29;
                                continue block20;
                            }
                            case 9400409: {
                                rate *= 35;
                                continue block20;
                            }
                            case 9400287: {
                                rate *= 60;
                                continue block20;
                            }
                        }
                        rate *= 10;
                    }
                    for (int i = 0; i < MonsterDropCreator.multipleDropsIncrement(itemid, idtoLog); ++i) {
                        if (first) {
                            sb.append("(DEFAULT, ");
                            first = false;
                        } else {
                            sb.append(", (DEFAULT, ");
                        }
                        sb.append(idtoLog).append(", ");
                        if (addFlagData) {
                            sb.append("'', ");
                        }
                        sb.append(itemid).append(", ");
                        sb.append("1, 1,");
                        sb.append("0, ");
                        int num = MonsterDropCreator.IncrementRate(itemid, i);
                        sb.append(num == -1 ? rate : num);
                        sb.append(")");
                        first = false;
                    }
                    sb.append("\n");
                    sb.append("-- Name : ");
                    MonsterDropCreator.retriveNLogItemName(sb, itemid);
                    sb.append("\n");
                }
                sb.append(";");
            }
            sb.append("\n");
            out.write(sb.toString().getBytes());
            sb.delete(0, Integer.MAX_VALUE);
        }
        System.out.println("\u8f09\u5165: \u602a\u7269\u66f8\u6578\u64da.");
        StringBuilder SQL = new StringBuilder();
        StringBuilder bookName = new StringBuilder();
        for (Pair<Integer, String> Pair3 : itemNameCache) {
            if (Pair3.getLeft() < 2380000 || Pair3.getLeft() > lastmonstercardid) continue;
            bookName.append(Pair3.getRight());
            if (bookName.toString().contains(" Card")) {
                bookName.delete(bookName.length() - 5, bookName.length());
            }
            for (Pair<Integer, MobInfo> Pair_ : mobCache) {
                if (!Pair_.getRight().getName().equalsIgnoreCase(bookName.toString())) continue;
                int rate = 1000;
                if (Pair_.getRight().getBoss() > 0) {
                    rate *= 25;
                }
                SQL.append("INSERT INTO ").append(monsterQueryData).append(" VALUES ");
                SQL.append("(DEFAULT, ");
                SQL.append(Pair_.getLeft()).append(", ");
                if (addFlagData) {
                    sb.append("'', ");
                }
                SQL.append(Pair3.getLeft()).append(", ");
                SQL.append("1, 1,");
                SQL.append("0, ");
                SQL.append(rate);
                SQL.append(");\n");
                SQL.append("-- \u7269\u54c1\u540d : ").append(Pair3.getRight()).append("\n");
                break;
            }
            bookName.delete(0, Integer.MAX_VALUE);
        }
        System.out.println("\u8f09\u5165: \u602a\u7269\u5361\u6578\u64da.");
        SQL.append("\n");
        int i = 1;
        int lastmonsterbookid = 0;
        for (Pair<Integer, String> Pair4 : itemNameCache) {
            if (Pair4.getLeft() < 2380000 || Pair4.getLeft() > lastmonstercardid) continue;
            bookName.append(Pair4.getRight());
            if (bookName.toString().contains(" Card")) {
                bookName.delete(bookName.length() - 5, bookName.length());
            }
            if (Pair4.getLeft() == lastmonsterbookid) continue;
            for (Pair<Integer, MobInfo> Pair_ : mobCache) {
                if (!Pair_.getRight().getName().equalsIgnoreCase(bookName.toString())) continue;
                SQL.append("INSERT INTO ").append("monstercarddata").append(" VALUES (");
                SQL.append(i).append(", ");
                SQL.append(Pair4.getLeft());
                SQL.append(", ");
                SQL.append(Pair_.getLeft()).append(");\n");
                lastmonsterbookid = Pair4.getLeft();
                ++i;
                break;
            }
            bookName.delete(0, Integer.MAX_VALUE);
        }
        out.write(SQL.toString().getBytes());
        out.close();
        long time = System.currentTimeMillis() - currtime;
        System.out.println("Time taken : " + (time /= 1000L));
    }

    private static void retriveNLogItemName(StringBuilder sb, int id) {
        for (Pair<Integer, String> Pair2 : itemNameCache) {
            if (Pair2.getLeft() != id) continue;
            sb.append(Pair2.getRight());
            return;
        }
        sb.append("MISSING STRING, ID : ");
        sb.append(id);
    }

    private static int IncrementRate(int itemid, int times) {
        if (times == 0) {
            if (itemid == 1002357 || itemid == 1002926 || itemid == 1002927) {
                return 999999;
            }
            if (itemid == 1122000) {
                return 999999;
            }
            if (itemid == 1002972) {
                return 999999;
            }
        } else if (times == 1) {
            if (itemid == 1002357 || itemid == 1002926 || itemid == 1002927) {
                return 999999;
            }
            if (itemid == 1122000) {
                return 999999;
            }
            if (itemid == 1002972) {
                return 300000;
            }
        } else if (times == 2) {
            if (itemid == 1002357 || itemid == 1002926 || itemid == 1002927) {
                return 300000;
            }
            if (itemid == 1122000) {
                return 300000;
            }
        } else if (times == 3 ? itemid == 1002357 || itemid == 1002926 || itemid == 1002927 : times == 4 && (itemid == 1002357 || itemid == 1002926 || itemid == 1002927)) {
            return 300000;
        }
        return -1;
    }

    private static int multipleDropsIncrement(int itemid, int mobid) {
        switch (itemid) {
            case 1002357: 
            case 1002390: 
            case 1002430: 
            case 1002926: 
            case 1002927: {
                return 5;
            }
            case 1122000: {
                return 4;
            }
            case 4021010: {
                return 7;
            }
            case 1002972: {
                return 2;
            }
            case 4000172: {
                if (mobid == 7220001) {
                    return 8;
                }
                return 1;
            }
            case 4000000: 
            case 4000003: 
            case 4000005: 
            case 4000016: 
            case 4000018: 
            case 4000019: 
            case 4000021: 
            case 4000026: 
            case 4000029: 
            case 4000031: 
            case 4000032: 
            case 4000033: 
            case 4000043: 
            case 4000044: 
            case 4000073: 
            case 4000074: 
            case 4000113: 
            case 4000114: 
            case 4000115: 
            case 4000117: 
            case 4000118: 
            case 4000119: 
            case 4000166: 
            case 4000167: 
            case 4000195: 
            case 4000268: 
            case 4000269: 
            case 4000270: 
            case 4000283: 
            case 4000284: 
            case 4000285: 
            case 4000289: 
            case 4000298: 
            case 4000329: 
            case 4000330: 
            case 4000331: 
            case 4000356: 
            case 4000364: 
            case 4000365: {
                if (mobid == 2220000 || mobid == 3220000 || mobid == 0x312221 || mobid == 0x406460 || mobid == 5220000 || mobid == 5220002 || mobid == 5220003 || mobid == 6220000 || mobid == 4000119 || mobid == 7220000 || mobid == 7220002 || mobid == 8220000 || mobid == 8220002 || mobid == 8220003) {
                    return 3;
                }
                return 1;
            }
        }
        return 1;
    }

    private static int getChance(int id, int mobid, boolean boss) {
        switch (id / 10000) {
            case 100: {
                switch (id) {
                    case 1002357: 
                    case 1002390: 
                    case 1002430: 
                    case 1002905: 
                    case 1002906: 
                    case 1002926: 
                    case 1002927: 
                    case 1002972: {
                        return 300000;
                    }
                }
                return 1500;
            }
            case 103: {
                switch (id) {
                    case 1032062: {
                        return 100;
                    }
                }
                return 1000;
            }
            case 105: 
            case 109: {
                switch (id) {
                    case 1092049: {
                        return 100;
                    }
                }
                return 700;
            }
            case 104: 
            case 106: 
            case 107: {
                switch (id) {
                    case 1072369: {
                        return 300000;
                    }
                }
                return 800;
            }
            case 108: 
            case 110: {
                return 1000;
            }
            case 112: {
                switch (id) {
                    case 1122000: {
                        return 300000;
                    }
                    case 1122011: 
                    case 1122012: {
                        return 800000;
                    }
                }
            }
            case 130: 
            case 131: 
            case 132: 
            case 137: {
                switch (id) {
                    case 1372049: {
                        return 999999;
                    }
                }
                return 700;
            }
            case 138: 
            case 140: 
            case 141: 
            case 142: 
            case 144: {
                return 700;
            }
            case 133: 
            case 143: 
            case 145: 
            case 146: 
            case 147: 
            case 148: 
            case 149: {
                return 500;
            }
            case 204: {
                switch (id) {
                    case 2049000: {
                        return 150;
                    }
                }
                return 300;
            }
            case 205: {
                return 50000;
            }
            case 206: {
                return 30000;
            }
            case 228: {
                return 30000;
            }
            case 229: {
                switch (id) {
                    case 2290096: {
                        return 800000;
                    }
                    case 2290125: {
                        return 100000;
                    }
                }
                return 500;
            }
            case 233: {
                switch (id) {
                    case 2330007: {
                        return 50;
                    }
                }
                return 500;
            }
            case 400: {
                switch (id) {
                    case 4000021: {
                        return 50000;
                    }
                    case 4001094: {
                        return 999999;
                    }
                    case 4001000: {
                        return 5000;
                    }
                    case 4000157: {
                        return 100000;
                    }
                    case 4001023: 
                    case 0x3D0D00: {
                        return 999999;
                    }
                    case 4000244: 
                    case 4000245: {
                        return 2000;
                    }
                    case 4001005: {
                        return 5000;
                    }
                    case 4001006: {
                        return 10000;
                    }
                    case 4000017: 
                    case 4000082: {
                        return 40000;
                    }
                    case 4000446: 
                    case 4000451: 
                    case 4000456: {
                        return 10000;
                    }
                    case 4000459: {
                        return 20000;
                    }
                    case 4000030: {
                        return 60000;
                    }
                    case 4000339: {
                        return 70000;
                    }
                    case 4000313: 
                    case 4007000: 
                    case 4007001: 
                    case 4007002: 
                    case 4007003: 
                    case 4007004: 
                    case 4007005: 
                    case 4007006: 
                    case 4007007: 
                    case 4031456: {
                        return 100000;
                    }
                    case 4001126: {
                        return 500000;
                    }
                }
                switch (id / 1000) {
                    case 4000: 
                    case 4001: {
                        return 600000;
                    }
                    case 4003: {
                        return 200000;
                    }
                    case 4004: 
                    case 4006: {
                        return 10000;
                    }
                    case 4005: {
                        return 1000;
                    }
                }
            }
            case 401: 
            case 402: {
                switch (id) {
                    case 4020009: {
                        return 5000;
                    }
                    case 4021010: {
                        return 300000;
                    }
                }
                return 9000;
            }
            case 403: {
                switch (id) {
                    case 4032024: {
                        return 50000;
                    }
                    case 4032181: {
                        return boss ? 999999 : 300000;
                    }
                    case 4032025: 
                    case 4032155: 
                    case 4032156: 
                    case 4032159: 
                    case 4032161: 
                    case 4032163: {
                        return 600000;
                    }
                    case 4032166: 
                    case 4032167: 
                    case 4032168: {
                        return 10000;
                    }
                    case 4032151: 
                    case 4032158: 
                    case 4032164: 
                    case 4032180: {
                        return 2000;
                    }
                    case 4032152: 
                    case 4032153: 
                    case 4032154: {
                        return 4000;
                    }
                }
                return 300;
            }
            case 413: {
                return 6000;
            }
            case 416: {
                return 6000;
            }
        }
        switch (id / 1000000) {
            case 1: {
                return 999999;
            }
            case 2: {
                switch (id) {
                    case 2000004: 
                    case 2000005: {
                        return boss ? 999999 : 20000;
                    }
                    case 2000006: {
                        return mobid == 9420540 ? 50000 : (boss ? 999999 : 20000);
                    }
                    case 2022345: {
                        return boss ? 999999 : 3000;
                    }
                    case 2012002: {
                        return 6000;
                    }
                    case 2020013: 
                    case 2020015: {
                        return boss ? 999999 : 20000;
                    }
                    case 2060000: 
                    case 2060001: 
                    case 2061000: 
                    case 2061001: {
                        return 25000;
                    }
                    case 2070000: 
                    case 2070001: 
                    case 2070002: 
                    case 2070003: 
                    case 2070004: 
                    case 2070008: 
                    case 2070009: 
                    case 2070010: {
                        return 500;
                    }
                    case 2070005: {
                        return 400;
                    }
                    case 2070006: 
                    case 2070007: {
                        return 200;
                    }
                    case 2070012: 
                    case 2070013: {
                        return 1500;
                    }
                    case 2070019: {
                        return 100;
                    }
                    case 2210006: {
                        return 999999;
                    }
                }
                return 20000;
            }
            case 3: {
                switch (id) {
                    case 3010007: 
                    case 3010008: {
                        return 500;
                    }
                }
                return 2000;
            }
        }
        System.out.println("\u672a\u8655\u7406\u7684\u6578\u64da, ID : " + id);
        return 999999;
    }

    private static Map<Integer, List<Integer>> getDropsNotInMonsterBook() {
        HashMap<Integer, List<Integer>> drops = new HashMap<Integer, List<Integer>>();
        ArrayList<Integer> IndiviualMonsterDrop = new ArrayList<Integer>();
        IndiviualMonsterDrop.add(4000139);
        IndiviualMonsterDrop.add(2002011);
        IndiviualMonsterDrop.add(2002011);
        IndiviualMonsterDrop.add(2002011);
        IndiviualMonsterDrop.add(2000004);
        IndiviualMonsterDrop.add(2000004);
        drops.put(9400112, IndiviualMonsterDrop);
        IndiviualMonsterDrop = new ArrayList();
        IndiviualMonsterDrop.add(4000140);
        IndiviualMonsterDrop.add(2022027);
        IndiviualMonsterDrop.add(2022027);
        IndiviualMonsterDrop.add(2000004);
        IndiviualMonsterDrop.add(2000004);
        IndiviualMonsterDrop.add(2002008);
        IndiviualMonsterDrop.add(2002008);
        drops.put(9400113, IndiviualMonsterDrop);
        IndiviualMonsterDrop = new ArrayList();
        IndiviualMonsterDrop.add(4000141);
        IndiviualMonsterDrop.add(2000004);
        IndiviualMonsterDrop.add(2040813);
        IndiviualMonsterDrop.add(2041030);
        IndiviualMonsterDrop.add(2041040);
        IndiviualMonsterDrop.add(1072238);
        IndiviualMonsterDrop.add(1032026);
        IndiviualMonsterDrop.add(1372011);
        drops.put(9400300, IndiviualMonsterDrop);
        IndiviualMonsterDrop = new ArrayList();
        IndiviualMonsterDrop.add(4000225);
        IndiviualMonsterDrop.add(2000006);
        IndiviualMonsterDrop.add(2000004);
        IndiviualMonsterDrop.add(2070013);
        IndiviualMonsterDrop.add(2002005);
        IndiviualMonsterDrop.add(2022018);
        IndiviualMonsterDrop.add(0x1F21F2);
        IndiviualMonsterDrop.add(2043704);
        IndiviualMonsterDrop.add(2044605);
        IndiviualMonsterDrop.add(2041034);
        IndiviualMonsterDrop.add(1032019);
        IndiviualMonsterDrop.add(1102013);
        IndiviualMonsterDrop.add(1322026);
        IndiviualMonsterDrop.add(1092015);
        IndiviualMonsterDrop.add(1382016);
        IndiviualMonsterDrop.add(1002276);
        IndiviualMonsterDrop.add(1002403);
        IndiviualMonsterDrop.add(1472027);
        drops.put(9400013, IndiviualMonsterDrop);
        IndiviualMonsterDrop = new ArrayList();
        IndiviualMonsterDrop.add(1372049);
        drops.put(8800002, IndiviualMonsterDrop);
        IndiviualMonsterDrop = new ArrayList();
        IndiviualMonsterDrop.add(4001094);
        IndiviualMonsterDrop.add(2290125);
        drops.put(8810018, IndiviualMonsterDrop);
        IndiviualMonsterDrop = new ArrayList();
        IndiviualMonsterDrop.add(4000138);
        IndiviualMonsterDrop.add(4010006);
        IndiviualMonsterDrop.add(2000006);
        IndiviualMonsterDrop.add(2000011);
        IndiviualMonsterDrop.add(2020016);
        IndiviualMonsterDrop.add(2022024);
        IndiviualMonsterDrop.add(2022026);
        IndiviualMonsterDrop.add(2043705);
        IndiviualMonsterDrop.add(2040716);
        IndiviualMonsterDrop.add(2040908);
        IndiviualMonsterDrop.add(2040510);
        IndiviualMonsterDrop.add(1072239);
        IndiviualMonsterDrop.add(1422013);
        IndiviualMonsterDrop.add(1402016);
        IndiviualMonsterDrop.add(1442020);
        IndiviualMonsterDrop.add(1432011);
        IndiviualMonsterDrop.add(1332022);
        IndiviualMonsterDrop.add(1312015);
        IndiviualMonsterDrop.add(1382010);
        IndiviualMonsterDrop.add(1372009);
        IndiviualMonsterDrop.add(1082085);
        IndiviualMonsterDrop.add(1332022);
        IndiviualMonsterDrop.add(1472033);
        drops.put(9400121, IndiviualMonsterDrop);
        IndiviualMonsterDrop = new ArrayList();
        IndiviualMonsterDrop.add(4032024);
        IndiviualMonsterDrop.add(4032025);
        IndiviualMonsterDrop.add(4020006);
        IndiviualMonsterDrop.add(4020008);
        IndiviualMonsterDrop.add(4010001);
        IndiviualMonsterDrop.add(4004001);
        IndiviualMonsterDrop.add(2070006);
        IndiviualMonsterDrop.add(2044404);
        IndiviualMonsterDrop.add(2044702);
        IndiviualMonsterDrop.add(2044305);
        IndiviualMonsterDrop.add(1102029);
        IndiviualMonsterDrop.add(1032023);
        IndiviualMonsterDrop.add(1402004);
        IndiviualMonsterDrop.add(1072210);
        IndiviualMonsterDrop.add(1040104);
        IndiviualMonsterDrop.add(1060092);
        IndiviualMonsterDrop.add(1082129);
        IndiviualMonsterDrop.add(1442008);
        IndiviualMonsterDrop.add(1072178);
        IndiviualMonsterDrop.add(1050092);
        IndiviualMonsterDrop.add(1002271);
        IndiviualMonsterDrop.add(1051053);
        IndiviualMonsterDrop.add(1382008);
        IndiviualMonsterDrop.add(1002275);
        IndiviualMonsterDrop.add(1051082);
        IndiviualMonsterDrop.add(1050064);
        IndiviualMonsterDrop.add(1472028);
        IndiviualMonsterDrop.add(1072193);
        IndiviualMonsterDrop.add(1072172);
        IndiviualMonsterDrop.add(1002285);
        drops.put(9400545, IndiviualMonsterDrop);
        IndiviualMonsterDrop = new ArrayList();
        return drops;
    }

    private static void getAllItems() {
        String itemName;
        int itemId;
        MapleDataProvider data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/String.wz"));
        ArrayList<Pair<Integer, String>> itemPairs = new ArrayList<Pair<Integer, String>>();
        MapleData itemsData = data.getData("Cash.img");
        for (MapleData itemFolder : itemsData.getChildren()) {
            itemId = Integer.parseInt(itemFolder.getName());
            itemName = MapleDataTool.getString("name", itemFolder, "NO-NAME");
            itemPairs.add(new Pair<Integer, String>(itemId, itemName));
        }
        itemsData = data.getData("Consume.img");
        for (MapleData itemFolder : itemsData.getChildren()) {
            itemId = Integer.parseInt(itemFolder.getName());
            itemName = MapleDataTool.getString("name", itemFolder, "NO-NAME");
            itemPairs.add(new Pair<Integer, String>(itemId, itemName));
        }
        itemsData = data.getData("Eqp.img").getChildByPath("Eqp");
        for (MapleData eqpType : itemsData.getChildren()) {
            for (MapleData itemFolder : eqpType.getChildren()) {
                int itemId2 = Integer.parseInt(itemFolder.getName());
                String itemName2 = MapleDataTool.getString("name", itemFolder, "NO-NAME");
                itemPairs.add(new Pair<Integer, String>(itemId2, itemName2));
            }
        }
        itemsData = data.getData("Etc.img").getChildByPath("Etc");
        for (MapleData itemFolder : itemsData.getChildren()) {
            int itemId3 = Integer.parseInt(itemFolder.getName());
            itemName = MapleDataTool.getString("name", itemFolder, "NO-NAME");
            itemPairs.add(new Pair<Integer, String>(itemId3, itemName));
        }
        itemsData = data.getData("Ins.img");
        for (MapleData itemFolder : itemsData.getChildren()) {
            int itemId4 = Integer.parseInt(itemFolder.getName());
            itemName = MapleDataTool.getString("name", itemFolder, "NO-NAME");
            itemPairs.add(new Pair<Integer, String>(itemId4, itemName));
        }
        itemsData = data.getData("Pet.img");
        for (MapleData itemFolder : itemsData.getChildren()) {
            int itemId5 = Integer.parseInt(itemFolder.getName());
            itemName = MapleDataTool.getString("name", itemFolder, "NO-NAME");
            itemPairs.add(new Pair<Integer, String>(itemId5, itemName));
        }
        itemNameCache.addAll(itemPairs);
    }

    public static void getAllMobs() {
        ArrayList<Pair<Integer, MobInfo>> itemPairs = new ArrayList<Pair<Integer, MobInfo>>();
        MapleDataProvider data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/String.wz"));
        MapleDataProvider mobData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/Mob.wz"));
        MapleData mob2 = data.getData("Mob.img");
        for (MapleData itemFolder : mob2.getChildren()) {
            int id = Integer.parseInt(itemFolder.getName());
            try {
                int boss;
                MapleData monsterData = mobData.getData(StringUtil.getLeftPaddedStr(Integer.toString(id) + ".img", '0', 11));
                int n = boss = id == 8810018 ? 1 : MapleDataTool.getIntConvert("boss", monsterData.getChildByPath("info"), 0);
                if (boss > 0) {
                    bossCache.put(id, true);
                }
                MobInfo mobInfo = new MobInfo(boss, MapleDataTool.getIntConvert("rareItemDropLevel", monsterData.getChildByPath("info"), 0), MapleDataTool.getString("name", itemFolder, "NO-NAME"));
                itemPairs.add(new Pair<Integer, MobInfo>(id, mobInfo));
            }
            catch (Exception exception) {}
        }
        mobCache.addAll(itemPairs);
    }

    public static class MobInfo {
        public int boss;
        public int rareItemDropLevel;
        public String name;

        public MobInfo(int boss, int rareItemDropLevel, String name) {
            this.boss = boss;
            this.rareItemDropLevel = rareItemDropLevel;
            this.name = name;
        }

        public int getBoss() {
            return this.boss;
        }

        public int rateItemDropLevel() {
            return this.rareItemDropLevel;
        }

        public String getName() {
            return this.name;
        }
    }
}

