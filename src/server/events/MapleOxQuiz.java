/*
 * Decompiled with CFR 0.152.
 */
package server.events;

import client.MapleCharacter;
import client.MapleStat;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import server.Timer;
import server.events.MapleEvent;
import server.events.MapleOxQuizFactory;
import server.maps.MapleMap;
import tools.MaplePacketCreator;
import tools.Pair;

public class MapleOxQuiz
extends MapleEvent {
    private ScheduledFuture<?> oxSchedule;
    private ScheduledFuture<?> oxSchedule2;
    private int timesAsked = 0;

    public MapleOxQuiz(int channel, int[] mapid) {
        super(channel, mapid);
    }

    private void resetSchedule() {
        if (this.oxSchedule != null) {
            this.oxSchedule.cancel(false);
            this.oxSchedule = null;
        }
        if (this.oxSchedule2 != null) {
            this.oxSchedule2.cancel(false);
            this.oxSchedule2 = null;
        }
    }

    @Override
    public void onMapLoad(MapleCharacter chr) {
        if (chr.getMapId() == this.mapid[0] && !chr.isGM()) {
            chr.canTalk(false);
        }
    }

    @Override
    public void reset() {
        super.reset();
        this.getMap(0).getPortal("join00").setPortalState(false);
        this.resetSchedule();
        this.timesAsked = 0;
    }

    @Override
    public void unreset() {
        super.unreset();
        this.getMap(0).getPortal("join00").setPortalState(true);
        this.resetSchedule();
    }

    @Override
    public void startEvent() {
        this.sendQuestion();
    }

    public void sendQuestion() {
        this.sendQuestion(this.getMap(0));
    }

    public void sendQuestion(final MapleMap toSend) {
        if (this.oxSchedule2 != null) {
            this.oxSchedule2.cancel(false);
        }
        this.oxSchedule2 = Timer.EventTimer.getInstance().schedule(new Runnable(){

            @Override
            public void run() {
                int number = 0;
                for (MapleCharacter mc : toSend.getCharactersThreadsafe()) {
                    if (!mc.isGM() && mc.isAlive()) continue;
                    ++number;
                }
                if (toSend.getCharactersSize() - number <= 1 || MapleOxQuiz.this.timesAsked == 10) {
                    toSend.broadcastMessage(MaplePacketCreator.serverNotice(6, "The event has ended"));
                    MapleOxQuiz.this.unreset();
                    for (MapleCharacter chr : toSend.getCharactersThreadsafe()) {
                        if (chr == null || chr.isGM() || !chr.isAlive()) continue;
                        chr.canTalk(true);
                        MapleOxQuiz.this.givePrize(chr);
                        MapleOxQuiz.this.warpBack(chr);
                    }
                    return;
                }
                final Map.Entry<Pair<Integer, Integer>, MapleOxQuizFactory.MapleOxQuizEntry> question = MapleOxQuizFactory.getInstance().grabRandomQuestion();
                toSend.broadcastMessage(MaplePacketCreator.showOXQuiz((Integer)question.getKey().left, (Integer)question.getKey().right, true));
                toSend.broadcastMessage(MaplePacketCreator.getClock(12));
                if (MapleOxQuiz.this.oxSchedule != null) {
                    MapleOxQuiz.this.oxSchedule.cancel(false);
                }
                MapleOxQuiz.this.oxSchedule = Timer.EventTimer.getInstance().schedule(new Runnable(){

                    @Override
                    public void run() {
                        toSend.broadcastMessage(MaplePacketCreator.showOXQuiz((Integer)((Pair)question.getKey()).left, (Integer)((Pair)question.getKey()).right, false));
                        MapleOxQuiz.this.timesAsked++;
                        for (MapleCharacter chr : toSend.getCharactersThreadsafe()) {
                            if (chr == null || chr.isGM() || !chr.isAlive()) continue;
                            if (!MapleOxQuiz.this.isCorrectAnswer(chr, ((MapleOxQuizFactory.MapleOxQuizEntry)question.getValue()).getAnswer())) {
                                chr.getStat().setHp(0);
                                chr.updateSingleStat(MapleStat.HP, 0);
                                continue;
                            }
                            chr.gainExp(3000, true, true, false);
                        }
                        MapleOxQuiz.this.sendQuestion();
                    }
                }, 12000L);
            }
        }, 10000L);
    }

    private boolean isCorrectAnswer(MapleCharacter chr, int answer) {
        double x = chr.getPosition().getX();
        double y = chr.getPosition().getY();
        if (x > -234.0 && y > -26.0 && answer == 0 || x < -234.0 && y > -26.0 && answer == 1) {
            chr.dropMessage(6, "[Ox Quiz] Correct!");
            return true;
        }
        chr.dropMessage(6, "[Ox Quiz] Incorrect!");
        return false;
    }
}

