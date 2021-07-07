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
import com.ctemplar.app.fdroid.utils.ArrayUtils;
import com.ctemplar.app.fdroid.utils.EncodeUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PGPEncryptionWithMultipleKeys {
    private static final int STRING_LENGTH = 1000;

    private PGPKeyEntity firstPGPRSAKeyEntity;
    private PGPKeyEntity secondPGPRSAKeyEntity;
    private PGPKeyEntity thirdPGPRSAKeyEntity;
    private PGPKeyEntity thirdPGPRSAKeyUpdatedEntity;

    private PGPKeyEntity fourthPGPECCKeyEntity;
    private PGPKeyEntity fifthPGPECCKeyEntity;
    private PGPKeyEntity sixthPGPECCKeyEntity;
    private PGPKeyEntity sixthPGPECCKeyUpdatedEntity;

    private String[] rsaPublicKeys;
    private String[] eccPublicKeys;
    private String[] allPublicKeys;

    private String firstRSAPrivateKey;
    private String secondRSAPrivateKey;
    private String thirdRSAPrivateKey;
    private String thirdRSAPrivateUpdatedKey;

    private String fourthECCPrivateKey;
    private String fifthECCPrivateKey;
    private String sixthECCPrivateKey;
    private String sixthECCPrivateUpdatedKey;

    private String firstRSAKeyRingPassword;
    private String secondRSAKeyRingPassword;
    private String thirdRSAKeyRingPassword;
    private String thirdRSAKeyRingNewPassword;

    private String fourthECCKeyRingPassword;
    private String fifthECCKeyRingPassword;
    private String sixthECCKeyRingPassword;
    private String sixthECCKeyRingNewPassword;

    @Before
    public void setup() throws PGPException, IOException {
        Security.addProvider(new BouncyCastleProvider());

        String firstRSAKeyRingId = EncodeUtils.randomString(6);
        String secondRSAKeyRingId = EncodeUtils.randomString(6);
        String thirdRSAKeyRingId = EncodeUtils.randomString(6);

        String fourthECCKeyRingId = EncodeUtils.randomString(6);
        String fifthECCKeyRingId = EncodeUtils.randomString(6);
        String sixthECCKeyRingId = EncodeUtils.randomString(6);

        firstRSAKeyRingPassword = EncodeUtils.randomString(8);
        secondRSAKeyRingPassword = EncodeUtils.randomString(8);
        thirdRSAKeyRingPassword = EncodeUtils.randomString(8);
        thirdRSAKeyRingNewPassword = EncodeUtils.randomString(8);

        fourthECCKeyRingPassword = EncodeUtils.randomString(8);
        fifthECCKeyRingPassword = EncodeUtils.randomString(8);
        sixthECCKeyRingPassword = EncodeUtils.randomString(8);
        sixthECCKeyRingNewPassword = EncodeUtils.randomString(8);

        firstPGPRSAKeyEntity = PGPManager.generateKeys(firstRSAKeyRingId, firstRSAKeyRingPassword);
        secondPGPRSAKeyEntity = PGPManager.generateKeys(secondRSAKeyRingId, secondRSAKeyRingPassword);
        thirdPGPRSAKeyEntity = PGPManager.generateKeys(thirdRSAKeyRingId, thirdRSAKeyRingPassword);
        thirdPGPRSAKeyUpdatedEntity = PGPManager.changePrivateKeyPassword(
                thirdPGPRSAKeyEntity, thirdRSAKeyRingPassword, thirdRSAKeyRingNewPassword
        );

        fourthPGPECCKeyEntity = PGPManager.generateKeys(fourthECCKeyRingId, fourthECCKeyRingPassword);
        fifthPGPECCKeyEntity = PGPManager.generateKeys(fifthECCKeyRingId, fifthECCKeyRingPassword);
        sixthPGPECCKeyEntity = PGPManager.generateKeys(sixthECCKeyRingId, sixthECCKeyRingPassword);
        sixthPGPECCKeyUpdatedEntity = PGPManager.changePrivateKeyPassword(
                sixthPGPECCKeyEntity, sixthECCKeyRingPassword, sixthECCKeyRingNewPassword
        );

        rsaPublicKeys = new String[]{
                firstPGPRSAKeyEntity.getPublicKey(),
                secondPGPRSAKeyEntity.getPublicKey(),
                thirdPGPRSAKeyEntity.getPublicKey(),
                thirdPGPRSAKeyUpdatedEntity.getPublicKey()
        };

        eccPublicKeys = new String[]{
                fourthPGPECCKeyEntity.getPublicKey(),
                fifthPGPECCKeyEntity.getPublicKey(),
                sixthPGPECCKeyEntity.getPublicKey(),
                sixthPGPECCKeyUpdatedEntity.getPublicKey()
        };

        allPublicKeys = ArrayUtils.concat(rsaPublicKeys, eccPublicKeys);

        firstRSAPrivateKey = firstPGPRSAKeyEntity.getPrivateKey();
        secondRSAPrivateKey = secondPGPRSAKeyEntity.getPrivateKey();
        thirdRSAPrivateKey = thirdPGPRSAKeyEntity.getPrivateKey();
        thirdRSAPrivateUpdatedKey = thirdPGPRSAKeyUpdatedEntity.getPrivateKey();

        fourthECCPrivateKey = fourthPGPECCKeyEntity.getPrivateKey();
        fifthECCPrivateKey = fifthPGPECCKeyEntity.getPrivateKey();
        sixthECCPrivateKey = sixthPGPECCKeyEntity.getPrivateKey();
        sixthECCPrivateUpdatedKey = sixthPGPECCKeyUpdatedEntity.getPrivateKey();
    }

    @Test
    public void checkRSAKeyEntities() {
        assertNotNull(firstPGPRSAKeyEntity);
        assertNotNull(secondPGPRSAKeyEntity);
        assertNotNull(thirdPGPRSAKeyEntity);
        assertNotNull(thirdPGPRSAKeyUpdatedEntity);
    }

    @Test
    public void checkECCKeyEntities() {
        assertNotNull(fourthPGPECCKeyEntity);
        assertNotNull(fifthPGPECCKeyEntity);
        assertNotNull(sixthPGPECCKeyEntity);
        assertNotNull(sixthPGPECCKeyUpdatedEntity);
    }

    @Test
    public void textRSAEncryptionAndDecryptionWithArmoring() {
        String originalTextString = ForeignAlphabetsStringGenerator.randomString(STRING_LENGTH);

        String encryptedTextString = PGPManager.encrypt(originalTextString, rsaPublicKeys);

        assertTrue(EncryptionUtils.checkEncryptedMessage(encryptedTextString.trim()));

        String firstDecryptedTextString = PGPManager.decrypt(encryptedTextString,
                firstRSAPrivateKey, firstRSAKeyRingPassword);

        assertEquals(originalTextString, firstDecryptedTextString);

        String secondDecryptedTextString = PGPManager.decrypt(encryptedTextString,
                secondRSAPrivateKey, secondRSAKeyRingPassword);

        assertEquals(originalTextString, secondDecryptedTextString);

        String thirdDecryptedTextString = PGPManager.decrypt(encryptedTextString,
                thirdRSAPrivateKey, thirdRSAKeyRingPassword);

        assertEquals(originalTextString, thirdDecryptedTextString);

        String thirdUpdatedDecryptedTextString = PGPManager.decrypt(encryptedTextString,
                thirdRSAPrivateUpdatedKey, thirdRSAKeyRingNewPassword);

        assertEquals(originalTextString, thirdUpdatedDecryptedTextString);
    }

    @Test
    public void textRSAEncryptionAndDecryptionWithoutArmoring() {
        String originalTextString = ForeignAlphabetsStringGenerator.randomString(STRING_LENGTH);

        byte[] encryptedTextBytes = PGPManager.encrypt(originalTextString.getBytes(),
                rsaPublicKeys, false);

        assertNotEquals(null, encryptedTextBytes);
        assertNotEquals(0, encryptedTextBytes.length);

        byte[] firstDecryptedTextBytes = PGPManager.decrypt(encryptedTextBytes,
                firstRSAPrivateKey, firstRSAKeyRingPassword);

        assertEquals(originalTextString, new String(firstDecryptedTextBytes));

        byte[] secondDecryptedTextBytes = PGPManager.decrypt(encryptedTextBytes,
                secondRSAPrivateKey, secondRSAKeyRingPassword);

        assertEquals(originalTextString, new String(secondDecryptedTextBytes));

        byte[] thirdDecryptedTextBytes = PGPManager.decrypt(encryptedTextBytes,
                thirdRSAPrivateKey, thirdRSAKeyRingPassword);

        assertEquals(originalTextString, new String(thirdDecryptedTextBytes));

        byte[] thirdUpdatedDecryptedTextBytes = PGPManager.decrypt(encryptedTextBytes,
                thirdRSAPrivateUpdatedKey, thirdRSAKeyRingNewPassword);

        assertEquals(originalTextString, new String(thirdUpdatedDecryptedTextBytes));
    }

    @Test
    public void textECCEncryptionAndDecryptionWithArmoring() {
        String originalTextString = ForeignAlphabetsStringGenerator.randomString(STRING_LENGTH);

        String encryptedTextString = PGPManager.encrypt(originalTextString, eccPublicKeys);

        assertTrue(EncryptionUtils.checkEncryptedMessage(encryptedTextString.trim()));

        String fourthDecryptedTextString = PGPManager.decrypt(encryptedTextString,
                fourthECCPrivateKey, fourthECCKeyRingPassword);

        assertEquals(originalTextString, fourthDecryptedTextString);

        String fifthDecryptedTextString = PGPManager.decrypt(encryptedTextString,
                fifthECCPrivateKey, fifthECCKeyRingPassword);

        assertEquals(originalTextString, fifthDecryptedTextString);

        String sixthDecryptedTextString = PGPManager.decrypt(encryptedTextString,
                sixthECCPrivateKey, sixthECCKeyRingPassword);

        assertEquals(originalTextString, sixthDecryptedTextString);

        String sixthUpdatedDecryptedTextString = PGPManager.decrypt(encryptedTextString,
                sixthECCPrivateUpdatedKey, sixthECCKeyRingNewPassword);

        assertEquals(originalTextString, sixthUpdatedDecryptedTextString);
    }

    @Test
    public void textECCEncryptionAndDecryptionWithoutArmoring() {
        String originalTextString = ForeignAlphabetsStringGenerator.randomString(STRING_LENGTH);

        byte[] encryptedTextBytes = PGPManager.encrypt(originalTextString.getBytes(),
                eccPublicKeys, false);

        assertNotEquals(null, encryptedTextBytes);
        assertNotEquals(0, encryptedTextBytes.length);

        byte[] fourthDecryptedTextBytes = PGPManager.decrypt(encryptedTextBytes,
                fourthECCPrivateKey, fourthECCKeyRingPassword);

        assertEquals(originalTextString, new String(fourthDecryptedTextBytes));

        byte[] fifthDecryptedTextBytes = PGPManager.decrypt(encryptedTextBytes,
                fifthECCPrivateKey, fifthECCKeyRingPassword);

        assertEquals(originalTextString, new String(fifthDecryptedTextBytes));

        byte[] sixthDecryptedTextBytes = PGPManager.decrypt(encryptedTextBytes,
                sixthECCPrivateKey, sixthECCKeyRingPassword);

        assertEquals(originalTextString, new String(sixthDecryptedTextBytes));

        byte[] sixthUpdatedDecryptedTextBytes = PGPManager.decrypt(encryptedTextBytes,
                sixthECCPrivateUpdatedKey, sixthECCKeyRingNewPassword);

        assertEquals(originalTextString, new String(sixthUpdatedDecryptedTextBytes));
    }

    @Test
    public void textMixedEncryptionAndDecryptionWithArmoring() {
        String originalTextString = ForeignAlphabetsStringGenerator.randomString(STRING_LENGTH);

        String encryptedTextString = PGPManager.encrypt(originalTextString, allPublicKeys);

        assertTrue(EncryptionUtils.checkEncryptedMessage(encryptedTextString.trim()));

        assertTrue(EncryptionUtils.checkEncryptedMessage(encryptedTextString.trim()));

        String firstDecryptedTextString = PGPManager.decrypt(encryptedTextString,
                firstRSAPrivateKey, firstRSAKeyRingPassword);

        assertEquals(originalTextString, firstDecryptedTextString);

        String secondDecryptedTextString = PGPManager.decrypt(encryptedTextString,
                secondRSAPrivateKey, secondRSAKeyRingPassword);

        assertEquals(originalTextString, secondDecryptedTextString);

        String thirdDecryptedTextString = PGPManager.decrypt(encryptedTextString,
                thirdRSAPrivateKey, thirdRSAKeyRingPassword);

        assertEquals(originalTextString, thirdDecryptedTextString);

        String thirdUpdatedDecryptedTextString = PGPManager.decrypt(encryptedTextString,
                thirdRSAPrivateUpdatedKey, thirdRSAKeyRingNewPassword);

        assertEquals(originalTextString, thirdUpdatedDecryptedTextString);

        assertTrue(EncryptionUtils.checkEncryptedMessage(encryptedTextString.trim()));

        String fourthDecryptedTextString = PGPManager.decrypt(encryptedTextString,
                fourthECCPrivateKey, fourthECCKeyRingPassword);

        assertEquals(originalTextString, fourthDecryptedTextString);

        String fifthDecryptedTextString = PGPManager.decrypt(encryptedTextString,
                fifthECCPrivateKey, fifthECCKeyRingPassword);

        assertEquals(originalTextString, fifthDecryptedTextString);

        String sixthDecryptedTextString = PGPManager.decrypt(encryptedTextString,
                sixthECCPrivateKey, sixthECCKeyRingPassword);

        assertEquals(originalTextString, sixthDecryptedTextString);

        String sixthUpdatedDecryptedTextString = PGPManager.decrypt(encryptedTextString,
                sixthECCPrivateUpdatedKey, sixthECCKeyRingNewPassword);

        assertEquals(originalTextString, sixthUpdatedDecryptedTextString);
    }

    @Test
    public void textMixedEncryptionAndDecryptionWithoutArmoring() {
        String originalTextString = ForeignAlphabetsStringGenerator.randomString(STRING_LENGTH);

        byte[] encryptedTextBytes = PGPManager.encrypt(originalTextString.getBytes(),
                allPublicKeys, false);

        assertNotEquals(null, encryptedTextBytes);
        assertNotEquals(0, encryptedTextBytes.length);

        byte[] firstDecryptedTextBytes = PGPManager.decrypt(encryptedTextBytes,
                firstRSAPrivateKey, firstRSAKeyRingPassword);

        assertEquals(originalTextString, new String(firstDecryptedTextBytes));

        byte[] secondDecryptedTextBytes = PGPManager.decrypt(encryptedTextBytes,
                secondRSAPrivateKey, secondRSAKeyRingPassword);

        assertEquals(originalTextString, new String(secondDecryptedTextBytes));

        byte[] thirdDecryptedTextBytes = PGPManager.decrypt(encryptedTextBytes,
                thirdRSAPrivateKey, thirdRSAKeyRingPassword);

        assertEquals(originalTextString, new String(thirdDecryptedTextBytes));

        byte[] thirdUpdatedDecryptedTextBytes = PGPManager.decrypt(encryptedTextBytes,
                thirdRSAPrivateUpdatedKey, thirdRSAKeyRingNewPassword);

        assertEquals(originalTextString, new String(thirdUpdatedDecryptedTextBytes));

        byte[] fourthDecryptedTextBytes = PGPManager.decrypt(encryptedTextBytes,
                fourthECCPrivateKey, fourthECCKeyRingPassword);

        assertEquals(originalTextString, new String(fourthDecryptedTextBytes));

        byte[] fifthDecryptedTextBytes = PGPManager.decrypt(encryptedTextBytes,
                fifthECCPrivateKey, fifthECCKeyRingPassword);

        assertEquals(originalTextString, new String(fifthDecryptedTextBytes));

        byte[] sixthDecryptedTextBytes = PGPManager.decrypt(encryptedTextBytes,
                sixthECCPrivateKey, sixthECCKeyRingPassword);

        assertEquals(originalTextString, new String(sixthDecryptedTextBytes));

        byte[] sixthUpdatedDecryptedTextBytes = PGPManager.decrypt(encryptedTextBytes,
                sixthECCPrivateUpdatedKey, sixthECCKeyRingNewPassword);

        assertEquals(originalTextString, new String(sixthUpdatedDecryptedTextBytes));
    }
}
