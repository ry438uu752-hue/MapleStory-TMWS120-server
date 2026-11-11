/*
 * Decompiled with CFR 0.152.
 */
package handling.login;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import provider.MapleData;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;

public class LoginInformationProvider {
    private static final LoginInformationProvider instance = new LoginInformationProvider();
    protected final List<String> ForbiddenName = new ArrayList<String>();

    public static LoginInformationProvider getInstance() {
        return instance;
    }

    protected LoginInformationProvider() {
        System.out.println("Loading LoginInformationProvider :::");
        String WZpath = System.getProperty("net.sf.odinms.wzpath");
        MapleData nameData = MapleDataProviderFactory.getDataProvider(new File(WZpath + "/Etc.wz")).getData("ForbiddenName.img");
        for (MapleData data : nameData.getChildren()) {
            this.ForbiddenName.add(MapleDataTool.getString(data));
        }
    }

    public final boolean isForbiddenName(String in) {
        for (String name : this.ForbiddenName) {
            if (!in.contains(name)) continue;
            return true;
        }
        return false;
    }
}

