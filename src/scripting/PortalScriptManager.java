/*
 * Decompiled with CFR 0.152.
 */
package scripting;

import client.MapleClient;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import scripting.PortalPlayerInteraction;
import scripting.PortalScript;
import server.MaplePortal;
import tools.EncodingDetect;
import tools.FileoutputUtil;

public class PortalScriptManager {
    private static final PortalScriptManager instance = new PortalScriptManager();
    private final Map<String, PortalScript> scripts = new HashMap<String, PortalScript>();
    private static final ScriptEngineFactory sef = new ScriptEngineManager().getEngineByName("nashorn").getFactory();

    public static final PortalScriptManager getInstance() {
        return instance;
    }

    private final PortalScript getPortalScript(String scriptName) {
        if (this.scripts.containsKey(scriptName)) {
            return this.scripts.get(scriptName);
        }
        String scriptPath = "Libs/scripts/portal/" + scriptName + ".js";
        File scriptFile = new File(scriptPath);
        if (!scriptFile.exists()) {
            this.scripts.put(scriptName, null);
            return null;
        }
        FileInputStream in = null;
        ScriptEngine portal = sef.getScriptEngine();
        try {
            in = new FileInputStream(scriptFile);
            BufferedReader bf = new BufferedReader(new InputStreamReader((InputStream)in, EncodingDetect.getJavaEncode(scriptFile)));
            String lines = "load('nashorn:mozilla_compat.js');" + bf.lines().collect(Collectors.joining(System.lineSeparator()));
            CompiledScript compiled = ((Compilable)((Object)portal)).compile(lines);
            compiled.eval();
        }
        catch (Exception e) {
            System.err.println("Error executing Portalscript: " + scriptName + ":" + e);
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Error executing Portal script. (" + scriptName + ") " + e);
        }
        PortalScript script = ((Invocable)((Object)portal)).getInterface(PortalScript.class);
        this.scripts.put(scriptName, script);
        return script;
    }

    public final void executePortalScript(MaplePortal portal, MapleClient c) {
        PortalScript script = this.getPortalScript(portal.getScriptName());
        if (script != null) {
            try {
                script.enter(new PortalPlayerInteraction(c, portal));
            }
            catch (Exception e) {
                System.err.println("Error entering Portalscript: " + portal.getScriptName() + ":" + e);
            }
        } else {
            System.out.println("Unhandled portal script " + portal.getScriptName() + " on map " + c.getPlayer().getMapId());
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Unhandled portal script " + portal.getScriptName() + " on map " + c.getPlayer().getMapId());
        }
    }

    public final void clearScripts() {
        this.scripts.clear();
    }
}

