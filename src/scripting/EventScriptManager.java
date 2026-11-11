/*
 * Decompiled with CFR 0.152.
 */
package scripting;

import handling.channel.ChannelServer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import scripting.AbstractScriptManager;
import scripting.EventManager;
import tools.FileoutputUtil;

public class EventScriptManager
extends AbstractScriptManager {
    private final Map<String, EventEntry> events = new LinkedHashMap<String, EventEntry>();
    private final AtomicInteger runningInstanceMapId = new AtomicInteger(0);

    public final int getNewInstanceMapId() {
        return this.runningInstanceMapId.addAndGet(1);
    }

    public EventScriptManager(ChannelServer cserv, String[] scripts) {
        for (String script : scripts) {
            Invocable iv;
            if (script.equals("") || (iv = this.getInvocable("event/" + script + ".js", null)) == null) continue;
            this.events.put(script, new EventEntry(script, iv, new EventManager(cserv, iv, script)));
        }
    }

    public final EventManager getEventManager(String event) {
        EventEntry entry = this.events.get(event);
        if (entry == null) {
            return null;
        }
        return entry.em;
    }

    public final void init() {
        for (EventEntry entry : this.events.values()) {
            try {
                ((ScriptEngine)((Object)entry.iv)).put("em", entry.em);
                entry.iv.invokeFunction("init", new Object[]{null});
            }
            catch (Exception ex) {
                System.out.println("Error initiating event: " + entry.script + ":" + ex);
                FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Error initiating event: " + entry.script + ":" + ex);
            }
        }
    }

    public final void cancel() {
        for (EventEntry entry : this.events.values()) {
            entry.em.cancel();
        }
    }

    private static class EventEntry {
        public String script;
        public Invocable iv;
        public EventManager em;

        public EventEntry(String script, Invocable iv, EventManager em) {
            this.script = script;
            this.iv = iv;
            this.em = em;
        }
    }
}

