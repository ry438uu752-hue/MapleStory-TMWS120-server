/*
 * Decompiled with CFR 0.152.
 */
package handling.channel;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import handling.MaplePacket;
import handling.world.CharacterTransfer;
import handling.world.CheaterData;
import handling.world.World;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import server.Timer;

public class PlayerStorage {
    private final ReentrantReadWriteLock mutex = new ReentrantReadWriteLock();
    private final Lock rL = this.mutex.readLock();
    private final Lock wL = this.mutex.writeLock();
    private final ReentrantReadWriteLock mutex2 = new ReentrantReadWriteLock();
    private final Lock rL2 = this.mutex2.readLock();
    private final Lock wL2 = this.mutex2.writeLock();
    private final Map<String, MapleCharacter> nameToChar = new HashMap<String, MapleCharacter>();
    private final Map<Integer, MapleCharacter> idToChar = new HashMap<Integer, MapleCharacter>();
    private final Map<Integer, CharacterTransfer> PendingCharacter = new HashMap<Integer, CharacterTransfer>();
    private int channel;

    public PlayerStorage(int channel) {
        this.channel = channel;
        Timer.PingTimer.getInstance().schedule(new PersistingTask(), 900000L);
    }

    public final Collection<MapleCharacter> getAllCharacters() {
        this.rL.lock();
        try {
            Collection<MapleCharacter> collection = Collections.unmodifiableCollection(this.idToChar.values());
            return collection;
        }
        finally {
            this.rL.unlock();
        }
    }

    public final void registerPlayer(MapleCharacter chr) {
        this.wL.lock();
        try {
            this.nameToChar.put(chr.getName().toLowerCase(), chr);
            this.idToChar.put(chr.getId(), chr);
        }
        finally {
            this.wL.unlock();
        }
        World.Find.register(chr.getId(), chr.getName(), this.channel);
    }

    public final void registerPendingPlayer(CharacterTransfer chr, int playerid) {
        this.wL2.lock();
        try {
            this.PendingCharacter.put(playerid, chr);
        }
        finally {
            this.wL2.unlock();
        }
    }

    public final void deregisterPlayer(MapleCharacter chr) {
        this.wL.lock();
        try {
            this.nameToChar.remove(chr.getName().toLowerCase());
            this.idToChar.remove(chr.getId());
        }
        finally {
            this.wL.unlock();
        }
        World.Find.forceDeregister(chr.getId(), chr.getName());
    }

    public final void deregisterPlayer(int idz, String namez) {
        this.wL.lock();
        try {
            this.nameToChar.remove(namez.toLowerCase());
            this.idToChar.remove(idz);
        }
        finally {
            this.wL.unlock();
        }
        World.Find.forceDeregister(idz, namez);
    }

    public final void deregisterPendingPlayer(int charid) {
        this.wL2.lock();
        try {
            this.PendingCharacter.remove(charid);
        }
        finally {
            this.wL2.unlock();
        }
    }

    public final CharacterTransfer getPendingCharacter(int charid) {
        CharacterTransfer toreturn;
        this.rL2.lock();
        try {
            toreturn = this.PendingCharacter.get(charid);
        }
        finally {
            this.rL2.unlock();
        }
        if (toreturn != null) {
            this.deregisterPendingPlayer(charid);
        }
        return toreturn;
    }

    public final MapleCharacter getCharacterByName(String name) {
        this.rL.lock();
        try {
            MapleCharacter mapleCharacter = this.nameToChar.get(name.toLowerCase());
            return mapleCharacter;
        }
        finally {
            this.rL.unlock();
        }
    }

    public final MapleCharacter getCharacterById(int id) {
        this.rL.lock();
        try {
            MapleCharacter mapleCharacter = this.idToChar.get(id);
            return mapleCharacter;
        }
        finally {
            this.rL.unlock();
        }
    }

    public final int getConnectedClients() {
        return this.idToChar.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final List<CheaterData> getCheaters() {
        ArrayList<CheaterData> cheaters = new ArrayList<CheaterData>();
        this.rL.lock();
        try {
            for (MapleCharacter chr : this.nameToChar.values()) {
                if (chr.getCheatTracker().getPoints() <= 0) continue;
                cheaters.add(new CheaterData(chr.getCheatTracker().getPoints(), MapleCharacterUtil.makeMapleReadable(chr.getName()) + " (" + chr.getCheatTracker().getPoints() + ") " + chr.getCheatTracker().getSummary()));
            }
        }
        finally {
            this.rL.unlock();
        }
        return cheaters;
    }

    public final void disconnectAll() {
        this.disconnectAll(false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void disconnectAll(boolean checkGM) {
        this.wL.lock();
        try {
            Iterator<MapleCharacter> itr = this.nameToChar.values().iterator();
            while (itr.hasNext()) {
                MapleCharacter chr = itr.next();
                if (chr.isGM() && checkGM) continue;
                chr.getClient().disconnect(false, false, true);
                chr.getClient().getSession().close();
                World.Find.forceDeregister(chr.getId(), chr.getName());
                itr.remove();
            }
        }
        finally {
            this.wL.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final String getOnlinePlayers(boolean byGM) {
        StringBuilder sb = new StringBuilder();
        if (byGM) {
            this.rL.lock();
            try {
                Iterator<MapleCharacter> itr = this.nameToChar.values().iterator();
                while (itr.hasNext()) {
                    sb.append(MapleCharacterUtil.makeMapleReadable(itr.next().getName()));
                    sb.append(", ");
                }
            }
            finally {
                this.rL.unlock();
            }
        }
        this.rL.lock();
        try {
            for (MapleCharacter chr : this.nameToChar.values()) {
                if (chr.isGM()) continue;
                sb.append(MapleCharacterUtil.makeMapleReadable(chr.getName()));
                sb.append(", ");
            }
        }
        finally {
            this.rL.unlock();
        }
        return sb.toString();
    }

    public final void broadcastPacket(MaplePacket data) {
        this.rL.lock();
        try {
            Iterator<MapleCharacter> itr = this.nameToChar.values().iterator();
            while (itr.hasNext()) {
                itr.next().getClient().getSession().write((Object)data);
            }
        }
        finally {
            this.rL.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void broadcastSmegaPacket(MaplePacket data) {
        this.rL.lock();
        try {
            for (MapleCharacter chr : this.nameToChar.values()) {
                if (!chr.getClient().isLoggedIn() || !chr.getSmega()) continue;
                chr.getClient().getSession().write((Object)data);
            }
        }
        finally {
            this.rL.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void broadcastGMPacket(MaplePacket data) {
        this.rL.lock();
        try {
            for (MapleCharacter chr : this.nameToChar.values()) {
                if (!chr.getClient().isLoggedIn() || !chr.isGM()) continue;
                chr.getClient().getSession().write((Object)data);
            }
        }
        finally {
            this.rL.unlock();
        }
    }

    public class PersistingTask
    implements Runnable {
        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            PlayerStorage.this.wL2.lock();
            try {
                long currenttime = System.currentTimeMillis();
                Iterator<Map.Entry<Integer, CharacterTransfer>> itr = PlayerStorage.this.PendingCharacter.entrySet().iterator();
                while (itr.hasNext()) {
                    if (currenttime - ((CharacterTransfer)itr.next().getValue()).TranferTime <= 40000L) continue;
                    itr.remove();
                }
                Timer.PingTimer.getInstance().schedule(new PersistingTask(), 900000L);
            }
            finally {
                PlayerStorage.this.wL2.unlock();
            }
        }
    }
}

