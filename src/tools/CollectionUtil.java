/*
 * Decompiled with CFR 0.152.
 */
package tools;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtil {
    private CollectionUtil() {
    }

    public static <T> List<T> copyFirst(List<T> list, int count) {
        ArrayList<T> ret = new ArrayList<T>(list.size() < count ? list.size() : count);
        int i = 0;
        for (T elem : list) {
            ret.add(elem);
            if (i++ <= count) continue;
            break;
        }
        return ret;
    }
}

