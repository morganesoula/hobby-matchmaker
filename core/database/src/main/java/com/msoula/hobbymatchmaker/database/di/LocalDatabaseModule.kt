package com.msoula.hobbymatchmaker.database.di

import androidx.room.Room
import com.msoula.hobbymatchmaker.core.database.dao.MovieDAO
import com.msoula.hobbymatchmaker.database.HMMLocalDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val localDatabaseModule = module {
    single<HMMLocalDatabase> {
        Room.databaseBuilder(
            androidContext(),
            HMMLocalDatabase::class.java,
            "local-hmm-database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single<MovieDAO> {
        get<HMMLocalDatabase>().movieDao()
    }
}
