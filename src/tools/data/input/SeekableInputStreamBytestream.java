/*
 * Decompiled with CFR 0.152.
 */
package tools.data.input;

import java.io.IOException;
import tools.data.input.ByteInputStream;

public interface SeekableInputStreamBytestream
extends ByteInputStream {
    public void seek(long var1) throws IOException;

    public long getPosition() throws IOException;

    @Override
    public String toString(boolean var1);
}

