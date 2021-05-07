package com.ctemplar.app.fdroid.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import com.ctemplar.app.fdroid.net.entity.PGPKeyEntity;
import com.ctemplar.app.fdroid.net.request.MailboxKey;
import com.ctemplar.app.fdroid.repository.entity.MailboxEntity;
import com.ctemplar.app.fdroid.security.PGPManager;
import timber.log.Timber;

public class EncodeUtils {
    private static final String MD5 = "MD5";
    private static final int MAX_SYMBOLS = 29;
    private static final String ENCODE_SCHEME = "$2a$10$";
    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
    private static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    public static char[] bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return hexChars;
    }

    public static String randomString(int length) {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(length);
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    public static String randomPass(int length) {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        char tempChar;
        for (int i = 0; i <= length; i++) {
            tempChar = ALPHABET[generator.nextInt(ALPHABET.length)];
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    private static String generateSaltWithUsername(String username, String salt) {
        if (username == null) {
            username = "";
        }
        username = username.replaceAll("[^a-zA-Z ]", "");
        username = username.isEmpty() ? "test" : username;
        if (salt.length() < MAX_SYMBOLS) {
            return generateSaltWithUsername(username, salt + username);
        } else {
            return salt.substring(0, MAX_SYMBOLS);
        }
    }

    public static String generateHash(String username, String password) {
        return BCrypt.hashpw(password, generateSaltWithUsername(username, ENCODE_SCHEME));
    }

    public static String md5(final String passPhrase) {
        try {
            MessageDigest digest = MessageDigest.getInstance(MD5);
            digest.update(passPhrase.getBytes());
            byte[] messageDigest = digest.digest();
            return new String(bytesToHex(messageDigest));
        } catch (NoSuchAlgorithmException e) {
            Timber.e(e);
        }
        return "";
    }

    public static Observable<PGPKeyEntity> getPGPKeyObservable(
            final String emailAddress,
            final String password
    ) {
        return Observable.fromCallable(()
                -> PGPManager.generateECCKeys(emailAddress, password))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<List<MailboxKey>> generateMailboxKeys(
            final List<MailboxEntity> mailboxEntities,
            final String oldPassword,
            final String password,
            final boolean resetKeys
    ) {
        return Observable.fromCallable(() -> {
            List<MailboxKey> mailboxKeys = new ArrayList<>();

            for (MailboxEntity mailboxEntity : mailboxEntities) {
                PGPKeyEntity pgpKeyEntity;
                if (resetKeys) {
                    pgpKeyEntity = PGPManager.generateECCKeys(
                            mailboxEntity.getEmail(), password
                    );
                } else {
                    PGPKeyEntity oldPgpKeyEntity = new PGPKeyEntity(
                            mailboxEntity.getPublicKey(),
                            mailboxEntity.getPrivateKey(),
                            mailboxEntity.getFingerprint()
                    );
                    pgpKeyEntity = PGPManager.changePrivateKeyPassword(
                            oldPgpKeyEntity, oldPassword, password
                    );
                }

                MailboxKey mailboxKey = new MailboxKey();
                mailboxKey.setMailboxId(mailboxEntity.getId());
                mailboxKey.setPrivateKey(pgpKeyEntity.getPrivateKey());
                mailboxKey.setPublicKey(pgpKeyEntity.getPublicKey());
                mailboxKeys.add(mailboxKey);
            }

            return mailboxKeys;
        }).subscribeOn(io.reactivex.schedulers.Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<PGPKeyEntity> generateAdditionalMailbox(
            final String emailAddress,
            final String password
    ) {
        return Observable.fromCallable(() -> PGPManager.generateECCKeys(emailAddress, password))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
