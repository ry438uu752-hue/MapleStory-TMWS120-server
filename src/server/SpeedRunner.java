/*
 * Decompiled with CFR 0.152.
 */
package server;

import database.DatabaseConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import server.maps.SpeedRunType;
import tools.Pair;
import tools.StringUtil;

public class SpeedRunner {
    private static SpeedRunner instance = new SpeedRunner();
    private final Map<SpeedRunType, Pair<String, Map<Integer, String>>> speedRunData = new EnumMap<SpeedRunType, Pair<String, Map<Integer, String>>>(SpeedRunType.class);

    private SpeedRunner() {
    }

    public static final SpeedRunner getInstance() {
        return instance;
    }

    public final Pair<String, Map<Integer, String>> getSpeedRunData(SpeedRunType type) {
        return this.speedRunData.get((Object)type);
    }

    public final void addSpeedRunData(SpeedRunType type, Pair<StringBuilder, Map<Integer, String>> mib) {
        this.speedRunData.put(type, new Pair<String, Map<Integer, String>>(mib.getLeft().toString(), mib.getRight()));
    }

    public final void removeSpeedRunData(SpeedRunType type) {
        this.speedRunData.remove((Object)type);
    }

    public final void loadSpeedRuns() throws SQLException {
        if (this.speedRunData.size() > 0) {
            return;
        }
        for (SpeedRunType type : SpeedRunType.values()) {
            this.loadSpeedRunData(type);
        }
    }

    public final void loadSpeedRunData(SpeedRunType type) throws SQLException {
        boolean cont;
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM speedruns WHERE type = ? ORDER BY time LIMIT 25");
        ps.setString(1, type.name());
        StringBuilder ret = new StringBuilder("#rThese are the speedrun times for " + StringUtil.makeEnumHumanReadable(type.name()) + ".#k\r\n\r\n");
        LinkedHashMap<Integer, String> rett = new LinkedHashMap<Integer, String>();
        ResultSet rs = ps.executeQuery();
        int rank = 1;
        boolean changed = cont = rs.first();
        while (cont) {
            this.addSpeedRunData(ret, rett, rs.getString("members"), rs.getString("leader"), rank, rs.getString("timestring"));
            ++rank;
            cont = rs.next();
        }
        rs.close();
        ps.close();
        if (changed) {
            this.speedRunData.put(type, new Pair<String, Map<Integer, String>>(ret.toString(), rett));
        }
    }

    public final Pair<StringBuilder, Map<Integer, String>> addSpeedRunData(StringBuilder ret, Map<Integer, String> rett, String members, String leader, int rank, String timestring) {
        StringBuilder rettt = new StringBuilder();
        String[] membrz = members.split(",");
        rettt.append("#b\u8a72\u9060\u5f81\u968a " + leader + "'\u6210\u529f\u6311\u6230\u6392\u540d\u70ba " + rank + ".#k\r\n\r\n");
        for (int i = 0; i < membrz.length; ++i) {
            rettt.append("#r#e");
            rettt.append(i + 1);
            rettt.append(".#n ");
            rettt.append(membrz[i]);
            rettt.append("#k\r\n");
        }
        rett.put(rank, rettt.toString());
        ret.append("#b");
        if (membrz.length > 1) {
            ret.append("#L");
            ret.append(rank);
            ret.append("#");
        }
        ret.append("Rank #e");
        ret.append(rank);
        ret.append("#n#k : ");
        ret.append(leader);
        ret.append(", in ");
        ret.append(timestring);
        if (membrz.length > 1) {
            ret.append("#l");
        }
        ret.append("\r\n");
        return new Pair<StringBuilder, Map<Integer, String>>(ret, rett);
    }
}

