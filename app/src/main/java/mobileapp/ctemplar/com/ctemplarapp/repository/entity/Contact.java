package mobileapp.ctemplar.com.ctemplarapp.repository.entity;

import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.ContactData;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Contacts.EncryptContact;
import mobileapp.ctemplar.com.ctemplarapp.repository.MailboxDao;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserStore;
import mobileapp.ctemplar.com.ctemplarapp.security.PGPManager;
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

    private static MailboxDao mailboxDao = CTemplarApp.getAppDatabase().mailboxDao();
    private static UserStore userStore = CTemplarApp.getUserStore();

    public static String encryptData(String content) {
        if (content == null || mailboxDao.getAll().isEmpty()) {
            return "";
        }
        MailboxEntity mailboxEntity = mailboxDao.getAll().get(0);
        String publicKey = mailboxEntity.getPublicKey();
        String[] keys = { publicKey };
        return PGPManager.encrypt(content, keys);
    }

    public static String decryptData(String content) {
        MailboxEntity mailboxEntity = EncryptUtils.getDefaultMailbox();
        String password = userStore.getUserPassword();
        if (content == null || mailboxEntity == null) {
            return "";
        }
        String privateKey = mailboxEntity.getPrivateKey();
        return PGPManager.decrypt(content, privateKey, password);
    }

    public static ContactData[] decryptContactData(ContactData[] contacts) {
        for (ContactData contactData : contacts) {
            if (contactData.isEncrypted()) {
                Gson gson = new Gson();
                String encryptedData = contactData.getEncryptedData();
                String decryptedData = decryptData(encryptedData);
                if (decryptedData.isEmpty()) {
                    continue;
                }
                EncryptContact decryptedContact = gson.fromJson(decryptedData, EncryptContact.class);

                contactData.setEmail(decryptedContact.getEmail());
                contactData.setName(decryptedContact.getName());
                contactData.setAddress(decryptedContact.getAddress());
                contactData.setNote(decryptedContact.getNote());
                contactData.setPhone(decryptedContact.getPhone());
                contactData.setPhone2(decryptedContact.getPhone2());
                contactData.setProvider(decryptedContact.getProvider());
            }
        }
        return contacts;
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
        result.setEncrypted(contactData.isEncrypted());
        result.setEncryptedData(contactData.getEncryptedData());

        return result;
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
        result.setEncrypted(entity.isEncrypted());
        result.setEncryptedData(entity.getEncryptedData());

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
