package mobileapp.ctemplar.com.ctemplarapp.utils;

import com.didisoft.pgp.KeyPairInformation;
import com.didisoft.pgp.KeyStore;
import com.didisoft.pgp.PGPException;
import com.didisoft.pgp.PGPLib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import mobileapp.ctemplar.com.ctemplarapp.net.entity.PGPKeyEntity;
import timber.log.Timber;

public class PGPManager {
    private PGPLib pgpLib;
    private final boolean asciiArmor = true;
    private final boolean withIntegrityCheck = true;

    public PGPManager() {
        pgpLib = new PGPLib();
    }

    public String encryptMessage(String message, String[] pubicKeys) {
        ByteArrayInputStream inputMessageStream = new ByteArrayInputStream(message.getBytes());
        ByteArrayOutputStream outputMessageStream = new ByteArrayOutputStream();

        try {
            KeyStore keyStore = new KeyStore();
            long[] keyIds = new long[pubicKeys.length];

            for (int i = 0; i < pubicKeys.length; i++) {
                InputStream keyStream = new ByteArrayInputStream(pubicKeys[i].getBytes());
                KeyPairInformation[] keyInfo = keyStore.importPublicKey(keyStream);
                keyIds[i] = keyInfo[0].getKeyID();
            }

            pgpLib.encryptStream(inputMessageStream, "input", keyStore, keyIds, outputMessageStream,
                    asciiArmor, withIntegrityCheck);

        } catch (IOException | PGPException e) {
            Timber.e("Pgp encrypt error: %s", e.getMessage());

        } finally {
            try {
                inputMessageStream.close();
                outputMessageStream.close();

            } catch (IOException e) {
                Timber.e("Pgp close stream error: %s", e.getMessage());
            }
        }

        return outputMessageStream.toString();
    }

    public String decryptMessage(String message, String privateKey, String password) {
        ByteArrayInputStream inputMessageStream = new ByteArrayInputStream(message.getBytes());
        ByteArrayInputStream privateKeyStream = new ByteArrayInputStream(privateKey.getBytes());
        ByteArrayOutputStream outputMessageStream = new ByteArrayOutputStream();

        try {
            pgpLib.decryptStream(inputMessageStream, privateKeyStream, password, outputMessageStream);

        } catch (IOException | PGPException e) {
            Timber.e("Pgp encrypt error: %s", e.getMessage());

        } finally {
            try {
                inputMessageStream.close();
                outputMessageStream.close();
                privateKeyStream.close();

            } catch (IOException e) {
                Timber.e("Pgp close stream error: %s", e.getMessage());
            }
        }

        return outputMessageStream.toString();
    }

    public PGPKeyEntity generateKeys(String userId, String password) {

        return new PGPKeyEntity("", "");
    }

}
