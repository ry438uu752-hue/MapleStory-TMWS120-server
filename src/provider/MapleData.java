/*
 * Decompiled with CFR 0.152.
 */
package provider;

import java.util.List;
import provider.MapleDataEntity;
import provider.WzXML.MapleDataType;

public interface MapleData
extends MapleDataEntity,
Iterable<MapleData> {
    @Override
    public String getName();

    public MapleDataType getType();

    public List<MapleData> getChildren();

    public MapleData getChildByPath(String var1);

    public Object getData();
}

