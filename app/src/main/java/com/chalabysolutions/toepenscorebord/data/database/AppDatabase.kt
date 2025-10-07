package com.chalabysolutions.toepenscorebord.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chalabysolutions.toepenscorebord.data.dao.GameRoundDao
import com.chalabysolutions.toepenscorebord.data.dao.PlayerDao
import com.chalabysolutions.toepenscorebord.data.model.Player
import com.chalabysolutions.toepenscorebord.data.model.GameRound

@Database(
    entities = [Player::class, GameRound::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun gameRoundDao(): GameRoundDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "toepen_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}