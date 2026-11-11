/*
 * Decompiled with CFR 0.152.
 */
package server.events;

public enum MapleEventType {
    Coconut("\u63a1\u679c\u5b50", new int[]{109080000}),
    CokePlay("\u6253\u74f6\u84cb", new int[]{109080010}),
    Fitness("\u5947\u5e7b\u5fcd\u8010", new int[]{109040000, 109040001, 109040002, 109040003, 109040004}),
    OlaOla("\u9b54\u6cd5\u68ee\u6797\u731c\u7e69\u5b50", new int[]{109030001, 109030002, 109030003}),
    OxQuiz("\u5c0f\u5c0f\u8003\u9a57", new int[]{109020001}),
    Snowball("\u6efe\u96ea\u7403", new int[]{109060000});

    public String command;
    public int[] mapids;

    private MapleEventType(String comm, int[] mapids) {
        this.command = comm;
        this.mapids = mapids;
    }

    public static final MapleEventType getByString(String splitted) {
        for (MapleEventType t : MapleEventType.values()) {
            if (!t.command.equalsIgnoreCase(splitted)) continue;
            return t;
        }
        return null;
    }
}

