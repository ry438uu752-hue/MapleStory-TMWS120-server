/*
 * Decompiled with CFR 0.152.
 */
package scripting;

import client.MapleClient;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import scripting.AbstractScriptManager;
import scripting.NPCConversationManager;
import server.quest.MapleQuest;
import tools.FileoutputUtil;

public class NPCScriptManager
extends AbstractScriptManager {
    private final Map<MapleClient, NPCConversationManager> cms = new WeakHashMap<MapleClient, NPCConversationManager>();
    private static final NPCScriptManager instance = new NPCScriptManager();

    public static final NPCScriptManager getInstance() {
        return instance;
    }

    public void start(MapleClient c, int npc) {
        this.start(c, npc, 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void start(MapleClient c, int npc, int wh) {
        Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (c.getPlayer().isGM()) {
                if (wh == 0) {
                    c.getPlayer().dropMessage("[\u7cfb\u7d71\u63d0\u793a]\u60a8\u5df2\u7d93\u5efa\u7acb\u8207NPC:" + npc + "\u7684\u5c0d\u8a71\u3002");
                } else {
                    c.getPlayer().dropMessage("[\u7cfb\u7d71\u63d0\u793a]\u60a8\u5df2\u7d93\u5efa\u7acb\u8207NPC:" + npc + "_" + wh + "\u7684\u5c0d\u8a71\u3002");
                }
            }
            if (!this.cms.containsKey(c)) {
                Invocable iv = wh == 0 ? this.getInvocable("npc/" + npc + ".js", c, true) : this.getInvocable("npc/" + npc + "_" + wh + ".js", c, true);
                if (iv == null && (iv = this.getInvocable("npc/notcoded.js", c, true)) == null) {
                    this.dispose(c);
                    return;
                }
                ScriptEngine scriptengine = (ScriptEngine)((Object)iv);
                NPCConversationManager cm = new NPCConversationManager(c, npc, -1, (byte)-1, iv);
                this.cms.put(c, cm);
                scriptengine.put("cm", cm);
                scriptengine.put("npcid", npc);
                c.getPlayer().setConversation(1);
                try {
                    iv.invokeFunction("start", new Object[0]);
                }
                catch (NoSuchMethodException nsme) {
                    iv.invokeFunction("action", (byte)1, (byte)0, 0);
                }
            } else {
                c.getPlayer().dropMessage("\u89d2\u8272\u72c0\u614b\u7570\u5e38\uff0c\u8acb\u4f7f\u7528@\u89e3\u5361/@ea \u4f86\u89e3\u9664\u7570\u5e38\u72c0\u614b");
            }
        }
        catch (Exception e) {
            System.err.println("NPC \u8173\u672c\u932f\u8aa4, \u5b83ID\u70ba : " + npc + "." + e);
            if (c.getPlayer().isGM()) {
                c.getPlayer().dropMessage("[\u7cfb\u7d71\u63d0\u793a] NPC " + npc + "\u8173\u672c\u932f\u8aa4 " + e + "");
            }
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Error executing NPC script, NPC ID : " + npc + "." + e);
            this.dispose(c);
        }
        finally {
            lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void action(MapleClient c, byte mode, byte type, int selection) {
        if (mode != -1) {
            NPCConversationManager cm = this.cms.get(c);
            if (cm == null || cm.getLastMsg() > -1) {
                return;
            }
            Lock lock = c.getNPCLock();
            lock.lock();
            try {
                if (cm.pendingDisposal) {
                    this.dispose(c);
                } else {
                    cm.getIv().invokeFunction("action", mode, type, selection);
                }
            }
            catch (Exception e) {
                if (c.getPlayer().isGM()) {
                    c.getPlayer().dropMessage("[\u7cfb\u7d71\u63d0\u793a] NPC " + cm.getNpc() + "\u8173\u672c\u932f\u8aa4 " + e + "");
                }
                System.err.println("NPC \u8173\u672c\u932f\u8aa4. \u5b83ID\u70ba : " + cm.getNpc() + ":" + e);
                this.dispose(c);
                FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Error executing NPC script, NPC ID : " + cm.getNpc() + "." + e);
            }
            finally {
                lock.unlock();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void startQuest(MapleClient c, int npc, int quest) {
        if (!MapleQuest.getInstance(quest).canStart(c.getPlayer(), null)) {
            return;
        }
        Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!this.cms.containsKey(c)) {
                Invocable iv = this.getInvocable("quest/" + quest + ".js", c, true);
                if (iv == null) {
                    this.dispose(c);
                    return;
                }
                ScriptEngine scriptengine = (ScriptEngine)((Object)iv);
                NPCConversationManager cm = new NPCConversationManager(c, npc, quest, (byte)0, iv);
                this.cms.put(c, cm);
                scriptengine.put("qm", cm);
                c.getPlayer().setConversation(1);
                if (c.getPlayer().isGM()) {
                    c.getPlayer().dropMessage("[\u7cfb\u7d71\u63d0\u793a]\u60a8\u5df2\u7d93\u5efa\u7acb\u8207\u4efb\u52d9\u8173\u672c:" + quest + "\u7684\u5f80\u4f86\u3002");
                }
                iv.invokeFunction("start", (byte)1, (byte)0, 0);
            }
        }
        catch (Exception e) {
            System.err.println("Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + e);
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + e);
            this.dispose(c);
        }
        finally {
            lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void startQuest(MapleClient c, byte mode, byte type, int selection) {
        Lock lock = c.getNPCLock();
        NPCConversationManager cm = this.cms.get(c);
        if (cm == null || cm.getLastMsg() > -1) {
            return;
        }
        lock.lock();
        try {
            if (cm.pendingDisposal) {
                this.dispose(c);
            } else {
                cm.getIv().invokeFunction("start", mode, type, selection);
            }
        }
        catch (Exception e) {
            if (c.getPlayer().isGM()) {
                c.getPlayer().dropMessage("[\u7cfb\u7d71\u63d0\u793a]\u4efb\u52d9\u8173\u672c:" + cm.getQuest() + "\u932f\u8aa4...NPC: " + cm.getNpc() + ":" + e);
            }
            System.err.println("Error executing Quest script. (" + cm.getQuest() + ")...NPC: " + cm.getNpc() + ":" + e);
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Error executing Quest script. (" + cm.getQuest() + ")..NPCID: " + cm.getNpc() + ":" + e);
            this.dispose(c);
        }
        finally {
            lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void endQuest(MapleClient c, int npc, int quest, boolean customEnd) {
        if (!customEnd && !MapleQuest.getInstance(quest).canComplete(c.getPlayer(), null)) {
            return;
        }
        Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!this.cms.containsKey(c)) {
                Invocable iv = this.getInvocable("quest/" + quest + ".js", c, true);
                if (iv == null) {
                    this.dispose(c);
                    return;
                }
                ScriptEngine scriptengine = (ScriptEngine)((Object)iv);
                NPCConversationManager cm = new NPCConversationManager(c, npc, quest, (byte)1, iv);
                this.cms.put(c, cm);
                scriptengine.put("qm", cm);
                c.getPlayer().setConversation(1);
                iv.invokeFunction("end", (byte)1, (byte)0, 0);
            }
        }
        catch (Exception e) {
            if (c.getPlayer().isGM()) {
                c.getPlayer().dropMessage("[\u7cfb\u7d71\u63d0\u793a]\u4efb\u52d9\u8173\u672c:" + quest + "\u932f\u8aa4...NPC: " + quest + ":" + e);
            }
            System.err.println("Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + e);
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + e);
            this.dispose(c);
        }
        finally {
            lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void endQuest(MapleClient c, byte mode, byte type, int selection) {
        Lock lock = c.getNPCLock();
        NPCConversationManager cm = this.cms.get(c);
        if (cm == null || cm.getLastMsg() > -1) {
            return;
        }
        lock.lock();
        try {
            if (cm.pendingDisposal) {
                this.dispose(c);
            } else {
                cm.getIv().invokeFunction("end", mode, type, selection);
            }
        }
        catch (Exception e) {
            if (c.getPlayer().isGM()) {
                c.getPlayer().dropMessage("[\u7cfb\u7d71\u63d0\u793a]\u4efb\u52d9\u8173\u672c:" + cm.getQuest() + "\u932f\u8aa4...NPC: " + cm.getNpc() + ":" + e);
            }
            System.err.println("Error executing Quest script. (" + cm.getQuest() + ")...NPC: " + cm.getNpc() + ":" + e);
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Error executing Quest script. (" + cm.getQuest() + ")..NPCID: " + cm.getNpc() + ":" + e);
            this.dispose(c);
        }
        finally {
            lock.unlock();
        }
    }

    public final void dispose(MapleClient c) {
        NPCConversationManager npccm = this.cms.get(c);
        if (npccm != null) {
            this.cms.remove(c);
            if (npccm.getType() == -1) {
                c.removeScriptEngine("Libs/scripts/npc/" + npccm.getNpc() + ".js");
                c.removeScriptEngine("Libs/scripts/npc/notcoded.js");
            } else {
                c.removeScriptEngine("Libs/scripts/quest/" + npccm.getQuest() + ".js");
            }
        }
        if (c.getPlayer() != null && c.getPlayer().getConversation() == 1) {
            c.getPlayer().setConversation(0);
        }
    }

    public final NPCConversationManager getCM(MapleClient c) {
        return this.cms.get(c);
    }
}

