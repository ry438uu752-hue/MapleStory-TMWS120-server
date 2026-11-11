/*
 * Decompiled with CFR 0.152.
 */
package server.events;

import client.MapleCharacter;
import client.MapleDisease;
import java.util.concurrent.ScheduledFuture;
import server.Timer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.MobSkillFactory;
import server.maps.MapleMap;
import tools.MaplePacketCreator;

public class MapleSnowball
extends MapleEvent {
    private MapleSnowballs[] balls = new MapleSnowballs[2];

    public MapleSnowball(int channel, int[] mapid) {
        super(channel, mapid);
    }

    @Override
    public void unreset() {
        super.unreset();
        for (int i = 0; i < 2; ++i) {
            this.getSnowBall(i).resetSchedule();
            this.resetSnowBall(i);
        }
    }

    @Override
    public void reset() {
        super.reset();
        this.makeSnowBall(0);
        this.makeSnowBall(1);
    }

    @Override
    public void startEvent() {
        for (int i = 0; i < 2; ++i) {
            MapleSnowballs ball = this.getSnowBall(i);
            ball.broadcast(this.getMap(0), 0);
            ball.setInvis(false);
            ball.broadcast(this.getMap(0), 5);
            this.getMap(0).broadcastMessage(MaplePacketCreator.enterSnowBall());
        }
    }

    public void resetSnowBall(int teamz) {
        this.balls[teamz] = null;
    }

    public void makeSnowBall(int teamz) {
        this.resetSnowBall(teamz);
        this.balls[teamz] = new MapleSnowballs(teamz);
    }

    public MapleSnowballs getSnowBall(int teamz) {
        return this.balls[teamz];
    }

    public static class MapleSnowballs {
        private int position = 0;
        private final int team;
        private int startPoint = 0;
        private boolean invis = true;
        private boolean hittable = true;
        private int snowmanhp = 7500;
        private ScheduledFuture<?> snowmanSchedule = null;

        public MapleSnowballs(int team_) {
            this.team = team_;
        }

        public void resetSchedule() {
            if (this.snowmanSchedule != null) {
                this.snowmanSchedule.cancel(false);
                this.snowmanSchedule = null;
            }
        }

        public int getTeam() {
            return this.team;
        }

        public int getPosition() {
            return this.position;
        }

        public void setPositionX(int pos) {
            this.position = pos;
        }

        public void setStartPoint(MapleMap map) {
            ++this.startPoint;
            this.broadcast(map, this.startPoint);
        }

        public boolean isInvis() {
            return this.invis;
        }

        public void setInvis(boolean i) {
            this.invis = i;
        }

        public boolean isHittable() {
            return this.hittable && !this.invis;
        }

        public void setHittable(boolean b) {
            this.hittable = b;
        }

        public int getSnowmanHP() {
            return this.snowmanhp;
        }

        public void setSnowmanHP(int shp) {
            this.snowmanhp = shp;
        }

        public void broadcast(MapleMap map, int message) {
            for (MapleCharacter chr : map.getCharactersThreadsafe()) {
                chr.getClient().getSession().write((Object)MaplePacketCreator.snowballMessage(this.team, message));
            }
        }

        public int getLeftX() {
            return this.position * 3 + 175;
        }

        public int getRightX() {
            return this.getLeftX() + 275;
        }

        public static final void hitSnowball(MapleCharacter chr) {
            block10: {
                MapleSnowballs ball;
                MapleSnowball sb;
                int team;
                block11: {
                    block14: {
                        block13: {
                            block12: {
                                boolean snowman;
                                team = chr.getPosition().y > -80 ? 0 : 1;
                                sb = (MapleSnowball)chr.getClient().getChannelServer().getEvent(MapleEventType.Snowball);
                                ball = sb.getSnowBall(team);
                                if (ball == null || ball.isInvis()) break block10;
                                boolean bl = snowman = chr.getPosition().x < -360 && chr.getPosition().x > -560;
                                if (snowman) break block11;
                                int damage = (Math.random() < 0.01 || chr.getPosition().x > ball.getLeftX() && chr.getPosition().x < ball.getRightX()) && ball.isHittable() ? 10 : 0;
                                chr.getMap().broadcastMessage(MaplePacketCreator.hitSnowBall(team, damage, 0, 1));
                                if (damage != 0) break block12;
                                if (Math.random() < 0.2) {
                                    chr.getClient().getSession().write((Object)MaplePacketCreator.leftKnockBack());
                                    chr.getClient().getSession().write((Object)MaplePacketCreator.enableActions());
                                }
                                break block10;
                            }
                            ball.setPositionX(ball.getPosition() + 1);
                            if (ball.getPosition() != 255 && ball.getPosition() != 511 && ball.getPosition() != 767) break block13;
                            ball.setStartPoint(chr.getMap());
                            chr.getMap().broadcastMessage(MaplePacketCreator.rollSnowball(4, sb.getSnowBall(0), sb.getSnowBall(1)));
                            break block10;
                        }
                        if (ball.getPosition() != 899) break block14;
                        MapleMap map = chr.getMap();
                        for (int i = 0; i < 2; ++i) {
                            sb.getSnowBall(i).setInvis(true);
                            map.broadcastMessage(MaplePacketCreator.rollSnowball(i + 2, sb.getSnowBall(0), sb.getSnowBall(1)));
                        }
                        chr.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "[\u606d\u559c] " + (team == 0 ? "\u85cd\u968a" : "\u7d05\u968a") + " \u8d0f\u5f97\u52dd\u5229!"));
                        for (MapleCharacter chrz : chr.getMap().getCharactersThreadsafe()) {
                            if (team == 0 && chrz.getPosition().y > -80 || team == 1 && chrz.getPosition().y <= -80) {
                                sb.givePrize(chrz);
                            }
                            sb.warpBack(chrz);
                        }
                        sb.unreset();
                        break block10;
                    }
                    if (ball.getPosition() >= 899) break block10;
                    chr.getMap().broadcastMessage(MaplePacketCreator.rollSnowball(4, sb.getSnowBall(0), sb.getSnowBall(1)));
                    ball.setInvis(false);
                    break block10;
                }
                if (ball.getPosition() < 899) {
                    int damage = 15;
                    if (Math.random() < 0.3) {
                        damage = 0;
                    }
                    if (Math.random() < 0.05) {
                        damage = 45;
                    }
                    chr.getMap().broadcastMessage(MaplePacketCreator.hitSnowBall(team + 2, damage, 0, 0));
                    ball.setSnowmanHP(ball.getSnowmanHP() - damage);
                    if (damage > 0) {
                        chr.getMap().broadcastMessage(MaplePacketCreator.rollSnowball(0, sb.getSnowBall(0), sb.getSnowBall(1)));
                        if (ball.getSnowmanHP() <= 0) {
                            ball.setSnowmanHP(7500);
                            final MapleSnowballs oBall = sb.getSnowBall(team == 0 ? 1 : 0);
                            oBall.setHittable(false);
                            final MapleMap map = chr.getMap();
                            oBall.broadcast(map, 4);
                            oBall.snowmanSchedule = Timer.EventTimer.getInstance().schedule(new Runnable(){

                                @Override
                                public void run() {
                                    oBall.setHittable(true);
                                    oBall.broadcast(map, 5);
                                }
                            }, 10000L);
                            for (MapleCharacter chrz : chr.getMap().getCharactersThreadsafe()) {
                                if ((ball.getTeam() != 0 || chr.getPosition().y >= -80) && (ball.getTeam() != 1 || chr.getPosition().y <= -80)) continue;
                                chrz.giveDebuff(MapleDisease.SEDUCE, MobSkillFactory.getMobSkill(128, 1));
                            }
                        }
                    }
                }
            }
        }
    }
}

