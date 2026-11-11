/*
 * Decompiled with CFR 0.152.
 */
package tools.data.input;

import java.io.IOException;
import tools.data.input.GenericLittleEndianAccessor;
import tools.data.input.SeekableInputStreamBytestream;
import tools.data.input.SeekableLittleEndianAccessor;

public class GenericSeekableLittleEndianAccessor
extends GenericLittleEndianAccessor
implements SeekableLittleEndianAccessor {
    private final SeekableInputStreamBytestream bs;

    public GenericSeekableLittleEndianAccessor(SeekableInputStreamBytestream bs) {
        super(bs);
        this.bs = bs;
    }

    @Override
    public final void seek(long offset) {
        try {
            this.bs.seek(offset);
        }
        catch (IOException e) {
            System.err.println("Seek failed" + e);
        }
    }

    @Override
    public final long getPosition() {
        try {
            return this.bs.getPosition();
        }
        catch (IOException e) {
            System.err.println("getPosition failed" + e);
            return -1L;
        }
    }

    @Override
    public final void skip(int num) {
        this.seek(this.getPosition() + (long)num);
    }
}

