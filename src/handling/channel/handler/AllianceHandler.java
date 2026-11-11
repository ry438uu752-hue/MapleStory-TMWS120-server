/*
 * Decompiled with CFR 0.152.
 */
package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import handling.MaplePacket;
import handling.world.World;
import handling.world.guild.MapleGuild;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

public class AllianceHandler {
    public static final void HandleAlliance(SeekableLittleEndianAccessor slea, MapleClient c, boolean denied) {
        if (c.getPlayer().getGuildId() <= 0) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        MapleGuild gs = World.Guild.getGuild(c.getPlayer().getGuildId());
        if (gs == null) {
            c.getSession().write((Object)MaplePacketCreator.enableActions());
            return;
        }
        byte op = slea.readByte();
        if (c.getPlayer().getGuildRank() != 1 && op != 1) {
            return;
        }
        if (op == 22) {
            denied = true;
        }
        int leaderid = 0;
        if (gs.getAllianceId() > 0) {
            leaderid = World.Alliance.getAllianceLeader(gs.getAllianceId());
        }
        if (op != 4 && !denied ? gs.getAllianceId() <= 0 || leaderid <= 0 : leaderid > 0 || gs.getAllianceId() > 0) {
            return;
        }
        if (denied) {
            AllianceHandler.DenyInvite(c, gs);
            return;
        }
        switch (op) {
            case 1: {
                for (MaplePacket pack : World.Alliance.getAllianceInfo(gs.getAllianceId(), false)) {
                    if (pack == null) continue;
                    c.getSession().write((Object)pack);
                }
                break;
            }
            case 3: {
                MapleCharacter chr;
                int newGuild = World.Guild.getGuildLeader(slea.readMapleAsciiString());
                if (newGuild <= 0 || c.getPlayer().getAllianceRank() != 1 || leaderid != c.getPlayer().getId() || (chr = c.getChannelServer().getPlayerStorage().getCharacterById(newGuild)) == null || chr.getGuildId() <= 0 || !World.Alliance.canInvite(gs.getAllianceId())) break;
                chr.getClient().getSession().write((Object)MaplePacketCreator.sendAllianceInvite(World.Alliance.getAlliance(gs.getAllianceId()).getName(), c.getPlayer()));
                World.Guild.setInvitedId(chr.getGuildId(), gs.getAllianceId());
                break;
            }
            case 4: {
                int inviteid = World.Guild.getInvitedId(c.getPlayer().getGuildId());
                if (inviteid <= 0) break;
                if (!World.Alliance.addGuildToAlliance(inviteid, c.getPlayer().getGuildId())) {
                    c.getPlayer().dropMessage(5, "An error occured when adding guild.");
                }
                World.Guild.setInvitedId(c.getPlayer().getGuildId(), 0);
                break;
            }
            case 2: 
            case 6: {
                int gid;
                if (op == 6 && slea.available() >= 4L) {
                    gid = slea.readInt();
                    if (slea.available() >= 4L && gs.getAllianceId() != slea.readInt()) {
                        break;
                    }
                } else {
                    gid = c.getPlayer().getGuildId();
                }
                if (c.getPlayer().getAllianceRank() > 2 || c.getPlayer().getAllianceRank() != 1 && c.getPlayer().getGuildId() != gid || World.Alliance.removeGuildFromAlliance(gs.getAllianceId(), gid, c.getPlayer().getGuildId() != gid)) break;
                c.getPlayer().dropMessage(5, "An error occured when removing guild.");
                break;
            }
            case 7: {
                if (c.getPlayer().getAllianceRank() != 1 || leaderid != c.getPlayer().getId() || World.Alliance.changeAllianceLeader(gs.getAllianceId(), slea.readInt())) break;
                c.getPlayer().dropMessage(5, "An error occured when changing leader.");
                break;
            }
            case 8: {
                if (c.getPlayer().getAllianceRank() != 1 || leaderid != c.getPlayer().getId()) break;
                String[] ranks = new String[5];
                for (int i = 0; i < 5; ++i) {
                    ranks[i] = slea.readMapleAsciiString();
                }
                World.Alliance.updateAllianceRanks(gs.getAllianceId(), ranks);
                break;
            }
            case 9: {
                if (c.getPlayer().getAllianceRank() > 2 || World.Alliance.changeAllianceRank(gs.getAllianceId(), slea.readInt(), slea.readByte())) break;
                c.getPlayer().dropMessage(5, "An error occured when changing rank.");
                break;
            }
            case 10: {
                String notice;
                if (c.getPlayer().getAllianceRank() > 2 || (notice = slea.readMapleAsciiString()).length() > 100) break;
                World.Alliance.updateAllianceNotice(gs.getAllianceId(), notice);
                break;
            }
            default: {
                System.out.println("Unhandled GuildAlliance op: " + op + ", \n" + slea.toString());
            }
        }
    }

    public static final void DenyInvite(MapleClient c, MapleGuild gs) {
        int newAlliance;
        int inviteid = World.Guild.getInvitedId(c.getPlayer().getGuildId());
        if (inviteid > 0 && (newAlliance = World.Alliance.getAllianceLeader(inviteid)) > 0) {
            MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterById(newAlliance);
            if (chr != null) {
                chr.dropMessage(5, gs.getName() + " Guild has rejected the Guild Union invitation.");
            }
            World.Guild.setInvitedId(c.getPlayer().getGuildId(), 0);
        }
    }
}

