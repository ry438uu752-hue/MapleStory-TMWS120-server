/*
 * Decompiled with CFR 0.152.
 */
package client.messages;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.commands.AdminCommand;
import client.messages.commands.CommandExecute;
import client.messages.commands.CommandObject;
import client.messages.commands.GMCommand;
import client.messages.commands.InternCommand;
import client.messages.commands.PlayerCommand;
import constants.ServerConstants;
import database.DatabaseConnection;
import java.lang.reflect.Modifier;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import tools.FileoutputUtil;

public class CommandProcessor {
    private static final HashMap<String, CommandObject> commands;
    private static final HashMap<Integer, ArrayList<String>> commandList;

    private static void sendDisplayMessage(MapleClient c, String msg, ServerConstants.CommandType type) {
        if (c.getPlayer() == null) {
            return;
        }
        switch (type) {
            case NORMAL: {
                c.getPlayer().dropMessage(6, msg);
                break;
            }
            case TRADE: {
                c.getPlayer().dropMessage(-2, "\u932f\u8aa4 : " + msg);
            }
        }
    }

    public static boolean processCommand(MapleClient c, String line, ServerConstants.CommandType type) {
        if (line.charAt(0) == ServerConstants.PlayerGMRank.NORMAL.getCommandPrefix()) {
            block10: {
                String[] splitted = line.split(" ");
                splitted[0] = splitted[0].toLowerCase();
                CommandObject co = commands.get(splitted[0]);
                if (co == null || co.getType() != type) {
                    CommandProcessor.sendDisplayMessage(c, "\u6c92\u6709\u9019\u500b\u6307\u4ee4,\u53ef\u4ee5\u4f7f\u7528 @\u5e6b\u52a9/@help \u4f86\u67e5\u770b\u6307\u4ee4.", type);
                    return true;
                }
                try {
                    int n = co.execute(c, splitted);
                }
                catch (Exception e) {
                    CommandProcessor.sendDisplayMessage(c, "\u6709\u932f\u8aa4.", type);
                    if (!c.getPlayer().isGM()) break block10;
                    CommandProcessor.sendDisplayMessage(c, "\u932f\u8aa4: " + e, type);
                }
            }
            return true;
        }
        if (c.getPlayer().getGMLevel() > ServerConstants.PlayerGMRank.NORMAL.getLevel() && (line.charAt(0) == ServerConstants.PlayerGMRank.GM.getCommandPrefix() || line.charAt(0) == ServerConstants.PlayerGMRank.ADMIN.getCommandPrefix() || line.charAt(0) == ServerConstants.PlayerGMRank.INTERN.getCommandPrefix())) {
            String[] splitted = line.split(" ");
            splitted[0] = splitted[0].toLowerCase();
            if (line.charAt(0) == '!') {
                CommandObject co = commands.get(splitted[0]);
                if (co == null || co.getType() != type) {
                    CommandProcessor.sendDisplayMessage(c, "\u6c92\u6709\u9019\u500b\u6307\u4ee4.", type);
                    return true;
                }
                if (c.getPlayer().getGMLevel() >= co.getReqGMLevel()) {
                    int ret = co.execute(c, splitted);
                    if (ret > 0 && c.getPlayer() != null) {
                        CommandProcessor.logGMCommandToDB(c.getPlayer(), line);
                        System.out.println("[ " + c.getPlayer().getName() + " ] \u4f7f\u7528\u4e86\u6307\u4ee4: " + line);
                    }
                } else {
                    CommandProcessor.sendDisplayMessage(c, "\u4f60\u6c92\u6709\u6b0a\u9650\u53ef\u4ee5\u4f7f\u7528\u6307\u4ee4.", type);
                }
                return true;
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void logGMCommandToDB(MapleCharacter player, String command) {
        PreparedStatement ps = null;
        try {
            ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO gmlog (cid, command, mapid) VALUES (?, ?, ?)");
            ps.setInt(1, player.getId());
            ps.setString(2, command);
            ps.setInt(3, player.getMap().getId());
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            FileoutputUtil.outputFileError("Logs/Log_Packet_Except.rtf", ex);
            ex.printStackTrace();
        }
        finally {
            try {
                ps.close();
            }
            catch (SQLException sQLException) {}
        }
    }

    static {
        Class[] CommandFiles;
        commands = new HashMap();
        commandList = new HashMap();
        for (Class clasz : CommandFiles = new Class[]{PlayerCommand.class, AdminCommand.class, GMCommand.class, InternCommand.class}) {
            try {
                ServerConstants.PlayerGMRank rankNeeded = (ServerConstants.PlayerGMRank)((Object)clasz.getMethod("getPlayerLevelRequired", new Class[0]).invoke(null, null));
                Class<?>[] a = clasz.getDeclaredClasses();
                ArrayList<String> cL = new ArrayList<String>();
                for (Class<?> c : a) {
                    try {
                        boolean enabled;
                        if (Modifier.isAbstract(c.getModifiers()) || c.isSynthetic()) continue;
                        Object o = c.newInstance();
                        try {
                            enabled = c.getDeclaredField("enabled").getBoolean(c.getDeclaredField("enabled"));
                        }
                        catch (NoSuchFieldException ex) {
                            enabled = true;
                        }
                        if (!(o instanceof CommandExecute) || !enabled) continue;
                        cL.add(rankNeeded.getCommandPrefix() + c.getSimpleName().toLowerCase());
                        commands.put(rankNeeded.getCommandPrefix() + c.getSimpleName().toLowerCase(), new CommandObject(rankNeeded.getCommandPrefix() + c.getSimpleName().toLowerCase(), (CommandExecute)o, rankNeeded.getLevel()));
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                        FileoutputUtil.outputFileError("Logs/Log_Script_Except.rtf", ex);
                    }
                }
                Collections.sort(cL);
                commandList.put(rankNeeded.getLevel(), cL);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                FileoutputUtil.outputFileError("Logs/Log_Script_Except.rtf", ex);
            }
        }
    }
}

