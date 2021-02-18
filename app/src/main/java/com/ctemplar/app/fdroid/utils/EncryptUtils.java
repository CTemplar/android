package com.ctemplar.app.fdroid.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;

import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.repository.MailboxDao;
import com.ctemplar.app.fdroid.repository.UserStore;
import com.ctemplar.app.fdroid.repository.entity.MailboxEntity;
import com.ctemplar.app.fdroid.security.PGPManager;
import timber.log.Timber;

public class EncryptUtils {
    private static final MailboxDao mailboxDao = CTemplarApp.getAppDatabase().mailboxDao();
    private static final UserStore userStore = CTemplarApp.getUserStore();

    public static String decryptContent(String content, long mailboxId, boolean decrypt) {
        if (content == null || content.length() == 0) {
            return "";
        }
        if (decrypt) {
            MailboxEntity mailboxEntity = mailboxDao.getById(mailboxId);
            String password = userStore.getUserPassword();
            if (mailboxEntity == null || password == null || password.length() == 0) {
                return "";
            }
            String privateKey = mailboxEntity.getPrivateKey();
            content = PGPManager.decrypt(content, privateKey, password);
        }
        return content;
    }

    public static String decryptSubject(String subject, long mailboxId) {
        return decryptContent(subject, mailboxId, true).replaceAll("<img.+?>", "");
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
        String password = userStore.getUserPassword();
        if (mailboxEntity == null || password == null || password.length() == 0) {
            return "";
        }
        String privateKey = mailboxEntity.getPrivateKey();
        return PGPManager.decrypt(content, privateKey, password);
    }

    public static boolean encryptAttachment(File originalFile, File encryptedFile, List<String> publicKeyList) {
        int fileSize = (int) originalFile.length();
        byte[] fileBytes = new byte[fileSize];
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(originalFile));
            bufferedInputStream.read(fileBytes, 0, fileBytes.length);
            bufferedInputStream.close();
        } catch (IOException e) {
            Timber.e(e);
            return false;
        }

        byte[] encryptedBytes = PGPManager.encrypt(
                fileBytes,
                publicKeyList.toArray(new String[0]),
                true
        );

        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(encryptedFile));
            bufferedOutputStream.write(encryptedBytes);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        } catch (IOException e) {
            Timber.e(e);
            return false;
        }
        return true;
    }

    public static boolean decryptAttachment(File encryptedFile, File decryptedFile, String password, String privateKey) {
        int fileSize = (int) encryptedFile.length();
        byte[] fileBytes = new byte[fileSize];
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(encryptedFile));
            bufferedInputStream.read(fileBytes, 0, fileBytes.length);
            bufferedInputStream.close();
        } catch (IOException e) {
            Timber.e(e);
            return false;
        }

        try {
            byte[] encryptedBytes = PGPManager.decrypt(fileBytes, privateKey, password);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(decryptedFile));
            bufferedOutputStream.write(encryptedBytes);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
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
