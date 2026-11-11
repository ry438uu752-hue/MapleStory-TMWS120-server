/*
 * Decompiled with CFR 0.152.
 */
package server.quest;

public enum MapleQuestActionType {
    UNDEFINED(-1),
    exp(0),
    item(1),
    nextQuest(2),
    money(3),
    quest(4),
    skill(5),
    pop(6),
    buffItemID(7),
    infoNumber(8),
    yes(9),
    no(10),
    sp(11);

    final byte type;

    private MapleQuestActionType(int type) {
        this.type = (byte)type;
    }

    public byte getType() {
        return this.type;
    }

    public static MapleQuestActionType getByType(byte type) {
        for (MapleQuestActionType l : MapleQuestActionType.values()) {
            if (l.getType() != type) continue;
            return l;
        }
        return null;
    }

    public static MapleQuestActionType getByWZName(String name) {
        try {
            return MapleQuestActionType.valueOf(name);
        }
        catch (IllegalArgumentException ex) {
            return UNDEFINED;
        }
    }
}

