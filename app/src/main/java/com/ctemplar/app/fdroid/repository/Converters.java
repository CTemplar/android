package com.ctemplar.app.fdroid.repository;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import com.ctemplar.app.fdroid.repository.entity.AttachmentEntity;
import com.ctemplar.app.fdroid.repository.entity.UserDisplayEntity;

import static com.ctemplar.app.fdroid.utils.DateUtils.GENERAL_GSON;

public class Converters {
    @TypeConverter
    public static List<String> fromString(String value) {
        Type listType = new TypeToken<List<String>>() {}.getType();
        return GENERAL_GSON.fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<String> list) {
        return GENERAL_GSON.toJson(list);
    }

    @TypeConverter
    public static List<AttachmentEntity> stringToAttachments(String json) {
        Type type = new TypeToken<List<AttachmentEntity>>() {}.getType();
        return GENERAL_GSON.fromJson(json, type);
    }

    @TypeConverter
    public static String attachmentsToString(List<AttachmentEntity> list) {
        Type type = new TypeToken<List<AttachmentEntity>>() {}.getType();
        return GENERAL_GSON.toJson(list, type);
    }

    @TypeConverter
    public static UserDisplayEntity stringToUserDisplay(String json) {
        Type type = new TypeToken<UserDisplayEntity>() {}.getType();
        return GENERAL_GSON.fromJson(json, type);
    }

    @TypeConverter
    public static String userDisplayToString(UserDisplayEntity userDisplay) {
        Type type = new TypeToken<UserDisplayEntity>() {}.getType();
        return GENERAL_GSON.toJson(userDisplay, type);
    }

    @TypeConverter
    public static List<UserDisplayEntity> stringToUserDisplayList(String json) {
        Type type = new TypeToken<List<UserDisplayEntity>>() {}.getType();
        return GENERAL_GSON.fromJson(json, type);
    }

    @TypeConverter
    public static String userDisplayListToString(List<UserDisplayEntity> list) {
        Type type = new TypeToken<List<UserDisplayEntity>>() {}.getType();
        return GENERAL_GSON.toJson(list, type);
    }

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
