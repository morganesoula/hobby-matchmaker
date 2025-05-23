package com.msoula.hobbymatchmaker.core.network.di

import com.msoula.hobbymatchmaker.core.network.Utility
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
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
    single<HttpClient> {
        HttpClient(Darwin) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println("Ktor log: $message")
                    }
                }
                level = LogLevel.ALL
            }

            install(ContentNegotiation) {
                json(get())
            }

            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.themoviedb.org"
                    encodedPath = "3/"
                    parameters.append("api_key", Utility.getPlatformTMDBKey())
                }

                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }
    }
}
