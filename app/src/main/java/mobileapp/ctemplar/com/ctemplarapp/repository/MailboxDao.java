package mobileapp.ctemplar.com.ctemplarapp.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface MailboxDao {
    @Query("SELECT * FROM mailboxes ORDER BY email")
    List<MailboxEntity> getAll();

    @Query("SELECT * FROM mailboxes WHERE isDefault = 1")
    MailboxEntity getDefault();

    @Query("UPDATE mailboxes SET isDefault = :state WHERE id = :mailboxId")
    void setDefault(long mailboxId, boolean state);

    @Query("UPDATE mailboxes SET isEnabled = :isEnabled WHERE id = :mailboxId")
    void setEnabled(long mailboxId, boolean isEnabled);

    @Query("UPDATE mailboxes SET displayName = :displayName, signature = :signature WHERE id = :mailboxId")
    void updateSignature(long mailboxId, String displayName, String signature);

    @Insert(onConflict = REPLACE)
    void save(MailboxEntity mailboxEntity);

    @Insert(onConflict = REPLACE)
    void saveAll(List<MailboxEntity> mailboxEntities);

    @Delete
    void delete(MailboxEntity mailbox);

    @Query("DELETE FROM mailboxes")
    void deleteAll();

    @Query("SELECT * FROM mailboxes WHERE id = :id")
    MailboxEntity getById(long id);

    @Query("SELECT * FROM mailboxes WHERE email = :email")
    MailboxEntity getByEmail(String email);
}
