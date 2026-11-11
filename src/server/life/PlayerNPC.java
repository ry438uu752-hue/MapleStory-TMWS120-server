/*
 * Decompiled with CFR 0.152.
 */
package server.life;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import database.DatabaseConnection;
import handling.channel.ChannelServer;
import handling.world.World;
import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import server.life.MapleLifeFactory;
import server.life.MapleNPC;
import server.maps.MapleMap;
import tools.MaplePacketCreator;

public class PlayerNPC
extends MapleNPC {
    private Map<Byte, Integer> equips = new HashMap<Byte, Integer>();
    private int mapid;
    private int face;
    private int hair;
    private int charId;
    private byte skin;
    private byte gender;
    private int[] pets = new int[3];

    public PlayerNPC(ResultSet rs) throws Exception {
        super(rs.getInt("ScriptId"), rs.getString("name"));
        this.hair = rs.getInt("hair");
        this.face = rs.getInt("face");
        this.mapid = rs.getInt("map");
        this.skin = rs.getByte("skin");
        this.charId = rs.getInt("charid");
        this.gender = rs.getByte("gender");
        this.setCoords(rs.getInt("x"), rs.getInt("y"), rs.getInt("dir"), rs.getInt("Foothold"));
        String[] pet = rs.getString("pets").split(",");
        for (int i = 0; i < 3; ++i) {
            this.pets[i] = pet[i] != null ? Integer.parseInt(pet[i]) : 0;
        }
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM playernpcs_equip WHERE NpcId = ?");
        ps.setInt(1, this.getId());
        ResultSet rs2 = ps.executeQuery();
        while (rs2.next()) {
            this.equips.put(rs2.getByte("equippos"), rs2.getInt("equipid"));
        }
        rs2.close();
        ps.close();
    }

    public PlayerNPC(MapleCharacter cid, int npc, MapleMap map, MapleCharacter base) {
        super(npc, cid.getName());
        this.charId = cid.getId();
        this.mapid = map.getId();
        this.setCoords(base.getPosition().x, base.getPosition().y, 0, base.getFH());
        this.update(cid);
    }

    public void setCoords(int x, int y, int f, int fh) {
        this.setPosition(new Point(x, y));
        this.setCy(y);
        this.setRx0(x - 50);
        this.setRx1(x + 50);
        this.setF(f);
        this.setFh(fh);
    }

    public static void loadAll() {
        ArrayList<PlayerNPC> toAdd = new ArrayList<PlayerNPC>();
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM playernpcs");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                toAdd.add(new PlayerNPC(rs));
            }
            rs.close();
            ps.close();
        }
        catch (Exception se) {
            se.printStackTrace();
        }
        for (PlayerNPC npc : toAdd) {
            npc.addToServer();
        }
    }

    public static void updateByCharId(MapleCharacter chr) {
        if (World.Find.findChannel(chr.getId()) > 0) {
            for (PlayerNPC npc : ChannelServer.getInstance(World.Find.findChannel(chr.getId())).getAllPlayerNPC()) {
                npc.update(chr);
            }
        }
    }

    public void addToServer() {
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            cserv.addPlayerNPC(this);
        }
    }

    public void removeFromServer() {
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            cserv.removePlayerNPC(this);
        }
    }

    public void update(MapleCharacter chr) {
        if (chr == null || this.charId != chr.getId()) {
            return;
        }
        this.setName(chr.getName());
        this.setHair(chr.getHair());
        this.setFace(chr.getFace());
        this.setSkin(chr.getSkinColor());
        this.setGender(chr.getGender());
        this.setPets(chr.getPets());
        this.equips = new HashMap<Byte, Integer>();
        for (IItem item : chr.getInventory(MapleInventoryType.EQUIPPED).list()) {
            if (item.getPosition() < -128) continue;
            this.equips.put((byte)item.getPosition(), item.getItemId());
        }
        this.saveToDB();
    }

    public void destroy() {
        this.destroy(false);
    }

    public void destroy(boolean remove) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM playernpcs WHERE scriptid = ?");
            ps.setInt(1, this.getId());
            ps.executeUpdate();
            ps.close();
            ps = con.prepareStatement("DELETE FROM playernpcs_equip WHERE npcid = ?");
            ps.setInt(1, this.getId());
            ps.executeUpdate();
            ps.close();
            if (remove) {
                this.removeFromServer();
            }
        }
        catch (Exception se) {
            se.printStackTrace();
        }
    }

    public void saveToDB() {
        Connection con = DatabaseConnection.getConnection();
        try {
            if (this.getNPCFromWZ() == null) {
                this.destroy(true);
                return;
            }
            this.destroy();
            PreparedStatement ps = con.prepareStatement("INSERT INTO playernpcs(name, hair, face, skin, x, y, map, charid, scriptid, foothold, dir, gender, pets) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, this.getName());
            ps.setInt(2, this.getHair());
            ps.setInt(3, this.getFace());
            ps.setInt(4, this.getSkin());
            ps.setInt(5, this.getPosition().x);
            ps.setInt(6, this.getPosition().y);
            ps.setInt(7, this.getMapId());
            ps.setInt(8, this.getCharId());
            ps.setInt(9, this.getId());
            ps.setInt(10, this.getFh());
            ps.setInt(11, this.getF());
            ps.setInt(12, this.getGender());
            String[] pet = new String[]{"0", "0", "0"};
            for (int i = 0; i < 3; ++i) {
                if (this.pets[i] <= 0) continue;
                pet[i] = String.valueOf(this.pets[i]);
            }
            ps.setString(13, pet[0] + "," + pet[1] + "," + pet[2]);
            ps.executeUpdate();
            ps.close();
            ps = con.prepareStatement("INSERT INTO playernpcs_equip(npcid, charid, equipid, equippos) VALUES (?, ?, ?, ?)");
            ps.setInt(1, this.getId());
            ps.setInt(2, this.getCharId());
            for (Map.Entry<Byte, Integer> equip : this.equips.entrySet()) {
                ps.setInt(3, equip.getValue());
                ps.setInt(4, equip.getKey().byteValue());
                ps.executeUpdate();
            }
            ps.close();
        }
        catch (Exception se) {
            se.printStackTrace();
        }
    }

    public Map<Byte, Integer> getEquips() {
        return this.equips;
    }

    public byte getSkin() {
        return this.skin;
    }

    public int getGender() {
        return this.gender;
    }

    public int getFace() {
        return this.face;
    }

    public int getHair() {
        return this.hair;
    }

    public int getCharId() {
        return this.charId;
    }

    public int getMapId() {
        return this.mapid;
    }

    public void setSkin(byte s) {
        this.skin = s;
    }

    public void setFace(int f) {
        this.face = f;
    }

    public void setHair(int h) {
        this.hair = h;
    }

    public void setGender(int g) {
        this.gender = (byte)g;
    }

    public int getPet(int i) {
        return this.pets[i] > 0 ? this.pets[i] : 0;
    }

    public void setPets(List<MaplePet> p) {
        for (int i = 0; i < 3; ++i) {
            this.pets[i] = p != null && p.size() > i && p.get(i) != null ? p.get(i).getPetItemId() : 0;
        }
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        client.getSession().write((Object)MaplePacketCreator.spawnNPC(this, true));
        client.getSession().write((Object)MaplePacketCreator.spawnPlayerNPC(this));
        client.getSession().write((Object)MaplePacketCreator.spawnNPCRequestController(this, true));
    }

    public MapleNPC getNPCFromWZ() {
        MapleNPC npc = MapleLifeFactory.getNPC(this.getId());
        if (npc != null) {
            npc.setName(this.getName());
        }
        return npc;
    }
}

