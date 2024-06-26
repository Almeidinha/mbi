package com.msoft.mbi.data.api.data.util;

public class EncryptionBase64 {

    static private final char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
    static private final byte[] codes    = new byte[256];
    static {
        for (int i = 0; i < 256; i++)
            codes[i] = -1;
        for (int i = 'A'; i <= 'Z'; i++)
            codes[i] = (byte) (i - 'A');
        for (int i = 'a'; i <= 'z'; i++)
            codes[i] = (byte) (26 + i - 'a');
        for (int i = '0'; i <= '9'; i++)
            codes[i] = (byte) (52 + i - '0');
        codes['+'] = 62;
        codes['/'] = 63;
    }


    public static String encryptionRSA(String string_normal) {
        byte[] data = string_normal.getBytes();
        char[] out = new char[((data.length + 2) / 3) * 4];
        int i = 0;
        for (int index = 0; i < data.length; index += 4) {
            boolean quad = false;
            boolean trip = false;
            int val = 0xff & data[i];
            val <<= 8;
            if (i + 1 < data.length) {
                val |= 0xff & data[i + 1];
                trip = true;
            }
            val <<= 8;
            if (i + 2 < data.length) {
                val |= 0xff & data[i + 2];
                quad = true;
            }
            out[index + 3] = alphabet[quad ? val & 0x3f : 64];
            val >>= 6;
            out[index + 2] = alphabet[trip ? val & 0x3f : 64];
            val >>= 6;
            out[index + 1] = alphabet[val & 0x3f];
            val >>= 6;
            out[index] = alphabet[val & 0x3f];
            i += 3;
        }

        return new String(out);
    }

    public static String decryptionRSA(String string_codificada) {
        char[] data = string_codificada.toCharArray();
        int tempLen = data.length;
        for (char c : data) {
            if ((c > 255) || codes[c] < 0)
                --tempLen;
        }
        int len = (tempLen / 4) * 3;
        if ((tempLen % 4) == 3)
            len += 2;
        if ((tempLen % 4) == 2)
            len += 1;

        byte[] out = new byte[len];

        int shift = 0;
        int accum = 0;
        int index = 0;

        for (char datum : data) {
            int value = (datum > 255) ? -1 : codes[datum];

            if (value >= 0) {
                accum <<= 6;
                shift += 6;
                accum |= value;
                if (shift >= 8) {
                    shift -= 8;
                    out[index++] =
                            (byte) ((accum >> shift) & 0xff);
                }
            }
        }

        if (index != out.length) {
            throw new Error("Miscalculated data length (wrote " + index + " instead of " + out.length + ")");
        }
        return new String(out);
    }

}
