package com.ctemplar.app.fdroid.repository.converter;

import static com.ctemplar.app.fdroid.utils.DateUtils.GENERAL_GSON;

import androidx.room.TypeConverter;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CommonConverter {
    @TypeConverter
    public static List<String> stringToList(String value) {
        Type type = new TypeToken<List<String>>() {
        }.getType();
        return GENERAL_GSON.fromJson(value, type);
    }

    @TypeConverter
    public static String stringListToString(List<String> value) {
        return GENERAL_GSON.toJson(value);
    }

    @TypeConverter
    public static Date longToDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToLong(Date value) {
        return value == null ? null : value.getTime();
    }

    @TypeConverter
    public static Map<String, String> stringToStringMap(String value) {
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        return GENERAL_GSON.fromJson(value, type);
    }

    @TypeConverter
    public static String stringMapToString(Map<String, String> value) {
        return GENERAL_GSON.toJson(value);
    }
}
