package com.msoula.hobbymatchmaker.presentation.navigation

import com.msoula.hobbymatchmaker.core.authentication.data.di.coreModuleAuthenticationData
import com.msoula.hobbymatchmaker.core.authentication.domain.di.coreModuleAuthenticationDomain
import com.msoula.hobbymatchmaker.core.common.di.coreModuleCommon
import com.msoula.hobbymatchmaker.core.database.di.coreModuleDAO
import com.msoula.hobbymatchmaker.core.di.di.coreModuleDI
import com.msoula.hobbymatchmaker.core.login.domain.useCases.di.coreModuleLoginFormValidation
import com.msoula.hobbymatchmaker.core.login.presentation.di.coreModuleSignInViewModel
import com.msoula.hobbymatchmaker.core.login.presentation.di.coreModuleSignUpViewModel
import com.msoula.hobbymatchmaker.core.network.di.coreModuleNetwork
import com.msoula.hobbymatchmaker.core.session.data.di.coreModuleSessionData
import com.msoula.hobbymatchmaker.core.session.domain.di.coreModuleSessionDomain
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.di.featuresModuleMovieDetailData
import com.msoula.hobbymatchmaker.features.moviedetail.domain.di.featuresModuleMovieDetailDomain
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.di.featuresModuleMovieDetailViewModel
import com.msoula.hobbymatchmaker.features.movies.data.di.featuresModuleMovieData
import com.msoula.hobbymatchmaker.features.movies.domain.di.featuresModuleMovieDomain
import com.msoula.hobbymatchmaker.features.movies.presentation.di.featuresModuleMovieViewModel

fun appModule() = listOf(
    coreModuleNetwork,
    coreModuleSessionData,
    coreModuleSessionDomain,
    coreModuleAuthenticationData,
    coreModuleAuthenticationDomain,
    coreModuleCommon,
    coreModuleDAO,
    coreModuleDI,
    coreModuleLoginFormValidation,
    coreModuleSignInViewModel,
    coreModuleSignUpViewModel,
    featuresModuleMovieDetailData,
    featuresModuleMovieDetailDomain,
    featuresModuleMovieDetailViewModel,
    featuresModuleMovieData,
    featuresModuleMovieDomain,
    featuresModuleMovieViewModel
)
