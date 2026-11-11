/*
 * Decompiled with CFR 0.152.
 */
package server;

import client.MapleCharacter;
import handling.channel.ChannelServer;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import server.maps.MapleMap;
import tools.MaplePacketCreator;

public class MapleCarnivalParty {
    private List<Integer> members = new LinkedList<Integer>();
    private WeakReference<MapleCharacter> leader;
    private byte team;
    private int channel;
    private short availableCP = 0;
    private short totalCP = 0;
    private boolean winner = false;

    public MapleCarnivalParty(MapleCharacter owner, List<MapleCharacter> members1, byte team1) {
        this.leader = new WeakReference<MapleCharacter>(owner);
        for (MapleCharacter mem : members1) {
            this.members.add(mem.getId());
            mem.setCarnivalParty(this);
        }
        this.team = team1;
        this.channel = owner.getClient().getChannel();
    }

    public final MapleCharacter getLeader() {
        return (MapleCharacter)this.leader.get();
    }

    public void addCP(MapleCharacter player, int ammount) {
        this.totalCP = (short)(this.totalCP + ammount);
        this.availableCP = (short)(this.availableCP + ammount);
        player.addCP(ammount);
    }

    public int getTotalCP() {
        return this.totalCP;
    }

    public int getAvailableCP() {
        return this.availableCP;
    }

    public void useCP(MapleCharacter player, int ammount) {
        this.availableCP = (short)(this.availableCP - ammount);
        player.useCP(ammount);
    }

    public List<Integer> getMembers() {
        return this.members;
    }

    public int getTeam() {
        return this.team;
    }

    public void warp(MapleMap map, String portalname) {
        for (int chr : this.members) {
            MapleCharacter c = ChannelServer.getInstance(this.channel).getPlayerStorage().getCharacterById(chr);
            if (c == null) continue;
            c.changeMap(map, map.getPortal(portalname));
        }
    }

    public void warp(MapleMap map, int portalid) {
        for (int chr : this.members) {
            MapleCharacter c = ChannelServer.getInstance(this.channel).getPlayerStorage().getCharacterById(chr);
            if (c == null) continue;
            c.changeMap(map, map.getPortal(portalid));
        }
    }

    public boolean allInMap(MapleMap map) {
        for (int chr : this.members) {
            if (map.getCharacterById(chr) != null) continue;
            return false;
        }
        return true;
    }

    public void removeMember(MapleCharacter chr) {
        for (int i = 0; i < this.members.size(); ++i) {
            if (this.members.get(i).intValue() != chr.getId()) continue;
            this.members.remove(i);
            chr.setCarnivalParty(null);
        }
    }

    public boolean isWinner() {
        return this.winner;
    }

    public void setWinner(boolean status) {
        this.winner = status;
    }

    public void displayMatchResult() {
        String effect = this.winner ? "quest/carnival/win" : "quest/carnival/lose";
        String sound = this.winner ? "MobCarnival/Win" : "MobCarnival/Lose";
        boolean done = false;
        for (int chr : this.members) {
            MapleCharacter c = ChannelServer.getInstance(this.channel).getPlayerStorage().getCharacterById(chr);
            if (c == null) continue;
            c.getClient().getSession().write((Object)MaplePacketCreator.showEffect(effect));
            c.getClient().getSession().write((Object)MaplePacketCreator.playSound(sound));
            if (done) continue;
            done = true;
            c.getMap().killAllMonsters(true);
            c.getMap().setSpawns(false);
        }
    }
}

