package com.msoula.hobbymatchmaker.features.moviedetail.fakes

import com.msoula.hobbymatchmaker.core.common.ErrorMessageProvider
import com.msoula.hobbymatchmaker.core.common.HMMAppError

class FakeMovieDetailErrorMessageProvider : ErrorMessageProvider {
    override suspend fun getMessage(error: HMMAppError): String {
        return ""
    }
}
