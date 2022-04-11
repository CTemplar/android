package mobileapp.ctemplar.com.ctemplarapp.repository.mapper;

import androidx.annotation.Nullable;

import mobileapp.ctemplar.com.ctemplarapp.net.response.folders.CustomFolderResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.folders.CustomFolderDTO;

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
