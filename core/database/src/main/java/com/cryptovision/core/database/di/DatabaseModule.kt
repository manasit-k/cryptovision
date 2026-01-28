package com.cryptovision.core.database.di

import android.content.Context
import androidx.room.Room
import com.cryptovision.core.database.CryptoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing database dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCryptoDatabase(
        @ApplicationContext context: Context
    ): CryptoDatabase {
        return Room.databaseBuilder(
            context,
            CryptoDatabase::class.java,
            "crypto_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideCoinDao(database: CryptoDatabase) = database.coinDao()

    @Provides
    fun provideFavoriteDao(database: CryptoDatabase) = database.favoriteDao()
}
