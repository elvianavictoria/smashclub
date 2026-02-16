package com.backendsyndicate.smashclub.common.security;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.AESLightEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;

public class Crypto {
    private static final String defaultKey = "21580c830693c0d419a6e3a6557f4f1434e6d0a3ea78dcfeb2cbe9934b45d820";

    public static String performEncrypt(String keyText, String plainText) {
        try {
            byte[] key = Hex.decode(keyText.getBytes());
            byte[] ptBytes = plainText.getBytes();
            BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESLightEngine()));
            cipher.init(true, new KeyParameter(key));
            byte[] rv = new byte[cipher.getOutputSize(ptBytes.length)];
            int oLen = cipher.processBytes(ptBytes, 0, ptBytes.length, rv, 0);
            cipher.doFinal(rv, oLen);

            return new String(Hex.encode(rv));
        } catch(Exception e) {
            return "Error";
        }
    }

    public static String performEncrypt(String cryptoText) { return performEncrypt(defaultKey, cryptoText); }

    public static String performDecrypt(String keyText, String cryptoText) {
        try {
            byte[] key = Hex.decode(keyText.getBytes());
            byte[] cipherText = Hex.decode(cryptoText.getBytes());
            BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESLightEngine()));
            cipher.init(false, new KeyParameter(key));
            byte[] rv = new byte[cipher.getOutputSize(cipherText.length)];
            int oLen = cipher.processBytes(cipherText, 0, cipherText.length, rv, 0);
            cipher.doFinal(rv, oLen);

            return new String(rv).trim();
        } catch(Exception e) {
            return "Error";
        }
    }

    public static String performDecrypt(String cryptoText) { return performDecrypt(defaultKey, cryptoText); }

    // Remove later when compiling it to jar for production
    public static void main(String[] args) {
        String strToEncrypt = "jdbc:sqlserver://sqlserver-juara;databaseName=SMASHCLUB;schema=smashclub;trustServerCertificate=true";
        System.out.println("Encryption Result for " + strToEncrypt + ": " + performEncrypt(strToEncrypt));

        String strToDecrypt = "151b7fe2e506c17f503ecd4195592832663fe42eaa37ce7e32c71699b5056e16c4933750d266ce5cde08895ad7fd2bfeacbd42d5f144a9ce8212a5e5e6e2d36b54d1030a346f3a2b14dc200d8f40b304";
        System.out.println("Decryption Result for " + strToDecrypt + ": " + performDecrypt(strToDecrypt));
    }
}