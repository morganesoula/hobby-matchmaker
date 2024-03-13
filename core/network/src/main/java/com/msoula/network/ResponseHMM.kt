package com.msoula.network

sealed class ResponseHMM<out T> {
    data class Success<out R>(val data: R) : ResponseHMM<R>()
    data class Failure(val throwable: Throwable) : ResponseHMM<Nothing>()
}
