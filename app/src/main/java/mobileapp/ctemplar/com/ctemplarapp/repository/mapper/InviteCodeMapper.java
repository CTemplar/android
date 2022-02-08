package mobileapp.ctemplar.com.ctemplarapp.repository.mapper;

import androidx.annotation.Nullable;

import mobileapp.ctemplar.com.ctemplarapp.net.response.invites.InviteCodeResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.invites.InviteCodeDTO;

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
