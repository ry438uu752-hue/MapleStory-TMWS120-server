/*
 * Decompiled with CFR 0.152.
 */
package server.events;

import client.MapleCharacter;
import handling.MaplePacket;
import handling.channel.ChannelServer;
import handling.world.World;
import server.MapleInventoryManipulator;
import server.RandomRewards;
import server.Randomizer;
import server.Timer;
import server.events.MapleEventType;
import server.maps.MapleMap;
import server.maps.SavedLocationType;
import tools.MaplePacketCreator;

public abstract class MapleEvent {
    protected int[] mapid;
    protected int channel;
    protected boolean isRunning = false;

    public MapleEvent(int channel, int[] mapid) {
        this.channel = channel;
        this.mapid = mapid;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public MapleMap getMap(int i) {
        return this.getChannelServer().getMapFactory().getMap(this.mapid[i]);
    }

    public ChannelServer getChannelServer() {
        return ChannelServer.getInstance(this.channel);
    }

    public void broadcast(MaplePacket packet) {
        for (int i = 0; i < this.mapid.length; ++i) {
            this.getMap(i).broadcastMessage(packet);
        }
    }

    public void givePrize(MapleCharacter chr) {
        int reward = RandomRewards.getInstance().getEventReward();
        if (reward == 0) {
            chr.gainMeso(66666, true, false, false);
            chr.dropMessage(5, "\u4f60\u7372\u5f97 166666 \u6953\u5e63");
        } else if (reward == 1) {
            chr.gainMeso(399999, true, false, false);
            chr.dropMessage(5, "\u4f60\u7372\u5f97 399999 \u6953\u5e63");
        } else if (reward == 2) {
            chr.gainMeso(666666, true, false, false);
            chr.dropMessage(5, "\u4f60\u7372\u5f97 666666 \u6953\u5e63");
        } else if (reward == 3) {
            chr.addFame(10);
            chr.dropMessage(5, "\u4f60\u7372\u5f97 10 \u540d\u8072");
        } else {
            int max_quantity = 1;
            switch (reward) {
                case 5062000: {
                    max_quantity = 3;
                    break;
                }
                case 5220000: {
                    max_quantity = 25;
                    break;
                }
                case 4031307: 
                case 5050000: {
                    max_quantity = 5;
                    break;
                }
                case 2022121: {
                    max_quantity = 10;
                }
            }
            int quantity = (max_quantity > 1 ? Randomizer.nextInt(max_quantity) : 0) + 1;
            if (MapleInventoryManipulator.checkSpace(chr.getClient(), reward, quantity, "")) {
                MapleInventoryManipulator.addById(chr.getClient(), reward, (short)quantity);
            } else {
                chr.gainMeso(100000, true, false, false);
                chr.dropMessage(5, "\u53c3\u52a0\u734e 100000 \u6953\u5e63");
            }
        }
    }

    public void finished(MapleCharacter chr) {
    }

    public void onMapLoad(MapleCharacter chr) {
    }

    public void startEvent() {
    }

    public void warpBack(MapleCharacter chr) {
        int map = chr.getSavedLocation(SavedLocationType.EVENT);
        if (map <= -1) {
            map = 104000000;
        }
        MapleMap mapp = chr.getClient().getChannelServer().getMapFactory().getMap(map);
        chr.changeMap(mapp, mapp.getPortal(0));
    }

    public void reset() {
        this.isRunning = true;
    }

    public void unreset() {
        this.isRunning = false;
    }

    public static final void setEvent(ChannelServer cserv, boolean auto) {
        if (auto) {
            block0: for (MapleEventType t : MapleEventType.values()) {
                final MapleEvent e = cserv.getEvent(t);
                if (!e.isRunning) continue;
                for (int i : e.mapid) {
                    if (cserv.getEvent() != i) continue;
                    e.broadcast(MaplePacketCreator.serverNotice(0, "\u8ddd\u96e2\u6d3b\u52d5\u958b\u59cb\u53ea\u5269\u4e00\u5206\u9418!"));
                    e.broadcast(MaplePacketCreator.getClock(60));
                    Timer.EventTimer.getInstance().schedule(new Runnable(){

                        @Override
                        public void run() {
                            e.startEvent();
                        }
                    }, 60000L);
                    continue block0;
                }
            }
        }
        cserv.setEvent(-1);
    }

    public static final void mapLoad(MapleCharacter chr, int channel) {
        if (chr == null) {
            return;
        }
        for (MapleEventType t : MapleEventType.values()) {
            MapleEvent e = ChannelServer.getInstance(channel).getEvent(t);
            if (!e.isRunning) continue;
            if (chr.getMapId() == 109050000) {
                e.finished(chr);
            }
            for (int i : e.mapid) {
                if (chr.getMapId() != i) continue;
                e.onMapLoad(chr);
            }
        }
    }

    public static final void onStartEvent(MapleCharacter chr) {
        for (MapleEventType t : MapleEventType.values()) {
            MapleEvent e = chr.getClient().getChannelServer().getEvent(t);
            if (!e.isRunning) continue;
            for (int i : e.mapid) {
                if (chr.getMapId() != i) continue;
                e.startEvent();
                chr.dropMessage(5, String.valueOf((Object)t) + " \u6d3b\u52d5\u958b\u59cb");
            }
        }
    }

    public static final String scheduleEvent(MapleEventType event, ChannelServer cserv) {
        if (cserv.getEvent() != -1 || cserv.getEvent(event) == null) {
            return "\u8a72\u6d3b\u52d5\u5df2\u7d93\u88ab\u7981\u6b62\u5b89\u6392\u4e86.";
        }
        for (int i : cserv.getEvent((MapleEventType)event).mapid) {
            if (cserv.getMapFactory().getMap(i).getCharactersSize() <= 0) continue;
            return "\u8a72\u6d3b\u52d5\u5df2\u7d93\u5728\u57f7\u884c\u4e2d.";
        }
        cserv.setEvent(cserv.getEvent((MapleEventType)event).mapid[0]);
        cserv.getEvent(event).reset();
        World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(0, "\u6d3b\u52d5 " + String.valueOf((Object)event) + " \u5373\u5c07\u5728\u983b\u9053 " + cserv.getChannel() + " \u8209\u884c , \u8981\u53c3\u52a0\u7684\u73a9\u5bb6\u8acb\u5230\u983b\u9053 " + cserv.getChannel()).getBytes());
        return "";
    }
}

