/*
 * Decompiled with CFR 0.152.
 */
package handling;

import java.io.Serializable;

public interface MaplePacket
extends Serializable {
    public byte[] getBytes();

    public Runnable getOnSend();

    public void setOnSend(Runnable var1);
}

