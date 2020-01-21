package mobileapp.ctemplar.com.ctemplarapp.repository;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import mobileapp.ctemplar.com.ctemplarapp.repository.entity.ContactEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MessageEntity;

@Database(entities = {MailboxEntity.class, ContactEntity.class, MessageEntity.class}, version = 9)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract MailboxDao mailboxDao();

    public abstract ContactDao contactDao();

    public abstract MessageDao messageDao();
}
