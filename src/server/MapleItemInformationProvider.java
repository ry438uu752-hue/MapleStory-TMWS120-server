/*
 * Decompiled with CFR 0.152.
 */
package server;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Equip;
import client.inventory.IItem;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import provider.MapleData;
import provider.MapleDataDirectoryEntry;
import provider.MapleDataFileEntry;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.MapleStatEffect;
import server.Randomizer;
import server.StructPotentialItem;
import server.StructRewardItem;
import server.StructSetItem;
import tools.Pair;
import tools.StringUtil;

public class MapleItemInformationProvider {
    private static final MapleItemInformationProvider instance = new MapleItemInformationProvider();
    protected final MapleDataProvider etcData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Etc.wz"));
    protected final MapleDataProvider itemData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Item.wz"));
    protected final MapleDataProvider equipData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Character.wz"));
    protected final MapleDataProvider stringData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/String.wz"));
    protected final MapleData cashStringData = this.stringData.getData("Cash.img");
    protected final MapleData consumeStringData = this.stringData.getData("Consume.img");
    protected final MapleData eqpStringData = this.stringData.getData("Eqp.img");
    protected final MapleData etcStringData = this.stringData.getData("Etc.img");
    protected final MapleData insStringData = this.stringData.getData("Ins.img");
    protected final MapleData petStringData = this.stringData.getData("Pet.img");
    protected final Map<Integer, List<Integer>> scrollReqCache = new HashMap<Integer, List<Integer>>();
    protected final Map<Integer, Short> slotMaxCache = new HashMap<Integer, Short>();
    protected final Map<Integer, List<StructPotentialItem>> potentialCache = new HashMap<Integer, List<StructPotentialItem>>();
    protected final Map<Integer, MapleStatEffect> itemEffects = new HashMap<Integer, MapleStatEffect>();
    protected final Map<Integer, Map<String, Integer>> equipStatsCache = new HashMap<Integer, Map<String, Integer>>();
    protected final Map<Integer, Map<String, Byte>> itemMakeStatsCache = new HashMap<Integer, Map<String, Byte>>();
    protected final Map<Integer, Short> itemMakeLevel = new HashMap<Integer, Short>();
    protected final Map<Integer, Equip> equipCache = new HashMap<Integer, Equip>();
    protected final Map<Integer, Double> priceCache = new HashMap<Integer, Double>();
    protected final Map<Integer, Integer> wholePriceCache = new HashMap<Integer, Integer>();
    protected final Map<Integer, Integer> projectileWatkCache = new HashMap<Integer, Integer>();
    protected final Map<Integer, Integer> monsterBookID = new HashMap<Integer, Integer>();
    protected final Map<Integer, String> nameCache = new HashMap<Integer, String>();
    protected final Map<Integer, String> descCache = new HashMap<Integer, String>();
    protected final Map<Integer, String> msgCache = new HashMap<Integer, String>();
    protected final Map<Integer, MapleInventoryType> inventoryTypeCache = new HashMap<Integer, MapleInventoryType>();
    protected final Map<Integer, Short> petFlagInfo = new HashMap<Integer, Short>();
    protected final Map<Integer, Map<String, Integer>> SkillStatsCache = new HashMap<Integer, Map<String, Integer>>();
    protected final Map<Integer, Byte> consumeOnPickupCache = new HashMap<Integer, Byte>();
    protected final Map<Integer, Boolean> dropRestrictionCache = new HashMap<Integer, Boolean>();
    protected final Map<Integer, Boolean> accCache = new HashMap<Integer, Boolean>();
    protected final Map<Integer, Boolean> pickupRestrictionCache = new HashMap<Integer, Boolean>();
    protected final Map<Integer, Integer> stateChangeCache = new HashMap<Integer, Integer>();
    protected final Map<Integer, Integer> mesoCache = new HashMap<Integer, Integer>();
    protected final Map<Integer, Boolean> notSaleCache = new HashMap<Integer, Boolean>();
    protected final Map<Integer, Integer> karmaEnabledCache = new HashMap<Integer, Integer>();
    protected final Map<Integer, Boolean> isQuestItemCache = new HashMap<Integer, Boolean>();
    protected final Map<Integer, Boolean> blockPickupCache = new HashMap<Integer, Boolean>();
    protected final Map<Integer, List<Integer>> petsCanConsumeCache = new HashMap<Integer, List<Integer>>();
    protected final Map<Integer, Boolean> logoutExpireCache = new HashMap<Integer, Boolean>();
    protected final Map<Integer, List<Pair<Integer, Integer>>> summonMobCache = new HashMap<Integer, List<Pair<Integer, Integer>>>();
    protected final List<Pair<Integer, String>> itemNameCache = new ArrayList<Pair<Integer, String>>();
    protected final Map<Integer, Map<Integer, Map<String, Integer>>> equipIncsCache = new HashMap<Integer, Map<Integer, Map<String, Integer>>>();
    protected final Map<Integer, Map<Integer, List<Integer>>> equipSkillsCache = new HashMap<Integer, Map<Integer, List<Integer>>>();
    protected final Map<Integer, Pair<Integer, List<StructRewardItem>>> RewardItem = new HashMap<Integer, Pair<Integer, List<StructRewardItem>>>();
    protected final Map<Byte, StructSetItem> setItems = new HashMap<Byte, StructSetItem>();
    protected final Map<Integer, Pair<Integer, List<Integer>>> questItems = new HashMap<Integer, Pair<Integer, List<Integer>>>();
    protected final Map<Integer, String> faceList = new HashMap<Integer, String>();
    protected final Map<Integer, String> hairList = new HashMap<Integer, String>();

    protected MapleItemInformationProvider() {
        System.out.println("Loading MapleItemInformationProvider :::");
    }

    public final void load() {
        if (this.setItems.size() != 0 || this.potentialCache.size() != 0) {
            return;
        }
        this.getAllItems();
        MapleData setsData = this.etcData.getData("SetItemInfo.img");
        for (MapleData dat : setsData) {
            StructSetItem itemz = new StructSetItem();
            itemz.setItemID = Byte.parseByte(dat.getName());
            itemz.completeCount = (byte)MapleDataTool.getIntConvert("completeCount", dat, 0);
            for (MapleData level : dat.getChildByPath("ItemID")) {
                itemz.itemIDs.add(MapleDataTool.getIntConvert(level));
            }
            for (MapleData level : dat.getChildByPath("Effect")) {
                StructSetItem.SetItem itez = new StructSetItem.SetItem();
                itez.incPDD = MapleDataTool.getIntConvert("incPDD", level, 0);
                itez.incMDD = MapleDataTool.getIntConvert("incMDD", level, 0);
                itez.incSTR = MapleDataTool.getIntConvert("incSTR", level, 0);
                itez.incDEX = MapleDataTool.getIntConvert("incDEX", level, 0);
                itez.incINT = MapleDataTool.getIntConvert("incINT", level, 0);
                itez.incLUK = MapleDataTool.getIntConvert("incLUK", level, 0);
                itez.incACC = MapleDataTool.getIntConvert("incACC", level, 0);
                itez.incPAD = MapleDataTool.getIntConvert("incPAD", level, 0);
                itez.incMAD = MapleDataTool.getIntConvert("incMAD", level, 0);
                itez.incSpeed = MapleDataTool.getIntConvert("incSpeed", level, 0);
                itez.incMHP = MapleDataTool.getIntConvert("incMHP", level, 0);
                itez.incMMP = MapleDataTool.getIntConvert("incMMP", level, 0);
                itemz.items.put(Integer.parseInt(level.getName()), itez);
            }
            this.setItems.put(itemz.setItemID, itemz);
        }
        MapleData potsData = this.itemData.getData("ItemOption.img");
        for (MapleData dat : potsData) {
            LinkedList<StructPotentialItem> items = new LinkedList<StructPotentialItem>();
            for (MapleData level : dat.getChildByPath("level")) {
                StructPotentialItem item = new StructPotentialItem();
                item.optionType = MapleDataTool.getIntConvert("info/optionType", dat, 0);
                item.reqLevel = MapleDataTool.getIntConvert("info/reqLevel", dat, 0);
                item.face = MapleDataTool.getString("face", level, "");
                item.boss = MapleDataTool.getIntConvert("boss", level, 0) > 0;
                item.potentialID = Short.parseShort(dat.getName());
                item.attackType = (short)MapleDataTool.getIntConvert("attackType", level, 0);
                item.incMHP = (short)MapleDataTool.getIntConvert("incMHP", level, 0);
                item.incMMP = (short)MapleDataTool.getIntConvert("incMMP", level, 0);
                item.incSTR = (byte)MapleDataTool.getIntConvert("incSTR", level, 0);
                item.incDEX = (byte)MapleDataTool.getIntConvert("incDEX", level, 0);
                item.incINT = (byte)MapleDataTool.getIntConvert("incINT", level, 0);
                item.incLUK = (byte)MapleDataTool.getIntConvert("incLUK", level, 0);
                item.incACC = (byte)MapleDataTool.getIntConvert("incACC", level, 0);
                item.incEVA = (byte)MapleDataTool.getIntConvert("incEVA", level, 0);
                item.incSpeed = (byte)MapleDataTool.getIntConvert("incSpeed", level, 0);
                item.incJump = (byte)MapleDataTool.getIntConvert("incJump", level, 0);
                item.incPAD = (byte)MapleDataTool.getIntConvert("incPAD", level, 0);
                item.incMAD = (byte)MapleDataTool.getIntConvert("incMAD", level, 0);
                item.incPDD = (byte)MapleDataTool.getIntConvert("incPDD", level, 0);
                item.incMDD = (byte)MapleDataTool.getIntConvert("incMDD", level, 0);
                item.prop = (byte)MapleDataTool.getIntConvert("prop", level, 0);
                item.time = (byte)MapleDataTool.getIntConvert("time", level, 0);
                item.incSTRr = (byte)MapleDataTool.getIntConvert("incSTRr", level, 0);
                item.incDEXr = (byte)MapleDataTool.getIntConvert("incDEXr", level, 0);
                item.incINTr = (byte)MapleDataTool.getIntConvert("incINTr", level, 0);
                item.incLUKr = (byte)MapleDataTool.getIntConvert("incLUKr", level, 0);
                item.incMHPr = (byte)MapleDataTool.getIntConvert("incMHPr", level, 0);
                item.incMMPr = (byte)MapleDataTool.getIntConvert("incMMPr", level, 0);
                item.incACCr = (byte)MapleDataTool.getIntConvert("incACCr", level, 0);
                item.incEVAr = (byte)MapleDataTool.getIntConvert("incEVAr", level, 0);
                item.incPADr = (byte)MapleDataTool.getIntConvert("incPADr", level, 0);
                item.incMADr = (byte)MapleDataTool.getIntConvert("incMADr", level, 0);
                item.incPDDr = (byte)MapleDataTool.getIntConvert("incPDDr", level, 0);
                item.incMDDr = (byte)MapleDataTool.getIntConvert("incMDDr", level, 0);
                item.incCr = (byte)MapleDataTool.getIntConvert("incCr", level, 0);
                item.incDAMr = (byte)MapleDataTool.getIntConvert("incDAMr", level, 0);
                item.RecoveryHP = (byte)MapleDataTool.getIntConvert("RecoveryHP", level, 0);
                item.RecoveryMP = (byte)MapleDataTool.getIntConvert("RecoveryMP", level, 0);
                item.HP = (byte)MapleDataTool.getIntConvert("HP", level, 0);
                item.MP = (byte)MapleDataTool.getIntConvert("MP", level, 0);
                item.level = (byte)MapleDataTool.getIntConvert("level", level, 0);
                item.ignoreTargetDEF = (byte)MapleDataTool.getIntConvert("ignoreTargetDEF", level, 0);
                item.ignoreDAM = (byte)MapleDataTool.getIntConvert("ignoreDAM", level, 0);
                item.DAMreflect = (byte)MapleDataTool.getIntConvert("DAMreflect", level, 0);
                item.mpconReduce = (byte)MapleDataTool.getIntConvert("mpconReduce", level, 0);
                item.mpRestore = (byte)MapleDataTool.getIntConvert("mpRestore", level, 0);
                item.incMesoProp = (byte)MapleDataTool.getIntConvert("incMesoProp", level, 0);
                item.incRewardProp = (byte)MapleDataTool.getIntConvert("incRewardProp", level, 0);
                item.incAllskill = (byte)MapleDataTool.getIntConvert("incAllskill", level, 0);
                item.ignoreDAMr = (byte)MapleDataTool.getIntConvert("ignoreDAMr", level, 0);
                item.RecoveryUP = (byte)MapleDataTool.getIntConvert("RecoveryUP", level, 0);
                switch (item.potentialID) {
                    case 31001: 
                    case 31002: 
                    case 31003: 
                    case 31004: {
                        item.skillID = (short)(item.potentialID - 23001);
                        break;
                    }
                    default: {
                        item.skillID = 0;
                    }
                }
                items.add(item);
            }
            this.potentialCache.put(Integer.parseInt(dat.getName()), items);
        }
    }

    public final List<StructPotentialItem> getPotentialInfo(int potId) {
        return this.potentialCache.get(potId);
    }

    public final Map<Integer, List<StructPotentialItem>> getAllPotentialInfo() {
        return this.potentialCache;
    }

    public static final MapleItemInformationProvider getInstance() {
        return instance;
    }

    public final List<Pair<Integer, String>> getAllItems() {
        if (this.itemNameCache.size() != 0) {
            return this.itemNameCache;
        }
        ArrayList<Pair<Integer, String>> itemPairs = new ArrayList<Pair<Integer, String>>();
        MapleData itemsData = this.stringData.getData("Cash.img");
        for (MapleData itemFolder : itemsData.getChildren()) {
            itemPairs.add(new Pair<Integer, String>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME")));
        }
        itemsData = this.stringData.getData("Consume.img");
        for (MapleData itemFolder : itemsData.getChildren()) {
            itemPairs.add(new Pair<Integer, String>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME")));
        }
        itemsData = this.stringData.getData("Eqp.img").getChildByPath("Eqp");
        for (MapleData eqpType : itemsData.getChildren()) {
            for (MapleData itemFolder : eqpType.getChildren()) {
                itemPairs.add(new Pair<Integer, String>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME")));
            }
        }
        itemsData = this.stringData.getData("Etc.img").getChildByPath("Etc");
        for (MapleData itemFolder : itemsData.getChildren()) {
            itemPairs.add(new Pair<Integer, String>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME")));
        }
        itemsData = this.stringData.getData("Ins.img");
        for (MapleData itemFolder : itemsData.getChildren()) {
            itemPairs.add(new Pair<Integer, String>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME")));
        }
        itemsData = this.stringData.getData("Pet.img");
        for (MapleData itemFolder : itemsData.getChildren()) {
            itemPairs.add(new Pair<Integer, String>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME")));
        }
        return itemPairs;
    }

    protected final MapleData getStringData(int itemId) {
        MapleData data;
        String cat = null;
        if (itemId >= 5010000) {
            data = this.cashStringData;
        } else if (itemId >= 2000000 && itemId < 3000000) {
            data = this.consumeStringData;
        } else if (itemId >= 1142000 && itemId < 1143000 || itemId >= 1010000 && itemId < 1040000 || itemId >= 1122000 && itemId < 1123000) {
            data = this.eqpStringData;
            cat = "Accessory";
        } else if (itemId >= 1000000 && itemId < 1010000) {
            data = this.eqpStringData;
            cat = "Cap";
        } else if (itemId >= 1102000 && itemId < 1103000) {
            data = this.eqpStringData;
            cat = "Cape";
        } else if (itemId >= 1040000 && itemId < 1050000) {
            data = this.eqpStringData;
            cat = "Coat";
        } else if (itemId >= 20000 && itemId < 22000) {
            data = this.eqpStringData;
            cat = "Face";
        } else if (itemId >= 1080000 && itemId < 1090000) {
            data = this.eqpStringData;
            cat = "Glove";
        } else if (itemId >= 30000 && itemId < 32000) {
            data = this.eqpStringData;
            cat = "Hair";
        } else if (itemId >= 1050000 && itemId < 1060000) {
            data = this.eqpStringData;
            cat = "Longcoat";
        } else if (itemId >= 1060000 && itemId < 1070000) {
            data = this.eqpStringData;
            cat = "Pants";
        } else if (itemId >= 1610000 && itemId < 1660000) {
            data = this.eqpStringData;
            cat = "Mechanic";
        } else if (itemId >= 1802000 && itemId < 1810000) {
            data = this.eqpStringData;
            cat = "PetEquip";
        } else if (itemId >= 1920000 && itemId < 2000000) {
            data = this.eqpStringData;
            cat = "Dragon";
        } else if (itemId >= 1112000 && itemId < 0x111700) {
            data = this.eqpStringData;
            cat = "Ring";
        } else if (itemId >= 1092000 && itemId < 1100000) {
            data = this.eqpStringData;
            cat = "Shield";
        } else if (itemId >= 1070000 && itemId < 1080000) {
            data = this.eqpStringData;
            cat = "Shoes";
        } else if (itemId >= 1900000 && itemId < 1920000) {
            data = this.eqpStringData;
            cat = "Taming";
        } else if (itemId >= 1300000 && itemId < 1800000) {
            data = this.eqpStringData;
            cat = "Weapon";
        } else if (itemId >= 4000000 && itemId < 5000000) {
            data = this.etcStringData;
        } else if (itemId >= 3000000 && itemId < 4000000) {
            data = this.insStringData;
        } else if (itemId >= 5000000 && itemId < 5010000) {
            data = this.petStringData;
        } else {
            return null;
        }
        if (cat == null) {
            return data.getChildByPath(String.valueOf(itemId));
        }
        return data.getChildByPath("Eqp/" + cat + "/" + itemId);
    }

    protected final MapleData getItemData(int itemId) {
        MapleData ret = null;
        String idStr = "0" + String.valueOf(itemId);
        MapleDataDirectoryEntry root = this.itemData.getRoot();
        for (MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
            for (MapleDataFileEntry iFile : topDir.getFiles()) {
                if (iFile.getName().equals(idStr.substring(0, 4) + ".img")) {
                    ret = this.itemData.getData(topDir.getName() + "/" + iFile.getName());
                    if (ret == null) {
                        return null;
                    }
                    ret = ret.getChildByPath(idStr);
                    return ret;
                }
                if (!iFile.getName().equals(idStr.substring(1) + ".img")) continue;
                return this.itemData.getData(topDir.getName() + "/" + iFile.getName());
            }
        }
        root = this.equipData.getRoot();
        for (MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
            for (MapleDataFileEntry iFile : topDir.getFiles()) {
                if (!iFile.getName().equals(idStr + ".img")) continue;
                return this.equipData.getData(topDir.getName() + "/" + iFile.getName());
            }
        }
        return ret;
    }

    public final short getSlotMax(MapleClient c, int itemId) {
        if (this.slotMaxCache.containsKey(itemId)) {
            return this.slotMaxCache.get(itemId);
        }
        int ret = 0;
        MapleData item = this.getItemData(itemId);
        if (item != null) {
            MapleData smEntry = item.getChildByPath("info/slotMax");
            ret = smEntry == null ? (this.getInventoryType(itemId) == MapleInventoryType.EQUIP ? 1 : 100) : (short)MapleDataTool.getInt(smEntry);
        }
        this.slotMaxCache.put(itemId, (short)ret);
        return (short)ret;
    }

    public final int getWholePrice(int itemId) {
        if (this.wholePriceCache.containsKey(itemId)) {
            return this.wholePriceCache.get(itemId);
        }
        MapleData item = this.getItemData(itemId);
        if (item == null) {
            return -1;
        }
        int pEntry = 0;
        MapleData pData = item.getChildByPath("info/price");
        if (pData == null) {
            return -1;
        }
        pEntry = MapleDataTool.getInt(pData);
        this.wholePriceCache.put(itemId, pEntry);
        return pEntry;
    }

    public final double getPrice(int itemId) {
        if (this.priceCache.containsKey(itemId)) {
            return this.priceCache.get(itemId);
        }
        MapleData item = this.getItemData(itemId);
        if (item == null) {
            return -1.0;
        }
        double pEntry = 0.0;
        MapleData pData = item.getChildByPath("info/unitPrice");
        if (pData != null) {
            try {
                pEntry = MapleDataTool.getDouble(pData);
            }
            catch (Exception e) {
                pEntry = MapleDataTool.getIntConvert(pData);
            }
        } else {
            pData = item.getChildByPath("info/price");
            if (pData == null) {
                return -1.0;
            }
            pEntry = MapleDataTool.getIntConvert(pData);
        }
        if (itemId == 2070019 || itemId == 2330007) {
            pEntry = 1.0;
        }
        this.priceCache.put(itemId, pEntry);
        return pEntry;
    }

    public final Map<String, Byte> getItemMakeStats(int itemId) {
        if (this.itemMakeStatsCache.containsKey(itemId)) {
            return this.itemMakeStatsCache.get(itemId);
        }
        if (itemId / 10000 != 425) {
            return null;
        }
        LinkedHashMap<String, Byte> ret = new LinkedHashMap<String, Byte>();
        MapleData item = this.getItemData(itemId);
        if (item == null) {
            return null;
        }
        MapleData info = item.getChildByPath("info");
        if (info == null) {
            return null;
        }
        ret.put("incPAD", (byte)MapleDataTool.getInt("incPAD", info, 0));
        ret.put("incMAD", (byte)MapleDataTool.getInt("incMAD", info, 0));
        ret.put("incACC", (byte)MapleDataTool.getInt("incACC", info, 0));
        ret.put("incEVA", (byte)MapleDataTool.getInt("incEVA", info, 0));
        ret.put("incSpeed", (byte)MapleDataTool.getInt("incSpeed", info, 0));
        ret.put("incJump", (byte)MapleDataTool.getInt("incJump", info, 0));
        ret.put("incMaxHP", (byte)MapleDataTool.getInt("incMaxHP", info, 0));
        ret.put("incMaxMP", (byte)MapleDataTool.getInt("incMaxMP", info, 0));
        ret.put("incSTR", (byte)MapleDataTool.getInt("incSTR", info, 0));
        ret.put("incINT", (byte)MapleDataTool.getInt("incINT", info, 0));
        ret.put("incLUK", (byte)MapleDataTool.getInt("incLUK", info, 0));
        ret.put("incDEX", (byte)MapleDataTool.getInt("incDEX", info, 0));
        ret.put("randOption", (byte)MapleDataTool.getInt("randOption", info, 0));
        ret.put("randStat", (byte)MapleDataTool.getInt("randStat", info, 0));
        this.itemMakeStatsCache.put(itemId, ret);
        return ret;
    }

    private int rand(int min, int max) {
        return Math.abs(Randomizer.rand(min, max));
    }

    public Equip levelUpEquip(Equip equip, Map<String, Integer> sta) {
        Equip nEquip = (Equip)equip.copy();
        try {
            for (Map.Entry<String, Integer> stat : sta.entrySet()) {
                if (stat.getKey().equals("STRMin")) {
                    nEquip.setStr((short)(nEquip.getStr() + this.rand(stat.getValue(), sta.get("STRMax"))));
                    continue;
                }
                if (stat.getKey().equals("DEXMin")) {
                    nEquip.setDex((short)(nEquip.getDex() + this.rand(stat.getValue(), sta.get("DEXMax"))));
                    continue;
                }
                if (stat.getKey().equals("INTMin")) {
                    nEquip.setInt((short)(nEquip.getInt() + this.rand(stat.getValue(), sta.get("INTMax"))));
                    continue;
                }
                if (stat.getKey().equals("LUKMin")) {
                    nEquip.setLuk((short)(nEquip.getLuk() + this.rand(stat.getValue(), sta.get("LUKMax"))));
                    continue;
                }
                if (stat.getKey().equals("PADMin")) {
                    nEquip.setWatk((short)(nEquip.getWatk() + this.rand(stat.getValue(), sta.get("PADMax"))));
                    continue;
                }
                if (stat.getKey().equals("PDDMin")) {
                    nEquip.setWdef((short)(nEquip.getWdef() + this.rand(stat.getValue(), sta.get("PDDMax"))));
                    continue;
                }
                if (stat.getKey().equals("MADMin")) {
                    nEquip.setMatk((short)(nEquip.getMatk() + this.rand(stat.getValue(), sta.get("MADMax"))));
                    continue;
                }
                if (stat.getKey().equals("MDDMin")) {
                    nEquip.setMdef((short)(nEquip.getMdef() + this.rand(stat.getValue(), sta.get("MDDMax"))));
                    continue;
                }
                if (stat.getKey().equals("ACCMin")) {
                    nEquip.setAcc((short)(nEquip.getAcc() + this.rand(stat.getValue(), sta.get("ACCMax"))));
                    continue;
                }
                if (stat.getKey().equals("EVAMin")) {
                    nEquip.setAvoid((short)(nEquip.getAvoid() + this.rand(stat.getValue(), sta.get("EVAMax"))));
                    continue;
                }
                if (stat.getKey().equals("SpeedMin")) {
                    nEquip.setSpeed((short)(nEquip.getSpeed() + this.rand(stat.getValue(), sta.get("SpeedMax"))));
                    continue;
                }
                if (stat.getKey().equals("JumpMin")) {
                    nEquip.setJump((short)(nEquip.getJump() + this.rand(stat.getValue(), sta.get("JumpMax"))));
                    continue;
                }
                if (stat.getKey().equals("MHPMin")) {
                    nEquip.setHp((short)(nEquip.getHp() + this.rand(stat.getValue(), sta.get("MHPMax"))));
                    continue;
                }
                if (stat.getKey().equals("MMPMin")) {
                    nEquip.setMp((short)(nEquip.getMp() + this.rand(stat.getValue(), sta.get("MMPMax"))));
                    continue;
                }
                if (stat.getKey().equals("MaxHPMin")) {
                    nEquip.setHp((short)(nEquip.getHp() + this.rand(stat.getValue(), sta.get("MaxHPMax"))));
                    continue;
                }
                if (!stat.getKey().equals("MaxMPMin")) continue;
                nEquip.setMp((short)(nEquip.getMp() + this.rand(stat.getValue(), sta.get("MaxMPMax"))));
            }
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
        return nEquip;
    }

    public final Map<Integer, Map<String, Integer>> getEquipIncrements(int itemId) {
        if (this.equipIncsCache.containsKey(itemId)) {
            return this.equipIncsCache.get(itemId);
        }
        LinkedHashMap<Integer, Map<String, Integer>> ret = new LinkedHashMap<Integer, Map<String, Integer>>();
        MapleData item = this.getItemData(itemId);
        if (item == null) {
            return null;
        }
        MapleData info = item.getChildByPath("info/level/info");
        if (info == null) {
            return null;
        }
        for (MapleData dat : info.getChildren()) {
            HashMap<String, Integer> incs = new HashMap<String, Integer>();
            for (MapleData data : dat.getChildren()) {
                if (data.getName().length() <= 3) continue;
                incs.put(data.getName().substring(3), MapleDataTool.getIntConvert(data.getName(), dat, 0));
            }
            ret.put(Integer.parseInt(dat.getName()), incs);
        }
        this.equipIncsCache.put(itemId, ret);
        return ret;
    }

    public final Map<Integer, List<Integer>> getEquipSkills(int itemId) {
        if (this.equipSkillsCache.containsKey(itemId)) {
            return this.equipSkillsCache.get(itemId);
        }
        LinkedHashMap<Integer, List<Integer>> ret = new LinkedHashMap<Integer, List<Integer>>();
        MapleData item = this.getItemData(itemId);
        if (item == null) {
            return null;
        }
        MapleData info = item.getChildByPath("info/level/case");
        if (info == null) {
            return null;
        }
        for (MapleData dat : info.getChildren()) {
            for (MapleData data : dat.getChildren()) {
                if (data.getName().length() != 1) continue;
                ArrayList<Integer> adds = new ArrayList<Integer>();
                for (MapleData skil : data.getChildByPath("Skill").getChildren()) {
                    adds.add(MapleDataTool.getIntConvert("id", skil, 0));
                }
                ret.put(Integer.parseInt(data.getName()), adds);
            }
        }
        this.equipSkillsCache.put(itemId, ret);
        return ret;
    }

    public final Map<String, Integer> getEquipStats(int itemId) {
        if (this.equipStatsCache.containsKey(itemId)) {
            return this.equipStatsCache.get(itemId);
        }
        LinkedHashMap<String, Integer> ret = new LinkedHashMap<String, Integer>();
        MapleData item = this.getItemData(itemId);
        if (item == null) {
            return null;
        }
        MapleData info = item.getChildByPath("info");
        if (info == null) {
            return null;
        }
        for (MapleData data : info.getChildren()) {
            if (!data.getName().startsWith("inc")) continue;
            ret.put(data.getName().substring(3), MapleDataTool.getIntConvert(data));
        }
        ret.put("tuc", MapleDataTool.getInt("tuc", info, 0));
        ret.put("reqLevel", MapleDataTool.getInt("reqLevel", info, 0));
        ret.put("reqJob", MapleDataTool.getInt("reqJob", info, 0));
        ret.put("reqSTR", MapleDataTool.getInt("reqSTR", info, 0));
        ret.put("reqDEX", MapleDataTool.getInt("reqDEX", info, 0));
        ret.put("reqINT", MapleDataTool.getInt("reqINT", info, 0));
        ret.put("reqLUK", MapleDataTool.getInt("reqLUK", info, 0));
        ret.put("reqPOP", MapleDataTool.getInt("reqPOP", info, 0));
        ret.put("cash", MapleDataTool.getInt("cash", info, 0));
        ret.put("canLevel", info.getChildByPath("level") == null ? 0 : 1);
        ret.put("cursed", MapleDataTool.getInt("cursed", info, 0));
        ret.put("success", MapleDataTool.getInt("success", info, 0));
        ret.put("setItemID", MapleDataTool.getInt("setItemID", info, 0));
        ret.put("equipTradeBlock", MapleDataTool.getInt("equipTradeBlock", info, 0));
        ret.put("durability", MapleDataTool.getInt("durability", info, -1));
        if (GameConstants.isMagicWeapon(itemId)) {
            ret.put("elemDefault", MapleDataTool.getInt("elemDefault", info, 100));
            ret.put("incRMAS", MapleDataTool.getInt("incRMAS", info, 100));
            ret.put("incRMAF", MapleDataTool.getInt("incRMAF", info, 100));
            ret.put("incRMAL", MapleDataTool.getInt("incRMAL", info, 100));
            ret.put("incRMAI", MapleDataTool.getInt("incRMAI", info, 100));
        }
        this.equipStatsCache.put(itemId, ret);
        return ret;
    }

    public final boolean canEquip(Map<String, Integer> stats, int itemid, int level, int job, int fame, int str, int dex, int luk, int int_, int supremacy) {
        if (level + supremacy >= stats.get("reqLevel") && str >= stats.get("reqSTR") && dex >= stats.get("reqDEX") && luk >= stats.get("reqLUK") && int_ >= stats.get("reqINT")) {
            int fameReq = stats.get("reqPOP");
            return fameReq == 0 || fame >= fameReq;
        }
        return false;
    }

    public final int getReqLevel(int itemId) {
        if (this.getEquipStats(itemId) == null) {
            return 0;
        }
        return this.getEquipStats(itemId).get("reqLevel");
    }

    public final int getSlots(int itemId) {
        if (this.getEquipStats(itemId) == null) {
            return 0;
        }
        return this.getEquipStats(itemId).get("tuc");
    }

    public final int getSetItemID(int itemId) {
        if (this.getEquipStats(itemId) == null) {
            return 0;
        }
        return this.getEquipStats(itemId).get("setItemID");
    }

    public final StructSetItem getSetItem(int setItemId) {
        return this.setItems.get((byte)setItemId);
    }

    public final List<Integer> getScrollReqs(int itemId) {
        if (this.scrollReqCache.containsKey(itemId)) {
            return this.scrollReqCache.get(itemId);
        }
        ArrayList<Integer> ret = new ArrayList<Integer>();
        MapleData data = this.getItemData(itemId).getChildByPath("req");
        if (data == null) {
            return ret;
        }
        for (MapleData req : data.getChildren()) {
            ret.add(MapleDataTool.getInt(req));
        }
        this.scrollReqCache.put(itemId, ret);
        return ret;
    }

    public final IItem scrollEquipWithId(IItem equip, IItem scrollId, boolean ws, MapleCharacter chr, int vegas) {
        if (equip.getType() == 1) {
            int curse;
            int succ;
            Equip nEquip = (Equip)equip;
            Map<String, Integer> stats = this.getEquipStats(scrollId.getItemId());
            Map<String, Integer> eqstats = this.getEquipStats(equip.getItemId());
            succ = GameConstants.isTablet(scrollId.getItemId()) ? GameConstants.getSuccessTablet(scrollId.getItemId(), nEquip.getLevel()) : (GameConstants.isEquipScroll(scrollId.getItemId()) || GameConstants.isPotentialScroll(scrollId.getItemId()) ? 0 : stats.get("success"));
            curse = GameConstants.isTablet(scrollId.getItemId()) ? GameConstants.getCurseTablet(scrollId.getItemId(), nEquip.getLevel()) : (GameConstants.isEquipScroll(scrollId.getItemId()) || GameConstants.isPotentialScroll(scrollId.getItemId()) ? 0 : stats.get("cursed"));
            int success = succ + (vegas == 5610000 && succ == 10 ? 20 : (vegas == 5610001 && succ == 60 ? 30 : 0));
            if (GameConstants.isPotentialScroll(scrollId.getItemId()) || GameConstants.isEquipScroll(scrollId.getItemId()) || Randomizer.nextInt(100) <= success) {
                switch (scrollId.getItemId()) {
                    case 2049000: 
                    case 2049001: 
                    case 2049002: 
                    case 2049003: 
                    case 2049004: 
                    case 2049005: {
                        if (nEquip.getLevel() + nEquip.getUpgradeSlots() >= eqstats.get("tuc")) break;
                        nEquip.setUpgradeSlots((byte)(nEquip.getUpgradeSlots() + 1));
                        break;
                    }
                    case 2049006: 
                    case 2049007: 
                    case 2049008: {
                        if (nEquip.getLevel() + nEquip.getUpgradeSlots() >= eqstats.get("tuc")) break;
                        nEquip.setUpgradeSlots((byte)(nEquip.getUpgradeSlots() + 2));
                        break;
                    }
                    case 2040727: {
                        byte flag = nEquip.getFlag();
                        flag = (byte)(flag | ItemFlag.SPIKES.getValue());
                        nEquip.setFlag(flag);
                        break;
                    }
                    case 2041058: {
                        byte flag = nEquip.getFlag();
                        flag = (byte)(flag | ItemFlag.COLD.getValue());
                        nEquip.setFlag(flag);
                        break;
                    }
                    default: {
                        if (GameConstants.isChaosScroll(scrollId.getItemId())) {
                            int z = GameConstants.getChaosNumber(scrollId.getItemId());
                            if (nEquip.getStr() > 0) {
                                nEquip.setStr((short)(nEquip.getStr() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getDex() > 0) {
                                nEquip.setDex((short)(nEquip.getDex() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getInt() > 0) {
                                nEquip.setInt((short)(nEquip.getInt() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getLuk() > 0) {
                                nEquip.setLuk((short)(nEquip.getLuk() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getWatk() > 0) {
                                nEquip.setWatk((short)(nEquip.getWatk() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getWdef() > 0) {
                                nEquip.setWdef((short)(nEquip.getWdef() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getMatk() > 0) {
                                nEquip.setMatk((short)(nEquip.getMatk() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getMdef() > 0) {
                                nEquip.setMdef((short)(nEquip.getMdef() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getAcc() > 0) {
                                nEquip.setAcc((short)(nEquip.getAcc() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getAvoid() > 0) {
                                nEquip.setAvoid((short)(nEquip.getAvoid() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getSpeed() > 0) {
                                nEquip.setSpeed((short)(nEquip.getSpeed() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getJump() > 0) {
                                nEquip.setJump((short)(nEquip.getJump() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getHp() > 0) {
                                nEquip.setHp((short)(nEquip.getHp() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getMp() <= 0) break;
                            nEquip.setMp((short)(nEquip.getMp() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            break;
                        }
                        if (GameConstants.isEquipScroll(scrollId.getItemId())) {
                            int chanc = Math.max((scrollId.getItemId() == 2049300 ? 100 : 80) - nEquip.getEnhance() * 10, 10);
                            if (Randomizer.nextInt(100) > chanc) {
                                return null;
                            }
                            if (nEquip.getStr() > 0 || Randomizer.nextInt(50) == 1) {
                                nEquip.setStr((short)(nEquip.getStr() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getDex() > 0 || Randomizer.nextInt(50) == 1) {
                                nEquip.setDex((short)(nEquip.getDex() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getInt() > 0 || Randomizer.nextInt(50) == 1) {
                                nEquip.setInt((short)(nEquip.getInt() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getLuk() > 0 || Randomizer.nextInt(50) == 1) {
                                nEquip.setLuk((short)(nEquip.getLuk() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getWatk() > 0 && GameConstants.isWeapon(nEquip.getItemId())) {
                                nEquip.setWatk((short)(nEquip.getWatk() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getWdef() > 0 || Randomizer.nextInt(40) == 1) {
                                nEquip.setWdef((short)(nEquip.getWdef() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getMatk() > 0 && GameConstants.isWeapon(nEquip.getItemId())) {
                                nEquip.setMatk((short)(nEquip.getMatk() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getMdef() > 0 || Randomizer.nextInt(40) == 1) {
                                nEquip.setMdef((short)(nEquip.getMdef() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getAcc() > 0 || Randomizer.nextInt(20) == 1) {
                                nEquip.setAcc((short)(nEquip.getAcc() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getAvoid() > 0 || Randomizer.nextInt(20) == 1) {
                                nEquip.setAvoid((short)(nEquip.getAvoid() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getSpeed() > 0 || Randomizer.nextInt(10) == 1) {
                                nEquip.setSpeed((short)(nEquip.getSpeed() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getJump() > 0 || Randomizer.nextInt(10) == 1) {
                                nEquip.setJump((short)(nEquip.getJump() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getHp() > 0 || Randomizer.nextInt(5) == 1) {
                                nEquip.setHp((short)(nEquip.getHp() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getMp() > 0 || Randomizer.nextInt(5) == 1) {
                                nEquip.setMp((short)(nEquip.getMp() + Randomizer.nextInt(5)));
                            }
                            nEquip.setEnhance((byte)(nEquip.getEnhance() + 1));
                            break;
                        }
                        if (GameConstants.isPotentialScroll(scrollId.getItemId())) {
                            int chanc;
                            if (nEquip.getState() != 0) break;
                            int n3 = chanc = scrollId.getItemId() == 2049400 ? 90 : 70;
                            if (Randomizer.nextInt(100) > chanc) {
                                return null;
                            }
                            nEquip.resetPotential();
                            break;
                        }
                        for (Map.Entry<String, Integer> stat : stats.entrySet()) {
                            String key = stat.getKey();
                            if (key.equals("STR")) {
                                nEquip.setStr((short)(nEquip.getStr() + stat.getValue()));
                                continue;
                            }
                            if (key.equals("DEX")) {
                                nEquip.setDex((short)(nEquip.getDex() + stat.getValue()));
                                continue;
                            }
                            if (key.equals("INT")) {
                                nEquip.setInt((short)(nEquip.getInt() + stat.getValue()));
                                continue;
                            }
                            if (key.equals("LUK")) {
                                nEquip.setLuk((short)(nEquip.getLuk() + stat.getValue()));
                                continue;
                            }
                            if (key.equals("PAD")) {
                                nEquip.setWatk((short)(nEquip.getWatk() + stat.getValue()));
                                continue;
                            }
                            if (key.equals("PDD")) {
                                nEquip.setWdef((short)(nEquip.getWdef() + stat.getValue()));
                                continue;
                            }
                            if (key.equals("MAD")) {
                                nEquip.setMatk((short)(nEquip.getMatk() + stat.getValue()));
                                continue;
                            }
                            if (key.equals("MDD")) {
                                nEquip.setMdef((short)(nEquip.getMdef() + stat.getValue()));
                                continue;
                            }
                            if (key.equals("ACC")) {
                                nEquip.setAcc((short)(nEquip.getAcc() + stat.getValue()));
                                continue;
                            }
                            if (key.equals("EVA")) {
                                nEquip.setAvoid((short)(nEquip.getAvoid() + stat.getValue()));
                                continue;
                            }
                            if (key.equals("Speed")) {
                                nEquip.setSpeed((short)(nEquip.getSpeed() + stat.getValue()));
                                continue;
                            }
                            if (key.equals("Jump")) {
                                nEquip.setJump((short)(nEquip.getJump() + stat.getValue()));
                                continue;
                            }
                            if (key.equals("MHP")) {
                                nEquip.setHp((short)(nEquip.getHp() + stat.getValue()));
                                continue;
                            }
                            if (key.equals("MMP")) {
                                nEquip.setMp((short)(nEquip.getMp() + stat.getValue()));
                                continue;
                            }
                            if (key.equals("MHPr")) {
                                nEquip.setHpR((short)(nEquip.getHpR() + stat.getValue()));
                                continue;
                            }
                            if (!key.equals("MMPr")) continue;
                            nEquip.setMpR((short)(nEquip.getMpR() + stat.getValue()));
                        }
                    }
                }
                if (!(GameConstants.isCleanSlate(scrollId.getItemId()) || GameConstants.isSpecialScroll(scrollId.getItemId()) || GameConstants.isEquipScroll(scrollId.getItemId()) || GameConstants.isPotentialScroll(scrollId.getItemId()))) {
                    nEquip.setUpgradeSlots((byte)(nEquip.getUpgradeSlots() - 1));
                    nEquip.setLevel((byte)(nEquip.getLevel() + 1));
                }
            } else {
                if (!(ws || GameConstants.isCleanSlate(scrollId.getItemId()) || GameConstants.isSpecialScroll(scrollId.getItemId()) || GameConstants.isEquipScroll(scrollId.getItemId()) || GameConstants.isPotentialScroll(scrollId.getItemId()))) {
                    nEquip.setUpgradeSlots((byte)(nEquip.getUpgradeSlots() - 1));
                }
                if (Randomizer.nextInt(99) < curse) {
                    return null;
                }
            }
        }
        return equip;
    }

    public final IItem getEquipById(int equipId) {
        return this.getEquipById(equipId, -1);
    }

    public final IItem getEquipById(int equipId, int ringId) {
        Equip nEquip = new Equip(equipId, (short)0, ringId, (byte)0);
        nEquip.setQuantity((short)1);
        Map<String, Integer> stats = this.getEquipStats(equipId);
        if (stats != null) {
            for (Map.Entry<String, Integer> stat : stats.entrySet()) {
                String key = stat.getKey();
                if (key.equals("STR")) {
                    nEquip.setStr((short)stat.getValue().intValue());
                    continue;
                }
                if (key.equals("DEX")) {
                    nEquip.setDex((short)stat.getValue().intValue());
                    continue;
                }
                if (key.equals("INT")) {
                    nEquip.setInt((short)stat.getValue().intValue());
                    continue;
                }
                if (key.equals("LUK")) {
                    nEquip.setLuk((short)stat.getValue().intValue());
                    continue;
                }
                if (key.equals("PAD")) {
                    nEquip.setWatk((short)stat.getValue().intValue());
                    continue;
                }
                if (key.equals("PDD")) {
                    nEquip.setWdef((short)stat.getValue().intValue());
                    continue;
                }
                if (key.equals("MAD")) {
                    nEquip.setMatk((short)stat.getValue().intValue());
                    continue;
                }
                if (key.equals("MDD")) {
                    nEquip.setMdef((short)stat.getValue().intValue());
                    continue;
                }
                if (key.equals("ACC")) {
                    nEquip.setAcc((short)stat.getValue().intValue());
                    continue;
                }
                if (key.equals("EVA")) {
                    nEquip.setAvoid((short)stat.getValue().intValue());
                    continue;
                }
                if (key.equals("Speed")) {
                    nEquip.setSpeed((short)stat.getValue().intValue());
                    continue;
                }
                if (key.equals("Jump")) {
                    nEquip.setJump((short)stat.getValue().intValue());
                    continue;
                }
                if (key.equals("MHP")) {
                    nEquip.setHp((short)stat.getValue().intValue());
                    continue;
                }
                if (key.equals("MMP")) {
                    nEquip.setMp((short)stat.getValue().intValue());
                    continue;
                }
                if (key.equals("MHPr")) {
                    nEquip.setHpR((short)stat.getValue().intValue());
                    continue;
                }
                if (key.equals("MMPr")) {
                    nEquip.setMpR((short)stat.getValue().intValue());
                    continue;
                }
                if (key.equals("tuc")) {
                    nEquip.setUpgradeSlots(stat.getValue().byteValue());
                    continue;
                }
                if (key.equals("Craft")) {
                    nEquip.setHands(stat.getValue().shortValue());
                    continue;
                }
                if (!key.equals("durability")) continue;
                nEquip.setDurability(stat.getValue());
            }
        }
        this.equipCache.put(equipId, nEquip);
        return nEquip.copy();
    }

    private final short getRandStat(short defaultValue, int maxRange) {
        if (defaultValue == 0) {
            return 0;
        }
        int lMaxRange = (int)Math.min(Math.ceil((double)defaultValue * 0.1), (double)maxRange);
        return (short)((double)(defaultValue - lMaxRange) + Math.floor(Math.random() * (double)(lMaxRange * 2 + 1)));
    }

    public final Equip randomizeStats(Equip equip) {
        equip.setStr(this.getRandStat(equip.getStr(), 5));
        equip.setDex(this.getRandStat(equip.getDex(), 5));
        equip.setInt(this.getRandStat(equip.getInt(), 5));
        equip.setLuk(this.getRandStat(equip.getLuk(), 5));
        equip.setMatk(this.getRandStat(equip.getMatk(), 5));
        equip.setWatk(this.getRandStat(equip.getWatk(), 5));
        equip.setAcc(this.getRandStat(equip.getAcc(), 5));
        equip.setAvoid(this.getRandStat(equip.getAvoid(), 5));
        equip.setJump(this.getRandStat(equip.getJump(), 5));
        equip.setHands(this.getRandStat(equip.getHands(), 5));
        equip.setSpeed(this.getRandStat(equip.getSpeed(), 5));
        equip.setWdef(this.getRandStat(equip.getWdef(), 10));
        equip.setMdef(this.getRandStat(equip.getMdef(), 10));
        equip.setHp(this.getRandStat(equip.getHp(), 10));
        equip.setMp(this.getRandStat(equip.getMp(), 10));
        return equip;
    }

    public final MapleStatEffect getItemEffect(int itemId) {
        MapleStatEffect ret = this.itemEffects.get(itemId);
        if (ret == null) {
            MapleData item = this.getItemData(itemId);
            if (item == null) {
                return null;
            }
            ret = MapleStatEffect.loadItemEffectFromData(item.getChildByPath("spec"), itemId);
            this.itemEffects.put(itemId, ret);
        }
        return ret;
    }

    public final List<Pair<Integer, Integer>> getSummonMobs(int itemId) {
        if (this.summonMobCache.containsKey(itemId)) {
            return this.summonMobCache.get(itemId);
        }
        if (!GameConstants.isSummonSack(itemId)) {
            return null;
        }
        MapleData data = this.getItemData(itemId).getChildByPath("mob");
        if (data == null) {
            return null;
        }
        ArrayList<Pair<Integer, Integer>> mobPairs = new ArrayList<Pair<Integer, Integer>>();
        for (MapleData child : data.getChildren()) {
            mobPairs.add(new Pair<Integer, Integer>(MapleDataTool.getIntConvert("id", child), MapleDataTool.getIntConvert("prob", child)));
        }
        this.summonMobCache.put(itemId, mobPairs);
        return mobPairs;
    }

    public final int getCardMobId(int id) {
        if (id == 0) {
            return 0;
        }
        if (this.monsterBookID.containsKey(id)) {
            return this.monsterBookID.get(id);
        }
        MapleData data = this.getItemData(id);
        int monsterid = MapleDataTool.getIntConvert("info/mob", data, 0);
        if (monsterid == 0) {
            return 0;
        }
        this.monsterBookID.put(id, monsterid);
        return this.monsterBookID.get(id);
    }

    public final int getWatkForProjectile(int itemId) {
        Integer atk = this.projectileWatkCache.get(itemId);
        if (atk != null) {
            return atk;
        }
        MapleData data = this.getItemData(itemId);
        atk = MapleDataTool.getInt("info/incPAD", data, 0);
        this.projectileWatkCache.put(itemId, atk);
        return atk;
    }

    public final boolean canScroll(int scrollid, int itemid) {
        return scrollid / 100 % 100 == itemid / 10000 % 100;
    }

    public final String getName(int itemId) {
        if (this.nameCache.containsKey(itemId)) {
            return this.nameCache.get(itemId);
        }
        MapleData strings = this.getStringData(itemId);
        if (strings == null) {
            return null;
        }
        String ret = MapleDataTool.getString("name", strings, null);
        this.nameCache.put(itemId, ret);
        return ret;
    }

    public final String getDesc(int itemId) {
        if (this.descCache.containsKey(itemId)) {
            return this.descCache.get(itemId);
        }
        MapleData strings = this.getStringData(itemId);
        if (strings == null) {
            return null;
        }
        String ret = MapleDataTool.getString("desc", strings, null);
        this.descCache.put(itemId, ret);
        return ret;
    }

    public final String getMsg(int itemId) {
        if (this.msgCache.containsKey(itemId)) {
            return this.msgCache.get(itemId);
        }
        MapleData strings = this.getStringData(itemId);
        if (strings == null) {
            return null;
        }
        String ret = MapleDataTool.getString("msg", strings, null);
        this.msgCache.put(itemId, ret);
        return ret;
    }

    public final short getItemMakeLevel(int itemId) {
        if (this.itemMakeLevel.containsKey(itemId)) {
            return this.itemMakeLevel.get(itemId);
        }
        if (itemId / 10000 != 400) {
            return 0;
        }
        short lvl = (short)MapleDataTool.getIntConvert("info/lv", this.getItemData(itemId), 0);
        this.itemMakeLevel.put(itemId, lvl);
        return lvl;
    }

    public final byte isConsumeOnPickup(int itemId) {
        if (this.consumeOnPickupCache.containsKey(itemId)) {
            return this.consumeOnPickupCache.get(itemId);
        }
        MapleData data = this.getItemData(itemId);
        byte consume = (byte)MapleDataTool.getIntConvert("spec/consumeOnPickup", data, 0);
        if (consume == 0) {
            consume = (byte)MapleDataTool.getIntConvert("specEx/consumeOnPickup", data, 0);
        }
        if (consume == 1 && MapleDataTool.getIntConvert("spec/party", this.getItemData(itemId), 0) > 0) {
            consume = 2;
        }
        this.consumeOnPickupCache.put(itemId, consume);
        return consume;
    }

    public final boolean isDropRestricted(int itemId) {
        if (this.dropRestrictionCache.containsKey(itemId)) {
            return this.dropRestrictionCache.get(itemId);
        }
        MapleData data = this.getItemData(itemId);
        boolean trade = false;
        if (MapleDataTool.getIntConvert("info/tradeBlock", data, 0) == 1 || MapleDataTool.getIntConvert("info/quest", data, 0) == 1) {
            trade = true;
        }
        this.dropRestrictionCache.put(itemId, trade);
        return trade;
    }

    public final boolean isPickupRestricted(int itemId) {
        if (this.pickupRestrictionCache.containsKey(itemId)) {
            return this.pickupRestrictionCache.get(itemId);
        }
        boolean bRestricted = MapleDataTool.getIntConvert("info/only", this.getItemData(itemId), 0) == 1;
        this.pickupRestrictionCache.put(itemId, bRestricted);
        return bRestricted;
    }

    public final boolean isAccountShared(int itemId) {
        if (this.accCache.containsKey(itemId)) {
            return this.accCache.get(itemId);
        }
        boolean bRestricted = MapleDataTool.getIntConvert("info/accountSharable", this.getItemData(itemId), 0) == 1;
        this.accCache.put(itemId, bRestricted);
        return bRestricted;
    }

    public final int getStateChangeItem(int itemId) {
        if (this.stateChangeCache.containsKey(itemId)) {
            return this.stateChangeCache.get(itemId);
        }
        int triggerItem = MapleDataTool.getIntConvert("info/stateChangeItem", this.getItemData(itemId), 0);
        this.stateChangeCache.put(itemId, triggerItem);
        return triggerItem;
    }

    public final int getMeso(int itemId) {
        if (this.mesoCache.containsKey(itemId)) {
            return this.mesoCache.get(itemId);
        }
        int triggerItem = MapleDataTool.getIntConvert("info/meso", this.getItemData(itemId), 0);
        this.mesoCache.put(itemId, triggerItem);
        return triggerItem;
    }

    public final boolean isKarmaEnabled(int itemId) {
        if (this.karmaEnabledCache.containsKey(itemId)) {
            return this.karmaEnabledCache.get(itemId) == 1;
        }
        int iRestricted = MapleDataTool.getIntConvert("info/tradeAvailable", this.getItemData(itemId), 0);
        this.karmaEnabledCache.put(itemId, iRestricted);
        return iRestricted == 1;
    }

    public final boolean isPKarmaEnabled(int itemId) {
        if (this.karmaEnabledCache.containsKey(itemId)) {
            return this.karmaEnabledCache.get(itemId) == 2;
        }
        int iRestricted = MapleDataTool.getIntConvert("info/tradeAvailable", this.getItemData(itemId), 0);
        this.karmaEnabledCache.put(itemId, iRestricted);
        return iRestricted == 2;
    }

    public final boolean isPickupBlocked(int itemId) {
        if (this.blockPickupCache.containsKey(itemId)) {
            return this.blockPickupCache.get(itemId);
        }
        boolean iRestricted = MapleDataTool.getIntConvert("info/pickUpBlock", this.getItemData(itemId), 0) == 1;
        this.blockPickupCache.put(itemId, iRestricted);
        return iRestricted;
    }

    public final boolean isLogoutExpire(int itemId) {
        if (this.logoutExpireCache.containsKey(itemId)) {
            return this.logoutExpireCache.get(itemId);
        }
        boolean iRestricted = MapleDataTool.getIntConvert("info/expireOnLogout", this.getItemData(itemId), 0) == 1;
        this.logoutExpireCache.put(itemId, iRestricted);
        return iRestricted;
    }

    public final boolean cantSell(int itemId) {
        if (this.notSaleCache.containsKey(itemId)) {
            return this.notSaleCache.get(itemId);
        }
        boolean bRestricted = MapleDataTool.getIntConvert("info/notSale", this.getItemData(itemId), 0) == 1;
        this.notSaleCache.put(itemId, bRestricted);
        return bRestricted;
    }

    public final Pair<Integer, List<StructRewardItem>> getRewardItem(int itemid) {
        if (this.RewardItem.containsKey(itemid)) {
            return this.RewardItem.get(itemid);
        }
        MapleData data = this.getItemData(itemid);
        if (data == null) {
            return null;
        }
        MapleData rewards = data.getChildByPath("reward");
        if (rewards == null) {
            return null;
        }
        int totalprob = 0;
        ArrayList<StructRewardItem> all = new ArrayList<StructRewardItem>();
        for (MapleData reward : rewards) {
            StructRewardItem struct = new StructRewardItem();
            struct.itemid = MapleDataTool.getInt("item", reward, 0);
            struct.prob = (byte)MapleDataTool.getInt("prob", reward, 0);
            struct.quantity = (short)MapleDataTool.getInt("count", reward, 0);
            struct.effect = MapleDataTool.getString("Effect", reward, "");
            struct.worldmsg = MapleDataTool.getString("worldMsg", reward, null);
            struct.period = MapleDataTool.getInt("period", reward, -1);
            totalprob += struct.prob;
            all.add(struct);
        }
        Pair<Integer, List<StructRewardItem>> toreturn = new Pair<Integer, List<StructRewardItem>>(totalprob, all);
        this.RewardItem.put(itemid, toreturn);
        return toreturn;
    }

    public final Map<String, Integer> getSkillStats(int itemId) {
        if (this.SkillStatsCache.containsKey(itemId)) {
            return this.SkillStatsCache.get(itemId);
        }
        if (itemId / 10000 != 228 && itemId / 10000 != 229 && itemId / 10000 != 562) {
            return null;
        }
        MapleData item = this.getItemData(itemId);
        if (item == null) {
            return null;
        }
        MapleData info = item.getChildByPath("info");
        if (info == null) {
            return null;
        }
        LinkedHashMap<String, Integer> ret = new LinkedHashMap<String, Integer>();
        for (MapleData data : info.getChildren()) {
            if (!data.getName().startsWith("inc")) continue;
            ret.put(data.getName().substring(3), MapleDataTool.getIntConvert(data));
        }
        ret.put("masterLevel", MapleDataTool.getInt("masterLevel", info, 0));
        ret.put("reqSkillLevel", MapleDataTool.getInt("reqSkillLevel", info, 0));
        ret.put("success", MapleDataTool.getInt("success", info, 0));
        MapleData skill = info.getChildByPath("skill");
        for (int i = 0; i < skill.getChildren().size(); ++i) {
            ret.put("skillid" + i, MapleDataTool.getInt(Integer.toString(i), skill, 0));
        }
        this.SkillStatsCache.put(itemId, ret);
        return ret;
    }

    public final List<Integer> petsCanConsume(int itemId) {
        if (this.petsCanConsumeCache.get(itemId) != null) {
            return this.petsCanConsumeCache.get(itemId);
        }
        ArrayList<Integer> ret = new ArrayList<Integer>();
        MapleData data = this.getItemData(itemId);
        if (data == null || data.getChildByPath("spec") == null) {
            return ret;
        }
        int curPetId = 0;
        for (MapleData c : data.getChildByPath("spec")) {
            try {
                Integer.parseInt(c.getName());
            }
            catch (NumberFormatException e) {
                continue;
            }
            curPetId = MapleDataTool.getInt(c, 0);
            if (curPetId == 0) break;
            ret.add(curPetId);
        }
        this.petsCanConsumeCache.put(itemId, ret);
        return ret;
    }

    public final boolean isQuestItem(int itemId) {
        if (this.isQuestItemCache.containsKey(itemId)) {
            return this.isQuestItemCache.get(itemId);
        }
        boolean questItem = MapleDataTool.getIntConvert("info/quest", this.getItemData(itemId), 0) == 1;
        this.isQuestItemCache.put(itemId, questItem);
        return questItem;
    }

    public final Pair<Integer, List<Integer>> questItemInfo(int itemId) {
        if (this.questItems.containsKey(itemId)) {
            return this.questItems.get(itemId);
        }
        if (itemId / 10000 != 422 || this.getItemData(itemId) == null) {
            return null;
        }
        MapleData itemD = this.getItemData(itemId).getChildByPath("info");
        if (itemD == null || itemD.getChildByPath("consumeItem") == null) {
            return null;
        }
        ArrayList<Integer> consumeItems = new ArrayList<Integer>();
        for (MapleData consume : itemD.getChildByPath("consumeItem")) {
            consumeItems.add(MapleDataTool.getInt(consume, 0));
        }
        Pair<Integer, List<Integer>> questItem = new Pair<Integer, List<Integer>>(MapleDataTool.getIntConvert("questId", itemD, 0), consumeItems);
        this.questItems.put(itemId, questItem);
        return questItem;
    }

    public final boolean itemExists(int itemId) {
        if (this.getInventoryType(itemId) == MapleInventoryType.UNDEFINED) {
            return false;
        }
        return this.getItemData(itemId) != null;
    }

    public final void loadStyles(boolean reload) {
        if (reload) {
            this.hairList.clear();
            this.faceList.clear();
        }
        if (!this.hairList.isEmpty() || !this.faceList.isEmpty()) {
            return;
        }
        ArrayList<String> types = new ArrayList<String>();
        types.add("Hair");
        types.add("Face");
        for (String type : types) {
            for (MapleData c : this.stringData.getData("Eqp.img").getChildByPath("Eqp/" + type)) {
                try {
                    if (this.equipData.getData(type + "/" + StringUtil.getLeftPaddedStr(c.getName() + ".img", '0', 12)) == null) continue;
                    int dataid = Integer.parseInt(c.getName());
                    String name = MapleDataTool.getString("name", c, "\u7121\u540d\u7a31");
                    if (type.equals("Hair")) {
                        this.hairList.put(dataid, name);
                        continue;
                    }
                    this.faceList.put(dataid, name);
                }
                catch (Exception exception) {}
            }
        }
    }

    public boolean hairExists(int hair) {
        return this.hairList.containsKey(hair);
    }

    public boolean faceExists(int face) {
        return this.faceList.containsKey(face);
    }

    public final Map<Integer, String> getFaceList() {
        HashMap<Integer, String> list = new HashMap<Integer, String>();
        list.putAll(this.faceList);
        return list;
    }

    public final Map<Integer, String> getHairList() {
        HashMap<Integer, String> list = new HashMap<Integer, String>();
        list.putAll(this.hairList);
        return list;
    }

    public final boolean isCash(int itemId) {
        if (this.getEquipStats(itemId) == null) {
            return this.getInventoryType(itemId) == MapleInventoryType.CASH;
        }
        return this.getInventoryType(itemId) == MapleInventoryType.CASH || this.getEquipStats(itemId).get("cash") > 0;
    }

    public final MapleInventoryType getInventoryType(int itemId) {
        if (this.inventoryTypeCache.containsKey(itemId)) {
            return this.inventoryTypeCache.get(itemId);
        }
        String idStr = "0" + String.valueOf(itemId);
        MapleDataDirectoryEntry root = this.itemData.getRoot();
        for (MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
            for (MapleDataFileEntry iFile : topDir.getFiles()) {
                if (iFile.getName().equals(idStr.substring(0, 4) + ".img")) {
                    MapleInventoryType ret = MapleInventoryType.getByWZName(topDir.getName());
                    this.inventoryTypeCache.put(itemId, ret);
                    return ret;
                }
                if (!iFile.getName().equals(idStr.substring(1) + ".img")) continue;
                MapleInventoryType ret = MapleInventoryType.getByWZName(topDir.getName());
                this.inventoryTypeCache.put(itemId, ret);
                return ret;
            }
        }
        root = this.equipData.getRoot();
        for (MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
            for (MapleDataFileEntry iFile : topDir.getFiles()) {
                if (!iFile.getName().equals(idStr + ".img")) continue;
                MapleInventoryType ret = MapleInventoryType.EQUIP;
                this.inventoryTypeCache.put(itemId, ret);
                return ret;
            }
        }
        MapleInventoryType ret = MapleInventoryType.UNDEFINED;
        this.inventoryTypeCache.put(itemId, ret);
        return ret;
    }

    public short getPetFlagInfo(int itemId) {
        System.out.println("ItemID = " + itemId);
        if (this.petFlagInfo.containsKey(itemId)) {
            return this.petFlagInfo.get(itemId);
        }
        short flag = 0;
        if (itemId / 10000 != 500) {
            return flag;
        }
        MapleData item = this.getItemData(itemId);
        if (item == null) {
            return flag;
        }
        if (MapleDataTool.getIntConvert("info/pickupItem", item, 0) > 0) {
            flag = (short)(flag | 1);
        }
        if (MapleDataTool.getIntConvert("info/longRange", item, 0) > 0) {
            flag = (short)(flag | 2);
        }
        if (MapleDataTool.getIntConvert("info/pickupAll", item, 0) > 0) {
            flag = (short)(flag | 4);
        }
        if (MapleDataTool.getIntConvert("info/sweepForDrop", item, 0) > 0) {
            flag = (short)(flag | 0x10);
        }
        if (MapleDataTool.getIntConvert("info/consumeHP", item, 0) > 0) {
            flag = (short)(flag | 0x20);
        }
        if (MapleDataTool.getIntConvert("info/consumeMP", item, 0) > 0) {
            flag = (short)(flag | 0x40);
        }
        if (MapleDataTool.getIntConvert("info/autoBuff", item, 0) > 0) {
            flag = (short)(flag | 0x200);
        }
        this.petFlagInfo.put(itemId, flag);
        return flag;
    }

    public boolean isCashItem(int itemId) {
        int pEntry;
        MapleData item = this.getItemData(itemId);
        if (item == null) {
            return false;
        }
        MapleData pData = item.getChildByPath("info/cash");
        if (pData == null) {
            return false;
        }
        try {
            pEntry = MapleDataTool.getInt(pData);
        }
        catch (Exception e) {
            return false;
        }
        return pEntry == 1;
    }
}

