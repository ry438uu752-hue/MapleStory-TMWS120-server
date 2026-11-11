/*
 * Decompiled with CFR 0.152.
 */
package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.MapleInventoryType;
import handling.channel.handler.MovementParse;
import java.awt.Point;
import java.util.List;
import server.MapleInventoryManipulator;
import server.Randomizer;
import server.life.MapleMonster;
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.maps.MapleMap;
import server.maps.MapleNodes;
import server.movement.LifeMovementFragment;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.MobPacket;

public class MobHandler {
    public static final void MoveMonster(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null) {
            return;
        }
        int oid = slea.readInt();
        MapleMonster monster = chr.getMap().getMonsterByOid(oid);
        if (monster == null) {
            chr.addMoveMob(oid);
            return;
        }
        short moveid = slea.readShort();
        boolean useSkill = slea.readByte() > 0;
        byte skill = slea.readByte();
        int skill1 = slea.readByte() & 0xFF;
        byte skill2 = slea.readByte();
        byte skill3 = slea.readByte();
        byte skill4 = slea.readByte();
        int realskill = 0;
        int level = 0;
        if (useSkill) {
            Pair<Integer, Integer> skillToUse;
            MobSkill mobSkill;
            byte size = monster.getNoSkills();
            boolean used = false;
            if (size > 0 && (mobSkill = MobSkillFactory.getMobSkill(realskill = (skillToUse = monster.getSkills().get((byte)Randomizer.nextInt(size))).getLeft().intValue(), level = skillToUse.getRight().intValue())) != null && !mobSkill.checkCurrentBuff(chr, monster)) {
                long now = System.currentTimeMillis();
                long ls = monster.getLastSkillUsed(realskill);
                if (ls == 0L || now - ls > mobSkill.getCoolTime()) {
                    monster.setLastSkillUsed(realskill, now, mobSkill.getCoolTime());
                    int reqHp = (int)((float)monster.getHp() / (float)monster.getMobMaxHp() * 100.0f);
                    if (reqHp <= mobSkill.getHP()) {
                        used = true;
                        mobSkill.applyEffect(chr, monster, true);
                    }
                }
            }
            if (!used) {
                realskill = 0;
                level = 0;
            }
        }
        slea.skip(33);
        Point startPos = monster.getPosition();
        List<LifeMovementFragment> res = MovementParse.parseMovement(slea, 2);
        c.getSession().write((Object)MobPacket.moveMonsterResponse(monster.getObjectId(), moveid, monster.getMp(), monster.isControllerHasAggro(), realskill, level));
        if (res != null && chr != null) {
            if (slea.available() < 9L || slea.available() > 17L) {
                System.out.println("slea.available != 17 (movement parsing error)");
                System.out.println(slea.toString(true));
                c.getSession().close();
                return;
            }
            MapleMap map = chr.getMap();
            MovementParse.updatePosition(res, monster, -1);
            map.moveMonster(monster, monster.getPosition());
            map.broadcastMessage(chr, MobPacket.moveMonster(useSkill, skill, skill1, skill2, skill3, skill4, monster.getObjectId(), startPos, monster.getPosition(), res), monster.getPosition());
        }
    }

    public static final void FriendlyDamage(SeekableLittleEndianAccessor slea, MapleCharacter chr) {
        MapleMap map = chr.getMap();
        if (map == null) {
            return;
        }
        MapleMonster mobfrom = map.getMonsterByOid(slea.readInt());
        slea.skip(4);
        MapleMonster mobto = map.getMonsterByOid(slea.readInt());
        if (mobfrom != null && mobto != null && mobto.getStats().isFriendly()) {
            int damage = mobto.getStats().getLevel() * Randomizer.nextInt(99) / 2;
            mobto.damage(chr, damage, true);
            MobHandler.checkShammos(chr, mobto, map);
        }
    }

    public static final void checkShammos(MapleCharacter chr, MapleMonster mobto, MapleMap map) {
        if (!mobto.isAlive() && mobto.getId() == 9300275) {
            for (MapleCharacter chrz : map.getCharactersThreadsafe()) {
                if (chrz.getParty() == null || chrz.getParty().getLeader().getId() != chrz.getId()) continue;
                if (!chrz.haveItem(2022698)) break;
                MapleInventoryManipulator.removeById(chrz.getClient(), MapleInventoryType.USE, 2022698, 1, false, true);
                mobto.heal((int)mobto.getMobMaxHp(), mobto.getMobMaxMp(), true);
                return;
            }
            map.broadcastMessage(MaplePacketCreator.serverNotice(6, "Your party has failed to protect the monster."));
            MapleMap mapp = chr.getClient().getChannelServer().getMapFactory().getMap(921120001);
            for (MapleCharacter chrz : map.getCharactersThreadsafe()) {
                chrz.changeMap(mapp, mapp.getPortal(0));
            }
        } else if (mobto.getId() == 9300275 && mobto.getEventInstance() != null) {
            mobto.getEventInstance().setProperty("HP", String.valueOf(mobto.getHp()));
        }
    }

    public static final void MonsterBomb(int oid, MapleCharacter chr) {
        MapleMonster monster = chr.getMap().getMonsterByOid(oid);
        if (monster == null || !chr.isAlive() || chr.isHidden()) {
            return;
        }
        byte selfd = monster.getStats().getSelfD();
        if (selfd != -1) {
            chr.getMap().killMonster(monster, chr, false, false, selfd);
        }
    }

    public static final void AutoAggro(int monsteroid, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null || chr.isHidden()) {
            return;
        }
        MapleMonster monster = chr.getMap().getMonsterByOid(monsteroid);
        if (monster != null && chr.getPosition().distanceSq(monster.getPosition()) < 200000.0) {
            if (monster.getController() != null) {
                if (chr.getMap().getCharacterById(monster.getController().getId()) == null) {
                    monster.switchController(chr, true);
                } else {
                    monster.switchController(monster.getController(), true);
                }
            } else {
                monster.switchController(chr, true);
            }
        }
    }

    public static final void HypnotizeDmg(SeekableLittleEndianAccessor slea, MapleCharacter chr) {
        MapleMonster mob_from = chr.getMap().getMonsterByOid(slea.readInt());
        slea.skip(4);
        int to = slea.readInt();
        slea.skip(1);
        int damage = slea.readInt();
        MapleMonster mob_to = chr.getMap().getMonsterByOid(to);
        if (mob_from != null && mob_to != null && mob_to.getStats().isFriendly()) {
            if (damage > 30000) {
                return;
            }
            mob_to.damage(chr, damage, true);
            MobHandler.checkShammos(chr, mob_to, chr.getMap());
        }
    }

    public static final void DisplayNode(SeekableLittleEndianAccessor slea, MapleCharacter chr) {
        MapleMonster mob_from = chr.getMap().getMonsterByOid(slea.readInt());
        if (mob_from != null) {
            chr.getClient().getSession().write((Object)MaplePacketCreator.getNodeProperties(mob_from, chr.getMap()));
        }
    }

    public static final void MobNode(SeekableLittleEndianAccessor slea, MapleCharacter chr) {
        MapleMonster mob_from = chr.getMap().getMonsterByOid(slea.readInt());
        int newNode = slea.readInt();
        int nodeSize = chr.getMap().getNodes().size();
        if (mob_from != null && nodeSize > 0 && nodeSize >= newNode) {
            MapleNodes.MapleNodeInfo mni = chr.getMap().getNode(newNode);
            if (mni == null) {
                return;
            }
            if (mni.attr == 2) {
                chr.getMap().talkMonster("Please escort me carefully.", 5120035, mob_from.getObjectId());
            }
            if (mob_from.getLastNode() >= newNode) {
                return;
            }
            mob_from.setLastNode(newNode);
            if (nodeSize == newNode) {
                int newMap = -1;
                switch (chr.getMapId() / 100) {
                    case 9211200: {
                        newMap = 921120100;
                        break;
                    }
                    case 9211201: {
                        newMap = 921120200;
                        break;
                    }
                    case 9211202: {
                        newMap = 921120300;
                        break;
                    }
                    case 9211203: {
                        newMap = 921120400;
                        break;
                    }
                    case 9211204: {
                        chr.getMap().removeMonster(mob_from);
                    }
                }
                if (newMap > 0) {
                    chr.getMap().broadcastMessage(MaplePacketCreator.serverNotice(5, "Proceed to the next stage."));
                    chr.getMap().removeMonster(mob_from);
                }
            }
        }
    }
}

