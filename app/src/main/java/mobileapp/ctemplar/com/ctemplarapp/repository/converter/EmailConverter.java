package mobileapp.ctemplar.com.ctemplarapp.repository.converter;

import static mobileapp.ctemplar.com.ctemplarapp.utils.DateUtils.GENERAL_GSON;

import androidx.room.TypeConverter;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.repository.entity.AttachmentEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.EncryptionMessageEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.UserDisplayEntity;

public class EmailConverter {
    @TypeConverter
    public static List<AttachmentEntity> stringToAttachments(String value) {
        Type type = new TypeToken<List<AttachmentEntity>>() {
        }.getType();
        return GENERAL_GSON.fromJson(value, type);
    }

    @TypeConverter
    public static String attachmentsToString(List<AttachmentEntity> value) {
        Type type = new TypeToken<List<AttachmentEntity>>() {
        }.getType();
        return GENERAL_GSON.toJson(value, type);
    }

    @TypeConverter
    public static UserDisplayEntity stringToUserDisplay(String value) {
        Type type = new TypeToken<UserDisplayEntity>() {
        }.getType();
        return GENERAL_GSON.fromJson(value, type);
    }

    @TypeConverter
    public static String userDisplayToString(UserDisplayEntity value) {
        Type type = new TypeToken<UserDisplayEntity>() {
        }.getType();
        return GENERAL_GSON.toJson(value, type);
    }

    @TypeConverter
    public static List<UserDisplayEntity> stringToUserDisplayList(String value) {
        Type type = new TypeToken<List<UserDisplayEntity>>() {
        }.getType();
        return GENERAL_GSON.fromJson(value, type);
    }

    @TypeConverter
    public static String userDisplayListToString(List<UserDisplayEntity> value) {
        Type type = new TypeToken<List<UserDisplayEntity>>() {
        }.getType();
        return GENERAL_GSON.toJson(value, type);
    }

    @TypeConverter
    public static EncryptionMessageEntity stringToEncryptionMessage(String value) {
        Type type = new TypeToken<EncryptionMessageEntity>() {
        }.getType();
        return GENERAL_GSON.fromJson(value, type);
    }

    @TypeConverter
    public static String encryptionMessageToString(EncryptionMessageEntity value) {
        Type type = new TypeToken<EncryptionMessageEntity>() {
        }.getType();
        return GENERAL_GSON.toJson(value, type);
    }
}
