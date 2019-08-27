package mobileapp.ctemplar.com.ctemplarapp.repository;

import android.content.Context;
import android.content.SharedPreferences;

import mobileapp.ctemplar.com.ctemplarapp.net.entity.UserEntity;

public class UserStoreImpl implements UserStore{

    private static UserStoreImpl instance;

    private static final String PREF_USER = "pref_user";
    private static final String KEY_USER_TOKEN = "key_user_token";
    private static final String KEY_FIREBASE_TOKEN = "key_firebase_token";
    private static final String KEY_USERNAME = "key_username";
    private static final String KEY_PASSWORD = "key_password";
    private static final String KEY_PASSWORD_HASHED = "key_password_hashed";
    private static final String KEY_PUBLIC_KEY = "key_public_key";
    private static final String KEY_PRIVATE_KEY = "key_private_key";
    private static final String KEY_TIMEZONE = "key_timezone";
    private static final String KEY_NOTIFICATIONS_ENABLED = "key_notifications_enabled";

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
    public void saveToken(String token) {
        preferences.edit().putString(KEY_USER_TOKEN, token).commit();
    }

    @Override
    public String getToken() {
        return preferences.getString(KEY_USER_TOKEN, "");
    }

    @Override
    public void saveFirebaseToken(String token) {
        preferences.edit().putString(KEY_FIREBASE_TOKEN, token).commit();
    }

    @Override
    public String getFirebaseToken() {
        return preferences.getString(KEY_FIREBASE_TOKEN, "");
    }

    @Override
    public void saveUserPref(String username, String pass, String passHashed, String privateKey, String publicKey) {
        preferences.edit()
        .putString(KEY_USERNAME, username)
        .putString(KEY_PASSWORD, pass)
        .putString(KEY_PASSWORD_HASHED, passHashed)
        .putString(KEY_PRIVATE_KEY, privateKey)
        .putString(KEY_PUBLIC_KEY, publicKey)
        .commit();
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
        return entity;
    }

    @Override
    public void clearToken() {
        preferences.edit().remove(KEY_USER_TOKEN).commit();
    }

    @Override
    public void logout() {
        preferences.edit().clear().commit();
    }

    @Override
    public void saveUsername(String username) {
        preferences.edit().putString(KEY_USERNAME, username).commit();
    }

    @Override
    public String getUsername() {
        return preferences.getString(KEY_USERNAME, "");
    }

    @Override
    public void savePassword(String password) {
        preferences.edit().putString(KEY_PASSWORD, password).commit();
    }

    @Override
    public String getUserPassword() {
        return preferences.getString(KEY_PASSWORD, "");
    }

    @Override
    public void saveTimeZone(String timezone) {
        preferences.edit().putString(KEY_TIMEZONE, timezone).commit();
    }

    @Override
    public String getTimeZone() {
        return preferences.getString(KEY_TIMEZONE, "");
    }

    @Override
    public void setNotificationsEnabled(boolean state) {
        preferences.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, state).commit();
    }

    @Override
    public boolean getNotificationsEnabled() {
        return preferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, false);
    }
}
