/*
 * Decompiled with CFR 0.152.
 */
package client.messages.commands;

import client.MapleClient;
import client.messages.commands.CommandExecute;
import constants.GameConstants;
import constants.ServerConstants;
import handling.world.World;
import java.util.Arrays;
import scripting.NPCScriptManager;
import server.life.MapleMonster;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.StringUtil;

public class PlayerCommand {
    public static ServerConstants.PlayerGMRank getPlayerLevelRequired() {
        return ServerConstants.PlayerGMRank.NORMAL;
    }

    public static class \u5e6b\u52a9
    extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(5, "\u6307\u4ee4\u5217\u8868 :");
            c.getPlayer().dropMessage(5, "@\u67e5\u770b/@ea <\u89e3\u9664\u7570\u5e38+\u67e5\u770b\u7576\u524d\u72c0\u614b>");
            c.getPlayer().dropMessage(5, "@\u4e1f\u88dd/@DropCash <\u4e1f\u68c4\u9ede\u88dd>");
            c.getPlayer().dropMessage(5, "@\u602a\u7269/@mob <\u67e5\u770b\u8eab\u908a\u602a\u7269\u8a0a\u606f>");
            c.getPlayer().dropMessage(5, "@CGM \u8a0a\u606f <\u50b3\u9001\u8a0a\u606f\u7d66GM>");
            return 1;
        }
    }

    public static class help
    extends \u5e6b\u52a9 {
    }

    public static class CGM
    extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted[1].length() == 0) {
                c.getPlayer().dropMessage(6, "\u8acb\u8f38\u5165\u8a0a\u606f.");
                return 1;
            }
            if (c.getPlayer().isGM()) {
                c.getPlayer().dropMessage(6, "\u56e0\u70ba\u4f60\u81ea\u5df1\u662fGM\u6240\u6cd5\u4f7f\u7528\u6b64\u6307\u4ee4,\u53ef\u4ee5\u5617\u8a66!cngm <\u8a0a\u606f> \u4f86\u5efa\u7acbGM\u804a\u5929\u983b\u9053~");
                return 1;
            }
            if (!c.getPlayer().getCheatTracker().GMSpam(100000, 1)) {
                World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "\u983b\u9053 " + c.getPlayer().getClient().getChannel() + " \u73a9\u5bb6 [" + c.getPlayer().getName() + "] : " + StringUtil.joinStringFrom(splitted, 1)).getBytes());
                c.getPlayer().dropMessage(6, "\u8a0a\u606f\u5df2\u7d93\u5bc4\u9001\u7d66GM\u4e86!");
            } else {
                c.getPlayer().dropMessage(6, "\u70ba\u4e86\u9632\u6b62\u5c0dGM\u5237\u5c4f\u6240\u4ee5\u6bcf1\u5206\u9418\u53ea\u80fd\u767c\u4e00\u6b21.");
            }
            return 1;
        }
    }

    public static class \u602a\u7269
    extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMonster mob2 = null;
            for (MapleMapObject monstermo : c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), 100000.0, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob2 = (MapleMonster)monstermo;
                if (!mob2.isAlive()) continue;
                c.getPlayer().dropMessage(6, "\u602a\u7269 " + mob2.toString());
                break;
            }
            if (mob2 == null) {
                c.getPlayer().dropMessage(6, "\u627e\u4e0d\u5230\u5730\u5716\u4e0a\u7684\u602a\u7269");
            }
            return 1;
        }
    }

    public static class mob
    extends \u602a\u7269 {
    }

    public static class \u67e5\u770b
    extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().dispose(c);
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            c.getPlayer().dropMessage(1, "\u89e3\u5361\u5b8c\u7562.");
            c.getPlayer().dropMessage(6, "\u7576\u524d\u6642\u9593\u662f" + FileoutputUtil.CurrentReadable_Time() + " GMT+8 ");
            c.getPlayer().dropMessage(6, "\u7d93\u9a57\u500d\u7387 " + (long)(Math.round(c.getPlayer().getEXPMod()) * 100) * Math.round(c.getPlayer().getStat().expBuff / 100.0) + "%");
            c.getPlayer().dropMessage(6, "\u6389\u5bf6\u500d\u7387 " + (long)(Math.round(c.getPlayer().getDropMod()) * 100) * Math.round(c.getPlayer().getStat().dropBuff / 100.0) + "%");
            c.getPlayer().dropMessage(6, "\u6953\u5e63\u500d\u7387 " + Math.round(c.getPlayer().getStat().mesoBuff / 100.0) * 100L + "%");
            c.getPlayer().dropMessage(6, "\u7576\u524d\u5ef6\u9072 " + c.getPlayer().getClient().getLatency() + " \u6beb\u79d2");
            return 1;
        }
    }

    public static class ea
    extends \u67e5\u770b {
    }

    public static class \u4e1f\u88dd
    extends OpenNPCCommand {
        public \u4e1f\u88dd() {
            this.npc = 0;
        }
    }

    public static class DropCash
    extends \u4e1f\u88dd {
    }

    public static abstract class OpenNPCCommand
    extends CommandExecute {
        protected int npc = -1;
        private static int[] npcs = new int[]{9010017};

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (this.npc != 1 && c.getPlayer().getMapId() != 910000000) {
                for (int i : GameConstants.blockedMaps) {
                    if (c.getPlayer().getMapId() != i) continue;
                    c.getPlayer().dropMessage(1, "\u4f60\u4e0d\u80fd\u5728\u9019\u88e1\u4f7f\u7528\u6307\u4ee4.");
                    return 0;
                }
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(1, "\u4f60\u7684\u7b49\u7d1a\u5fc5\u9808\u662f10\u7b49.");
                    return 0;
                }
                if (c.getPlayer().getMap().getSquadByMap() != null || c.getPlayer().getEventInstance() != null || c.getPlayer().getMap().getEMByMap() != null || c.getPlayer().getMapId() >= 990000000) {
                    c.getPlayer().dropMessage(1, "\u4f60\u4e0d\u80fd\u5728\u9019\u88e1\u4f7f\u7528\u6307\u4ee4.");
                    return 0;
                }
                if (c.getPlayer().getMapId() >= 680000210 && c.getPlayer().getMapId() <= 680000502 || c.getPlayer().getMapId() / 1000 == 980000 && c.getPlayer().getMapId() != 980000000 || c.getPlayer().getMapId() / 100 == 1030008 || c.getPlayer().getMapId() / 100 == 922010 || c.getPlayer().getMapId() / 10 == 13003000) {
                    c.getPlayer().dropMessage(1, "\u4f60\u4e0d\u80fd\u5728\u9019\u88e1\u4f7f\u7528\u6307\u4ee4.");
                    return 0;
                }
            }
            NPCScriptManager.getInstance().start(c, npcs[this.npc]);
            return 1;
        }
    }
}

