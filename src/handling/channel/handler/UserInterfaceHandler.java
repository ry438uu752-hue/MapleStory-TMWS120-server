/*
 * Decompiled with CFR 0.152.
 */
package handling.channel.handler;

import client.MapleClient;
import scripting.EventManager;
import scripting.NPCScriptManager;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

public class UserInterfaceHandler {
    public static final void CygnusSummon_NPCRequest(MapleClient c) {
        if (c.getPlayer().getJob() == 2000) {
            NPCScriptManager.getInstance().start(c, 1202000);
        } else if (c.getPlayer().getJob() == 1000) {
            NPCScriptManager.getInstance().start(c, 1101008);
        }
    }

    public static final void InGame_Poll(SeekableLittleEndianAccessor slea, MapleClient c) {
    }

    public static final void ShipObjectRequest(int mapid, MapleClient c) {
        int effect = 3;
        switch (mapid) {
            case 101000300: 
            case 200000111: {
                EventManager em = c.getChannelServer().getEventSM().getEventManager("Boats");
                if (em == null || !em.getProperty("docked").equals("true")) break;
                effect = 1;
                break;
            }
            case 200000121: 
            case 220000110: {
                EventManager em = c.getChannelServer().getEventSM().getEventManager("Trains");
                if (em == null || !em.getProperty("docked").equals("true")) break;
                effect = 1;
                break;
            }
            case 200000151: 
            case 260000100: {
                EventManager em = c.getChannelServer().getEventSM().getEventManager("Geenie");
                if (em == null || !em.getProperty("docked").equals("true")) break;
                effect = 1;
                break;
            }
            case 200000131: 
            case 240000110: {
                EventManager em = c.getChannelServer().getEventSM().getEventManager("Flight");
                if (em == null || !em.getProperty("docked").equals("true")) break;
                effect = 1;
                break;
            }
            case 200090000: 
            case 200090010: {
                EventManager em = c.getChannelServer().getEventSM().getEventManager("Boats");
                if (em != null && em.getProperty("haveBalrog").equals("true")) {
                    effect = 1034;
                    c.getSession().write((Object)MaplePacketCreator.boatEffect(effect));
                    return;
                }
                return;
            }
            default: {
                System.out.println("Unhandled ship object, MapID : " + mapid);
            }
        }
        c.getSession().write((Object)MaplePacketCreator.boatPacket(effect));
    }
}

