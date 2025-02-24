package com.msoula.hobbymatchmaker.core.database.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.msoula.hobbymatchmaker.core.database.HMMLocalDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

val localIosDatabaseModule = module {
    single<HMMLocalDatabase> { configureRoomBuilder().build() }
}

private fun configureRoomBuilder(): RoomDatabase.Builder<HMMLocalDatabase> {
    val dbFilePath = documentDirectory() + "/hmm_database.db"

    return Room.databaseBuilder<HMMLocalDatabase>(
        name = dbFilePath
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    return NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )?.path ?: error("Unable to find document directory")
}
