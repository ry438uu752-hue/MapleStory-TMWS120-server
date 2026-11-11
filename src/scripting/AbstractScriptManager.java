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
import java.util.stream.Collectors;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import tools.EncodingDetect;
import tools.FileoutputUtil;

public abstract class AbstractScriptManager {
    private static final ScriptEngineManager sem = new ScriptEngineManager();

    protected Invocable getInvocable(String path, MapleClient c) {
        return this.getInvocable(path, c, false);
    }

    protected Invocable getInvocable(String path, MapleClient c, boolean npc) {
        FileInputStream in = null;
        try {
            path = "Libs/scripts/" + path;
            ScriptEngine engine = null;
            if (c != null) {
                engine = c.getScriptEngine(path);
            }
            if (engine == null) {
                File scriptFile = new File(path);
                if (!scriptFile.exists()) {
                    return null;
                }
                engine = sem.getEngineByName("nashorn");
                if (c != null) {
                    c.setScriptEngine(path, engine);
                }
                in = new FileInputStream(scriptFile);
                BufferedReader bf = new BufferedReader(new InputStreamReader((InputStream)in, EncodingDetect.getJavaEncode(scriptFile)));
                String lines = "load('nashorn:mozilla_compat.js');" + bf.lines().collect(Collectors.joining(System.lineSeparator()));
                engine.eval(lines);
            } else if (c != null && npc) {
                c.getPlayer().dropMessage(5, "\u89d2\u8272\u72c0\u614b\u7570\u5e38\uff0c\u8acb\u4f7f\u7528@\u89e3\u5361/@ea \u4f86\u89e3\u9664\u7570\u5e38\u72c0\u614b");
            }
            return (Invocable)((Object)engine);
        }
        catch (Exception e) {
            System.err.println("Error executing script. Path: " + path + "\nException " + e);
            FileoutputUtil.log("Logs/Log_Script_Except.rtf", "Error executing script. Path: " + path + "\nException " + e);
            return null;
        }
    }
}

