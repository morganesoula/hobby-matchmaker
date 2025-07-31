package com.msoula.hobbymatchmaker.features.moviedetail.fakes

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.ErrorMessageProvider

class FakeMovieDetailErrorMessageProvider : ErrorMessageProvider {
    override suspend fun getMessage(error: AppError): String {
        return ""
    }
}
