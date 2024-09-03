package com.msoula.hobbymatchmaker.core.database.di

import androidx.room.Room
import com.msoula.hobbymatchmaker.core.database.HMMDatabase
import com.msoula.hobbymatchmaker.core.database.dao.MovieDAO
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single<HMMDatabase> {
        Room.databaseBuilder(
            androidContext(),
            HMMDatabase::class.java,
            "database-hmm"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single<MovieDAO> {
        get<HMMDatabase>().movieDAO()
    }
}
