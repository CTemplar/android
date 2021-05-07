package mobileapp.ctemplar.com.ctemplarapp.repository.mapper;

import java.util.ArrayList;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.net.response.mailboxes.MailboxResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.enums.KeyType;

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
