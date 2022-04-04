package mobileapp.ctemplar.com.ctemplarapp.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.repository.MailboxDao;
import mobileapp.ctemplar.com.ctemplarapp.repository.MailboxKeyDao;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserStore;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxKeyEntity;
import mobileapp.ctemplar.com.ctemplarapp.security.Cryptor;
import mobileapp.ctemplar.com.ctemplarapp.security.PGPManager;
import timber.log.Timber;

public class EncryptUtils {
    private static final MailboxDao mailboxDao = CTemplarApp.getAppDatabase().mailboxDao();
    private static final MailboxKeyDao mailboxKeyDao = CTemplarApp.getAppDatabase().mailboxKeyDao();
    private static final UserStore userStore = CTemplarApp.getUserStore();

    private static List<String> getPrivateKeys(long mailboxId) {
        MailboxEntity mailboxEntity = mailboxDao.getById(mailboxId);
        if (mailboxEntity == null) {
            return null;
        }
        List<MailboxKeyEntity> mailboxKeyEntity = mailboxKeyDao.getByMailboxId(mailboxId);
        String privateKey = mailboxEntity.getPrivateKey();
        List<String> privateKeys = new ArrayList<>();
        privateKeys.add(privateKey);
        if (mailboxKeyEntity != null) {
            for (MailboxKeyEntity keyEntity : mailboxKeyEntity) {
                privateKeys.add(keyEntity.getPrivateKey());
            }
        }
        return privateKeys;
    }

    public static String decryptContent(String content, long mailboxId, boolean decrypt) {
        if (content == null || content.length() == 0) {
            return "";
        }
        if (!decrypt) {
            return content;
        }
        List<String> privateKeys = getPrivateKeys(mailboxId);
        if (privateKeys == null) {
            return "";
        }
        MailboxEntity mailboxEntity = mailboxDao.getById(mailboxId);
        String password = userStore.getUserPassword();
        if (mailboxEntity == null || password == null || password.length() == 0) {
            return "";
        }
        return Cryptor.decryptPGP(content, privateKeys, password);
    }

    public static String decryptSubject(String subject, long mailboxId) {
        return decryptContent(subject, mailboxId, true)
                .replaceAll("<img.+?>", "");
    }

    public static String encryptData(String content) {
        if (content == null || content.length() == 0) {
            return "";
        }
        MailboxEntity mailboxEntity = EncryptUtils.getDefaultMailbox();
        if (mailboxEntity == null) {
            return "";
        }
        String publicKey = mailboxEntity.getPublicKey();
        return PGPManager.encrypt(content, new String[]{publicKey});
    }

    public static String decryptData(String content) {
        if (content == null || content.length() == 0) {
            return "";
        }
        MailboxEntity mailboxEntity = EncryptUtils.getDefaultMailbox();
        if (mailboxEntity == null) {
            return "";
        }
        return decryptContent(content, mailboxEntity.getId(), true);
    }

    public static boolean encryptAttachment(File originalFile, File encryptedFile,
                                            List<String> publicKeyList) {
        byte[] fileBytes;
        try {
            fileBytes = new byte[(int) originalFile.length()];
            BufferedInputStream bufferedInputStream = new BufferedInputStream(
                    new FileInputStream(originalFile));
            bufferedInputStream.read(fileBytes, 0, fileBytes.length);
            bufferedInputStream.close();
        } catch (IOException e) {
            Timber.e(e);
            return false;
        }
        byte[] encryptedBytes = PGPManager.encrypt(fileBytes, publicKeyList.toArray(new String[0]),
                true);
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                    new FileOutputStream(encryptedFile));
            bufferedOutputStream.write(encryptedBytes);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        } catch (IOException e) {
            Timber.e(e);
            return false;
        }
        return true;
    }

    public static boolean decryptAttachment(File encryptedFile, File decryptedFile, String password,
                                            long mailboxId) throws InterruptedException {
        List<String> privateKeys = getPrivateKeys(mailboxId);
        if (privateKeys == null) {
            return false;
        }
        byte[] fileBytes = new byte[(int) encryptedFile.length()];
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(
                    new FileInputStream(encryptedFile));
            bufferedInputStream.read(fileBytes, 0, fileBytes.length);
            bufferedInputStream.close();
        } catch (IOException e) {
            Timber.e(e);
            return false;
        }
        try {
            byte[] encryptedBytes = Cryptor.decryptPGP(fileBytes, privateKeys, password);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                    new FileOutputStream(decryptedFile));
            bufferedOutputStream.write(encryptedBytes);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            Timber.e(e);
            return false;
        }
        return true;
    }

    public static boolean encryptAttachmentGPG(File originalFile, File encryptedFile,
                                               String passPhrase) {
        int fileSize = (int) originalFile.length();
        byte[] fileBytes = new byte[fileSize];
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(
                    new FileInputStream(originalFile));
            bufferedInputStream.read(fileBytes, 0, fileBytes.length);
            bufferedInputStream.close();
        } catch (IOException e) {
            Timber.e(e);
            return false;
        }
        byte[] encryptedBytes = PGPManager.encryptGPG(fileBytes, passPhrase, true);
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                    new FileOutputStream(encryptedFile));
            bufferedOutputStream.write(encryptedBytes);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        } catch (IOException e) {
            Timber.e(e);
            return false;
        }
        return true;
    }

    public static boolean decryptAttachmentGPG(File encryptedFile, File decryptedFile,
                                               String password) throws InterruptedException {
        int fileSize = (int) encryptedFile.length();
        byte[] fileBytes = new byte[fileSize];
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(
                    new FileInputStream(encryptedFile));
            bufferedInputStream.read(fileBytes, 0, fileBytes.length);
            bufferedInputStream.close();
        } catch (IOException e) {
            Timber.e(e);
            return false;
        }
        try {
            byte[] encryptedBytes = PGPManager.decryptGPG(fileBytes, password);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                    new FileOutputStream(decryptedFile));
            bufferedOutputStream.write(encryptedBytes);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            Timber.e(e);
            return false;
        }
        return true;
    }

    @Nullable
    public static MailboxEntity getDefaultMailbox() {
        if (mailboxDao.getDefault() == null) {
            if (mailboxDao.getAll().size() > 0) {
                return mailboxDao.getAll().get(0);
            } else {
                Timber.e("Mailbox not found");
                return null;
            }
        } else {
            return mailboxDao.getDefault();
        }
    }
}
