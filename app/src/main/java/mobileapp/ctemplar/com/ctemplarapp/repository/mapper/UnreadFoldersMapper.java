package mobileapp.ctemplar.com.ctemplarapp.repository.mapper;

import androidx.annotation.Nullable;

import mobileapp.ctemplar.com.ctemplarapp.net.response.emails.UnreadFoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.dto.emails.UnreadFoldersDTO;

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
