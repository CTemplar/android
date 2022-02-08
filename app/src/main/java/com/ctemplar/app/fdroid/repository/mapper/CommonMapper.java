package com.ctemplar.app.fdroid.repository.mapper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class CommonMapper {
    @Nullable
    @SuppressWarnings("unchecked")
    public static <I, O> List<O> map(@NonNull Class<?> clazz, @Nullable List<I> responses) {
        if (responses == null) {
            return null;
        }
        List<O> entities = new ArrayList<>();
        for (I response : responses) {
            try {
                entities.add((O) clazz.getMethod("map", response.getClass()).invoke(null,
                        response));
            } catch (Exception e) {
                Timber.e(e, "CommonMapper map");
                return null;
            }
        }
        return entities;
    }
}
