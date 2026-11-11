/*
 * Decompiled with CFR 0.152.
 */
package handling.channel.handler;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import handling.world.MaplePartyCharacter;
import handling.world.World;
import handling.world.family.MapleFamily;
import handling.world.family.MapleFamilyBuff;
import handling.world.family.MapleFamilyCharacter;
import java.util.List;
import server.maps.FieldLimitType;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.FamilyPacket;

public class FamilyHandler {
    public static final void RequestFamily(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
        if (chr != null) {
            c.getSession().write((Object)FamilyPacket.getFamilyPedigree(chr));
        }
    }

    public static final void OpenFamily(SeekableLittleEndianAccessor slea, MapleClient c) {
        c.getSession().write((Object)FamilyPacket.getFamilyInfo(c.getPlayer()));
    }

    public static final void UseFamily(SeekableLittleEndianAccessor slea, MapleClient c) {
        boolean success;
        int type = slea.readInt();
        MapleFamilyBuff.MapleFamilyBuffEntry entry = MapleFamilyBuff.getBuffEntry(type);
        if (entry == null) {
            return;
        }
        boolean bl = success = c.getPlayer().getFamilyId() > 0 && c.getPlayer().canUseFamilyBuff(entry) && c.getPlayer().getCurrentRep() > entry.rep;
        if (!success) {
            return;
        }
        MapleCharacter victim = null;
        switch (type) {
            case 0: {
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
                if (FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) || !c.getPlayer().isAlive()) {
                    c.getPlayer().dropMessage(5, "\u53ec\u559a\u5931\u6557\uff0c\u4f60\u73fe\u5728\u7684\u5730\u5716\u6216\u72c0\u614b\u7121\u6cd5\u5141\u8a31\u53ec\u559a");
                    success = false;
                    break;
                }
                if (victim == null || victim.isGM() && !c.getPlayer().isGM()) {
                    c.getPlayer().dropMessage(1, "\u932f\u8aa4\u7684\u540d\u7a31(\u4e0d\u5728\u76f8\u540c\u983b\u9053?)");
                    success = false;
                    break;
                }
                if (victim.getFamilyId() == c.getPlayer().getFamilyId() && !FieldLimitType.VipRock.check(victim.getMap().getFieldLimit()) && victim.getId() != c.getPlayer().getId()) {
                    c.getPlayer().changeMap(victim.getMap(), victim.getMap().getPortal(0));
                    break;
                }
                c.getPlayer().dropMessage(5, "\u53ec\u559a\u5931\u6557\uff0c\u4f60\u73fe\u5728\u7684\u5730\u5716\u6216\u72c0\u614b\u7121\u6cd5\u5141\u8a31\u53ec\u559a");
                success = false;
                break;
            }
            case 1: {
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
                if (FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) || !c.getPlayer().isAlive()) {
                    c.getPlayer().dropMessage(5, "\u53ec\u559a\u5931\u6557\uff0c\u4f60\u73fe\u5728\u7684\u5730\u5716\u6216\u72c0\u614b\u7121\u6cd5\u5141\u8a31\u53ec\u559a");
                } else if (victim == null || victim.isGM() && !c.getPlayer().isGM()) {
                    c.getPlayer().dropMessage(1, "\u932f\u8aa4\u7684\u540d\u7a31(\u4e0d\u5728\u76f8\u540c\u983b\u9053?)");
                } else if (victim.getTeleportName().length() > 0) {
                    c.getPlayer().dropMessage(1, "\u5df2\u7d93\u6709\u5176\u4ed6\u73a9\u5bb6\u8acb\u6c42\u50b3\u9001");
                } else if (victim.getFamilyId() == c.getPlayer().getFamilyId() && !FieldLimitType.VipRock.check(victim.getMap().getFieldLimit()) && victim.getId() != c.getPlayer().getId()) {
                    victim.getClient().getSession().write((Object)FamilyPacket.familySummonRequest(c.getPlayer().getName(), c.getPlayer().getMap().getMapName()));
                    victim.setTeleportName(c.getPlayer().getName());
                } else {
                    c.getPlayer().dropMessage(5, "\u53ec\u559a\u5931\u6557\uff0c\u4f60\u73fe\u5728\u7684\u5730\u5716\u6216\u72c0\u614b\u7121\u6cd5\u5141\u8a31\u53ec\u559a");
                }
                return;
            }
            case 4: {
                MapleFamily fam = World.Family.getFamily(c.getPlayer().getFamilyId());
                List<MapleFamilyCharacter> chrs = fam.getMFC(c.getPlayer().getId()).getOnlineJuniors(fam);
                if (chrs.size() < 7) {
                    success = false;
                    break;
                }
                for (MapleFamilyCharacter chrz : chrs) {
                    int chr = World.Find.findChannel(chrz.getId());
                    if (chr == -1) continue;
                    MapleCharacter chrr = World.getStorage(chr).getCharacterById(chrz.getId());
                    entry.applyTo(chrr);
                }
                break;
            }
            case 2: 
            case 3: 
            case 5: 
            case 6: 
            case 7: 
            case 8: {
                entry.applyTo(c.getPlayer());
                break;
            }
            case 9: 
            case 10: {
                entry.applyTo(c.getPlayer());
                if (c.getPlayer().getParty() == null) break;
                for (MaplePartyCharacter mpc : c.getPlayer().getParty().getMembers()) {
                    MapleCharacter chr;
                    if (mpc.getId() == c.getPlayer().getId() || (chr = c.getPlayer().getMap().getCharacterById(mpc.getId())) == null) continue;
                    entry.applyTo(chr);
                }
                break;
            }
        }
        if (success) {
            c.getPlayer().setCurrentRep(c.getPlayer().getCurrentRep() - entry.rep);
            c.getSession().write((Object)FamilyPacket.changeRep(-entry.rep));
            c.getPlayer().useFamilyBuff(entry);
        } else {
            c.getPlayer().dropMessage(5, "An error occured.");
        }
    }

    public static final void FamilyOperation(SeekableLittleEndianAccessor slea, MapleClient c) {
        if (c.getPlayer() == null) {
            return;
        }
        MapleCharacter addChr = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
        if (addChr == null) {
            c.getPlayer().dropMessage(1, "\u8a72\u73a9\u5bb6\u4e0d\u5b58\u5728\u6216\u8005\u4e0d\u518d\u7dda\u4e0a");
        } else if (addChr.getFamilyId() == c.getPlayer().getFamilyId() && addChr.getFamilyId() > 0) {
            c.getPlayer().dropMessage(1, "\u4f60\u5011\u5df2\u7d93\u662f\u540c\u4e00\u500b\u5bb6\u65cf\u4e86");
        } else if (addChr.getMapId() != c.getPlayer().getMapId()) {
            c.getPlayer().dropMessage(1, "\u8ddf\u96a8\u8005\u5fc5\u9808\u5728\u540c\u5730\u5716");
        } else if (addChr.getSeniorId() != 0) {
            c.getPlayer().dropMessage(1, "\u8a72\u73a9\u5bb6\u5df2\u7d93\u6709\u5bb6\u65cf");
        } else if (addChr.getLevel() >= c.getPlayer().getLevel()) {
            c.getPlayer().dropMessage(1, "\u8a72\u73a9\u5bb6\u7b49\u7d1a\u9ad8\u904e\u4f60");
        } else if (addChr.getLevel() < c.getPlayer().getLevel() - 20) {
            c.getPlayer().dropMessage(1, "\u96d9\u65b9\u7b49\u7d1a\u5dee\u7570\u8d85\u904e\u4e8c\u5341");
        } else if (addChr.getLevel() < 10) {
            c.getPlayer().dropMessage(1, "\u8ddf\u96a8\u8005\u7b49\u7d1a\u5fc5\u9808\u572810\u7d1a\u4ee5\u4e0a");
        } else if (c.getPlayer().getJunior1() > 0 && c.getPlayer().getJunior2() > 0) {
            c.getPlayer().dropMessage(1, "\u4f60\u5df2\u7d93\u6709\u5169\u500b\u8ddf\u96a8\u8005");
        } else if (c.getPlayer().isGM() || !addChr.isGM()) {
            addChr.getClient().getSession().write((Object)FamilyPacket.sendFamilyInvite(c.getPlayer().getId(), c.getPlayer().getLevel(), c.getPlayer().getJob(), c.getPlayer().getName()));
        }
        c.getSession().write((Object)MaplePacketCreator.enableActions());
    }

    public static final void FamilyPrecept(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleFamily fam = World.Family.getFamily(c.getPlayer().getFamilyId());
        if (fam == null || fam.getLeaderId() != c.getPlayer().getId()) {
            return;
        }
        fam.setNotice(slea.readMapleAsciiString());
        c.getPlayer().dropMessage(1, "\u91cd\u958b\u5bb6\u65cf\u8996\u7a97\u5373\u53ef\u5957\u7528.");
    }

    public static final void FamilySummon(SeekableLittleEndianAccessor slea, MapleClient c) {
        int TYPE = 1;
        MapleFamilyBuff.MapleFamilyBuffEntry cost = MapleFamilyBuff.getBuffEntry(TYPE);
        MapleCharacter tt = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
        if (c.getPlayer().getFamilyId() > 0 && tt != null && tt.getFamilyId() == c.getPlayer().getFamilyId() && !FieldLimitType.VipRock.check(tt.getMap().getFieldLimit()) && !FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) && c.getPlayer().isAlive() && tt.isAlive() && tt.canUseFamilyBuff(cost) && c.getPlayer().getTeleportName().equals(tt.getName()) && tt.getCurrentRep() > cost.rep && c.getPlayer().getEventInstance() == null && tt.getEventInstance() == null) {
            boolean accepted;
            boolean bl = accepted = slea.readByte() > 0;
            if (accepted) {
                c.getPlayer().changeMap(tt.getMap(), tt.getMap().getPortal(0));
                tt.setCurrentRep(tt.getCurrentRep() - cost.rep);
                tt.getClient().getSession().write((Object)FamilyPacket.changeRep(-cost.rep));
                tt.useFamilyBuff(cost);
            } else {
                tt.dropMessage(5, "\u53ec\u559a\u5931\u6557\uff0c\u4f60\u73fe\u5728\u7684\u5730\u5716\u6216\u72c0\u614b\u7121\u6cd5\u5141\u8a31\u53ec\u559a");
            }
        } else {
            c.getPlayer().dropMessage(5, "\u53ec\u559a\u5931\u6557\uff0c\u4f60\u73fe\u5728\u7684\u5730\u5716\u6216\u72c0\u614b\u7121\u6cd5\u5141\u8a31\u53ec\u559a");
        }
        c.getPlayer().setTeleportName("");
    }

    public static final void DeleteJunior(SeekableLittleEndianAccessor slea, MapleClient c) {
        boolean junior2;
        int juniorid = slea.readInt();
        if (c.getPlayer().getFamilyId() <= 0 || juniorid <= 0 || c.getPlayer().getJunior1() != juniorid && c.getPlayer().getJunior2() != juniorid) {
            return;
        }
        MapleFamily fam = World.Family.getFamily(c.getPlayer().getFamilyId());
        MapleFamilyCharacter other = fam.getMFC(juniorid);
        if (other == null) {
            return;
        }
        MapleFamilyCharacter oth = c.getPlayer().getMFC();
        boolean bl = junior2 = oth.getJunior2() == juniorid;
        if (junior2) {
            oth.setJunior2(0);
        } else {
            oth.setJunior1(0);
        }
        c.getPlayer().saveFamilyStatus();
        other.setSeniorId(0);
        MapleFamily.setOfflineFamilyStatus(other.getFamilyId(), other.getSeniorId(), other.getJunior1(), other.getJunior2(), other.getCurrentRep(), other.getTotalRep(), other.getId());
        MapleCharacterUtil.sendNote(other.getName(), c.getPlayer().getName(), c.getPlayer().getName() + " \u89e3\u6563\u4e86\u5bb6\u65cf", 0);
        if (!fam.splitFamily(juniorid, other)) {
            if (!junior2) {
                fam.resetDescendants();
            }
            fam.resetPedigree();
        }
        c.getPlayer().dropMessage(1, "\u8e22\u51fa\u4e86 (" + other.getName() + ").");
        c.getSession().write((Object)MaplePacketCreator.enableActions());
    }

    public static final void DeleteSenior(SeekableLittleEndianAccessor slea, MapleClient c) {
        boolean junior2;
        if (c.getPlayer().getFamilyId() <= 0 || c.getPlayer().getSeniorId() <= 0) {
            return;
        }
        MapleFamily fam = World.Family.getFamily(c.getPlayer().getFamilyId());
        MapleFamilyCharacter mgc = fam.getMFC(c.getPlayer().getSeniorId());
        MapleFamilyCharacter mgc_ = c.getPlayer().getMFC();
        mgc_.setSeniorId(0);
        boolean bl = junior2 = mgc.getJunior2() == c.getPlayer().getId();
        if (junior2) {
            mgc.setJunior2(0);
        } else {
            mgc.setJunior1(0);
        }
        MapleFamily.setOfflineFamilyStatus(mgc.getFamilyId(), mgc.getSeniorId(), mgc.getJunior1(), mgc.getJunior2(), mgc.getCurrentRep(), mgc.getTotalRep(), mgc.getId());
        c.getPlayer().saveFamilyStatus();
        MapleCharacterUtil.sendNote(mgc.getName(), c.getPlayer().getName(), c.getPlayer().getName() + " \u96e2\u958b\u4f60\u7684\u5bb6\u65cf", 0);
        if (!fam.splitFamily(c.getPlayer().getId(), mgc_)) {
            if (!junior2) {
                fam.resetDescendants();
            }
            fam.resetPedigree();
        }
        c.getPlayer().dropMessage(1, "\u9000\u51fa\u4e86 (" + mgc.getName() + ") \u7684\u5bb6\u65cf.");
        c.getSession().write((Object)MaplePacketCreator.enableActions());
    }

    public static final void AcceptFamily(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter inviter = c.getPlayer().getMap().getCharacterById(slea.readInt());
        if (inviter != null && c.getPlayer().getSeniorId() == 0 && (c.getPlayer().isGM() || !inviter.isHidden()) && inviter.getLevel() - 20 <= c.getPlayer().getLevel() && inviter.getLevel() >= 10 && inviter.getName().equals(slea.readMapleAsciiString()) && inviter.getNoJuniors() < 2 && c.getPlayer().getLevel() >= 10) {
            boolean accepted = slea.readByte() > 0;
            inviter.getClient().getSession().write((Object)FamilyPacket.sendFamilyJoinResponse(accepted, c.getPlayer().getName()));
            if (accepted) {
                int oldj2;
                c.getSession().write((Object)FamilyPacket.getSeniorMessage(inviter.getName()));
                int old = c.getPlayer().getMFC() == null ? 0 : c.getPlayer().getMFC().getFamilyId();
                int oldj1 = c.getPlayer().getMFC() == null ? 0 : c.getPlayer().getMFC().getJunior1();
                int n = oldj2 = c.getPlayer().getMFC() == null ? 0 : c.getPlayer().getMFC().getJunior2();
                if (inviter.getFamilyId() > 0 && World.Family.getFamily(inviter.getFamilyId()) != null) {
                    MapleFamily fam = World.Family.getFamily(inviter.getFamilyId());
                    c.getPlayer().setFamily(old <= 0 ? inviter.getFamilyId() : old, inviter.getId(), oldj1 <= 0 ? 0 : oldj1, oldj2 <= 0 ? 0 : oldj2);
                    MapleFamilyCharacter mf = inviter.getMFC();
                    if (mf.getJunior1() > 0) {
                        mf.setJunior2(c.getPlayer().getId());
                    } else {
                        mf.setJunior1(c.getPlayer().getId());
                    }
                    inviter.saveFamilyStatus();
                    if (old > 0 && World.Family.getFamily(old) != null) {
                        MapleFamily.mergeFamily(fam, World.Family.getFamily(old));
                    } else {
                        c.getPlayer().setFamily(inviter.getFamilyId(), inviter.getId(), oldj1 <= 0 ? 0 : oldj1, oldj2 <= 0 ? 0 : oldj2);
                        fam.setOnline(c.getPlayer().getId(), true, c.getChannel());
                        c.getPlayer().saveFamilyStatus();
                    }
                    if (fam != null) {
                        if (inviter.getNoJuniors() == 1 || old > 0) {
                            fam.resetDescendants();
                        }
                        fam.resetPedigree();
                    }
                } else {
                    int id = MapleFamily.createFamily(inviter.getId());
                    if (id > 0) {
                        MapleFamily.setOfflineFamilyStatus(id, 0, c.getPlayer().getId(), 0, inviter.getCurrentRep(), inviter.getTotalRep(), inviter.getId());
                        MapleFamily.setOfflineFamilyStatus(id, inviter.getId(), oldj1 <= 0 ? 0 : oldj1, oldj2 <= 0 ? 0 : oldj2, c.getPlayer().getCurrentRep(), c.getPlayer().getTotalRep(), c.getPlayer().getId());
                        inviter.setFamily(id, 0, c.getPlayer().getId(), 0);
                        c.getPlayer().setFamily(id, inviter.getId(), oldj1 <= 0 ? 0 : oldj1, oldj2 <= 0 ? 0 : oldj2);
                        MapleFamily fam = World.Family.getFamily(id);
                        fam.setOnline(inviter.getId(), true, inviter.getClient().getChannel());
                        if (old > 0 && World.Family.getFamily(old) != null) {
                            MapleFamily.mergeFamily(fam, World.Family.getFamily(old));
                        } else {
                            fam.setOnline(c.getPlayer().getId(), true, c.getChannel());
                        }
                        fam.resetDescendants();
                        fam.resetPedigree();
                    }
                }
                c.getSession().write((Object)FamilyPacket.getFamilyInfo(c.getPlayer()));
            }
        }
    }
}

