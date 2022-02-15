package com.ctemplar.app.fdroid.repository.mapper;

import androidx.annotation.Nullable;

import com.ctemplar.app.fdroid.net.response.folders.CustomFolderResponse;
import com.ctemplar.app.fdroid.repository.dto.folders.CustomFolderDTO;

public class CustomFolderMapper {
    @Nullable
    public static CustomFolderDTO map(@Nullable CustomFolderResponse response) {
        if (response == null) {
            return null;
        }
        return new CustomFolderDTO(
                response.getId(),
                response.getName(),
                response.getColor(),
                response.getSortOrder()
        );
    }
}
