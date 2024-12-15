package com.isamuha.mybook.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.isamuha.mybook.model.Book
import com.isamuha.mybook.model.User

@Database(entities = [Book::class], version = 1, exportSchema = false)
abstract class DatabaseInstance : RoomDatabase() {

    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        @Volatile
        private var INSTANCE: DatabaseInstance? = null

        fun getDatabase(context: Context): DatabaseInstance {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseInstance::class.java,
                    "mybook_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
