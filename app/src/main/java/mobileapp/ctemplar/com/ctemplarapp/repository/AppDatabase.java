package mobileapp.ctemplar.com.ctemplarapp.repository;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import mobileapp.ctemplar.com.ctemplarapp.repository.entity.ContactEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MessageEntity;

@Database(entities = {MailboxEntity.class, ContactEntity.class, MessageEntity.class}, version = 6)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract MailboxDao mailboxDao();

    public abstract ContactDao contactDao();

    public abstract MessageDao messageDao();
}
