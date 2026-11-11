/*
 * Decompiled with CFR 0.152.
 */
package server.events;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import server.Randomizer;
import tools.Pair;

public class MapleOxQuizFactory {
    private boolean initialized = false;
    private Map<Pair<Integer, Integer>, MapleOxQuizEntry> questionCache = new HashMap<Pair<Integer, Integer>, MapleOxQuizEntry>();
    private static MapleOxQuizFactory instance = new MapleOxQuizFactory();

    public static MapleOxQuizFactory getInstance() {
        return instance;
    }

    public boolean hasInitialized() {
        return this.initialized;
    }

    public Map.Entry<Pair<Integer, Integer>, MapleOxQuizEntry> grabRandomQuestion() {
        Map.Entry<Pair<Integer, Integer>, MapleOxQuizEntry> oxquiz;
        int size = this.questionCache.size();
        block0: while (true) {
            Iterator<Map.Entry<Pair<Integer, Integer>, MapleOxQuizEntry>> iterator = this.questionCache.entrySet().iterator();
            do {
                if (!iterator.hasNext()) continue block0;
                oxquiz = iterator.next();
            } while (Randomizer.nextInt(size) != 0);
            break;
        }
        return oxquiz;
    }

    public void initialize() {
        if (this.initialized) {
            return;
        }
        System.out.println("Loading OX Quiz :::");
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_oxdata");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                this.questionCache.put(new Pair<Integer, Integer>(rs.getInt("questionset"), rs.getInt("questionid")), this.get(rs));
            }
            rs.close();
            ps.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.print("Done\r");
        this.initialized = true;
    }

    public MapleOxQuizEntry getFromSQL(String sql) {
        MapleOxQuizEntry ret = null;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ret = this.get(rs);
            }
            rs.close();
            ps.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static MapleOxQuizEntry getOxEntry(int questionSet, int questionId) {
        return MapleOxQuizFactory.getInstance().getOxQuizEntry(new Pair<Integer, Integer>(questionSet, questionId));
    }

    public static MapleOxQuizEntry getOxEntry(Pair<Integer, Integer> pair) {
        return MapleOxQuizFactory.getInstance().getOxQuizEntry(pair);
    }

    public MapleOxQuizEntry getOxQuizEntry(Pair<Integer, Integer> pair) {
        MapleOxQuizEntry mooe = this.questionCache.get(pair);
        if (mooe == null) {
            if (this.initialized) {
                return null;
            }
            mooe = this.getFromSQL("SELECT * FROM wz_oxdata WHERE questionset = " + pair.getLeft() + " AND questionid = " + pair.getRight());
            this.questionCache.put(pair, mooe);
        }
        return mooe;
    }

    private MapleOxQuizEntry get(ResultSet rs) throws SQLException {
        return new MapleOxQuizEntry(rs.getString("question"), rs.getString("display"), this.getAnswerByText(rs.getString("answer")), rs.getInt("questionset"), rs.getInt("questionid"));
    }

    private int getAnswerByText(String text) {
        if (text.equalsIgnoreCase("x")) {
            return 0;
        }
        if (text.equalsIgnoreCase("o")) {
            return 1;
        }
        return -1;
    }

    public static class MapleOxQuizEntry {
        private String question;
        private String answerText;
        private int answer;
        private int questionset;
        private int questionid;

        public MapleOxQuizEntry(String question, String answerText, int answer, int questionset, int questionid) {
            this.question = question;
            this.answerText = answerText;
            this.answer = answer;
            this.questionset = questionset;
            this.questionid = questionid;
        }

        public String getQuestion() {
            return this.question;
        }

        public String getAnswerText() {
            return this.answerText;
        }

        public int getAnswer() {
            return this.answer;
        }

        public int getQuestionSet() {
            return this.questionset;
        }

        public int getQuestionId() {
            return this.questionid;
        }
    }
}

