/*
 * Decompiled with CFR 0.152.
 */
package server.quest;

import client.ISkill;
import client.MapleCharacter;
import client.MapleQuestStatus;
import client.SkillFactory;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import constants.GameConstants;
import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuest;
import server.quest.MapleQuestRequirementType;
import tools.Pair;

public class MapleQuestRequirement
implements Serializable {
    private static final long serialVersionUID = 9179541993413738569L;
    private MapleQuest quest;
    private MapleQuestRequirementType type;
    private int intStore;
    private String stringStore;
    private List<Pair<Integer, Integer>> dataStore;

    public MapleQuestRequirement(MapleQuest quest, MapleQuestRequirementType type, MapleData data) {
        this.type = type;
        this.quest = quest;
        switch (type) {
            case job: {
                List<MapleData> child = data.getChildren();
                this.dataStore = new LinkedList<Pair<Integer, Integer>>();
                for (int i = 0; i < child.size(); ++i) {
                    this.dataStore.add(new Pair<Integer, Integer>(i, MapleDataTool.getInt(child.get(i), -1)));
                }
                break;
            }
            case skill: {
                List<MapleData> child = data.getChildren();
                this.dataStore = new LinkedList<Pair<Integer, Integer>>();
                for (int i = 0; i < child.size(); ++i) {
                    MapleData childdata = child.get(i);
                    this.dataStore.add(new Pair<Integer, Integer>(MapleDataTool.getInt(childdata.getChildByPath("id"), 0), MapleDataTool.getInt(childdata.getChildByPath("acquire"), 0)));
                }
                break;
            }
            case quest: {
                List<MapleData> child = data.getChildren();
                this.dataStore = new LinkedList<Pair<Integer, Integer>>();
                for (int i = 0; i < child.size(); ++i) {
                    MapleData childdata = child.get(i);
                    this.dataStore.add(new Pair<Integer, Integer>(MapleDataTool.getInt(childdata.getChildByPath("id")), MapleDataTool.getInt(childdata.getChildByPath("state"), 0)));
                }
                break;
            }
            case item: {
                List<MapleData> child = data.getChildren();
                this.dataStore = new LinkedList<Pair<Integer, Integer>>();
                for (int i = 0; i < child.size(); ++i) {
                    MapleData childdata = child.get(i);
                    this.dataStore.add(new Pair<Integer, Integer>(MapleDataTool.getInt(childdata.getChildByPath("id")), MapleDataTool.getInt(childdata.getChildByPath("count"), 0)));
                }
                break;
            }
            case pettamenessmin: 
            case npc: 
            case questComplete: 
            case pop: 
            case interval: 
            case mbmin: 
            case lvmax: 
            case lvmin: {
                this.intStore = MapleDataTool.getInt(data, -1);
                break;
            }
            case end: {
                this.stringStore = MapleDataTool.getString(data, null);
                break;
            }
            case mob: {
                List<MapleData> child = data.getChildren();
                this.dataStore = new LinkedList<Pair<Integer, Integer>>();
                for (int i = 0; i < child.size(); ++i) {
                    MapleData childdata = child.get(i);
                    this.dataStore.add(new Pair<Integer, Integer>(MapleDataTool.getInt(childdata.getChildByPath("id"), 0), MapleDataTool.getInt(childdata.getChildByPath("count"), 0)));
                }
                break;
            }
            case fieldEnter: {
                MapleData zeroField = data.getChildByPath("0");
                if (zeroField != null) {
                    this.intStore = MapleDataTool.getInt(zeroField);
                    break;
                }
                this.intStore = -1;
                break;
            }
            case mbcard: {
                List<MapleData> child = data.getChildren();
                this.dataStore = new LinkedList<Pair<Integer, Integer>>();
                for (int i = 0; i < child.size(); ++i) {
                    MapleData childdata = child.get(i);
                    this.dataStore.add(new Pair<Integer, Integer>(MapleDataTool.getInt(childdata.getChildByPath("id"), 0), MapleDataTool.getInt(childdata.getChildByPath("min"), 0)));
                }
                break;
            }
            case pet: {
                this.dataStore = new LinkedList<Pair<Integer, Integer>>();
                for (MapleData child : data) {
                    this.dataStore.add(new Pair<Integer, Integer>(-1, MapleDataTool.getInt("id", child, 0)));
                }
                break;
            }
        }
    }

    public boolean check(MapleCharacter c, Integer npcid) {
        switch (this.type) {
            case job: {
                for (Pair<Integer, Integer> a : this.dataStore) {
                    if (a.getRight().intValue() != c.getJob() && !c.isGM()) continue;
                    return true;
                }
                return false;
            }
            case skill: {
                for (Pair<Integer, Integer> a : this.dataStore) {
                    boolean acquire = a.getRight() > 0;
                    int skill = a.getLeft();
                    ISkill skil = SkillFactory.getSkill(skill);
                    if (!(acquire ? (skil.isFourthJob() ? c.getMasterLevel(skil) == 0 : c.getSkillLevel(skil) == 0) : c.getSkillLevel(skil) > 0 || c.getMasterLevel(skil) > 0)) continue;
                    return false;
                }
                return true;
            }
            case quest: {
                for (Pair<Integer, Integer> a : this.dataStore) {
                    MapleQuestStatus q = c.getQuest(MapleQuest.getInstance(a.getLeft()));
                    int state = a.getRight();
                    if (state == 0 || q == null && state == 0 || q != null && q.getStatus() == state) continue;
                    return false;
                }
                return true;
            }
            case item: {
                for (Pair<Integer, Integer> a : this.dataStore) {
                    int itemId = a.getLeft();
                    int quantity = 0;
                    MapleInventoryType iType = GameConstants.getInventoryType(itemId);
                    for (IItem item : c.getInventory(iType).listById(itemId)) {
                        quantity = (short)(quantity + item.getQuantity());
                    }
                    int count = a.getRight();
                    if (quantity >= count && (count > 0 || quantity <= 0)) continue;
                    return false;
                }
                return true;
            }
            case lvmin: {
                return c.getLevel() >= this.intStore;
            }
            case lvmax: {
                return c.getLevel() <= this.intStore;
            }
            case end: {
                String timeStr = this.stringStore;
                Calendar cal = Calendar.getInstance();
                cal.set(Integer.parseInt(timeStr.substring(0, 4)), Integer.parseInt(timeStr.substring(4, 6)), Integer.parseInt(timeStr.substring(6, 8)), Integer.parseInt(timeStr.substring(8, 10)), 0);
                return cal.getTimeInMillis() >= System.currentTimeMillis();
            }
            case mob: {
                for (Pair<Integer, Integer> a : this.dataStore) {
                    int mobId = a.getLeft();
                    int killReq = a.getRight();
                    if (c.getQuest(this.quest).getMobKills(mobId) >= killReq) continue;
                    return false;
                }
                return true;
            }
            case npc: {
                return npcid == null || npcid == this.intStore;
            }
            case fieldEnter: {
                if (this.intStore != -1) {
                    return this.intStore == c.getMapId();
                }
                return false;
            }
            case mbmin: {
                return c.getMonsterBook().getTotalCards() >= this.intStore;
            }
            case mbcard: {
                for (Pair<Integer, Integer> a : this.dataStore) {
                    int cardId = a.getLeft();
                    int killReq = a.getRight();
                    if (c.getMonsterBook().getLevelByCard(cardId) >= killReq) continue;
                    return false;
                }
                return true;
            }
            case pop: {
                return c.getFame() <= this.intStore;
            }
            case questComplete: {
                return c.getNumQuest() >= this.intStore;
            }
            case interval: {
                return c.getQuest(this.quest).getStatus() != 2 || c.getQuest(this.quest).getCompletionTime() <= System.currentTimeMillis() - (long)(this.intStore * 60) * 1000L;
            }
            case pet: {
                for (Pair<Integer, Integer> a : this.dataStore) {
                    if (c.getPetById(a.getRight()) != -1) continue;
                    return false;
                }
                return true;
            }
            case pettamenessmin: {
                for (MaplePet pet : c.getPets()) {
                    if (!pet.getSummoned() || pet.getCloseness() < this.intStore) continue;
                    return true;
                }
                return false;
            }
        }
        return true;
    }

    public MapleQuestRequirementType getType() {
        return this.type;
    }

    public String toString() {
        return this.type.toString();
    }
}

