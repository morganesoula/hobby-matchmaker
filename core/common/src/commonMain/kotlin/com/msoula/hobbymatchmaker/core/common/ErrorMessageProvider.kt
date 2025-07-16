package com.msoula.hobbymatchmaker.core.common

interface ErrorMessageProvider {
    suspend fun getMessage(error: AppError): String
}
