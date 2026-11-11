/*
 * Decompiled with CFR 0.152.
 */
package server.maps;

import client.MapleCharacter;
import handling.world.World;
import java.awt.Point;
import server.Randomizer;
import server.Timer;
import server.life.MapleLifeFactory;
import server.maps.MapleMap;
import server.maps.MapleReactor;
import tools.MaplePacketCreator;

public class AramiaFireWorks {
    public static final int KEG_ID = 4031875;
    public static final int SUN_ID = 4001246;
    public static final int DEC_ID = 4001473;
    public static final int MAX_KEGS = 10000;
    public static final int MAX_SUN = 14000;
    public static final int MAX_DEC = 18000;
    private short kegs = 0;
    private short sunshines = (short)2333;
    private short decorations = (short)3000;
    private static final AramiaFireWorks instance = new AramiaFireWorks();
    private static final int[] arrayMob = new int[]{9400708};
    private static final int[] arrayX = new int[]{-115};
    private static final int[] arrayY = new int[]{154};
    private static final int[] array_X = new int[]{720, 180, 630, 270, 360, 540, 450, 142, 142, 218, 772, 810, 848, 232, 308, 142};
    private static final int[] array_Y = new int[]{1234, 1234, 1174, 1234, 1174, 1174, 1174, 1260, 1234, 1234, 1234, 1234, 1234, 1114, 1114, 1140};
    private static final int flake_Y = 149;

    public static final AramiaFireWorks getInstance() {
        return instance;
    }

    public final void giveKegs(MapleCharacter c, int kegs) {
        this.kegs = (short)(this.kegs + kegs);
        if (this.kegs >= 10000) {
            this.kegs = 0;
            this.broadcastEvent(c);
        }
    }

    private final void broadcastServer(MapleCharacter c, int itemid) {
        World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, itemid, "<\u983b\u9053 " + c.getClient().getChannel() + "> \u5f13\u7bad\u624b\u6751\u90b1\u6bd4\u7279\u516c\u5712\u5373\u5c07\u958b\u59cb\u767c\u5c04\u7159\u706b!").getBytes());
    }

    public final short getKegsPercentage() {
        return (short)(this.kegs / 10000 * 10000);
    }

    private final void broadcastEvent(final MapleCharacter c) {
        Timer.EventTimer.getInstance().schedule(new Runnable(){

            @Override
            public final void run() {
                AramiaFireWorks.this.startEvent(c.getClient().getChannelServer().getMapFactory().getMap(209080000));
            }
        }, 10000L);
    }

    private final void startEvent(final MapleMap map) {
        map.startMapEffect("\u96ea\u4eba\u5927\u5927\u51fa\u73fe\u5566", 5120000);
        Timer.EventTimer.getInstance().schedule(new Runnable(){

            @Override
            public final void run() {
                AramiaFireWorks.this.spawnMonster(map);
            }
        }, 5000L);
    }

    private final void spawnMonster(MapleMap map) {
        for (int i = 0; i < arrayMob.length; ++i) {
            Point pos = new Point(arrayX[i], arrayY[i]);
            map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(arrayMob[i]), pos);
        }
    }

    public final void giveSuns(MapleCharacter c, int kegs) {
        this.sunshines = (short)(this.sunshines + kegs);
        MapleMap map = c.getClient().getChannelServer().getMapFactory().getMap(555000000);
        MapleReactor reactor = map.getReactorByName("XmasTree");
        block3: for (int gogo = kegs + 2333; gogo > 0; gogo -= 2333) {
            switch (reactor.getState()) {
                case 0: 
                case 1: 
                case 2: 
                case 3: 
                case 4: {
                    if (this.sunshines < 2333 * (2 + reactor.getState())) continue block3;
                    reactor.setState((byte)(reactor.getState() + 1));
                    reactor.setTimerActive(false);
                    map.broadcastMessage(MaplePacketCreator.triggerReactor(reactor, reactor.getState()));
                    continue block3;
                }
                default: {
                    if (this.sunshines < 2333) continue block3;
                    map.resetReactors();
                }
            }
        }
        if (this.sunshines >= 14000) {
            this.sunshines = 0;
            this.broadcastSun(c);
        }
    }

    public final short getSunsPercentage() {
        return (short)(this.sunshines / 14000 * 10000);
    }

    private final void broadcastSun(final MapleCharacter c) {
        this.broadcastServer(c, 4001246);
        Timer.EventTimer.getInstance().schedule(new Runnable(){

            @Override
            public final void run() {
                AramiaFireWorks.this.startSun(c.getClient().getChannelServer().getMapFactory().getMap(970010000));
            }
        }, 10000L);
    }

    private final void startSun(final MapleMap map) {
        map.startMapEffect("The tree is bursting with sunshine!", 5121010);
        for (int i = 0; i < 3; ++i) {
            Timer.EventTimer.getInstance().schedule(new Runnable(){

                @Override
                public final void run() {
                    AramiaFireWorks.this.spawnItem(map);
                }
            }, 5000 + i * 10000);
        }
    }

    private final void spawnItem(MapleMap map) {
        for (int i = 0; i < Randomizer.nextInt(5) + 10; ++i) {
            Point pos = new Point(array_X[i], array_Y[i]);
            map.spawnAutoDrop(Randomizer.nextInt(3) == 1 ? 3010025 : 4001246, pos);
        }
    }

    public final void giveDecs(MapleCharacter c, int kegs) {
        this.decorations = (short)(this.decorations + kegs);
        MapleMap map = c.getClient().getChannelServer().getMapFactory().getMap(555000000);
        MapleReactor reactor = map.getReactorByName("XmasTree");
        block3: for (int gogo = kegs + 3000; gogo > 0; gogo -= 3000) {
            switch (reactor.getState()) {
                case 0: 
                case 1: 
                case 2: 
                case 3: 
                case 4: {
                    if (this.decorations < 3000 * (2 + reactor.getState())) continue block3;
                    reactor.setState((byte)(reactor.getState() + 1));
                    reactor.setTimerActive(false);
                    map.broadcastMessage(MaplePacketCreator.triggerReactor(reactor, reactor.getState()));
                    continue block3;
                }
                default: {
                    if (this.decorations < 3000) continue block3;
                    map.resetReactors();
                }
            }
        }
        if (this.decorations >= 18000) {
            this.decorations = 0;
            this.broadcastDec(c);
        }
    }

    public final short getDecsPercentage() {
        return (short)(this.decorations / 18000 * 10000);
    }

    private final void broadcastDec(final MapleCharacter c) {
        this.broadcastServer(c, 4001473);
        Timer.EventTimer.getInstance().schedule(new Runnable(){

            @Override
            public final void run() {
                AramiaFireWorks.this.startDec(c.getClient().getChannelServer().getMapFactory().getMap(555000000));
            }
        }, 10000L);
    }

    private final void startDec(final MapleMap map) {
        map.startMapEffect("The tree is bursting with snow!", 5120000);
        for (int i = 0; i < 3; ++i) {
            Timer.EventTimer.getInstance().schedule(new Runnable(){

                @Override
                public final void run() {
                    AramiaFireWorks.this.spawnDec(map);
                }
            }, 5000 + i * 10000);
        }
    }

    private final void spawnDec(MapleMap map) {
        for (int i = 0; i < Randomizer.nextInt(10) + 40; ++i) {
            Point pos = new Point(Randomizer.nextInt(800) - 400, 149);
            map.spawnAutoDrop(Randomizer.nextInt(15) == 1 ? 2060006 : 2060006, pos);
        }
    }
}

