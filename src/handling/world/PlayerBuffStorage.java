/*
 * Decompiled with CFR 0.152.
 */
package handling.world;

import client.MapleCoolDownValueHolder;
import client.MapleDiseaseValueHolder;
import handling.world.PlayerBuffValueHolder;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerBuffStorage
implements Serializable {
    private static final Map<Integer, List<PlayerBuffValueHolder>> buffs = new ConcurrentHashMap<Integer, List<PlayerBuffValueHolder>>();
    private static final Map<Integer, List<MapleCoolDownValueHolder>> coolDowns = new ConcurrentHashMap<Integer, List<MapleCoolDownValueHolder>>();
    private static final Map<Integer, List<MapleDiseaseValueHolder>> diseases = new ConcurrentHashMap<Integer, List<MapleDiseaseValueHolder>>();

    public static final void addBuffsToStorage(int chrid, List<PlayerBuffValueHolder> toStore) {
        buffs.put(chrid, toStore);
    }

    public static final void addCooldownsToStorage(int chrid, List<MapleCoolDownValueHolder> toStore) {
        coolDowns.put(chrid, toStore);
    }

    public static final void addDiseaseToStorage(int chrid, List<MapleDiseaseValueHolder> toStore) {
        diseases.put(chrid, toStore);
    }

    public static final List<PlayerBuffValueHolder> getBuffsFromStorage(int chrid) {
        return buffs.remove(chrid);
    }

    public static final List<MapleCoolDownValueHolder> getCooldownsFromStorage(int chrid) {
        return coolDowns.remove(chrid);
    }

    public static final List<MapleDiseaseValueHolder> getDiseaseFromStorage(int chrid) {
        return diseases.remove(chrid);
    }
}

