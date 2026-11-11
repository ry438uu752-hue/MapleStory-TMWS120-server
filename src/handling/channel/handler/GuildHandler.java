/*
 * Decompiled with CFR 0.152.
 */
package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import handling.MaplePacket;
import handling.world.World;
import handling.world.guild.MapleGuild;
import handling.world.guild.MapleGuildResponse;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

public class GuildHandler {
    private static final List<Invited> invited = new LinkedList<Invited>();
    private static long nextPruneTime = System.currentTimeMillis() + 1200000L;

    public static final void DenyGuildRequest(String from, MapleClient c) {
        MapleCharacter cfrom = c.getChannelServer().getPlayerStorage().getCharacterByName(from);
        if (cfrom != null) {
            cfrom.getClient().getSession().write((Object)MaplePacketCreator.denyGuildInvitation(c.getPlayer().getName()));
        }
    }

    private static final boolean isGuildNameAcceptable(String name) {
        if (name.length() > 15) {
            return false;
        }
        return name.length() >= 3;
    }

    private static final void respawnPlayer(MapleCharacter mc) {
        mc.getMap().broadcastMessage(mc, MaplePacketCreator.removePlayerFromMap(mc.getId()), false);
        mc.getMap().broadcastMessage(mc, MaplePacketCreator.spawnPlayerMapobject(mc), false);
    }

    public static final void Guild(SeekableLittleEndianAccessor slea, MapleClient c) {
        if (System.currentTimeMillis() >= nextPruneTime) {
            Iterator<Invited> itr = invited.iterator();
            while (itr.hasNext()) {
                Invited inv = itr.next();
                if (System.currentTimeMillis() < inv.expiration) continue;
                itr.remove();
            }
            nextPruneTime = System.currentTimeMillis() + 1200000L;
        }
        block0 : switch (slea.readByte()) {
            case 2: {
                if (c.getPlayer().getGuildId() > 0 || c.getPlayer().getMapId() != 200000301) {
                    c.getPlayer().dropMessage(1, "\u4f60\u4e0d\u80fd\u5728\u5275\u5efa\u4e00\u500b\u65b0\u7684\u516c\u6703.");
                    return;
                }
                if (c.getPlayer().getMeso() < 500000) {
                    c.getPlayer().dropMessage(1, "\u4f60\u7684\u6953\u5e63\u4e0d\u5920,\u7121\u6cd5\u5275\u5efa\u516c\u6703");
                    return;
                }
                String guildName = slea.readMapleAsciiString();
                if (!GuildHandler.isGuildNameAcceptable(guildName)) {
                    c.getPlayer().dropMessage(1, "\u9019\u500b\u516c\u6703\u540d\u7a31\u662f\u4e0d\u88ab\u51c6\u8a31\u7684.");
                    return;
                }
                int guildId = World.Guild.createGuild(c.getPlayer().getId(), guildName);
                if (guildId == 0) {
                    c.getSession().write((Object)MaplePacketCreator.genericGuildMessage((byte)28));
                    return;
                }
                c.getPlayer().gainMeso(-500000, true, false, true);
                c.getPlayer().setGuildId(guildId);
                c.getPlayer().setGuildRank((byte)1);
                c.getPlayer().saveGuildStatus();
                c.getSession().write((Object)MaplePacketCreator.showGuildInfo(c.getPlayer()));
                World.Guild.setGuildMemberOnline(c.getPlayer().getMGC(), true, c.getChannel());
                c.getPlayer().dropMessage(1, "\u606d\u559c\u4f60\u6210\u529f\u5275\u5efa\u4e00\u500b\u516c\u6703.");
                GuildHandler.respawnPlayer(c.getPlayer());
                break;
            }
            case 5: {
                if (c.getPlayer().getGuildId() <= 0 || c.getPlayer().getGuildRank() > 2) {
                    return;
                }
                String name = slea.readMapleAsciiString();
                MapleGuildResponse mgr = MapleGuild.sendInvite(c, name);
                if (mgr != null) {
                    c.getSession().write((Object)mgr.getPacket());
                    break;
                }
                Invited inv = new Invited(name, c.getPlayer().getGuildId());
                if (invited.contains(inv)) break;
                invited.add(inv);
                break;
            }
            case 6: {
                if (c.getPlayer().getGuildId() > 0) {
                    return;
                }
                int guildId = slea.readInt();
                int cid = slea.readInt();
                if (cid != c.getPlayer().getId()) {
                    return;
                }
                String name = c.getPlayer().getName().toLowerCase();
                Iterator<Invited> itr = invited.iterator();
                while (itr.hasNext()) {
                    Invited inv = itr.next();
                    if (guildId != inv.gid || !name.equals(inv.name)) continue;
                    c.getPlayer().setGuildId(guildId);
                    c.getPlayer().setGuildRank((byte)5);
                    itr.remove();
                    int s = World.Guild.addGuildMember(c.getPlayer().getMGC());
                    if (s == 0) {
                        c.getPlayer().dropMessage(1, "\u4f60\u60f3\u8981\u52a0\u5165\u7684\u516c\u6703\u5df2\u7d93\u6eff\u4e86.");
                        c.getPlayer().setGuildId(0);
                        return;
                    }
                    c.getSession().write((Object)MaplePacketCreator.showGuildInfo(c.getPlayer()));
                    MapleGuild gs = World.Guild.getGuild(guildId);
                    for (MaplePacket pack : World.Alliance.getAllianceInfo(gs.getAllianceId(), true)) {
                        if (pack == null) continue;
                        c.getSession().write((Object)pack);
                    }
                    c.getPlayer().saveGuildStatus();
                    GuildHandler.respawnPlayer(c.getPlayer());
                    break block0;
                }
                break;
            }
            case 7: {
                int cid = slea.readInt();
                String name = slea.readMapleAsciiString();
                if (cid != c.getPlayer().getId() || !name.equals(c.getPlayer().getName()) || c.getPlayer().getGuildId() <= 0) {
                    return;
                }
                World.Guild.leaveGuild(c.getPlayer().getMGC());
                c.getSession().write((Object)MaplePacketCreator.showGuildInfo(null));
                c.getSession().write((Object)MaplePacketCreator.fuckGuildInfo(c.getPlayer()));
                break;
            }
            case 8: {
                int cid = slea.readInt();
                String name = slea.readMapleAsciiString();
                if (c.getPlayer().getGuildRank() > 2 || c.getPlayer().getGuildId() <= 0) {
                    return;
                }
                World.Guild.expelMember(c.getPlayer().getMGC(), name, cid);
                break;
            }
            case 13: {
                if (c.getPlayer().getGuildId() <= 0 || c.getPlayer().getGuildRank() != 1) {
                    return;
                }
                String[] ranks = new String[5];
                for (int i = 0; i < 5; ++i) {
                    ranks[i] = slea.readMapleAsciiString();
                }
                World.Guild.changeRankTitle(c.getPlayer().getGuildId(), ranks);
                break;
            }
            case 14: {
                int cid = slea.readInt();
                byte newRank = slea.readByte();
                if (newRank <= 1 || newRank > 5 || c.getPlayer().getGuildRank() > 2 || newRank <= 2 && c.getPlayer().getGuildRank() != 1 || c.getPlayer().getGuildId() <= 0) {
                    return;
                }
                World.Guild.changeRank(c.getPlayer().getGuildId(), cid, newRank);
                break;
            }
            case 15: {
                if (c.getPlayer().getGuildId() <= 0 || c.getPlayer().getGuildRank() != 1 || c.getPlayer().getMapId() != 200000301) {
                    return;
                }
                if (c.getPlayer().getMeso() < 1000000) {
                    c.getPlayer().dropMessage(1, "\u4f60\u7684\u6953\u5e63\u4e0d\u5920,\u7121\u6cd5\u5275\u5efa\u516c\u6703\u5fbd\u7ae0");
                    return;
                }
                short bg = slea.readShort();
                byte bgcolor = slea.readByte();
                short logo = slea.readShort();
                byte logocolor = slea.readByte();
                World.Guild.setGuildEmblem(c.getPlayer().getGuildId(), bg, bgcolor, logo, logocolor);
                c.getPlayer().gainMeso(-1000000, true, false, true);
                GuildHandler.respawnPlayer(c.getPlayer());
                break;
            }
            case 16: {
                String notice = slea.readMapleAsciiString();
                if (notice.length() > 100 || c.getPlayer().getGuildId() <= 0 || c.getPlayer().getGuildRank() > 2) {
                    return;
                }
                World.Guild.setGuildNotice(c.getPlayer().getGuildId(), notice);
            }
        }
    }

    private static final class Invited {
        public String name;
        public int gid;
        public long expiration;

        public Invited(String n, int id) {
            this.name = n.toLowerCase();
            this.gid = id;
            this.expiration = System.currentTimeMillis() + 3600000L;
        }

        public final boolean equals(Object other) {
            if (!(other instanceof Invited)) {
                return false;
            }
            Invited oth = (Invited)other;
            return this.gid == oth.gid && this.name.equals(oth.name);
        }
    }
}

