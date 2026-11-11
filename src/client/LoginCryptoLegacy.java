/*
 * Decompiled with CFR 0.152.
 */
package client;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class LoginCryptoLegacy {
    private static final Random rand;
    private static final char[] iota64;

    public static final String hashPassword(String password) {
        byte[] randomBytes = new byte[6];
        rand.setSeed(System.currentTimeMillis());
        rand.nextBytes(randomBytes);
        return LoginCryptoLegacy.myCrypt(password, LoginCryptoLegacy.genSalt(randomBytes));
    }

    public static final boolean checkPassword(String password, String hash) {
        return LoginCryptoLegacy.myCrypt(password, hash).equals(hash);
    }

    public static final boolean isLegacyPassword(String hash) {
        return hash.substring(0, 3).equals("$H$");
    }

    private static final String myCrypt(String password, String seed) throws RuntimeException {
        String salt;
        String out = null;
        int count = 8;
        if (!seed.substring(0, 3).equals("$H$")) {
            byte[] randomBytes = new byte[6];
            rand.nextBytes(randomBytes);
            seed = LoginCryptoLegacy.genSalt(randomBytes);
        }
        if ((salt = seed.substring(4, 12)).length() != 8) {
            throw new RuntimeException("Error hashing password - Invalid seed.");
        }
        try {
            MessageDigest digester = MessageDigest.getInstance("SHA-1");
            digester.update((salt + password).getBytes("iso-8859-1"), 0, (salt + password).length());
            byte[] sha1Hash = digester.digest();
            do {
                byte[] CombinedBytes = new byte[sha1Hash.length + password.length()];
                System.arraycopy(sha1Hash, 0, CombinedBytes, 0, sha1Hash.length);
                System.arraycopy(password.getBytes("iso-8859-1"), 0, CombinedBytes, sha1Hash.length, password.getBytes("iso-8859-1").length);
                digester.update(CombinedBytes, 0, CombinedBytes.length);
                sha1Hash = digester.digest();
            } while (--count > 0);
            out = seed.substring(0, 12);
            out = out + LoginCryptoLegacy.encode64(sha1Hash);
        }
        catch (NoSuchAlgorithmException Ex) {
            System.err.println("Error hashing password." + Ex);
        }
        catch (UnsupportedEncodingException Ex) {
            System.err.println("Error hashing password." + Ex);
        }
        if (out == null) {
            throw new RuntimeException("Error hashing password - out = null");
        }
        return out;
    }

    private static final String genSalt(byte[] Random2) {
        StringBuilder Salt = new StringBuilder("$H$");
        Salt.append(iota64[30]);
        Salt.append(LoginCryptoLegacy.encode64(Random2));
        return Salt.toString();
    }

    private static final String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; ++i) {
            int halfbyte = data[i] >>> 4 & 0xF;
            int two_halfs = 0;
            do {
                if (0 <= halfbyte && halfbyte <= 9) {
                    buf.append((char)(48 + halfbyte));
                } else {
                    buf.append((char)(97 + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0xF;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static final String encodeSHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        return LoginCryptoLegacy.convertToHex(md.digest());
    }

    private static final String encode64(byte[] Input) {
        int iLen = Input.length;
        int oDataLen = (iLen * 4 + 2) / 3;
        int oLen = (iLen + 2) / 3 * 4;
        char[] out = new char[oLen];
        int ip = 0;
        int op = 0;
        while (ip < iLen) {
            int i0 = Input[ip++] & 0xFF;
            int i1 = ip < iLen ? Input[ip++] & 0xFF : 0;
            int i2 = ip < iLen ? Input[ip++] & 0xFF : 0;
            int o0 = i0 >>> 2;
            int o1 = (i0 & 3) << 4 | i1 >>> 4;
            int o2 = (i1 & 0xF) << 2 | i2 >>> 6;
            int o3 = i2 & 0x3F;
            out[op++] = iota64[o0];
            out[op++] = iota64[o1];
            out[op] = op < oDataLen ? iota64[o2] : 61;
            int n = ++op < oDataLen ? iota64[o3] : 61;
            out[op] = (char)n;
            ++op;
        }
        return new String(out);
    }

    static {
        int c;
        rand = new Random();
        iota64 = new char[64];
        int i = 0;
        LoginCryptoLegacy.iota64[i++] = (char)46;
        LoginCryptoLegacy.iota64[i++] = (char)47;
        for (c = 65; c <= 90; c = (int)((char)(c + 1))) {
            LoginCryptoLegacy.iota64[i++] = (char)c;
        }
        for (c = 97; c <= 122; c = (int)((char)(c + 1))) {
            LoginCryptoLegacy.iota64[i++] = (char)c;
        }
        for (c = 48; c <= 57; c = (int)((char)(c + 1))) {
            LoginCryptoLegacy.iota64[i++] = (char)c;
        }
    }
}

