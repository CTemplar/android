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

    @Insert(onConflict = REPLACE)
    void saveAll(List<MailboxKeyEntity> entities);
}
