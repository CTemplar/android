package mobileapp.ctemplar.com.ctemplarapp.repository;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxKeyEntity;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface MailboxKeyDao {
    @Query("SELECT * FROM mailbox_keys")
    List<MailboxKeyEntity> getAll();

    @Query("SELECT * FROM mailbox_keys WHERE mailbox = :mailboxId")
    List<MailboxKeyEntity> getByMailboxId(long mailboxId);

    @Insert(onConflict = REPLACE)
    void save(MailboxKeyEntity entity);

    @Insert(onConflict = REPLACE)
    void saveAll(List<MailboxKeyEntity> entities);

    @Query("DELETE FROM mailbox_keys")
    void deleteAll();
}
