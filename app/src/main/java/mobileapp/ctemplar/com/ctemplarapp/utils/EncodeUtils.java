package mobileapp.ctemplar.com.ctemplarapp.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import mobileapp.ctemplar.com.ctemplarapp.net.entity.PGPKeyEntity;
import mobileapp.ctemplar.com.ctemplarapp.net.request.MailboxKey;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;

public class EncodeUtils {

    private static final int MAX_SYMBOLS = 29;
    private static String ENCODE_SCHEME = "$2a$10$";

    private static String generateSaltWithUsername(String username, String salt) {
        username = username.replaceAll("[^a-zA-Z]", "");

        if(salt.length() < MAX_SYMBOLS) {
            return generateSaltWithUsername(username, salt + username);
        } else {
            return salt.substring(0, MAX_SYMBOLS);
        }
    }

    public static String encodePassword(String username, String password) {
        return BCrypt.hashpw(password, generateSaltWithUsername(username, ENCODE_SCHEME));
    }

    public static Observable<PGPKeyEntity> getPGPKeyObservable(final String password) {
        return Observable.fromCallable(new Callable<PGPKeyEntity>() {
            @Override
            public PGPKeyEntity call() throws Exception {
                PGPManager pgpManager = new PGPManager();
                return pgpManager.generateKeys("name <name@domain.com>", password);
            }
        }).subscribeOn(io.reactivex.schedulers.Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<List<MailboxKey>> generateMailboxKeys(final String userName,
                                                                   final String oldPassword,
                                                                   final String password,
                                                                   final boolean resetKeys,
                                                                   final List<MailboxEntity> mailboxEntities) {

        return Observable.fromCallable(new Callable<List<MailboxKey>>() {
            @Override
            public List<MailboxKey> call() {
                PGPManager pgpManager = new PGPManager();
                List<MailboxKey> mailboxKeys = new ArrayList<>();

                for (MailboxEntity mailboxEntity : mailboxEntities) {
                    PGPKeyEntity pgpKeyEntity;
                    if (resetKeys) {
                        pgpKeyEntity = pgpManager.generateKeys(userName, password);
                    } else {
                        pgpKeyEntity = pgpManager.changePrivateKeyPassword(mailboxEntity.getPrivateKey(), oldPassword, password);
                    }

                    MailboxKey mailboxKey = new MailboxKey();
                    mailboxKey.setMailboxId(mailboxEntity.getId());
                    mailboxKey.setPrivateKey(pgpKeyEntity.getPrivateKey());
                    mailboxKey.setPublicKey(pgpKeyEntity.getPublicKey());

                    mailboxKeys.add(mailboxKey);
                }

                return mailboxKeys;
            }
        }).subscribeOn(io.reactivex.schedulers.Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
