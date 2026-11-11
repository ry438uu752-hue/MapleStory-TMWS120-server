/*
 * Decompiled with CFR 0.152.
 */
package server;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import server.MapleCarnivalChallenge;

public class RankingWorker {
    private final Map<Integer, List<RankingInformation>> rankings = new HashMap<Integer, List<RankingInformation>>();
    private final Map<String, Integer> jobCommands = new HashMap<String, Integer>();
    private static RankingWorker instance;
    private Connection con;

    public static final RankingWorker getInstance() {
        if (instance == null) {
            instance = new RankingWorker();
        }
        return instance;
    }

    public final Integer getJobCommand(String job) {
        return this.jobCommands.get(job);
    }

    public final Map<String, Integer> getJobCommands() {
        return this.jobCommands;
    }

    public final List<RankingInformation> getRankingInfo(int job) {
        return this.rankings.get(job);
    }

    public final void run() {
        System.out.println("Loading Ranking Server :::");
        this.loadJobCommands();
        try {
            this.con = DatabaseConnection.getConnection();
            this.updateRanking();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Could not update rankings");
        }
    }

    private void updateRanking() throws Exception {
        StringBuilder sb = new StringBuilder("SELECT c.id, c.job, c.exp, c.level, c.name, c.jobRank, c.jobRankMove, c.rank, c.rankMove");
        sb.append(", a.lastlogin AS lastlogin, a.loggedin FROM characters AS c LEFT JOIN accounts AS a ON c.accountid = a.id WHERE c.gm = 0 AND a.banned = 0 ");
        sb.append("ORDER BY c.level DESC , c.exp DESC , c.fame DESC , c.meso DESC , c.rank ASC");
        PreparedStatement charSelect = this.con.prepareStatement(sb.toString());
        ResultSet rs = charSelect.executeQuery();
        PreparedStatement ps = this.con.prepareStatement("UPDATE characters SET jobRank = ?, jobRankMove = ?, rank = ?, rankMove = ? WHERE id = ?");
        int rank = 0;
        LinkedHashMap<Integer, Integer> rankMap = new LinkedHashMap<Integer, Integer>();
        for (int i : this.jobCommands.values()) {
            rankMap.put(i, 0);
            this.rankings.put(i, new ArrayList());
        }
        while (rs.next()) {
            int job = rs.getInt("job");
            if (!rankMap.containsKey(job / 100)) continue;
            int jobRank = (Integer)rankMap.get(job / 100) + 1;
            rankMap.put(job / 100, jobRank);
            this.rankings.get(-1).add(new RankingInformation(rs.getString("name"), job, rs.getInt("level"), rs.getInt("exp"), ++rank));
            this.rankings.get(job / 100).add(new RankingInformation(rs.getString("name"), job, rs.getInt("level"), rs.getInt("exp"), jobRank));
            ps.setInt(1, jobRank);
            ps.setInt(2, rs.getInt("jobRank") - jobRank);
            ps.setInt(3, rank);
            ps.setInt(4, rs.getInt("rank") - rank);
            ps.setInt(5, rs.getInt("id"));
            ps.addBatch();
        }
        ps.executeBatch();
        rs.close();
        charSelect.close();
        ps.close();
    }

    public final void loadJobCommands() {
        this.jobCommands.put("all", -1);
        this.jobCommands.put("beginner", 0);
        this.jobCommands.put("warrior", 1);
        this.jobCommands.put("magician", 2);
        this.jobCommands.put("bowman", 3);
        this.jobCommands.put("thief", 4);
        this.jobCommands.put("pirate", 5);
    }

    public static class RankingInformation {
        public int job;
        public int level;
        public int exp;
        public int rank;
        public String name;
        public String toString;

        public RankingInformation(String name, int job, int level, int exp, int rank) {
            this.name = name;
            this.job = job;
            this.level = level;
            this.exp = exp;
            this.rank = rank;
            this.loadToString();
        }

        public void loadToString() {
            StringBuilder builder = new StringBuilder("Rank ");
            builder.append(this.rank);
            builder.append(" : ");
            builder.append(this.name);
            builder.append(" - Level ");
            builder.append(this.level);
            builder.append(" ");
            builder.append(MapleCarnivalChallenge.getJobNameById(this.job));
            builder.append(" | ");
            builder.append(this.exp);
            builder.append(" EXP");
            this.toString = builder.toString();
        }

        public String toString() {
            return this.toString;
        }
    }
}

