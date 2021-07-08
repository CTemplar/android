package mobileapp.ctemplar.com.ctemplarapp;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.Security;

import mobileapp.ctemplar.com.ctemplarapp.net.entity.PGPKeyEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.enums.KeyType;
import mobileapp.ctemplar.com.ctemplarapp.security.PGPManager;
import mobileapp.ctemplar.com.ctemplarapp.util.EncryptionUtils;
import mobileapp.ctemplar.com.ctemplarapp.util.ForeignAlphabetsStringGenerator;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncodeUtils;

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
    public void setup() throws PGPException, IOException {
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
    public void generationKeyEntitySuccess() {
        assertNotNull(pgpKeyEntity);
    }

    @Test
    public void keyTypeMatch() {
        KeyType keyType = PGPManager.getKeyType(pgpKeyEntity.getPrivateKey());
        assertEquals(KeyType.RSA4096, keyType);
    }

    @Test
    public void generatePublicKey() throws IOException, PGPException {
        String originalTextString = ForeignAlphabetsStringGenerator.randomString(STRING_LENGTH);
        String privateKey = pgpKeyEntity.getPrivateKey();

        PGPKeyEntity generatedPGPKeyEntity = PGPManager.generatePublicKey(privateKey,
                keyRingPassword, keyRingNewPassword);

        String[] publicKeys = {generatedPGPKeyEntity.getPublicKey()};

        String encryptedTextString = PGPManager.encrypt(originalTextString, publicKeys);

        assertTrue(EncryptionUtils.checkEncryptedMessage(encryptedTextString.trim()));

        String decryptedTextString = PGPManager.decrypt(encryptedTextString,
                generatedPGPKeyEntity.getPrivateKey(), keyRingNewPassword);

        assertEquals(originalTextString, decryptedTextString);
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
