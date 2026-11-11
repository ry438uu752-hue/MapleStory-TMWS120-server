/*
 * Decompiled with CFR 0.152.
 */
package server;

import database.DatabaseConnection;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.World;
import java.sql.SQLException;
import server.Timer;

public class ShutdownServer
implements Runnable {
    private static final ShutdownServer instance = new ShutdownServer();
    public static boolean running = false;

    public static ShutdownServer getInstance() {
        return instance;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        ShutdownServer shutdownServer = this;
        synchronized (shutdownServer) {
            if (running) {
                return;
            }
            running = true;
        }
        World.isShutDown = true;
        try {
            Integer[] chs;
            for (ChannelServer cs : ChannelServer.getAllInstances()) {
                cs.setShutdown();
            }
            LoginServer.shutdown();
            Integer[] integerArray = chs = ChannelServer.getAllInstance().toArray(new Integer[0]);
            int n = integerArray.length;
            for (int i = 0; i < n; ++i) {
                int i2 = integerArray[i];
                try {
                    ChannelServer cs = ChannelServer.getInstance(i2);
                    ShutdownServer shutdownServer2 = this;
                    synchronized (shutdownServer2) {
                        cs.shutdown(this);
                        try {
                            this.wait();
                        }
                        catch (InterruptedException interruptedException) {
                            // empty catch block
                        }
                        continue;
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            CashShopServer.shutdown();
            World.Guild.save();
            World.Alliance.save();
            World.Family.save();
            DatabaseConnection.closeAll();
        }
        catch (SQLException e) {
            System.err.println("THROW" + e);
        }
        Timer.WorldTimer.getInstance().stop();
        Timer.MapTimer.getInstance().stop();
        Timer.MobTimer.getInstance().stop();
        Timer.BuffTimer.getInstance().stop();
        Timer.CloneTimer.getInstance().stop();
        Timer.EventTimer.getInstance().stop();
        Timer.EtcTimer.getInstance().stop();
        try {
            Thread.sleep(5000L);
        }
        catch (Exception exception) {
            // empty catch block
        }
        System.exit(0);
    }
}

