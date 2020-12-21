package mobileapp.ctemplar.com.ctemplarapp.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MessageEntity;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM messages")
    List<MessageEntity> getAll();

    @Query("SELECT * FROM messages WHERE requestFolder=:folderName ORDER BY updatedAt DESC")
    List<MessageEntity> getAllByFolder(String folderName);

    @Query("SELECT * FROM messages WHERE isStarred=1 ORDER BY updatedAt DESC")
    List<MessageEntity> getAllStarred();

    @Query("SELECT * FROM messages WHERE isRead=0 AND folderName<>'spam' ORDER BY updatedAt DESC")
    List<MessageEntity> getAllUnread();

    @Query("SELECT * FROM messages WHERE folderName<>'spam' AND folderName<>'trash' ORDER BY updatedAt DESC")
    List<MessageEntity> getAllMails();

    @Insert(onConflict = REPLACE)
    void save(MessageEntity messageEntity);

    @Insert(onConflict = REPLACE)
    void saveAll(List<MessageEntity> messages);

    @Insert(onConflict = IGNORE)
    void saveAllWithIgnore(List<MessageEntity> messages);

    @Delete
    void delete(MessageEntity mailbox);

    @Query("DELETE FROM messages")
    void deleteAll();

    @Query("SELECT * FROM messages WHERE id=:id")
    MessageEntity getById(long id);

    @Query("UPDATE messages SET isStarred=:isStarred WHERE id=:id")
    void updateIsStarred(long id, boolean isStarred);

    @Query("UPDATE messages SET isRead=:isRead WHERE id=:id")
    void updateIsRead(long id, boolean isRead);

    @Query("DELETE FROM messages WHERE requestFolder=:folderName")
    void deleteAllByFolder(String folderName);

    @Query("DELETE FROM messages WHERE isStarred=1")
    void deleteStarred();

    @Query("DELETE FROM messages WHERE isRead=0 AND folderName<>'spam'")
    void deleteUnread();

    @Query("DELETE FROM messages WHERE folderName<>'spam' AND folderName<>'trash'")
    void deleteAllMails();

    @Query("DELETE FROM messages WHERE parent=:id")
    void deleteAllByParentId(String id);

    @Query("DELETE FROM messages WHERE id=:id")
    void deleteById(long id);

    @Query("SELECT * FROM messages WHERE parent=:id")
    List<MessageEntity> getByParentId(String id);

    @Query("UPDATE messages SET requestFolder=folderName=:newFolderName WHERE id=:messageId")
    void updateFolderName(long messageId, String newFolderName);
}
