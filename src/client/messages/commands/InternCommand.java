/*
 * Decompiled with CFR 0.152.
 */
package client.messages.commands;

import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import client.messages.commands.CommandExecute;
import constants.ServerConstants;
import handling.channel.ChannelServer;
import handling.world.World;
import server.maps.MapleMap;
import tools.MaplePacketCreator;
import tools.StringUtil;

public class InternCommand {
    public static ServerConstants.PlayerGMRank getPlayerLevelRequired() {
        return ServerConstants.PlayerGMRank.INTERN;
    }

    public static class UnHide
    extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dispelBuff(9001004);
            c.getPlayer().dropMessage(6, "\u7ba1\u7406\u54e1\u96b1\u85cf = \u95dc\u9589 \r\n \u958b\u555f\u8acb\u8f38\u5165!hide");
            return 1;
        }
    }

    public static class Hide
    extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            SkillFactory.getSkill(9001004).getEffect(1).applyTo(c.getPlayer());
            c.getPlayer().dropMessage(6, "\u7ba1\u7406\u54e1\u96b1\u85cf = \u958b\u555f \r\n \u89e3\u9664\u8acb\u8f38\u5165!unhide");
            return 0;
        }
    }

    public static class CnGM
    extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(5, "<GM\u804a\u5929\u8996\u7a97>\u983b\u9053" + c.getPlayer().getClient().getChannel() + " [" + c.getPlayer().getName() + "] : " + StringUtil.joinStringFrom(splitted, 1)).getBytes());
            return 1;
        }
    }

    public static class Warp
    extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null) {
                if (splitted.length == 2) {
                    c.getPlayer().changeMap(victim.getMap(), victim.getMap().findClosestSpawnpoint(victim.getPosition()));
                } else {
                    MapleMap target = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(Integer.parseInt(splitted[2]));
                    victim.changeMap(target, target.getPortal(0));
                }
            } else {
                try {
                    victim = c.getPlayer();
                    int ch = World.Find.findChannel(splitted[1]);
                    if (ch < 0) {
                        MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                        c.getPlayer().changeMap(target, target.getPortal(0));
                    } else {
                        victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted[1]);
                        c.getPlayer().dropMessage(6, "Cross changing channel. Please wait.");
                        if (victim.getMapId() != c.getPlayer().getMapId()) {
                            MapleMap mapp = c.getChannelServer().getMapFactory().getMap(victim.getMapId());
                            c.getPlayer().changeMap(mapp, mapp.getPortal(0));
                        }
                        c.getPlayer().changeChannel(ch);
                    }
                }
                catch (Exception e) {
                    c.getPlayer().dropMessage(6, "Something went wrong " + e.getMessage());
                    return 0;
                }
            }
            return 1;
        }
    }

    public static class online
    extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            int total = 0;
            int curConnected = c.getChannelServer().getConnectedClients();
            c.getPlayer().dropMessage(6, "-------------------------------------------------------------------------------------");
            c.getPlayer().dropMessage(6, "\u983b\u9053: " + c.getChannelServer().getChannel() + " \u7dda\u4e0a\u4eba\u6578: " + curConnected);
            total += curConnected;
            for (MapleCharacter chr : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                if (chr == null || c.getPlayer().getGMLevel() < chr.getGMLevel()) continue;
                StringBuilder ret = new StringBuilder();
                ret.append(" \u89d2\u8272\u66b1\u7a31 ");
                ret.append(StringUtil.getRightPaddedStr(chr.getName(), ' ', 13));
                ret.append(" ID: ");
                ret.append(chr.getId());
                ret.append(" \u7b49\u7d1a: ");
                ret.append(StringUtil.getRightPaddedStr(String.valueOf(chr.getLevel()), ' ', 3));
                ret.append(" \u8077\u696d: ");
                ret.append(chr.getJob());
                if (chr.getMap() == null) continue;
                ret.append(" \u5730\u5716: ");
                ret.append(chr.getMapId() + " - " + chr.getMap().getMapName().toString());
                c.getPlayer().dropMessage(6, ret.toString());
            }
            c.getPlayer().dropMessage(6, "\u7576\u524d\u4f3a\u670d\u5668\u7e3d\u8a08\u7dda\u4e0a\u4eba\u6578: " + total);
            c.getPlayer().dropMessage(6, "-------------------------------------------------------------------------------------");
            return 1;
        }
    }

    public static class online1
    extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(6, "\u4e0a\u7dda\u7684\u89d2\u8272 \u983b\u9053-" + c.getChannel() + ":");
            c.getPlayer().dropMessage(6, c.getChannelServer().getPlayerStorage().getOnlinePlayers(true));
            return 1;
        }
    }

    public static class spy
    extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "\u4f7f\u7528\u898f\u5247: !spy <\u73a9\u5bb6\u540d\u5b57>");
            } else {
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
                if (victim.getGMLevel() > 3) {
                    c.getPlayer().dropMessage(5, "\u4f60\u4e0d\u80fd\u67e5\u770b\u6bd4\u4f60\u9ad8\u6b0a\u9650\u7684\u4eba!");
                    return 0;
                }
                if (victim != null) {
                    c.getPlayer().dropMessage(5, "\u6b64\u73a9\u5bb6\u72c0\u614b:");
                    c.getPlayer().dropMessage(5, "\u7b49\u7d1a: " + victim.getLevel() + "\u8077\u696d: " + victim.getJob() + "\u540d\u8072: " + victim.getFame());
                    c.getPlayer().dropMessage(5, "\u5730\u5716: " + victim.getMapId() + " - " + victim.getMap().getMapName().toString());
                    c.getPlayer().dropMessage(5, "\u529b\u91cf: " + victim.getStat().getStr() + "  ||  \u654f\u6377: " + victim.getStat().getDex() + "  ||  \u667a\u529b: " + victim.getStat().getInt() + "  ||  \u5e78\u904b: " + victim.getStat().getLuk());
                    c.getPlayer().dropMessage(5, "\u64c1\u6709 " + victim.getMeso() + " \u6953\u5e63.");
                    victim.dropMessage(5, c.getPlayer().getName() + " GM\u5728\u89c0\u5bdf\u60a8..");
                } else {
                    c.getPlayer().dropMessage(5, "\u627e\u4e0d\u5230\u6b64\u73a9\u5bb6.");
                }
            }
            return 1;
        }
    }

    public static class DC
    extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter victim;
            int level = 0;
            if (splitted[1].charAt(0) == '-') {
                level = StringUtil.countCharacters(splitted[1], 'f');
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[2]);
            } else {
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            }
            if (level < 2 && victim != null) {
                victim.getClient().getSession().close();
                if (level >= 1) {
                    victim.getClient().disconnect(true, false);
                }
                return 1;
            }
            c.getPlayer().dropMessage(6, "Please use dc -f instead, or the victim does not exist.");
            return 0;
        }
    }

    public static class UnBan
    extends CommandExecute {
        protected boolean hellban = false;

        private String getCommand() {
            return "UnBan";
        }

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "[Syntax] !" + this.getCommand() + " <\u539f\u56e0>");
                return 0;
            }
            byte ret = this.hellban ? MapleClient.unHellban(splitted[1]) : MapleClient.unban(splitted[1]);
            if (ret == -2) {
                c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] SQL error.");
                return 0;
            }
            if (ret == -1) {
                c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] The character does not exist.");
                return 0;
            }
            c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] Successfully unbanned!");
            byte ret_ = MapleClient.unbanIPMacs(splitted[1]);
            if (ret_ == -2) {
                c.getPlayer().dropMessage(6, "[UnbanIP] SQL error.");
            } else if (ret_ == -1) {
                c.getPlayer().dropMessage(6, "[UnbanIP] The character does not exist.");
            } else if (ret_ == 0) {
                c.getPlayer().dropMessage(6, "[UnbanIP] No IP or Mac with that character exists!");
            } else if (ret_ == 1) {
                c.getPlayer().dropMessage(6, "[UnbanIP] IP/Mac -- one of them was found and unbanned.");
            } else if (ret_ == 2) {
                c.getPlayer().dropMessage(6, "[UnbanIP] Both IP and Macs were unbanned.");
            }
            return ret_ > 0 ? 1 : 0;
        }
    }

    public static class Ban
    extends CommandExecute {
        protected boolean hellban = false;

        private String getCommand() {
            return "Ban";
        }

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(5, "[Syntax] !" + this.getCommand() + " <\u73a9\u5bb6> <\u539f\u56e0>");
                return 0;
            }
            StringBuilder sb = new StringBuilder(c.getPlayer().getName());
            sb.append(" banned ").append(splitted[1]).append(": ").append(StringUtil.joinStringFrom(splitted, 2));
            MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (target != null) {
                if (c.getPlayer().getGMLevel() > target.getGMLevel() || c.getPlayer().isAdmin()) {
                    sb.append(" (IP: ").append(target.getClient().getSessionIPAddress()).append(")");
                    if (target.ban(sb.toString(), c.getPlayer().isAdmin(), false, this.hellban)) {
                        c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] \u6210\u529f\u5c01\u9396 " + splitted[1] + ".");
                        return 1;
                    }
                    c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] \u5c01\u9396\u5931\u6557.");
                    return 0;
                }
                c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] May not ban GMs...");
                return 1;
            }
            if (MapleCharacter.ban(splitted[1], sb.toString(), false, c.getPlayer().isAdmin() ? 250 : c.getPlayer().getGMLevel(), splitted[0].equals("!hellban"))) {
                c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] \u6210\u529f\u96e2\u7dda\u5c01\u9396 " + splitted[1] + ".");
                return 1;
            }
            c.getPlayer().dropMessage(6, "[" + this.getCommand() + "] \u5c01\u9396\u5931\u6557 " + splitted[1]);
            return 0;
        }
    }
}

