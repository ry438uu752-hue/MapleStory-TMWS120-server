/*
 * Decompiled with CFR 0.152.
 */
package provider.WzXML;

import provider.MapleDataEntity;
import provider.MapleDataEntry;

public class WZEntry
implements MapleDataEntry {
    private String name;
    private int size;
    private int checksum;
    private int offset;
    private MapleDataEntity parent;

    public WZEntry(String name, int size, int checksum, MapleDataEntity parent) {
        this.name = name;
        this.size = size;
        this.checksum = checksum;
        this.parent = parent;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public int getChecksum() {
        return this.checksum;
    }

    @Override
    public int getOffset() {
        return this.offset;
    }

    @Override
    public MapleDataEntity getParent() {
        return this.parent;
    }
}

