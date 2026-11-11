/*
 * Decompiled with CFR 0.152.
 */
package provider;

import provider.MapleDataEntity;

public interface MapleDataEntry
extends MapleDataEntity {
    @Override
    public String getName();

    public int getSize();

    public int getChecksum();

    public int getOffset();
}

