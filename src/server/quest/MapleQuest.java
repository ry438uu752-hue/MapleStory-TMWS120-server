/*
 * Decompiled with CFR 0.152.
 */
package server.quest;

import client.MapleCharacter;
import client.MapleQuestStatus;
import constants.GameConstants;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import scripting.NPCScriptManager;
import server.quest.MapleCustomQuest;
import server.quest.MapleQuestAction;
import server.quest.MapleQuestActionType;
import server.quest.MapleQuestRequirement;
import server.quest.MapleQuestRequirementType;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.Pair;

public class MapleQuest
implements Serializable {
    private static final long serialVersionUID = 9179541993413738569L;
    private static Map<Integer, MapleQuest> quests = new LinkedHashMap<Integer, MapleQuest>();
    protected int id;
    protected List<MapleQuestRequirement> startReqs;
    protected List<MapleQuestRequirement> completeReqs;
    protected List<MapleQuestAction> startActs;
    protected List<MapleQuestAction> completeActs;
    protected Map<String, List<Pair<String, Pair<String, Integer>>>> partyQuestInfo;
    protected Map<Integer, Integer> relevantMobs = new LinkedHashMap<Integer, Integer>();
    private boolean autoStart = false;
    private boolean autoPreComplete = false;
    private boolean repeatable = false;
    private boolean customend = false;
    private int viewMedalItem = 0;
    private int selectedSkillID = 0;
    protected String name = "";
    private static MapleDataProvider questData;
    private static MapleData actions;
    private static MapleData requirements;
    private static MapleData info;
    private static MapleData pinfo;

    protected MapleQuest(int id) {
        this.startReqs = new LinkedList<MapleQuestRequirement>();
        this.completeReqs = new LinkedList<MapleQuestRequirement>();
        this.startActs = new LinkedList<MapleQuestAction>();
        this.completeActs = new LinkedList<MapleQuestAction>();
        this.partyQuestInfo = new LinkedHashMap<String, List<Pair<String, Pair<String, Integer>>>>();
        this.id = id;
    }

    private static boolean loadQuest(MapleQuest ret, int id) throws NullPointerException {
        MapleData mapleData;
        MapleData mapleData2;
        MapleData mapleData3;
        MapleData startActData;
        List<MapleData> completeC;
        MapleData completeReqData;
        List<MapleData> startC;
        MapleData basedata1 = requirements.getChildByPath(String.valueOf(id));
        MapleData basedata2 = actions.getChildByPath(String.valueOf(id));
        if (basedata1 == null || basedata2 == null) {
            return false;
        }
        MapleData startReqData = basedata1.getChildByPath("0");
        if (startReqData != null && (startC = startReqData.getChildren()) != null && startC.size() > 0) {
            for (MapleData mapleData4 : startC) {
                MapleQuestRequirement mapleQuestRequirement;
                MapleQuestRequirementType mapleQuestRequirementType = MapleQuestRequirementType.getByWZName(mapleData4.getName());
                if (mapleQuestRequirementType.equals((Object)MapleQuestRequirementType.interval)) {
                    ret.repeatable = true;
                }
                if ((mapleQuestRequirement = new MapleQuestRequirement(ret, mapleQuestRequirementType, mapleData4)).getType().equals((Object)MapleQuestRequirementType.mob)) {
                    for (MapleData mob2 : mapleData4.getChildren()) {
                        ret.relevantMobs.put(MapleDataTool.getInt(mob2.getChildByPath("id")), MapleDataTool.getInt(mob2.getChildByPath("count"), 0));
                    }
                }
                ret.startReqs.add(mapleQuestRequirement);
            }
        }
        if ((completeReqData = basedata1.getChildByPath("1")) != null && (completeC = completeReqData.getChildren()) != null && completeC.size() > 0) {
            for (MapleData mapleData5 : completeC) {
                MapleQuestRequirement mapleQuestRequirement = new MapleQuestRequirement(ret, MapleQuestRequirementType.getByWZName(mapleData5.getName()), mapleData5);
                if (mapleQuestRequirement.getType().equals((Object)MapleQuestRequirementType.mob)) {
                    for (MapleData mob2 : mapleData5.getChildren()) {
                        ret.relevantMobs.put(MapleDataTool.getInt(mob2.getChildByPath("id")), MapleDataTool.getInt(mob2.getChildByPath("count"), 0));
                    }
                } else if (mapleQuestRequirement.getType().equals((Object)MapleQuestRequirementType.endscript)) {
                    ret.customend = true;
                }
                ret.completeReqs.add(mapleQuestRequirement);
            }
        }
        if ((startActData = basedata2.getChildByPath("0")) != null) {
            List<MapleData> list = startActData.getChildren();
            for (MapleData mapleData6 : list) {
                ret.startActs.add(new MapleQuestAction(MapleQuestActionType.getByWZName(mapleData6.getName()), mapleData6, ret));
            }
        }
        if ((mapleData3 = basedata2.getChildByPath("1")) != null) {
            List<MapleData> list = mapleData3.getChildren();
            for (MapleData completeAct : list) {
                ret.completeActs.add(new MapleQuestAction(MapleQuestActionType.getByWZName(completeAct.getName()), completeAct, ret));
            }
        }
        if ((mapleData2 = info.getChildByPath(String.valueOf(id))) != null) {
            ret.name = MapleDataTool.getString("name", mapleData2, "");
            ret.autoStart = MapleDataTool.getInt("autoStart", mapleData2, 0) == 1;
            ret.autoPreComplete = MapleDataTool.getInt("autoPreComplete", mapleData2, 0) == 1;
            ret.viewMedalItem = MapleDataTool.getInt("viewMedalItem", mapleData2, 0);
            ret.selectedSkillID = MapleDataTool.getInt("selectedSkillID", mapleData2, 0);
        }
        if ((mapleData = pinfo.getChildByPath(String.valueOf(id))) != null) {
            for (MapleData d : mapleData.getChildByPath("rank")) {
                ArrayList<Pair<String, Pair<String, Integer>>> pInfo = new ArrayList<Pair<String, Pair<String, Integer>>>();
                for (MapleData c : d) {
                    for (MapleData b : c) {
                        pInfo.add(new Pair<String, Pair<String, Integer>>(c.getName(), new Pair<String, Integer>(b.getName(), MapleDataTool.getInt(b, 0))));
                    }
                }
                ret.partyQuestInfo.put(d.getName(), pInfo);
            }
        }
        return true;
    }

    public List<Pair<String, Pair<String, Integer>>> getInfoByRank(String rank) {
        return this.partyQuestInfo.get(rank);
    }

    public final int getSkillID() {
        return this.selectedSkillID;
    }

    public final String getName() {
        return this.name;
    }

    public static void initQuests() {
        questData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Quest.wz"));
        actions = questData.getData("Act.img");
        requirements = questData.getData("Check.img");
        info = questData.getData("QuestInfo.img");
        pinfo = questData.getData("PQuest.img");
    }

    public static void clearQuests() {
        quests.clear();
        MapleQuest.initQuests();
    }

    public static MapleQuest getInstance(int id) {
        MapleQuest ret = quests.get(id);
        if (ret == null) {
            ret = new MapleQuest(id);
            try {
                if (GameConstants.isCustomQuest(id) || !MapleQuest.loadQuest(ret, id)) {
                    ret = new MapleCustomQuest(id);
                }
                quests.put(id, ret);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                FileoutputUtil.outputFileError("Logs/Log_Script_Except.rtf", ex);
                FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Caused by questID " + id);
                System.out.println("Caused by questID " + id);
                return new MapleCustomQuest(id);
            }
        }
        return ret;
    }

    public boolean canStart(MapleCharacter c, Integer npcid) {
        if (!(c.getQuest(this).getStatus() == 0 || c.getQuest(this).getStatus() == 2 && this.repeatable)) {
            return false;
        }
        for (MapleQuestRequirement r : this.startReqs) {
            if (r.check(c, npcid)) continue;
            return false;
        }
        return true;
    }

    public boolean canComplete(MapleCharacter c, Integer npcid) {
        if (c.getQuest(this).getStatus() != 1) {
            return false;
        }
        for (MapleQuestRequirement r : this.completeReqs) {
            if (r.check(c, npcid)) continue;
            return false;
        }
        return true;
    }

    public final void RestoreLostItem(MapleCharacter c, int itemid) {
        for (MapleQuestAction a : this.startActs) {
            if (a.RestoreLostItem(c, itemid)) break;
        }
    }

    public void start(MapleCharacter c, int npc) {
        if ((this.autoStart || this.checkNPCOnMap(c, npc)) && this.canStart(c, npc)) {
            for (MapleQuestAction a : this.startActs) {
                if (a.checkEnd(c, null)) continue;
                return;
            }
            for (MapleQuestAction a : this.startActs) {
                a.runStart(c, null);
            }
            if (!this.customend) {
                this.forceStart(c, npc, null);
            } else {
                NPCScriptManager.getInstance().endQuest(c.getClient(), npc, this.getId(), true);
            }
        }
    }

    public void complete(MapleCharacter c, int npc) {
        this.complete(c, npc, null);
    }

    public void complete(MapleCharacter c, int npc, Integer selection) {
        if ((this.autoPreComplete || this.checkNPCOnMap(c, npc)) && this.canComplete(c, npc)) {
            for (MapleQuestAction a : this.completeActs) {
                if (a.checkEnd(c, selection)) continue;
                return;
            }
            this.forceComplete(c, npc);
            for (MapleQuestAction a : this.completeActs) {
                a.runEnd(c, selection);
            }
            c.getClient().getSession().write((Object)MaplePacketCreator.showSpecialEffect(10));
            c.getMap().broadcastMessage(c, MaplePacketCreator.showSpecialEffect(c.getId(), 10), false);
        }
    }

    public void forfeit(MapleCharacter c) {
        if (c.getQuest(this).getStatus() != 1) {
            return;
        }
        MapleQuestStatus oldStatus = c.getQuest(this);
        MapleQuestStatus newStatus = new MapleQuestStatus(this, (byte)0);
        newStatus.setForfeited(oldStatus.getForfeited() + 1);
        newStatus.setCompletionTime(oldStatus.getCompletionTime());
        c.updateQuest(newStatus);
    }

    public void forceStart(MapleCharacter c, int npc, String customData) {
        MapleQuestStatus newStatus = new MapleQuestStatus(this, (byte)1, npc);
        newStatus.setForfeited(c.getQuest(this).getForfeited());
        newStatus.setCompletionTime(c.getQuest(this).getCompletionTime());
        newStatus.setCustomData(customData);
        c.updateQuest(newStatus);
    }

    public void forceComplete(MapleCharacter c, int npc) {
        MapleQuestStatus newStatus = new MapleQuestStatus(this, (byte)2, npc);
        newStatus.setForfeited(c.getQuest(this).getForfeited());
        c.updateQuest(newStatus);
    }

    public int getId() {
        return this.id;
    }

    public Map<Integer, Integer> getRelevantMobs() {
        return this.relevantMobs;
    }

    private boolean checkNPCOnMap(MapleCharacter player, int npcid) {
        return GameConstants.isEvan(player.getJob()) && npcid == 1013000 || player.getMap() != null && player.getMap().containsNPC(npcid);
    }

    public int getMedalItem() {
        return this.viewMedalItem;
    }

    public static enum MedalQuest {
        \u65b0\u624b\u5192\u96aa\u5bb6(29005, 29015, 15, new int[]{104000000, 0x6331111, 100000006, 104020000, 100000000, 100010000, 100040000, 100040100, 101010103, 101020000, 101000000, 102000000, 101030104, 101030406, 102020300, 103000000, 102050000, 103010001, 103030200, 110000000}),
        ElNath(29006, 29012, 50, new int[]{200000000, 200010100, 200010300, 200080000, 200080100, 211000000, 211030000, 211040300, 211040400, 211040401}),
        LudusLake(29007, 29012, 40, new int[]{222000000, 222010400, 222020000, 220000000, 220020300, 220040200, 221020701, 221000000, 221030600, 221040400}),
        Underwater(29008, 29012, 40, new int[]{230000000, 230010400, 230010200, 230010201, 230020000, 230020201, 230030100, 230040000, 230040200, 230040400}),
        MuLung(29009, 29012, 50, new int[]{251000000, 251010200, 251010402, 251010500, 250010500, 250010504, 250000000, 250010300, 250010304, 250020300}),
        NihalDesert(29010, 29012, 70, new int[]{261030000, 261020401, 261020000, 261010100, 261000000, 260020700, 260020300, 260000000, 260010600, 260010300}),
        MinarForest(29011, 29012, 70, new int[]{240000000, 240010200, 240010800, 240020401, 240020101, 240030000, 240040400, 240040511, 240040521, 240050000}),
        Sleepywood(29014, 29015, 50, new int[]{105040300, 105070001, 105040305, 105090200, 105090300, 105090301, 105090312, 105090500, 105090900, 105080000});

        public int questid;
        public int level;
        public int lquestid;
        public int[] maps;

        private MedalQuest(int questid, int lquestid, int level, int[] maps) {
            this.questid = questid;
            this.level = level;
            this.lquestid = lquestid;
            this.maps = maps;
        }
    }
}

