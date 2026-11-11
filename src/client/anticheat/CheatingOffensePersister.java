/*
 * Decompiled with CFR 0.152.
 */
package client.anticheat;

import client.anticheat.CheatingOffenseEntry;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import server.Timer;

public class CheatingOffensePersister {
    private static final CheatingOffensePersister instance = new CheatingOffensePersister();
    private final Set<CheatingOffenseEntry> toPersist = new LinkedHashSet<CheatingOffenseEntry>();
    private final Lock mutex = new ReentrantLock();

    private CheatingOffensePersister() {
        Timer.CheatTimer.getInstance().register(new PersistingTask(), 61000L);
    }

    public static CheatingOffensePersister getInstance() {
        return instance;
    }

    public void persistEntry(CheatingOffenseEntry coe) {
        this.mutex.lock();
        try {
            this.toPersist.remove(coe);
            this.toPersist.add(coe);
        }
        finally {
            this.mutex.unlock();
        }
    }

    public class PersistingTask
    implements Runnable {
        @Override
        public void run() {
            CheatingOffensePersister.this.mutex.lock();
            try {
                CheatingOffensePersister.this.toPersist.clear();
            }
            finally {
                CheatingOffensePersister.this.mutex.unlock();
            }
        }
    }
}

