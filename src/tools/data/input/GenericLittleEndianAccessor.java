/*
 * Decompiled with CFR 0.152.
 */
package tools.data.input;

import java.awt.Point;
import tools.data.input.ByteInputStream;
import tools.data.input.LittleEndianAccessor;

public class GenericLittleEndianAccessor
implements LittleEndianAccessor {
    private final ByteInputStream bs;

    public GenericLittleEndianAccessor(ByteInputStream bs) {
        this.bs = bs;
    }

    @Override
    public final int readByteAsInt() {
        return this.bs.readByte();
    }

    @Override
    public final byte readByte() {
        return (byte)this.bs.readByte();
    }

    @Override
    public final int readInt() {
        int byte1 = this.bs.readByte();
        int byte2 = this.bs.readByte();
        int byte3 = this.bs.readByte();
        int byte4 = this.bs.readByte();
        return (byte4 << 24) + (byte3 << 16) + (byte2 << 8) + byte1;
    }

    @Override
    public final short readShort() {
        int byte1 = this.bs.readByte();
        int byte2 = this.bs.readByte();
        return (short)((byte2 << 8) + byte1);
    }

    @Override
    public final char readChar() {
        return (char)this.readShort();
    }

    @Override
    public final long readLong() {
        int byte1 = this.bs.readByte();
        int byte2 = this.bs.readByte();
        int byte3 = this.bs.readByte();
        int byte4 = this.bs.readByte();
        int byte5 = this.bs.readByte();
        int byte6 = this.bs.readByte();
        int byte7 = this.bs.readByte();
        int byte8 = this.bs.readByte();
        return (byte8 << 56) + (byte7 << 48) + (byte6 << 40) + (byte5 << 32) + (byte4 << 24) + (byte3 << 16) + (byte2 << 8) + byte1;
    }

    @Override
    public final float readFloat() {
        return Float.intBitsToFloat(this.readInt());
    }

    @Override
    public final double readDouble() {
        return Double.longBitsToDouble(this.readLong());
    }

    @Override
    public final String readAsciiString(int n) {
        byte[] ret = new byte[n];
        for (int x = 0; x < n; ++x) {
            ret[x] = this.readByte();
        }
        try {
            String str = new String(ret, "BIG5");
            return str;
        }
        catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }

    @Override
    public final long getBytesRead() {
        return this.bs.getBytesRead();
    }

    @Override
    public final String readMapleAsciiString() {
        return this.readAsciiString(this.readShort());
    }

    @Override
    public final Point readPos() {
        short x = this.readShort();
        short y = this.readShort();
        return new Point(x, y);
    }

    @Override
    public final byte[] read(int num) {
        byte[] ret = new byte[num];
        for (int x = 0; x < num; ++x) {
            ret[x] = this.readByte();
        }
        return ret;
    }

    @Override
    public void skip(int num) {
        for (int x = 0; x < num; ++x) {
            this.readByte();
        }
    }

    @Override
    public final long available() {
        return this.bs.available();
    }

    public final String toString() {
        return this.bs.toString();
    }

    @Override
    public final String toString(boolean b) {
        return this.bs.toString(b);
    }
}

