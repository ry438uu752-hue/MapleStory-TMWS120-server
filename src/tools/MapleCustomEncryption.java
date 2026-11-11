/*
 * Decompiled with CFR 0.152.
 */
package tools;

import tools.BitTools;

public class MapleCustomEncryption {
    public static final byte[] encryptData(byte[] data) {
        for (int j = 0; j < 6; ++j) {
            byte cur;
            int i;
            byte remember = 0;
            byte dataLength = (byte)(data.length & 0xFF);
            if (j % 2 == 0) {
                for (i = 0; i < data.length; ++i) {
                    cur = data[i];
                    cur = BitTools.rollLeft(cur, 3);
                    cur = (byte)(cur + dataLength);
                    remember = cur = (byte)(cur ^ remember);
                    cur = BitTools.rollRight(cur, dataLength & 0xFF);
                    cur = (byte)(~cur & 0xFF);
                    cur = (byte)(cur + 72);
                    dataLength = (byte)(dataLength - 1);
                    data[i] = cur;
                }
                continue;
            }
            for (i = data.length - 1; i >= 0; --i) {
                cur = data[i];
                cur = BitTools.rollLeft(cur, 4);
                cur = (byte)(cur + dataLength);
                remember = cur = (byte)(cur ^ remember);
                cur = (byte)(cur ^ 0x13);
                cur = BitTools.rollRight(cur, 3);
                dataLength = (byte)(dataLength - 1);
                data[i] = cur;
            }
        }
        return data;
    }

    public static final byte[] decryptData(byte[] data) {
        for (int j = 1; j <= 1; ++j) {
            byte cur;
            int i;
            byte remember = 0;
            byte dataLength = (byte)(data.length & 0xFF);
            byte nextRemember = 0;
            if (j % 2 == 0) {
                for (i = 0; i < data.length; ++i) {
                    cur = data[i];
                    cur = (byte)(cur - 72);
                    cur = (byte)(~cur & 0xFF);
                    nextRemember = cur = BitTools.rollLeft(cur, dataLength & 0xFF);
                    cur = (byte)(cur ^ remember);
                    remember = nextRemember;
                    cur = (byte)(cur - dataLength);
                    data[i] = cur = BitTools.rollRight(cur, 3);
                    dataLength = (byte)(dataLength - 1);
                }
                continue;
            }
            for (i = data.length - 1; i >= 0; --i) {
                cur = data[i];
                cur = BitTools.rollLeft(cur, 3);
                nextRemember = cur = (byte)(cur ^ 0x13);
                cur = (byte)(cur ^ remember);
                remember = nextRemember;
                cur = (byte)(cur - dataLength);
                data[i] = cur = BitTools.rollRight(cur, 4);
                dataLength = (byte)(dataLength - 1);
            }
        }
        return data;
    }
}

