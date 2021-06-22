package mobileapp.ctemplar.com.ctemplarapp;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.Security;

import mobileapp.ctemplar.com.ctemplarapp.net.entity.PGPKeyEntity;
import mobileapp.ctemplar.com.ctemplarapp.security.PGPManager;
import mobileapp.ctemplar.com.ctemplarapp.util.EncryptionUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncodeUtils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PGPKeyManageUnitTest {
    private PGPKeyEntity pgpKeyEntity;
    private PGPKeyEntity changedPgpKeyEntity;

    @Before
    public void setup() throws PGPException, IOException {
        Security.addProvider(new BouncyCastleProvider());

        String keyRingId = EncodeUtils.randomString(6);
        String keyRingPassword = EncodeUtils.randomString(8);
        String keyRingNewPassword = EncodeUtils.randomString(8);

        pgpKeyEntity = PGPManager.generateKeys(keyRingId, keyRingPassword);
        changedPgpKeyEntity = PGPManager.changePrivateKeyPassword(
                pgpKeyEntity, keyRingPassword, keyRingNewPassword
        );
    }

    @Test
    public void generationKeyEntitySuccess() {
        assertNotNull(pgpKeyEntity);
    }

    @Test
    public void keyEntityHasFingerprintSuccess() {
        assertFalse(pgpKeyEntity.getFingerprint().isEmpty());
    }

    @Test
    public void keyEntityPublicKeyIsCorrect() {
        String trimmedPublicKey = pgpKeyEntity.getPublicKey().trim();
        assertTrue(EncryptionUtils.checkPublicKey(trimmedPublicKey));
    }

    @Test
    public void keyEntityPrivateKeyIsCorrect() {
        String trimmedPrivateKey = pgpKeyEntity.getPrivateKey().trim();
        assertTrue(EncryptionUtils.checkPrivateKey(trimmedPrivateKey));
    }

    @Test
    public void keyEntityChangePasswordSuccess() {
        assertNotNull(changedPgpKeyEntity);
    }

    @Test
    public void changedKeyEntityHasFingerprintSuccess() {
        assertNotNull(changedPgpKeyEntity.getFingerprint());
    }

    @Test
    public void changedKeyEntityPublicKeyIsCorrect() {
        String trimmedPublicKey = changedPgpKeyEntity.getPublicKey().trim();
        assertTrue(EncryptionUtils.checkPublicKey(trimmedPublicKey));
    }

    @Test
    public void changedKeyEntityPrivateKeyIsCorrect() {
        String trimmedPrivateKey = changedPgpKeyEntity.getPrivateKey().trim();
        assertTrue(EncryptionUtils.checkPrivateKey(trimmedPrivateKey));
    }
}
