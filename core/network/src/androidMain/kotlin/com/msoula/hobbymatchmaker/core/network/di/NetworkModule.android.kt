package com.msoula.hobbymatchmaker.core.network.di

import com.msoula.hobbymatchmaker.core.network.AndroidNetworkConnectivityChecker
import com.msoula.hobbymatchmaker.core.network.BuildKonfig.APP_SECRET
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
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
import kotlinx.io.IOException
import org.koin.dsl.module

actual val coreModuleNetworkPlatformSpecific = module {
    single<NetworkConnectivityChecker> { AndroidNetworkConnectivityChecker(get()) }

    single<HttpClient>(createdAtStart = true) {
        HttpClient(CIO) {
            val connectivityChecker = get<NetworkConnectivityChecker>()

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

            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
                connectTimeoutMillis = 10_000
                socketTimeoutMillis = 10_000
            }

            install(HttpRequestRetry) {
                maxRetries = 3
                retryOnServerErrors(maxRetries = 3)
                retryOnException(maxRetries = 3, retryOnTimeout = true)
                exponentialDelay()
            }

            install(createClientPlugin("ConnectivityGuard") {
                onRequest { _, _ ->
                    if (!connectivityChecker.hasActiveConnection()) {
                        throw IOException("No network connection")
                    }
                }
            })

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
