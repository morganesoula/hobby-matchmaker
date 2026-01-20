package com.msoula.hobbymatchmaker.core.network.di

import com.msoula.hobbymatchmaker.core.network.AndroidNetworkConnectivityChecker
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.core.network.BuildKonfig.APP_SECRET
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import org.koin.dsl.module

actual val coreModuleNetworkPlatformSpecific = module {
    single<NetworkConnectivityChecker> { AndroidNetworkConnectivityChecker(get()) }

    single<HttpClient>(createdAtStart = true) {
        HttpClient(CIO) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println("Ktor log: $message")
                    }
                }
                //TODO Switch to NONE once you want to release
                level = LogLevel.ALL
            }

            install(ContentNegotiation) {
                json(get())
            }

            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "hmm-api.morganes56.workers.dev"
                    encodedPath = "/"
                }

                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header("Authorization", "Bearer $APP_SECRET")
            }
        }
    }
}
