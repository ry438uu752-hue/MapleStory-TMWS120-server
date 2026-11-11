/*
 * Decompiled with CFR 0.152.
 */
package server;

import constants.GameConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import server.Randomizer;

public class RandomRewards {
    private static final RandomRewards instance = new RandomRewards();
    private List<Integer> compiledGold = null;
    private List<Integer> compiledSilver = null;
    private List<Integer> compiledFishing = null;
    private List<Integer> compiledEvent = null;
    private List<Integer> compiledEventC = null;
    private List<Integer> compiledEventB = null;
    private List<Integer> compiledEventA = null;

    public static RandomRewards getInstance() {
        return instance;
    }

    protected RandomRewards() {
        System.out.println("Loading RandomRewards :::");
        ArrayList<Integer> returnArray = new ArrayList<Integer>();
        this.processRewards(returnArray, GameConstants.goldrewards);
        this.compiledGold = returnArray;
        returnArray = new ArrayList();
        this.processRewards(returnArray, GameConstants.silverrewards);
        this.compiledSilver = returnArray;
        returnArray = new ArrayList();
        this.processRewards(returnArray, GameConstants.fishingReward);
        this.compiledFishing = returnArray;
        returnArray = new ArrayList();
        this.processRewards(returnArray, GameConstants.eventCommonReward);
        this.compiledEventC = returnArray;
        returnArray = new ArrayList();
        this.processRewards(returnArray, GameConstants.eventUncommonReward);
        this.compiledEventB = returnArray;
        returnArray = new ArrayList();
        this.processRewards(returnArray, GameConstants.eventRareReward);
        this.compiledEventA = returnArray;
        returnArray = new ArrayList();
        this.processRewards(returnArray, GameConstants.eventSuperReward);
        this.compiledEvent = returnArray;
    }

    private final void processRewards(List<Integer> returnArray, int[] list) {
        int lastitem = 0;
        for (int i = 0; i < list.length; ++i) {
            if (i % 2 == 0) {
                lastitem = list[i];
                continue;
            }
            for (int j = 0; j < list[i]; ++j) {
                returnArray.add(lastitem);
            }
        }
        Collections.shuffle(returnArray);
    }

    public final int getGoldBoxReward() {
        return this.compiledGold.get(Randomizer.nextInt(this.compiledGold.size()));
    }

    public final int getSilverBoxReward() {
        return this.compiledSilver.get(Randomizer.nextInt(this.compiledSilver.size()));
    }

    public final int getFishingReward() {
        return this.compiledFishing.get(Randomizer.nextInt(this.compiledFishing.size()));
    }

    public final int getEventReward() {
        int chance = Randomizer.nextInt(100);
        if (chance < 50) {
            return this.compiledEventC.get(Randomizer.nextInt(this.compiledEventC.size()));
        }
        if (chance < 80) {
            return this.compiledEventB.get(Randomizer.nextInt(this.compiledEventB.size()));
        }
        if (chance < 95) {
            return this.compiledEventA.get(Randomizer.nextInt(this.compiledEventA.size()));
        }
        return this.compiledEvent.get(Randomizer.nextInt(this.compiledEvent.size()));
    }
}

