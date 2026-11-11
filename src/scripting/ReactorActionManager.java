/*
 * Decompiled with CFR 0.152.
 */
package scripting;

import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import handling.channel.ChannelServer;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import scripting.AbstractPlayerInteraction;
import scripting.ReactorScriptManager;
import server.MapleCarnivalFactory;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.maps.MapleReactor;
import server.maps.ReactorDropEntry;

public class ReactorActionManager
extends AbstractPlayerInteraction {
    private MapleReactor reactor;

    public ReactorActionManager(MapleClient c, MapleReactor reactor) {
        super(c);
        this.reactor = reactor;
    }

    public void dropItems() {
        this.dropItems(false, 0, 0, 0, 0);
    }

    public void dropItems(boolean meso, int mesoChance, int minMeso, int maxMeso) {
        this.dropItems(meso, mesoChance, minMeso, maxMeso, 0);
    }

    public void dropItems(boolean meso, int mesoChance, int minMeso, int maxMeso, int minItems) {
        List<ReactorDropEntry> chances = ReactorScriptManager.getInstance().getDrops(this.reactor.getReactorId());
        LinkedList<ReactorDropEntry> items = new LinkedList<ReactorDropEntry>();
        if (meso && Math.random() < 1.0 / (double)mesoChance) {
            items.add(new ReactorDropEntry(0, mesoChance, -1));
        }
        int numItems = 0;
        for (ReactorDropEntry d : chances) {
            if (!(Math.random() < 1.0 / (double)d.chance) || d.questid > 0 && this.getPlayer().getQuestStatus(d.questid) != 1) continue;
            ++numItems;
            items.add(d);
        }
        while (items.size() < minItems) {
            items.add(new ReactorDropEntry(0, mesoChance, -1));
            ++numItems;
        }
        Point dropPos = this.reactor.getPosition();
        dropPos.x -= 12 * numItems;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        for (ReactorDropEntry d : items) {
            if (d.itemId == 0) {
                int range = maxMeso - minMeso;
                int mesoDrop = Randomizer.nextInt(range) + minMeso * ChannelServer.getInstance(this.getClient().getChannel()).getMesoRate();
                this.reactor.getMap().spawnMesoDrop(mesoDrop, dropPos, this.reactor, this.getPlayer(), false, (byte)0);
            } else {
                Item drop = GameConstants.getInventoryType(d.itemId) != MapleInventoryType.EQUIP ? new Item(d.itemId, (short)0, (short)1, (byte)0) : ii.randomizeStats((Equip)ii.getEquipById(d.itemId));
                this.reactor.getMap().spawnItemDrop(this.reactor, this.getPlayer(), drop, dropPos, false, false);
            }
            dropPos.x += 25;
        }
    }

    @Override
    public void spawnNpc(int npcId) {
        this.spawnNpc(npcId, this.getPosition());
    }

    public Point getPosition() {
        Point pos = this.reactor.getPosition();
        pos.y -= 10;
        return pos;
    }

    public MapleReactor getReactor() {
        return this.reactor;
    }

    public void spawnZakum() {
        this.reactor.getMap().spawnZakum(this.getPosition().x, this.getPosition().y);
    }

    public void spawnFakeMonster(int id) {
        this.spawnFakeMonster(id, 1, this.getPosition());
    }

    public void spawnFakeMonster(int id, int x, int y) {
        this.spawnFakeMonster(id, 1, new Point(x, y));
    }

    public void spawnFakeMonster(int id, int qty) {
        this.spawnFakeMonster(id, qty, this.getPosition());
    }

    public void spawnFakeMonster(int id, int qty, int x, int y) {
        this.spawnFakeMonster(id, qty, new Point(x, y));
    }

    private void spawnFakeMonster(int id, int qty, Point pos) {
        for (int i = 0; i < qty; ++i) {
            this.reactor.getMap().spawnFakeMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), pos);
        }
    }

    public void killAll() {
        this.reactor.getMap().killAllMonsters(true);
    }

    public void killMonster(int monsId) {
        this.reactor.getMap().killMonster(monsId);
    }

    @Override
    public void spawnMonster(int id) {
        this.spawnMonster(id, 1, this.getPosition());
    }

    @Override
    public void spawnMonster(int id, int qty) {
        this.spawnMonster(id, qty, this.getPosition());
    }

    public void dispelAllMonsters(int num) {
        MapleCarnivalFactory.MCSkill skil = MapleCarnivalFactory.getInstance().getGuardian(num);
        if (skil != null) {
            for (MapleMonster mons : this.getMap().getAllMonstersThreadsafe()) {
                mons.dispelSkill(skil.getSkill());
            }
        }
    }
}

