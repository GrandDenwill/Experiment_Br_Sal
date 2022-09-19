package com.denwill.kotlin.bristolsalary

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

private const val DB_NAME = "stuff.db"

@Database(entities = [SalaryEntity::class], version = 1)
@TypeConverters(TypeTransmogrifier::class)
abstract class SalaryDatabase : RoomDatabase() {
    abstract fun todoStore(): SalaryEntity.Store

    companion object {
        fun newInstance(context: Context) =
            Room.databaseBuilder(context, SalaryDatabase::class.java, DB_NAME).build()

        fun newTestInstance(context: Context) =
            Room.inMemoryDatabaseBuilder(context, SalaryDatabase::class.java).build()
    }
}