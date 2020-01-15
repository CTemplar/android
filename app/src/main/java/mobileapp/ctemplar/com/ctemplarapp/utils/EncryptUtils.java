package mobileapp.ctemplar.com.ctemplarapp.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.repository.MailboxDao;
import mobileapp.ctemplar.com.ctemplarapp.repository.UserStore;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.security.PGPManager;
import timber.log.Timber;

public class EncryptUtils {

    private static MailboxDao mailboxDao = CTemplarApp.getAppDatabase().mailboxDao();

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
                false
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

    public static MailboxEntity getDefaultMailbox() {
        if (mailboxDao.getDefault() != null) {
            return mailboxDao.getDefault();
        } else {
            if (!mailboxDao.getAll().isEmpty()) {
                return mailboxDao.getAll().get(0);
            } else {
                Timber.e("Mailbox not found");
            }
        }
        return null;
    }
}
