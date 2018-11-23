package mobileapp.ctemplar.com.ctemplarapp.repository;

import mobileapp.ctemplar.com.ctemplarapp.net.entity.UserEntity;

public interface UserStore {

    public String getToken();
    public void saveToken(String token);

    void savePassword(String password);

    public void saveUserPref(String username, String pass, String passHashed, String privateKey, String publicKey);
    public UserEntity getUser();
    public void clearToken();
    public void logout();

    void saveUsername(String username);

    String getUsername();

    String getUserPassword();
}
