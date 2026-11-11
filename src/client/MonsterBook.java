/*
 * Decompiled with CFR 0.152.
 */
package client;

import client.MapleClient;
import constants.GameConstants;
import database.DatabaseConnection;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import server.MapleItemInformationProvider;
import tools.MaplePacketCreator;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.MonsterBookPacket;

public class MonsterBook
implements Serializable {
    private static final long serialVersionUID = 7179541993413738569L;
    private boolean changed = false;
    private int SpecialCard = 0;
    private int NormalCard = 0;
    private int BookLevel = 1;
    private Map<Integer, Integer> cards;

    public MonsterBook(Map<Integer, Integer> cards) {
        this.cards = cards;
        for (Map.Entry<Integer, Integer> card : cards.entrySet()) {
            if (GameConstants.isSpecialCard(card.getKey())) {
                this.SpecialCard += card.getValue().intValue();
                continue;
            }
            this.NormalCard += card.getValue().intValue();
        }
        this.calculateLevel();
    }

    public Map<Integer, Integer> getCards() {
        return this.cards;
    }

    public final int getTotalCards() {
        return this.SpecialCard + this.NormalCard;
    }

    public final int getLevelByCard(int cardid) {
        return this.cards.get(cardid) == null ? 0 : this.cards.get(cardid);
    }

    public static final MonsterBook loadCards(int charid) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM monsterbook WHERE charid = ? ORDER BY cardid ASC");
        ps.setInt(1, charid);
        ResultSet rs = ps.executeQuery();
        LinkedHashMap<Integer, Integer> cards = new LinkedHashMap<Integer, Integer>();
        while (rs.next()) {
            cards.put(rs.getInt("cardid"), rs.getInt("level"));
        }
        rs.close();
        ps.close();
        return new MonsterBook(cards);
    }

    public final void saveCards(int charid) throws SQLException {
        if (!this.changed || this.cards.size() == 0) {
            return;
        }
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("DELETE FROM monsterbook WHERE charid = ?");
        ps.setInt(1, charid);
        ps.execute();
        ps.close();
        boolean first = true;
        StringBuilder query = new StringBuilder();
        for (Map.Entry<Integer, Integer> all : this.cards.entrySet()) {
            if (first) {
                first = false;
                query.append("INSERT INTO monsterbook VALUES (DEFAULT,");
            } else {
                query.append(",(DEFAULT,");
            }
            query.append(charid);
            query.append(",");
            query.append(all.getKey());
            query.append(",");
            query.append(all.getValue());
            query.append(")");
        }
        ps = con.prepareStatement(query.toString());
        ps.execute();
        ps.close();
    }

    private final void calculateLevel() {
        int Size = this.NormalCard + this.SpecialCard;
        this.BookLevel = 8;
        for (int i = 0; i < 8; ++i) {
            if (Size > GameConstants.getBookLevel(i)) continue;
            this.BookLevel = i + 1;
            break;
        }
    }

    public final void addCardPacket(MaplePacketLittleEndianWriter mplew) {
        mplew.writeShort(this.cards.size());
        for (Map.Entry<Integer, Integer> all : this.cards.entrySet()) {
            mplew.writeShort(GameConstants.getCardShortId(all.getKey()));
            mplew.write(all.getValue());
        }
    }

    public final void addCharInfoPacket(int bookcover, MaplePacketLittleEndianWriter mplew) {
        mplew.writeInt(this.BookLevel);
        mplew.writeInt(this.NormalCard);
        mplew.writeInt(this.SpecialCard);
        mplew.writeInt(this.NormalCard + this.SpecialCard);
        mplew.writeInt(MapleItemInformationProvider.getInstance().getCardMobId(bookcover));
    }

    public final void updateCard(MapleClient c, int cardid) {
        c.getSession().write((Object)MonsterBookPacket.changeCover(cardid));
    }

    public final void addCard(MapleClient c, int cardid) {
        this.changed = true;
        c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MonsterBookPacket.showForeginCardEffect(c.getPlayer().getId()), false);
        if (this.cards.containsKey(cardid)) {
            int levels = this.cards.get(cardid);
            if (levels >= 5) {
                c.getSession().write((Object)MonsterBookPacket.addCard(true, cardid, levels));
            } else {
                if (GameConstants.isSpecialCard(cardid)) {
                    ++this.SpecialCard;
                } else {
                    ++this.NormalCard;
                }
                c.getSession().write((Object)MonsterBookPacket.addCard(false, cardid, levels));
                c.getSession().write((Object)MonsterBookPacket.showGainCard(cardid));
                c.getSession().write((Object)MaplePacketCreator.showSpecialEffect(14));
                this.cards.put(cardid, levels + 1);
                this.calculateLevel();
            }
            return;
        }
        if (GameConstants.isSpecialCard(cardid)) {
            ++this.SpecialCard;
        } else {
            ++this.NormalCard;
        }
        this.cards.put(cardid, 1);
        c.getSession().write((Object)MonsterBookPacket.addCard(false, cardid, 1));
        c.getSession().write((Object)MonsterBookPacket.showGainCard(cardid));
        c.getSession().write((Object)MaplePacketCreator.showSpecialEffect(14));
        this.calculateLevel();
    }
}

