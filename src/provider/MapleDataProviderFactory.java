/*
 * Decompiled with CFR 0.152.
 */
package provider;

import java.io.File;
import provider.MapleDataProvider;
import provider.WzXML.XMLWZFile;

public class MapleDataProviderFactory {
    private static final String wzPath = System.getProperty("net.sf.odinms.wzpath");

    private static MapleDataProvider getWZ(Object in, boolean provideImages) {
        if (in instanceof File) {
            File fileIn = (File)in;
            return new XMLWZFile(fileIn);
        }
        throw new IllegalArgumentException("Can't create data provider for input " + in);
    }

    public static MapleDataProvider getDataProvider(Object in) {
        return MapleDataProviderFactory.getWZ(in, false);
    }

    public static MapleDataProvider getImageProvidingDataProvider(Object in) {
        return MapleDataProviderFactory.getWZ(in, true);
    }

    public static File fileInWZPath(String filename) {
        return new File(wzPath, filename);
    }
}

