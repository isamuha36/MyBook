package com.isamuha.mybook.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.isamuha.mybook.model.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM user WHERE username = :username AND password = :password")
    suspend fun getUser(username: String, password : String): User?

    @Query("SELECT * FROM user WHERE id = :userId")
    suspend fun getUserById(userId: String): User?
}
