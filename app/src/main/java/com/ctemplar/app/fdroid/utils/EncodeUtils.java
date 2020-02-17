package com.ctemplar.app.fdroid.utils;

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

public class EncodeUtils {

    private static final int MAX_SYMBOLS = 29;
    private static final String ENCODE_SCHEME = "$2a$10$";
    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

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
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    private static String generateSaltWithUsername(String username, String salt) {
        if (username.isEmpty()) {
            return "";
        }

        username = username.replaceAll("[^a-zA-Z]", "");
        if(salt.length() < MAX_SYMBOLS) {
            return generateSaltWithUsername(username, salt + username);
        } else {
            return salt.substring(0, MAX_SYMBOLS);
        }
    }

    public static String generateHash(String username, String password) {
        return BCrypt.hashpw(password, generateSaltWithUsername(username, ENCODE_SCHEME));
    }

    public static Observable<PGPKeyEntity> getPGPKeyObservable(final String password) {
        return Observable.fromCallable(()
                -> PGPManager.generateKeys("user@ctemplar.com", password))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<List<MailboxKey>> generateMailboxKeys(final String username,
                                                                   final String oldPassword,
                                                                   final String password,
                                                                   final boolean resetKeys,
                                                                   final List<MailboxEntity> mailboxEntities) {

        return Observable.fromCallable(() -> {
            List<MailboxKey> mailboxKeys = new ArrayList<>();

            for (MailboxEntity mailboxEntity : mailboxEntities) {
                PGPKeyEntity pgpKeyEntity;
                if (resetKeys) {
                    pgpKeyEntity = PGPManager.generateKeys(username, password);
                } else {
                    PGPKeyEntity oldPgpKeyEntity = new PGPKeyEntity(
                            mailboxEntity.getPublicKey(), mailboxEntity.getPrivateKey(), mailboxEntity.getFingerprint()
                    );
                    pgpKeyEntity = PGPManager.changePrivateKeyPassword(oldPgpKeyEntity, oldPassword, password);
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

    public static Observable<PGPKeyEntity> generateAdditionalMailbox(final String username, final String password) {
        return Observable.fromCallable(() -> PGPManager.generateKeys(username, password))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
