/*
 * Decompiled with CFR 0.152.
 */
package server.quest;

import client.ISkill;
import client.MapleCharacter;
import client.MapleQuestStatus;
import client.MapleStat;
import client.SkillFactory;
import client.inventory.InventoryException;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import provider.MapleData;
import provider.MapleDataTool;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.quest.MapleQuest;
import server.quest.MapleQuestActionType;
import tools.MaplePacketCreator;

public class MapleQuestAction
implements Serializable {
    private static final long serialVersionUID = 9179541993413738569L;
    private MapleQuestActionType type;
    private MapleData data;
    private MapleQuest quest;

    public MapleQuestAction(MapleQuestActionType type, MapleData data, MapleQuest quest) {
        this.type = type;
        this.data = data;
        this.quest = quest;
    }

    private static boolean canGetItem(MapleData item, MapleCharacter c) {
        int gender;
        if (item.getChildByPath("gender") != null && (gender = MapleDataTool.getInt(item.getChildByPath("gender"))) != 2 && gender != c.getGender()) {
            return false;
        }
        if (item.getChildByPath("job") != null) {
            int job = MapleDataTool.getInt(item.getChildByPath("job"));
            List<Integer> code = MapleQuestAction.getJobBy5ByteEncoding(job);
            boolean jobFound = false;
            for (int codec : code) {
                if (codec / 100 != c.getJob() / 100) continue;
                jobFound = true;
                break;
            }
            if (!jobFound && item.getChildByPath("jobEx") != null) {
                int jobEx = MapleDataTool.getInt(item.getChildByPath("jobEx"));
                List<Integer> codeEx = MapleQuestAction.getJobBy5ByteEncoding(jobEx);
                for (int codec : codeEx) {
                    if (codec / 100 != c.getJob() / 100) continue;
                    jobFound = true;
                    break;
                }
            }
            return jobFound;
        }
        return true;
    }

    public final boolean RestoreLostItem(MapleCharacter c, int itemid) {
        if (this.type == MapleQuestActionType.item) {
            for (MapleData iEntry : this.data.getChildren()) {
                int retitem = MapleDataTool.getInt(iEntry.getChildByPath("id"), -1);
                if (retitem != itemid) continue;
                if (!c.haveItem(retitem, 1, true, false)) {
                    MapleInventoryManipulator.addById(c.getClient(), retitem, (short)1);
                }
                return true;
            }
        }
        return false;
    }

    public void runStart(MapleCharacter c, Integer extSelection) {
        switch (this.type) {
            case exp: {
                MapleQuestStatus status = c.getQuest(this.quest);
                if (status.getForfeited() > 0) break;
                c.gainExp(MapleDataTool.getInt(this.data, 0) * GameConstants.getExpRate_Quest(c.getLevel()), true, true, true);
                break;
            }
            case item: {
                HashMap<Integer, Integer> props = new HashMap<Integer, Integer>();
                for (MapleData iEntry : this.data.getChildren()) {
                    MapleData prop = iEntry.getChildByPath("prop");
                    if (prop == null || MapleDataTool.getInt(prop) == -1 || !MapleQuestAction.canGetItem(iEntry, c)) continue;
                    for (int i = 0; i < MapleDataTool.getInt(iEntry.getChildByPath("prop")); ++i) {
                        props.put(props.size(), MapleDataTool.getInt(iEntry.getChildByPath("id")));
                    }
                }
                int selection = 0;
                int extNum = 0;
                if (props.size() > 0) {
                    selection = (Integer)props.get(Randomizer.nextInt(props.size()));
                }
                for (MapleData iEntry : this.data.getChildren()) {
                    if (!MapleQuestAction.canGetItem(iEntry, c)) continue;
                    int id = MapleDataTool.getInt(iEntry.getChildByPath("id"), -1);
                    if (iEntry.getChildByPath("prop") != null && (MapleDataTool.getInt(iEntry.getChildByPath("prop")) != -1 ? id != selection : extSelection != extNum++)) continue;
                    short count = (short)MapleDataTool.getInt(iEntry.getChildByPath("count"), 1);
                    if (count < 0) {
                        try {
                            MapleInventoryManipulator.removeById(c.getClient(), GameConstants.getInventoryType(id), id, count * -1, true, false);
                        }
                        catch (InventoryException ie) {
                            System.err.println("[h4x] Completing a quest without meeting the requirements" + ie);
                        }
                        c.getClient().getSession().write((Object)MaplePacketCreator.getShowItemGain(id, count, true));
                        continue;
                    }
                    int period = MapleDataTool.getInt(iEntry.getChildByPath("period"), 0) / 1440;
                    String name = MapleItemInformationProvider.getInstance().getName(id);
                    if (id / 10000 == 114 && name != null && name.length() > 0) {
                        String msg = "\u4f60\u5df2\u7372\u5f97\u7a31\u865f <" + name + ">";
                        c.dropMessage(-1, msg);
                        c.dropMessage(5, msg);
                    }
                    MapleInventoryManipulator.addById(c.getClient(), id, count, "", null, period);
                    c.getClient().getSession().write((Object)MaplePacketCreator.getShowItemGain(id, count, true));
                }
                break;
            }
            case nextQuest: {
                MapleQuestStatus status = c.getQuest(this.quest);
                if (status.getForfeited() > 0) break;
                c.getClient().getSession().write((Object)MaplePacketCreator.updateQuestFinish(this.quest.getId(), status.getNpc(), MapleDataTool.getInt(this.data)));
                break;
            }
            case money: {
                MapleQuestStatus status = c.getQuest(this.quest);
                if (status.getForfeited() > 0) break;
                c.gainMeso(MapleDataTool.getInt(this.data, 0), true, false, true);
                break;
            }
            case quest: {
                for (MapleData qEntry : this.data) {
                    c.updateQuest(new MapleQuestStatus(MapleQuest.getInstance(MapleDataTool.getInt(qEntry.getChildByPath("id"))), (byte)MapleDataTool.getInt(qEntry.getChildByPath("state"), 0)));
                }
                break;
            }
            case skill: {
                block18: for (MapleData sEntry : this.data) {
                    int skillid = MapleDataTool.getInt(sEntry.getChildByPath("id"));
                    int skillLevel = MapleDataTool.getInt(sEntry.getChildByPath("skillLevel"), 0);
                    int masterLevel = MapleDataTool.getInt(sEntry.getChildByPath("masterLevel"), 0);
                    ISkill skillObject = SkillFactory.getSkill(skillid);
                    for (MapleData applicableJob : sEntry.getChildByPath("job")) {
                        if (!skillObject.isBeginnerSkill() && c.getJob() != MapleDataTool.getInt(applicableJob)) continue;
                        c.changeSkillLevel(skillObject, (byte)Math.max(skillLevel, c.getSkillLevel(skillObject)), (byte)Math.max(masterLevel, c.getMasterLevel(skillObject)));
                        continue block18;
                    }
                }
                break;
            }
            case pop: {
                MapleQuestStatus status = c.getQuest(this.quest);
                if (status.getForfeited() > 0) break;
                int fameGain = MapleDataTool.getInt(this.data, 0);
                c.addFame(fameGain);
                c.updateSingleStat(MapleStat.FAME, c.getFame());
                c.getClient().getSession().write((Object)MaplePacketCreator.getShowFameGain(fameGain));
                break;
            }
            case buffItemID: {
                int tobuff;
                MapleQuestStatus status = c.getQuest(this.quest);
                if (status.getForfeited() > 0 || (tobuff = MapleDataTool.getInt(this.data, -1)) == -1) break;
                MapleItemInformationProvider.getInstance().getItemEffect(tobuff).applyTo(c);
                break;
            }
            case infoNumber: {
                break;
            }
            case sp: {
                MapleQuestStatus status = c.getQuest(this.quest);
                if (status.getForfeited() > 0) break;
                for (MapleData iEntry : this.data.getChildren()) {
                    int sp_val = MapleDataTool.getInt(iEntry.getChildByPath("sp_value"), 0);
                    if (iEntry.getChildByPath("job") != null) {
                        int finalJob = 0;
                        for (MapleData jEntry : iEntry.getChildByPath("job").getChildren()) {
                            int job_val = MapleDataTool.getInt(jEntry, 0);
                            if (c.getJob() < job_val || job_val <= finalJob) continue;
                            finalJob = job_val;
                        }
                        if (finalJob == 0) {
                            c.gainSP(sp_val);
                            continue;
                        }
                        c.gainSP(sp_val, GameConstants.getSkillBook(finalJob));
                        continue;
                    }
                    c.gainSP(sp_val);
                }
                break;
            }
        }
    }

    public boolean checkEnd(MapleCharacter c, Integer extSelection) {
        switch (this.type) {
            case item: {
                HashMap<Integer, Integer> props = new HashMap<Integer, Integer>();
                for (MapleData iEntry : this.data.getChildren()) {
                    MapleData prop = iEntry.getChildByPath("prop");
                    if (prop == null || MapleDataTool.getInt(prop) == -1 || !MapleQuestAction.canGetItem(iEntry, c)) continue;
                    for (int i = 0; i < MapleDataTool.getInt(iEntry.getChildByPath("prop")); ++i) {
                        props.put(props.size(), MapleDataTool.getInt(iEntry.getChildByPath("id")));
                    }
                }
                int selection = 0;
                int extNum = 0;
                if (props.size() > 0) {
                    selection = (Integer)props.get(Randomizer.nextInt(props.size()));
                }
                short eq = 0;
                short use = 0;
                short setup = 0;
                short etc = 0;
                short cash = 0;
                for (MapleData iEntry : this.data.getChildren()) {
                    if (!MapleQuestAction.canGetItem(iEntry, c)) continue;
                    int id = MapleDataTool.getInt(iEntry.getChildByPath("id"), -1);
                    if (iEntry.getChildByPath("prop") != null && (MapleDataTool.getInt(iEntry.getChildByPath("prop")) != -1 ? id != selection : extSelection != extNum++)) continue;
                    short count = (short)MapleDataTool.getInt(iEntry.getChildByPath("count"), 1);
                    if (count < 0) {
                        if (c.haveItem(id, count, false, true)) continue;
                        c.dropMessage(1, "You are short of some item to complete quest.");
                        return false;
                    }
                    if (MapleItemInformationProvider.getInstance().isPickupRestricted(id) && c.haveItem(id, 1, true, false)) {
                        c.dropMessage(1, "You have this item already: " + MapleItemInformationProvider.getInstance().getName(id));
                        return false;
                    }
                    switch (GameConstants.getInventoryType(id)) {
                        case EQUIP: {
                            eq = (byte)(eq + 1);
                            break;
                        }
                        case USE: {
                            use = (byte)(use + 1);
                            break;
                        }
                        case SETUP: {
                            setup = (byte)(setup + 1);
                            break;
                        }
                        case ETC: {
                            etc = (byte)(etc + 1);
                            break;
                        }
                        case CASH: {
                            cash = (byte)(cash + 1);
                        }
                    }
                }
                if (c.getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < eq) {
                    c.dropMessage(1, "Please make space for your Equip inventory.");
                    return false;
                }
                if (c.getInventory(MapleInventoryType.USE).getNumFreeSlot() < use) {
                    c.dropMessage(1, "Please make space for your Use inventory.");
                    return false;
                }
                if (c.getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < setup) {
                    c.dropMessage(1, "Please make space for your Setup inventory.");
                    return false;
                }
                if (c.getInventory(MapleInventoryType.ETC).getNumFreeSlot() < etc) {
                    c.dropMessage(1, "Please make space for your Etc inventory.");
                    return false;
                }
                if (c.getInventory(MapleInventoryType.CASH).getNumFreeSlot() < cash) {
                    c.dropMessage(1, "Please make space for your Cash inventory.");
                    return false;
                }
                return true;
            }
            case money: {
                int meso = MapleDataTool.getInt(this.data, 0);
                if (c.getMeso() + meso < 0) {
                    c.dropMessage(1, "Meso exceed the max amount, 2147483647.");
                    return false;
                }
                if (meso < 0 && c.getMeso() < Math.abs(meso)) {
                    c.dropMessage(1, "Insufficient meso.");
                    return false;
                }
                return true;
            }
        }
        return true;
    }

    public void runEnd(MapleCharacter c, Integer extSelection) {
        switch (this.type) {
            case exp: {
                c.gainExp(MapleDataTool.getInt(this.data, 0) * GameConstants.getExpRate_Quest(c.getLevel()), true, true, true);
                break;
            }
            case item: {
                HashMap<Integer, Integer> props = new HashMap<Integer, Integer>();
                for (MapleData iEntry : this.data.getChildren()) {
                    MapleData prop = iEntry.getChildByPath("prop");
                    if (prop == null || MapleDataTool.getInt(prop) == -1 || !MapleQuestAction.canGetItem(iEntry, c)) continue;
                    for (int i = 0; i < MapleDataTool.getInt(iEntry.getChildByPath("prop")); ++i) {
                        props.put(props.size(), MapleDataTool.getInt(iEntry.getChildByPath("id")));
                    }
                }
                int selection = 0;
                int extNum = 0;
                if (props.size() > 0) {
                    selection = (Integer)props.get(Randomizer.nextInt(props.size()));
                }
                for (MapleData iEntry : this.data.getChildren()) {
                    if (!MapleQuestAction.canGetItem(iEntry, c)) continue;
                    int id = MapleDataTool.getInt(iEntry.getChildByPath("id"), -1);
                    if (iEntry.getChildByPath("prop") != null && (MapleDataTool.getInt(iEntry.getChildByPath("prop")) != -1 ? id != selection : extSelection != extNum++)) continue;
                    short count = (short)MapleDataTool.getInt(iEntry.getChildByPath("count"), 1);
                    if (count < 0) {
                        MapleInventoryManipulator.removeById(c.getClient(), GameConstants.getInventoryType(id), id, count * -1, true, false);
                        c.getClient().getSession().write((Object)MaplePacketCreator.getShowItemGain(id, count, true));
                        continue;
                    }
                    int period = MapleDataTool.getInt(iEntry.getChildByPath("period"), 0) / 1440;
                    String name = MapleItemInformationProvider.getInstance().getName(id);
                    if (id / 10000 == 114 && name != null && name.length() > 0) {
                        String msg = "You have attained title <" + name + ">";
                        c.dropMessage(-1, msg);
                        c.dropMessage(5, msg);
                    }
                    MapleInventoryManipulator.addById(c.getClient(), id, count, "", null, period);
                    c.getClient().getSession().write((Object)MaplePacketCreator.getShowItemGain(id, count, true));
                }
                break;
            }
            case nextQuest: {
                c.getClient().getSession().write((Object)MaplePacketCreator.updateQuestFinish(this.quest.getId(), c.getQuest(this.quest).getNpc(), MapleDataTool.getInt(this.data)));
                break;
            }
            case money: {
                c.gainMeso(MapleDataTool.getInt(this.data, 0), true, false, true);
                break;
            }
            case quest: {
                for (MapleData qEntry : this.data) {
                    c.updateQuest(new MapleQuestStatus(MapleQuest.getInstance(MapleDataTool.getInt(qEntry.getChildByPath("id"))), (byte)MapleDataTool.getInt(qEntry.getChildByPath("state"), 0)));
                }
                break;
            }
            case skill: {
                block16: for (MapleData sEntry : this.data) {
                    int skillid = MapleDataTool.getInt(sEntry.getChildByPath("id"));
                    int skillLevel = MapleDataTool.getInt(sEntry.getChildByPath("skillLevel"), 0);
                    int masterLevel = MapleDataTool.getInt(sEntry.getChildByPath("masterLevel"), 0);
                    ISkill skillObject = SkillFactory.getSkill(skillid);
                    for (MapleData applicableJob : sEntry.getChildByPath("job")) {
                        if (!skillObject.isBeginnerSkill() && c.getJob() != MapleDataTool.getInt(applicableJob)) continue;
                        c.changeSkillLevel(skillObject, (byte)Math.max(skillLevel, c.getSkillLevel(skillObject)), (byte)Math.max(masterLevel, c.getMasterLevel(skillObject)));
                        continue block16;
                    }
                }
                break;
            }
            case pop: {
                int fameGain = MapleDataTool.getInt(this.data, 0);
                c.addFame(fameGain);
                c.updateSingleStat(MapleStat.FAME, c.getFame());
                c.getClient().getSession().write((Object)MaplePacketCreator.getShowFameGain(fameGain));
                break;
            }
            case buffItemID: {
                int tobuff = MapleDataTool.getInt(this.data, -1);
                if (tobuff == -1) break;
                MapleItemInformationProvider.getInstance().getItemEffect(tobuff).applyTo(c);
                break;
            }
            case infoNumber: {
                break;
            }
            case sp: {
                for (MapleData iEntry : this.data.getChildren()) {
                    int sp_val = MapleDataTool.getInt(iEntry.getChildByPath("sp_value"), 0);
                    if (iEntry.getChildByPath("job") != null) {
                        int finalJob = 0;
                        for (MapleData jEntry : iEntry.getChildByPath("job").getChildren()) {
                            int job_val = MapleDataTool.getInt(jEntry, 0);
                            if (c.getJob() < job_val || job_val <= finalJob) continue;
                            finalJob = job_val;
                        }
                        c.gainSP(sp_val, GameConstants.getSkillBook(finalJob));
                        continue;
                    }
                    c.gainSP(sp_val);
                }
                break;
            }
        }
    }

    private static List<Integer> getJobBy5ByteEncoding(int encoded) {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        if ((encoded & 1) != 0) {
            ret.add(0);
        }
        if ((encoded & 2) != 0) {
            ret.add(100);
        }
        if ((encoded & 4) != 0) {
            ret.add(200);
        }
        if ((encoded & 8) != 0) {
            ret.add(300);
        }
        if ((encoded & 0x10) != 0) {
            ret.add(400);
        }
        if ((encoded & 0x20) != 0) {
            ret.add(500);
        }
        if ((encoded & 0x400) != 0) {
            ret.add(1000);
        }
        if ((encoded & 0x800) != 0) {
            ret.add(1100);
        }
        if ((encoded & 0x1000) != 0) {
            ret.add(1200);
        }
        if ((encoded & 0x2000) != 0) {
            ret.add(1300);
        }
        if ((encoded & 0x4000) != 0) {
            ret.add(1400);
        }
        if ((encoded & 0x8000) != 0) {
            ret.add(1500);
        }
        if ((encoded & 0x20000) != 0) {
            ret.add(2001);
            ret.add(2200);
        }
        if ((encoded & 0x100000) != 0) {
            ret.add(2000);
            ret.add(2001);
        }
        if ((encoded & 0x200000) != 0) {
            ret.add(2100);
        }
        if ((encoded & 0x400000) != 0) {
            ret.add(2001);
            ret.add(2200);
        }
        if ((encoded & 0x40000000) != 0) {
            ret.add(3000);
            ret.add(3200);
            ret.add(3300);
            ret.add(3500);
        }
        return ret;
    }

    public MapleQuestActionType getType() {
        return this.type;
    }

    public String toString() {
        return (Object)((Object)this.type) + ": " + this.data;
    }
}

