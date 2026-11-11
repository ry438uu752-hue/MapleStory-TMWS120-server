/*
 * Decompiled with CFR 0.152.
 */
package client.messages.commands;

import client.MapleClient;
import constants.ServerConstants;

public abstract class CommandExecute {
    public abstract int execute(MapleClient var1, String[] var2);

    public ServerConstants.CommandType getType() {
        return ServerConstants.CommandType.NORMAL;
    }

    public static abstract class TradeExecute
    extends CommandExecute {
        @Override
        public ServerConstants.CommandType getType() {
            return ServerConstants.CommandType.TRADE;
        }
    }

    static enum ReturnValue {
        DONT_LOG,
        LOG;

    }
}

