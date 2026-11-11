/*
 * Decompiled with CFR 0.152.
 */
package handling.channel.handler;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import server.maps.AnimatedMapleMapObject;
import server.movement.LifeMovement;
import server.movement.LifeMovementFragment;
import server.movement.StaticLifeMovement;
import tools.data.input.SeekableLittleEndianAccessor;

public class MovementParse {
    public static final List<LifeMovementFragment> parseMovement(SeekableLittleEndianAccessor lea, int kind) {
        ArrayList<LifeMovementFragment> res = new ArrayList<LifeMovementFragment>();
        byte numCommands = lea.readByte();
        block8: for (byte i = 0; i < numCommands; i = (byte)(i + 1)) {
            byte command = lea.readByte();
            switch (command) {
                case 0: 
                case 5: 
                case 12: 
                case 14: 
                case 35: 
                case 36: {
                    short xpos = lea.readShort();
                    short ypos = lea.readShort();
                    short xwobble = lea.readShort();
                    short ywobble = lea.readShort();
                    short unk = lea.readShort();
                    short fh = 0;
                    if (command == 12) {
                        fh = lea.readShort();
                    }
                    short xoffset = lea.readShort();
                    short yoffset = lea.readShort();
                    byte newstate = lea.readByte();
                    short duration = lea.readShort();
                    StaticLifeMovement mov = new StaticLifeMovement(command, new Point(xpos, ypos), duration, newstate, unk);
                    mov.setUnk(unk);
                    mov.setFh(fh);
                    mov.setPixelsPerSecond(new Point(xwobble, ywobble));
                    mov.setOffset(new Point(xoffset, yoffset));
                    res.add(mov);
                    continue block8;
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
                    short xwobble = lea.readShort();
                    short ywobble = lea.readShort();
                    byte newstate = lea.readByte();
                    short duration = lea.readShort();
                    StaticLifeMovement mov = new StaticLifeMovement(command, new Point(xwobble, ywobble), duration, newstate, 0);
                    mov.setPixelsPerSecond(new Point(xwobble, ywobble));
                    res.add(mov);
                    continue block8;
                }
                case 3: 
                case 4: 
                case 6: 
                case 7: 
                case 8: 
                case 10: {
                    short xpos = lea.readShort();
                    short ypos = lea.readShort();
                    short fh = lea.readShort();
                    byte newstate = lea.readByte();
                    short duration = lea.readShort();
                    StaticLifeMovement mov = new StaticLifeMovement(command, new Point(xpos, ypos), duration, newstate, 0);
                    mov.setFh(fh);
                    res.add(mov);
                    continue block8;
                }
                case 11: {
                    short xpos = lea.readShort();
                    short ypos = lea.readShort();
                    short unk = lea.readShort();
                    byte newstate = lea.readByte();
                    short duration = lea.readShort();
                    StaticLifeMovement mov = new StaticLifeMovement(command, new Point(xpos, ypos), duration, newstate, 0);
                    mov.setUnk(unk);
                    res.add(mov);
                    continue block8;
                }
                case 17: {
                    short xpos = lea.readShort();
                    short ypos = lea.readShort();
                    short unk = lea.readShort();
                    short fh = lea.readShort();
                    byte newstate = lea.readByte();
                    short duration = lea.readShort();
                    StaticLifeMovement mov = new StaticLifeMovement(command, new Point(xpos, ypos), duration, newstate, 0);
                    mov.setUnk(unk);
                    mov.setFh(fh);
                    res.add(mov);
                    continue block8;
                }
                case 9: {
                    byte wui = lea.readByte();
                    StaticLifeMovement mov = new StaticLifeMovement(command, null, 0, 0, 0);
                    mov.setWui(wui);
                    res.add(mov);
                    continue block8;
                }
                default: {
                    byte newstate = lea.readByte();
                    short duration = lea.readShort();
                    StaticLifeMovement mov = new StaticLifeMovement(command, null, duration, newstate, 0);
                    res.add(mov);
                    continue block8;
                }
            }
        }
        if (numCommands != res.size()) {
            System.out.println("error in movement");
            return null;
        }
        return res;
    }

    public static final void updatePosition(List<LifeMovementFragment> movement, AnimatedMapleMapObject target, int yoffset) {
        for (LifeMovementFragment move : movement) {
            Point position;
            if (!(move instanceof LifeMovement)) continue;
            if (move instanceof StaticLifeMovement && (position = ((StaticLifeMovement)move).getPosition()) != null) {
                position.y += yoffset;
                target.setPosition(position);
            }
            target.setStance(((StaticLifeMovement)move).getNewstate());
        }
    }
}

