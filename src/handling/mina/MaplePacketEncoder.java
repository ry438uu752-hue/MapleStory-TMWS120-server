/*
 * Decompiled with CFR 0.152.
 */
package handling.mina;

import client.MapleClient;
import handling.MaplePacket;
import java.util.concurrent.locks.Lock;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import tools.MapleAESOFB;

public class MaplePacketEncoder
implements ProtocolEncoder {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        MapleClient client = (MapleClient)session.getAttribute("CLIENT");
        if (client != null) {
            MapleAESOFB send_crypto = client.getSendCrypto();
            byte[] inputInitialPacket = ((MaplePacket)message).getBytes();
            byte[] unencrypted = new byte[inputInitialPacket.length];
            System.arraycopy(inputInitialPacket, 0, unencrypted, 0, inputInitialPacket.length);
            byte[] ret = new byte[unencrypted.length + 4];
            Lock mutex = client.getLock();
            mutex.lock();
            try {
                byte[] header = send_crypto.getPacketHeader(unencrypted.length);
                send_crypto.crypt(unencrypted);
                System.arraycopy(header, 0, ret, 0, 4);
            }
            finally {
                mutex.unlock();
            }
            System.arraycopy(unencrypted, 0, ret, 4, unencrypted.length);
            out.write(ByteBuffer.wrap((byte[])ret));
        } else {
            out.write(ByteBuffer.wrap((byte[])((MaplePacket)message).getBytes()));
        }
    }

    public void dispose(IoSession session) throws Exception {
    }
}

