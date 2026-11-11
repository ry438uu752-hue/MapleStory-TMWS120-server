/*
 * Decompiled with CFR 0.152.
 */
package handling.channel.handler;

import client.ISkill;
import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.PlayerStats;
import client.SkillFactory;
import client.SkillMacro;
import client.anticheat.CheatingOffense;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import constants.GameConstants;
import constants.MapConstants;
import handling.channel.ChannelServer;
import handling.channel.handler.AttackInfo;
import handling.channel.handler.AttackType;
import handling.channel.handler.DamageParse;
import handling.channel.handler.MovementParse;
import java.awt.Point;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import server.AutobanManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.MapleStatEffect;
import server.Randomizer;
import server.Timer;
import server.events.MapleSnowball;
import server.life.MapleMonster;
import server.life.MobAttackInfo;
import server.life.MobAttackInfoFactory;
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
import server.movement.LifeMovementFragment;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.MTSCSPacket;
import tools.packet.MobPacket;
import tools.packet.UIPacket;

public class PlayerHandler {
    private static boolean isFinisher(int skillid) {
        switch (skillid) {
            case 1111003: 
            case 1111004: 
            case 1111005: 
            case 1111006: 
            case 11111002: 
            case 11111003: {
                return true;
            }
        }
        return false;
    }

    public static void ChangeMonsterBookCover(int bookid, MapleClient c, MapleCharacter chr) {
        if (bookid == 0 || GameConstants.isMonsterCard(bookid)) {
            chr.setMonsterBookCover(bookid);
            chr.getMonsterBook().updateCard(c, bookid);
        }
    }

    public static void ChangeSkillMacro(SeekableLittleEndianAccessor slea, MapleCharacter chr) {
        int num = slea.readByte();
        for (int i = 0; i < num; ++i) {
            String name = slea.readMapleAsciiString();
            byte shout = slea.readByte();
            int skill1 = slea.readInt();
            int skill2 = slea.readInt();
            int skill3 = slea.readInt();
            SkillMacro macro = new SkillMacro(skill1, skill2, skill3, name, shout, i);
            chr.updateMacros(i, macro);
        }
    }

    public static final void ChangeKeymap(SeekableLittleEndianAccessor slea, MapleCharacter chr) {
        if (slea.available() > 8L && chr != null) {
            chr.updateTick(slea.readInt());
            int numChanges = slea.readInt();
            for (int i = 0; i < numChanges; ++i) {
                chr.changeKeybinding(slea.readInt(), slea.readByte(), slea.readInt());
            }
        }
    }

    public static final void UseChair(int itemId, MapleClient c, MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        IItem toUse = chr.getInventory(MapleInventoryType.SETUP).findById(itemId);
        if (toUse == null) {
            chr.getCheatTracker().registerOffense(CheatingOffense.USING_UNAVAILABLE_ITEM, Integer.toString(itemId));
            return;
        }
        int map = chr.getMapId();
        if (map == 749050500 || map == 749050501 || map == 749050502) {
            boolean haz = false;
            for (IItem item : c.getPlayer().getInventory(MapleInventoryType.CASH).list()) {
                if (item.getItemId() == 5340000) {
                    haz = true;
                    continue;
                }
                if (item.getItemId() != 5340001) continue;
                haz = false;
                chr.startFishingTask(true);
                break;
            }
            if (haz) {
                chr.startFishingTask(false);
            }
        }
        chr.setChair(itemId);
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.showChair(chr.getId(), itemId), false);
        c.getSession().write((Object)MaplePacketCreator.enableActions());
    }

    public static final void CancelChair(short id, MapleClient c, MapleCharacter chr) {
        if (id == -1) {
            int map = chr.getMapId();
            if (map == 749050500 || map == 749050501 || map == 749050502) {
                chr.cancelFishingTask();
            }
            chr.setChair(0);
            c.getSession().write((Object)MaplePacketCreator.cancelChair(-1));
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.showChair(chr.getId(), 0), false);
        } else {
            chr.setChair(id);
            c.getSession().write((Object)MaplePacketCreator.cancelChair(id));
        }
    }

    public static final void TrockAddMap(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        byte addrem = slea.readByte();
        byte vip = slea.readByte();
        if (vip == 1) {
            if (addrem == 0) {
                chr.deleteFromRocks(slea.readInt());
            } else if (addrem == 1) {
                if (!FieldLimitType.VipRock.check(chr.getMap().getFieldLimit())) {
                    chr.addRockMap();
                } else {
                    chr.dropMessage(1, "You may not add this map.");
                }
            }
        } else if (addrem == 0) {
            chr.deleteFromRegRocks(slea.readInt());
        } else if (addrem == 1) {
            if (!FieldLimitType.VipRock.check(chr.getMap().getFieldLimit())) {
                chr.addRegRockMap();
            } else {
                chr.dropMessage(1, "You may not add this map.");
            }
        }
        c.getSession().write((Object)MTSCSPacket.getTrockRefresh(chr, vip == 1, addrem == 3));
    }

    public static final void CharInfoRequest(int objectid, MapleClient c, MapleCharacter chr) {
        if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
            return;
        }
        MapleCharacter player = c.getPlayer().getMap().getCharacterById(objectid);
        c.getSession().write((Object)MaplePacketCreator.enableActions());
        if (!(player == null || player.isClone() || player.isGM() && !c.getPlayer().isGM())) {
            c.getSession().write((Object)MaplePacketCreator.charInfo(player, c.getPlayer().getId() == objectid));
        }
    }

    public static final void TakeDamage(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        MobSkill skill;
        chr.updateTick(slea.readInt());
        byte type = slea.readByte();
        slea.skip(1);
        int damage = slea.readInt();
        int oid = 0;
        int monsteridfrom = 0;
        int reflect = 0;
        byte direction = 0;
        int pos_x = 0;
        int pos_y = 0;
        int fake = 0;
        int mpattack = 0;
        boolean is_pg = false;
        boolean isDeadlyAttack = false;
        MapleMonster attacker = null;
        if (chr == null || chr.isHidden() || chr.getMap() == null) {
            return;
        }
        if (chr.isGM() && chr.isInvincible()) {
            return;
        }
        PlayerStats stats = chr.getStat();
        if (type != -2 && type != -3 && type != -4) {
            MobAttackInfo attackInfo;
            monsteridfrom = slea.readInt();
            oid = slea.readInt();
            attacker = chr.getMap().getMonsterByOid(oid);
            direction = slea.readByte();
            if (attacker == null) {
                return;
            }
            if (type != -1 && (attackInfo = MobAttackInfoFactory.getInstance().getMobAttackInfo(attacker, type)) != null) {
                if (attackInfo.isDeadlyAttack()) {
                    isDeadlyAttack = true;
                    mpattack = stats.getMp() - 1;
                } else {
                    mpattack += attackInfo.getMpBurn();
                }
                skill = MobSkillFactory.getMobSkill(attackInfo.getDiseaseSkill(), attackInfo.getDiseaseLevel());
                if (skill != null && (damage == -1 || damage > 0)) {
                    skill.applyEffect(chr, attacker, false);
                }
                attacker.setMp(attacker.getMp() - attackInfo.getMpCon());
            }
        }
        if (damage == -1) {
            fake = 4020002 + (chr.getJob() / 10 - 40) * 100000;
        } else if (damage < -1 || damage > 60000) {
            AutobanManager.getInstance().addPoints(c, 1000, 60000L, "Taking abnormal amounts of damge from " + monsteridfrom + ": " + damage);
            return;
        }
        chr.getCheatTracker().checkTakeDamage(damage);
        if (damage > 0) {
            MapleStatEffect blueAura;
            MapleStatEffect magicShield;
            int bouncedam_;
            byte level;
            chr.getCheatTracker().setAttacksWithoutHit(false);
            if (chr.getBuffedValue(MapleBuffStat.MORPH) != null) {
                chr.cancelMorphs();
            }
            if (slea.available() == 3L && (level = slea.readByte()) > 0 && (skill = MobSkillFactory.getMobSkill(slea.readShort(), level)) != null) {
                skill.applyEffect(chr, attacker, false);
            }
            if (type != -2 && type != -3 && type != -4 && (bouncedam_ = (Randomizer.nextInt(100) < chr.getStat().DAMreflect_rate ? chr.getStat().DAMreflect : 0) + (type == -1 && chr.getBuffedValue(MapleBuffStat.POWERGUARD) != null ? chr.getBuffedValue(MapleBuffStat.POWERGUARD) : 0) + (type == -1 && chr.getBuffedValue(MapleBuffStat.PERFECT_ARMOR) != null ? chr.getBuffedValue(MapleBuffStat.PERFECT_ARMOR) : 0)) > 0 && attacker != null) {
                long bouncedamage = damage * bouncedam_ / 100;
                bouncedamage = Math.min(bouncedamage, attacker.getMobMaxHp() / 10L);
                attacker.damage(chr, bouncedamage, true);
                damage = (int)((long)damage - bouncedamage);
                chr.getMap().broadcastMessage(chr, MobPacket.damageMonster(oid, bouncedamage), chr.getPosition());
                is_pg = true;
            }
            if (type != -1 && type != -2 && type != -3 && type != -4) {
                switch (chr.getJob()) {
                    case 112: {
                        ISkill skill2 = SkillFactory.getSkill(1120004);
                        if (chr.getSkillLevel(skill2) <= 0) break;
                        damage = (int)((double)skill2.getEffect(chr.getSkillLevel(skill2)).getX() / 1000.0 * (double)damage);
                        break;
                    }
                    case 122: {
                        ISkill skill2 = SkillFactory.getSkill(1220005);
                        if (chr.getSkillLevel(skill2) <= 0) break;
                        damage = (int)((double)skill2.getEffect(chr.getSkillLevel(skill2)).getX() / 1000.0 * (double)damage);
                        break;
                    }
                    case 132: {
                        ISkill skill2 = SkillFactory.getSkill(1320005);
                        if (chr.getSkillLevel(skill2) <= 0) break;
                        damage = (int)((double)skill2.getEffect(chr.getSkillLevel(skill2)).getX() / 1000.0 * (double)damage);
                        break;
                    }
                }
            }
            if ((magicShield = chr.getStatForBuff(MapleBuffStat.MAGIC_SHIELD)) != null) {
                damage -= (int)((double)magicShield.getX() / 100.0 * (double)damage);
            }
            if ((blueAura = chr.getStatForBuff(MapleBuffStat.BLUE_AURA)) != null) {
                damage -= (int)((double)blueAura.getY() / 100.0 * (double)damage);
            }
            if (chr.getBuffedValue(MapleBuffStat.SATELLITESAFE_PROC) != null && chr.getBuffedValue(MapleBuffStat.SATELLITESAFE_ABSORB) != null) {
                double buff = chr.getBuffedValue(MapleBuffStat.SATELLITESAFE_PROC).doubleValue();
                double buffz = chr.getBuffedValue(MapleBuffStat.SATELLITESAFE_ABSORB).doubleValue();
                if ((int)(buff / 100.0 * (double)chr.getStat().getMaxHp()) <= damage) {
                    damage -= (int)(buffz / 100.0 * (double)damage);
                    chr.cancelEffectFromBuffStat(MapleBuffStat.SUMMON);
                    chr.cancelEffectFromBuffStat(MapleBuffStat.REAPER);
                }
            }
            if (chr.getBuffedValue(MapleBuffStat.MAGIC_GUARD) != null) {
                int hploss = 0;
                int mploss = 0;
                if (isDeadlyAttack) {
                    if (stats.getHp() > 1) {
                        hploss = stats.getHp() - 1;
                    }
                    if (stats.getMp() > 1) {
                        mploss = stats.getMp() - 1;
                    }
                    if (chr.getBuffedValue(MapleBuffStat.INFINITY) != null) {
                        mploss = 0;
                    }
                    chr.addMPHP(-hploss, -mploss);
                } else {
                    mploss = (int)((double)damage * (chr.getBuffedValue(MapleBuffStat.MAGIC_GUARD).doubleValue() / 100.0)) + mpattack;
                    hploss = damage - mploss;
                    if (chr.getBuffedValue(MapleBuffStat.INFINITY) != null) {
                        mploss = 0;
                    } else if (mploss > stats.getMp()) {
                        mploss = stats.getMp();
                        hploss = damage - mploss + mpattack;
                    }
                    chr.addMPHP(-hploss, -mploss);
                }
            } else if (chr.getBuffedValue(MapleBuffStat.MESOGUARD) != null) {
                damage = damage % 2 == 0 ? damage / 2 : damage / 2 + 1;
                int mesoloss = (int)((double)damage * (chr.getBuffedValue(MapleBuffStat.MESOGUARD).doubleValue() / 100.0));
                if (chr.getMeso() < mesoloss) {
                    chr.gainMeso(-chr.getMeso(), false);
                    chr.cancelBuffStats(MapleBuffStat.MESOGUARD);
                } else {
                    chr.gainMeso(-mesoloss, false);
                }
                if (isDeadlyAttack && stats.getMp() > 1) {
                    mpattack = stats.getMp() - 1;
                }
                chr.addMPHP(-damage, -mpattack);
            } else if (isDeadlyAttack) {
                chr.addMPHP(stats.getHp() > 1 ? -(stats.getHp() - 1) : 0, stats.getMp() > 1 ? -(stats.getMp() - 1) : 0);
            } else {
                chr.addMPHP(-damage, -mpattack);
            }
            chr.handleBattleshipHP(damage);
        }
        if (!chr.isHidden()) {
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.damagePlayer(type, monsteridfrom, chr.getId(), damage, fake, direction, reflect, is_pg, oid, pos_x, pos_y), false);
        }
    }

    public static final void AranCombo(MapleClient c, MapleCharacter chr) {
        if (chr != null && chr.getJob() >= 2000 && chr.getJob() <= 2112) {
            short combo = chr.getCombo();
            long curr = System.currentTimeMillis();
            if (combo > 0 && curr - chr.getLastCombo() > 7000L) {
                combo = 0;
            }
            if (combo < 30000) {
                combo = (short)(combo + 1);
            }
            chr.setLastCombo(curr);
            chr.setCombo(combo);
            c.getSession().write((Object)MaplePacketCreator.testCombo(combo));
            switch (combo) {
                case 10: 
                case 20: 
                case 30: 
                case 40: 
                case 50: 
                case 60: 
                case 70: 
                case 80: 
                case 90: 
                case 100: {
                    if (chr.getSkillLevel(21000000) < combo / 10) break;
                    SkillFactory.getSkill(21000000).getEffect(combo / 10).applyComboBuff(chr, combo);
                }
            }
        }
    }

    public static final void UseItemEffect(int itemId, MapleClient c, MapleCharacter chr) {
        IItem toUse = chr.getInventory(MapleInventoryType.CASH).findById(itemId);
        if (toUse == null || toUse.getItemId() != itemId || toUse.getQuantity() < 1) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        if (itemId != 5510000) {
            chr.setItemEffect(itemId);
        }
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.itemEffect(chr.getId(), itemId), false);
    }

    public static final void CancelItemEffect(int id, MapleCharacter chr) {
        chr.cancelEffect(MapleItemInformationProvider.getInstance().getItemEffect(-id), false, -1L);
    }

    public static final void CancelBuffHandler(int sourceid, MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        ISkill skill = SkillFactory.getSkill(sourceid);
        if (skill.isChargeSkill()) {
            chr.setKeyDownSkill_Time(0L);
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.skillCancel(chr, sourceid), false);
        } else {
            chr.cancelEffect(skill.getEffect(1), false, -1L);
        }
    }

    public static final void SkillEffect(SeekableLittleEndianAccessor slea, MapleCharacter chr) {
        int skillId = slea.readInt();
        byte level = slea.readByte();
        byte flags = slea.readByte();
        byte speed = slea.readByte();
        byte unk = slea.readByte();
        ISkill skill = SkillFactory.getSkill(skillId);
        if (chr == null) {
            return;
        }
        byte skilllevel_serv = chr.getSkillLevel(skill);
        if (skilllevel_serv > 0 && skilllevel_serv == level && skill.isChargeSkill()) {
            chr.setKeyDownSkill_Time(System.currentTimeMillis());
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.skillEffect(chr, skillId, level, flags, speed, unk), false);
        }
    }

    public static final void ThrowSkill(SeekableLittleEndianAccessor slea, MapleClient c) {
        int x = slea.readInt();
        int y = slea.readInt();
        slea.skip(8);
        int skillid = slea.readInt();
        int skillLevel = slea.readInt();
        if (skillid == 4341003) {
            c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.skillCancel(c.getPlayer(), skillid));
            return;
        }
        if (skillid == 4321002) {
            int left = c.getPlayer().getFacingDirection();
            c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.skillEffect(c.getPlayer(), skillid, (byte)skillLevel, (byte)0, (byte)1, (byte)left), false);
        }
    }

    public static final void Monster_Bomb(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        MapleMonster monster = chr.getMap().getMonsterByOid(slea.readInt());
        int x = slea.readInt();
        int y = slea.readInt();
        if (monster == null || chr.getJob() != 434 || chr.getMap() == null || !chr.isAlive() || chr.isHidden()) {
            return;
        }
        ISkill skill = SkillFactory.getSkill(4341003);
        if (skill != null) {
            if (monster != null) {
                PlayerStats statst = chr.getStat();
                MapleMonster mob2 = monster;
                float basedamage = statst.calculateMaxBaseDamage(statst.getTotalWatk());
                double damage = Randomizer.rand((int)((double)basedamage * ((double)(800 + chr.getSkillLevel(skill) * 10) / 200.0)), (int)((double)basedamage * ((double)(800 + chr.getSkillLevel(skill) * 20) / 100.0)));
                c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MobPacket.damageMonster(monster.getObjectId(), (long)damage), true);
                mob2.damage(c.getPlayer(), (long)damage, true);
                c.getSession().write((Object)MaplePacketCreator.skillCooldown(skill.getId(), 50000));
                chr.addCooldown(4341003, System.currentTimeMillis(), 50000L);
            }
            c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.showMonsterBombEffect(monster.getPosition().x, monster.getPosition().y, chr.getSkillLevel(skill)), true);
        }
        c.getSession().write((Object)MaplePacketCreator.enableActions());
    }

    public static final void SpecialMove(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        MapleStatEffect effect;
        if (chr == null || !chr.isAlive() || chr.getMap() == null) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        slea.skip(4);
        int skillid = slea.readInt();
        byte skillLevel = slea.readByte();
        ISkill skill = SkillFactory.getSkill(skillid);
        if (chr.getSkillLevel(skill) <= 0 || chr.getSkillLevel(skill) != skillLevel) {
            if (!GameConstants.isMulungSkill(skillid) && !GameConstants.isPyramidSkill(skillid)) {
                c.getSession().close();
                return;
            }
            if (GameConstants.isMulungSkill(skillid)) {
                if (chr.getMapId() / 10000 != 92502) {
                    return;
                }
                chr.mulung_EnergyModify(false);
            } else if (GameConstants.isPyramidSkill(skillid) && chr.getMapId() / 10000 != 92602) {
                return;
            }
        }
        if ((effect = skill.getEffect(chr.getSkillLevel(GameConstants.getLinkedAranSkill(skillid)))).getCooldown() > 0 && !chr.isGM()) {
            if (chr.skillisCooling(skillid)) {
                c.getSession().write((Object)MaplePacketCreator.enableActions());
                return;
            }
            if (skillid != 5221006) {
                c.getSession().write((Object)MaplePacketCreator.skillCooldown(skillid, effect.getCooldown()));
                chr.addCooldown(skillid, System.currentTimeMillis(), effect.getCooldown() * 1000);
            }
        }
        switch (skillid) {
            case 1121001: 
            case 1221001: 
            case 1321001: 
            case 9001020: {
                int number_of_mobs = slea.readByte();
                slea.skip(3);
                for (int i = 0; i < number_of_mobs; ++i) {
                    int mobId = slea.readInt();
                    MapleMonster mob2 = chr.getMap().getMonsterByOid(mobId);
                    if (mob2 == null) continue;
                    mob2.switchController(chr, mob2.isControllerHasAggro());
                }
                chr.getMap().broadcastMessage(chr, MaplePacketCreator.showBuffeffect(chr.getId(), skillid, 1, slea.readByte()), chr.getPosition());
                c.getSession().write((Object)MaplePacketCreator.enableActions());
                break;
            }
            case 4341003: {
                int hasMonster = slea.readByte();
                boolean isPoison = false;
                if (skillid == 4121004 || skillid == 4221004) {
                    isPoison = true;
                }
                if (hasMonster > 0) {
                    MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(MonsterStatus.MONSTER_BOMB, 1, 4341003, null, false);
                    for (int i = 0; i < hasMonster; ++i) {
                        int mobid = slea.readInt();
                        MapleMonster monster = c.getPlayer().getMap().getMonsterByOid(mobid);
                        if (monster == null) continue;
                        monster.applyStatus(chr, monsterStatusEffect, false, 3000L, false);
                        chr.getMap().broadcastMessage(chr, MaplePacketCreator.giveHoming(skillid, mobid), chr.getPosition());
                    }
                }
                c.getSession().write((Object)MaplePacketCreator.enableActions());
                break;
            }
            default: {
                Point pos = null;
                if (slea.available() == 5L || slea.available() == 7L) {
                    pos = slea.readPos();
                }
                if (effect.isMagicDoor()) {
                    if (!FieldLimitType.MysticDoor.check(chr.getMap().getFieldLimit())) {
                        effect.applyTo(c.getPlayer(), pos);
                        break;
                    }
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    break;
                }
                int mountid = MapleStatEffect.parseMountInfo(c.getPlayer(), skill.getId());
                if (mountid != 0 && mountid != GameConstants.getMountItem(skill.getId()) && !c.getPlayer().isGM() && c.getPlayer().getBuffedValue(MapleBuffStat.MONSTER_RIDING) == null && c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-118) == null && !GameConstants.isMountItemAvailable(mountid, c.getPlayer().getJob())) {
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    return;
                }
                effect.applyTo(c.getPlayer(), pos);
            }
        }
    }

    public static final void closeRangeAttack(SeekableLittleEndianAccessor slea, MapleClient c, final MapleCharacter chr, final boolean energy) {
        if (chr == null || energy && chr.getBuffedValue(MapleBuffStat.ENERGY_CHARGE) == null && chr.getBuffedValue(MapleBuffStat.BODY_PRESSURE) == null && !GameConstants.isKOC(chr.getJob())) {
            return;
        }
        if (!chr.isAlive() || chr.getMap() == null) {
            chr.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_WHILE_DEAD);
            return;
        }
        AttackInfo attack = DamageParse.Modify_AttackCrit(DamageParse.parseDmgM(slea), chr, 1);
        final boolean mirror = chr.getBuffedValue(MapleBuffStat.MIRROR_IMAGE) != null;
        double maxdamage = chr.getStat().getCurrentMaxBaseDamage();
        int attackCount = chr.getJob() >= 430 && chr.getJob() <= 434 ? 2 : 1;
        byte skillLevel = 0;
        MapleStatEffect effect = null;
        ISkill skill = null;
        if (attack.skill != 0) {
            skill = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(attack.skill));
            skillLevel = chr.getSkillLevel(skill);
            effect = attack.getAttackEffect(chr, skillLevel, skill);
            if (effect == null) {
                return;
            }
            maxdamage *= (double)effect.getDamage() / 100.0;
            attackCount = effect.getAttackCount();
            if (effect.getCooldown() > 0 && !chr.isGM()) {
                if (chr.skillisCooling(attack.skill)) {
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    return;
                }
                c.getSession().write((Object)MaplePacketCreator.skillCooldown(attack.skill, effect.getCooldown()));
                chr.addCooldown(attack.skill, System.currentTimeMillis(), effect.getCooldown() * 1000);
            }
        }
        attackCount *= mirror ? 2 : 1;
        if (!energy) {
            if ((chr.getMapId() == 109060000 || chr.getMapId() == 109060002 || chr.getMapId() == 109060004) && attack.skill == 0) {
                MapleSnowball.MapleSnowballs.hitSnowball(chr);
            }
            int numFinisherOrbs = 0;
            Integer comboBuff = chr.getBuffedValue(MapleBuffStat.COMBO);
            if (PlayerHandler.isFinisher(attack.skill)) {
                if (comboBuff != null) {
                    numFinisherOrbs = comboBuff - 1;
                }
                chr.handleOrbconsume();
            } else if (attack.targets > 0 && comboBuff != null) {
                switch (chr.getJob()) {
                    case 111: 
                    case 112: 
                    case 1110: 
                    case 1111: {
                        if (attack.skill == 1111008) break;
                        chr.handleOrbgain();
                    }
                }
            }
            switch (chr.getJob()) {
                case 511: 
                case 512: {
                    chr.handleEnergyCharge(5110001, attack.targets * attack.hits);
                    break;
                }
                case 1510: 
                case 1511: 
                case 1512: {
                    chr.handleEnergyCharge(15100004, attack.targets * attack.hits);
                }
            }
            if (attack.targets > 0 && attack.skill == 1211002) {
                byte advcharge_level = chr.getSkillLevel(SkillFactory.getSkill(1220010));
                if (advcharge_level > 0) {
                    if (!SkillFactory.getSkill(1220010).getEffect(advcharge_level).makeChanceResult()) {
                        chr.cancelEffectFromBuffStat(MapleBuffStat.WK_CHARGE);
                        chr.cancelEffectFromBuffStat(MapleBuffStat.LIGHTNING_CHARGE);
                    }
                } else {
                    chr.cancelEffectFromBuffStat(MapleBuffStat.WK_CHARGE);
                    chr.cancelEffectFromBuffStat(MapleBuffStat.LIGHTNING_CHARGE);
                }
            }
            if (numFinisherOrbs > 0) {
                maxdamage *= (double)numFinisherOrbs;
            } else if (comboBuff != null) {
                ISkill combo = c.getPlayer().getJob() == 1110 || c.getPlayer().getJob() == 1111 ? SkillFactory.getSkill(11111001) : SkillFactory.getSkill(1111002);
                if (c.getPlayer().getSkillLevel(combo) > 0) {
                    maxdamage *= 1.0 + ((double)combo.getEffect(c.getPlayer().getSkillLevel(combo)).getDamage() / 100.0 - 1.0) * (double)(comboBuff - 1);
                }
            }
            if (PlayerHandler.isFinisher(attack.skill)) {
                if (numFinisherOrbs == 0) {
                    return;
                }
                maxdamage = 199999.0;
            }
        }
        chr.checkFollow();
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.closeRangeAttack(chr.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.animation, attack.speed, attack.allDamage, energy, chr.getLevel(), chr.getStat().passive_mastery(), attack.unk, attack.charge), chr.getPosition());
        DamageParse.applyAttack(attack, skill, c.getPlayer(), attackCount, maxdamage, effect, mirror ? AttackType.NON_RANGED_WITH_MIRROR : AttackType.NON_RANGED);
        WeakReference<MapleCharacter>[] clones = chr.getClones();
        for (int i = 0; i < clones.length; ++i) {
            if (clones[i].get() == null) continue;
            final MapleCharacter clone = (MapleCharacter)clones[i].get();
            final ISkill skil2 = skill;
            final byte skillLevel2 = skillLevel;
            final int attackCount2 = attackCount;
            final double maxdamage2 = maxdamage;
            final MapleStatEffect eff2 = effect;
            final AttackInfo attack2 = DamageParse.DivideAttack(attack, chr.isGM() ? 1 : 4);
            Timer.CloneTimer.getInstance().schedule(new Runnable(){

                @Override
                public void run() {
                    clone.getMap().broadcastMessage(MaplePacketCreator.closeRangeAttack(clone.getId(), attack2.tbyte, attack2.skill, skillLevel2, attack2.display, attack2.animation, attack2.speed, attack2.allDamage, energy, clone.getLevel(), clone.getStat().passive_mastery(), attack2.unk, attack2.charge));
                    DamageParse.applyAttack(attack2, skil2, chr, attackCount2, maxdamage2, eff2, mirror ? AttackType.NON_RANGED_WITH_MIRROR : AttackType.NON_RANGED);
                }
            }, 500 * i + 500);
        }
    }

    public static final void rangedAttack(SeekableLittleEndianAccessor slea, MapleClient c, final MapleCharacter chr) {
        double basedamage;
        Integer ShadowPartner;
        if (chr == null) {
            return;
        }
        if (!chr.isAlive() || chr.getMap() == null) {
            chr.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_WHILE_DEAD);
            return;
        }
        AttackInfo attack = DamageParse.Modify_AttackCrit(DamageParse.parseDmgR(slea), chr, 2);
        int bulletCount = 1;
        byte skillLevel = 0;
        MapleStatEffect effect = null;
        ISkill skill = null;
        if (attack.skill != 0) {
            skill = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(attack.skill));
            skillLevel = chr.getSkillLevel(skill);
            effect = attack.getAttackEffect(chr, skillLevel, skill);
            if (effect == null) {
                return;
            }
            switch (attack.skill) {
                case 14101006: 
                case 21110004: {
                    bulletCount = effect.getAttackCount();
                    break;
                }
                default: {
                    bulletCount = effect.getBulletCount();
                }
            }
            if (effect.getCooldown() > 0 && !chr.isGM()) {
                if (chr.skillisCooling(attack.skill)) {
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    return;
                }
                c.getSession().write((Object)MaplePacketCreator.skillCooldown(attack.skill, effect.getCooldown()));
                chr.addCooldown(attack.skill, System.currentTimeMillis(), effect.getCooldown() * 1000);
            }
        }
        if ((ShadowPartner = chr.getBuffedValue(MapleBuffStat.SHADOWPARTNER)) != null) {
            bulletCount *= 2;
        }
        int projectile = 0;
        int visProjectile = 0;
        if (attack.AOE != 0 && chr.getBuffedValue(MapleBuffStat.SOULARROW) == null && attack.skill != 4111004) {
            if (chr.getInventory(MapleInventoryType.USE).getItem(attack.slot) == null) {
                return;
            }
            projectile = chr.getInventory(MapleInventoryType.USE).getItem(attack.slot).getItemId();
            if (attack.csstar > 0) {
                if (chr.getInventory(MapleInventoryType.CASH).getItem(attack.csstar) == null) {
                    return;
                }
                visProjectile = chr.getInventory(MapleInventoryType.CASH).getItem(attack.csstar).getItemId();
            } else {
                visProjectile = projectile;
            }
            if (chr.getBuffedValue(MapleBuffStat.SPIRIT_CLAW) == null) {
                int bulletConsume = bulletCount;
                if (effect != null && effect.getBulletConsume() != 0) {
                    bulletConsume = effect.getBulletConsume() * (ShadowPartner != null ? 2 : 1);
                }
                if (!MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, projectile, bulletConsume, false, true)) {
                    chr.dropMessage(5, "You do not have enough arrows/bullets/stars.");
                    return;
                }
            }
        }
        int projectileWatk = 0;
        if (projectile != 0) {
            projectileWatk = MapleItemInformationProvider.getInstance().getWatkForProjectile(projectile);
        }
        PlayerStats statst = chr.getStat();
        switch (attack.skill) {
            case 4001344: 
            case 4121007: 
            case 14001004: 
            case 14111005: {
                basedamage = (float)statst.getTotalLuk() * 5.0f * (float)(statst.getTotalWatk() + projectileWatk) / 100.0f;
                break;
            }
            case 4111004: {
                basedamage = 13000.0;
                break;
            }
            default: {
                basedamage = projectileWatk != 0 ? (double)statst.calculateMaxBaseDamage(statst.getTotalWatk() + projectileWatk) : (double)statst.getCurrentMaxBaseDamage();
                switch (attack.skill) {
                    case 3101005: {
                        basedamage *= (double)effect.getX() / 100.0;
                    }
                }
            }
        }
        if (effect != null) {
            basedamage *= (double)effect.getDamage() / 100.0;
            int money = effect.getMoneyCon();
            if (money != 0) {
                if (money > chr.getMeso()) {
                    money = chr.getMeso();
                }
                chr.gainMeso(-money, false);
            }
        }
        chr.checkFollow();
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.rangedAttack(chr.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.animation, attack.speed, visProjectile, attack.allDamage, attack.position, chr.getLevel(), chr.getStat().passive_mastery(), attack.unk), chr.getPosition());
        DamageParse.applyAttack(attack, skill, chr, bulletCount, basedamage, effect, ShadowPartner != null ? AttackType.RANGED_WITH_SHADOWPARTNER : AttackType.RANGED);
        WeakReference<MapleCharacter>[] clones = chr.getClones();
        for (int i = 0; i < clones.length; ++i) {
            if (clones[i].get() == null) continue;
            final MapleCharacter clone = (MapleCharacter)clones[i].get();
            final ISkill skil2 = skill;
            final MapleStatEffect eff2 = effect;
            final double basedamage2 = basedamage;
            final int bulletCount2 = bulletCount;
            final int visProjectile2 = visProjectile;
            final byte skillLevel2 = skillLevel;
            final AttackInfo attack2 = DamageParse.DivideAttack(attack, chr.isGM() ? 1 : 4);
            Timer.CloneTimer.getInstance().schedule(new Runnable(){

                @Override
                public void run() {
                    clone.getMap().broadcastMessage(MaplePacketCreator.rangedAttack(clone.getId(), attack2.tbyte, attack2.skill, skillLevel2, attack2.display, attack2.animation, attack2.speed, visProjectile2, attack2.allDamage, attack2.position, clone.getLevel(), clone.getStat().passive_mastery(), attack2.unk));
                    DamageParse.applyAttack(attack2, skil2, chr, bulletCount2, basedamage2, eff2, AttackType.RANGED);
                }
            }, 500 * i + 500);
        }
    }

    public static final void MagicDamage(SeekableLittleEndianAccessor slea, MapleClient c, final MapleCharacter chr) {
        ISkill skill;
        byte skillLevel;
        if (chr == null) {
            return;
        }
        if (!chr.isAlive() || chr.getMap() == null) {
            chr.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_WHILE_DEAD);
            return;
        }
        AttackInfo attack = DamageParse.Modify_AttackCrit(DamageParse.parseDmgMa(slea), chr, 3);
        MapleStatEffect effect = attack.getAttackEffect(chr, skillLevel = chr.getSkillLevel(skill = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(attack.skill))), skill);
        if (effect == null) {
            return;
        }
        if (effect.getCooldown() > 0 && !chr.isGM()) {
            if (chr.skillisCooling(attack.skill)) {
                c.getSession().write((Object)MaplePacketCreator.enableActions());
                return;
            }
            c.getSession().write((Object)MaplePacketCreator.skillCooldown(attack.skill, effect.getCooldown()));
            chr.addCooldown(attack.skill, System.currentTimeMillis(), effect.getCooldown() * 1000);
        }
        chr.checkFollow();
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.magicAttack(chr.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.animation, attack.speed, attack.allDamage, attack.charge, chr.getLevel(), attack.unk), chr.getPosition());
        DamageParse.applyAttackMagic(attack, skill, c.getPlayer(), effect);
        WeakReference<MapleCharacter>[] clones = chr.getClones();
        for (int i = 0; i < clones.length; ++i) {
            if (clones[i].get() == null) continue;
            final MapleCharacter clone = (MapleCharacter)clones[i].get();
            final ISkill skil2 = skill;
            final MapleStatEffect eff2 = effect;
            final byte skillLevel2 = skillLevel;
            final AttackInfo attack2 = DamageParse.DivideAttack(attack, chr.isGM() ? 1 : 4);
            Timer.CloneTimer.getInstance().schedule(new Runnable(){

                @Override
                public void run() {
                    clone.getMap().broadcastMessage(MaplePacketCreator.magicAttack(clone.getId(), attack2.tbyte, attack2.skill, skillLevel2, attack2.display, attack2.animation, attack2.speed, attack2.allDamage, attack2.charge, clone.getLevel(), attack2.unk));
                    DamageParse.applyAttackMagic(attack2, skil2, chr, eff2);
                }
            }, 500 * i + 500);
        }
    }

    public static final void DropMeso(int meso, MapleCharacter chr) {
        if (!chr.isAlive() || meso < 10 || meso > 50000 || meso > chr.getMeso()) {
            chr.getClient().getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        chr.gainMeso(-meso, false, true);
        chr.getMap().spawnMesoDrop(meso, chr.getPosition(), chr, chr, true, (byte)0);
        chr.getCheatTracker().checkDrop(true);
    }

    public static final void ChangeEmotion(final int emote, MapleCharacter chr) {
        int emoteid;
        MapleInventoryType type;
        if (emote > 7 && chr.getInventory(type = GameConstants.getInventoryType(emoteid = 5159992 + emote)).findById(emoteid) == null) {
            chr.getCheatTracker().registerOffense(CheatingOffense.USING_UNAVAILABLE_ITEM, Integer.toString(emoteid));
            return;
        }
        if (emote > 0 && chr != null && chr.getMap() != null) {
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.facialExpression(chr, emote), false);
            WeakReference<MapleCharacter>[] clones = chr.getClones();
            for (int i = 0; i < clones.length; ++i) {
                if (clones[i].get() == null) continue;
                final MapleCharacter clone = (MapleCharacter)clones[i].get();
                Timer.CloneTimer.getInstance().schedule(new Runnable(){

                    @Override
                    public void run() {
                        clone.getMap().broadcastMessage(MaplePacketCreator.facialExpression(clone, emote));
                    }
                }, 500 * i + 500);
            }
        }
    }

    public static final void Heal(SeekableLittleEndianAccessor slea, MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        chr.updateTick(slea.readInt());
        if (slea.available() >= 8L) {
            slea.skip(4);
        }
        short healHP = slea.readShort();
        short healMP = slea.readShort();
        PlayerStats stats = chr.getStat();
        if (stats.getHp() <= 0) {
            return;
        }
        if (healHP != 0) {
            if ((float)healHP > stats.getHealHP()) {
                chr.getCheatTracker().registerOffense(CheatingOffense.REGEN_HIGH_HP, String.valueOf(healHP));
            }
            chr.addHP(healHP);
        }
        if (healMP != 0) {
            if ((float)healMP > stats.getHealMP()) {
                chr.getCheatTracker().registerOffense(CheatingOffense.REGEN_HIGH_MP, String.valueOf(healMP));
            }
            chr.addMP(healMP);
        }
    }

    public static final void MovePlayer(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        List<LifeMovementFragment> res;
        if (chr == null) {
            return;
        }
        final Point Original_Pos = chr.getPosition();
        slea.skip(37);
        try {
            res = MovementParse.parseMovement(slea, 1);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("AIOBE Type1:\n" + slea.toString(true));
            return;
        }
        if (res != null && c.getPlayer().getMap() != null) {
            if (slea.available() < 11L || slea.available() > 26L) {
                System.out.println("slea.available != 13-26 (movement parsing error)\n" + slea.toString(true));
                return;
            }
            ArrayList<LifeMovementFragment> res2 = new ArrayList<LifeMovementFragment>(res);
            final MapleMap map = c.getPlayer().getMap();
            if (chr.isHidden()) {
                chr.setLastRes(res2);
                c.getPlayer().getMap().broadcastGMMessage(chr, MaplePacketCreator.movePlayer(chr.getId(), res, Original_Pos), false);
            } else {
                c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.movePlayer(chr.getId(), res, Original_Pos), false);
            }
            MovementParse.updatePosition(res, chr, 0);
            final Point pos = chr.getPosition();
            map.movePlayer(chr, pos);
            if (chr.getFollowId() > 0 && chr.isFollowOn() && chr.isFollowInitiator()) {
                MapleCharacter fol = map.getCharacterById(chr.getFollowId());
                if (fol != null) {
                    Point original_pos = fol.getPosition();
                    fol.getClient().getSession().write((Object)MaplePacketCreator.moveFollow(Original_Pos, original_pos, pos, res));
                    MovementParse.updatePosition(res, fol, 0);
                    map.broadcastMessage(fol, MaplePacketCreator.movePlayer(fol.getId(), res, original_pos), false);
                } else {
                    chr.checkFollow();
                }
            }
            WeakReference<MapleCharacter>[] clones = chr.getClones();
            for (int i = 0; i < clones.length; ++i) {
                if (clones[i].get() == null) continue;
                final MapleCharacter clone = (MapleCharacter)clones[i].get();
                final ArrayList<LifeMovementFragment> res3 = new ArrayList<LifeMovementFragment>(res2);
                Timer.CloneTimer.getInstance().schedule(new Runnable(){

                    @Override
                    public void run() {
                        try {
                            if (clone.getMap() == map) {
                                if (clone.isHidden()) {
                                    clone.setLastRes(res3);
                                } else {
                                    map.broadcastMessage(clone, MaplePacketCreator.movePlayer(clone.getId(), res3, Original_Pos), false);
                                }
                                MovementParse.updatePosition(res3, clone, 0);
                                map.movePlayer(clone, pos);
                            }
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                    }
                }, 500 * i + 500);
            }
            int count = c.getPlayer().getFallCounter();
            if (map.getFootholds().findBelow(c.getPlayer().getPosition()) == null && c.getPlayer().getPosition().y > c.getPlayer().getOldPosition().y && c.getPlayer().getPosition().x == c.getPlayer().getOldPosition().x) {
                if (count > 10) {
                    c.getPlayer().changeMap(map, map.getPortal(0));
                    c.getPlayer().setFallCounter(0);
                } else {
                    c.getPlayer().setFallCounter(++count);
                }
            } else if (count > 0) {
                c.getPlayer().setFallCounter(0);
            }
            c.getPlayer().setOldPosition(new Point(c.getPlayer().getPosition()));
        }
    }

    public static final void ChangeMapSpecial(String portal_name, MapleClient c, MapleCharacter chr) {
        MaplePortal portal = chr.getMap().getPortal(portal_name);
        if (portal != null) {
            portal.enterPortal(c);
        }
    }

    public static final void ChangeMap(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if (chr == null) {
            chr.dropMessage(1, "\u89d2\u8272\u72c0\u614b\u7570\u5e38\uff0c\u8acb\u4f7f\u7528@\u89e3\u5361/@ea \u4f86\u89e3\u9664\u7570\u5e38\u72c0\u614b");
            return;
        }
        if (slea.available() != 0L) {
            boolean wheel;
            slea.readByte();
            int targetid = slea.readInt();
            MaplePortal portal = chr.getMap().getPortal(slea.readMapleAsciiString());
            slea.skip(1);
            boolean bl = wheel = slea.readShort() > 0 && !MapConstants.isEventMap(chr.getMapId()) && chr.haveItem(5510000, 1, false, true);
            if (targetid != -1 && !chr.isAlive()) {
                chr.setStance(0);
                if (chr.getEventInstance() != null && chr.getEventInstance().revivePlayer(chr) && chr.isAlive()) {
                    return;
                }
                if (chr.getPyramidSubway() != null) {
                    chr.getStat().setHp(50);
                    chr.getPyramidSubway().fail(chr);
                    return;
                }
                if (!wheel) {
                    chr.getStat().setHp(50);
                    MapleMap to = chr.getMap().getReturnMap();
                    chr.changeMap(to, to.getPortal(0));
                } else {
                    c.getSession().write((Object)MTSCSPacket.useWheel(chr.getInventory(MapleInventoryType.CASH).countById(5510000) - 1));
                    chr.getStat().setHp(chr.getStat().getMaxHp() / 100 * 40);
                    MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, 5510000, 1, true, false);
                    MapleMap to = chr.getMap();
                    chr.changeMap(to, to.getPortal(0));
                }
            } else if (targetid != -1 && chr.isGM()) {
                MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                chr.changeMap(to, to.getPortal(0));
            } else if (targetid != -1 && !chr.isGM()) {
                int divi = chr.getMapId() / 100;
                if (divi == 9130401) {
                    if (targetid == 130000000 || targetid / 100 == 9130401) {
                        MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                        chr.changeMap(to, to.getPortal(0));
                    }
                } else if (divi == 9140900) {
                    if (targetid == 914090011 || targetid == 914090012 || targetid == 914090013 || targetid == 140090000) {
                        MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                        chr.changeMap(to, to.getPortal(0));
                    }
                } else if (divi == 9140901 && targetid == 140000000) {
                    c.getSession().write((Object)UIPacket.IntroDisableUI(false));
                    c.getSession().write((Object)UIPacket.IntroLock(false));
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                    chr.changeMap(to, to.getPortal(0));
                } else if (divi == 9140902 && (targetid == 140030000 || targetid == 140000000)) {
                    c.getSession().write((Object)UIPacket.IntroDisableUI(false));
                    c.getSession().write((Object)UIPacket.IntroLock(false));
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                    chr.changeMap(to, to.getPortal(0));
                } else if (divi == 9000900 && targetid / 100 == 9000900 && targetid > chr.getMapId()) {
                    MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                    chr.changeMap(to, to.getPortal(0));
                } else if (divi / 1000 == 9000 && targetid / 100000 == 9000) {
                    if (targetid < 900090000 || targetid > 900090004) {
                        c.getSession().write((Object)UIPacket.IntroDisableUI(false));
                        c.getSession().write((Object)UIPacket.IntroLock(false));
                        c.getSession().write((Object)MaplePacketCreator.enableActions());
                    }
                    MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                    chr.changeMap(to, to.getPortal(0));
                } else if (divi / 10 == 1020 && targetid == 1020000) {
                    c.getSession().write((Object)UIPacket.IntroDisableUI(false));
                    c.getSession().write((Object)UIPacket.IntroLock(false));
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                    chr.changeMap(to, to.getPortal(0));
                } else if (chr.getMapId() == 900090101 && targetid == 100030100) {
                    c.getSession().write((Object)UIPacket.IntroDisableUI(false));
                    c.getSession().write((Object)UIPacket.IntroLock(false));
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                    chr.changeMap(to, to.getPortal(0));
                } else if (chr.getMapId() == 2010000 && targetid == 104000000) {
                    c.getSession().write((Object)UIPacket.IntroDisableUI(false));
                    c.getSession().write((Object)UIPacket.IntroLock(false));
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                    chr.changeMap(to, to.getPortal(0));
                } else if (chr.getMapId() == 106020001 || chr.getMapId() == 106020502) {
                    if (targetid == chr.getMapId() - 1) {
                        c.getSession().write((Object)UIPacket.IntroDisableUI(false));
                        c.getSession().write((Object)UIPacket.IntroLock(false));
                        c.getSession().write((Object)MaplePacketCreator.enableActions());
                        MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                        chr.changeMap(to, to.getPortal(0));
                    }
                } else if (chr.getMapId() == 0 && targetid == 10000) {
                    c.getSession().write((Object)UIPacket.IntroDisableUI(false));
                    c.getSession().write((Object)UIPacket.IntroLock(false));
                    c.getSession().write((Object)MaplePacketCreator.enableActions());
                    MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                    chr.changeMap(to, to.getPortal(0));
                }
            } else if (portal != null) {
                portal.enterPortal(c);
            } else {
                c.getSession().write((Object)MaplePacketCreator.enableActions());
            }
        }
    }

    public static final void InnerPortal(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        MaplePortal portal = chr.getMap().getPortal(slea.readMapleAsciiString());
        Point Original_Pos = chr.getPosition();
        short toX = slea.readShort();
        short toY = slea.readShort();
        if (portal == null) {
            return;
        }
        if (portal.getPosition().distanceSq(chr.getPosition()) > 22500.0) {
            chr.getCheatTracker().registerOffense(CheatingOffense.USING_FARAWAY_PORTAL);
        }
        chr.getMap().movePlayer(chr, new Point(toX, toY));
        chr.checkFollow();
    }

    public static final void snowBall(SeekableLittleEndianAccessor slea, MapleClient c) {
        c.getSession().write((Object)MaplePacketCreator.enableActions());
    }

    public static final void leftKnockBack(SeekableLittleEndianAccessor slea, MapleClient c) {
        if (c.getPlayer().getMapId() / 10000 == 10906) {
            c.getSession().write((Object)MaplePacketCreator.leftKnockBack());
            c.getSession().write((Object)MaplePacketCreator.enableActions());
        }
    }
}

