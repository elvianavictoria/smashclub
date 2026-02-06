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
        String strToEncrypt = "xnd_public_development_Yp_HpuT9hw2PROmqk_ykFUxpSOw2oR88cnopzRU4juTZ9_4YUK07pYiTp1QMMzO3";
        System.out.println("Encryption Result for " + strToEncrypt + ": " + performEncrypt(strToEncrypt));

        String strToDecrypt = "a66b7ef2f967873fd6a1b3bd2306aa37";
        System.out.println("Decryption Result for " + strToDecrypt + ": " + performDecrypt(strToDecrypt));
    }
}