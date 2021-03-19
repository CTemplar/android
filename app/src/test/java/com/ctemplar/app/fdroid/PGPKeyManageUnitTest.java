package com.ctemplar.app.fdroid;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.Security;

import com.ctemplar.app.fdroid.net.entity.PGPKeyEntity;
import com.ctemplar.app.fdroid.security.PGPManager;
import com.ctemplar.app.fdroid.util.EncryptionUtils;
import com.ctemplar.app.fdroid.utils.EncodeUtils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PGPKeyManageUnitTest {
    private PGPKeyEntity pgpKeyEntity;
    private PGPKeyEntity changedPgpKeyEntity;

    @Before
    public void setUp() throws IOException, PGPException {
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
    public void generationKeyEntity_Success() {
        assertNotNull(pgpKeyEntity);
    }

    @Test
    public void keyEntityHasFingerprint_Success() {
        assertFalse(pgpKeyEntity.getFingerprint().isEmpty());
    }

    @Test
    public void keyEntityPublicKey_isCorrect() {
        String trimmedPublicKey = pgpKeyEntity.getPublicKey().trim();
        assertTrue(EncryptionUtils.checkPublicKey(trimmedPublicKey));
    }

    @Test
    public void keyEntityPrivateKey_isCorrect() {
        String trimmedPrivateKey = pgpKeyEntity.getPrivateKey().trim();
        assertTrue(EncryptionUtils.checkPrivateKey(trimmedPrivateKey));
    }

    @Test
    public void keyEntityChangePassword_Success() {
        assertNotNull(changedPgpKeyEntity);
    }

    @Test
    public void changedKeyEntityHasFingerprint_Success() {
        assertNotNull(changedPgpKeyEntity.getFingerprint());
    }

    @Test
    public void changedKeyEntityPublicKey_isCorrect() {
        String trimmedPublicKey = changedPgpKeyEntity.getPublicKey().trim();
        assertTrue(EncryptionUtils.checkPublicKey(trimmedPublicKey));
    }

    @Test
    public void changedKeyEntityPrivateKey_isCorrect() {
        String trimmedPrivateKey = changedPgpKeyEntity.getPrivateKey().trim();
        assertTrue(EncryptionUtils.checkPrivateKey(trimmedPrivateKey));
    }
}
