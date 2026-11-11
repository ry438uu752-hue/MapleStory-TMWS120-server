/*
 * Decompiled with CFR 0.152.
 */
package client;

import server.MapleStatEffect;
import server.life.Element;

public interface ISkill {
    public int getId();

    public MapleStatEffect getEffect(int var1);

    public byte getMaxLevel();

    public int getAnimationTime();

    public boolean canBeLearnedBy(int var1);

    public boolean isFourthJob();

    public boolean getAction();

    public boolean isTimeLimited();

    public int getMasterLevel();

    public Element getElement();

    public boolean isBeginnerSkill();

    public boolean hasRequiredSkill();

    public boolean isInvisible();

    public boolean isChargeSkill();

    public int getRequiredSkillLevel();

    public int getRequiredSkillId();

    public String getName();
}

