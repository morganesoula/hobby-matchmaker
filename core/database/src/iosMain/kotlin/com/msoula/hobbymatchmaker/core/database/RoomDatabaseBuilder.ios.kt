package com.msoula.hobbymatchmaker.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSHomeDirectory

fun getDatabaseBuilder(): RoomDatabase.Builder<HMMLocalDatabase> {
    val dbFile = "${NSHomeDirectory()}/hmm_database.db"

    return Room.databaseBuilder<HMMLocalDatabase>(
        name = dbFile
    )
        .setQueryCoroutineContext(Dispatchers.IO)
}

