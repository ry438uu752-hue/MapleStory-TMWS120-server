/*
 * Decompiled with CFR 0.152.
 */
package client;

import client.ISkill;
import client.Skill;
import client.SummonSkillEntry;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import provider.MapleData;
import provider.MapleDataDirectoryEntry;
import provider.MapleDataFileEntry;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import tools.StringUtil;

public class SkillFactory {
    private static final Map<Integer, ISkill> skills = new HashMap<Integer, ISkill>();
    private static final Map<Integer, List<Integer>> skillsByJob = new HashMap<Integer, List<Integer>>();
    private static final Map<Integer, SummonSkillEntry> SummonSkillInformation = new HashMap<Integer, SummonSkillEntry>();
    private static final MapleData stringData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/String.wz")).getData("Skill.img");

    public static final ISkill getSkill(int id) {
        if (skills.size() != 0) {
            return skills.get(id);
        }
        System.out.println("Loading SkillFactory :::");
        MapleDataProvider datasource = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Skill.wz"));
        MapleDataDirectoryEntry root = datasource.getRoot();
        for (MapleDataFileEntry topDir : root.getFiles()) {
            if (topDir.getName().length() > 8) continue;
            for (MapleData data : datasource.getData(topDir.getName())) {
                if (!data.getName().equals("skill")) continue;
                for (MapleData data2 : data) {
                    if (data2 == null) continue;
                    int skillid = Integer.parseInt(data2.getName());
                    Skill skil = Skill.loadFromData(skillid, data2);
                    List<Integer> job = skillsByJob.get(skillid / 10000);
                    if (job == null) {
                        job = new ArrayList<Integer>();
                        skillsByJob.put(skillid / 10000, job);
                    }
                    job.add(skillid);
                    skil.setName(SkillFactory.getName(skillid));
                    skills.put(skillid, skil);
                    MapleData summon_data = data2.getChildByPath("summon/attack1/info");
                    if (summon_data == null) continue;
                    SummonSkillEntry sse = new SummonSkillEntry();
                    sse.attackAfter = (short)MapleDataTool.getInt("attackAfter", summon_data, 999999);
                    sse.type = (byte)MapleDataTool.getInt("type", summon_data, 0);
                    sse.mobCount = (byte)MapleDataTool.getInt("mobCount", summon_data, 1);
                    SummonSkillInformation.put(skillid, sse);
                }
            }
        }
        return null;
    }

    public static final List<Integer> getSkillsByJob(int jobId) {
        return skillsByJob.get(jobId);
    }

    public static final String getSkillName(int id) {
        ISkill skil = SkillFactory.getSkill(id);
        if (skil != null) {
            return skil.getName();
        }
        return null;
    }

    public static final String getName(int id) {
        String strId = Integer.toString(id);
        MapleData skillroot = stringData.getChildByPath(strId = StringUtil.getLeftPaddedStr(strId, '0', 7));
        if (skillroot != null) {
            return MapleDataTool.getString(skillroot.getChildByPath("name"), "");
        }
        return null;
    }

    public static final SummonSkillEntry getSummonData(int skillid) {
        return SummonSkillInformation.get(skillid);
    }

    public static final Collection<ISkill> getAllSkills() {
        return skills.values();
    }
}

