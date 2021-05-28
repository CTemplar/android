package mobileapp.ctemplar.com.ctemplarapp.net.response.contacts;

import com.google.gson.annotations.SerializedName;

import mobileapp.ctemplar.com.ctemplarapp.utils.EncryptUtils;

import static mobileapp.ctemplar.com.ctemplarapp.utils.DateUtils.GENERAL_GSON;

public class ContactData {
    @SerializedName("id")
    private long id;

    @SerializedName("address")
    private String address;

    @SerializedName("email")
    private String email;

    @SerializedName("name")
    private String name;

    @SerializedName("note")
    private String note;

    @SerializedName("phone")
    private String phone;

    @SerializedName("phone2")
    private String phone2;

    @SerializedName("provider")
    private String provider;

    @SerializedName("is_encrypted")
    private boolean isEncrypted;

    @SerializedName("encrypted_data")
    private String encryptedData;

    public ContactData() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    public String getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }

    public void encryptContactData() {
        EncryptContact encryptContact = new EncryptContact();
        encryptContact.setEmail(getEmail());
        encryptContact.setName(getName());
        encryptContact.setAddress(getAddress());
        encryptContact.setNote(getNote());
        encryptContact.setPhone(getPhone());
        encryptContact.setPhone2(getPhone2());
        encryptContact.setProvider(getProvider());

        setEmail(null);
        setName(null);
        setAddress(null);
        setNote(null);
        setPhone(null);
        setPhone2(null);
        setProvider(null);
        setEncrypted(true);

        String contactString = GENERAL_GSON.toJson(encryptContact);
        String encryptedContactString = EncryptUtils.encryptData(contactString);
        setEncryptedData(encryptedContactString);
    }
}
