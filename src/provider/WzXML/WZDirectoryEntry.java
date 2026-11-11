/*
 * Decompiled with CFR 0.152.
 */
package provider.WzXML;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import provider.MapleDataDirectoryEntry;
import provider.MapleDataEntity;
import provider.MapleDataEntry;
import provider.MapleDataFileEntry;
import provider.WzXML.WZEntry;

public class WZDirectoryEntry
extends WZEntry
implements MapleDataDirectoryEntry {
    private List<MapleDataDirectoryEntry> subdirs = new ArrayList<MapleDataDirectoryEntry>();
    private List<MapleDataFileEntry> files = new ArrayList<MapleDataFileEntry>();
    private Map<String, MapleDataEntry> entries = new HashMap<String, MapleDataEntry>();

    public WZDirectoryEntry(String name, int size, int checksum, MapleDataEntity parent) {
        super(name, size, checksum, parent);
    }

    public WZDirectoryEntry() {
        super(null, 0, 0, null);
    }

    public void addDirectory(MapleDataDirectoryEntry dir) {
        this.subdirs.add(dir);
        this.entries.put(dir.getName(), dir);
    }

    public void addFile(MapleDataFileEntry fileEntry) {
        this.files.add(fileEntry);
        this.entries.put(fileEntry.getName(), fileEntry);
    }

    @Override
    public List<MapleDataDirectoryEntry> getSubdirectories() {
        return Collections.unmodifiableList(this.subdirs);
    }

    @Override
    public List<MapleDataFileEntry> getFiles() {
        return Collections.unmodifiableList(this.files);
    }

    @Override
    public MapleDataEntry getEntry(String name) {
        return this.entries.get(name);
    }
}

