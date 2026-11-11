/*
 * Decompiled with CFR 0.152.
 */
package server.life;

import client.MapleCharacter;
import client.MapleDisease;
import client.status.MonsterStatus;
import constants.GameConstants;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import server.life.BanishInfo;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleMist;

public class MobSkill {
    private int skillId;
    private int skillLevel;
    private int mpCon;
    private int spawnEffect;
    private int hp;
    private int x;
    private int y;
    private long duration;
    private long cooltime;
    private float prop;
    private short limit;
    private List<Integer> toSummon = new ArrayList<Integer>();
    private Point lt;
    private Point rb;

    public MobSkill(int skillId, int level) {
        this.skillId = skillId;
        this.skillLevel = level;
    }

    public void setMpCon(int mpCon) {
        this.mpCon = mpCon;
    }

    public void addSummons(List<Integer> toSummon) {
        this.toSummon = toSummon;
    }

    public void setSpawnEffect(int spawnEffect) {
        this.spawnEffect = spawnEffect;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setCoolTime(long cooltime) {
        this.cooltime = cooltime;
    }

    public void setProp(float prop) {
        this.prop = prop;
    }

    public void setLtRb(Point lt, Point rb) {
        this.lt = lt;
        this.rb = rb;
    }

    public void setLimit(short limit) {
        this.limit = limit;
    }

    public boolean checkCurrentBuff(MapleCharacter player, MapleMonster monster) {
        boolean stop = false;
        switch (this.skillId) {
            case 100: 
            case 110: 
            case 150: {
                stop = monster.isBuffed(MonsterStatus.WEAPON_ATTACK_UP);
                break;
            }
            case 101: 
            case 111: 
            case 151: {
                stop = monster.isBuffed(MonsterStatus.MAGIC_ATTACK_UP);
                break;
            }
            case 102: 
            case 112: 
            case 152: {
                stop = monster.isBuffed(MonsterStatus.WEAPON_DEFENSE_UP);
                break;
            }
            case 103: 
            case 113: 
            case 153: {
                stop = monster.isBuffed(MonsterStatus.MAGIC_DEFENSE_UP);
                break;
            }
            case 140: 
            case 141: 
            case 142: 
            case 143: 
            case 144: 
            case 145: {
                stop = monster.isBuffed(MonsterStatus.DAMAGE_IMMUNITY) || monster.isBuffed(MonsterStatus.MAGIC_IMMUNITY) || monster.isBuffed(MonsterStatus.WEAPON_IMMUNITY);
                break;
            }
            case 200: {
                stop = player.getMap().getNumMonsters() >= this.limit;
            }
        }
        return stop;
    }

    public void applyEffect(MapleCharacter player, MapleMonster monster, boolean skill) {
        MapleDisease disease = null;
        EnumMap<MonsterStatus, Integer> stats = new EnumMap<MonsterStatus, Integer>(MonsterStatus.class);
        LinkedList<Integer> reflection = new LinkedList<Integer>();
        switch (this.skillId) {
            case 100: 
            case 110: 
            case 150: {
                stats.put(MonsterStatus.WEAPON_ATTACK_UP, this.x);
                break;
            }
            case 101: 
            case 111: 
            case 151: {
                stats.put(MonsterStatus.MAGIC_ATTACK_UP, this.x);
                break;
            }
            case 102: 
            case 112: 
            case 152: {
                stats.put(MonsterStatus.WEAPON_DEFENSE_UP, this.x);
                break;
            }
            case 103: 
            case 113: 
            case 153: {
                stats.put(MonsterStatus.MAGIC_DEFENSE_UP, this.x);
                break;
            }
            case 154: {
                stats.put(MonsterStatus.ACC, this.x);
                break;
            }
            case 155: {
                stats.put(MonsterStatus.AVOID, this.x);
                break;
            }
            case 156: {
                stats.put(MonsterStatus.SPEED, this.x);
                break;
            }
            case 157: {
                stats.put(MonsterStatus.SEAL, this.x);
                break;
            }
            case 114: {
                if (this.lt != null && this.rb != null && skill && monster != null) {
                    List<MapleMapObject> objects = this.getObjectsInRange(monster, MapleMapObjectType.MONSTER);
                    int hp = this.getX() / 1000 * (int)(950.0 + 1050.0 * Math.random());
                    Iterator iterator = objects.iterator();
                    while (iterator.hasNext()) {
                        MapleMapObject mons = (MapleMapObject)iterator.next();
                        ((MapleMonster)mons).heal(hp, this.getY(), true);
                    }
                    break;
                }
                if (monster == null) break;
                monster.heal(this.getX(), this.getY(), true);
                break;
            }
            case 120: 
            case 121: 
            case 122: 
            case 123: 
            case 124: 
            case 125: 
            case 126: 
            case 128: 
            case 132: 
            case 133: 
            case 134: 
            case 135: 
            case 136: 
            case 137: {
                disease = MapleDisease.getBySkill(this.skillId);
                break;
            }
            case 127: {
                if (this.lt != null && this.rb != null && skill && monster != null && player != null) {
                    for (MapleCharacter character : this.getPlayersInRange(monster, player)) {
                        character.dispel();
                    }
                    break;
                }
                if (player == null) break;
                player.dispel();
                break;
            }
            case 129: {
                Object info;
                if (monster == null || monster.getEventInstance() != null && monster.getEventInstance().getName().indexOf("BossQuest") != -1 || (info = monster.getStats().getBanishInfo()) == null) break;
                if (this.lt != null && this.rb != null && skill && player != null) {
                    for (MapleCharacter chr : this.getPlayersInRange(monster, player)) {
                        chr.changeMapBanish(((BanishInfo)info).getMap(), ((BanishInfo)info).getPortal(), ((BanishInfo)info).getMsg());
                    }
                    break;
                }
                if (player == null) break;
                player.changeMapBanish(((BanishInfo)info).getMap(), ((BanishInfo)info).getPortal(), ((BanishInfo)info).getMsg());
                break;
            }
            case 131: {
                if (monster == null) break;
                monster.getMap().spawnMist(new MapleMist(this.calculateBoundingBox(monster.getPosition(), true), monster, this), this.x * 10, false);
                break;
            }
            case 140: {
                stats.put(MonsterStatus.WEAPON_IMMUNITY, this.x);
                break;
            }
            case 141: {
                stats.put(MonsterStatus.MAGIC_IMMUNITY, this.x);
                break;
            }
            case 142: {
                stats.put(MonsterStatus.DAMAGE_IMMUNITY, this.x);
                break;
            }
            case 143: {
                stats.put(MonsterStatus.WEAPON_DAMAGE_REFLECT, this.x);
                stats.put(MonsterStatus.WEAPON_IMMUNITY, this.x);
                reflection.add(this.x);
                break;
            }
            case 144: {
                stats.put(MonsterStatus.MAGIC_DAMAGE_REFLECT, this.x);
                stats.put(MonsterStatus.MAGIC_IMMUNITY, this.x);
                reflection.add(this.x);
                break;
            }
            case 145: {
                stats.put(MonsterStatus.WEAPON_DAMAGE_REFLECT, this.x);
                stats.put(MonsterStatus.WEAPON_IMMUNITY, this.x);
                stats.put(MonsterStatus.MAGIC_DAMAGE_REFLECT, this.x);
                stats.put(MonsterStatus.MAGIC_IMMUNITY, this.x);
                reflection.add(this.x);
                reflection.add(this.x);
                break;
            }
            case 200: {
                if (monster == null) {
                    return;
                }
                block37: for (Integer mobId : this.getSummons()) {
                    MapleMonster toSpawn = null;
                    try {
                        toSpawn = MapleLifeFactory.getMonster(GameConstants.getCustomSpawnID(monster.getId(), mobId));
                    }
                    catch (RuntimeException e) {
                        continue;
                    }
                    if (toSpawn == null) continue;
                    toSpawn.setPosition(monster.getPosition());
                    int ypos = (int)monster.getPosition().getY();
                    int xpos = (int)monster.getPosition().getX();
                    switch (mobId) {
                        case 8500003: {
                            toSpawn.setFh((int)Math.ceil(Math.random() * 19.0));
                            ypos = -590;
                            break;
                        }
                        case 8500004: {
                            xpos = (int)(monster.getPosition().getX() + Math.ceil(Math.random() * 1000.0) - 500.0);
                            ypos = (int)monster.getPosition().getY();
                            break;
                        }
                        case 8510100: {
                            if (Math.ceil(Math.random() * 5.0) == 1.0) {
                                ypos = 78;
                                xpos = (int)(0.0 + Math.ceil(Math.random() * 5.0)) + (Math.ceil(Math.random() * 2.0) == 1.0 ? 180 : 0);
                                break;
                            }
                            xpos = (int)(monster.getPosition().getX() + Math.ceil(Math.random() * 1000.0) - 500.0);
                            break;
                        }
                        case 8820007: {
                            continue block37;
                        }
                    }
                    switch (monster.getMap().getId()) {
                        case 220080001: {
                            if (xpos < -890) {
                                xpos = (int)(-890.0 + Math.ceil(Math.random() * 150.0));
                                break;
                            }
                            if (xpos <= 230) break;
                            xpos = (int)(230.0 - Math.ceil(Math.random() * 150.0));
                            break;
                        }
                        case 230040420: {
                            if (xpos < -239) {
                                xpos = (int)(-239.0 + Math.ceil(Math.random() * 150.0));
                                break;
                            }
                            if (xpos <= 371) break;
                            xpos = (int)(371.0 - Math.ceil(Math.random() * 150.0));
                        }
                    }
                    monster.getMap().spawnMonsterWithEffect(toSpawn, this.getSpawnEffect(), monster.getMap().calcPointBelow(new Point(xpos, ypos - 1)));
                }
                break;
            }
        }
        if (stats.size() > 0 && monster != null) {
            if (this.lt != null && this.rb != null && skill) {
                int d = 0;
                for (MapleMapObject mons : this.getObjectsInRange(monster, MapleMapObjectType.MONSTER)) {
                    ((MapleMonster)mons).applyMonsterBuff(stats, this.getSkillId(), this.getDuration() + (long)d, this, reflection);
                    d += 100;
                }
            } else {
                monster.applyMonsterBuff(stats, this.getSkillId(), this.getDuration(), this, reflection);
            }
        }
        if (disease != null && player != null) {
            if (this.lt != null && this.rb != null && skill && monster != null) {
                for (MapleCharacter chr : this.getPlayersInRange(monster, player)) {
                    chr.giveDebuff(disease, this);
                }
            } else {
                player.giveDebuff(disease, this);
            }
        }
        if (monster != null) {
            monster.setMp(monster.getMp() - this.getMpCon());
        }
    }

    public int getSkillId() {
        return this.skillId;
    }

    public int getSkillLevel() {
        return this.skillLevel;
    }

    public int getMpCon() {
        return this.mpCon;
    }

    public List<Integer> getSummons() {
        return Collections.unmodifiableList(this.toSummon);
    }

    public int getSpawnEffect() {
        return this.spawnEffect;
    }

    public int getHP() {
        return this.hp;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public long getDuration() {
        return this.duration;
    }

    public long getCoolTime() {
        return this.cooltime;
    }

    public Point getLt() {
        return this.lt;
    }

    public Point getRb() {
        return this.rb;
    }

    public int getLimit() {
        return this.limit;
    }

    public boolean makeChanceResult() {
        return (double)this.prop >= 1.0 || Math.random() < (double)this.prop;
    }

    private Rectangle calculateBoundingBox(Point posFrom, boolean facingLeft) {
        Point myrb;
        Point mylt;
        if (facingLeft) {
            mylt = new Point(this.lt.x + posFrom.x, this.lt.y + posFrom.y);
            myrb = new Point(this.rb.x + posFrom.x, this.rb.y + posFrom.y);
        } else {
            myrb = new Point(this.lt.x * -1 + posFrom.x, this.rb.y + posFrom.y);
            mylt = new Point(this.rb.x * -1 + posFrom.x, this.lt.y + posFrom.y);
        }
        Rectangle bounds = new Rectangle(mylt.x, mylt.y, myrb.x - mylt.x, myrb.y - mylt.y);
        return bounds;
    }

    private List<MapleCharacter> getPlayersInRange(MapleMonster monster, MapleCharacter player) {
        Rectangle bounds = this.calculateBoundingBox(monster.getPosition(), monster.isFacingLeft());
        ArrayList<MapleCharacter> players = new ArrayList<MapleCharacter>();
        players.add(player);
        return monster.getMap().getPlayersInRectAndInList(bounds, players);
    }

    private List<MapleMapObject> getObjectsInRange(MapleMonster monster, MapleMapObjectType objectType) {
        Rectangle bounds = this.calculateBoundingBox(monster.getPosition(), monster.isFacingLeft());
        ArrayList<MapleMapObjectType> objectTypes = new ArrayList<MapleMapObjectType>();
        objectTypes.add(objectType);
        return monster.getMap().getMapObjectsInRect(bounds, objectTypes);
    }
}

