package mobileapp.ctemplar.com.ctemplarapp.utils;

import com.didisoft.pgp.CompressionAlgorithm;
import com.didisoft.pgp.CypherAlgorithm;
import com.didisoft.pgp.HashAlgorithm;
import com.didisoft.pgp.KeyAlgorithm;
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
    private final int KEY_SIZE = 4096;
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
            e.printStackTrace();
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
            Timber.e("Pgp decrypt error: %s", e.getMessage());

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

        PGPKeyEntity pgpKeyEntity = new PGPKeyEntity();
        ByteArrayOutputStream publicKeyStream = new ByteArrayOutputStream();
        ByteArrayOutputStream privateKeyStream = new ByteArrayOutputStream();

        KeyAlgorithm keyAlgorithm = KeyAlgorithm.RSA;

        CompressionAlgorithm[] compressionAlgorithms = new CompressionAlgorithm[] {
                CompressionAlgorithm.ZIP,
                CompressionAlgorithm.ZLIB,
                CompressionAlgorithm.UNCOMPRESSED
        };

        HashAlgorithm[] hashAlgorithms = new HashAlgorithm[] {
                HashAlgorithm.SHA256,
                HashAlgorithm.SHA384,
                HashAlgorithm.SHA512
        };

        CypherAlgorithm[] cypherAlgorithm = new CypherAlgorithm[] {
                CypherAlgorithm.AES_128,
                CypherAlgorithm.AES_192,
                CypherAlgorithm.AES_256,
                CypherAlgorithm.TWOFISH
        };

        try {
            KeyStore keyStore = new KeyStore();
            KeyPairInformation keyPairInformation = keyStore.generateKeyPair(
                    KEY_SIZE,
                    userId,
                    keyAlgorithm,
                    password,
                    compressionAlgorithms,
                    hashAlgorithms,
                    cypherAlgorithm
             );

            String fingerprint = keyPairInformation.getFingerprint();
            keyStore.exportPublicKey(publicKeyStream, keyPairInformation.getKeyID(), asciiArmor);
            keyStore.exportPrivateKey(privateKeyStream, keyPairInformation.getKeyID(), asciiArmor);

            pgpKeyEntity.setFingerprint(fingerprint);
            pgpKeyEntity.setPublicKey(publicKeyStream.toString());
            pgpKeyEntity.setPrivateKey(privateKeyStream.toString());

        } catch (PGPException | IOException e) {
            e.printStackTrace();
            Timber.e("Pgp generation key error: %s", e.getMessage());

        } finally {
            try {
                publicKeyStream.close();
                privateKeyStream.close();
            } catch (IOException e) {
                Timber.e("Pgp close stream error: %s", e.getMessage());
            }
        }

        return pgpKeyEntity;
    }

    public PGPKeyEntity changePrivateKeyPassword(String privateKey, String oldPassword, String password) {
        PGPKeyEntity pgpKeyEntity = new PGPKeyEntity();
        ByteArrayOutputStream publicKeyStream = new ByteArrayOutputStream();
        ByteArrayOutputStream privateKeyStream = new ByteArrayOutputStream();

        try {
            KeyStore keyStore = new KeyStore();

            InputStream inputPrivateKeyStream = new ByteArrayInputStream(privateKey.getBytes());
            KeyPairInformation[] keyPairInformation = keyStore.importPrivateKey(inputPrivateKeyStream);
            long keyId = keyPairInformation[0].getKeyID();

            keyStore.changePrivateKeyPassword(keyId, oldPassword, password);

            String fingerprint = keyPairInformation[0].getFingerprint();
            keyStore.exportPublicKey(publicKeyStream, keyId, asciiArmor);
            keyStore.exportPrivateKey(privateKeyStream, keyId, asciiArmor);

            pgpKeyEntity.setFingerprint(fingerprint);
            pgpKeyEntity.setPublicKey(publicKeyStream.toString());
            pgpKeyEntity.setPrivateKey(privateKeyStream.toString());

        } catch (PGPException | IOException e) {
            e.printStackTrace();
            Timber.e("Pgp regeneration key error: %s", e.getMessage());

        } finally {
            try {
                publicKeyStream.close();
                privateKeyStream.close();
            } catch (IOException e) {
                Timber.e("Pgp close stream error: %s", e.getMessage());
            }
        }

        return pgpKeyEntity;
    }
}
