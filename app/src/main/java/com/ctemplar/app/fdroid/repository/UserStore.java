package com.ctemplar.app.fdroid.repository;

import com.ctemplar.app.fdroid.net.entity.UserEntity;

public interface UserStore {

    void saveUserPref(String username, String pass, String passHashed, String privateKey, String publicKey);
    UserEntity getUser();

    void clearToken();
    void logout();

    void saveToken(String token);
    String getToken();

    void saveAppToken(String token);
    String getAppToken();

    void saveUsername(String username);
    String getUsername();

    void savePassword(String password);
    String getUserPassword();

    void saveKeepMeLoggedIn(boolean state);
    boolean getKeepMeLoggedIn();

    void saveMobileSignature(String signature);
    String getMobileSignature();

    void saveTimeZone(String timezone);
    String getTimeZone();

    void setSignatureEnabled(boolean state);
    boolean getSignatureEnabled();

    void setMobileSignatureEnabled(boolean state);
    boolean getMobileSignatureEnabled();

    void setNotificationsEnabled(boolean state);
    boolean getNotificationsEnabled();

    void setAttachmentsEncryptionEnabled(boolean state);
    boolean getAttachmentsEncryptionEnabled();

    void setContactsEncryptionEnabled(boolean state);
    boolean getContactsEncryptionEnabled();
}
