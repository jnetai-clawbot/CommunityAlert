package com.jnetai.communityalert.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jnetai.communityalert.data.converter.Converters
import com.jnetai.communityalert.data.dao.AlertDao
import com.jnetai.communityalert.data.entity.Alert

@Database(entities = [Alert::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AlertDatabase : RoomDatabase() {

    abstract fun alertDao(): AlertDao

    companion object {
        @Volatile
        private var INSTANCE: AlertDatabase? = null

        fun getDatabase(context: Context): AlertDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AlertDatabase::class.java,
                    "alert_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}