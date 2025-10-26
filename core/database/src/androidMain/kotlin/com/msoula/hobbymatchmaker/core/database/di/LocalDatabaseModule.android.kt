package com.msoula.hobbymatchmaker.core.database.di

import com.msoula.hobbymatchmaker.core.database.DatabaseDriver
import com.msoula.hobbymatchmaker.core.database.HMMDatabase
import org.koin.dsl.module
import com.msoula.hobbymatchmaker.core.database.User_profile
import com.msoula.hobbymatchmaker.core.database.adapters.InterestsAdapter

actual val coreModuleDaoPlatformSpecific = module {
    single<HMMDatabase> {
        HMMDatabase(
            driver = DatabaseDriver(get()).createDriver(),
            user_profileAdapter = User_profile.Adapter(
                interests_jsonAdapter = InterestsAdapter
            )
        )
    }
}
