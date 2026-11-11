/*
 * Decompiled with CFR 0.152.
 */
package handling.world.family;

import client.MapleBuffStat;
import client.MapleCharacter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.Timer;
import tools.MaplePacketCreator;
import tools.Pair;

public class MapleFamilyBuff {
    private static final int event = 4;
    private static final int[] type = new int[]{0, 1, 2, 3};
    private static final int[] duration = new int[]{0, 0, 15, 15};
    private static final int[] effect = new int[]{0, 0, 150, 150};
    private static final int[] rep = new int[]{0, 0, 300, 500};
    private static final String[] name = new String[]{"\u4e00\u8d77\u597d\u55ce?", "\u53ec\u559a\u5cfd\u8c37\u7684\u53ec\u559a\u5e2b?!", "\u6389\u843d\u7387\u589e\u52a0 1.5x (15\u5206\u9418)", "\u7d93\u9a57\u503c\u589e\u52a0 1.5x (15\u5206\u9418)"};
    private static final String[] desc = new String[]{"[\u76ee\u5730] \u81ea\u5df1\n[\u6548\u679c] \u50b3\u9001\u4f60\u5bb6\u7684\u4eba\u5230\u4f60\u65c1\u908a.", "[\u689d\u4ef6] 1 \u500b\u5bb6\u4eba\n[\u6548\u679c] \u50b3\u9001\u4e00\u4f4d\u5bb6\u4eba\u5230\u4f60\u65c1\u908a.", "[\u76ee\u5730] \u81ea\u5df1\n[\u6642\u9593] 15 \u5206\u9418.\n[\u6548\u679c] \u589e\u52a0\u602a\u7269\u6389\u843d\u7387 #c1.5x# \n \u7121\u6cd5\u8207\u5176\u4ed6\u8d08\u79ae\u4e00\u8d77\u4f7f\u7528 ", "[\u76ee\u7684] \u81ea\u5df1\n[\u6642\u9593] 15 \u5206\u9418.\n[\u6548\u679c] \u602a\u7269\u7d93\u9a57\u503c\u589e\u52a0 #c1.5x# \n \u7121\u6cd5\u8207\u7375\u4eba\u7684\u5e78\u904b\u4e00\u8d77\u4f7f\u7528"};
    private static final List<MapleFamilyBuffEntry> buffEntries = new ArrayList<MapleFamilyBuffEntry>();

    public static List<MapleFamilyBuffEntry> getBuffEntry() {
        return buffEntries;
    }

    public static MapleFamilyBuffEntry getBuffEntry(int i) {
        return buffEntries.get(i);
    }

    static {
        for (int i = 0; i < 4; ++i) {
            buffEntries.add(new MapleFamilyBuffEntry(i, name[i], desc[i], 1, rep[i], type[i], 190000 + i, duration[i], effect[i]));
        }
    }

    public static class MapleFamilyBuffEntry {
        public String name;
        public String desc;
        public int count;
        public int rep;
        public int type;
        public int index;
        public int questID;
        public int duration;
        public int effect;
        public List<Pair<MapleBuffStat, Integer>> effects;

        public MapleFamilyBuffEntry(int index, String name, String desc, int count, int rep, int type, int questID, int duration, int effect) {
            this.name = name;
            this.desc = desc;
            this.count = count;
            this.rep = rep;
            this.type = type;
            this.questID = questID;
            this.index = index;
            this.duration = duration;
            this.effect = effect;
            this.effects = this.getEffects();
        }

        public int getEffectId() {
            switch (this.type) {
                case 2: {
                    return 2022463;
                }
                case 3: {
                    return 2450000;
                }
            }
            return 2022332;
        }

        public final List<Pair<MapleBuffStat, Integer>> getEffects() {
            ArrayList<Pair<MapleBuffStat, Integer>> ret = new ArrayList<Pair<MapleBuffStat, Integer>>();
            switch (this.type) {
                case 2: {
                    ret.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DROP_RATE, this.effect));
                    ret.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MESO_RATE, this.effect));
                    break;
                }
                case 3: {
                    ret.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.EXPRATE, this.effect));
                    break;
                }
                case 4: {
                    ret.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.EXPRATE, this.effect));
                    ret.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DROP_RATE, this.effect));
                    ret.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MESO_RATE, this.effect));
                }
            }
            return ret;
        }

        public void applyTo(MapleCharacter chr) {
            chr.getClient().getSession().write((Object)MaplePacketCreator.giveBuff(-this.getEffectId(), this.duration * 60000, this.effects, null));
            MapleStatEffect eff = MapleItemInformationProvider.getInstance().getItemEffect(this.getEffectId());
            chr.cancelEffect(eff, true, -1L, this.effects);
            long starttime = System.currentTimeMillis();
            MapleStatEffect.CancelEffectAction cancelAction = new MapleStatEffect.CancelEffectAction(chr, eff, starttime);
            ScheduledFuture<?> schedule = Timer.BuffTimer.getInstance().schedule(cancelAction, starttime + (long)(this.duration * 60000) - starttime);
            chr.registerEffect(eff, starttime, schedule, this.effects);
        }
    }
}

