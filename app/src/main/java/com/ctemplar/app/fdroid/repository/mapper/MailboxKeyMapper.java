package com.ctemplar.app.fdroid.repository.mapper;

import java.util.ArrayList;
import java.util.List;

import com.ctemplar.app.fdroid.net.response.mailboxes.MailboxKeyResponse;
import com.ctemplar.app.fdroid.repository.entity.MailboxKeyEntity;
import com.ctemplar.app.fdroid.repository.enums.KeyType;

public class MailboxKeyMapper {
    public static MailboxKeyEntity map(MailboxKeyResponse response) {
        if (response == null) {
            return null;
        }
        return new MailboxKeyEntity(
                response.getId(),
                response.getPrivateKey(),
                response.getPublicKey(),
                response.getFingerprint(),
                response.isDeleted(),
                response.getDeletedAt(),
                response.getKeyType() == null ? KeyType.RSA4096 : response.getKeyType(),
                response.getMailbox()
        );
    }

    public static List<MailboxKeyEntity> map(List<MailboxKeyResponse> responses) {
        if (responses == null) {
            return null;
        }
        List<MailboxKeyEntity> mailboxKeyEntities = new ArrayList<>();
        for (MailboxKeyResponse response : responses) {
            mailboxKeyEntities.add(map(response));
        }
        return mailboxKeyEntities;
    }
}
