package mobileapp.ctemplar.com.ctemplarapp.repository;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface MailboxDao {

    @Query("SELECT * FROM mailboxes")
    List<MailboxEntity> getAll();

    @Query("SELECT * FROM mailboxes WHERE isDefault = 1")
    MailboxEntity getDefault();

    @Query("UPDATE mailboxes SET isDefault = :state WHERE id = :mailboxId")
    void setDefault(long mailboxId, boolean state);

    @Query("UPDATE mailboxes SET isEnabled = :isEnabled WHERE id = :mailboxId")
    void setEnabled(long mailboxId, boolean isEnabled);

    @Insert(onConflict = REPLACE)
    void save(MailboxEntity mailboxEntity);

    @Insert(onConflict = REPLACE)
    void saveAll(List<MailboxEntity> mailboxes);

    @Delete
    void delete(MailboxEntity mailbox);

    @Query("DELETE FROM mailboxes")
    void deleteAll();

    @Query("SELECT * FROM mailboxes WHERE id = :id")
    MailboxEntity getById(long id);

    @Query("SELECT * FROM mailboxes WHERE email = :email")
    MailboxEntity getByEmail(String email);
}
