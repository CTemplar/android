package mobileapp.ctemplar.com.ctemplarapp.security;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRing;

import java.io.IOException;

import mobileapp.ctemplar.com.ctemplarapp.net.entity.PGPKeyEntity;
import timber.log.Timber;

 public class PGPManager {
    private static final int KEY_STRENGTH = 4096;

    public static PGPKeyEntity generateKeys(String keyRingId, String password) {
        try {
            PGPKeyRingGenerator pgpKeyRingGenerator = PGPLib.generateKeyRing(
                    password.toCharArray(), KEY_STRENGTH, keyRingId
            );
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
        PGPSecretKeyRing updatedPGPSecretKeyRing = PGPLib.updatePGPSecretKeyRing(
                pgpSecretKeyRing, oldPassword.toCharArray(), newPassword.toCharArray()
        );
        byte[] updatedPrivateKeyBytes = PGPLib.getPGPPrivateKey(updatedPGPSecretKeyRing);
        String keyFingerprint = PGPLib.getPGPKeyFingerprint(updatedPGPSecretKeyRing);

        return new PGPKeyEntity(publicKey, new String(updatedPrivateKeyBytes), keyFingerprint);
    }

    public static String encrypt(String inputText, String[] pubKeys) {
        return new String(encrypt(inputText.getBytes(), pubKeys, true));
    }

    public static String encrypt(String inputText, String[] pubKeys, boolean asciiArmor) {
        return new String(encrypt(inputText.getBytes(), pubKeys, asciiArmor));
    }

    public static byte[] encrypt(byte[] inputBytes, String[] pubKeys, boolean asciiArmor) {
        try {
            PGPPublicKeyRing[] pgpPublicKeyRings = PGPLib.getPGPPublicKeyRings(pubKeys);
            return PGPLib.encrypt(inputBytes, pgpPublicKeyRings, asciiArmor);
        } catch (IOException | PGPException e) {
            Timber.e(e);
        }
        return new byte[0];
    }

    public static String decrypt(String encryptedText, String privateKey, String password) {
        return new String(decrypt(encryptedText.getBytes(), privateKey, password));
    }

    public static byte[] decrypt(byte[] encryptedBytes, String privateKey, String password) {
        try {
            PGPSecretKeyRing pgpSecretKeyRing = PGPLib.getPGPSecretKeyRing(privateKey);
            return PGPLib.decrypt(encryptedBytes, pgpSecretKeyRing, password.toCharArray());
        } catch (PGPException e) {
            Timber.e(e);
        } catch (Exception e) {
            Timber.e(e);
            return encryptedBytes;
        }
        return new byte[0];
    }
}
