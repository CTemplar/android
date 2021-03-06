package mobileapp.ctemplar.com.ctemplarapp.repository;

import mobileapp.ctemplar.com.ctemplarapp.net.entity.UserEntity;

public interface UserStore {
    void saveUserPref(String username, String password, String passwordHashed);
    UserEntity getUser();

    void clearToken();
    void logout();

    void saveUserToken(String token);
    String getUserToken();

    void updateLastForceRefreshTokenAttemptTime();
    long getLastForceRefreshTokenAttemptTime();

    void saveFirebaseToken(String token);
    String getFirebaseToken();

    void saveUsername(String username);
    String getUsername();

    void savePassword(String password);
    String getUserPassword();

    void saveKeepMeLoggedIn(boolean state);
    boolean getKeepMeLoggedIn();

    void saveTimeZone(String timezone);
    String getTimeZone();

    void setSignatureEnabled(boolean state);
    boolean isSignatureEnabled();

    void setNotificationsEnabled(boolean state);
    boolean isNotificationsEnabled();

    void setContactsEncryptionEnabled(boolean state);
    boolean isContactsEncryptionEnabled();

    void setKeepDecryptedSubjectsEnabled(boolean state);
    boolean isKeepDecryptedSubjectsEnabled();

    void setDraftsAutoSaveEnabled(boolean state);
    boolean isDraftsAutoSaveEnabled();

    void setBlockExternalImagesEnabled(boolean state);
    boolean isBlockExternalImagesEnabled();

    void setReportBugsEnabled(boolean state);
    boolean isReportBugsEnabled();

    void setPINLock(String pin);
    boolean checkPINLock(String pinCode);

    void disablePINLock();
    boolean isPINLockEnabled();

    void setAutoLockTime(int timeInMillis);
    int getAutoLockTime();

    void setLastPauseTime(long lastPauseTimeInMillis);
    long getLastPauseTime();

    void setLocked(boolean locked);
    boolean isLocked();

    void updateLockLastAttemptTime();
    long getLockLastAttemptTime();

    void setLockAttemptsCount(int attemptsCount);
    int getLockAttemptsCount();

    void setDarkModeValue(int value);
    int getDarkModeValue();

    void setDarkModeKey(String key);
    String getDarkModeKey();

    void setLanguageKey(String key);
    String getLanguageKey();
}
