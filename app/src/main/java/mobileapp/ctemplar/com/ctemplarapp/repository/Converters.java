package mobileapp.ctemplar.com.ctemplarapp.repository;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.repository.entity.AttachmentEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.UserDisplayEntity;

public class Converters {
    @TypeConverter
    public static List<String> fromString(String value) {
        Type listType = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<String> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<AttachmentEntity> stringToAttachments(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<AttachmentEntity>>() {}.getType();
        return gson.fromJson(json, type);
    }

    @TypeConverter
    public static String attachmentsToString(List<AttachmentEntity> list) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<AttachmentEntity>>() {}.getType();
        return gson.toJson(list, type);
    }

    @TypeConverter
    public static UserDisplayEntity stringToUserDisplay(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<UserDisplayEntity>() {}.getType();
        return gson.fromJson(json, type);
    }

    @TypeConverter
    public static String userDisplayToString(UserDisplayEntity userDisplay) {
        Gson gson = new Gson();
        Type type = new TypeToken<UserDisplayEntity>() {}.getType();
        return gson.toJson(userDisplay, type);
    }

    @TypeConverter
    public static List<UserDisplayEntity> stringToUserDisplayList(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<UserDisplayEntity>>() {}.getType();
        return gson.fromJson(json, type);
    }

    @TypeConverter
    public static String userDisplayListToString(List<UserDisplayEntity> list) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<UserDisplayEntity>>() {}.getType();
        return gson.toJson(list, type);
    }
}
