package com.ctemplar.app.fdroid.repository;

import com.ctemplar.app.fdroid.net.entity.UserEntity;

import java.net.Proxy;

public interface UserStore {
    void saveUserPref(String username, String password, String passwordHashed);
    UserEntity getUser();

    void clearToken();
    void logout();

    void saveUserToken(String token);
    String getUserToken();

    void updateLastForceRefreshTokenAttemptTime();
    long getLastForceRefreshTokenAttemptTime();

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

    void setAutoReadEmailEnabled(boolean state);
    boolean isAutoReadEmailEnabled();

    void setIncludeOriginalMessage(boolean state);
    boolean isIncludeOriginalMessage();

    void setBlockExternalImagesEnabled(boolean state);
    boolean isBlockExternalImagesEnabled();

    void setWarnExternalLinkEnabled(boolean state);
    boolean isWarnExternalLinkEnabled();

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

    void setProxyTorEnabled(boolean value);
    boolean isProxyTorEnabled();

    void setProxyCustomEnabled(boolean value);
    boolean isProxyCustomEnabled();

    void setProxyTypeIndex(int proxyType);
    int getProxyTypeIndex();
    Proxy.Type getProxyType();

    void setProxyIP(String value);
    String getProxyIP();

    void setProxyPort(int value);
    int getProxyPort();

    void setDarkModeValue(int value);
    int getDarkModeValue();

    void setDarkModeKey(String key);
    String getDarkModeKey();

    void setHideAppPreview(boolean value);
    boolean isHideAppPreview();

    void setLanguageKey(String key);
    String getLanguageKey();
}
