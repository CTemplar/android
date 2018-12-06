package mobileapp.ctemplar.com.ctemplarapp.repository;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import mobileapp.ctemplar.com.ctemplarapp.repository.entity.ContactEntity;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;

@Database(entities = {MailboxEntity.class, ContactEntity.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MailboxDao mailboxDao();
    public abstract ContactDao contactDao();
}