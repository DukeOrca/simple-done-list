package com.duke.orca.android.kotlin.simpledonelist.persistence.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.duke.orca.android.kotlin.simpledonelist.application.Application
import com.duke.orca.android.kotlin.simpledonelist.persistence.dao.DoneListDao
import com.duke.orca.android.kotlin.simpledonelist.donelist.model.Done
import com.duke.orca.android.kotlin.simpledonelist.history.models.JulianDay
import com.duke.orca.android.kotlin.simpledonelist.persistence.dao.HistoryDao

@Database(entities = [Done::class, JulianDay::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun doneDao(): DoneListDao
    abstract fun historyDao(): HistoryDao

    companion object {
        private const val PACKAGE_NAME = "${Application.PACKAGE_NAME}.persistence.database"
        private const val NAME = "$PACKAGE_NAME.AppDatabase:1.1.0"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE done ADD COLUMN julian_day INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        NAME
                    )
                        .addMigrations(MIGRATION_1_2)
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}