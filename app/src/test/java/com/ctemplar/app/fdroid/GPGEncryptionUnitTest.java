package com.ctemplar.app.fdroid;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Test;

import java.security.Security;

import com.ctemplar.app.fdroid.security.PGPManager;
import com.ctemplar.app.fdroid.util.EncryptionUtils;
import com.ctemplar.app.fdroid.util.ForeignAlphabetsStringGenerator;
import com.ctemplar.app.fdroid.utils.EncodeUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class GPGEncryptionUnitTest {
    private static final int STRING_LENGTH = 1000;

    private String passPhrase;

    @Before
    public void setup() {
        Security.addProvider(new BouncyCastleProvider());
        passPhrase = EncodeUtils.randomString(8);
    }

    @Test
    public void textEncryptionAndDecryptionWithArmoring() {
        String originalTextString = ForeignAlphabetsStringGenerator.randomString(STRING_LENGTH);

        String encryptedTextString = PGPManager.encryptGPG(originalTextString, passPhrase);

        assertTrue(EncryptionUtils.checkEncryptedMessage(encryptedTextString.trim()));

        String decryptedTextString = PGPManager.decryptGPG(encryptedTextString, passPhrase);

        assertEquals(originalTextString, decryptedTextString);
    }

    @Test
    public void textEncryptionAndDecryptionWithoutArmoring() {
        String originalTextString = ForeignAlphabetsStringGenerator.randomString(STRING_LENGTH);

        byte[] encryptedTextBytes = PGPManager.encryptGPG(originalTextString.getBytes(),
                passPhrase, false);

        assertNotEquals(null, encryptedTextBytes);
        assertNotEquals(0, encryptedTextBytes.length);

        byte[] decryptedTextBytes = PGPManager.decryptGPG(encryptedTextBytes, passPhrase);

        assertEquals(originalTextString, new String(decryptedTextBytes));
    }
}
