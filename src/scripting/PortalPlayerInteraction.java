/*
 * Decompiled with CFR 0.152.
 */
package scripting;

import client.MapleClient;
import scripting.AbstractPlayerInteraction;
import server.MaplePortal;

public class PortalPlayerInteraction
extends AbstractPlayerInteraction {
    private final MaplePortal portal;

    public PortalPlayerInteraction(MapleClient c, MaplePortal portal) {
        super(c);
        this.portal = portal;
    }

    public final MaplePortal getPortal() {
        return this.portal;
    }

    public final void inFreeMarket() {
        if (this.getPlayer().getLevel() >= 10) {
            this.saveLocation("FREE_MARKET");
            this.playPortalSE();
            this.warp(910000000, "st00");
        } else {
            this.playerMessage(5, "\u4f60\u9700\u898110\u7d1a\u624d\u53ef\u4ee5\u9032\u5165\u81ea\u7531\u5e02\u5834");
        }
    }

    @Override
    public void spawnMonster(int id) {
        this.spawnMonster(id, 1, this.portal.getPosition());
    }

    @Override
    public void spawnMonster(int id, int qty) {
        this.spawnMonster(id, qty, this.portal.getPosition());
    }
}

