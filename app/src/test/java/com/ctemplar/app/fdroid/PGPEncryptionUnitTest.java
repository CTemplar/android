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
import com.ctemplar.app.fdroid.util.ForeignAlphabetsStringGenerator;
import com.ctemplar.app.fdroid.utils.EncodeUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PGPEncryptionUnitTest {
    private static final int STRING_LENGTH = 1000;

    private PGPKeyEntity pgpKeyEntity;
    private PGPKeyEntity changedPgpKeyEntity;

    private String keyRingPassword;
    private String keyRingNewPassword;

    @Before
    public void setUp() throws IOException, PGPException {
        Security.addProvider(new BouncyCastleProvider());

        String keyRingId = EncodeUtils.randomString(6);
        keyRingPassword = EncodeUtils.randomString(8);
        keyRingNewPassword = EncodeUtils.randomString(8);

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
    public void textEncryptionAndDecryptionWithArmoring() {
        String originalTextString = ForeignAlphabetsStringGenerator.randomString(STRING_LENGTH);
        String[] publicKeys = {pgpKeyEntity.getPublicKey()};
        String privateKey = pgpKeyEntity.getPrivateKey();

        String encryptedTextString = PGPManager.encrypt(originalTextString, publicKeys);

        assertTrue(EncryptionUtils.checkEncryptedMessage(encryptedTextString.trim()));

        String decryptedTextString = PGPManager.decrypt(encryptedTextString,
                privateKey, keyRingPassword);

        assertEquals(originalTextString, decryptedTextString);
    }

    @Test
    public void textEncryptionAndDecryptionWithoutArmoring() {
        String originalTextString = ForeignAlphabetsStringGenerator.randomString(STRING_LENGTH);
        String[] publicKeys = {pgpKeyEntity.getPublicKey()};
        String privateKey = pgpKeyEntity.getPrivateKey();

        byte[] encryptedTextBytes = PGPManager.encrypt(originalTextString.getBytes(),
                publicKeys, false);

        assertNotEquals(null, encryptedTextBytes);
        assertNotEquals(0, encryptedTextBytes.length);

        byte[] decryptedTextBytes = PGPManager.decrypt(encryptedTextBytes,
                privateKey, keyRingPassword);

        assertEquals(originalTextString, new String(decryptedTextBytes));
    }

    @Test
    public void textEncryptionAndDecryptionWithChangedPass() {
        String originalTextString = ForeignAlphabetsStringGenerator.randomString(STRING_LENGTH);
        String[] publicKeys = {pgpKeyEntity.getPublicKey()};
        String privateKey = changedPgpKeyEntity.getPrivateKey();

        String encryptedTextString = PGPManager.encrypt(originalTextString, publicKeys);

        assertTrue(EncryptionUtils.checkEncryptedMessage(encryptedTextString.trim()));

        String decryptedTextString = PGPManager.decrypt(encryptedTextString,
                privateKey, keyRingNewPassword);

        assertEquals(originalTextString, decryptedTextString);
    }
}
