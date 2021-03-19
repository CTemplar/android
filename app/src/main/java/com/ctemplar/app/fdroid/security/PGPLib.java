package com.ctemplar.app.fdroid.security;

import com.ctemplar.app.fdroid.utils.EncodeUtils;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.bcpg.sig.Features;
import org.bouncycastle.bcpg.sig.KeyFlags;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.generators.X25519KeyPairGenerator;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.X25519KeyGenerationParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PBESecretKeyEncryptor;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.bc.BcPGPKeyPair;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Iterator;

class PGPLib {
    private static final int[] symmetricAlgorithms = new int[]{
            SymmetricKeyAlgorithmTags.AES_256,
            SymmetricKeyAlgorithmTags.AES_192,
            SymmetricKeyAlgorithmTags.AES_128
    };
    private static final int[] hashAlgorithms = new int[]{
            HashAlgorithmTags.SHA256,
            HashAlgorithmTags.SHA1,
            HashAlgorithmTags.SHA224,
            HashAlgorithmTags.SHA384,
            HashAlgorithmTags.SHA512,
    };

    static byte[] decrypt(byte[] encryptedBytes, PGPSecretKeyRing pgpSecretKeyRing, char[] passwordCharArray) throws Exception {
        InputStream in = new ByteArrayInputStream(encryptedBytes);
        in = PGPUtil.getDecoderStream(in);
        JcaPGPObjectFactory pgpF = new JcaPGPObjectFactory(in);
        PGPEncryptedDataList enc;
        Object o = pgpF.nextObject();
        if (o instanceof PGPEncryptedDataList) {
            enc = (PGPEncryptedDataList) o;
        } else {
            enc = (PGPEncryptedDataList) pgpF.nextObject();
        }

        PGPPrivateKey sKey = null;
        PGPPublicKeyEncryptedData pbe = null;
        if (enc == null) {
            return encryptedBytes;
        }
        int encryptedDataObjectSize = enc.size();
        for (int i = 0; sKey == null && i < encryptedDataObjectSize; i++) {
            pbe = (PGPPublicKeyEncryptedData) enc.get(i);
            sKey = getPrivateKey(pgpSecretKeyRing, pbe.getKeyID(), passwordCharArray);
        }

        if (pbe != null && sKey != null) {
            InputStream clear = pbe.getDataStream(new BcPublicKeyDataDecryptorFactory(sKey));
            JcaPGPObjectFactory pgpFact = new JcaPGPObjectFactory(clear);
            Object oData = pgpFact.nextObject();
            if (oData instanceof PGPCompressedData) {
                PGPCompressedData cData = (PGPCompressedData) oData;
                oData = new JcaPGPObjectFactory(cData.getDataStream()).nextObject();
            }
            PGPLiteralData ld = (PGPLiteralData) oData;
            InputStream unc = ld.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int ch;
            while ((ch = unc.read()) >= 0) {
                out.write(ch);
            }
            byte[] returnBytes = out.toByteArray();
            out.close();
            return returnBytes;
        }
        return new byte[0];
    }

    static byte[] encrypt(byte[] inputBytes, PGPPublicKeyRing[] pgpPublicKeyRings, boolean asciiArmor, boolean compression) throws IOException, PGPException {
        if (pgpPublicKeyRings.length <= 0) {
            return new byte[0];
        }
        ByteArrayOutputStream encOut = new ByteArrayOutputStream();
        OutputStream out = asciiArmor ? new ArmoredOutputStream(encOut) : encOut;
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(PGPCompressedDataGenerator.ZIP);
        OutputStream cos = compression ? comData.open(bOut) : bOut;
        PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();
        OutputStream pOut = lData.open(
                cos, PGPLiteralData.BINARY, PGPLiteralData.CONSOLE, inputBytes.length, new Date()
        );
        pOut.write(inputBytes);
        lData.close();
        comData.close();

        PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(
                new JcePGPDataEncryptorBuilder(PGPEncryptedData.AES_256)
                        .setWithIntegrityPacket(true)
                        .setSecureRandom(new SecureRandom())
                        .setProvider(new BouncyCastleProvider())
        );

        for (PGPPublicKeyRing pgpPublicKeyRing : pgpPublicKeyRings) {
            PGPPublicKey encKey = getPublicKey(pgpPublicKeyRing);
            if (encKey != null) {
                encGen.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(encKey)
                        .setProvider(new BouncyCastleProvider()));
            }
        }
        byte[] encryptedBytes = bOut.toByteArray();
        OutputStream cOut = encGen.open(out, encryptedBytes.length);
        cOut.write(encryptedBytes);
        cOut.close();
        out.close();
        return encOut.toByteArray();
    }

    static PGPKeyRingGenerator generateKeyRing(char[] passwordCharArray, int strength, String keyRingId) throws PGPException {
        RSAKeyPairGenerator kpg = new RSAKeyPairGenerator();
        kpg.init(new RSAKeyGenerationParameters(BigInteger.valueOf(0x10001), new SecureRandom(), strength, 12));

        PGPKeyPair rsakpSign = new BcPGPKeyPair(PGPPublicKey.RSA_GENERAL, kpg.generateKeyPair(), new Date());
        PGPKeyPair rsakpEnc = new BcPGPKeyPair(PGPPublicKey.RSA_GENERAL, kpg.generateKeyPair(), new Date());

        PGPSignatureSubpacketGenerator signhashgen = new PGPSignatureSubpacketGenerator();
        signhashgen.setKeyFlags(false, KeyFlags.SIGN_DATA | KeyFlags.CERTIFY_OTHER | KeyFlags.SHARED);
        signhashgen.setPreferredSymmetricAlgorithms(false, symmetricAlgorithms);
        signhashgen.setPreferredHashAlgorithms(false, hashAlgorithms);
        signhashgen.setFeature(false, Features.FEATURE_MODIFICATION_DETECTION);

        PGPSignatureSubpacketGenerator enchashgen = new PGPSignatureSubpacketGenerator();
        enchashgen.setKeyFlags(false, KeyFlags.ENCRYPT_COMMS | KeyFlags.ENCRYPT_STORAGE);

        PGPDigestCalculator sha1Calc = new BcPGPDigestCalculatorProvider().get(HashAlgorithmTags.SHA1);
        PGPDigestCalculator sha256Calc = new BcPGPDigestCalculatorProvider().get(HashAlgorithmTags.SHA256);

        PBESecretKeyEncryptor pske = (
                new BcPBESecretKeyEncryptorBuilder(PGPEncryptedData.AES_256, sha256Calc, 0xc0)
        ).build(passwordCharArray);

        PGPKeyRingGenerator keyRingGen = new PGPKeyRingGenerator(
                PGPSignature.POSITIVE_CERTIFICATION, rsakpSign, keyRingId, sha1Calc, signhashgen.generate(), null,
                new BcPGPContentSignerBuilder(rsakpSign.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1), pske
        );
        keyRingGen.addSubKey(rsakpEnc, enchashgen.generate(), null);
        return keyRingGen;
    }

    static PGPKeyRingGenerator generateECCKeyRing(char[] passPhrase, String identity) throws PGPException {
        Ed25519KeyPairGenerator edKp = new Ed25519KeyPairGenerator();
        edKp.init(new Ed25519KeyGenerationParameters(null));

        PGPKeyPair dsaKeyPair = new BcPGPKeyPair(PGPPublicKey.EDDSA, edKp.generateKeyPair(), new Date());

        X25519KeyPairGenerator dhKp = new X25519KeyPairGenerator();
        dhKp.init(new X25519KeyGenerationParameters(null));

        PGPKeyPair dhKeyPair = new BcPGPKeyPair(PGPPublicKey.ECDH, dhKp.generateKeyPair(), new Date());

        PGPDigestCalculator sha1Calc = new BcPGPDigestCalculatorProvider().get(HashAlgorithmTags.SHA1);

        PGPKeyRingGenerator keyRingGen = new PGPKeyRingGenerator(
                PGPSignature.POSITIVE_CERTIFICATION, dsaKeyPair,
                identity, sha1Calc, null, null,
                new BcPGPContentSignerBuilder(dsaKeyPair.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA256),
                new BcPBESecretKeyEncryptorBuilder(PGPEncryptedData.AES_256, sha1Calc).build(passPhrase));

        keyRingGen.addSubKey(dhKeyPair);
        return keyRingGen;
    }

    static PGPSecretKeyRing updatePGPSecretKeyRing(PGPSecretKeyRing keyRing, char[] oldPassword, char[] newPassword) throws PGPException {
        PGPDigestCalculator sha256Calc = new BcPGPDigestCalculatorProvider().get(HashAlgorithmTags.SHA256);
        PBESecretKeyDecryptor pskd = new BcPBESecretKeyDecryptorBuilder(
                new BcPGPDigestCalculatorProvider()
        ).build(oldPassword);
        PBESecretKeyEncryptor pske = (
                new BcPBESecretKeyEncryptorBuilder(PGPEncryptedData.AES_256, sha256Calc, 0xc0)
        ).build(newPassword);
        return PGPSecretKeyRing.copyWithNewPassword(keyRing, pskd, pske);
    }

    static PGPPublicKeyRing[] getPGPPublicKeyRings(String[] pubKeys) throws IOException {
        int keyCount = pubKeys.length;
        PGPPublicKeyRing[] pgpPublicKeyRings = new PGPPublicKeyRing[keyCount];
        for (int currentKeyNumber = 0; currentKeyNumber < keyCount; ++currentKeyNumber) {
            InputStream inputStream = new ByteArrayInputStream(pubKeys[currentKeyNumber].getBytes());
            PGPPublicKeyRing pgpPublicKeyRing = new PGPPublicKeyRing(
                    new ArmoredInputStream(inputStream), new JcaKeyFingerprintCalculator()
            );
            pgpPublicKeyRings[currentKeyNumber] = pgpPublicKeyRing;
        }
        return pgpPublicKeyRings;
    }

    static PGPSecretKeyRing getPGPSecretKeyRing(String privateKey) throws IOException, PGPException {
        InputStream inputStream = new ByteArrayInputStream(privateKey.getBytes());
        PGPSecretKeyRing pgpSecretKeyRing = new PGPSecretKeyRing(new ArmoredInputStream(inputStream), new JcaKeyFingerprintCalculator());
        inputStream.close();
        return pgpSecretKeyRing;
    }

    static byte[] getPGPPublicKey(PGPKeyRingGenerator pgpKeyRingGenerator) throws IOException {
        return getPGPPublicKey(pgpKeyRingGenerator.generatePublicKeyRing());
    }

    private static byte[] getPGPPublicKey(PGPPublicKeyRing pgpPublicKeyRing) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ArmoredOutputStream armoredOutputStream = new ArmoredOutputStream(byteArrayOutputStream);
        pgpPublicKeyRing.encode(armoredOutputStream);
        armoredOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    static byte[] getPGPPrivateKey(PGPKeyRingGenerator pgpKeyRingGenerator) throws IOException {
        return getPGPPrivateKey(pgpKeyRingGenerator.generateSecretKeyRing());
    }

    static byte[] getPGPPrivateKey(PGPSecretKeyRing pgpSecretKeyRing) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ArmoredOutputStream armoredOutputStream = new ArmoredOutputStream(byteArrayOutputStream);
        pgpSecretKeyRing.encode(armoredOutputStream);
        armoredOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    static String getPGPKeyFingerprint(PGPSecretKeyRing pgpSecretKeyRing) {
        byte[] fingerprintBytes = pgpSecretKeyRing.getPublicKey().getFingerprint();
        return new String(EncodeUtils.bytesToHex(fingerprintBytes));
    }

    private static PGPPublicKey getPublicKey(PGPPublicKeyRing publicKeyRing) {
        Iterator<?> kIt = publicKeyRing.getPublicKeys();
        while (kIt.hasNext()) {
            PGPPublicKey k = (PGPPublicKey) kIt.next();
            if (k.isEncryptionKey()) {
                return k;
            }
        }
        return null;
    }

    private static PGPPrivateKey getPrivateKey(PGPSecretKeyRing keyRing, long keyID, char[] pass) throws PGPException {
        PGPSecretKey secretKey = keyRing.getSecretKey(keyID);
        if (secretKey == null) {
            return null;
        }
        PBESecretKeyDecryptor decryptor = new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider()).build(pass);
        return secretKey.extractPrivateKey(decryptor);
    }
}
