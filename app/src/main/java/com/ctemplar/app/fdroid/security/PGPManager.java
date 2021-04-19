package com.ctemplar.app.fdroid.security;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRing;

import java.io.IOException;

import com.ctemplar.app.fdroid.net.entity.PGPKeyEntity;
import timber.log.Timber;

public class PGPManager {
    private static final int RSA_KEY_STRENGTH = 4096;
    private static final boolean COMPRESSION = false;

    public static PGPKeyEntity generateKeys(String keyRingId, String password) {
        try {
            PGPKeyRingGenerator pgpKeyRingGenerator = PGPLib.generateKeyRing(password,
                    RSA_KEY_STRENGTH, keyRingId);
            byte[] publicKeyBytes = PGPLib.getPGPPublicKey(pgpKeyRingGenerator);
            byte[] privateKeyBytes = PGPLib.getPGPPrivateKey(pgpKeyRingGenerator);
            String keyFingerprint = PGPLib.getPGPKeyFingerprint(pgpKeyRingGenerator.generateSecretKeyRing());
            return new PGPKeyEntity(new String(publicKeyBytes), new String(privateKeyBytes), keyFingerprint);
        } catch (IOException | PGPException e) {
            Timber.e(e);
        }
        return new PGPKeyEntity("", "", "");
    }

    public static PGPKeyEntity generateECCKeys(String keyRingId, String password) {
        try {
            PGPKeyRingGenerator pgpKeyRingGenerator = PGPLib.generateECCKeyRing(password, keyRingId);
            byte[] publicKeyBytes = PGPLib.getPGPPublicKey(pgpKeyRingGenerator);
            byte[] privateKeyBytes = PGPLib.getPGPPrivateKey(pgpKeyRingGenerator);
            String keyFingerprint = PGPLib.getPGPKeyFingerprint(pgpKeyRingGenerator.generateSecretKeyRing());
            return new PGPKeyEntity(new String(publicKeyBytes), new String(privateKeyBytes), keyFingerprint);
        } catch (IOException | PGPException e) {
            Timber.e(e);
        }
        return new PGPKeyEntity("", "", "");
    }

    public static PGPKeyEntity changePrivateKeyPassword(PGPKeyEntity pgpKeyEntity, String oldPassword, String newPassword) throws IOException, PGPException {
        String publicKey = pgpKeyEntity.getPublicKey();
        String privateKey = pgpKeyEntity.getPrivateKey();
        PGPSecretKeyRing pgpSecretKeyRing = PGPLib.getPGPSecretKeyRing(privateKey);
        PGPSecretKeyRing updatedPGPSecretKeyRing = PGPLib.updatePGPSecretKeyRing(pgpSecretKeyRing,
                oldPassword, newPassword);
        byte[] updatedPrivateKeyBytes = PGPLib.getPGPPrivateKey(updatedPGPSecretKeyRing);
        String keyFingerprint = PGPLib.getPGPKeyFingerprint(updatedPGPSecretKeyRing);

        return new PGPKeyEntity(publicKey, new String(updatedPrivateKeyBytes), keyFingerprint);
    }

    public static String encrypt(String inputText, String[] pubKeys) {
        return new String(encrypt(inputText.getBytes(), pubKeys, true));
    }

    public static byte[] encrypt(byte[] inputBytes, String[] pubKeys, boolean asciiArmor) {
        try {
            PGPPublicKeyRing[] pgpPublicKeyRings = PGPLib.getPGPPublicKeyRings(pubKeys);
            if (pgpPublicKeyRings.length <= 0) {
                return new byte[0];
            }
            return PGPLib.encrypt(inputBytes, pgpPublicKeyRings, asciiArmor, COMPRESSION);
        } catch (Exception e) {
            Timber.e(e);
        }
        return new byte[0];
    }

    public static String decrypt(String encryptedText, String privateKey, String passPhrase) {
        return new String(decrypt(encryptedText.getBytes(), privateKey, passPhrase));
    }

    public static byte[] decrypt(byte[] encryptedBytes, String privateKey, String passPhrase) {
        try {
            PGPSecretKeyRing pgpSecretKeyRing = PGPLib.getPGPSecretKeyRing(privateKey);
            return PGPLib.decrypt(encryptedBytes, pgpSecretKeyRing, passPhrase);
        } catch (PGPException e) {
            Timber.i(e);
        } catch (IOException e) {
            Timber.w(e);
        } catch (Exception e) {
            Timber.e(e);
            return encryptedBytes;
        }
        return new byte[0];
    }

    public static String encryptGPG(String inputText, String passPhrase) {
        return new String(encryptGPG(inputText.getBytes(), passPhrase, true));
    }

    public static byte[] encryptGPG(byte[] inputBytes, String passPhrase, boolean asciiArmor) {
        try {
            return PGPLib.encryptGPG(inputBytes, passPhrase, asciiArmor);
        } catch (Exception e) {
            Timber.e(e);
        }
        return new byte[0];
    }

    public static String decryptGPG(String encryptedText, String passPhrase) {
        return new String(decryptGPG(encryptedText.getBytes(), passPhrase));
    }

    public static byte[] decryptGPG(byte[] encryptedBytes, String passPhrase) {
        try {
            return PGPLib.decryptGPG(encryptedBytes, passPhrase);
        } catch (PGPException e) {
            Timber.i(e);
        } catch (IOException e) {
            Timber.w(e);
        } catch (Exception e) {
            Timber.e(e);
            return encryptedBytes;
        }
        return new byte[0];
    }

    public static String decryptGPGUnsafe(String encryptedData, String passPhrase) throws IOException, PGPException {
        return new String(PGPLib.decryptGPG(encryptedData.getBytes(), passPhrase));
    }
}
