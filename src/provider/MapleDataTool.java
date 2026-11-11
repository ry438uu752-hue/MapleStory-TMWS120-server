/*
 * Decompiled with CFR 0.152.
 */
package provider;

import java.awt.Point;
import java.awt.image.BufferedImage;
import provider.MapleCanvas;
import provider.MapleData;
import provider.MapleDataEntity;
import provider.WzXML.MapleDataType;

public class MapleDataTool {
    public static String getString(MapleData data) {
        return (String)data.getData();
    }

    public static String getString(MapleData data, String def) {
        if (data == null || data.getData() == null) {
            return def;
        }
        return (String)data.getData();
    }

    public static String getString(String path, MapleData data) {
        return MapleDataTool.getString(data.getChildByPath(path));
    }

    public static String getString(String path, MapleData data, String def) {
        return MapleDataTool.getString(data.getChildByPath(path), def);
    }

    public static double getDouble(MapleData data) {
        return (Double)data.getData();
    }

    public static float getFloat(MapleData data) {
        return ((Float)data.getData()).floatValue();
    }

    public static float getFloat(MapleData data, float def) {
        if (data == null || data.getData() == null) {
            return def;
        }
        return ((Float)data.getData()).floatValue();
    }

    public static int getInt(MapleData data) {
        return (Integer)data.getData();
    }

    public static int getInt(MapleData data, int def) {
        if (data == null || data.getData() == null) {
            return def;
        }
        if (data.getType() == MapleDataType.STRING) {
            return Integer.parseInt(MapleDataTool.getString(data));
        }
        if (data.getType() == MapleDataType.SHORT) {
            return ((Short)data.getData()).shortValue();
        }
        return (Integer)data.getData();
    }

    public static int getInt(String path, MapleData data) {
        return MapleDataTool.getInt(data.getChildByPath(path));
    }

    public static int getIntConvert(MapleData data) {
        if (data.getType() == MapleDataType.STRING) {
            return Integer.parseInt(MapleDataTool.getString(data));
        }
        return MapleDataTool.getInt(data);
    }

    public static int getIntConvert(String path, MapleData data) {
        MapleData d = data.getChildByPath(path);
        if (d.getType() == MapleDataType.STRING) {
            return Integer.parseInt(MapleDataTool.getString(d));
        }
        return MapleDataTool.getInt(d);
    }

    public static int getInt(String path, MapleData data, int def) {
        return MapleDataTool.getInt(data.getChildByPath(path), def);
    }

    public static int getIntConvert(String path, MapleData data, int def) {
        if (data == null) {
            return def;
        }
        MapleData d = data.getChildByPath(path);
        if (d == null) {
            return def;
        }
        if (d.getType() == MapleDataType.STRING) {
            try {
                return Integer.parseInt(MapleDataTool.getString(d));
            }
            catch (NumberFormatException nfe) {
                return def;
            }
        }
        return MapleDataTool.getInt(d, def);
    }

    public static BufferedImage getImage(MapleData data) {
        return ((MapleCanvas)data.getData()).getImage();
    }

    public static Point getPoint(MapleData data) {
        return (Point)data.getData();
    }

    public static Point getPoint(String path, MapleData data) {
        return MapleDataTool.getPoint(data.getChildByPath(path));
    }

    public static Point getPoint(String path, MapleData data, Point def) {
        MapleData pointData = data.getChildByPath(path);
        if (pointData == null) {
            return def;
        }
        return MapleDataTool.getPoint(pointData);
    }

    public static String getFullDataPath(MapleData data) {
        String path = "";
        for (MapleDataEntity myData = data; myData != null; myData = myData.getParent()) {
            path = myData.getName() + "/" + path;
        }
        return path.substring(0, path.length() - 1);
    }
}

