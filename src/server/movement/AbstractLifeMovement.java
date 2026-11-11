/*
 * Decompiled with CFR 0.152.
 */
package server.movement;

import java.awt.Point;
import server.movement.LifeMovement;

public abstract class AbstractLifeMovement
implements LifeMovement {
    private Point position;
    private int duration;
    private int newstate;
    private final int newfh;
    private int type;

    public AbstractLifeMovement(int type, Point position, int duration, int newstate, int newfh) {
        this.type = type;
        this.position = position;
        this.duration = duration;
        this.newstate = newstate;
        this.newfh = newfh;
    }

    @Override
    public int getType() {
        return this.type;
    }

    @Override
    public int getDuration() {
        return this.duration;
    }

    @Override
    public int getNewstate() {
        return this.newstate;
    }

    @Override
    public int getNewFh() {
        return this.newfh;
    }

    @Override
    public Point getPosition() {
        return this.position;
    }
}

