package com.ctemplar.app.fdroid.net.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChangePasswordRequest {
    @SerializedName("old_password")
    private String oldPassword;

    @SerializedName("password")
    private String password;

    @SerializedName("confirm_password")
    private String confirmPassword;

    @SerializedName("delete_data")
    private boolean deleteData;

    @SerializedName("new_keys")
    private List<MailboxKey> mailboxesKeys;

    public ChangePasswordRequest(String oldPassword, String password, String confirmPassword, Boolean deleteData) {
        this.oldPassword = oldPassword;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.deleteData = deleteData;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public void setDeleteData(boolean deleteData) {
        this.deleteData = deleteData;
    }

    public void setMailboxesKeys(List<MailboxKey> mailboxesKeys) {
        this.mailboxesKeys = mailboxesKeys;
    }
}
