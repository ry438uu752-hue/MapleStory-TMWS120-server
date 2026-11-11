/*
 * Decompiled with CFR 0.152.
 */
package server.quest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import provider.MapleData;
import provider.MapleDataEntity;
import provider.WzXML.MapleDataType;

public class MapleCustomQuestData
implements MapleData,
Serializable {
    private static final long serialVersionUID = -8600005891655365066L;
    private List<MapleCustomQuestData> children = new LinkedList<MapleCustomQuestData>();
    private String name;
    private Object data;
    private MapleDataEntity parent;

    public MapleCustomQuestData(String name, Object data, MapleDataEntity parent) {
        this.name = name;
        this.data = data;
        this.parent = parent;
    }

    public void addChild(MapleData child) {
        this.children.add((MapleCustomQuestData)child);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public MapleDataType getType() {
        return MapleDataType.UNKNOWN_TYPE;
    }

    @Override
    public List<MapleData> getChildren() {
        MapleData[] ret = new MapleData[this.children.size()];
        ret = this.children.toArray(ret);
        return new ArrayList<MapleData>(Arrays.asList(ret));
    }

    @Override
    public MapleData getChildByPath(String name) {
        String nextName;
        String lookup;
        if (name.equals(this.name)) {
            return this;
        }
        if (name.indexOf("/") == -1) {
            lookup = name;
            nextName = name;
        } else {
            lookup = name.substring(0, name.indexOf("/"));
            nextName = name.substring(name.indexOf("/") + 1);
        }
        for (MapleData mapleData : this.children) {
            if (!mapleData.getName().equals(lookup)) continue;
            return mapleData.getChildByPath(nextName);
        }
        return null;
    }

    @Override
    public Object getData() {
        return this.data;
    }

    @Override
    public Iterator<MapleData> iterator() {
        return this.getChildren().iterator();
    }

    @Override
    public MapleDataEntity getParent() {
        return this.parent;
    }
}

