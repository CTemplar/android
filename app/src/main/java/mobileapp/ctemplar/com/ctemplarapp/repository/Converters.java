package mobileapp.ctemplar.com.ctemplarapp.repository;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.repository.entity.AttachmentEntity;

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
}