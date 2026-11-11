/*
 * Decompiled with CFR 0.152.
 */
package handling.channel;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import server.Timer;

public class MapleGuildRanking {
    private static MapleGuildRanking instance = new MapleGuildRanking();
    private List<GuildRankingInfo> ranks = new LinkedList<GuildRankingInfo>();
    private final List<levelRankingInfo> LevelRank = new LinkedList<levelRankingInfo>();
    private final List<mesoRankingInfo> MesoRank = new LinkedList<mesoRankingInfo>();
    private final List<mesoRankingInfo> FameRank = new LinkedList<mesoRankingInfo>();

    public void RankingUpdate() {
        Timer.WorldTimer.getInstance().register(new Runnable(){

            @Override
            public void run() {
                try {
                    MapleGuildRanking.this.reload();
                    MapleGuildRanking.this.showLevelRank();
                    MapleGuildRanking.this.showMesoRank();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    System.err.println("Could not update rankings");
                }
            }
        }, 3600000L, 3600000L);
    }

    public static MapleGuildRanking getInstance() {
        return instance;
    }

    public List<GuildRankingInfo> getRank() {
        if (this.ranks.isEmpty()) {
            this.reload();
        }
        return this.ranks;
    }

    public List<levelRankingInfo> getLevelRank() {
        if (this.LevelRank.isEmpty()) {
            this.showLevelRank();
        }
        return this.LevelRank;
    }

    public List<mesoRankingInfo> getMesoRank() {
        if (this.MesoRank.isEmpty()) {
            this.showMesoRank();
        }
        return this.MesoRank;
    }

    private void reload() {
        this.ranks.clear();
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM guilds ORDER BY `GP` DESC LIMIT 50");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                GuildRankingInfo rank = new GuildRankingInfo(rs.getString("name"), rs.getInt("GP"), rs.getInt("logo"), rs.getInt("logoColor"), rs.getInt("logoBG"), rs.getInt("logoBGColor"));
                this.ranks.add(rank);
            }
            ps.close();
            rs.close();
        }
        catch (SQLException e) {
            System.err.println("Error handling guildRanking");
            e.printStackTrace();
        }
    }

    private void showLevelRank() {
        this.LevelRank.clear();
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE gm < 1 and accountid > 0 ORDER BY `level` DESC LIMIT 100");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                levelRankingInfo rank1 = new levelRankingInfo(rs.getString("name"), rs.getInt("level"), rs.getInt("str"), rs.getInt("dex"), rs.getInt("int"), rs.getInt("luk"));
                this.LevelRank.add(rank1);
            }
            ps.close();
            rs.close();
        }
        catch (SQLException e) {
            System.err.println("\u6392\u884c\u932f\u8aa4");
        }
    }

    private void showMesoRank() {
        this.MesoRank.clear();
        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT *, ( chr.meso + s.meso ) as money FROM `characters` as chr , `storages` as s WHERE chr.gm < 1  AND s.accountid = chr.accountid ORDER BY money DESC LIMIT 20");){
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                mesoRankingInfo rank2 = new mesoRankingInfo(rs.getString("name"), rs.getLong("money"), rs.getInt("str"), rs.getInt("dex"), rs.getInt("int"), rs.getInt("luk"));
                this.MesoRank.add(rank2);
            }
            rs.close();
        }
        catch (SQLException e) {
            System.err.println("\u5bcc\u8c6a\u6392\u884c\u932f\u8aa4");
        }
    }

    public static class GuildRankingInfo {
        private String name;
        private int gp;
        private int logo;
        private int logocolor;
        private int logobg;
        private int logobgcolor;

        public GuildRankingInfo(String name, int gp, int logo, int logocolor, int logobg, int logobgcolor) {
            this.name = name;
            this.gp = gp;
            this.logo = logo;
            this.logocolor = logocolor;
            this.logobg = logobg;
            this.logobgcolor = logobgcolor;
        }

        public String getName() {
            return this.name;
        }

        public int getGP() {
            return this.gp;
        }

        public int getLogo() {
            return this.logo;
        }

        public int getLogoColor() {
            return this.logocolor;
        }

        public int getLogoBg() {
            return this.logobg;
        }

        public int getLogoBgColor() {
            return this.logobgcolor;
        }
    }

    public static class levelRankingInfo {
        private final String name;
        private final int level;
        private final int str;
        private final int dex;
        private final int _int;
        private final int luk;

        public levelRankingInfo(String name, int level, int str, int dex, int intt, int luk) {
            this.name = name;
            this.level = level;
            this.str = str;
            this.dex = dex;
            this._int = intt;
            this.luk = luk;
        }

        public String getName() {
            return this.name;
        }

        public int getLevel() {
            return this.level;
        }

        public int getStr() {
            return this.str;
        }

        public int getDex() {
            return this.dex;
        }

        public int getInt() {
            return this._int;
        }

        public int getLuk() {
            return this.luk;
        }
    }

    public static class mesoRankingInfo {
        private final String name;
        private final long meso;
        private final int str;
        private final int dex;
        private final int _int;
        private final int luk;

        public mesoRankingInfo(String name, long meso, int str, int dex, int intt, int luk) {
            this.name = name;
            this.meso = meso;
            this.str = str;
            this.dex = dex;
            this._int = intt;
            this.luk = luk;
        }

        public String getName() {
            return this.name;
        }

        public long getMeso() {
            return this.meso;
        }

        public int getStr() {
            return this.str;
        }

        public int getDex() {
            return this.dex;
        }

        public int getInt() {
            return this._int;
        }

        public int getLuk() {
            return this.luk;
        }
    }
}

