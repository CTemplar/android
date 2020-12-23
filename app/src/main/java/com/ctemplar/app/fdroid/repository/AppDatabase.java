package com.ctemplar.app.fdroid.repository;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.ctemplar.app.fdroid.repository.entity.ContactEntity;
import com.ctemplar.app.fdroid.repository.entity.MailboxEntity;
import com.ctemplar.app.fdroid.repository.entity.MessageEntity;

@Database(
        entities = {
                MailboxEntity.class,
                ContactEntity.class,
                MessageEntity.class
        },
        version = 10,
        exportSchema = false
)
@TypeConverters({
        Converters.class
})
public abstract class AppDatabase extends RoomDatabase {
    public abstract MailboxDao mailboxDao();

    public abstract ContactDao contactDao();

    public abstract MessageDao messageDao();
}
