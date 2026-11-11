/*
 * Decompiled with CFR 0.152.
 */
package tools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FileoutputUtil {
    public static final String Acc_Stuck = "Logs/Log_AccountStuck.rtf";
    public static final String Login_Error = "Logs/Log_Login_Error.rtf";
    public static final String IP_Log = "Logs/Log_AccountIP.rtf";
    public static final String Zakum_Log = "Logs/Log_Zakum.rtf";
    public static final String Horntail_Log = "Logs/Log_Horntail.rtf";
    public static final String Pinkbean_Log = "Logs/Log_Pinkbean.rtf";
    public static final String ScriptEx_Log = "Logs/Log_Script_Except.rtf";
    public static final String PacketEx_Log = "Logs/Log_Packet_Except.rtf";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat sdf_ = new SimpleDateFormat("yyyy-MM-dd");

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void log(String file, String msg) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file, true);
            out.write(("\n------------------------ " + FileoutputUtil.CurrentReadable_Time() + " ------------------------\n").getBytes());
            out.write(msg.getBytes());
        }
        catch (IOException iOException) {
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
            }
            catch (IOException iOException) {}
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void outputFileError(String file, Throwable t) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file, true);
            out.write(("\n------------------------ " + FileoutputUtil.CurrentReadable_Time() + " ------------------------\n").getBytes());
            out.write(FileoutputUtil.getString(t).getBytes());
        }
        catch (IOException iOException) {
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
            }
            catch (IOException iOException) {}
        }
    }

    public static String CurrentReadable_Date() {
        return sdf_.format(Calendar.getInstance().getTime());
    }

    public static String CurrentReadable_Time() {
        return sdf.format(Calendar.getInstance().getTime());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getString(Throwable e) {
        String retValue = null;
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            retValue = sw.toString();
        }
        finally {
            try {
                if (pw != null) {
                    pw.close();
                }
                if (sw != null) {
                    sw.close();
                }
            }
            catch (IOException iOException) {}
        }
        return retValue;
    }
}

