package mobileapp.ctemplar.com.ctemplarapp.repository.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "contacts")
public class ContactEntity {
    @PrimaryKey
    public long id;
    public String email;
    public String name;
    public String address;
    public String note;
    public String phone;
    public String phone2;
    public String provider;
    public boolean isEncrypted;
    public String encryptedData;

    public ContactEntity() {

    }

    public ContactEntity(long id, String email, String name, String address, String note, String phone, String phone2, String provider, boolean isEncrypted, String encryptedData) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.address = address;
        this.note = note;
        this.phone = phone;
        this.phone2 = phone2;
        this.provider = provider;
        this.isEncrypted = isEncrypted;
        this.encryptedData = encryptedData;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
}
