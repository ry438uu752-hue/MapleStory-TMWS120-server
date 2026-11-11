/*
 * Decompiled with CFR 0.152.
 */
package tools;

import java.io.File;
import tools.BytesEncodingDetect;

public class EncodingDetect {
    public static String getJavaEncode(String filePath) {
        return EncodingDetect.getJavaEncode(new File(filePath));
    }

    public static String getJavaEncode(File file) {
        BytesEncodingDetect s = new BytesEncodingDetect();
        String fileCode = BytesEncodingDetect.javaname[s.detectEncoding(file)];
        return fileCode;
    }
}

