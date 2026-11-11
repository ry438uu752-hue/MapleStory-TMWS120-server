/*
 * Decompiled with CFR 0.152.
 */
package server.events;

import client.MapleCharacter;
import java.util.concurrent.ScheduledFuture;
import server.Randomizer;
import server.Timer;
import server.events.MapleEvent;
import tools.MaplePacketCreator;

public class MapleOla
extends MapleEvent {
    private static final long serialVersionUID = 845748150824L;
    private long time = 600000L;
    private long timeStarted = 0L;
    private transient ScheduledFuture<?> olaSchedule;
    private int[] stages = new int[3];

    public MapleOla(int channel, int[] mapid) {
        super(channel, mapid);
    }

    @Override
    public void finished(MapleCharacter chr) {
        this.givePrize(chr);
    }

    @Override
    public void onMapLoad(MapleCharacter chr) {
        if (this.isTimerStarted()) {
            chr.getClient().getSession().write((Object)MaplePacketCreator.getClock((int)(this.getTimeLeft() / 1000L)));
        }
    }

    @Override
    public void startEvent() {
        this.unreset();
        super.reset();
        this.broadcast(MaplePacketCreator.getClock((int)(this.time / 1000L)));
        this.timeStarted = System.currentTimeMillis();
        this.olaSchedule = Timer.EventTimer.getInstance().schedule(new Runnable(){

            @Override
            public void run() {
                for (int i = 0; i < MapleOla.this.mapid.length; ++i) {
                    for (MapleCharacter chr : MapleOla.this.getMap(i).getCharactersThreadsafe()) {
                        MapleOla.this.warpBack(chr);
                    }
                    MapleOla.this.unreset();
                }
            }
        }, this.time);
        this.broadcast(MaplePacketCreator.serverNotice(0, "The portal has now opened. Press the up arrow key at the portal to enter."));
    }

    public boolean isTimerStarted() {
        return this.timeStarted > 0L;
    }

    public long getTime() {
        return this.time;
    }

    public void resetSchedule() {
        this.timeStarted = 0L;
        if (this.olaSchedule != null) {
            this.olaSchedule.cancel(false);
        }
        this.olaSchedule = null;
    }

    @Override
    public void reset() {
        super.reset();
        this.resetSchedule();
        this.getMap(0).getPortal("join00").setPortalState(false);
        this.stages = new int[]{0, 0, 0};
    }

    @Override
    public void unreset() {
        super.unreset();
        this.resetSchedule();
        this.getMap(0).getPortal("join00").setPortalState(true);
        this.stages = new int[]{Randomizer.nextInt(5), Randomizer.nextInt(8), Randomizer.nextInt(15)};
    }

    public long getTimeLeft() {
        return this.time - (System.currentTimeMillis() - this.timeStarted);
    }

    public boolean isCharCorrect(String portalName, int mapid) {
        int st = this.stages[mapid % 10 - 1];
        return portalName.equals("ch" + (st < 10 ? "0" : "") + st);
    }
}

