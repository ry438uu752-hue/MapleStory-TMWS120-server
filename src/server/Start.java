/*
 * Decompiled with CFR 0.152.
 */
package server;

import client.SkillFactory;
import database.DatabaseConnection;
import handling.MapleServerHandler;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.channel.MapleGuildRanking;
import handling.login.LoginInformationProvider;
import handling.login.LoginServer;
import handling.world.World;
import handling.world.family.MapleFamilyBuff;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import server.AutobanManager;
import server.CashItemFactory;
import server.MTSStorage;
import server.MapleCarnivalFactory;
import server.MapleItemInformationProvider;
import server.RandomRewards;
import server.RankingWorker;
import server.ServerProperties;
import server.ShutdownServer;
import server.SpeedRunner;
import server.Timer;
import server.events.MapleOxQuizFactory;
import server.life.MapleLifeFactory;
import server.quest.MapleQuest;

public class Start {
    public static final void main(String[] args) {
        if (Boolean.parseBoolean(ServerProperties.getProperty("tms.Admin"))) {
            System.out.println("[!!! \u7ba1\u7406\u54e1\u6a21\u5f0f !!!]");
        }
        if (Boolean.parseBoolean(ServerProperties.getProperty("tms.AutoRegister"))) {
            System.out.println("\u958b\u555f\u8a3b\u518a\u6a21\u5f0f :::");
        }
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE accounts SET loggedin = 0");
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException ex) {
            throw new RuntimeException("[EXCEPTION] Please check if the SQL server is active.");
        }
        World.init();
        Timer.WorldTimer.getInstance().start();
        Timer.EtcTimer.getInstance().start();
        Timer.MapTimer.getInstance().start();
        Timer.MobTimer.getInstance().start();
        Timer.CloneTimer.getInstance().start();
        Timer.EventTimer.getInstance().start();
        Timer.BuffTimer.getInstance().start();
        LoginInformationProvider.getInstance();
        MapleQuest.initQuests();
        MapleLifeFactory.loadQuestCounts();
        MapleItemInformationProvider.getInstance().load();
        System.out.println("[\u8f09\u5165\u9aee\u578b\u81c9\u90e8\u7269\u4ef6]");
        MapleItemInformationProvider.getInstance().loadStyles(false);
        RandomRewards.getInstance();
        SkillFactory.getSkill(99999999);
        MapleOxQuizFactory.getInstance().initialize();
        MapleCarnivalFactory.getInstance();
        MapleGuildRanking.getInstance().getRank();
        MapleFamilyBuff.getBuffEntry();
        MapleServerHandler.registerMBean();
        RankingWorker.getInstance().run();
        MTSStorage.load();
        CashItemFactory.getInstance().initialize();
        LoginServer.run_startup_configurations();
        ChannelServer.startChannel_Main();
        System.out.println("[\u8cfc\u7269\u5546\u57ce\u4f3a\u670d\u5668\u555f\u52d5\u4e2d]");
        CashShopServer.run_startup_configurations();
        System.out.println("[\u8cfc\u7269\u5546\u57ce\u4f3a\u670d\u5668\u555f\u52d5\u5b8c\u6210]");
        Timer.CheatTimer.getInstance().register(AutobanManager.getInstance(), 60000L);
        Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown()));
        try {
            SpeedRunner.getInstance().loadSpeedRuns();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        World.registerRespawn();
        LoginServer.setOn();
        System.out.println("\u52a0\u8f09\u5b8c\u6210 :::");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.gc();
        Timer.PingTimer.getInstance().register(System::gc, 1800000L);
    }

    public static class Shutdown
    implements Runnable {
        @Override
        public void run() {
            new Thread(ShutdownServer.getInstance()).start();
        }
    }
}

