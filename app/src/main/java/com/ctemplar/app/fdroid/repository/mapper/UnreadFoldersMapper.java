package com.ctemplar.app.fdroid.repository.mapper;

import androidx.annotation.Nullable;

import com.ctemplar.app.fdroid.net.response.emails.UnreadFoldersResponse;
import com.ctemplar.app.fdroid.repository.dto.emails.UnreadFoldersDTO;

public class UnreadFoldersMapper {
    @Nullable
    public static UnreadFoldersDTO map(@Nullable UnreadFoldersResponse response) {
        if (response == null) {
            return null;
        }
        return new UnreadFoldersDTO(
                response.getInbox(),
                response.getDraft(),
                response.getStarred(),
                response.getSpam(),
                response.getOutboxDeadManCounter(),
                response.getOutboxDelayedDeliveryCounter(),
                response.getOutboxSelfDestructCounter()
        );
    }
}
