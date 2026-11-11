/*
 * Decompiled with CFR 0.152.
 */
package provider.WzXML;

import provider.MapleDataEntity;
import provider.MapleDataFileEntry;
import provider.WzXML.WZEntry;

public class WZFileEntry
extends WZEntry
implements MapleDataFileEntry {
    private int offset;

    public WZFileEntry(String name, int size, int checksum, MapleDataEntity parent) {
        super(name, size, checksum, parent);
    }

    @Override
    public int getOffset() {
        return this.offset;
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }
}

