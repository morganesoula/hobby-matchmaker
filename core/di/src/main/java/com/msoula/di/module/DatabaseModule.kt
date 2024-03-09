package com.msoula.di.module

import android.content.Context
import androidx.room.Room
import com.msoula.database.data.HMMDatabase
import com.msoula.database.data.dao.MovieDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context): HMMDatabase =
        Room.databaseBuilder(
            context,
            HMMDatabase::class.java,
            "database-hmm"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun providesMovieDAO(database: HMMDatabase): MovieDAO = database.movieDAO()
}
