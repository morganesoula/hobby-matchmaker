package com.msoula.hobbymatchmaker.core.database.di

import com.msoula.hobbymatchmaker.core.database.DatabaseDriver
import com.msoula.hobbymatchmaker.core.database.HMMDatabase
import com.msoula.hobbymatchmaker.core.database.User_profile
import com.msoula.hobbymatchmaker.core.database.adapters.InterestsAdapter
import org.koin.dsl.module

actual val coreModuleDaoPlatformSpecific = module {
    single<HMMDatabase> {
        HMMDatabase(
            driver = DatabaseDriver().createDriver(),
            user_profileAdapter = User_profile.Adapter(
                interests_jsonAdapter = InterestsAdapter
            )
        )
    }
}
