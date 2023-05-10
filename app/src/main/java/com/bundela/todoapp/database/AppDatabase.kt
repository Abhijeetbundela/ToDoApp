package com.bundela.todoapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bundela.todoapp.database.converter.DateTypeConverter
import com.bundela.todoapp.database.converter.PriorityConverter
import com.bundela.todoapp.database.dao.TodoDao
import com.bundela.todoapp.database.model.TodoData

@Database(
    entities = [TodoData::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateTypeConverter::class, PriorityConverter::class)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = buildRoomDB(context)
                }
            }
            return INSTANCE!!
        }

        private fun buildRoomDB(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "database_0.2"
            ).build()

    }

    fun destroyInstance() {
        INSTANCE = null
    }

    abstract fun toDoDao(): TodoDao
}