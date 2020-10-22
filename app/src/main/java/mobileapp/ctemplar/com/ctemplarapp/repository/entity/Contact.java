package mobileapp.ctemplar.com.ctemplarapp.repository.entity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactData;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.EncryptContact;
import mobileapp.ctemplar.com.ctemplarapp.utils.EncryptUtils;

public class Contact {
    private long id;
    private String email;
    private String name;
    private String address;
    private String note;
    private String phone;
    private String phone2;
    private String provider;
    private Boolean isEncrypted;
    private String encryptedData;

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

    public Boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(Boolean isEncrypted) {
        this.isEncrypted = isEncrypted;
    }

    public String getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }

    public static Contact fromContactData(ContactData contactData) {
        if (contactData == null) {
            return new Contact();
        }
        Contact contact = new Contact();
        contact.setId(contactData.getId());
        contact.setEmail(contactData.getEmail());
        contact.setName(contactData.getName());
        contact.setAddress(contactData.getAddress());
        contact.setNote(contactData.getNote());
        contact.setPhone(contactData.getPhone());
        contact.setPhone2(contactData.getPhone2());
        contact.setProvider(contactData.getProvider());
        contact.setEncrypted(contactData.isEncrypted());
        contact.setEncryptedData(contactData.getEncryptedData());
        return contact;
    }

    public static Contact fromEntity(ContactEntity entity) {
        if (entity == null) {
            return new Contact();
        }
        Contact contact = new Contact();
        contact.setId(entity.getId());
        contact.setEncryptedData(entity.getEncryptedData());
        if (entity.isEncrypted) {
            String encryptedData = entity.getEncryptedData();
            String decryptedData = EncryptUtils.decryptData(encryptedData);
            EncryptContact decryptedContact = new Gson().fromJson(decryptedData, EncryptContact.class);
            if (decryptedContact == null) {
                return contact;
            }
            contact.setEmail(decryptedContact.getEmail());
            contact.setName(decryptedContact.getName());
            contact.setAddress(decryptedContact.getAddress());
            contact.setNote(decryptedContact.getNote());
            contact.setPhone(decryptedContact.getPhone());
            contact.setPhone2(decryptedContact.getPhone2());
            contact.setProvider(decryptedContact.getProvider());
            contact.setEncrypted(false);
        } else {
            contact.setEmail(entity.getEmail());
            contact.setName(entity.getName());
            contact.setAddress(entity.getAddress());
            contact.setNote(entity.getNote());
            contact.setPhone(entity.getPhone());
            contact.setPhone2(entity.getPhone2());
            contact.setProvider(entity.getProvider());
            contact.setEncrypted(true);
        }
        return contact;
    }

    public static ContactEntity fromContactDataToEntity(ContactData contactData) {
        if (contactData == null) {
            return new ContactEntity();
        }
        ContactEntity contactEntity = new ContactEntity();
        contactEntity.setId(contactData.getId());
        contactEntity.setEmail(contactData.getEmail());
        contactEntity.setName(contactData.getName());
        contactEntity.setAddress(contactData.getAddress());
        contactEntity.setNote(contactData.getNote());
        contactEntity.setPhone(contactData.getPhone());
        contactEntity.setProvider(contactData.getProvider());
        contactEntity.setPhone2(contactData.getPhone2());
        contactEntity.setEncrypted(contactData.isEncrypted());
        contactEntity.setEncryptedData(contactData.getEncryptedData());
        return contactEntity;
    }

    public static List<Contact> fromContactData(ContactData[] contactData) {
        if (contactData == null || contactData.length == 0) {
            return new ArrayList<>();
        }
        List<Contact> contactDataList = new LinkedList<>();
        for (ContactData contactDatum : contactData) {
            contactDataList.add(fromContactData(contactDatum));
        }
        return contactDataList;
    }

    public static List<Contact> fromEntities(List<ContactEntity> contactEntityList) {
        if (contactEntityList == null || contactEntityList.isEmpty()) {
            return new ArrayList<>();
        }
        List<Contact> contactList = new LinkedList<>();
        for (ContactEntity contactEntity : contactEntityList) {
            contactList.add(fromEntity(contactEntity));
        }
        return contactList;
    }

    public static ContactEntity[] fromContactDataToEntities(ContactData[] contactData) {
        if (contactData == null || contactData.length == 0) {
            return new ContactEntity[0];
        }
        ContactEntity[] contactEntities = new ContactEntity[contactData.length];
        for (int i = 0; i < contactData.length; ++i) {
            contactEntities[i] = fromContactDataToEntity(contactData[i]);
        }
        return contactEntities;
    }
}
