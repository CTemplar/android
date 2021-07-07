package com.ctemplar.app.fdroid.repository.entity;

public class UserDisplayEntity {
    private String email;
    private String name;
    private boolean isEncrypted;

    public UserDisplayEntity() {
    }

    public UserDisplayEntity(String email, String name, boolean isEncrypted) {
        this.email = email;
        this.name = name;
        this.isEncrypted = isEncrypted;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }
}
