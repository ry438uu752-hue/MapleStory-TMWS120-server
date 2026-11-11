/*
 * Decompiled with CFR 0.152.
 */
package server.movement;

import java.awt.Point;
import server.movement.AbstractLifeMovement;
import tools.data.output.MaplePacketLittleEndianWriter;

public class StaticLifeMovement
extends AbstractLifeMovement {
    private Point pixelsPerSecond;
    private Point offset;
    private short unk;
    private short fh;
    private int wui;

    public StaticLifeMovement(int type, Point position, int duration, int newstate, int newfh) {
        super(type, position, duration, newstate, newfh);
    }

    public void setPixelsPerSecond(Point wobble) {
        this.pixelsPerSecond = wobble;
    }

    public void setOffset(Point wobble) {
        this.offset = wobble;
    }

    public void setFh(short fh) {
        this.fh = fh;
    }

    public void setUnk(short unk) {
        this.unk = unk;
    }

    public short getUnk() {
        return this.unk;
    }

    public void setWui(int wui) {
        this.wui = wui;
    }

    public void defaulted() {
        this.unk = 0;
        this.fh = 0;
        this.pixelsPerSecond = new Point(0, 0);
        this.offset = new Point(0, 0);
        this.wui = 0;
    }

    @Override
    public void serialize(MaplePacketLittleEndianWriter lew) {
        lew.write(this.getType());
        switch (this.getType()) {
            case 0: 
            case 5: 
            case 12: 
            case 14: 
            case 35: 
            case 36: {
                lew.writePos(this.getPosition());
                lew.writePos(this.pixelsPerSecond);
                lew.writeShort(this.unk);
                if (this.getType() == 12) {
                    lew.writeShort(this.fh);
                }
                lew.writePos(this.offset);
                break;
            }
            case 1: 
            case 2: 
            case 13: 
            case 16: 
            case 18: 
            case 31: 
            case 32: 
            case 33: 
            case 34: {
                lew.writePos(this.pixelsPerSecond);
                break;
            }
            case 3: 
            case 4: 
            case 6: 
            case 7: 
            case 8: 
            case 10: {
                lew.writePos(this.getPosition());
                lew.writeShort(this.fh);
                break;
            }
            case 11: {
                lew.writePos(this.getPosition());
                lew.writeShort(this.unk);
                break;
            }
            case 17: {
                lew.writePos(this.getPosition());
                lew.writeShort(this.unk);
                lew.writeShort(this.fh);
            }
        }
        if (this.getType() != 9) {
            lew.write(this.getNewstate());
            lew.writeShort(this.getDuration());
        } else {
            lew.write(this.wui);
        }
    }
}

