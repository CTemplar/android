package mobileapp.ctemplar.com.ctemplarapp.repository.mapper;

import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxKeyEntity;
import mobileapp.ctemplar.com.ctemplarapp.settings.keys.GeneralizedMailboxKey;

public class GeneralizedMailboxKeyMapper {
    public static GeneralizedMailboxKey map(MailboxEntity entity) {
        if (entity == null) {
            return null;
        }
        return new GeneralizedMailboxKey(
                -1,
                entity.getPrivateKey(),
                entity.getPublicKey(),
                entity.getFingerprint(),
                entity.getKeyType()
        );
    }

    public static GeneralizedMailboxKey map(MailboxKeyEntity entity) {
        if (entity == null) {
            return null;
        }
        return new GeneralizedMailboxKey(
                entity.getId(),
                entity.getPrivateKey(),
                entity.getPublicKey(),
                entity.getFingerprint(),
                entity.getKeyType()
        );
    }
}
