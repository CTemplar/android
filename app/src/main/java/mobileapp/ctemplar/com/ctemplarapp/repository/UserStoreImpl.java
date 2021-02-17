package mobileapp.ctemplar.com.ctemplarapp.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import mobileapp.ctemplar.com.ctemplarapp.net.entity.UserEntity;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncodeUtils;

public class UserStoreImpl implements UserStore {
    private static UserStoreImpl instance;

    private final SharedPreferences preferences;
    private final SharedPreferences globalPreferences;

    private static final String PREF_USER = "pref_user";
    private static final String KEY_USER_TOKEN = "key_user_token";
    private static final String KEY_USER_LAST_FORCE_REFRESH_TOKEN_TIME = "key_user_last_force_refresh_token_attempt_time";
    private static final String KEY_FIREBASE_TOKEN = "key_firebase_token";
    private static final String KEY_USERNAME = "key_username";
    private static final String KEY_PASSWORD = "key_password";
    private static final String KEY_PASSWORD_HASHED = "key_password_hashed";
    private static final String KEY_KEEP_ME_LOGGED_IN = "key_keep_me_logged_in";
    private static final String KEY_PUBLIC_KEY = "key_public_key";
    private static final String KEY_PRIVATE_KEY = "key_private_key";
    private static final String KEY_TIMEZONE = "key_timezone";
    private static final String KEY_SIGNATURE_ENABLED = "key_signature_enabled";
    private static final String KEY_NOTIFICATIONS_ENABLED = "key_notifications_enabled";
    private static final String KEY_CONTACTS_ENCRYPTION_ENABLED = "key_contacts_encryption_enabled";
    private static final String KEY_KEEP_DECRYPTED_SUBJECTS_ENABLED = "key_keep_decrypted_subjects_enabled";
    private static final String KEY_DRAFTS_AUTO_SAVE_ENABLED = "key_drafts_auto_save_enabled";
    private static final String KEY_BLOCK_EXTERNAL_IMAGES_ENABLED = "key_block_external_images_enabled";
    private static final String KEY_REPORT_BUGS_ENABLED = "key_report_bugs_enabled";
    private static final String KEY_PIN_LOCK = "key_pin_lock";
    private static final String KEY_AUTO_LOCK_TIME = "key_auto_lock_time";
    private static final String KEY_LAST_PAUSE_TIME = "key_last_pause_time";
    private static final String KEY_IS_LOCKED = "key_is_locked";
    private static final String KEY_DARK_MODE = "key_dark_mode";
    private static final String KEY_LANGUAGE = "key_language";

    public static UserStoreImpl getInstance(Context context) {
        if (instance == null) {
            instance = new UserStoreImpl(context);
        }
        return instance;
    }

    public UserStoreImpl(Context context) {
        preferences = context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE);
        globalPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void saveUserToken(String token) {
        preferences.edit().putString(KEY_USER_TOKEN, token).apply();
    }

    @Override
    public String getUserToken() {
        return preferences.getString(KEY_USER_TOKEN, "");
    }

    @Override
    public void updateLastForceRefreshTokenAttemptTime() {
        preferences.edit().putLong(KEY_USER_LAST_FORCE_REFRESH_TOKEN_TIME, System.currentTimeMillis()).apply();
    }

    @Override
    public long getLastForceRefreshTokenAttemptTime() {
        return preferences.getLong(KEY_USER_LAST_FORCE_REFRESH_TOKEN_TIME, 0);
    }

    @Override
    public void saveFirebaseToken(String token) {
        preferences.edit().putString(KEY_FIREBASE_TOKEN, token).apply();
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
                .apply();
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
        preferences.edit().remove(KEY_USER_TOKEN).apply();
    }

    @Override
    public void logout() {
        preferences.edit().clear().apply();
        globalPreferences.edit().clear().apply();
    }

    @Override
    public void saveUsername(String username) {
        preferences.edit().putString(KEY_USERNAME, username).apply();
    }

    @Override
    public String getUsername() {
        return preferences.getString(KEY_USERNAME, "");
    }

    @Override
    public void savePassword(String password) {
        preferences.edit().putString(KEY_PASSWORD, password).apply();
    }

    @Override
    public String getUserPassword() {
        return preferences.getString(KEY_PASSWORD, "");
    }

    @Override
    public void saveKeepMeLoggedIn(boolean state) {
        preferences.edit().putBoolean(KEY_KEEP_ME_LOGGED_IN, state).apply();
    }

    @Override
    public boolean getKeepMeLoggedIn() {
        return preferences.getBoolean(KEY_KEEP_ME_LOGGED_IN, false);
    }

    @Override
    public void saveTimeZone(String timezone) {
        preferences.edit().putString(KEY_TIMEZONE, timezone).apply();
    }

    @Override
    public String getTimeZone() {
        return preferences.getString(KEY_TIMEZONE, "");
    }

    @Override
    public void setSignatureEnabled(boolean state) {
        preferences.edit().putBoolean(KEY_SIGNATURE_ENABLED, state).apply();
    }

    @Override
    public boolean isSignatureEnabled() {
        return preferences.getBoolean(KEY_SIGNATURE_ENABLED, true);
    }

    @Override
    public void setNotificationsEnabled(boolean state) {
        preferences.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, state).apply();
    }

    @Override
    public boolean isNotificationsEnabled() {
        return preferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
    }

    @Override
    public void setContactsEncryptionEnabled(boolean state) {
        preferences.edit().putBoolean(KEY_CONTACTS_ENCRYPTION_ENABLED, state).apply();
    }

    @Override
    public boolean isContactsEncryptionEnabled() {
        return preferences.getBoolean(KEY_CONTACTS_ENCRYPTION_ENABLED, false);
    }

    @Override
    public void setKeepDecryptedSubjectsEnabled(boolean state) {
        preferences.edit().putBoolean(KEY_KEEP_DECRYPTED_SUBJECTS_ENABLED, state).apply();
    }

    @Override
    public boolean isKeepDecryptedSubjectsEnabled() {
        return preferences.getBoolean(KEY_KEEP_DECRYPTED_SUBJECTS_ENABLED, true);
    }

    @Override
    public void setDraftsAutoSaveEnabled(boolean state) {
        preferences.edit().putBoolean(KEY_DRAFTS_AUTO_SAVE_ENABLED, state).apply();
    }

    @Override
    public boolean isDraftsAutoSaveEnabled() {
        return preferences.getBoolean(KEY_DRAFTS_AUTO_SAVE_ENABLED, false);
    }

    @Override
    public void setBlockExternalImagesEnabled(boolean state) {
        globalPreferences.edit().putBoolean(KEY_BLOCK_EXTERNAL_IMAGES_ENABLED, state).apply();
    }

    @Override
    public boolean isBlockExternalImagesEnabled() {
        return globalPreferences.getBoolean(KEY_BLOCK_EXTERNAL_IMAGES_ENABLED, false);
    }

    @Override
    public void setReportBugsEnabled(boolean state) {
        preferences.edit().putBoolean(KEY_REPORT_BUGS_ENABLED, state).apply();
    }

    @Override
    public boolean isReportBugsEnabled() {
        return preferences.getBoolean(KEY_REPORT_BUGS_ENABLED, false);
    }

    @Override
    public void setPINLock(String pinCode) {
        String generatedHash = EncodeUtils.generateHash(getUserPassword(), pinCode);
        preferences.edit().putString(KEY_PIN_LOCK, generatedHash).apply();
    }

    @Override
    public boolean checkPINLock(@Nullable String pinCode) {
        if (pinCode == null) {
            return false;
        }
        String pinCodeHash = preferences.getString(KEY_PIN_LOCK, null);
        if (pinCodeHash == null) {
            return true;
        }
        String generatedHash = EncodeUtils.generateHash(getUserPassword(), pinCode);
        return TextUtils.equals(pinCodeHash, generatedHash);
    }

    @Override
    public void disablePINLock() {
        preferences.edit().putString(KEY_PIN_LOCK, null).apply();
    }

    @Override
    public boolean isPINLockEnabled() {
        return EditTextUtils.isNotEmpty(preferences.getString(KEY_PIN_LOCK, null));
    }

    @Override
    public void setAutoLockTime(int timeInMillis) {
        preferences.edit().putInt(KEY_AUTO_LOCK_TIME, timeInMillis).apply();
    }

    @Override
    public int getAutoLockTime() {
        return preferences.getInt(KEY_AUTO_LOCK_TIME, 60000);
    }

    @Override
    public void setLastPauseTime(long lastPauseTimeInMillis) {
        preferences.edit().putLong(KEY_LAST_PAUSE_TIME, lastPauseTimeInMillis).apply();
    }

    @Override
    public long getLastPauseTime() {
        return preferences.getLong(KEY_LAST_PAUSE_TIME, Long.MAX_VALUE);
    }

    @Override
    public void setLocked(boolean locked) {
        preferences.edit().putBoolean(KEY_IS_LOCKED, locked).apply();
    }

    @Override
    public boolean isLocked() {
        return preferences.getBoolean(KEY_IS_LOCKED, false);
    }

    @Override
    public void setDarkModeValue(int value) {
        preferences.edit().putInt(KEY_DARK_MODE, value).apply();
        AppCompatDelegate.setDefaultNightMode(value);
    }

    @Override
    public int getDarkModeValue() {
        return preferences.getInt(KEY_DARK_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    @Override
    public void setDarkModeKey(String key) {
        int value;
        switch (key) {
            case "on":
                value = AppCompatDelegate.MODE_NIGHT_YES;
                break;
            case "off":
                value = AppCompatDelegate.MODE_NIGHT_NO;
                break;
            default:
                value = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
        preferences.edit().putInt(KEY_DARK_MODE, value).apply();
        AppCompatDelegate.setDefaultNightMode(value);
    }

    @Override
    public String getDarkModeKey() {
        int value = preferences.getInt(KEY_DARK_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        switch (value) {
            case AppCompatDelegate.MODE_NIGHT_YES:
                return "on";
            case AppCompatDelegate.MODE_NIGHT_NO:
                return "off";
            default:
                return "auto";
        }
    }

    @Override
    public void setLanguageKey(String key) {
        preferences.edit().putString(KEY_LANGUAGE, key).apply();
    }

    @Override
    public String getLanguageKey() {
        return preferences.getString(KEY_LANGUAGE, "auto");
    }
}
