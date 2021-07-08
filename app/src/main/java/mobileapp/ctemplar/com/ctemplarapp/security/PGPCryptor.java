package mobileapp.ctemplar.com.ctemplarapp.security;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

class PGPCryptor {
    static PGPSecretKeyRing getPGPSecretKeyRing(String privateKey) throws PGPCryptorException, PGPBadPrivateKeyException {
        try (InputStream inputStream = new ByteArrayInputStream(privateKey.getBytes())) {
            return new PGPSecretKeyRing(new ArmoredInputStream(inputStream),
                    new JcaKeyFingerprintCalculator());
        } catch (IOException e) {
            throw new PGPCryptorException("IOException occurs: " + e.getMessage(), e);
        } catch (PGPException e) {
            throw new PGPBadPrivateKeyException(e);
        }
    }

    static byte[] decrypt(byte[] content, List<String> privateKeys, String passPhrase)
            throws PGPCryptorException, PGPBadPrivateKeyException, PGPCryptorPublicKeysNotFound, PGPCryptorPrivateKeyExtractFailed, PGPCryptorPrivateKeyNotFound, PGPCryptorDataNotFound, PGPCryptorReadDataFailed {
        Map<Long, PGPSecretKeyRing> secretKeyRingMap = new HashMap<>();
        Exception lastException = null;
        for (String privateKey : privateKeys) {
            PGPSecretKeyRing secretKeyRing;
            try {
                secretKeyRing = getPGPSecretKeyRing(privateKey);
            } catch (PGPCryptorException | PGPBadPrivateKeyException e) {
                lastException = e;
                Timber.e(e);
                continue;
            }
            for (Iterator<PGPPublicKey> it = secretKeyRing.getPublicKeys(); it.hasNext(); ) {
                PGPPublicKey publicKey = it.next();
                secretKeyRingMap.put(publicKey.getKeyID(), secretKeyRing);
            }
        }
        if (secretKeyRingMap.isEmpty()) {
            if (lastException == null) {
                throw new PGPCryptorPublicKeysNotFound();
            }
            if (lastException instanceof PGPCryptorException) {
                throw (PGPCryptorException) lastException;
            }
            throw (PGPBadPrivateKeyException) lastException;
        }
        PGPContentReader pgpContentReader;
        try {
            pgpContentReader = PGPContentReader.read(content);
        } catch (IOException e) {
            throw new PGPCryptorException("Failed to decode content", e);
        }
        List<PGPPublicKeyEncryptedData> publicKeyEncryptedDatum = pgpContentReader.getPublicKeyEncryptedDatum();
        if (publicKeyEncryptedDatum == null || publicKeyEncryptedDatum.isEmpty()) {
            throw new PGPCryptorPublicKeysNotFound();
        }
        PGPPrivateKey privateKey = null;
        PGPPublicKeyEncryptedData publicKeyEncryptedData = null;
        char[] pass = passPhrase.toCharArray();
        lastException = null;
        for (PGPPublicKeyEncryptedData pgpPublicKeyEncryptedData : publicKeyEncryptedDatum) {
            long keyId = pgpPublicKeyEncryptedData.getKeyID();
            PGPSecretKeyRing pgpSecretKeyRing = secretKeyRingMap.get(pgpPublicKeyEncryptedData.getKeyID());
            if (pgpSecretKeyRing == null) {
                continue;
            }
            PGPSecretKey secretKey = pgpSecretKeyRing.getSecretKey(keyId);
            if (secretKey == null) {
                Timber.wtf("Secret key not found!!!");
                continue;
            }
            PBESecretKeyDecryptor decryptor = new BcPBESecretKeyDecryptorBuilder(
                    new BcPGPDigestCalculatorProvider()).build(pass);
            PGPPrivateKey pgpPrivateKey;
            try {
                pgpPrivateKey = secretKey.extractPrivateKey(decryptor);
            } catch (PGPException e) {
                Timber.e(e, "Extract private key failed");
                lastException = new PGPCryptorPrivateKeyExtractFailed(e);
                continue;
            }
            if (pgpPrivateKey == null) {
                Timber.e("Private key is empty");
                continue;
            }
            privateKey = pgpPrivateKey;
            publicKeyEncryptedData = pgpPublicKeyEncryptedData;
            break;
        }
        if (privateKey == null) {
            if (lastException != null) {
                throw (PGPCryptorPrivateKeyExtractFailed) lastException;
            }
            throw new PGPCryptorPrivateKeyNotFound();
        }
        try {
            pgpContentReader = PGPContentReader.read(publicKeyEncryptedData, privateKey);
        } catch (PGPException e) {
            throw new PGPCryptorException("Failed to encrypt", e);
        }
        PGPLiteralData literalData = pgpContentReader.getUncompressedLiteralData();
        if (literalData == null) {
            throw new PGPCryptorDataNotFound();
        }
        InputStream unc = literalData.getInputStream();
        byte[] returnBytes;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            int ch;
            while ((ch = unc.read()) >= 0) {
                out.write(ch);
            }
            returnBytes = out.toByteArray();
        } catch (IOException e) {
            throw new PGPCryptorReadDataFailed(e);
        }
        return returnBytes;
    }
}

class PGPCryptorException extends Exception {
    PGPCryptorException(String message) {
        super(message);
    }

    PGPCryptorException(String message, Throwable cause) {
        super(message, cause);
    }
}

class PGPBadPrivateKeyException extends Exception {
    PGPBadPrivateKeyException() {
        super("Bad private key");
    }

    PGPBadPrivateKeyException(Throwable cause) {
        super("Bad private key", cause);
    }
}

class PGPCryptorPublicKeysNotFound extends Exception {
    PGPCryptorPublicKeysNotFound() {
        super("Public keys not found");
    }
}

class PGPCryptorPrivateKeyNotFound extends Exception {
    PGPCryptorPrivateKeyNotFound() {
        super("Private key not found");
    }
}

class PGPCryptorPrivateKeyExtractFailed extends Exception {
    PGPCryptorPrivateKeyExtractFailed(Throwable cause) {
        super("Private key extract failed. Maybe password is invalid? (" + cause.getMessage() + ")", cause);
    }

    PGPCryptorPrivateKeyExtractFailed() {
        super("Private key extract failed. Maybe password is invalid?");
    }
}

class PGPCryptorDataNotFound extends Exception {
    PGPCryptorDataNotFound() {
        super("Data not found");
    }
}

class PGPCryptorReadDataFailed extends Exception {
    PGPCryptorReadDataFailed() {
        super("Read data failed");
    }

    PGPCryptorReadDataFailed(Throwable cause) {
        super("Read data failed (" + cause.getMessage() + ")", cause);
    }
}
