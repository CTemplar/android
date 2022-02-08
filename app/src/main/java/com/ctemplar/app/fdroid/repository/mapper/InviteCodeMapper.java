package com.ctemplar.app.fdroid.repository.mapper;

import androidx.annotation.Nullable;

import com.ctemplar.app.fdroid.net.response.invites.InviteCodeResponse;
import com.ctemplar.app.fdroid.repository.dto.invites.InviteCodeDTO;

public class InviteCodeMapper {
    @Nullable
    public static InviteCodeDTO map(@Nullable InviteCodeResponse response) {
        if (response == null) {
            return null;
        }
        return new InviteCodeDTO(
                response.getExpirationDate(),
                response.getCode(),
                response.isUsed(),
                response.isPremium()
        );
    }
}
