package mobileapp.ctemplar.com.ctemplarapp.net.response.contacts;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EncryptContact {
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
    private Boolean isEncrypted;

    @SerializedName("extra_emails")
    private List<String> extraEmails;

    @SerializedName("extra_phones")
    private List<String> extraPhones;

    public EncryptContact() {

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

    public Boolean getEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(Boolean encrypted) {
        isEncrypted = encrypted;
    }

    public List<String> getExtraEmails() {
        return extraEmails;
    }

    public void setExtraEmails(List<String> extraEmails) {
        this.extraEmails = extraEmails;
    }

    public List<String> getExtraPhones() {
        return extraPhones;
    }

    public void setExtraPhones(List<String> extraPhones) {
        this.extraPhones = extraPhones;
    }
}
