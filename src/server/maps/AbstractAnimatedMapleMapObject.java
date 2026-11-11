/*
 * Decompiled with CFR 0.152.
 */
package server.maps;

import server.maps.AbstractMapleMapObject;
import server.maps.AnimatedMapleMapObject;

public abstract class AbstractAnimatedMapleMapObject
extends AbstractMapleMapObject
implements AnimatedMapleMapObject {
    private int stance;

    @Override
    public int getStance() {
        return this.stance;
    }

    @Override
    public void setStance(int stance) {
        this.stance = stance;
    }

    @Override
    public boolean isFacingLeft() {
        return this.getStance() % 2 != 0;
    }

    public int getFacingDirection() {
        return this.getStance() % 2;
    }
}

