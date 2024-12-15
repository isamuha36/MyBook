package com.isamuha.mybook.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.isamuha.mybook.model.Book

@Dao
interface BookmarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: Book)

    @Query("SELECT * FROM book WHERE user_id = :userId")
    suspend fun getAllBookmark(userId: String): List<Book>

    @Query("SELECT * FROM book WHERE id = :bookId AND user_id = :userId")
    suspend fun getBookmarkById(bookId: String, userId: String): Book?

    @Query("DELETE FROM book WHERE id = :bookId AND user_id = :userId")
    suspend fun delete(bookId: String, userId: String)
}