package mobileapp.ctemplar.com.ctemplarapp.utils;

import android.text.TextUtils;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import mobileapp.ctemplar.com.ctemplarapp.net.entity.PGPKeyEntity;

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
                return pgpManager.generateKeys("name <name@domain.com>", password); //TODO
            }
        }).subscribeOn(io.reactivex.schedulers.Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static String decodeMessage(String encodedMessage, String publicKey, String privateKey) {
        if(!TextUtils.isEmpty(encodedMessage)) {

        }

        return "";
    }
}
