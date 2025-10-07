package com.chalabysolutions.toepenscorebord

import android.content.Context
import androidx.room.Room
import com.chalabysolutions.toepenscorebord.data.database.AppDatabase
import com.chalabysolutions.toepenscorebord.data.repository.ToepenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "toepen_database"
            ).fallbackToDestructiveMigration(false).build()
    }

    @Provides
    @Singleton
    fun provideToepenRepository(db: AppDatabase): ToepenRepository {
        return ToepenRepository(db) // repository haalt zelf de Dao's uit de db
    }
}