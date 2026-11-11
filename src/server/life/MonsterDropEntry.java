/*
 * Decompiled with CFR 0.152.
 */
package server.life;

public class MonsterDropEntry {
    public short questid;
    public int itemId;
    public int chance;
    public int Minimum;
    public int Maximum;

    public MonsterDropEntry(int itemId, int chance, int Minimum, int Maximum, short questid) {
        this.itemId = itemId;
        this.chance = chance;
        this.questid = questid;
        this.Minimum = Minimum;
        this.Maximum = Maximum;
    }
}

