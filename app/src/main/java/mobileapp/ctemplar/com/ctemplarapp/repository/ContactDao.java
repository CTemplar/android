package mobileapp.ctemplar.com.ctemplarapp.repository;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.repository.entity.ContactEntity;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ContactDao {

    @Query("SELECT * FROM contacts ORDER BY id DESC")
    List<ContactEntity> getAll();

    @Insert(onConflict = REPLACE)
    void save(ContactEntity contactEntity);

    @Query("DELETE FROM contacts WHERE id = :id")
    void delete(long id);

    @Query("SELECT * FROM contacts WHERE id = :id")
    ContactEntity getById(long id);
}