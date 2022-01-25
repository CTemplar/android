package com.ctemplar.app.fdroid.repository;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.ctemplar.app.fdroid.repository.converter.CommonConverter;
import com.ctemplar.app.fdroid.repository.converter.EmailConverter;
import com.ctemplar.app.fdroid.repository.converter.KeyTypeConverter;
import com.ctemplar.app.fdroid.repository.entity.ContactEntity;
import com.ctemplar.app.fdroid.repository.entity.MailboxEntity;
import com.ctemplar.app.fdroid.repository.entity.MailboxKeyEntity;
import com.ctemplar.app.fdroid.repository.entity.MessageEntity;

@Database(
        entities = {
                MailboxEntity.class,
                MailboxKeyEntity.class,
                ContactEntity.class,
                MessageEntity.class
        },
        version = 16,
        exportSchema = false
)
@TypeConverters({
        CommonConverter.class,
        EmailConverter.class,
        KeyTypeConverter.class
})
public abstract class AppDatabase extends RoomDatabase {
    public abstract MailboxDao mailboxDao();

    public abstract MailboxKeyDao mailboxKeyDao();

    public abstract ContactDao contactDao();

    public abstract MessageDao messageDao();
}
