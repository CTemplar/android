package com.ctemplar.app.fdroid.repository.mapper;

import java.util.ArrayList;
import java.util.List;

import com.ctemplar.app.fdroid.net.response.mailboxes.MailboxResponse;
import com.ctemplar.app.fdroid.repository.entity.MailboxEntity;
import com.ctemplar.app.fdroid.repository.enums.KeyType;

public class MailboxMapper {
    public static MailboxEntity map(MailboxResponse response) {
        if (response == null) {
            return null;
        }
        return new MailboxEntity(
                response.getId(),
                response.getEmail(),
                response.isDeleted(),
                response.getDeletedAt(),
                response.getDisplayName(),
                response.isDefault(),
                response.isEnabled(),
                response.getPrivateKey(),
                response.getPublicKey(),
                response.getFingerprint(),
                response.getSortOrder(),
                response.getSignature(),
                response.getPreferEncrypt(),
                response.isAutocryptEnabled(),
                response.getKeyType() == null ? KeyType.RSA4096 : response.getKeyType()
        );
    }

    public static List<MailboxEntity> map(List<MailboxResponse> responses) {
        if (responses == null) {
            return null;
        }
        List<MailboxEntity> mailboxEntities = new ArrayList<>();
        for (MailboxResponse response : responses) {
            mailboxEntities.add(map(response));
        }
        return mailboxEntities;
    }
}
