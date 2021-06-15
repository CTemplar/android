package mobileapp.ctemplar.com.ctemplarapp.repository.mapper;

import java.util.ArrayList;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.net.response.mailboxes.MailboxKeyResponse;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxKeyEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.enums.KeyType;

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
        response.getMailboxId()
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
