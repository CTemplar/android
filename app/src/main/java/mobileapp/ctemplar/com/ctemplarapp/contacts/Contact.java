package mobileapp.ctemplar.com.ctemplarapp.contacts;

import java.util.LinkedList;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactData;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.ContactEntity;

public class Contact {
    private long id;
    private String email;
    private String name;
    private String address;
    private String note;
    private String phone;
    private String phone2;
    private String provider;

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

    public static Contact fromEntity(ContactEntity entity) {
        Contact result = new Contact();

        result.setId(entity.getId());
        result.setEmail(entity.getEmail());
        result.setName(entity.getName());
        result.setAddress(entity.getAddress());
        result.setNote(entity.getNote());
        result.setPhone(entity.getPhone());
        result.setPhone2(entity.getPhone2());
        result.setProvider(entity.getProvider());

        return result;
    }

    public static Contact fromResponseResult(ContactData contactData) {
        Contact result = new Contact();

        result.setId(contactData.getId());
        result.setEmail(contactData.getEmail());
        result.setName(contactData.getName());
        result.setAddress(contactData.getAddress());
        result.setNote(contactData.getNote());
        result.setPhone(contactData.getPhone());
        result.setPhone2(contactData.getPhone2());
        result.setProvider(contactData.getProvider());

        return result;
    }

    public static List<Contact> fromEntities(List<ContactEntity> entities) {
        List<Contact> result = new LinkedList<>();

        for (ContactEntity entity : entities) {
            result.add(fromEntity(entity));
        }

        return result;
    }

    public static List<Contact> fromResponseResults(ContactData[] contactDataList) {
        List<Contact> result = new LinkedList<>();

        for (ContactData contactData : contactDataList) {
            result.add(fromResponseResult(contactData));
        }

        return result;
    }

}
