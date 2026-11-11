/*
 * Decompiled with CFR 0.152.
 */
package handling.channel.handler;

import client.ISkill;
import client.MapleCharacter;
import client.SkillFactory;
import constants.GameConstants;
import java.awt.Point;
import java.util.List;
import server.AutobanManager;
import server.MapleStatEffect;
import tools.AttackPair;

public class AttackInfo {
    public int skill;
    public int charge;
    public int lastAttackTickCount;
    public List<AttackPair> allDamage;
    public Point position;
    public byte hits;
    public byte targets;
    public byte tbyte;
    public byte display;
    public byte animation;
    public byte speed;
    public byte csstar;
    public byte AOE;
    public byte slot;
    public byte unk;
    public boolean real = true;

    public final MapleStatEffect getAttackEffect(MapleCharacter chr, int skillLevel, ISkill skill_) {
        if (GameConstants.isMulungSkill(this.skill) || GameConstants.isPyramidSkill(this.skill)) {
            skillLevel = 1;
        } else if (skillLevel <= 0) {
            return null;
        }
        if (GameConstants.isLinkedAranSkill(this.skill)) {
            ISkill skillLink = SkillFactory.getSkill(this.skill);
            if (this.display > 80 && !skillLink.getAction()) {
                AutobanManager.getInstance().autoban(chr.getClient(), "No delay hack, SkillID : " + this.skill);
                return null;
            }
            return skillLink.getEffect(skillLevel);
        }
        if (this.display > 80 && !skill_.getAction()) {
            AutobanManager.getInstance().autoban(chr.getClient(), "No delay hack, SkillID : " + this.skill);
            return null;
        }
        return skill_.getEffect(skillLevel);
    }
}

