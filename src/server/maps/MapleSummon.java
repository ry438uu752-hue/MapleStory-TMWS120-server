/*
 * Decompiled with CFR 0.152.
 */
package server.maps;

import client.MapleCharacter;
import client.MapleClient;
import client.anticheat.CheatingOffense;
import constants.GameConstants;
import java.awt.Point;
import server.MapleStatEffect;
import server.maps.AbstractAnimatedMapleMapObject;
import server.maps.MapleMap;
import server.maps.MapleMapObjectType;
import server.maps.SummonMovementType;
import tools.MaplePacketCreator;

public class MapleSummon
extends AbstractAnimatedMapleMapObject {
    private final int ownerid;
    private final int skillLevel;
    private final int ownerLevel;
    private final int skill;
    private int fh;
    private MapleMap map;
    private short hp;
    private boolean changedMap = false;
    private SummonMovementType movementType;
    private int lastSummonTickCount;
    private byte Summon_tickResetCount;
    private long Server_ClientSummonTickDiff;

    public MapleSummon(MapleCharacter owner, MapleStatEffect skill, Point pos, SummonMovementType movementType) {
        this.ownerid = owner.getId();
        this.ownerLevel = owner.getLevel();
        this.skill = skill.getSourceId();
        this.map = owner.getMap();
        this.skillLevel = skill.getLevel();
        this.movementType = movementType;
        this.setPosition(pos);
        try {
            this.fh = owner.getMap().getFootholds().findBelow(pos).getId();
        }
        catch (NullPointerException e) {
            this.fh = 0;
        }
        if (!this.isPuppet()) {
            this.lastSummonTickCount = 0;
            this.Summon_tickResetCount = 0;
            this.Server_ClientSummonTickDiff = 0L;
        }
    }

    @Override
    public final void sendSpawnData(MapleClient client) {
        if (this != null) {
            client.getSession().write((Object)MaplePacketCreator.spawnSummon(this, false));
        }
    }

    @Override
    public final void sendDestroyData(MapleClient client) {
        client.getSession().write((Object)MaplePacketCreator.removeSummon(this, false));
    }

    public final void updateMap(MapleMap map) {
        this.map = map;
    }

    public final MapleCharacter getOwner() {
        return this.map.getCharacterById(this.ownerid);
    }

    public final int getFh() {
        return this.fh;
    }

    public final void setFh(int fh) {
        this.fh = fh;
    }

    public final int getOwnerId() {
        return this.ownerid;
    }

    public final int getOwnerLevel() {
        return this.ownerLevel;
    }

    public final int getSkill() {
        return this.skill;
    }

    public final short getHP() {
        return this.hp;
    }

    public final void addHP(short delta) {
        this.hp = (short)(this.hp + delta);
    }

    public final SummonMovementType getMovementType() {
        return this.movementType;
    }

    public final boolean isPuppet() {
        switch (this.skill) {
            case 3111002: 
            case 3211002: 
            case 4341006: 
            case 13111004: 
            case 33111003: {
                return true;
            }
        }
        return false;
    }

    public final boolean isGaviota() {
        return this.skill == 5211002;
    }

    public final boolean isBeholder() {
        return this.skill == 1321007;
    }

    public final boolean isMultiSummon() {
        return this.skill == 5211002 || this.skill == 5211001 || this.skill == 5220002 || this.skill == 32111006;
    }

    public final boolean isSummon() {
        switch (this.skill) {
            case 1321007: 
            case 2121005: 
            case 2221005: 
            case 2311006: 
            case 2321003: 
            case 5211001: 
            case 5211002: 
            case 5220002: 
            case 11001004: 
            case 12001004: 
            case 12111004: 
            case 13001004: 
            case 13111004: 
            case 14001005: 
            case 15001004: 
            case 32111006: 
            case 33111005: 
            case 35111001: 
            case 35111002: 
            case 35111004: 
            case 35111005: 
            case 35111009: 
            case 35111010: 
            case 35121009: 
            case 35121011: {
                return true;
            }
        }
        return false;
    }

    public final int getSkillLevel() {
        return this.skillLevel;
    }

    public final int getSummonType() {
        if (this.isPuppet()) {
            return 0;
        }
        switch (this.skill) {
            case 1321007: {
                return 2;
            }
            case 35111001: 
            case 35111009: 
            case 35111010: {
                return 3;
            }
            case 35121009: {
                return 4;
            }
        }
        return 1;
    }

    @Override
    public final MapleMapObjectType getType() {
        return MapleMapObjectType.SUMMON;
    }

    public final void CheckSummonAttackFrequency(MapleCharacter chr, int tickcount) {
        long STime_TC;
        long S_C_Difference;
        int tickdifference = tickcount - this.lastSummonTickCount;
        if (tickdifference < GameConstants.getSummonAttackDelay(this.skill)) {
            chr.getCheatTracker().registerOffense(CheatingOffense.FAST_SUMMON_ATTACK);
        }
        if ((S_C_Difference = this.Server_ClientSummonTickDiff - (STime_TC = System.currentTimeMillis() - (long)tickcount)) > 200L) {
            chr.getCheatTracker().registerOffense(CheatingOffense.FAST_SUMMON_ATTACK);
        }
        this.Summon_tickResetCount = (byte)(this.Summon_tickResetCount + 1);
        if (this.Summon_tickResetCount > 4) {
            this.Summon_tickResetCount = 0;
            this.Server_ClientSummonTickDiff = STime_TC;
        }
        this.lastSummonTickCount = tickcount;
    }

    public final boolean isChangedMap() {
        return this.changedMap;
    }

    public final void setChangedMap(boolean cm) {
        this.changedMap = cm;
    }
}

