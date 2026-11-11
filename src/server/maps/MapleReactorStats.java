/*
 * Decompiled with CFR 0.152.
 */
package server.maps;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import tools.Pair;

public class MapleReactorStats {
    private byte facingDirection;
    private Point tl;
    private Point br;
    private Map<Byte, StateData> stateInfo = new HashMap<Byte, StateData>();

    public final void setFacingDirection(byte facingDirection) {
        this.facingDirection = facingDirection;
    }

    public final byte getFacingDirection() {
        return this.facingDirection;
    }

    public void setTL(Point tl) {
        this.tl = tl;
    }

    public void setBR(Point br) {
        this.br = br;
    }

    public Point getTL() {
        return this.tl;
    }

    public Point getBR() {
        return this.br;
    }

    public void addState(byte state, int type, Pair<Integer, Integer> reactItem, byte nextState, int timeOut) {
        StateData newState = new StateData(type, reactItem, nextState, timeOut);
        this.stateInfo.put(state, newState);
    }

    public byte getNextState(byte state) {
        StateData nextState = this.stateInfo.get(state);
        if (nextState != null) {
            return nextState.getNextState();
        }
        return -1;
    }

    public int getType(byte state) {
        StateData nextState = this.stateInfo.get(state);
        if (nextState != null) {
            return nextState.getType();
        }
        return -1;
    }

    public Pair<Integer, Integer> getReactItem(byte state) {
        StateData nextState = this.stateInfo.get(state);
        if (nextState != null) {
            return nextState.getReactItem();
        }
        return null;
    }

    public int getTimeOut(byte state) {
        StateData nextState = this.stateInfo.get(state);
        if (nextState != null) {
            return nextState.getTimeOut();
        }
        return -1;
    }

    private static class StateData {
        private int type;
        private int timeOut;
        private Pair<Integer, Integer> reactItem;
        private byte nextState;

        private StateData(int type, Pair<Integer, Integer> reactItem, byte nextState, int timeOut) {
            this.type = type;
            this.reactItem = reactItem;
            this.nextState = nextState;
            this.timeOut = timeOut;
        }

        private int getType() {
            return this.type;
        }

        private byte getNextState() {
            return this.nextState;
        }

        private Pair<Integer, Integer> getReactItem() {
            return this.reactItem;
        }

        private int getTimeOut() {
            return this.timeOut;
        }
    }
}

