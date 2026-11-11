/*
 * Decompiled with CFR 0.152.
 */
package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.CommandProcessor;
import constants.ServerConstants;
import handling.channel.ChannelServer;
import handling.world.MapleMessenger;
import handling.world.MapleMessengerCharacter;
import handling.world.World;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

public class ChatHandler {
    public static final void GeneralChat(String text, byte unk, MapleClient c, MapleCharacter chr) {
        if (chr != null && !CommandProcessor.processCommand(c, text, ServerConstants.CommandType.NORMAL)) {
            if (!chr.isGM() && text.length() >= 80) {
                return;
            }
            if (chr.getCanTalk() || chr.isStaff()) {
                if (chr.isHidden()) {
                    chr.getMap().broadcastGMMessage(chr, MaplePacketCreator.getChatText(chr.getId(), text, c.getPlayer().isGM(), unk), true);
                } else {
                    chr.getCheatTracker().checkMsg();
                    chr.getMap().broadcastMessage(MaplePacketCreator.getChatText(chr.getId(), text, c.getPlayer().isGM(), unk), c.getPlayer().getPosition());
                }
            } else {
                c.getSession().write((Object)MaplePacketCreator.serverNotice(6, "You have been muted and are therefore unable to talk."));
            }
        }
    }

    public static final void Others(SeekableLittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        byte type = slea.readByte();
        byte numRecipients = slea.readByte();
        int[] recipients = new int[numRecipients];
        for (byte i = 0; i < numRecipients; i = (byte)(i + 1)) {
            recipients[i] = slea.readInt();
        }
        String chattext = slea.readMapleAsciiString();
        if (chr == null || !chr.getCanTalk()) {
            c.getSession().write((Object)MaplePacketCreator.serverNotice(6, "You have been muted and are therefore unable to talk."));
            return;
        }
        if (CommandProcessor.processCommand(c, chattext, ServerConstants.CommandType.NORMAL)) {
            return;
        }
        chr.getCheatTracker().checkMsg();
        switch (type) {
            case 0: {
                World.Buddy.buddyChat(recipients, chr.getId(), chr.getName(), chattext);
                break;
            }
            case 1: {
                if (chr.getParty() == null) break;
                World.Party.partyChat(chr.getParty().getId(), chattext, chr.getName());
                break;
            }
            case 2: {
                if (chr.getGuildId() <= 0) break;
                World.Guild.guildChat(chr.getGuildId(), chr.getName(), chr.getId(), chattext);
                break;
            }
            case 3: {
                if (chr.getGuildId() <= 0) break;
                World.Alliance.allianceChat(chr.getGuildId(), chr.getName(), chr.getId(), chattext);
            }
        }
    }

    public static final void Messenger(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleMessenger messenger = c.getPlayer().getMessenger();
        switch (slea.readByte()) {
            case 0: {
                int position;
                if (messenger != null) break;
                int messengerid = slea.readInt();
                if (messengerid == 0) {
                    c.getPlayer().setMessenger(World.Messenger.createMessenger(new MapleMessengerCharacter(c.getPlayer())));
                    break;
                }
                messenger = World.Messenger.getMessenger(messengerid);
                if (messenger == null || (position = messenger.getLowestPosition()) <= -1 || position >= 4) break;
                c.getPlayer().setMessenger(messenger);
                World.Messenger.joinMessenger(messenger.getId(), new MapleMessengerCharacter(c.getPlayer()), c.getPlayer().getName(), c.getChannel());
                break;
            }
            case 2: {
                if (messenger == null) break;
                MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(c.getPlayer());
                World.Messenger.leaveMessenger(messenger.getId(), messengerplayer);
                c.getPlayer().setMessenger(null);
                break;
            }
            case 3: {
                if (messenger == null) break;
                int position = messenger.getLowestPosition();
                if (position <= -1 || position >= 4) {
                    return;
                }
                String input = slea.readMapleAsciiString();
                MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(input);
                if (target != null) {
                    if (target.getMessenger() == null) {
                        if (!target.isGM() || c.getPlayer().isGM()) {
                            c.getSession().write((Object)MaplePacketCreator.messengerNote(input, 4, 1));
                            target.getClient().getSession().write((Object)MaplePacketCreator.messengerInvite(c.getPlayer().getName(), messenger.getId()));
                            break;
                        }
                        c.getSession().write((Object)MaplePacketCreator.messengerNote(input, 4, 0));
                        break;
                    }
                    c.getSession().write((Object)MaplePacketCreator.messengerChat(c.getPlayer().getName() + " : " + target.getName() + " is already using Maple Messenger."));
                    break;
                }
                if (World.isConnected(input)) {
                    World.Messenger.messengerInvite(c.getPlayer().getName(), messenger.getId(), input, c.getChannel(), c.getPlayer().isGM());
                    break;
                }
                c.getSession().write((Object)MaplePacketCreator.messengerNote(input, 4, 0));
                break;
            }
            case 5: {
                String targeted = slea.readMapleAsciiString();
                MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(targeted);
                if (target != null) {
                    if (target.getMessenger() == null) break;
                    target.getClient().getSession().write((Object)MaplePacketCreator.messengerNote(c.getPlayer().getName(), 5, 0));
                    break;
                }
                if (c.getPlayer().isGM()) break;
                World.Messenger.declineChat(targeted, c.getPlayer().getName());
                break;
            }
            case 6: {
                if (messenger == null) break;
                World.Messenger.messengerChat(messenger.getId(), slea.readMapleAsciiString(), c.getPlayer().getName());
            }
        }
    }

    public static final void Whisper_Find(SeekableLittleEndianAccessor slea, MapleClient c) {
        byte mode = slea.readByte();
        slea.readInt();
        switch (mode) {
            case 5: 
            case 68: {
                String recipient = slea.readMapleAsciiString();
                MapleCharacter player = c.getChannelServer().getPlayerStorage().getCharacterByName(recipient);
                if (player != null) {
                    if (!player.isGM() || c.getPlayer().isGM() && player.isGM()) {
                        c.getSession().write((Object)MaplePacketCreator.getFindReplyWithMap(player.getName(), player.getMap().getId(), mode == 68));
                        break;
                    }
                    c.getSession().write((Object)MaplePacketCreator.getWhisperReply(recipient, (byte)0));
                    break;
                }
                int ch = World.Find.findChannel(recipient);
                if (ch > 0) {
                    player = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(recipient);
                    if (player == null) break;
                    if (player != null) {
                        if (!player.isGM() || c.getPlayer().isGM() && player.isGM()) {
                            c.getSession().write((Object)MaplePacketCreator.getFindReply(recipient, (byte)ch, mode == 68));
                        } else {
                            c.getSession().write((Object)MaplePacketCreator.getWhisperReply(recipient, (byte)0));
                        }
                        return;
                    }
                }
                if (ch == -10) {
                    c.getSession().write((Object)MaplePacketCreator.getFindReplyWithCS(recipient, mode == 68));
                    break;
                }
                if (ch == -20) {
                    c.getSession().write((Object)MaplePacketCreator.getFindReplyWithMTS(recipient, mode == 68));
                    break;
                }
                c.getSession().write((Object)MaplePacketCreator.getWhisperReply(recipient, (byte)0));
                break;
            }
            case 6: {
                if (!c.getPlayer().getCanTalk()) {
                    c.getSession().write((Object)MaplePacketCreator.serverNotice(6, "You have been muted and are therefore unable to talk."));
                    return;
                }
                c.getPlayer().getCheatTracker().checkMsg();
                String recipient = slea.readMapleAsciiString();
                String text = slea.readMapleAsciiString();
                int ch = World.Find.findChannel(recipient);
                if (ch > 0) {
                    MapleCharacter player = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(recipient);
                    if (player == null) break;
                    player.getClient().getSession().write((Object)MaplePacketCreator.getWhisper(c.getPlayer().getName(), c.getChannel(), text));
                    if (!c.getPlayer().isGM() && player.isGM()) {
                        c.getSession().write((Object)MaplePacketCreator.getWhisperReply(recipient, (byte)0));
                        break;
                    }
                    c.getSession().write((Object)MaplePacketCreator.getWhisperReply(recipient, (byte)1));
                    break;
                }
                c.getSession().write((Object)MaplePacketCreator.getWhisperReply(recipient, (byte)0));
            }
        }
    }
}

