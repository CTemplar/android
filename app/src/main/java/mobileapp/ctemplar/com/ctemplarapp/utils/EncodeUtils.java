package mobileapp.ctemplar.com.ctemplarapp.utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import mobileapp.ctemplar.com.ctemplarapp.net.entity.PGPKeyEntity;
import mobileapp.ctemplar.com.ctemplarapp.net.request.MailboxKey;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.security.PGPManager;

public class EncodeUtils {

    private static final int MAX_SYMBOLS = 29;
    private static String ENCODE_SCHEME = "$2a$10$";

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
