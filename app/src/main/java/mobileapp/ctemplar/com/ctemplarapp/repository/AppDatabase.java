package mobileapp.ctemplar.com.ctemplarapp.repository;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import mobileapp.ctemplar.com.ctemplarapp.repository.converter.CommonConverter;
import mobileapp.ctemplar.com.ctemplarapp.repository.converter.EmailConverter;
import mobileapp.ctemplar.com.ctemplarapp.repository.converter.KeyTypeConverter;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.ContactEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxKeyEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MessageEntity;

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
