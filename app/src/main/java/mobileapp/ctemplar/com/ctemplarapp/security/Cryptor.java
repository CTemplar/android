package mobileapp.ctemplar.com.ctemplarapp.security;

import java.util.List;

import timber.log.Timber;

public class Cryptor {
    public static byte[] decryptPGP(byte[] content, List<String> privateKeys, String passPhrase) {
        if (content == null) {
            throw new NullPointerException("content cannot be null");
        }
        if (passPhrase == null) {
            throw new NullPointerException("passPhrase cannot be null");
        }
        if (privateKeys == null) {
            throw new NullPointerException("privateKeys cannot be null");
        }
        if (privateKeys.isEmpty()) {
            throw new IllegalArgumentException("privateKeys cannot be empty");
        }
        try {
            return PGPCryptor.decrypt(content, privateKeys, passPhrase);
        } catch (PGPCryptorPrivateKeyExtractFailed e) {
            Timber.e(e, "Pass is wrong?");
        } catch (PGPCryptorPrivateKeyNotFound e) {
            Timber.e(e, "Private key is deleted?");
        } catch (PGPCryptorException | PGPBadPrivateKeyException | PGPCryptorPublicKeysNotFound | PGPCryptorDataNotFound | PGPCryptorReadDataFailed e) {
            Timber.e(e, "Failed to decrypt (" + e.getClass().getSimpleName() + " exception)");
        } catch (Throwable e) {
            Timber.e(e, "Unhandled exception detected");
        }
        return content;
    }

    public static String decryptPGP(String content, List<String> privateKeys, String passPhrase) {
        if (content == null) {
            throw new NullPointerException("content cannot be null");
        }
        byte[] result = decryptPGP(content.getBytes(), privateKeys, passPhrase);
        if (result == null) {
            return null;
        }
        return new String(result);
    }

    public static byte[] decryptGPG(byte[] content, String passPhrase) {
        if (content == null) {
            throw new NullPointerException("content cannot be null");
        }
        if (passPhrase == null) {
            throw new NullPointerException("passPhrase cannot be null");
        }
        return content;
    }

    public static String decryptGPG(String content, String passPhrase) {
        if (content == null) {
            throw new NullPointerException("content cannot be null");
        }
        byte[] result = decryptGPG(content.getBytes(), passPhrase);
        if (result == null) {
            return null;
        }
        return new String(result);
    }

    public static byte[] encrypt(byte[] content, List<String> publicKeys, boolean asciiArmor) {
        if (content == null) {
            throw new NullPointerException("content cannot be null");
        }
        if (publicKeys == null) {
            throw new NullPointerException("publicKeys cannot be null");
        }
        if (publicKeys.isEmpty()) {
            throw new IllegalArgumentException("publicKeys cannot be empty");
        }
        return content;
    }

    public static String encrypt(String content, List<String> publicKeys, boolean asciiArmor) {
        if (content == null) {
            throw new NullPointerException("content cannot be null");
        }
        byte[] result = encrypt(content.getBytes(), publicKeys, asciiArmor);
        if (result == null) {
            return null;
        }
        return new String(result);
    }
}
