package mobileapp.ctemplar.com.ctemplarapp.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.Date;
import java.util.List;

import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MessageEntity;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM messages")
    List<MessageEntity> getAll();

    @Query("SELECT * FROM messages WHERE folderName=:folderName AND parent IS NULL ORDER BY updatedAt DESC LIMIT :limit OFFSET :offset")
    List<MessageEntity> getAllByFolder(String folderName, int limit, int offset);

    @Query("SELECT * FROM messages WHERE folderName=:folderName AND parent IS NULL ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    List<MessageEntity> getAllByFolderAndCreatedAt(String folderName, int limit, int offset);

    @Query("SELECT * FROM messages WHERE (folderName='sent' OR send=1 OR hasSentChild=1) AND parent IS NULL ORDER BY updatedAt DESC LIMIT :limit OFFSET :offset")//  CASE WHEN updatedAt >= createdAt THEN updatedAt WHEN updatedAt < createdAt THEN createdAt END
    List<MessageEntity> getSent(int limit, int offset);

    @Query("SELECT * FROM messages WHERE (folderName='inbox' OR hasInboxChild=1) AND parent IS NULL ORDER BY updatedAt DESC LIMIT :limit OFFSET :offset")//  CASE WHEN updatedAt >= createdAt THEN updatedAt WHEN updatedAt < createdAt THEN createdAt END
    List<MessageEntity> getInbox(int limit, int offset);

    @Query("SELECT * FROM messages WHERE isStarred=1 ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    List<MessageEntity> getAllStarred(int limit, int offset);

    @Query("SELECT * FROM messages WHERE isRead=0 AND folderName<>'spam' AND parent IS NULL ORDER BY updatedAt DESC LIMIT :limit OFFSET :offset")
    List<MessageEntity> getAllUnread(int limit, int offset);

    @Query("SELECT * FROM messages WHERE folderName<>'spam' AND folderName<>'trash' AND parent IS NULL ORDER BY updatedAt DESC LIMIT :limit OFFSET :offset")
    List<MessageEntity> getAllMails(int limit, int offset);

    @Insert(onConflict = REPLACE)
    void save(MessageEntity messageEntity);

    @Insert(onConflict = REPLACE)
    void saveAll(List<MessageEntity> messages);

    @Insert(onConflict = IGNORE)
    void saveAllWithIgnore(List<MessageEntity> messages);

    @Delete
    void delete(MessageEntity mailbox);

    @Delete
    void delete(List<MessageEntity> messages);

    @Query("DELETE FROM messages WHERE updatedAt > :from AND updatedAt < :to AND folderName<>'spam' AND folderName<>'trash' AND parent IS NULL")
    int deleteAllEmailsInPeriod(Date from, Date to);

    @Query("DELETE FROM messages WHERE updatedAt > :from AND updatedAt < :to AND folderName=:folder AND parent IS NULL")
    int deleteByFolderInPeriod(String folder, Date from, Date to);

    @Query("DELETE FROM messages WHERE createdAt > :from AND createdAt < :to AND folderName=:folder AND parent IS NULL")
    int deleteByFolderInPeriodByCreatedAt(String folder, Date from, Date to);

    @Query("UPDATE messages SET isRead=1 WHERE updatedAt > :from AND updatedAt < :to AND isRead=0 AND folderName<>'spam' AND parent IS NULL")
    int markUnreadAsReadInPeriod(Date from, Date to);

    @Query("UPDATE messages SET isStarred=0 WHERE createdAt > :from AND createdAt < :to AND isStarred=1 AND parent IS NULL")
    int markStarredAsUnstarredInPeriod(Date from, Date to);

    @Query("DELETE FROM messages")
    void deleteAll();

    @Query("SELECT * FROM messages WHERE id=:id")
    MessageEntity getById(long id);

    @Query("UPDATE messages SET isStarred=:isStarred WHERE id=:id")
    void updateIsStarred(long id, boolean isStarred);

    @Query("UPDATE messages SET isRead=:isRead WHERE id=:id")
    void updateIsRead(long id, boolean isRead);

    @Query("DELETE FROM messages WHERE folderName=:folderName")
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

    @Query("UPDATE messages SET folderName=:newFolderName WHERE id=:messageId")
    void updateFolderName(long messageId, String newFolderName);

    @Query("UPDATE messages SET decryptedSubject=:decryptedSubject WHERE id=:messageId")
    void updateDecryptedSubject(long messageId, String decryptedSubject);

    @Query("UPDATE messages SET decryptedSubject=NULL")
    void clearAllDecryptedSubjects();
}
