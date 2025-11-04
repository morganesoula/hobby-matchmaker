package com.msoula.hobbymatchmaker.core.database.di

import app.cash.sqldelight.db.SqlDriver
import com.msoula.hobbymatchmaker.core.database.DriverFactory
import com.msoula.hobbymatchmaker.core.database.HMMDatabase
import com.msoula.hobbymatchmaker.core.database.User_profile
import com.msoula.hobbymatchmaker.core.database.adapters.InterestsAdapter
import com.msoula.hobbymatchmaker.core.database.services.MovieDAO
import com.msoula.hobbymatchmaker.core.database.services.MovieDAOImpl
import com.msoula.hobbymatchmaker.core.database.services.SocialMemberDAO
import com.msoula.hobbymatchmaker.core.database.services.SocialMemberDAOImpl
import com.msoula.hobbymatchmaker.core.database.services.UserProfileDAO
import com.msoula.hobbymatchmaker.core.database.services.UserProfileDAOImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModuleDAO = module {
    includes(coreModuleDatabasePlatformSpecific)
    single<SqlDriver> { get<DriverFactory>().createDRiver() }
    single<HMMDatabase> {
        val driver = get<SqlDriver>()
        driver.execute(null, "PRAGMA foreign_keys=ON", 0)

        HMMDatabase(
            driver = driver,
            user_profileAdapter = User_profile.Adapter(
                interests_jsonAdapter = InterestsAdapter
            )
        )
    }

    singleOf(::MovieDAOImpl) bind MovieDAO::class
    singleOf(::UserProfileDAOImpl) bind UserProfileDAO::class
    singleOf(::SocialMemberDAOImpl) bind SocialMemberDAO::class
}

expect val coreModuleDatabasePlatformSpecific: Module
