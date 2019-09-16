package mobileapp.ctemplar.com.ctemplarapp.utils;

import com.didisoft.pgp.exceptions.NonPGPDataException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import timber.log.Timber;

public class EncryptUtils {

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

        PGPManager pgpManager = new PGPManager();
        byte[] encryptedBytes = pgpManager.encryptBytes(fileBytes, publicKeyList.toArray(new String[0]));

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

        PGPManager pgpManager = new PGPManager();
        try {
            byte[] encryptedBytes = pgpManager.decryptBytes(fileBytes, privateKey, password);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(decryptedFile));
            bufferedOutputStream.write(encryptedBytes);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();

        } catch (NonPGPDataException | IOException e) {
            Timber.e(e);
            return false;
        }

        return true;
    }
}
