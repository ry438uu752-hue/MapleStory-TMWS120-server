/*
 * Decompiled with CFR 0.152.
 */
package server.life;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.life.MapleMonster;
import server.life.MobAttackInfo;
import tools.Pair;
import tools.StringUtil;

public class MobAttackInfoFactory {
    private static final MobAttackInfoFactory instance = new MobAttackInfoFactory();
    private static final MapleDataProvider dataSource = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Mob.wz"));
    private static Map<Pair<Integer, Integer>, MobAttackInfo> mobAttacks = new HashMap<Pair<Integer, Integer>, MobAttackInfo>();

    public static MobAttackInfoFactory getInstance() {
        return instance;
    }

    public MobAttackInfo getMobAttackInfo(MapleMonster mob2, int attack) {
        MobAttackInfo ret = mobAttacks.get(new Pair<Integer, Integer>(mob2.getId(), attack));
        if (ret != null) {
            return ret;
        }
        MapleData mobData = dataSource.getData(StringUtil.getLeftPaddedStr(Integer.toString(mob2.getId()) + ".img", '0', 11));
        if (mobData != null) {
            MapleData attackData;
            MapleData infoData = mobData.getChildByPath("info/link");
            if (infoData != null) {
                String linkedmob = MapleDataTool.getString("info/link", mobData);
                mobData = dataSource.getData(StringUtil.getLeftPaddedStr(linkedmob + ".img", '0', 11));
            }
            if ((attackData = mobData.getChildByPath("attack" + (attack + 1) + "/info")) != null) {
                ret = new MobAttackInfo();
                ret.setDeadlyAttack(attackData.getChildByPath("deadlyAttack") != null);
                ret.setMpBurn(MapleDataTool.getInt("mpBurn", attackData, 0));
                ret.setDiseaseSkill(MapleDataTool.getInt("disease", attackData, 0));
                ret.setDiseaseLevel(MapleDataTool.getInt("level", attackData, 0));
                ret.setMpCon(MapleDataTool.getInt("conMP", attackData, 0));
            }
        }
        mobAttacks.put(new Pair<Integer, Integer>(mob2.getId(), attack), ret);
        return ret;
    }
}

