/*
 * Decompiled with CFR 0.152.
 */
package tools.data.output;

import handling.ByteArrayMaplePacket;
import handling.MaplePacket;
import java.io.ByteArrayOutputStream;
import server.ServerProperties;
import tools.HexTool;
import tools.data.output.BAOSByteOutputStream;
import tools.data.output.GenericLittleEndianWriter;

public class MaplePacketLittleEndianWriter
extends GenericLittleEndianWriter {
    private final ByteArrayOutputStream baos;
    private static boolean debugMode = Boolean.parseBoolean(ServerProperties.getProperty("tms.Debug", "false"));

    public MaplePacketLittleEndianWriter() {
        this(32);
    }

    public MaplePacketLittleEndianWriter(int size) {
        this.baos = new ByteArrayOutputStream(size);
        this.setByteOutputStream(new BAOSByteOutputStream(this.baos));
    }

    public final MaplePacket getPacket() {
        if (debugMode) {
            ByteArrayMaplePacket packet = new ByteArrayMaplePacket(this.baos.toByteArray());
            System.out.println("Packet to be sent:\n" + ((Object)packet).toString());
        }
        return new ByteArrayMaplePacket(this.baos.toByteArray());
    }

    public final String toString() {
        return HexTool.toString(this.baos.toByteArray());
    }
}

