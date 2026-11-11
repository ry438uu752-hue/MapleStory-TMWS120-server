/*
 * Decompiled with CFR 0.152.
 */
package handling.channel.handler;

import client.ISkill;
import client.MapleBuffStat;
import client.MapleCharacter;
import client.PlayerStats;
import client.SkillFactory;
import client.anticheat.CheatTracker;
import client.anticheat.CheatingOffense;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import constants.GameConstants;
import handling.channel.handler.AttackInfo;
import handling.channel.handler.AttackType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import server.MapleStatEffect;
import server.Randomizer;
import server.Timer;
import server.life.Element;
import server.life.MapleMonster;
import server.life.MapleMonsterStats;
import server.maps.MapleMap;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.AttackPair;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.data.input.LittleEndianAccessor;

public class DamageParse {
    private static final int[] charges = new int[]{1211005, 1211006};

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void applyAttack(AttackInfo attack, ISkill theSkill, MapleCharacter player, int attackCount, double maxDamagePerMonster, MapleStatEffect effect, AttackType attack_type) {
        if (!player.isAlive()) {
            player.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_WHILE_DEAD);
            return;
        }
        if (attack.real) {
            player.getCheatTracker().checkAttack(attack.skill, attack.lastAttackTickCount);
        }
        if (attack.skill != 0) {
            if (effect == null) {
                player.getClient().getSession().write((Object)MaplePacketCreator.enableActions());
                return;
            }
            if (GameConstants.isMulungSkill(attack.skill)) {
                if (player.getMapId() / 10000 != 92502) {
                    return;
                }
                player.mulung_EnergyModify(false);
            }
            if (GameConstants.isPyramidSkill(attack.skill)) {
                if (player.getMapId() / 1000000 != 926) {
                    return;
                }
                if (player.getPyramidSubway() == null || !player.getPyramidSubway().onSkillUse(player)) {
                    return;
                }
            }
            if (attack.targets > effect.getMobCount()) {
                player.getCheatTracker().registerOffense(CheatingOffense.MISMATCHING_BULLETCOUNT);
                return;
            }
        }
        if (attack.hits > attackCount && attack.skill != 4211006) {
            player.getCheatTracker().registerOffense(CheatingOffense.MISMATCHING_BULLETCOUNT);
            return;
        }
        if (attack.hits > 0 && attack.targets > 0 && !player.getStat().checkEquipDurabilitys(player, -1)) {
            player.dropMessage(5, "An item has run out of durability but has no inventory room to go to.");
            return;
        }
        int totDamage = 0;
        MapleMap map = player.getMap();
        if (attack.skill == 4211006) {
            for (AttackPair oned : attack.allDamage) {
                if (oned.attack != null) continue;
                MapleMapObject mapobject = map.getMapObject(oned.objectid, MapleMapObjectType.ITEM);
                if (mapobject != null) {
                    MapleMapItem mapitem = (MapleMapItem)mapobject;
                    mapitem.getLock().lock();
                    try {
                        if (mapitem.getMeso() > 0) {
                            if (mapitem.isPickedUp()) {
                                return;
                            }
                            map.removeMapObject(mapitem);
                            map.broadcastMessage(MaplePacketCreator.explodeDrop(mapitem.getObjectId()));
                            mapitem.setPickedUp(true);
                            continue;
                        }
                        player.getCheatTracker().registerOffense(CheatingOffense.ETC_EXPLOSION);
                        return;
                    }
                    finally {
                        mapitem.getLock().unlock();
                        continue;
                    }
                }
                player.getCheatTracker().registerOffense(CheatingOffense.EXPLODING_NONEXISTANT);
                return;
            }
        }
        int totDamageToOneMonster = 0;
        long hpMob = 0L;
        PlayerStats stats = player.getStat();
        short CriticalDamage = stats.passive_sharpeye_percent();
        byte ShdowPartnerAttackPercentage = 0;
        if (attack_type == AttackType.RANGED_WITH_SHADOWPARTNER || attack_type == AttackType.NON_RANGED_WITH_MIRROR) {
            MapleStatEffect shadowPartnerEffect = attack_type == AttackType.NON_RANGED_WITH_MIRROR ? player.getStatForBuff(MapleBuffStat.MIRROR_IMAGE) : player.getStatForBuff(MapleBuffStat.SHADOWPARTNER);
            if (shadowPartnerEffect != null) {
                ShdowPartnerAttackPercentage = attack.skill != 0 && attack_type != AttackType.NON_RANGED_WITH_MIRROR ? (byte)shadowPartnerEffect.getY() : (byte)shadowPartnerEffect.getX();
            }
            attackCount /= 2;
        }
        switch (attack.skill) {
            case 21100004: 
            case 21110004: 
            case 21120006: {
                player.setLastCombo(System.currentTimeMillis());
                player.setCombo((short)0);
            }
        }
        double maxDamagePerHit = 0.0;
        int d = 0;
        for (AttackPair oned : attack.allDamage) {
            MapleMonster monster = map.getMonsterByOid(oned.objectid);
            if (monster != null) {
                MapleStatEffect ds;
                boolean Tempest;
                totDamageToOneMonster = 0;
                hpMob = monster.getHp();
                MapleMonsterStats monsterstats = monster.getStats();
                int fixeddmg = monsterstats.getFixedDamage();
                boolean bl = Tempest = monster.getStatusSourceID(MonsterStatus.FREEZE) == 21120006;
                if (!Tempest && !player.isGM()) {
                    maxDamagePerHit = !monster.isBuffed(MonsterStatus.DAMAGE_IMMUNITY) && !monster.isBuffed(MonsterStatus.WEAPON_IMMUNITY) && !monster.isBuffed(MonsterStatus.WEAPON_DAMAGE_REFLECT) ? DamageParse.CalculateMaxWeaponDamagePerHit(player, monster, attack, theSkill, effect, maxDamagePerMonster, Integer.valueOf(CriticalDamage)) : 1.0;
                }
                int overallAttackCount = 0;
                for (Pair<Integer, Boolean> eachde : oned.attack) {
                    Integer eachd = (Integer)eachde.left;
                    if ((overallAttackCount = (int)((byte)(overallAttackCount + 1))) - 1 == attackCount) {
                        maxDamagePerHit = maxDamagePerHit / 100.0 * (double)ShdowPartnerAttackPercentage;
                    }
                    if (fixeddmg != -1) {
                        eachd = monsterstats.getOnlyNoramlAttack() ? Integer.valueOf(attack.skill != 0 ? 0 : fixeddmg) : Integer.valueOf(fixeddmg);
                    } else if (monsterstats.getOnlyNoramlAttack()) {
                        eachd = attack.skill != 0 ? 0 : Math.min(eachd, (int)maxDamagePerHit);
                    } else if (!player.isGM()) {
                        if (Tempest) {
                            if ((long)eachd.intValue() > monster.getMobMaxHp()) {
                                eachd = (int)Math.min(monster.getMobMaxHp(), Integer.MAX_VALUE);
                                player.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE);
                            }
                        } else if (!(monster.isBuffed(MonsterStatus.DAMAGE_IMMUNITY) || monster.isBuffed(MonsterStatus.WEAPON_IMMUNITY) || monster.isBuffed(MonsterStatus.WEAPON_DAMAGE_REFLECT))) {
                            if ((double)eachd.intValue() > maxDamagePerHit * 3.0) {
                                player.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE);
                                if ((double)eachd.intValue() > maxDamagePerHit * 5.0) {
                                    eachd = (int)(maxDamagePerHit * 5.0);
                                    player.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE_2);
                                }
                            }
                        } else if ((double)eachd.intValue() > maxDamagePerHit) {
                            eachd = (int)maxDamagePerHit;
                        }
                    }
                    if (player == null) {
                        return;
                    }
                    if (player.getClient().getChannelServer().isAdminOnly()) {
                        player.dropMessage(-1, "Damage: " + eachd);
                    }
                    totDamageToOneMonster += eachd.intValue();
                    if (monster.getId() != 9300021 || player.getPyramidSubway() == null) continue;
                    player.getPyramidSubway().onMiss(player);
                }
                totDamage += totDamageToOneMonster;
                player.checkMonsterAggro(monster);
                if (player.getPosition().distanceSq(monster.getPosition()) > 700000.0) {
                    player.getCheatTracker().registerOffense(CheatingOffense.ATTACK_FARAWAY_MONSTER);
                }
                if (player.getBuffedValue(MapleBuffStat.PICKPOCKET) != null) {
                    switch (attack.skill) {
                        case 0: 
                        case 4001334: 
                        case 4201005: 
                        case 4211002: 
                        case 4211004: 
                        case 4221003: 
                        case 4221007: {
                            DamageParse.handlePickPocket(player, monster, oned);
                        }
                    }
                }
                if (!((ds = player.getStatForBuff(MapleBuffStat.DARKSIGHT)) == null || player.isGM() || ds.getSourceId() == 0x421211 && ds.makeChanceResult())) {
                    player.cancelEffectFromBuffStat(MapleBuffStat.DARKSIGHT);
                }
                if (totDamageToOneMonster > 0) {
                    if (attack.skill != 1221011) {
                        monster.damage(player, totDamageToOneMonster, true, attack.skill);
                    } else {
                        monster.damage(player, monster.getStats().isBoss() ? 199999L : monster.getHp() - 1L, true, attack.skill);
                    }
                    if (monster.isBuffed(MonsterStatus.WEAPON_DAMAGE_REFLECT)) {
                        player.addHP(-(7000 + Randomizer.nextInt(8000)));
                    }
                    if (stats.hpRecoverProp > 0 && Randomizer.nextInt(100) <= stats.hpRecoverProp) {
                        player.healHP(stats.hpRecover);
                    }
                    if (stats.mpRecoverProp > 0 && Randomizer.nextInt(100) <= stats.mpRecoverProp) {
                        player.healMP(stats.mpRecover);
                    }
                    if (player.getBuffedValue(MapleBuffStat.COMBO_DRAIN) != null) {
                        stats.setHp(stats.getHp() + (int)Math.min(monster.getMobMaxHp(), (long)Math.min((int)((double)totDamage * (double)player.getStatForBuff(MapleBuffStat.COMBO_DRAIN).getX() / 100.0), stats.getMaxHp() / 2)), true);
                    }
                    switch (attack.skill) {
                        case 4101005: 
                        case 5111004: 
                        case 14101006: {
                            stats.setHp(stats.getHp() + (int)Math.min(monster.getMobMaxHp(), (long)Math.min((int)((double)totDamage * (double)theSkill.getEffect(player.getSkillLevel(theSkill)).getX() / 100.0), stats.getMaxHp() / 2)), true);
                            break;
                        }
                        case 5211006: 
                        case 5220011: 
                        case 22151002: {
                            player.setLinkMid(monster.getObjectId());
                            break;
                        }
                        case 1311005: {
                            int remainingHP = stats.getHp() - totDamage * effect.getX() / 100;
                            stats.setHp(remainingHP < 1 ? 1 : remainingHP);
                            break;
                        }
                        case 4001002: 
                        case 4001334: 
                        case 4001344: 
                        case 4111005: 
                        case 4121007: 
                        case 4201005: 
                        case 4211002: 
                        case 4221001: 
                        case 4221007: 
                        case 4301001: 
                        case 4311002: 
                        case 4311003: 
                        case 4331000: 
                        case 4331004: 
                        case 4331005: 
                        case 4341005: {
                            ISkill skill = SkillFactory.getSkill(4120005);
                            ISkill skill2 = SkillFactory.getSkill(4220005);
                            ISkill skill3 = SkillFactory.getSkill(4340001);
                            if (player.getSkillLevel(skill) > 0) {
                                MapleStatEffect venomEffect = skill.getEffect(player.getSkillLevel(skill));
                                for (int i = 0; i < attackCount; ++i) {
                                    if (!venomEffect.makeChanceResult() || monster.getVenomMulti() >= 3) continue;
                                    monster.setVenomMulti((byte)(monster.getVenomMulti() + 1));
                                    MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(MonsterStatus.POISON, 1, 4120005, null, false);
                                    monster.applyStatus(player, monsterStatusEffect, false, venomEffect.getDuration() + d, true);
                                }
                            } else if (player.getSkillLevel(skill2) > 0) {
                                MapleStatEffect venomEffect = skill2.getEffect(player.getSkillLevel(skill2));
                                for (int i = 0; i < attackCount; ++i) {
                                    if (!venomEffect.makeChanceResult() || monster.getVenomMulti() >= 3) continue;
                                    monster.setVenomMulti((byte)(monster.getVenomMulti() + 1));
                                    MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(MonsterStatus.POISON, 1, 4220005, null, false);
                                    monster.applyStatus(player, monsterStatusEffect, false, venomEffect.getDuration() + d, true);
                                }
                            } else {
                                if (player.getSkillLevel(skill3) <= 0) break;
                                MapleStatEffect venomEffect = skill3.getEffect(player.getSkillLevel(skill3));
                                for (int i = 0; i < attackCount; ++i) {
                                    if (!venomEffect.makeChanceResult() || monster.getVenomMulti() >= 3) continue;
                                    monster.setVenomMulti((byte)(monster.getVenomMulti() + 1));
                                    MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(MonsterStatus.POISON, 1, 4340001, null, false);
                                    monster.applyStatus(player, monsterStatusEffect, false, venomEffect.getDuration() + d, true);
                                }
                            }
                            break;
                        }
                        case 4201004: {
                            break;
                        }
                        case 21000002: 
                        case 21100001: 
                        case 21100002: 
                        case 21100004: 
                        case 21110002: 
                        case 21110003: 
                        case 21110004: 
                        case 21110006: 
                        case 21110007: 
                        case 21110008: 
                        case 21120002: 
                        case 21120005: 
                        case 21120006: 
                        case 21120009: 
                        case 21120010: {
                            MapleStatEffect eff;
                            if (player.getBuffedValue(MapleBuffStat.WK_CHARGE) != null && !monster.getStats().isBoss() && (eff = player.getStatForBuff(MapleBuffStat.WK_CHARGE)) != null && eff.getSourceId() == 21111005) {
                                monster.applyStatus(player, new MonsterStatusEffect(MonsterStatus.SPEED, eff.getX(), eff.getSourceId(), null, false), false, eff.getY() * 1000 + d, false);
                            }
                            if (player.getBuffedValue(MapleBuffStat.BODY_PRESSURE) == null || monster.getStats().isBoss() || (eff = player.getStatForBuff(MapleBuffStat.BODY_PRESSURE)) == null || !eff.makeChanceResult() || monster.isBuffed(MonsterStatus.NEUTRALISE)) break;
                            monster.applyStatus(player, new MonsterStatusEffect(MonsterStatus.NEUTRALISE, 1, eff.getSourceId(), null, false), false, eff.getX() * 1000 + d, false);
                            break;
                        }
                    }
                    if (totDamageToOneMonster > 0) {
                        Object skill;
                        MapleStatEffect eff;
                        MapleStatEffect eff2;
                        MonsterStatusEffect monsterStatusEffect;
                        MonsterStatus stat;
                        IItem weapon_ = player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11);
                        if (weapon_ != null && (stat = GameConstants.getStatFromWeapon(weapon_.getItemId())) != null && Randomizer.nextInt(100) < GameConstants.getStatChance()) {
                            monsterStatusEffect = new MonsterStatusEffect(stat, GameConstants.getXForStat(stat), GameConstants.getSkillForStat(stat), null, false);
                            monster.applyStatus(player, monsterStatusEffect, false, (long)(10000 + d), false, false);
                        }
                        if (player.getBuffedValue(MapleBuffStat.BLIND) != null && (eff2 = player.getStatForBuff(MapleBuffStat.BLIND)).makeChanceResult()) {
                            monsterStatusEffect = new MonsterStatusEffect(MonsterStatus.ACC, eff2.getX(), eff2.getSourceId(), null, false);
                            monster.applyStatus(player, monsterStatusEffect, false, eff2.getY() * 1000 + d, false);
                        }
                        if (player.getBuffedValue(MapleBuffStat.HAMSTRING) != null && (eff = SkillFactory.getSkill(3121007).getEffect(player.getSkillLevel(SkillFactory.getSkill(3121007)))).makeChanceResult()) {
                            MonsterStatusEffect monsterStatusEffect2 = new MonsterStatusEffect(MonsterStatus.SPEED, eff.getX(), 3121007, null, false);
                            monster.applyStatus(player, monsterStatusEffect2, false, eff.getY() * 1000 + d, false);
                        }
                        if (player.getJob() == 121) {
                            int n = charges.length;
                            for (int i = 0; i < n; ++i) {
                                int charge = charges[i];
                                ISkill skill2 = SkillFactory.getSkill(charge);
                                if (!player.isBuffFrom(MapleBuffStat.WK_CHARGE, skill2)) continue;
                                MonsterStatusEffect monsterStatusEffect3 = new MonsterStatusEffect(MonsterStatus.FREEZE, 1, (int)charge, null, false);
                                monster.applyStatus(player, monsterStatusEffect3, false, skill2.getEffect(player.getSkillLevel(skill2)).getY() * 2000 + d, false);
                                break;
                            }
                        }
                    }
                    if (effect != null && effect.getMonsterStati().size() > 0 && effect.makeChanceResult()) {
                        for (Map.Entry<MonsterStatus, Integer> z : effect.getMonsterStati().entrySet()) {
                            monster.applyStatus(player, new MonsterStatusEffect(z.getKey(), z.getValue(), theSkill.getId(), null, false), effect.isPoison(), (long)(effect.getDuration() + d), false, d);
                        }
                    }
                }
            }
            d += 50;
        }
        if (attack.skill == 4331003 && (long)totDamageToOneMonster < hpMob) {
            return;
        }
        if (attack.skill != 0 && (attack.targets > 0 || attack.skill != 4331003 && attack.skill != 4341002) && attack.skill != 21101003 && attack.skill != 5110001 && attack.skill != 15100004 && attack.skill != 11101002 && attack.skill != 13101002) {
            effect.applyTo(player, attack.position);
        }
        if (totDamage > 1) {
            CheatTracker tracker = player.getCheatTracker();
            tracker.setAttacksWithoutHit(true);
            if (tracker.getAttacksWithoutHit() > 1000) {
                tracker.registerOffense(CheatingOffense.ATTACK_WITHOUT_GETTING_HIT, Integer.toString(tracker.getAttacksWithoutHit()));
            }
        }
    }

    public static final void applyAttackMagic(AttackInfo attack, ISkill theSkill, MapleCharacter player, MapleStatEffect effect) {
        double maxDamagePerHit;
        if (!player.isAlive()) {
            player.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_WHILE_DEAD);
            return;
        }
        if (attack.real) {
            player.getCheatTracker().checkAttack(attack.skill, attack.lastAttackTickCount);
        }
        if (attack.hits > effect.getAttackCount() || attack.targets > effect.getMobCount()) {
            player.getCheatTracker().registerOffense(CheatingOffense.MISMATCHING_BULLETCOUNT);
            return;
        }
        if (attack.hits > 0 && attack.targets > 0 && !player.getStat().checkEquipDurabilitys(player, -1)) {
            player.dropMessage(5, "An item has run out of durability but has no inventory room to go to.");
            return;
        }
        if (GameConstants.isMulungSkill(attack.skill)) {
            if (player.getMapId() / 10000 != 92502) {
                return;
            }
            player.mulung_EnergyModify(false);
        }
        if (GameConstants.isPyramidSkill(attack.skill)) {
            if (player.getMapId() / 1000000 != 926) {
                return;
            }
            if (player.getPyramidSubway() == null || !player.getPyramidSubway().onSkillUse(player)) {
                return;
            }
        }
        PlayerStats stats = player.getStat();
        if (attack.skill == 2301002) {
            maxDamagePerHit = 30000.0;
        } else if (attack.skill == 1000 || attack.skill == 10001000 || attack.skill == 20001000 || attack.skill == 20011000 || attack.skill == 30001000) {
            maxDamagePerHit = 40.0;
        } else if (GameConstants.isPyramidSkill(attack.skill)) {
            maxDamagePerHit = 1.0;
        } else {
            double v75 = (double)effect.getMatk() * 0.058;
            maxDamagePerHit = (double)stats.getTotalMagic() * ((double)stats.getInt() * 0.5 + v75 * v75 + (double)effect.getMatk() * 3.3) / 100.0;
        }
        maxDamagePerHit *= 1.04;
        Element element = player.getBuffedValue(MapleBuffStat.ELEMENT_RESET) != null ? Element.NEUTRAL : theSkill.getElement();
        double MaxDamagePerHit = 0.0;
        int totDamage = 0;
        short CriticalDamage = stats.passive_sharpeye_percent();
        ISkill eaterSkill = SkillFactory.getSkill(GameConstants.getMPEaterForJob(player.getJob()));
        byte eaterLevel = player.getSkillLevel(eaterSkill);
        MapleMap map = player.getMap();
        int d = 0;
        for (AttackPair oned : attack.allDamage) {
            MapleMonster monster = map.getMonsterByOid(oned.objectid);
            if (monster != null) {
                boolean Tempest = monster.getStatusSourceID(MonsterStatus.FREEZE) == 21120006 && !monster.getStats().isBoss();
                int totDamageToOneMonster = 0;
                MapleMonsterStats monsterstats = monster.getStats();
                int fixeddmg = monsterstats.getFixedDamage();
                if (!Tempest && !player.isGM()) {
                    MaxDamagePerHit = !monster.isBuffed(MonsterStatus.DAMAGE_IMMUNITY) && !monster.isBuffed(MonsterStatus.MAGIC_IMMUNITY) && !monster.isBuffed(MonsterStatus.MAGIC_DAMAGE_REFLECT) ? DamageParse.CalculateMaxMagicDamagePerHit(player, theSkill, monster, monsterstats, stats, element, Integer.valueOf(CriticalDamage), maxDamagePerHit) : 1.0;
                }
                int overallAttackCount = 0;
                for (Pair<Integer, Boolean> pair : oned.attack) {
                    Integer eachd = (Integer)pair.left;
                    overallAttackCount = (byte)(overallAttackCount + 1);
                    if (fixeddmg != -1) {
                        eachd = monsterstats.getOnlyNoramlAttack() ? 0 : fixeddmg;
                    } else if (monsterstats.getOnlyNoramlAttack()) {
                        eachd = 0;
                    } else if (!player.isGM()) {
                        if (Tempest) {
                            if ((long)eachd.intValue() > monster.getMobMaxHp()) {
                                eachd = (int)Math.min(monster.getMobMaxHp(), Integer.MAX_VALUE);
                                player.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE_MAGIC);
                            }
                        } else if (!(monster.isBuffed(MonsterStatus.DAMAGE_IMMUNITY) || monster.isBuffed(MonsterStatus.MAGIC_IMMUNITY) || monster.isBuffed(MonsterStatus.MAGIC_DAMAGE_REFLECT))) {
                            if ((double)eachd.intValue() > maxDamagePerHit * 2.0) {
                                player.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE_MAGIC);
                                if ((double)eachd.intValue() > MaxDamagePerHit * 3.0) {
                                    eachd = (int)(MaxDamagePerHit * 3.0);
                                    player.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE_MAGIC_2);
                                }
                            }
                        } else if ((double)eachd.intValue() > maxDamagePerHit) {
                            eachd = (int)maxDamagePerHit;
                        }
                    }
                    totDamageToOneMonster += eachd.intValue();
                }
                totDamage += totDamageToOneMonster;
                player.checkMonsterAggro(monster);
                if (player.getPosition().distanceSq(monster.getPosition()) > 700000.0) {
                    player.getCheatTracker().registerOffense(CheatingOffense.ATTACK_FARAWAY_MONSTER);
                }
                if (attack.skill == 2301002 && !monsterstats.getUndead()) {
                    player.getCheatTracker().registerOffense(CheatingOffense.HEAL_ATTACKING_UNDEAD);
                    return;
                }
                if (totDamageToOneMonster > 0) {
                    monster.damage(player, totDamageToOneMonster, true, attack.skill);
                    if (monster.isBuffed(MonsterStatus.MAGIC_DAMAGE_REFLECT)) {
                        player.addHP(-(7000 + Randomizer.nextInt(8000)));
                    }
                    switch (attack.skill) {
                        case 2221003: {
                            monster.setTempEffectiveness(Element.FIRE, theSkill.getEffect(player.getSkillLevel(theSkill)).getDuration() + d);
                            break;
                        }
                        case 2121003: {
                            monster.setTempEffectiveness(Element.ICE, theSkill.getEffect(player.getSkillLevel(theSkill)).getDuration() + d);
                        }
                    }
                    if (effect != null && effect.getMonsterStati().size() > 0 && effect.makeChanceResult()) {
                        for (Map.Entry entry : effect.getMonsterStati().entrySet()) {
                            monster.applyStatus(player, new MonsterStatusEffect((MonsterStatus)entry.getKey(), (Integer)entry.getValue(), theSkill.getId(), null, false), effect.isPoison(), (long)(effect.getDuration() + d), false, d);
                        }
                    }
                    if (eaterLevel > 0) {
                        eaterSkill.getEffect(eaterLevel).applyPassive(player, monster);
                    }
                }
            }
            d += 50;
        }
        if (attack.skill != 2301002) {
            effect.applyTo(player);
        }
        if (totDamage > 1) {
            CheatTracker tracker = player.getCheatTracker();
            tracker.setAttacksWithoutHit(true);
            if (tracker.getAttacksWithoutHit() > 1000) {
                tracker.registerOffense(CheatingOffense.ATTACK_WITHOUT_GETTING_HIT, Integer.toString(tracker.getAttacksWithoutHit()));
            }
        }
    }

    private static final double CalculateMaxMagicDamagePerHit(MapleCharacter chr, ISkill skill, MapleMonster monster, MapleMonsterStats mobstats, PlayerStats stats, Element elem, Integer sharpEye, double maxDamagePerMonster) {
        double elemMaxDamagePerMob;
        int dLevel = Math.max(mobstats.getLevel() - chr.getLevel(), 0);
        int Accuracy = (int)(Math.floor((double)stats.getTotalInt() / 10.0) + Math.floor((double)stats.getTotalLuk() / 10.0));
        int MinAccuracy = mobstats.getEva() * (dLevel * 2 + 51) / 120;
        if (MinAccuracy > Accuracy && skill.getId() != 1000 && skill.getId() != 10001000 && skill.getId() != 20001000 && skill.getId() != 20011000 && skill.getId() != 30001000 && !GameConstants.isPyramidSkill(skill.getId())) {
            return 0.0;
        }
        switch (monster.getEffectiveness(elem)) {
            case IMMUNE: {
                elemMaxDamagePerMob = 1.0;
                break;
            }
            case NORMAL: {
                elemMaxDamagePerMob = DamageParse.ElementalStaffAttackBonus(elem, maxDamagePerMonster, stats);
                break;
            }
            case WEAK: {
                elemMaxDamagePerMob = DamageParse.ElementalStaffAttackBonus(elem, maxDamagePerMonster * 1.5, stats);
                break;
            }
            case STRONG: {
                elemMaxDamagePerMob = DamageParse.ElementalStaffAttackBonus(elem, maxDamagePerMonster * 0.5, stats);
                break;
            }
            default: {
                throw new RuntimeException("Unknown enum constant");
            }
        }
        elemMaxDamagePerMob -= (double)mobstats.getMagicDefense() * 0.5;
        elemMaxDamagePerMob += elemMaxDamagePerMob / 100.0 * (double)sharpEye.intValue();
        elemMaxDamagePerMob += elemMaxDamagePerMob * (mobstats.isBoss() ? stats.bossdam_r : stats.dam_r) / 100.0;
        switch (skill.getId()) {
            case 1000: 
            case 10001000: 
            case 20001000: 
            case 20011000: 
            case 30001000: {
                elemMaxDamagePerMob = 40.0;
                break;
            }
            case 1020: 
            case 10001020: 
            case 20001020: 
            case 20011020: 
            case 30001020: {
                elemMaxDamagePerMob = 1.0;
            }
        }
        if (elemMaxDamagePerMob > 199999.0) {
            elemMaxDamagePerMob = 199999.0;
        } else if (elemMaxDamagePerMob < 0.0) {
            elemMaxDamagePerMob = 1.0;
        }
        return elemMaxDamagePerMob;
    }

    private static final double ElementalStaffAttackBonus(Element elem, double elemMaxDamagePerMob, PlayerStats stats) {
        switch (elem) {
            case FIRE: {
                return elemMaxDamagePerMob / 100.0 * (double)stats.element_fire;
            }
            case ICE: {
                return elemMaxDamagePerMob / 100.0 * (double)stats.element_ice;
            }
            case LIGHTING: {
                return elemMaxDamagePerMob / 100.0 * (double)stats.element_light;
            }
            case POISON: {
                return elemMaxDamagePerMob / 100.0 * (double)stats.element_psn;
            }
        }
        return elemMaxDamagePerMob / 100.0 * (double)stats.def;
    }

    private static void handlePickPocket(final MapleCharacter player, final MapleMonster mob2, AttackPair oned) {
        final int maxmeso = player.getBuffedValue(MapleBuffStat.PICKPOCKET);
        ISkill skill = SkillFactory.getSkill(4211003);
        MapleStatEffect s = skill.getEffect(player.getSkillLevel(skill));
        for (Pair<Integer, Boolean> eachde : oned.attack) {
            final Integer eachd = (Integer)eachde.left;
            if (!s.makeChanceResult()) continue;
            Timer.MapTimer.getInstance().schedule(new Runnable(){

                @Override
                public void run() {
                    player.getMap().spawnMesoDrop(Math.min((int)Math.max((double)eachd.intValue() / 20000.0 * (double)maxmeso, 1.0), maxmeso), new Point((int)(mob2.getPosition().getX() + (double)Randomizer.nextInt(100) - 50.0), (int)mob2.getPosition().getY()), mob2, player, true, (byte)0);
                }
            }, 100L);
        }
    }

    private static double CalculateMaxWeaponDamagePerHit(MapleCharacter player, MapleMonster monster, AttackInfo attack, ISkill theSkill, MapleStatEffect attackEffect, double maximumDamageToMonster, Integer CriticalDamagePercent) {
        short moblevel;
        if (player.getMapId() / 1000000 == 914) {
            return 199999.0;
        }
        ArrayList<Element> elements = new ArrayList<Element>();
        boolean defined = false;
        if (theSkill != null) {
            elements.add(theSkill.getElement());
            switch (theSkill.getId()) {
                case 3001004: 
                case 33101001: {
                    defined = true;
                    break;
                }
                case 1000: 
                case 10001000: 
                case 20001000: 
                case 20011000: 
                case 30001000: {
                    maximumDamageToMonster = 40.0;
                    defined = true;
                    break;
                }
                case 1020: 
                case 10001020: 
                case 20001020: 
                case 20011020: 
                case 30001020: {
                    maximumDamageToMonster = 1.0;
                    defined = true;
                    break;
                }
                case 4331003: {
                    maximumDamageToMonster = monster.getStats().isBoss() ? 199999L : monster.getHp();
                    defined = true;
                    break;
                }
                case 3221007: {
                    maximumDamageToMonster = monster.getStats().isBoss() ? 199999L : monster.getMobMaxHp();
                    defined = true;
                    break;
                }
                case 1221011: {
                    maximumDamageToMonster = monster.getStats().isBoss() ? 199999L : monster.getHp() - 1L;
                    defined = true;
                    break;
                }
                case 4211006: {
                    maximumDamageToMonster = 750000.0;
                    defined = true;
                    break;
                }
                case 1009: 
                case 10001009: 
                case 20001009: 
                case 20011009: 
                case 30001009: {
                    defined = true;
                    maximumDamageToMonster = monster.getStats().isBoss() ? monster.getMobMaxHp() / 30L * 100L : monster.getMobMaxHp();
                    break;
                }
                case 3211006: {
                    if (monster.getStatusSourceID(MonsterStatus.FREEZE) != 3211003) break;
                    defined = true;
                    maximumDamageToMonster = monster.getHp();
                }
            }
        }
        if (player.getBuffedValue(MapleBuffStat.WK_CHARGE) != null) {
            int chargeSkillId = player.getBuffSource(MapleBuffStat.WK_CHARGE);
            switch (chargeSkillId) {
                case 1211003: 
                case 1211004: {
                    elements.add(Element.FIRE);
                    break;
                }
                case 1211005: 
                case 1211006: 
                case 21111005: {
                    elements.add(Element.ICE);
                    break;
                }
                case 1211007: 
                case 1211008: 
                case 15101006: {
                    elements.add(Element.LIGHTING);
                    break;
                }
                case 1221003: 
                case 1221004: 
                case 11111007: {
                    elements.add(Element.HOLY);
                    break;
                }
                case 12101005: {
                    elements.clear();
                }
            }
        }
        if (player.getBuffedValue(MapleBuffStat.LIGHTNING_CHARGE) != null) {
            elements.add(Element.LIGHTING);
        }
        double elementalMaxDamagePerMonster = maximumDamageToMonster;
        if (elements.size() > 0) {
            double elementalEffect;
            switch (attack.skill) {
                case 3111003: 
                case 3211003: {
                    elementalEffect = (double)attackEffect.getX() / 200.0;
                    break;
                }
                default: {
                    elementalEffect = 0.5;
                }
            }
            for (Element element : elements) {
                switch (monster.getEffectiveness(element)) {
                    case IMMUNE: {
                        elementalMaxDamagePerMonster = 1.0;
                        break;
                    }
                    case WEAK: {
                        elementalMaxDamagePerMonster *= 1.0 + elementalEffect;
                        break;
                    }
                    case STRONG: {
                        elementalMaxDamagePerMonster *= 1.0 - elementalEffect;
                        break;
                    }
                }
            }
        }
        short d = (moblevel = monster.getStats().getLevel()) > player.getLevel() ? (short)(moblevel - player.getLevel()) : (short)0;
        elementalMaxDamagePerMonster = elementalMaxDamagePerMonster * (1.0 - 0.01 * (double)d) - (double)monster.getStats().getPhysicalDefense() * 0.5;
        elementalMaxDamagePerMonster += elementalMaxDamagePerMonster / 100.0 * (double)CriticalDamagePercent.intValue();
        if (theSkill != null && theSkill.isChargeSkill() && player.getKeyDownSkill_Time() == 0L) {
            return 0.0;
        }
        MapleStatEffect homing = player.getStatForBuff(MapleBuffStat.HOMING_BEACON);
        if (homing != null && player.getLinkMid() == monster.getObjectId() && homing.getSourceId() == 5220011) {
            elementalMaxDamagePerMonster += elementalMaxDamagePerMonster * (double)homing.getX();
        }
        PlayerStats stat = player.getStat();
        if ((elementalMaxDamagePerMonster += elementalMaxDamagePerMonster * (monster.getStats().isBoss() ? stat.bossdam_r : stat.dam_r) / 100.0) > 199999.0) {
            if (!defined) {
                elementalMaxDamagePerMonster = 199999.0;
            }
        } else if (elementalMaxDamagePerMonster < 0.0) {
            elementalMaxDamagePerMonster = 1.0;
        }
        return elementalMaxDamagePerMonster;
    }

    public static final AttackInfo DivideAttack(AttackInfo attack, int rate) {
        attack.real = false;
        if (rate <= 1) {
            return attack;
        }
        for (AttackPair p : attack.allDamage) {
            if (p.attack == null) continue;
            Iterator<Pair<Integer, Boolean>> iterator = p.attack.iterator();
            while (iterator.hasNext()) {
                Pair<Integer, Boolean> eachd;
                Pair<Integer, Boolean> pair = eachd = iterator.next();
                Integer.valueOf((Integer)pair.left / rate);
                pair.left = pair.left;
            }
        }
        return attack;
    }

    public static final AttackInfo Modify_AttackCrit(AttackInfo attack, MapleCharacter chr, int type) {
        boolean shadow;
        byte CriticalRate = chr.getStat().passive_sharpeye_rate();
        boolean bl = shadow = type == 2 && chr.getBuffedValue(MapleBuffStat.SHADOWPARTNER) != null || type == 1 && chr.getBuffedValue(MapleBuffStat.MIRROR_IMAGE) != null;
        if (attack.skill != 4211006 && attack.skill != 3211003 && attack.skill != 4111004 && (CriticalRate > 0 || attack.skill == 4221001 || attack.skill == 3221007)) {
            for (AttackPair p : attack.allDamage) {
                if (p.attack == null) continue;
                int hit = 0;
                int mid_att = p.attack.size() / 2;
                ArrayList<Pair<Integer, Boolean>> eachd_copy = new ArrayList<Pair<Integer, Boolean>>(p.attack);
                for (Pair<Integer, Boolean> eachd : p.attack) {
                    ++hit;
                    if (((Boolean)eachd.right).booleanValue()) continue;
                    eachd.right = attack.skill == 4221001 ? Boolean.valueOf(hit == 4 && Randomizer.nextInt(100) < 90) : (attack.skill == 3221007 || (Integer)eachd.left > 199999 ? Boolean.valueOf(true) : (shadow && hit > mid_att ? (Boolean)((Pair)eachd_copy.get((int)(hit - 1 - mid_att))).right : Boolean.valueOf(Randomizer.nextInt(100) < CriticalRate)));
                    ((Pair)eachd_copy.get((int)(hit - 1))).right = eachd.right;
                }
            }
        }
        return attack;
    }

    public static final AttackInfo parseDmgMa(LittleEndianAccessor lea) {
        AttackInfo ret = new AttackInfo();
        lea.skip(1);
        boolean unkk = lea.readByte() == -1;
        lea.skip(unkk ? 7 : 8);
        ret.tbyte = lea.readByte();
        ret.targets = (byte)(ret.tbyte >>> 4 & 0xF);
        ret.hits = (byte)(ret.tbyte & 0xF);
        lea.skip(8);
        ret.skill = lea.readInt();
        lea.skip(12);
        switch (ret.skill) {
            case 2121001: 
            case 2221001: 
            case 2321001: 
            case 22121000: 
            case 22151001: {
                ret.charge = lea.readInt();
                break;
            }
            default: {
                ret.charge = -1;
            }
        }
        ret.unk = lea.readByte();
        ret.display = lea.readByte();
        ret.animation = lea.readByte();
        lea.skip(1);
        ret.speed = lea.readByte();
        ret.lastAttackTickCount = lea.readInt();
        lea.skip(4);
        ret.allDamage = new ArrayList<AttackPair>();
        for (int i = 0; i < ret.targets; ++i) {
            int oid = lea.readInt();
            lea.skip(14);
            ArrayList<Pair<Integer, Boolean>> allDamageNumbers = new ArrayList<Pair<Integer, Boolean>>();
            for (int j = 0; j < ret.hits; ++j) {
                int damage = lea.readInt();
                allDamageNumbers.add(new Pair<Integer, Boolean>(damage, false));
            }
            lea.skip(4);
            ret.allDamage.add(new AttackPair(oid, allDamageNumbers));
        }
        ret.position = lea.readPos();
        return ret;
    }

    public static final AttackInfo parseDmgM(LittleEndianAccessor lea) {
        AttackInfo ret = new AttackInfo();
        lea.skip(1);
        boolean unkk = lea.readByte() == -1;
        lea.skip(unkk ? 7 : 8);
        ret.tbyte = lea.readByte();
        ret.targets = (byte)(ret.tbyte >>> 4 & 0xF);
        ret.hits = (byte)(ret.tbyte & 0xF);
        lea.skip(8);
        ret.skill = lea.readInt();
        lea.skip(12);
        switch (ret.skill) {
            case 4341002: 
            case 4341003: 
            case 5101004: 
            case 5201002: 
            case 14111006: 
            case 15101003: {
                ret.charge = lea.readInt();
                break;
            }
            default: {
                ret.charge = 0;
            }
        }
        ret.unk = lea.readByte();
        ret.display = lea.readByte();
        ret.animation = lea.readByte();
        lea.skip(1);
        ret.speed = lea.readByte();
        ret.lastAttackTickCount = lea.readInt();
        lea.skip(4);
        ret.allDamage = new ArrayList<AttackPair>();
        if (ret.skill == 4211006) {
            return DamageParse.parseMesoExplosion(lea, ret);
        }
        for (int i = 0; i < ret.targets; ++i) {
            int oid = lea.readInt();
            lea.skip(14);
            ArrayList<Pair<Integer, Boolean>> allDamageNumbers = new ArrayList<Pair<Integer, Boolean>>();
            for (int j = 0; j < ret.hits; ++j) {
                int damage = lea.readInt();
                allDamageNumbers.add(new Pair<Integer, Boolean>(damage, false));
            }
            lea.skip(4);
            ret.allDamage.add(new AttackPair(oid, allDamageNumbers));
        }
        if (ret.skill == 14111006) {
            lea.skip(4);
        }
        ret.position = lea.readPos();
        return ret;
    }

    public static final AttackInfo parseDmgR(LittleEndianAccessor lea) {
        AttackInfo ret = new AttackInfo();
        lea.skip(1);
        boolean unkk = lea.readByte() == -1;
        lea.skip(unkk ? 7 : 8);
        ret.tbyte = lea.readByte();
        ret.targets = (byte)(ret.tbyte >>> 4 & 0xF);
        ret.hits = (byte)(ret.tbyte & 0xF);
        lea.skip(8);
        ret.skill = lea.readInt();
        lea.skip(12);
        switch (ret.skill) {
            case 3121004: 
            case 3221001: 
            case 5221004: 
            case 13111002: 
            case 33121009: {
                lea.skip(4);
            }
        }
        ret.charge = -1;
        ret.unk = lea.readByte();
        ret.display = lea.readByte();
        ret.animation = lea.readByte();
        lea.skip(1);
        ret.speed = lea.readByte();
        ret.lastAttackTickCount = lea.readInt();
        lea.skip(4);
        ret.slot = (byte)lea.readShort();
        ret.csstar = (byte)lea.readShort();
        ret.AOE = lea.readByte();
        ret.allDamage = new ArrayList<AttackPair>();
        for (int i = 0; i < ret.targets; ++i) {
            int oid = lea.readInt();
            lea.skip(14);
            ArrayList<Pair<Integer, Boolean>> allDamageNumbers = new ArrayList<Pair<Integer, Boolean>>();
            for (int j = 0; j < ret.hits; ++j) {
                int damage = lea.readInt();
                allDamageNumbers.add(new Pair<Integer, Boolean>(damage, false));
            }
            lea.skip(4);
            ret.allDamage.add(new AttackPair(oid, allDamageNumbers));
        }
        lea.skip(4);
        ret.position = lea.readPos();
        return ret;
    }

    public static final AttackInfo parseMesoExplosion(LittleEndianAccessor lea, AttackInfo ret) {
        int bullets;
        if (ret.hits == 0) {
            lea.skip(4);
            int bullets2 = lea.readByte();
            for (int j = 0; j < bullets2; ++j) {
                ret.allDamage.add(new AttackPair(lea.readInt(), null));
                lea.skip(1);
            }
            lea.skip(2);
            return ret;
        }
        for (int i = 0; i < ret.targets; ++i) {
            int oid = lea.readInt();
            lea.skip(12);
            bullets = lea.readByte();
            ArrayList<Pair<Integer, Boolean>> allDamageNumbers = new ArrayList<Pair<Integer, Boolean>>();
            for (int j = 0; j < bullets; ++j) {
                allDamageNumbers.add(new Pair<Integer, Boolean>(lea.readInt(), false));
            }
            ret.allDamage.add(new AttackPair(oid, allDamageNumbers));
            lea.skip(4);
        }
        lea.skip(4);
        bullets = lea.readByte();
        for (int j = 0; j < bullets; ++j) {
            ret.allDamage.add(new AttackPair(lea.readInt(), null));
            lea.skip(1);
        }
        lea.skip(2);
        return ret;
    }
}

