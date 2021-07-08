package mobileapp.ctemplar.com.ctemplarapp.net.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.net.request.mailboxes.MailboxExtraKeyRequest;
import mobileapp.ctemplar.com.ctemplarapp.net.request.mailboxes.MailboxKeyRequest;

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
    private List<MailboxKeyRequest> newKeys;

    @SerializedName("extra_keys")
    private List<MailboxExtraKeyRequest> extraKeys;

    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(String oldPassword, String password, String confirmPassword, boolean deleteData, List<MailboxKeyRequest> newKeys, List<MailboxExtraKeyRequest> extraKeys) {
        this.oldPassword = oldPassword;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.deleteData = deleteData;
        this.newKeys = newKeys;
        this.extraKeys = extraKeys;
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

    public void setNewKeys(List<MailboxKeyRequest> newKeys) {
        this.newKeys = newKeys;
    }

    public void setExtraKeys(List<MailboxExtraKeyRequest> extraKeys) {
        this.extraKeys = extraKeys;
    }
}
