package com.chalabysolutions.toepenscorebord.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.chalabysolutions.toepenscorebord.data.dao.SessionDao
import com.chalabysolutions.toepenscorebord.data.dao.PlayerDao
import com.chalabysolutions.toepenscorebord.data.dao.RoundPlayerDao
import com.chalabysolutions.toepenscorebord.data.dao.SessionPlayerDao
import com.chalabysolutions.toepenscorebord.data.dao.RoundDao
import com.chalabysolutions.toepenscorebord.data.entity.Player
import com.chalabysolutions.toepenscorebord.data.entity.Session
import com.chalabysolutions.toepenscorebord.data.entity.RoundPlayer
import com.chalabysolutions.toepenscorebord.data.entity.SessionPlayer
import com.chalabysolutions.toepenscorebord.data.entity.Round

@Database(
    entities = [
        Player::class,
        Session::class,
        Round::class,
        RoundPlayer::class,
        SessionPlayer::class
               ],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun sessionPlayerDao(): SessionPlayerDao
    abstract fun sessionDao(): SessionDao
    abstract fun roundDao(): RoundDao
    abstract fun roundPlayerDao(): RoundPlayerDao
}