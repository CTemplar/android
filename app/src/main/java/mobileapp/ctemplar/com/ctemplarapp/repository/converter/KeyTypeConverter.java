package mobileapp.ctemplar.com.ctemplarapp.repository.converter;

import androidx.room.TypeConverter;

import mobileapp.ctemplar.com.ctemplarapp.repository.enums.KeyType;
import timber.log.Timber;

public class KeyTypeConverter {
    @TypeConverter
    public static KeyType fromString(String value) {
        if (value == null) {
            return null;
        }
        try {
            return KeyType.valueOf(value);
        } catch (IllegalArgumentException e) {
            Timber.e("fromString failed: undefined value %s", value);
        }
        return null;
    }

    @TypeConverter
    public static String toString(KeyType value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }
}
