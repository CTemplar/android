package mobileapp.ctemplar.com.ctemplarapp.repository;

import mobileapp.ctemplar.com.ctemplarapp.net.entity.UserEntity;

public interface UserStore {

    void saveUserPref(String username, String pass, String passHashed, String privateKey, String publicKey);
    UserEntity getUser();

    void clearToken();
    void logout();

    void saveToken(String token);
    String getToken();

    void saveUsername(String username);
    String getUsername();

    void savePassword(String password);
    String getUserPassword();

    void saveTimeZone(String timezone);
    String getTimeZone();
}
