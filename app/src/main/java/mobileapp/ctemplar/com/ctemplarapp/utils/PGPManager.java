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
    private final boolean ASCII_ARMOR = true;
    private final boolean INTEGRITY_CHECK = true;
    private final int KEY_SIZE = 4096;

    public PGPManager() {
        pgpLib = new PGPLib();
    }

    public String encryptMessage(String message, String[] publicKeys) {
        return new String(encryptBytes(message.getBytes(), publicKeys));
    }

    byte[] encryptBytes(byte[] bytes, String[] publicKeys) {
        return encryptBytes(bytes, publicKeys, true);
    }

    byte[] encryptBytes(byte[] bytes, String[] pubicKeys, boolean asciiArmor) {

        ByteArrayInputStream inputMessageStream = new ByteArrayInputStream(bytes);
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
                    asciiArmor, INTEGRITY_CHECK);

        } catch (IOException | PGPException e) {
            Timber.e(e);

        } finally {
            try {
                inputMessageStream.close();
                outputMessageStream.close();
            } catch (IOException e) {
                Timber.e(e);
            }
        }

        return outputMessageStream.toByteArray();
    }

    public String decryptMessage(String message, String privateKey, String password) throws Exception {
        return new String(decryptBytes(message.getBytes(), privateKey, password));
    }

    byte[] decryptBytes(byte[] bytes, String privateKey, String password) throws Exception {

        ByteArrayInputStream inputMessageStream = new ByteArrayInputStream(bytes);
        ByteArrayInputStream privateKeyStream = new ByteArrayInputStream(privateKey.getBytes());
        ByteArrayOutputStream outputMessageStream = new ByteArrayOutputStream();

        pgpLib.decryptStream(inputMessageStream, privateKeyStream, password, outputMessageStream);

        try {
            inputMessageStream.close();
            outputMessageStream.close();
            privateKeyStream.close();
        } catch (IOException e) {
            Timber.e(e);
        }

        return outputMessageStream.toByteArray();
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
            keyStore.exportPublicKey(publicKeyStream, keyPairInformation.getKeyID(), ASCII_ARMOR);
            keyStore.exportPrivateKey(privateKeyStream, keyPairInformation.getKeyID(), ASCII_ARMOR);

            pgpKeyEntity.setFingerprint(fingerprint);
            pgpKeyEntity.setPublicKey(publicKeyStream.toString());
            pgpKeyEntity.setPrivateKey(privateKeyStream.toString());

        } catch (PGPException | IOException e) {
            e.printStackTrace();
            Timber.e(e);

        } finally {
            try {
                publicKeyStream.close();
                privateKeyStream.close();
            } catch (IOException e) {
                Timber.e(e);
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
            keyStore.exportPublicKey(publicKeyStream, keyId, ASCII_ARMOR);
            keyStore.exportPrivateKey(privateKeyStream, keyId, ASCII_ARMOR);

            pgpKeyEntity.setFingerprint(fingerprint);
            pgpKeyEntity.setPublicKey(publicKeyStream.toString());
            pgpKeyEntity.setPrivateKey(privateKeyStream.toString());

        } catch (PGPException | IOException e) {
            Timber.e(e);

        } finally {
            try {
                publicKeyStream.close();
                privateKeyStream.close();
            } catch (IOException e) {
                Timber.e(e);
            }
        }

        return pgpKeyEntity;
    }
}
