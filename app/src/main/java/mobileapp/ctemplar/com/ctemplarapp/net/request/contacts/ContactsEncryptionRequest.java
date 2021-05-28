package mobileapp.ctemplar.com.ctemplarapp.net.request.contacts;

import com.google.gson.annotations.SerializedName;

public class ContactsEncryptionRequest {
    @SerializedName("is_contacts_encrypted")
    private boolean isContactsEncrypted;

    public ContactsEncryptionRequest(boolean isContactsEncrypted) {
        this.isContactsEncrypted = isContactsEncrypted;
    }

    public void setContactsEncrypted(boolean contactsEncrypted) {
        isContactsEncrypted = contactsEncrypted;
    }
}
