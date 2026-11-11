/*
 * Decompiled with CFR 0.152.
 */
package tools.data.output;

import java.awt.Point;

public interface LittleEndianWriter {
    public void writeZeroBytes(int var1);

    public void write(byte[] var1);

    public void write(byte var1);

    public void write(int var1);

    public void writeInt(int var1);

    public void writeShort(short var1);

    public void writeShort(int var1);

    public void writeLong(long var1);

    public void writeAsciiString(String var1);

    public void writeAsciiString(String var1, int var2);

    public void writePos(Point var1);

    public void writeMapleAsciiString(String var1);
}

