/*
 * Decompiled with CFR 0.152.
 */
package handling.login;

import client.MapleClient;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import java.util.Map;
import server.Timer;
import tools.MaplePacketCreator;
import tools.packet.LoginPacket;

public class LoginWorker {
    private static long lastUpdate = 0L;

    public static void registerClient(MapleClient c) {
        if (LoginServer.isAdminOnly() && !c.isGm()) {
            c.getSession().write((Object)MaplePacketCreator.serverNotice(1, "The server is currently set to Admin login only.\r\nWe are currently testing some issues.\r\nPlease try again later."));
            c.getSession().write((Object)LoginPacket.getLoginFailed(7));
            return;
        }
        if (System.currentTimeMillis() - lastUpdate > 600000L) {
            lastUpdate = System.currentTimeMillis();
            Map<Integer, Integer> load = ChannelServer.getChannelLoad();
            int usersOn = 0;
            if (load == null || load.size() <= 0) {
                lastUpdate = 0L;
                c.getSession().write((Object)LoginPacket.getLoginFailed(7));
                return;
            }
            double loadFactor = 1200.0 / ((double)LoginServer.getUserLimit() / (double)load.size());
            for (Map.Entry<Integer, Integer> entry : load.entrySet()) {
                usersOn += entry.getValue().intValue();
                load.put(entry.getKey(), Math.min(1200, (int)((double)entry.getValue().intValue() * loadFactor)));
            }
            LoginServer.setLoad(load, usersOn);
            lastUpdate = System.currentTimeMillis();
        }
        if (c.finishLogin() == 0) {
            if (c.getSecondPassword() == null) {
                c.getSession().write((Object)LoginPacket.getGenderNeeded(c));
            } else {
                c.getSession().write((Object)LoginPacket.getAuthSuccessRequest(c));
                c.getSession().write((Object)LoginPacket.getServerList(0, LoginServer.getServerName(), LoginServer.getLoad()));
                c.getSession().write((Object)LoginPacket.getEndOfServerList());
            }
        } else {
            c.getSession().write((Object)LoginPacket.getLoginFailed(7));
            return;
        }
        c.setIdleTask(Timer.PingTimer.getInstance().schedule(new Runnable(){

            @Override
            public void run() {
            }
        }, 6000000L));
    }
}

