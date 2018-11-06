package mobileapp.ctemplar.com.ctemplarapp.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import mobileapp.ctemplar.com.ctemplarapp.net.entity.UserEntity;

public class UserStoreImpl implements UserStore{

    private static UserStoreImpl instance;

    private static final String PREF_USER = "pref_user";
    private static final String KEY_USER_TOKEN = "key_user_token";
    private static final String KEY_USERNAME = "key_username";
    private static final String KEY_PASSWORD = "key_password";
    private static final String KEY_PASSWORD_HASHED = "key_password_hashed";
    private static final String KEY_PUBLIC_KEY = "key_public_key";
    private static final String KEY_PRIVATE_KEY = "key_private_key";

    private Context context;
    private SharedPreferences preferences;

    public static UserStoreImpl getInstance(Context context) {
        if(instance == null) {
            instance = new UserStoreImpl(context);
        }

        return instance;
    }

    public UserStoreImpl(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE);
    }

    @Override
    public String getToken() {
        return preferences.getString(KEY_USER_TOKEN, "");
    }

    @Override
    public void saveToken(String token) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USER_TOKEN, token);
        editor.commit();
    }

    @Override
    public void saveUserPref(String username, String pass, String passHashed, String privateKey, String publicKey) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, pass);
        editor.putString(KEY_PASSWORD_HASHED, passHashed);
        editor.putString(KEY_PRIVATE_KEY, privateKey);
        editor.putString(KEY_PUBLIC_KEY, publicKey);
        editor.commit();
    }

    @Override
    public UserEntity getUser() {
        UserEntity entity = new UserEntity();
        entity.setUsername(preferences.getString(KEY_USERNAME, ""));
        entity.setPassword(preferences.getString(KEY_PASSWORD, ""));
        entity.setPasswordHashed(preferences.getString(KEY_PASSWORD_HASHED, ""));
        entity.setPrivateKey(preferences.getString(KEY_PRIVATE_KEY, ""));
        entity.setPublicKey(preferences.getString(KEY_PUBLIC_KEY, ""));
        entity.setToken(preferences.getString(KEY_USER_TOKEN, ""));
        return null;
    }

    @Override
    public void clearToken() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_USER_TOKEN);
        editor.commit();
    }

    @Override
    public void logout() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}
