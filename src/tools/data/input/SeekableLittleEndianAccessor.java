/*
 * Decompiled with CFR 0.152.
 */
package tools.data.input;

import tools.data.input.LittleEndianAccessor;

public interface SeekableLittleEndianAccessor
extends LittleEndianAccessor {
    public void seek(long var1);

    public long getPosition();
}

