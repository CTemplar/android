package mobileapp.ctemplar.com.ctemplarapp;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.Security;
import java.util.Random;

import mobileapp.ctemplar.com.ctemplarapp.net.entity.PGPKeyEntity;
import mobileapp.ctemplar.com.ctemplarapp.security.PGPManager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PGPKeyManageUnitTest {

    private final String BEGIN_PGP_PUBLIC_KEY_BLOCK = "-----BEGIN PGP PUBLIC KEY BLOCK-----";
    private final String BEGIN_PGP_PRIVATE_KEY_BLOCK = "-----BEGIN PGP PRIVATE KEY BLOCK-----";
    private final String END_PGP_PUBLIC_KEY_BLOCK = "-----END PGP PUBLIC KEY BLOCK-----";
    private final String END_PGP_PRIVATE_KEY_BLOCK = "-----END PGP PRIVATE KEY BLOCK-----";

    private PGPKeyEntity pgpKeyEntity;
    private PGPKeyEntity changedPgpKeyEntity;
    private String keyRingId;
    private String keyRingPassword;
    private String keyRingNewPassword;

    @Before
    public void setUp() throws IOException, PGPException {
        Security.addProvider(new BouncyCastleProvider());

        keyRingId = randomString(6);
        keyRingPassword = randomString(8);
        keyRingNewPassword = randomString(8);

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
        assertTrue(checkPublicKey(trimmedPublicKey));
    }

    @Test
    public void keyEntityPrivateKey_isCorrect() {
        String trimmedPrivateKey = pgpKeyEntity.getPrivateKey().trim();
        assertTrue(checkPrivateKey(trimmedPrivateKey));
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
        assertTrue(checkPublicKey(trimmedPublicKey));
    }

    @Test
    public void changedKeyEntityPrivateKey_isCorrect() {
        String trimmedPrivateKey = changedPgpKeyEntity.getPrivateKey().trim();
        assertTrue(checkPrivateKey(trimmedPrivateKey));
    }

    private boolean checkPublicKey(String publicKey) {
        boolean startWith = publicKey.startsWith(BEGIN_PGP_PUBLIC_KEY_BLOCK);
        boolean endWith = publicKey.endsWith(END_PGP_PUBLIC_KEY_BLOCK);
        return startWith && endWith;
    }

    private boolean checkPrivateKey(String privateKey) {
        boolean startWith = privateKey.startsWith(BEGIN_PGP_PRIVATE_KEY_BLOCK);
        boolean endWith = privateKey.endsWith(END_PGP_PRIVATE_KEY_BLOCK);
        return startWith && endWith;
    }

    private static String randomString(int length) {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(length);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
