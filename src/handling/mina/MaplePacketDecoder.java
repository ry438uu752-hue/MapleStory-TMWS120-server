/*
 * Decompiled with CFR 0.152.
 */
package handling.mina;

import client.MapleClient;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import tools.MapleAESOFB;

public class MaplePacketDecoder
extends CumulativeProtocolDecoder {
    public static final String DECODER_STATE_KEY = MaplePacketDecoder.class.getName() + ".STATE";

    protected boolean doDecode(IoSession session, ByteBuffer in, ProtocolDecoderOutput out) throws Exception {
        DecoderState decoderState = (DecoderState)session.getAttribute(DECODER_STATE_KEY);
        MapleClient client = (MapleClient)session.getAttribute("CLIENT");
        if (decoderState.packetlength == -1) {
            if (in.remaining() >= 4) {
                int packetHeader = in.getInt();
                if (!client.getReceiveCrypto().checkPacket(packetHeader)) {
                    session.close();
                    return false;
                }
                decoderState.packetlength = MapleAESOFB.getPacketLength(packetHeader);
            } else {
                return false;
            }
        }
        if (in.remaining() >= decoderState.packetlength) {
            byte[] decryptedPacket = new byte[decoderState.packetlength];
            in.get(decryptedPacket, 0, decoderState.packetlength);
            decoderState.packetlength = -1;
            client.getReceiveCrypto().crypt(decryptedPacket);
            out.write((Object)decryptedPacket);
            return true;
        }
        return false;
    }

    public static class DecoderState {
        public int packetlength = -1;
    }
}

