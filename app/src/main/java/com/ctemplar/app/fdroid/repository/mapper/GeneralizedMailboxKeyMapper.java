package com.ctemplar.app.fdroid.repository.mapper;

import com.ctemplar.app.fdroid.repository.entity.MailboxEntity;
import com.ctemplar.app.fdroid.repository.entity.MailboxKeyEntity;
import com.ctemplar.app.fdroid.repository.entity.GeneralizedMailboxKey;

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
