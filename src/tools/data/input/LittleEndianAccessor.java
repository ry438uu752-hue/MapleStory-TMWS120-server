/*
 * Decompiled with CFR 0.152.
 */
package tools.data.input;

import java.awt.Point;

public interface LittleEndianAccessor {
    public byte readByte();

    public int readByteAsInt();

    public char readChar();

    public short readShort();

    public int readInt();

    public long readLong();

    public void skip(int var1);

    public byte[] read(int var1);

    public float readFloat();

    public double readDouble();

    public String readAsciiString(int var1);

    public String readMapleAsciiString();

    public Point readPos();

    public long getBytesRead();

    public long available();

    public String toString(boolean var1);
}

