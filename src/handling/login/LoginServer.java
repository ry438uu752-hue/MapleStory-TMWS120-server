/*
 * Decompiled with CFR 0.152.
 */
package handling.login;

import handling.MapleServerHandler;
import handling.mina.MapleCodecFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.ByteBufferAllocator;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoFilter;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoServiceConfig;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;
import server.ServerProperties;

public class LoginServer {
    public static final int PORT = 8484;
    private static InetSocketAddress InetSocketadd;
    private static IoAcceptor acceptor;
    private static Map<Integer, Integer> load;
    private static String serverName;
    private static String eventMessage;
    private static String bubbleMessage;
    private static String bubbleMessagePos;
    private static byte flag;
    private static int maxCharacters;
    private static int userLimit;
    private static int usersOn;
    private static boolean finishedShutdown;
    private static boolean adminOnly;

    public static final void addChannel(int channel) {
        load.put(channel, 0);
    }

    public static final void removeChannel(int channel) {
        load.remove(channel);
    }

    public static final void run_startup_configurations() {
        userLimit = Integer.parseInt(ServerProperties.getProperty("tms.UserLimit"));
        serverName = ServerProperties.getProperty("tms.ServerName");
        eventMessage = ServerProperties.getProperty("tms.EventMessage");
        bubbleMessage = ServerProperties.getProperty("tms.BubbleMessage");
        bubbleMessagePos = ServerProperties.getProperty("tms.BubbleMessagePos");
        flag = Byte.parseByte(ServerProperties.getProperty("tms.Flag"));
        adminOnly = Boolean.parseBoolean(ServerProperties.getProperty("tms.Admin", "false"));
        maxCharacters = Integer.parseInt(ServerProperties.getProperty("tms.MaxCharacters"));
        ByteBuffer.setUseDirectBuffers((boolean)false);
        ByteBuffer.setAllocator((ByteBufferAllocator)new SimpleByteBufferAllocator());
        acceptor = new SocketAcceptor();
        SocketAcceptorConfig cfg = new SocketAcceptorConfig();
        cfg.getSessionConfig().setTcpNoDelay(true);
        cfg.setDisconnectOnUnbind(true);
        cfg.getFilterChain().addLast("codec", (IoFilter)new ProtocolCodecFilter((ProtocolCodecFactory)new MapleCodecFactory()));
        try {
            InetSocketadd = new InetSocketAddress(8484);
            acceptor.bind((SocketAddress)InetSocketadd, (IoHandler)new MapleServerHandler(-1, false), (IoServiceConfig)cfg);
            System.out.println("Login   1: Listening on port 8484");
        }
        catch (IOException e) {
            System.err.println("Binding to port 8484 failed" + e);
        }
    }

    public static final void shutdown() {
        if (finishedShutdown) {
            return;
        }
        System.out.println("Shutting down login...");
        acceptor.unbindAll();
        finishedShutdown = true;
    }

    public static final String getServerName() {
        return serverName;
    }

    public static final String getEventMessage() {
        return eventMessage;
    }

    public static final String getBubbleMessage() {
        return bubbleMessage;
    }

    public static final String getBubbleMessagePos() {
        return bubbleMessagePos;
    }

    public static final byte getFlag() {
        return flag;
    }

    public static final int getMaxCharacters() {
        return maxCharacters;
    }

    public static final Map<Integer, Integer> getLoad() {
        return load;
    }

    public static void setLoad(Map<Integer, Integer> load_, int usersOn_) {
        load = load_;
        usersOn = usersOn_;
    }

    public static final void setEventMessage(String newMessage) {
        eventMessage = newMessage;
    }

    public static final void setFlag(byte newflag) {
        flag = newflag;
    }

    public static final int getUserLimit() {
        return userLimit;
    }

    public static final int getUsersOn() {
        return usersOn;
    }

    public static final void setUserLimit(int newLimit) {
        userLimit = newLimit;
    }

    public static final int getNumberOfSessions() {
        return acceptor.getManagedSessions((SocketAddress)InetSocketadd).size();
    }

    public static final boolean isAdminOnly() {
        return adminOnly;
    }

    public static final boolean isShutdown() {
        return finishedShutdown;
    }

    public static final void setOn() {
        finishedShutdown = false;
    }

    static {
        load = new HashMap<Integer, Integer>();
        usersOn = 0;
        finishedShutdown = true;
        adminOnly = false;
    }
}

