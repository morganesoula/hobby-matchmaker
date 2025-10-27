package com.msoula.hobbymatchmaker.core.database.di

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
    includes(coreModuleDaoPlatformSpecific)
    singleOf(::MovieDAOImpl) bind MovieDAO::class
    singleOf(::UserProfileDAOImpl) bind UserProfileDAO::class
    singleOf(::SocialMemberDAOImpl) bind SocialMemberDAO::class
}

expect val coreModuleDaoPlatformSpecific: Module
