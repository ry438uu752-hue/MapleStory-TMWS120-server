/*
 * Decompiled with CFR 0.152.
 */
package server.life;

import client.ISkill;
import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleDisease;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import constants.GameConstants;
import constants.ServerConstants;
import handling.MaplePacket;
import handling.channel.ChannelServer;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import java.awt.Point;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import scripting.EventInstanceManager;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.Timer;
import server.life.AbstractLoadedMapleLife;
import server.life.Element;
import server.life.ElementalEffectiveness;
import server.life.MapleLifeFactory;
import server.life.MapleMonsterInformationProvider;
import server.life.MapleMonsterStats;
import server.life.MobSkill;
import server.life.MonsterDropEntry;
import server.life.MonsterListener;
import server.life.OverrideMonsterStats;
import server.maps.MapScriptMethods;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.ConcurrentEnumMap;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.packet.MobPacket;

public class MapleMonster
extends AbstractLoadedMapleLife {
    private MapleMonsterStats stats;
    private OverrideMonsterStats ostats = null;
    private long hp;
    private int mp;
    private byte venom_counter;
    private byte carnivalTeam;
    private MapleMap map;
    private WeakReference<MapleMonster> sponge = new WeakReference<MapleMonster>(null);
    private int linkoid = 0;
    private int lastNode = -1;
    private int lastNodeController = -1;
    private int highestDamageChar = 0;
    private WeakReference<MapleCharacter> controller = new WeakReference<MapleCharacter>(null);
    private boolean fake;
    private boolean dropsDisabled;
    private boolean controllerHasAggro;
    private boolean controllerKnowsAboutAggro;
    private final Collection<AttackerEntry> attackers = new LinkedList<AttackerEntry>();
    private EventInstanceManager eventInstance;
    private MonsterListener listener = null;
    private MaplePacket reflectpack = null;
    private MaplePacket nodepack = null;
    private final Map<MonsterStatus, MonsterStatusEffect> stati = new ConcurrentEnumMap<MonsterStatus, MonsterStatusEffect>(MonsterStatus.class);
    private Map<Integer, Long> usedSkills;
    private int stolen = -1;
    private ScheduledFuture<?> dropItemSchedule;
    private boolean shouldDropItem = false;

    public MapleMonster(int id, MapleMonsterStats stats) {
        super(id);
        this.initWithStats(stats);
    }

    public MapleMonster(MapleMonster monster) {
        super(monster);
        this.initWithStats(monster.stats);
    }

    private final void initWithStats(MapleMonsterStats stats) {
        this.setStance(5);
        this.stats = stats;
        this.hp = stats.getHp();
        this.mp = stats.getMp();
        this.venom_counter = 0;
        this.carnivalTeam = (byte)-1;
        this.fake = false;
        this.dropsDisabled = false;
        if (stats.getNoSkills() > 0) {
            this.usedSkills = new HashMap<Integer, Long>();
        }
    }

    public final MapleMonsterStats getStats() {
        return this.stats;
    }

    public final void disableDrops() {
        this.dropsDisabled = true;
    }

    public final boolean dropsDisabled() {
        return this.dropsDisabled;
    }

    public final void setSponge(MapleMonster mob2) {
        this.sponge = new WeakReference<MapleMonster>(mob2);
    }

    public final void setMap(MapleMap map) {
        this.map = map;
        this.startDropItemSchedule();
    }

    public final long getHp() {
        return this.hp;
    }

    public final void setHp(long hp) {
        this.hp = hp;
    }

    public final long getMobMaxHp() {
        if (this.ostats != null) {
            return this.ostats.getHp();
        }
        return this.stats.getHp();
    }

    public final int getMp() {
        return this.mp;
    }

    public final void setMp(int mp) {
        if (mp < 0) {
            mp = 0;
        }
        this.mp = mp;
    }

    public final int getMobMaxMp() {
        if (this.ostats != null) {
            return this.ostats.getMp();
        }
        return this.stats.getMp();
    }

    public final int getMobExp() {
        if (this.ostats != null) {
            return this.ostats.getExp();
        }
        return this.stats.getExp();
    }

    public final void setOverrideStats(OverrideMonsterStats ostats) {
        this.ostats = ostats;
        this.hp = ostats.getHp();
        this.mp = ostats.getMp();
    }

    public final MapleMonster getSponge() {
        return (MapleMonster)this.sponge.get();
    }

    public final byte getVenomMulti() {
        return this.venom_counter;
    }

    public final void setVenomMulti(byte venom_counter) {
        this.venom_counter = venom_counter;
    }

    public final void damage(MapleCharacter from, long damage, boolean updateAttackTime) {
        this.damage(from, damage, updateAttackTime, 0);
    }

    public final void damage(MapleCharacter from, long damage, boolean updateAttackTime, int lastSkill) {
        if (from == null || damage <= 0L || !this.isAlive()) {
            return;
        }
        AttackerEntry attacker = null;
        attacker = from.getParty() != null ? new PartyAttackerEntry(from.getParty().getId(), this.map.getChannel()) : new SingleAttackerEntry(from, this.map.getChannel());
        boolean replaced = false;
        for (AttackerEntry aentry : this.attackers) {
            if (!aentry.equals(attacker)) continue;
            attacker = aentry;
            replaced = true;
            break;
        }
        if (!replaced) {
            this.attackers.add(attacker);
        }
        long rDamage = Math.max(0L, Math.min(damage, this.hp));
        attacker.addDamage(from, rDamage, updateAttackTime);
        if (this.stats.getSelfD() != -1) {
            this.hp -= rDamage;
            if (this.hp > 0L) {
                if (this.hp < (long)this.stats.getSelfDHp()) {
                    this.map.killMonster(this, from, false, false, this.stats.getSelfD(), lastSkill);
                } else {
                    for (AttackerEntry mattacker : this.attackers) {
                        for (AttackingMapleCharacter cattacker : mattacker.getAttackers()) {
                            if (cattacker.getAttacker().getMap() != from.getMap() || cattacker.getLastAttackTime() < System.currentTimeMillis() - 4000L) continue;
                            cattacker.getAttacker().getClient().getSession().write((Object)MobPacket.showMonsterHP(this.getObjectId(), (int)Math.ceil((double)this.hp * 100.0 / (double)this.getMobMaxHp())));
                        }
                    }
                }
            } else {
                this.map.killMonster(this, from, true, false, (byte)1, lastSkill);
            }
        } else {
            if (this.sponge.get() != null && ((MapleMonster)this.sponge.get()).hp > 0L) {
                ((MapleMonster)this.sponge.get()).hp -= rDamage;
                if (((MapleMonster)this.sponge.get()).hp <= 0L) {
                    this.map.killMonster((MapleMonster)this.sponge.get(), from, true, false, (byte)1, lastSkill);
                } else {
                    this.map.broadcastMessage(MobPacket.showBossHP((MapleMonster)this.sponge.get()));
                }
            }
            if (this.hp > 0L) {
                this.hp -= rDamage;
                if (this.eventInstance != null) {
                    this.eventInstance.monsterDamaged(from, this, (int)rDamage);
                } else {
                    EventInstanceManager em = from.getEventInstance();
                    if (em != null) {
                        em.monsterDamaged(from, this, (int)rDamage);
                    }
                }
                if (this.sponge.get() == null && this.hp > 0L) {
                    switch (this.stats.getHPDisplayType()) {
                        case 0: {
                            this.map.broadcastMessage(MobPacket.showBossHP(this), this.getPosition());
                            break;
                        }
                        case 1: {
                            this.map.broadcastMessage(from, MobPacket.damageFriendlyMob(this, damage, true), false);
                            break;
                        }
                        case 2: {
                            this.map.broadcastMessage(MobPacket.showMonsterHP(this.getObjectId(), (int)Math.ceil((double)this.hp * 100.0 / (double)this.getMobMaxHp())));
                            from.mulung_EnergyModify(true);
                            break;
                        }
                        case 3: {
                            for (AttackerEntry mattacker : this.attackers) {
                                for (AttackingMapleCharacter cattacker : mattacker.getAttackers()) {
                                    if (cattacker.getAttacker().getMap() != from.getMap() || cattacker.getLastAttackTime() < System.currentTimeMillis() - 4000L) continue;
                                    cattacker.getAttacker().getClient().getSession().write((Object)MobPacket.showMonsterHP(this.getObjectId(), (int)Math.ceil((double)this.hp * 100.0 / (double)this.getMobMaxHp())));
                                }
                            }
                            break;
                        }
                    }
                }
                if (this.hp <= 0L) {
                    this.map.killMonster(this, from, true, false, (byte)1, lastSkill);
                }
            }
        }
        this.startDropItemSchedule();
    }

    public final void heal(int hp, int mp, boolean broadcast) {
        long TotalHP = this.getHp() + (long)hp;
        int TotalMP = this.getMp() + mp;
        if (TotalHP >= this.getMobMaxHp()) {
            this.setHp(this.getMobMaxHp());
        } else {
            this.setHp(TotalHP);
        }
        if (TotalMP >= this.getMp()) {
            this.setMp(this.getMp());
        } else {
            this.setMp(TotalMP);
        }
        if (broadcast) {
            this.map.broadcastMessage(MobPacket.healMonster(this.getObjectId(), hp));
        } else if (this.sponge.get() != null) {
            ((MapleMonster)this.sponge.get()).hp += (long)hp;
        }
    }

    private final void giveExpToCharacter(MapleCharacter attacker, int exp, boolean highestDamage, int numExpSharers, byte pty, byte Class_Bonus_EXP_PERCENT, byte Premium_Bonus_EXP_PERCENT, int lastskillID) {
        if (highestDamage) {
            if (this.eventInstance != null) {
                this.eventInstance.monsterKilled(attacker, this);
            } else {
                EventInstanceManager em = attacker.getEventInstance();
                if (em != null) {
                    em.monsterKilled(attacker, this);
                }
            }
            this.highestDamageChar = attacker.getId();
        }
        if (exp > 0) {
            Integer holySymbol;
            MonsterStatusEffect mse = this.stati.get(MonsterStatus.SHOWDOWN);
            if (mse != null) {
                exp += (int)((double)exp * ((double)mse.getX().intValue() / 100.0));
            }
            if ((holySymbol = attacker.getBuffedValue(MapleBuffStat.HOLY_SYMBOL)) != null) {
                exp = numExpSharers == 1 ? (int)((double)exp * (1.0 + holySymbol.doubleValue() / 500.0)) : (int)((double)exp * (1.0 + holySymbol.doubleValue() / 100.0));
            }
            if (attacker.hasDisease(MapleDisease.CURSE)) {
                exp /= 2;
            }
            exp *= attacker.getEXPMod() * (int)(attacker.getStat().expBuff / 100.0);
            exp = Math.min(Integer.MAX_VALUE, exp * (attacker.getLevel() < 10 ? GameConstants.getExpRate_Below10(attacker.getJob()) : ChannelServer.getInstance(this.map.getChannel()).getExpRate()));
            int Class_Bonus_EXP = 0;
            if (Class_Bonus_EXP_PERCENT > 0) {
                Class_Bonus_EXP = (int)((double)exp / 100.0 * (double)Class_Bonus_EXP_PERCENT);
            }
            int Premium_Bonus_EXP = 0;
            if (Premium_Bonus_EXP_PERCENT > 0) {
                Premium_Bonus_EXP = (int)((double)exp / 100.0 * (double)Premium_Bonus_EXP_PERCENT);
            }
            int Equipment_Bonus_EXP = (int)((double)exp / 100.0 * (double)attacker.getStat().equipmentBonusExp);
            if (attacker.getStat().equippedFairy) {
                Equipment_Bonus_EXP += (int)((double)exp / 100.0 * (double)attacker.getFairyExp());
            }
            attacker.gainExpMonster(exp, true, highestDamage, pty, Class_Bonus_EXP, Equipment_Bonus_EXP, Premium_Bonus_EXP);
        }
        attacker.mobKilled(this.getId(), lastskillID);
    }

    public final int killBy(MapleCharacter killer, int lastSkill) {
        int totalBaseExp = this.getMobExp();
        AttackerEntry highest = null;
        long highdamage = 0L;
        for (AttackerEntry attackerEntry : this.attackers) {
            if (attackerEntry.getDamage() <= highdamage) continue;
            highest = attackerEntry;
            highdamage = attackerEntry.getDamage();
        }
        for (AttackerEntry attackEntry : this.attackers) {
            int baseExp = (int)Math.ceil((double)totalBaseExp * ((double)attackEntry.getDamage() / (double)this.getMobMaxHp()));
            attackEntry.killedMob(this.getMap(), baseExp, attackEntry == highest, lastSkill);
        }
        MapleCharacter mapleCharacter = (MapleCharacter)this.controller.get();
        if (mapleCharacter != null) {
            mapleCharacter.getClient().getSession().write((Object)MobPacket.stopControllingMonster(this.getObjectId()));
            mapleCharacter.stopControllingMonster(this);
        }
        switch (this.getId()) {
            default: 
        }
        this.spawnRevives(this.getMap());
        if (this.eventInstance != null) {
            this.eventInstance.unregisterMonster(this);
            this.eventInstance = null;
        }
        if (killer != null && killer.getPyramidSubway() != null) {
            killer.getPyramidSubway().onKill(killer);
        }
        MapleMonster oldSponge = this.getSponge();
        this.sponge = new WeakReference<MapleMonster>(null);
        if (oldSponge != null && oldSponge.isAlive()) {
            boolean set = true;
            for (MapleMapObject mapleMapObject : this.map.getAllMonstersThreadsafe()) {
                MapleMonster mons = (MapleMonster)mapleMapObject;
                if (mons.getObjectId() == oldSponge.getObjectId() || mons.getObjectId() == this.getObjectId() || mons.getSponge() != oldSponge && mons.getLinkOid() != oldSponge.getObjectId()) continue;
                set = false;
                break;
            }
            if (set) {
                this.map.killMonster(oldSponge, killer, true, false, (byte)1);
            }
        }
        this.nodepack = null;
        this.reflectpack = null;
        this.stati.clear();
        this.cancelDropItem();
        if (this.listener != null) {
            this.listener.monsterKilled();
        }
        int v1 = this.highestDamageChar;
        this.highestDamageChar = 0;
        return v1;
    }

    public final void spawnRevives(MapleMap map) {
        List<Integer> toSpawn = this.stats.getRevives();
        if (toSpawn == null) {
            return;
        }
        MapleMonster spongy = null;
        switch (this.getId()) {
            case 0x866E86: 
            case 8810119: 
            case 0x866E88: 
            case 8810121: {
                Iterator<Integer> iterator = toSpawn.iterator();
                while (iterator.hasNext()) {
                    int n = iterator.next();
                    MapleMonster mob2 = MapleLifeFactory.getMonster(n);
                    mob2.setPosition(this.getPosition());
                    if (this.eventInstance != null) {
                        this.eventInstance.registerMonster(mob2);
                    }
                    if (this.dropsDisabled()) {
                        mob2.disableDrops();
                    }
                    switch (mob2.getId()) {
                        case 8810119: 
                        case 0x866E88: 
                        case 8810121: 
                        case 8810122: {
                            spongy = mob2;
                        }
                    }
                }
                if (spongy == null) break;
                map.spawnRevives(spongy, this.getObjectId());
                for (MapleMapObject mapleMapObject : map.getAllMonstersThreadsafe()) {
                    MapleMonster mons = (MapleMonster)mapleMapObject;
                    if (mons.getObjectId() == spongy.getObjectId() || mons.getSponge() != this && mons.getLinkOid() != this.getObjectId()) continue;
                    mons.setSponge(spongy);
                    mons.setLinkOid(spongy.getObjectId());
                }
                break;
            }
            case 8810026: 
            case 8810130: 
            case 8820008: 
            case 8820009: 
            case 8820010: 
            case 8820011: 
            case 8820012: 
            case 8820013: {
                ArrayList<MapleMonster> mobs = new ArrayList<MapleMonster>();
                block12: for (int i : toSpawn) {
                    MapleMonster mob3 = MapleLifeFactory.getMonster(i);
                    mob3.setPosition(this.getPosition());
                    if (this.eventInstance != null) {
                        this.eventInstance.registerMonster(mob3);
                    }
                    if (this.dropsDisabled()) {
                        mob3.disableDrops();
                    }
                    switch (mob3.getId()) {
                        case 8810018: 
                        case 0x866E86: 
                        case 8820009: 
                        case 8820010: 
                        case 8820011: 
                        case 8820012: 
                        case 8820013: 
                        case 8820014: {
                            spongy = mob3;
                            continue block12;
                        }
                    }
                    mobs.add(mob3);
                }
                if (spongy == null) break;
                map.spawnRevives(spongy, this.getObjectId());
                for (MapleMonster i : mobs) {
                    i.setSponge(spongy);
                    map.spawnRevives(i, this.getObjectId());
                }
                break;
            }
            default: {
                for (int n : toSpawn) {
                    MapleMonster mob4 = MapleLifeFactory.getMonster(n);
                    if (this.eventInstance != null) {
                        this.eventInstance.registerMonster(mob4);
                    }
                    mob4.setPosition(this.getPosition());
                    if (this.dropsDisabled()) {
                        mob4.disableDrops();
                    }
                    map.spawnRevives(mob4, this.getObjectId());
                    if (mob4.getId() != 9300216) continue;
                    map.broadcastMessage(MaplePacketCreator.environmentChange("Dojang/clear", 4));
                    map.broadcastMessage(MaplePacketCreator.environmentChange("dojang/end/clear", 3));
                }
            }
        }
    }

    public final boolean isAlive() {
        return this.hp > 0L;
    }

    public final void setCarnivalTeam(byte team) {
        this.carnivalTeam = team;
    }

    public final byte getCarnivalTeam() {
        return this.carnivalTeam;
    }

    public final MapleCharacter getController() {
        return (MapleCharacter)this.controller.get();
    }

    public final void setController(MapleCharacter controller) {
        this.controller = new WeakReference<MapleCharacter>(controller);
    }

    public final void switchController(MapleCharacter newController, boolean immediateAggro) {
        MapleCharacter controllers = this.getController();
        if (controllers == newController) {
            return;
        }
        if (controllers != null) {
            controllers.stopControllingMonster(this);
            controllers.getClient().getSession().write((Object)MobPacket.stopControllingMonster(this.getObjectId()));
        }
        newController.controlMonster(this, immediateAggro);
        this.setController(newController);
        if (immediateAggro) {
            this.setControllerHasAggro(true);
        }
        this.setControllerKnowsAboutAggro(false);
        if (this.getId() == 9300275 && this.map.getId() >= 921120100 && this.map.getId() < 921120500) {
            if (this.lastNodeController != -1 && this.lastNodeController != newController.getId()) {
                this.resetShammos(newController.getClient());
            } else {
                this.setLastNodeController(newController.getId());
            }
        }
    }

    public final void resetShammos(MapleClient c) {
        this.map.killAllMonsters(true);
        this.map.broadcastMessage(MaplePacketCreator.serverNotice(5, "A player has moved too far from Shammos. Shammos is going back to the start."));
        for (MapleCharacter chr : this.map.getCharactersThreadsafe()) {
            chr.changeMap(chr.getMap(), chr.getMap().getPortal(0));
        }
        MapScriptMethods.startScript_FirstUser(c, "shammos_Fenter");
    }

    public final void addListener(MonsterListener listener) {
        this.listener = listener;
    }

    public final boolean isControllerHasAggro() {
        return this.controllerHasAggro;
    }

    public final void setControllerHasAggro(boolean controllerHasAggro) {
        this.controllerHasAggro = controllerHasAggro;
    }

    public final boolean isControllerKnowsAboutAggro() {
        return this.controllerKnowsAboutAggro;
    }

    public final void setControllerKnowsAboutAggro(boolean controllerKnowsAboutAggro) {
        this.controllerKnowsAboutAggro = controllerKnowsAboutAggro;
    }

    @Override
    public final void sendSpawnData(MapleClient client) {
        if (!this.isAlive()) {
            return;
        }
        client.getSession().write((Object)MobPacket.spawnMonster(this, this.lastNode >= 0 ? -2 : -1, this.fake ? 252 : (this.lastNode >= 0 ? 12 : 0), 0));
        if (this.reflectpack != null) {
            client.getSession().write((Object)this.reflectpack);
        }
        if (this.lastNode >= 0) {
            client.getSession().write((Object)MaplePacketCreator.getNodeProperties(this, this.map));
            if (this.getId() == 9300275 && this.map.getId() >= 921120100 && this.map.getId() < 921120500) {
                if (this.lastNodeController != -1) {
                    this.resetShammos(client);
                } else {
                    this.setLastNodeController(client.getPlayer().getId());
                }
            }
        }
    }

    @Override
    public final void sendDestroyData(MapleClient client) {
        if (this.lastNode == -1) {
            client.getSession().write((Object)MobPacket.killMonster(this.getObjectId(), 0));
        }
        if (this.getId() == 9300275 && this.map.getId() >= 921120100 && this.map.getId() < 921120500) {
            this.resetShammos(client);
        }
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.stats.getName());
        sb.append("(");
        sb.append(this.getId());
        sb.append(") (\u7b49\u7d1a ");
        sb.append(this.stats.getLevel());
        sb.append(") \u5728 (X");
        sb.append(this.getPosition().x);
        sb.append("/ Y");
        sb.append(this.getPosition().y);
        sb.append(") \u5ea7\u6a19 ");
        sb.append(this.getHp());
        sb.append("/ ");
        sb.append(this.getMobMaxHp());
        sb.append("\u8840\u91cf, ");
        sb.append(this.getMp());
        sb.append("/ ");
        sb.append(this.getMobMaxMp());
        sb.append(" \u9b54\u529b, \u53cd\u61c9\u5806: ");
        sb.append(this.getObjectId());
        sb.append(" || \u4ec7\u6068\u76ee\u6a19 : ");
        MapleCharacter chr = (MapleCharacter)this.controller.get();
        sb.append(chr != null ? chr.getName() : "\u7121");
        return sb.toString();
    }

    @Override
    public final MapleMapObjectType getType() {
        return MapleMapObjectType.MONSTER;
    }

    public final EventInstanceManager getEventInstance() {
        return this.eventInstance;
    }

    public final void setEventInstance(EventInstanceManager eventInstance) {
        this.eventInstance = eventInstance;
    }

    public final int getStatusSourceID(MonsterStatus status) {
        MonsterStatusEffect effect = this.stati.get(status);
        if (effect != null) {
            return effect.getSkill();
        }
        return -1;
    }

    public final ElementalEffectiveness getEffectiveness(Element e) {
        if (this.stati.size() > 0 && this.stati.get(MonsterStatus.DOOM) != null) {
            return ElementalEffectiveness.NORMAL;
        }
        return this.stats.getEffectiveness(e);
    }

    public final void applyStatus(MapleCharacter from, MonsterStatusEffect status, boolean poison, long duration, boolean venom) {
        this.applyStatus(from, status, poison, duration, venom, true, 0);
    }

    public final void applyStatus(MapleCharacter from, MonsterStatusEffect status, boolean poison, long duration, boolean venom, int delay) {
        this.applyStatus(from, status, poison, duration, venom, true, delay);
    }

    public final void applyStatus(MapleCharacter from, MonsterStatusEffect status, boolean poison, long duration, boolean venom, boolean checkboss) {
        this.applyStatus(from, status, poison, duration, venom, checkboss, 0);
    }

    public final void applyStatus(MapleCharacter from, MonsterStatusEffect status, boolean poison, long duration, boolean venom, boolean checkboss, int delay) {
        int poisonDamage;
        MonsterStatusEffect oldEffect;
        if (!this.isAlive()) {
            return;
        }
        ISkill skilz = SkillFactory.getSkill(status.getSkill());
        if (skilz != null) {
            switch (this.stats.getEffectiveness(skilz.getElement())) {
                case IMMUNE: 
                case STRONG: {
                    return;
                }
                case NORMAL: 
                case WEAK: {
                    break;
                }
                default: {
                    return;
                }
            }
        }
        int statusSkill = status.getSkill();
        switch (statusSkill) {
            case 2111006: {
                switch (this.stats.getEffectiveness(Element.POISON)) {
                    case IMMUNE: 
                    case STRONG: {
                        return;
                    }
                }
                break;
            }
            case 2211006: {
                switch (this.stats.getEffectiveness(Element.ICE)) {
                    case IMMUNE: 
                    case STRONG: {
                        return;
                    }
                }
                break;
            }
            case 4120005: 
            case 4220005: 
            case 14110004: {
                switch (this.stats.getEffectiveness(Element.POISON)) {
                    case WEAK: {
                        return;
                    }
                }
            }
        }
        final MonsterStatus stat = status.getStati();
        if (this.stats.isNoDoom() && stat == MonsterStatus.DOOM) {
            return;
        }
        if (this.stats.isBoss()) {
            if (stat == MonsterStatus.STUN) {
                return;
            }
            if (checkboss && stat != MonsterStatus.SPEED && stat != MonsterStatus.NINJA_AMBUSH && stat != MonsterStatus.WATK) {
                return;
            }
        }
        if ((oldEffect = this.stati.get(stat)) != null) {
            this.stati.remove(stat);
            if (oldEffect.getStati() == null) {
                oldEffect.cancelTask();
                oldEffect.cancelPoisonSchedule();
            }
        }
        Timer.MobTimer timerManager = Timer.MobTimer.getInstance();
        Runnable cancelTask = new Runnable(){

            @Override
            public final void run() {
                MapleMonster.this.cancelStatus(stat);
            }
        };
        if (poison && this.getHp() > 1L) {
            poisonDamage = (int)Math.min(32767L, (long)((double)this.getMobMaxHp() / (70.0 - (double)from.getSkillLevel(status.getSkill())) + 0.999));
            status.setValue(MonsterStatus.POISON, poisonDamage);
            status.setPoisonSchedule(timerManager.register(new PoisonTask(poisonDamage, from, status, cancelTask, false), 1000L, 1000L));
        } else if (venom) {
            byte poisonLevel = 0;
            short matk = 0;
            switch (from.getJob()) {
                case 412: {
                    poisonLevel = from.getSkillLevel(SkillFactory.getSkill(4120005));
                    if (poisonLevel <= 0) {
                        return;
                    }
                    matk = SkillFactory.getSkill(4120005).getEffect(poisonLevel).getMatk();
                    break;
                }
                case 422: {
                    poisonLevel = from.getSkillLevel(SkillFactory.getSkill(4220005));
                    if (poisonLevel <= 0) {
                        return;
                    }
                    matk = SkillFactory.getSkill(4220005).getEffect(poisonLevel).getMatk();
                    break;
                }
                case 1411: 
                case 1412: {
                    poisonLevel = from.getSkillLevel(SkillFactory.getSkill(14110004));
                    if (poisonLevel <= 0) {
                        return;
                    }
                    matk = SkillFactory.getSkill(14110004).getEffect(poisonLevel).getMatk();
                    break;
                }
                case 434: {
                    poisonLevel = from.getSkillLevel(SkillFactory.getSkill(4340001));
                    if (poisonLevel <= 0) {
                        return;
                    }
                    matk = SkillFactory.getSkill(4340001).getEffect(poisonLevel).getMatk();
                    break;
                }
                default: {
                    return;
                }
            }
            short luk = from.getStat().getLuk();
            int maxDmg = (int)Math.ceil(Math.min(32767.0, 0.2 * (double)luk * (double)matk));
            int minDmg = (int)Math.ceil(Math.min(32767.0, 0.1 * (double)luk * (double)matk));
            int gap = maxDmg - minDmg;
            if (gap == 0) {
                gap = 1;
            }
            int poisonDamage2 = 0;
            for (int i = 0; i < this.getVenomMulti(); ++i) {
                poisonDamage2 += Randomizer.nextInt(gap) + minDmg;
            }
            poisonDamage2 = Math.min(Short.MAX_VALUE, poisonDamage2);
            status.setValue(MonsterStatus.POISON, poisonDamage2);
            status.setPoisonSchedule(timerManager.register(new PoisonTask(poisonDamage2, from, status, cancelTask, false), 1000L, 1000L));
        } else if (statusSkill == 4111003 || statusSkill == 14111001) {
            status.setPoisonSchedule(timerManager.schedule(new PoisonTask((int)((double)this.getMobMaxHp() / 50.0 + 0.999), from, status, cancelTask, true), 3500L));
        } else if (statusSkill == 4341003) {
            poisonDamage = (int)Math.min(32767L, (long)((double)this.getMobMaxHp() / (70.0 - (double)from.getSkillLevel(status.getSkill())) + 0.999));
            status.setPoisonSchedule(timerManager.schedule(new PoisonTask((int)((double)((float)poisonDamage * from.getStat().getCurrentMaxBaseDamage()) / 100.0), from, status, cancelTask, true), 3000L));
        } else if (statusSkill == 4121004 || statusSkill == 4221004) {
            int damage = (from.getStat().getStr() + from.getStat().getLuk()) * 2 * 0;
            status.setPoisonSchedule(timerManager.register(new PoisonTask(damage, from, status, cancelTask, false), 1000L, 1000L));
        }
        this.stati.put(stat, status);
        if (poison) {
            status.setPoisonStatusSchedule(timerManager.schedule(new ApplyMonsterStatusDelay(from, this.getController(), this.getObjectId(), status, this.getPosition(), this), delay));
        } else {
            this.map.broadcastMessage(MobPacket.applyMonsterStatus(this.getObjectId(), status), this.getPosition());
            if (this.getController() != null && !this.getController().isMapObjectVisible(this)) {
                this.getController().getClient().getSession().write((Object)MobPacket.applyMonsterStatus(this.getObjectId(), status));
            }
        }
        int aniTime = 0;
        if (skilz != null) {
            aniTime = skilz.getAnimationTime();
        }
        ScheduledFuture<?> schedule = timerManager.schedule(cancelTask, duration + (long)aniTime);
        status.setCancelTask(schedule);
    }

    public final void dispelSkill(MobSkill skillId) {
        ArrayList<MonsterStatus> toCancel = new ArrayList<MonsterStatus>();
        for (Map.Entry<MonsterStatus, MonsterStatusEffect> effects : this.stati.entrySet()) {
            if (effects.getValue().getMobSkill() == null || effects.getValue().getMobSkill().getSkillId() != skillId.getSkillId()) continue;
            toCancel.add(effects.getKey());
        }
        for (MonsterStatus stat : toCancel) {
            this.cancelStatus(stat);
        }
    }

    public final void applyMonsterBuff(final Map<MonsterStatus, Integer> effect, int skillId, long duration, MobSkill skill, final List<Integer> reflection) {
        Timer.MobTimer timerManager = Timer.MobTimer.getInstance();
        Runnable cancelTask = new Runnable(){

            @Override
            public final void run() {
                if (reflection.size() > 0) {
                    MapleMonster.this.reflectpack = null;
                }
                if (MapleMonster.this.isAlive()) {
                    for (MonsterStatus z : effect.keySet()) {
                        MapleMonster.this.cancelStatus(z);
                    }
                }
            }
        };
        for (Map.Entry<MonsterStatus, Integer> z : effect.entrySet()) {
            MonsterStatusEffect effectz = new MonsterStatusEffect(z.getKey(), z.getValue(), 0, skill, true);
            this.stati.put(z.getKey(), effectz);
        }
        if (reflection.size() > 0) {
            this.reflectpack = MobPacket.applyMonsterStatus(this.getObjectId(), effect, reflection, skill);
            this.map.broadcastMessage(this.reflectpack, this.getPosition());
            if (this.getController() != null && !this.getController().isMapObjectVisible(this)) {
                this.getController().getClient().getSession().write((Object)this.reflectpack);
            }
        } else {
            for (Map.Entry<MonsterStatus, Integer> z : effect.entrySet()) {
                this.map.broadcastMessage(MobPacket.applyMonsterStatus(this.getObjectId(), z.getKey(), z.getValue(), skill), this.getPosition());
                if (this.getController() == null || this.getController().isMapObjectVisible(this)) continue;
                this.getController().getClient().getSession().write((Object)MobPacket.applyMonsterStatus(this.getObjectId(), z.getKey(), z.getValue(), skill));
            }
        }
        timerManager.schedule(cancelTask, duration);
    }

    public final void setTempEffectiveness(final Element e, long milli) {
        this.stats.setEffectiveness(e, ElementalEffectiveness.WEAK);
        Timer.MobTimer.getInstance().schedule(new Runnable(){

            @Override
            public void run() {
                MapleMonster.this.stats.removeEffectiveness(e);
            }
        }, milli);
    }

    public final boolean isBuffed(MonsterStatus status) {
        return this.stati.containsKey(status);
    }

    public final MonsterStatusEffect getBuff(MonsterStatus status) {
        return this.stati.get(status);
    }

    public final void setFake(boolean fake) {
        this.fake = fake;
    }

    public final boolean isFake() {
        return this.fake;
    }

    public final MapleMap getMap() {
        return this.map;
    }

    public final List<Pair<Integer, Integer>> getSkills() {
        return this.stats.getSkills();
    }

    public final boolean hasSkill(int skillId, int level) {
        return this.stats.hasSkill(skillId, level);
    }

    public final long getLastSkillUsed(int skillId) {
        if (this.usedSkills.containsKey(skillId)) {
            return this.usedSkills.get(skillId);
        }
        return 0L;
    }

    public final void setLastSkillUsed(int skillId, long now, long cooltime) {
        switch (skillId) {
            case 140: {
                this.usedSkills.put(skillId, now + cooltime * 2L);
                this.usedSkills.put(141, now);
                break;
            }
            case 141: {
                this.usedSkills.put(skillId, now + cooltime * 2L);
                this.usedSkills.put(140, now + cooltime);
                break;
            }
            default: {
                this.usedSkills.put(skillId, now + cooltime);
            }
        }
    }

    public final byte getNoSkills() {
        return this.stats.getNoSkills();
    }

    public final boolean isFirstAttack() {
        return this.stats.isFirstAttack();
    }

    public final int getBuffToGive() {
        return this.stats.getBuffToGive();
    }

    public int getLinkOid() {
        return this.linkoid;
    }

    public void setLinkOid(int lo) {
        this.linkoid = lo;
    }

    public final Map<MonsterStatus, MonsterStatusEffect> getStati() {
        return this.stati;
    }

    public void addEmpty() {
        this.stati.put(MonsterStatus.EMPTY, new MonsterStatusEffect(MonsterStatus.EMPTY, 0, 0, null, false));
        this.stati.put(MonsterStatus.SUMMON, new MonsterStatusEffect(MonsterStatus.SUMMON, 0, 0, null, false));
    }

    public final int getStolen() {
        return this.stolen;
    }

    public final void setStolen(int s) {
        this.stolen = s;
    }

    public final void handleSteal(MapleCharacter chr) {
        ISkill steal = SkillFactory.getSkill(4201004);
        byte level = chr.getSkillLevel(steal);
        if (level > 0 && !this.getStats().isBoss() && this.stolen == -1 && steal.getEffect(level).makeChanceResult()) {
            MapleMonsterInformationProvider mi = MapleMonsterInformationProvider.getInstance();
            ArrayList<MonsterDropEntry> dropEntry = new ArrayList<MonsterDropEntry>(mi.retrieveDrop(this.getId()));
            Collections.shuffle(dropEntry);
            for (MonsterDropEntry d : dropEntry) {
                Item idrop;
                if (d.itemId <= 0 || d.questid != 0 || !steal.getEffect(level).makeChanceResult()) continue;
                if (GameConstants.getInventoryType(d.itemId) == MapleInventoryType.EQUIP) {
                    Equip eq = (Equip)MapleItemInformationProvider.getInstance().getEquipById(d.itemId);
                    idrop = MapleItemInformationProvider.getInstance().randomizeStats(eq);
                } else {
                    idrop = new Item(d.itemId, (short)0, (short)(d.Maximum != 1 ? Randomizer.nextInt(d.Maximum - d.Minimum) + d.Minimum : 1), (byte)0);
                }
                this.stolen = d.itemId;
                this.map.spawnMobDrop(idrop, this.map.calcDropPos(new Point(this.getPosition()), this.getPosition()), this, chr, (byte)0, (short)0);
                break;
            }
        } else {
            this.stolen = 0;
        }
    }

    public final void setLastNode(int lastNode) {
        this.lastNode = lastNode;
    }

    public final int getLastNode() {
        return this.lastNode;
    }

    public final void setLastNodeController(int lastNode) {
        this.lastNodeController = lastNode;
    }

    public final int getLastNodeController() {
        return this.lastNodeController;
    }

    public final void cancelStatus(MonsterStatus stat) {
        MonsterStatusEffect mse = this.stati.get(stat);
        if (mse == null || !this.isAlive()) {
            return;
        }
        mse.cancelPoisonSchedule();
        this.map.broadcastMessage(MobPacket.cancelMonsterStatus(this.getObjectId(), stat), this.getPosition());
        if (this.getController() != null && !this.getController().isMapObjectVisible(this)) {
            this.getController().getClient().getSession().write((Object)MobPacket.cancelMonsterStatus(this.getObjectId(), stat));
        }
        this.stati.remove(stat);
        this.setVenomMulti((byte)0);
    }

    public final void cancelDropItem() {
        if (this.dropItemSchedule != null) {
            this.dropItemSchedule.cancel(false);
            this.dropItemSchedule = null;
        }
    }

    public final void startDropItemSchedule() {
        int itemId;
        this.cancelDropItem();
        if (this.stats.getDropItemPeriod() <= 0 || !this.isAlive()) {
            return;
        }
        switch (this.getId()) {
            case 9300102: {
                itemId = 4031507;
                break;
            }
            default: {
                return;
            }
        }
        this.shouldDropItem = false;
        this.dropItemSchedule = Timer.MobTimer.getInstance().register(new Runnable(){

            @Override
            public void run() {
                if (MapleMonster.this.isAlive() && MapleMonster.this.map != null) {
                    if (MapleMonster.this.shouldDropItem) {
                        MapleMonster.this.map.spawnAutoDrop(itemId, MapleMonster.this.getPosition());
                    } else {
                        MapleMonster.this.shouldDropItem = true;
                    }
                }
            }
        }, this.stats.getDropItemPeriod() * 1000);
    }

    public MaplePacket getNodePacket() {
        return this.nodepack;
    }

    public void setNodePacket(MaplePacket np) {
        this.nodepack = np;
    }

    public final void killed() {
        if (this.listener != null) {
            this.listener.monsterKilled();
        }
        this.listener = null;
    }

    private class PartyAttackerEntry
    implements AttackerEntry {
        private long totDamage;
        private final Map<Integer, OnePartyAttacker> attackers = new HashMap<Integer, OnePartyAttacker>(6);
        private int partyid;
        private int channel;

        public PartyAttackerEntry(int partyid, int cserv) {
            this.partyid = partyid;
            this.channel = cserv;
        }

        @Override
        public List<AttackingMapleCharacter> getAttackers() {
            ArrayList<AttackingMapleCharacter> ret = new ArrayList<AttackingMapleCharacter>(this.attackers.size());
            for (Map.Entry<Integer, OnePartyAttacker> entry : this.attackers.entrySet()) {
                MapleCharacter chr = MapleMonster.this.map.getCharacterById(entry.getKey());
                if (chr == null) continue;
                ret.add(new AttackingMapleCharacter(chr, entry.getValue().lastAttackTime));
            }
            return ret;
        }

        private final Map<MapleCharacter, OnePartyAttacker> resolveAttackers() {
            HashMap<MapleCharacter, OnePartyAttacker> ret = new HashMap<MapleCharacter, OnePartyAttacker>(this.attackers.size());
            for (Map.Entry<Integer, OnePartyAttacker> aentry : this.attackers.entrySet()) {
                MapleCharacter chr = MapleMonster.this.map.getCharacterById(aentry.getKey());
                if (chr == null) continue;
                ret.put(chr, aentry.getValue());
            }
            return ret;
        }

        @Override
        public final boolean contains(MapleCharacter chr) {
            return this.attackers.containsKey(chr.getId());
        }

        @Override
        public final long getDamage() {
            return this.totDamage;
        }

        @Override
        public void addDamage(MapleCharacter from, long damage, boolean updateAttackTime) {
            OnePartyAttacker oldPartyAttacker = this.attackers.get(from.getId());
            if (oldPartyAttacker != null) {
                oldPartyAttacker.damage += damage;
                oldPartyAttacker.lastKnownParty = from.getParty();
                if (updateAttackTime) {
                    oldPartyAttacker.lastAttackTime = System.currentTimeMillis();
                }
            } else {
                OnePartyAttacker onePartyAttacker = new OnePartyAttacker(from.getParty(), damage);
                this.attackers.put(from.getId(), onePartyAttacker);
                if (!updateAttackTime) {
                    onePartyAttacker.lastAttackTime = 0L;
                }
            }
            this.totDamage += damage;
        }

        @Override
        public final void killedMob(MapleMap map, int baseExp, boolean mostDamage, int lastSkill) {
            MapleCharacter highest = null;
            long highestDamage = 0L;
            int iexp = 0;
            HashMap<MapleCharacter, ExpMap> expMap = new HashMap<MapleCharacter, ExpMap>(6);
            int added_partyinc = 0;
            for (Map.Entry<MapleCharacter, OnePartyAttacker> attacker : this.resolveAttackers().entrySet()) {
                MapleParty party = attacker.getValue().lastKnownParty;
                double averagePartyLevel = 0.0;
                byte Class_Bonus_EXP = 0;
                byte Premium_Bonus_EXP = 0;
                ArrayList<MapleCharacter> expApplicable = new ArrayList<MapleCharacter>();
                for (MaplePartyCharacter partychar : party.getMembers()) {
                    MapleCharacter pchr;
                    if (attacker.getKey().getLevel() - partychar.getLevel() > 5 && MapleMonster.this.stats.getLevel() - partychar.getLevel() > 5 || (pchr = map.getCharacterById(partychar.getId())) == null || !pchr.isAlive() || pchr.getMap() != map) continue;
                    expApplicable.add(pchr);
                    averagePartyLevel += (double)pchr.getLevel();
                    if (Class_Bonus_EXP == 0) {
                        Class_Bonus_EXP = ServerConstants.Class_Bonus_EXP(pchr.getJob());
                    }
                    if (pchr.getStat().equippedWelcomeBackRing && Premium_Bonus_EXP == 0) {
                        Premium_Bonus_EXP = 80;
                    }
                    if (!pchr.getStat().hasPartyBonus || added_partyinc >= 4) continue;
                    added_partyinc = (byte)(added_partyinc + 1);
                }
                double expBonus = 1.0;
                if (expApplicable.size() > 1) {
                    expBonus = 1.1 + 0.05 * (double)expApplicable.size();
                    averagePartyLevel /= (double)expApplicable.size();
                } else {
                    Class_Bonus_EXP = 0;
                }
                long iDamage = attacker.getValue().damage;
                if (iDamage > highestDamage) {
                    highest = attacker.getKey();
                    highestDamage = iDamage;
                }
                double innerBaseExp = (double)baseExp * ((double)iDamage / (double)this.totDamage);
                double expFraction = innerBaseExp * expBonus / (double)(expApplicable.size() + 1);
                for (MapleCharacter expReceiver : expApplicable) {
                    iexp = expMap.get(expReceiver) == null ? 0 : ((ExpMap)expMap.get((Object)expReceiver)).exp;
                    double expWeight = expReceiver == attacker.getKey() ? 2.0 : 0.3;
                    double levelMod = (double)expReceiver.getLevel() / averagePartyLevel;
                    if (levelMod > 1.0 || this.attackers.containsKey(expReceiver.getId())) {
                        levelMod = 1.0;
                    }
                    expMap.put(expReceiver, new ExpMap(iexp += (int)Math.round(expFraction * expWeight * levelMod), (byte)(expApplicable.size() + added_partyinc), Class_Bonus_EXP, Premium_Bonus_EXP));
                }
            }
            for (Map.Entry expReceiver : expMap.entrySet()) {
                ExpMap expmap = (ExpMap)expReceiver.getValue();
                MapleMonster.this.giveExpToCharacter((MapleCharacter)expReceiver.getKey(), expmap.exp, mostDamage ? expReceiver.getKey() == highest : false, expMap.size(), expmap.ptysize, expmap.Class_Bonus_EXP, expmap.Premium_Bonus_EXP, lastSkill);
            }
        }

        public final int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + this.partyid;
            return result;
        }

        public final boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            PartyAttackerEntry other = (PartyAttackerEntry)obj;
            return this.partyid == other.partyid;
        }
    }

    private static final class OnePartyAttacker {
        public MapleParty lastKnownParty;
        public long damage;
        public long lastAttackTime;

        public OnePartyAttacker(MapleParty lastKnownParty, long damage) {
            this.lastKnownParty = lastKnownParty;
            this.damage = damage;
            this.lastAttackTime = System.currentTimeMillis();
        }
    }

    private static final class ExpMap {
        public final int exp;
        public final byte ptysize;
        public final byte Class_Bonus_EXP;
        public final byte Premium_Bonus_EXP;

        public ExpMap(int exp, byte ptysize, byte Class_Bonus_EXP, byte Premium_Bonus_EXP) {
            this.exp = exp;
            this.ptysize = ptysize;
            this.Class_Bonus_EXP = Class_Bonus_EXP;
            this.Premium_Bonus_EXP = Premium_Bonus_EXP;
        }
    }

    private final class SingleAttackerEntry
    implements AttackerEntry {
        private long damage = 0L;
        private int chrid;
        private long lastAttackTime;
        private int channel;

        public SingleAttackerEntry(MapleCharacter from, int cserv) {
            this.chrid = from.getId();
            this.channel = cserv;
        }

        @Override
        public void addDamage(MapleCharacter from, long damage, boolean updateAttackTime) {
            if (this.chrid == from.getId()) {
                this.damage += damage;
                if (updateAttackTime) {
                    this.lastAttackTime = System.currentTimeMillis();
                }
            }
        }

        @Override
        public final List<AttackingMapleCharacter> getAttackers() {
            MapleCharacter chr = MapleMonster.this.map.getCharacterById(this.chrid);
            if (chr != null) {
                return Collections.singletonList(new AttackingMapleCharacter(chr, this.lastAttackTime));
            }
            return Collections.emptyList();
        }

        @Override
        public boolean contains(MapleCharacter chr) {
            return this.chrid == chr.getId();
        }

        @Override
        public long getDamage() {
            return this.damage;
        }

        @Override
        public void killedMob(MapleMap map, int baseExp, boolean mostDamage, int lastSkill) {
            MapleCharacter chr = map.getCharacterById(this.chrid);
            if (chr != null && chr.isAlive()) {
                MapleMonster.this.giveExpToCharacter(chr, baseExp, mostDamage, 1, (byte)0, (byte)0, (byte)0, lastSkill);
            }
        }

        public int hashCode() {
            return this.chrid;
        }

        public final boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            SingleAttackerEntry other = (SingleAttackerEntry)obj;
            return this.chrid == other.chrid;
        }
    }

    private static interface AttackerEntry {
        public List<AttackingMapleCharacter> getAttackers();

        public void addDamage(MapleCharacter var1, long var2, boolean var4);

        public long getDamage();

        public boolean contains(MapleCharacter var1);

        public void killedMob(MapleMap var1, int var2, boolean var3, int var4);
    }

    private static class AttackingMapleCharacter {
        private MapleCharacter attacker;
        private long lastAttackTime;

        public AttackingMapleCharacter(MapleCharacter attacker, long lastAttackTime) {
            this.attacker = attacker;
            this.lastAttackTime = lastAttackTime;
        }

        public final long getLastAttackTime() {
            return this.lastAttackTime;
        }

        public final void setLastAttackTime(long lastAttackTime) {
            this.lastAttackTime = lastAttackTime;
        }

        public final MapleCharacter getAttacker() {
            return this.attacker;
        }
    }

    private final class PoisonTask
    implements Runnable {
        private final int poisonDamage;
        private final MapleCharacter chr;
        private final MonsterStatusEffect status;
        private final Runnable cancelTask;
        private final boolean shadowWeb;
        private final MapleMap map;

        private PoisonTask(int poisonDamage, MapleCharacter chr, MonsterStatusEffect status, Runnable cancelTask, boolean shadowWeb) {
            this.poisonDamage = poisonDamage;
            this.chr = chr;
            this.status = status;
            this.cancelTask = cancelTask;
            this.shadowWeb = shadowWeb;
            this.map = chr.getMap();
        }

        @Override
        public void run() {
            long damage = this.poisonDamage;
            if (damage >= MapleMonster.this.hp) {
                damage = MapleMonster.this.hp - 1L;
                if (!this.shadowWeb) {
                    this.cancelTask.run();
                    this.status.cancelTask();
                }
            }
            if (MapleMonster.this.hp > 1L && damage > 0L) {
                MapleMonster.this.damage(this.chr, damage, false);
                if (this.shadowWeb) {
                    this.map.broadcastMessage(MobPacket.damageMonster(MapleMonster.this.getObjectId(), damage), MapleMonster.this.getPosition());
                }
            }
        }
    }

    private final class ApplyMonsterStatusDelay
    implements Runnable {
        private final MapleCharacter controller;
        private final MapleCharacter chr;
        private final int oid;
        private final MonsterStatusEffect status;
        private Point position;
        private final MapleMap map;
        private final MapleMonster monster;

        private ApplyMonsterStatusDelay(MapleCharacter chr, MapleCharacter Controller, int oid, MonsterStatusEffect status, Point position, MapleMonster mon) {
            this.chr = chr;
            this.controller = Controller;
            this.oid = oid;
            this.status = status;
            this.position = position;
            this.map = chr.getMap();
            this.monster = mon;
        }

        @Override
        public void run() {
            this.map.broadcastMessage(MobPacket.applyMonsterStatus(this.oid, this.status), this.position);
            if (this.controller != null && !this.controller.isMapObjectVisible(this.monster)) {
                this.controller.getClient().getSession().write((Object)MobPacket.applyMonsterStatus(this.oid, this.status));
            }
        }
    }
}

