/*
 * Decompiled with CFR 0.152.
 */
package provider;

import java.util.List;
import provider.MapleDataEntry;
import provider.MapleDataFileEntry;

public interface MapleDataDirectoryEntry
extends MapleDataEntry {
    public List<MapleDataDirectoryEntry> getSubdirectories();

    public List<MapleDataFileEntry> getFiles();

    public MapleDataEntry getEntry(String var1);
}

