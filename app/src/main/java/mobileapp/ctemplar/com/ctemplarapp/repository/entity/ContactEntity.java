package mobileapp.ctemplar.com.ctemplarapp.repository.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "contacts")
public class ContactEntity {
    @PrimaryKey
    private final long id;
    private final String email;
    private final String name;
    private final String address;
    private final String note;
    private final String phone;
    private final String phone2;
    private final String provider;
    private final boolean isEncrypted;
    private final String encryptedData;

    public ContactEntity(
            long id, String email, String name, String address, String note,
            String phone, String phone2, String provider, boolean isEncrypted,
            String encryptedData
    ) {
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

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getNote() {
        return note;
    }

    public String getPhone() {
        return phone;
    }

    public String getPhone2() {
        return phone2;
    }

    public String getProvider() {
        return provider;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public String getEncryptedData() {
        return encryptedData;
    }
}
