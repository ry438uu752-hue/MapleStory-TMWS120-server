/*
 * Decompiled with CFR 0.152.
 */
package tools.data.output;

import org.apache.mina.common.ByteBuffer;
import tools.data.output.ByteBufferOutputstream;
import tools.data.output.GenericLittleEndianWriter;

public class ByteBufferLittleEndianWriter
extends GenericLittleEndianWriter {
    private ByteBuffer bb;

    public ByteBufferLittleEndianWriter() {
        this(50, true);
    }

    public ByteBufferLittleEndianWriter(int size) {
        this(size, false);
    }

    public ByteBufferLittleEndianWriter(int initialSize, boolean autoExpand) {
        this.bb = ByteBuffer.allocate((int)initialSize);
        this.bb.setAutoExpand(autoExpand);
        this.setByteOutputStream(new ByteBufferOutputstream(this.bb));
    }

    public ByteBuffer getFlippedBB() {
        return this.bb.flip();
    }

    public ByteBuffer getByteBuffer() {
        return this.bb;
    }
}

