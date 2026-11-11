/*
 * Decompiled with CFR 0.152.
 */
package handling.channel.handler;

import client.ISkill;
import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import client.SummonSkillEntry;
import client.anticheat.CheatingOffense;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import handling.channel.handler.MovementParse;
import java.awt.Point;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import server.AutobanManager;
import server.MapleStatEffect;
import server.Timer;
import server.life.MapleMonster;
import server.life.SummonAttackEntry;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleSummon;
import server.maps.SummonMovementType;
import server.movement.LifeMovementFragment;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.MobPacket;

public class SummonHandler {
    public static final void MoveDragon(SeekableLittleEndianAccessor slea, MapleCharacter chr) {
        slea.skip(4);
        Point startPos = new Point(slea.readShort(), slea.readShort());
        List<LifeMovementFragment> res = MovementParse.parseMovement(slea, 5);
        if (chr != null && chr.getDragon() != null) {
            Point pos = chr.getDragon().getPosition();
            MovementParse.updatePosition(res, chr.getDragon(), 0);
            if (!chr.isHidden()) {
                chr.getMap().broadcastMessage(chr, MaplePacketCreator.moveDragon(chr.getDragon(), pos, res), chr.getPosition());
            }
            WeakReference<MapleCharacter>[] clones = chr.getClones();
            for (int i = 0; i < clones.length; ++i) {
                if (clones[i].get() == null) continue;
                final MapleMap map = chr.getMap();
                final MapleCharacter clone = (MapleCharacter)clones[i].get();
                final ArrayList<LifeMovementFragment> res3 = new ArrayList<LifeMovementFragment>(res);
                Timer.CloneTimer.getInstance().schedule(new Runnable(){

                    @Override
                    public void run() {
                        try {
                            if (clone.getMap() == map && clone.getDragon() != null) {
                                Point startPos = clone.getDragon().getPosition();
                                MovementParse.updatePosition(res3, clone.getDragon(), 0);
                                if (!clone.isHidden()) {
                                    map.broadcastMessage(clone, MaplePacketCreator.moveDragon(clone.getDragon(), startPos, res3), clone.getPosition());
                                }
                            }
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                    }
                }, 500 * i + 500);
            }
        }
    }

    public static final void MoveSummon(SeekableLittleEndianAccessor slea, MapleCharacter chr) {
        int oid = slea.readInt();
        slea.skip(4);
        Point startPos = new Point(slea.readShort(), slea.readShort());
        List<LifeMovementFragment> res = MovementParse.parseMovement(slea, 4);
        if (chr == null) {
            return;
        }
        for (MapleSummon sum : chr.getSummons().values()) {
            if (sum.getObjectId() != oid || sum.getMovementType() == SummonMovementType.STATIONARY) continue;
            Point pos = sum.getPosition();
            MovementParse.updatePosition(res, sum, 0);
            if (res.size() <= 0) break;
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.moveSummon(chr.getId(), oid, startPos, res), sum.getPosition());
            break;
        }
    }

    public static final void DamageSummon(SeekableLittleEndianAccessor slea, MapleCharacter chr) {
        byte unkByte = slea.readByte();
        int damage = slea.readInt();
        int monsterIdFrom = slea.readInt();
        for (MapleSummon summon : chr.getSummons().values()) {
            if (!summon.isPuppet() || summon.getOwnerId() != chr.getId()) continue;
            summon.addHP((short)(-damage));
            if (summon.getHP() <= 0) {
                chr.cancelEffectFromBuffStat(MapleBuffStat.PUPPET);
            }
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.damageSummon(chr.getId(), summon.getSkill(), damage, unkByte, monsterIdFrom), summon.getPosition());
            break;
        }
    }

    public static void SummonAttack(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        ISkill summonSkill;
        MapleStatEffect summonEffect;
        if (chr == null || !chr.isAlive()) {
            return;
        }
        MapleMap map = chr.getMap();
        MapleMapObject obj = map.getMapObject(slea.readInt(), MapleMapObjectType.SUMMON);
        if (obj == null) {
            return;
        }
        MapleSummon summon = (MapleSummon)obj;
        if (summon.getOwnerId() != chr.getId() || summon.getSkillLevel() <= 0) {
            return;
        }
        SummonSkillEntry sse = SkillFactory.getSummonData(summon.getSkill());
        if (sse == null) {
            return;
        }
        slea.skip(8);
        int tick = slea.readInt();
        chr.updateTick(tick);
        summon.CheckSummonAttackFrequency(chr, tick);
        slea.skip(8);
        byte animation = slea.readByte();
        slea.skip(8);
        int numAttacked = slea.readByte();
        if (numAttacked > sse.mobCount) {
            chr.getCheatTracker().registerOffense(CheatingOffense.SUMMON_HACK_MOBS);
            return;
        }
        slea.skip(8);
        ArrayList<SummonAttackEntry> allDamage = new ArrayList<SummonAttackEntry>();
        chr.getCheatTracker().checkSummonAttack();
        for (int i = 0; i < numAttacked; ++i) {
            MapleMonster mob2 = map.getMonsterByOid(slea.readInt());
            if (mob2 == null) continue;
            if (chr.getPosition().distanceSq(mob2.getPosition()) > 400000.0) {
                chr.getCheatTracker().registerOffense(CheatingOffense.ATTACK_FARAWAY_MONSTER_SUMMON);
            }
            slea.skip(18);
            int damage = slea.readInt();
            allDamage.add(new SummonAttackEntry(mob2, damage));
        }
        if (!summon.isChangedMap()) {
            map.broadcastMessage(chr, MaplePacketCreator.summonAttack(summon.getOwnerId(), summon.getObjectId(), animation, allDamage, chr.getLevel()), summon.getPosition());
        }
        if ((summonEffect = (summonSkill = SkillFactory.getSkill(summon.getSkill())).getEffect(summon.getSkillLevel())) == null) {
            return;
        }
        for (SummonAttackEntry attackEntry : allDamage) {
            int toDamage = attackEntry.getDamage();
            MapleMonster mob3 = attackEntry.getMonster();
            if (toDamage > 0 && summonEffect.getMonsterStati().size() > 0 && summonEffect.makeChanceResult()) {
                for (Map.Entry<MonsterStatus, Integer> z : summonEffect.getMonsterStati().entrySet()) {
                    mob3.applyStatus(chr, new MonsterStatusEffect(z.getKey(), z.getValue(), summonSkill.getId(), null, false), summonEffect.isPoison(), 4000L, false);
                }
            }
            if (chr.isGM() || toDamage < 120000) {
                mob3.damage(chr, toDamage, true);
                chr.checkMonsterAggro(mob3);
                if (mob3.isAlive()) continue;
                chr.getClient().getSession().write((Object)MobPacket.killMonster(mob3.getObjectId(), 1));
                continue;
            }
            AutobanManager.getInstance().autoban(c, "High Summon Damage (" + toDamage + " to " + attackEntry.getMonster().getId() + ")");
        }
        if (summon.isGaviota()) {
            chr.getMap().broadcastMessage(MaplePacketCreator.removeSummon(summon, true));
            chr.getMap().removeMapObject(summon);
            chr.removeVisibleMapObject(summon);
            chr.cancelEffectFromBuffStat(MapleBuffStat.SUMMON);
            chr.cancelEffectFromBuffStat(MapleBuffStat.REAPER);
        }
    }
}

