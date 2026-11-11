/*
 * Decompiled with CFR 0.152.
 */
package server.maps;

import client.MapleCharacter;
import client.MapleQuestStatus;
import handling.channel.ChannelServer;
import handling.world.MaplePartyCharacter;
import java.awt.Point;
import java.util.concurrent.ScheduledFuture;
import server.Randomizer;
import server.Timer;
import server.life.MapleLifeFactory;
import server.maps.MapleMap;
import server.quest.MapleQuest;
import tools.MaplePacketCreator;

public class Event_PyramidSubway {
    private int kill = 0;
    private int cool = 0;
    private int miss = 0;
    private int skill = 0;
    private int type;
    private int energybar = 100;
    private boolean broaded = false;
    private ScheduledFuture<?> energyBarDecrease;
    private ScheduledFuture<?> timerSchedule;
    private ScheduledFuture<?> yetiSchedule;

    public Event_PyramidSubway(final MapleCharacter c) {
        int mapid = c.getMapId();
        this.type = mapid / 10000 == 91032 ? -1 : mapid % 10000 / 1000;
        if (c.getParty() == null || c.getParty().getLeader().equals(new MaplePartyCharacter(c))) {
            this.commenceTimerNextMap(c, 1);
            this.energyBarDecrease = Timer.MapTimer.getInstance().register(new Runnable(){

                @Override
                public void run() {
                    Event_PyramidSubway.this.energybar = Event_PyramidSubway.this.energybar - (c.getParty() != null && c.getParty().getMembers().size() > 1 ? 10 : 5);
                    if (Event_PyramidSubway.this.broaded) {
                        c.getMap().respawn(true);
                    } else {
                        Event_PyramidSubway.this.broaded = true;
                    }
                    if (Event_PyramidSubway.this.energybar <= 0) {
                        Event_PyramidSubway.this.fail(c);
                    }
                }
            }, 1000L);
        }
    }

    public final void fullUpdate(MapleCharacter c, int stage) {
        this.broadcastEnergy(c, "massacre_party", c.getParty() == null ? 0 : c.getParty().getMembers().size());
        this.broadcastEnergy(c, "massacre_miss", this.miss);
        this.broadcastEnergy(c, "massacre_cool", this.cool);
        this.broadcastEnergy(c, "massacre_skill", this.skill);
        this.broadcastEnergy(c, "massacre_laststage", stage - 1);
        this.broadcastEnergy(c, "massacre_hit", this.kill);
        this.broadcastUpdate(c);
    }

    public final void commenceTimerNextMap(final MapleCharacter c, final int stage) {
        if (this.timerSchedule != null) {
            this.timerSchedule.cancel(false);
            this.timerSchedule = null;
        }
        if (this.yetiSchedule != null) {
            this.yetiSchedule.cancel(false);
            this.yetiSchedule = null;
        }
        MapleMap ourMap = c.getMap();
        int time = (this.type == -1 ? 180 : (stage == 1 ? 240 : 300)) - 1;
        if (c.getParty() != null && c.getParty().getMembers().size() > 1) {
            for (MaplePartyCharacter mpc : c.getParty().getMembers()) {
                MapleCharacter chr = ourMap.getCharacterById(mpc.getId());
                if (chr == null) continue;
                chr.getClient().getSession().write((Object)MaplePacketCreator.getClock(time));
                chr.getClient().getSession().write((Object)MaplePacketCreator.showEffect("killing/first/number/" + stage));
                chr.getClient().getSession().write((Object)MaplePacketCreator.showEffect("killing/first/stage"));
                chr.getClient().getSession().write((Object)MaplePacketCreator.showEffect("killing/first/start"));
                this.fullUpdate(chr, stage);
            }
        } else {
            c.getClient().getSession().write((Object)MaplePacketCreator.getClock(time));
            c.getClient().getSession().write((Object)MaplePacketCreator.showEffect("killing/first/number/" + stage));
            c.getClient().getSession().write((Object)MaplePacketCreator.showEffect("killing/first/stage"));
            c.getClient().getSession().write((Object)MaplePacketCreator.showEffect("killing/first/start"));
            this.fullUpdate(c, stage);
        }
        if (this.type != -1 && (stage == 4 || stage == 5)) {
            final Point pos = c.getPosition();
            final MapleMap map = c.getMap();
            this.yetiSchedule = Timer.MapTimer.getInstance().register(new Runnable(){

                @Override
                public void run() {
                    if (map.countMonsterById(9300021) <= (stage == 4 ? 1 : 2)) {
                        map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9300021), new Point(pos));
                    }
                }
            }, 10000L);
        }
        this.timerSchedule = Timer.MapTimer.getInstance().schedule(new Runnable(){

            @Override
            public void run() {
                boolean ret = false;
                ret = Event_PyramidSubway.this.type == -1 ? Event_PyramidSubway.warpNextMap_Subway(c) : Event_PyramidSubway.warpNextMap_Pyramid(c, Event_PyramidSubway.this.type);
                if (!ret) {
                    Event_PyramidSubway.this.fail(c);
                }
            }
        }, (long)time * 1000L);
    }

    public final void onKill(MapleCharacter c) {
        ++this.kill;
        if (Randomizer.nextInt(100) < 5) {
            ++this.cool;
            this.broadcastEnergy(c, "massacre_cool", this.cool);
        }
        this.energybar += 5;
        if (this.energybar > 100) {
            this.energybar = 100;
        }
        if (this.type != -1) {
            for (int i = 5; i >= 1; --i) {
                if ((this.kill + this.cool) % (i * 100) != 0 || Randomizer.nextInt(100) >= 50) continue;
                this.broadcastEffect(c, "killing/yeti" + (i - 1));
                break;
            }
            if ((this.kill + this.cool) % 500 == 0) {
                ++this.skill;
                this.broadcastEnergy(c, "massacre_skill", this.skill);
            }
        }
        this.broadcastUpdate(c);
        this.broadcastEnergy(c, "massacre_hit", this.kill);
    }

    public final void onMiss(MapleCharacter c) {
        ++this.miss;
        this.energybar -= 5;
        this.broadcastUpdate(c);
        this.broadcastEnergy(c, "massacre_miss", this.miss);
    }

    public final boolean onSkillUse(MapleCharacter c) {
        if (this.skill > 0 && this.type != -1) {
            --this.skill;
            this.broadcastEnergy(c, "massacre_skill", this.skill);
            return true;
        }
        return false;
    }

    public final void onChangeMap(MapleCharacter c, int newmapid) {
        if (newmapid == 910330001 && this.type == -1 || newmapid == 926020001 + this.type && this.type != -1) {
            this.succeed(c);
        } else {
            if (this.type == -1 && (newmapid < 910320100 || newmapid > 910320304)) {
                this.dispose(c);
                return;
            }
            if (this.type != -1 && (newmapid < 926010100 || newmapid > 926013504)) {
                this.dispose(c);
                return;
            }
            if (c.getParty() == null || c.getParty().getLeader().equals(new MaplePartyCharacter(c))) {
                this.energybar = 100;
                this.commenceTimerNextMap(c, newmapid % 1000 / 100);
            }
        }
    }

    public final void succeed(MapleCharacter c) {
        MapleQuestStatus record = c.getQuestNAdd(MapleQuest.getInstance(this.type == -1 ? 7662 : 7760));
        String data = record.getCustomData();
        if (data == null) {
            record.setCustomData("0");
            data = record.getCustomData();
        }
        int mons = Integer.parseInt(data);
        int tk = this.kill + this.cool;
        record.setCustomData(String.valueOf(mons + tk));
        byte rank = 4;
        if (this.type == -1) {
            if (tk >= 2000) {
                rank = 0;
            } else if (tk >= 1500 && tk <= 1999) {
                rank = 1;
            } else if (tk >= 1000 && tk <= 1499) {
                rank = 2;
            } else if (tk >= 500 && tk <= 999) {
                rank = 3;
            }
        } else if (tk >= 3000) {
            rank = 0;
        } else if (tk >= 2000 && tk <= 2999) {
            rank = 1;
        } else if (tk >= 1500 && tk <= 1999) {
            rank = 2;
        } else if (tk >= 500 && tk <= 1499) {
            rank = 3;
        }
        int pt = 0;
        block0 : switch (this.type) {
            case 0: {
                switch (rank) {
                    case 0: {
                        pt = 60500;
                        break;
                    }
                    case 1: {
                        pt = 55000;
                        break;
                    }
                    case 2: {
                        pt = 46750;
                        break;
                    }
                    case 3: {
                        pt = 22000;
                    }
                }
                break;
            }
            case 1: {
                switch (rank) {
                    case 0: {
                        pt = 66000;
                        break;
                    }
                    case 1: {
                        pt = 60000;
                        break;
                    }
                    case 2: {
                        pt = 51750;
                        break;
                    }
                    case 3: {
                        pt = 24000;
                    }
                }
                break;
            }
            case 2: {
                switch (rank) {
                    case 0: {
                        pt = 71500;
                        break;
                    }
                    case 1: {
                        pt = 65000;
                        break;
                    }
                    case 2: {
                        pt = 55250;
                        break;
                    }
                    case 3: {
                        pt = 26000;
                    }
                }
                break;
            }
            case 3: {
                switch (rank) {
                    case 0: {
                        pt = 77000;
                        break;
                    }
                    case 1: {
                        pt = 70000;
                        break;
                    }
                    case 2: {
                        pt = 59500;
                        break;
                    }
                    case 3: {
                        pt = 28000;
                    }
                }
                break;
            }
            default: {
                switch (rank) {
                    case 0: {
                        pt = 22000;
                        break block0;
                    }
                    case 1: {
                        pt = 17000;
                        break block0;
                    }
                    case 2: {
                        pt = 10750;
                        break block0;
                    }
                    case 3: {
                        pt = 7000;
                    }
                }
            }
        }
        int exp = 0;
        if (rank < 4) {
            exp = (this.kill * 2 + this.cool * 10 + pt) * c.getClient().getChannelServer().getExpRate();
            c.gainExp(exp, true, false, false);
        }
        c.getClient().getSession().write((Object)MaplePacketCreator.showEffect("killing/clear"));
        c.getClient().getSession().write((Object)MaplePacketCreator.sendPyramidResult(rank, exp));
        this.dispose(c);
    }

    public final void fail(MapleCharacter c) {
        MapleMap map = this.type == -1 ? c.getClient().getChannelServer().getMapFactory().getMap(910320001) : c.getClient().getChannelServer().getMapFactory().getMap(926010001 + this.type);
        Event_PyramidSubway.changeMap(c, map, 1, 200, 2);
        this.dispose(c);
    }

    public final void dispose(MapleCharacter c) {
        boolean lead;
        boolean bl = lead = this.energyBarDecrease != null && this.timerSchedule != null;
        if (this.energyBarDecrease != null) {
            this.energyBarDecrease.cancel(false);
            this.energyBarDecrease = null;
        }
        if (this.timerSchedule != null) {
            this.timerSchedule.cancel(false);
            this.timerSchedule = null;
        }
        if (this.yetiSchedule != null) {
            this.yetiSchedule.cancel(false);
            this.yetiSchedule = null;
        }
        if (c.getParty() != null && lead && c.getParty().getMembers().size() > 1) {
            this.fail(c);
            return;
        }
        c.setPyramidSubway(null);
    }

    public final void broadcastUpdate(MapleCharacter c) {
        MapleMap map = c.getMap();
        if (c.getParty() != null && c.getParty().getMembers().size() > 1) {
            for (MaplePartyCharacter mpc : c.getParty().getMembers()) {
                MapleCharacter chr = map.getCharacterById(mpc.getId());
                if (chr == null) continue;
                chr.getClient().getSession().write((Object)MaplePacketCreator.sendPyramidUpdate(this.energybar));
            }
        } else {
            c.getClient().getSession().write((Object)MaplePacketCreator.sendPyramidUpdate(this.energybar));
        }
    }

    public final void broadcastEffect(MapleCharacter c, String effect) {
        c.getClient().getSession().write((Object)MaplePacketCreator.showEffect(effect));
    }

    public final void broadcastEnergy(MapleCharacter c, String type, int amount) {
        c.getClient().getSession().write((Object)MaplePacketCreator.sendPyramidEnergy(type, String.valueOf(amount)));
    }

    public static boolean warpStartSubway(MapleCharacter c) {
        int mapid = 910320100;
        ChannelServer ch = c.getClient().getChannelServer();
        for (int i = 0; i < 5; ++i) {
            MapleMap map = ch.getMapFactory().getMap(910320100 + i);
            if (map.getCharactersSize() != 0) continue;
            Event_PyramidSubway.clearMap(map, false);
            Event_PyramidSubway.changeMap(c, map, 25, 30);
            return true;
        }
        return false;
    }

    public static boolean warpBonusSubway(MapleCharacter c) {
        int mapid = 910320010;
        ChannelServer ch = c.getClient().getChannelServer();
        for (int i = 0; i < 20; ++i) {
            MapleMap map = ch.getMapFactory().getMap(910320010 + i);
            if (map.getCharactersSize() != 0) continue;
            Event_PyramidSubway.clearMap(map, false);
            c.changeMap(map, map.getPortal(0));
            return true;
        }
        return false;
    }

    public static boolean warpNextMap_Subway(MapleCharacter c) {
        int currentmap = c.getMapId();
        int thisStage = (currentmap - 910320100) / 100;
        MapleMap map = c.getMap();
        Event_PyramidSubway.clearMap(map, true);
        ChannelServer ch = c.getClient().getChannelServer();
        if (thisStage >= 2) {
            map = ch.getMapFactory().getMap(910330001);
            Event_PyramidSubway.changeMap(c, map, 1, 200, 1);
            return true;
        }
        int nextmapid = 910320100 + (thisStage + 1) * 100;
        for (int i = 0; i < 5; ++i) {
            map = ch.getMapFactory().getMap(nextmapid + i);
            if (map.getCharactersSize() != 0) continue;
            Event_PyramidSubway.clearMap(map, false);
            Event_PyramidSubway.changeMap(c, map, 1, 200, 1);
            return true;
        }
        return false;
    }

    public static boolean warpStartPyramid(MapleCharacter c, int difficulty) {
        int mapid = 926010100 + difficulty * 1000;
        int minLevel = 40;
        int maxLevel = 60;
        switch (difficulty) {
            case 1: {
                minLevel = 45;
                break;
            }
            case 2: {
                minLevel = 50;
                break;
            }
            case 3: {
                minLevel = 61;
                maxLevel = 200;
            }
        }
        ChannelServer ch = c.getClient().getChannelServer();
        for (int i = 0; i < 5; ++i) {
            MapleMap map = ch.getMapFactory().getMap(mapid + i);
            if (map.getCharactersSize() != 0) continue;
            Event_PyramidSubway.clearMap(map, false);
            Event_PyramidSubway.changeMap(c, map, minLevel, maxLevel);
            return true;
        }
        return false;
    }

    public static boolean warpBonusPyramid(MapleCharacter c, int difficulty) {
        int mapid = 926010010 + difficulty * 20;
        ChannelServer ch = c.getClient().getChannelServer();
        for (int i = 0; i < 20; ++i) {
            MapleMap map = ch.getMapFactory().getMap(mapid + i);
            if (map.getCharactersSize() != 0) continue;
            Event_PyramidSubway.clearMap(map, false);
            c.changeMap(map, map.getPortal(0));
            return true;
        }
        return false;
    }

    public static boolean warpNextMap_Pyramid(MapleCharacter c, int difficulty) {
        int currentmap = c.getMapId();
        int thisStage = (currentmap - (926010100 + difficulty * 1000)) / 100;
        MapleMap map = c.getMap();
        Event_PyramidSubway.clearMap(map, true);
        ChannelServer ch = c.getClient().getChannelServer();
        if (thisStage >= 4) {
            map = ch.getMapFactory().getMap(926020001 + difficulty);
            Event_PyramidSubway.changeMap(c, map, 1, 200, 1);
            return true;
        }
        int nextmapid = 926010100 + (thisStage + 1) * 100 + difficulty * 1000;
        for (int i = 0; i < 5; ++i) {
            map = ch.getMapFactory().getMap(nextmapid + i);
            if (map.getCharactersSize() != 0) continue;
            Event_PyramidSubway.clearMap(map, false);
            Event_PyramidSubway.changeMap(c, map, 1, 200, 1);
            return true;
        }
        return false;
    }

    private static final void changeMap(MapleCharacter c, MapleMap map, int minLevel, int maxLevel) {
        Event_PyramidSubway.changeMap(c, map, minLevel, maxLevel, 0);
    }

    private static final void changeMap(MapleCharacter c, MapleMap map, int minLevel, int maxLevel, int clear) {
        MapleMap oldMap = c.getMap();
        if (c.getParty() != null && c.getParty().getMembers().size() > 1) {
            for (MaplePartyCharacter mpc : c.getParty().getMembers()) {
                MapleCharacter chr = oldMap.getCharacterById(mpc.getId());
                if (chr == null || chr.getId() == c.getId() || chr.getLevel() < minLevel || chr.getLevel() > maxLevel) continue;
                if (clear == 1) {
                    chr.getClient().getSession().write((Object)MaplePacketCreator.showEffect("killing/clear"));
                } else if (clear == 2) {
                    chr.getClient().getSession().write((Object)MaplePacketCreator.showEffect("killing/fail"));
                }
                chr.changeMap(map, map.getPortal(0));
            }
        }
        if (clear == 1) {
            c.getClient().getSession().write((Object)MaplePacketCreator.showEffect("killing/clear"));
        } else if (clear == 2) {
            c.getClient().getSession().write((Object)MaplePacketCreator.showEffect("killing/fail"));
        }
        c.changeMap(map, map.getPortal(0));
    }

    private static final void clearMap(MapleMap map, boolean check) {
        if (check && map.getCharactersSize() > 0) {
            return;
        }
        map.resetFully(false);
    }
}

