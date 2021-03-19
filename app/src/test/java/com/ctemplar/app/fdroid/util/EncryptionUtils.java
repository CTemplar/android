package com.ctemplar.app.fdroid.util;

public class EncryptionUtils {
    private static final String BEGIN_PGP_PUBLIC_KEY_BLOCK = "-----BEGIN PGP PUBLIC KEY BLOCK-----";
    private static final String BEGIN_PGP_PRIVATE_KEY_BLOCK = "-----BEGIN PGP PRIVATE KEY BLOCK-----";
    private static final String END_PGP_PUBLIC_KEY_BLOCK = "-----END PGP PUBLIC KEY BLOCK-----";
    private static final String END_PGP_PRIVATE_KEY_BLOCK = "-----END PGP PRIVATE KEY BLOCK-----";

    private static final String BEGIN_PGP_MESSAGE = "-----BEGIN PGP MESSAGE-----";
    private static final String END_PGP_MESSAGE = "-----END PGP MESSAGE-----";

    public static boolean checkPublicKey(String publicKey) {
        boolean startWith = publicKey.startsWith(BEGIN_PGP_PUBLIC_KEY_BLOCK);
        boolean endWith = publicKey.endsWith(END_PGP_PUBLIC_KEY_BLOCK);
        return startWith && endWith;
    }

    public static boolean checkPrivateKey(String privateKey) {
        boolean startWith = privateKey.startsWith(BEGIN_PGP_PRIVATE_KEY_BLOCK);
        boolean endWith = privateKey.endsWith(END_PGP_PRIVATE_KEY_BLOCK);
        return startWith && endWith;
    }

    public static boolean checkEncryptedMessage(String message) {
        boolean startWith = message.startsWith(BEGIN_PGP_MESSAGE);
        boolean endWith = message.endsWith(END_PGP_MESSAGE);
        return startWith && endWith;
    }
}
