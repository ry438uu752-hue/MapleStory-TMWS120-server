/*
 * Decompiled with CFR 0.152.
 */
package handling.channel.handler;

import client.MapleClient;
import handling.world.World;
import handling.world.guild.MapleBBSThread;
import java.util.List;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

public class BBSHandler {
    private static final String correctLength(String in, int maxSize) {
        if (in.length() > maxSize) {
            return in.substring(0, maxSize);
        }
        return in;
    }

    public static final void BBSOperatopn(SeekableLittleEndianAccessor slea, MapleClient c) {
        if (c.getPlayer().getGuildId() <= 0) {
            return;
        }
        int localthreadid = 0;
        byte action = slea.readByte();
        switch (action) {
            case 0: {
                boolean bEdit;
                boolean bl = bEdit = slea.readByte() > 0;
                if (bEdit) {
                    localthreadid = slea.readInt();
                }
                boolean bNotice = slea.readByte() > 0;
                String title = BBSHandler.correctLength(slea.readMapleAsciiString(), 25);
                String text = BBSHandler.correctLength(slea.readMapleAsciiString(), 600);
                int icon = slea.readInt();
                if (icon >= 100 && icon <= 106 ? !c.getPlayer().haveItem(5290000 + icon - 100, 1, false, true) : icon < 0 || icon > 2) {
                    return;
                }
                if (!bEdit) {
                    BBSHandler.newBBSThread(c, title, text, icon, bNotice);
                    break;
                }
                BBSHandler.editBBSThread(c, title, text, icon, localthreadid);
                break;
            }
            case 1: {
                localthreadid = slea.readInt();
                BBSHandler.deleteBBSThread(c, localthreadid);
                break;
            }
            case 2: {
                int start = slea.readInt();
                BBSHandler.listBBSThreads(c, start * 10);
                break;
            }
            case 3: {
                localthreadid = slea.readInt();
                BBSHandler.displayThread(c, localthreadid);
                break;
            }
            case 4: {
                localthreadid = slea.readInt();
                String text = BBSHandler.correctLength(slea.readMapleAsciiString(), 25);
                BBSHandler.newBBSReply(c, localthreadid, text);
                break;
            }
            case 5: {
                localthreadid = slea.readInt();
                int replyid = slea.readInt();
                BBSHandler.deleteBBSReply(c, localthreadid, replyid);
            }
        }
    }

    private static void listBBSThreads(MapleClient c, int start) {
        if (c.getPlayer().getGuildId() <= 0) {
            return;
        }
        c.getSession().write((Object)MaplePacketCreator.BBSThreadList(World.Guild.getBBS(c.getPlayer().getGuildId()), start));
    }

    private static final void newBBSReply(MapleClient c, int localthreadid, String text) {
        if (c.getPlayer().getGuildId() <= 0) {
            return;
        }
        World.Guild.addBBSReply(c.getPlayer().getGuildId(), localthreadid, text, c.getPlayer().getId());
        BBSHandler.displayThread(c, localthreadid);
    }

    private static final void editBBSThread(MapleClient c, String title, String text, int icon, int localthreadid) {
        if (c.getPlayer().getGuildId() <= 0) {
            return;
        }
        World.Guild.editBBSThread(c.getPlayer().getGuildId(), localthreadid, title, text, icon, c.getPlayer().getId(), c.getPlayer().getGuildRank());
        BBSHandler.displayThread(c, localthreadid);
    }

    private static final void newBBSThread(MapleClient c, String title, String text, int icon, boolean bNotice) {
        if (c.getPlayer().getGuildId() <= 0) {
            return;
        }
        BBSHandler.displayThread(c, World.Guild.addBBSThread(c.getPlayer().getGuildId(), title, text, icon, bNotice, c.getPlayer().getId()));
    }

    private static final void deleteBBSThread(MapleClient c, int localthreadid) {
        if (c.getPlayer().getGuildId() <= 0) {
            return;
        }
        World.Guild.deleteBBSThread(c.getPlayer().getGuildId(), localthreadid, c.getPlayer().getId(), c.getPlayer().getGuildRank());
    }

    private static final void deleteBBSReply(MapleClient c, int localthreadid, int replyid) {
        if (c.getPlayer().getGuildId() <= 0) {
            return;
        }
        World.Guild.deleteBBSReply(c.getPlayer().getGuildId(), localthreadid, replyid, c.getPlayer().getId(), c.getPlayer().getGuildRank());
        BBSHandler.displayThread(c, localthreadid);
    }

    private static final void displayThread(MapleClient c, int localthreadid) {
        if (c.getPlayer().getGuildId() <= 0) {
            return;
        }
        List<MapleBBSThread> bbsList = World.Guild.getBBS(c.getPlayer().getGuildId());
        if (bbsList != null) {
            for (MapleBBSThread t : bbsList) {
                if (t == null || t.localthreadID != localthreadid) continue;
                c.getSession().write((Object)MaplePacketCreator.showThread(t));
            }
        }
    }
}

