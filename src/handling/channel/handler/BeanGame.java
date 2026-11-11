/*
 * Decompiled with CFR 0.152.
 */
package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import java.util.ArrayList;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

public class BeanGame {
    public static final void BeanGame1(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        ArrayList beansInfo = new ArrayList();
        byte type = slea.readByte();
        short \u529b\u5ea6 = 0;
        int \u8c46\u8c46\u5e8f\u865f = 0;
        if (type == 1) {
            \u529b\u5ea6 = slea.readShort();
            chr.setBeansRange(\u529b\u5ea6);
        } else if (type == 2) {
            if (type == 11 || type == 0) {
                \u529b\u5ea6 = slea.readShort();
                \u8c46\u8c46\u5e8f\u865f = slea.readInt() + 1;
                chr.setBeansRange(\u529b\u5ea6);
                chr.setBeansNum(\u8c46\u8c46\u5e8f\u865f);
                if (\u8c46\u8c46\u5e8f\u865f == 1) {
                    chr.setCanSetBeansNum(false);
                }
            }
        } else if (type == 6) {
            slea.skip(1);
            byte \u5faa\u74b0\u6b21\u6578 = slea.readByte();
            if (\u5faa\u74b0\u6b21\u6578 == 0) {
                return;
            }
            if (\u5faa\u74b0\u6b21\u6578 != 1) {
                slea.skip((\u5faa\u74b0\u6b21\u6578 - 1) * 8);
            }
            if (chr.isCanSetBeansNum()) {
                chr.setBeansNum(chr.getBeansNum() + \u5faa\u74b0\u6b21\u6578);
            }
            chr.gainBeans(-\u5faa\u74b0\u6b21\u6578);
            chr.setCanSetBeansNum(true);
        } else {
            System.out.println("\u672a\u8655\u7406\u7684\u985e\u578b\u3010" + type + "\u3011\n\u5305" + slea.toString());
        }
        if (type == 11 || type == 6) {
            byte size = slea.readByte();
            short Pos2 = slea.readShort();
            byte Type2 = slea.readByte();
            c.getSession().write((Object)MaplePacketCreator.showBeans(size, Pos2, Type2));
        }
    }

    private static int getBeanType() {
        int random = BeanGame.rand(1, 100);
        int beanType = 0;
        switch (random) {
            case 2: {
                beanType = 1;
                break;
            }
            case 49: {
                beanType = 2;
                break;
            }
            case 99: {
                beanType = 3;
            }
        }
        return beanType;
    }

    private static int rand(int lbound, int ubound) {
        return (int)(Math.random() * (double)(ubound - lbound + 1) + (double)lbound);
    }

    public static final void BeanGame2(SeekableLittleEndianAccessor slea, MapleClient c) {
        c.getSession().write((Object)MaplePacketCreator.updateBeans(c.getPlayer().getId(), c.getPlayer().getBeans()));
        c.getSession().write((Object)MaplePacketCreator.enableActions());
    }

    public class Beans {
        private int number;
        private int type;
        private int pos;

        public Beans(int pos, int type, int number) {
            this.pos = pos;
            this.number = number;
            this.type = type;
        }

        public int getType() {
            return this.type;
        }

        public int getNumber() {
            return this.number;
        }

        public int getPos() {
            return this.pos;
        }
    }
}

