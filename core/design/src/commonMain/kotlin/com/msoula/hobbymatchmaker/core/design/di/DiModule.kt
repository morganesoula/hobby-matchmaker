package com.msoula.hobbymatchmaker.core.design.di

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.msoula.hobbymatchmaker.core.design.util.DefaultErrorMessageMapper
import com.msoula.hobbymatchmaker.core.design.util.ErrorMessageMapper
import io.ktor.client.HttpClient
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModuleDi = module {
    single(createdAtStart = true) { DefaultErrorMessageMapper } bind ErrorMessageMapper::class

    single {
        val context = get<PlatformContext>()
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory(get<HttpClient>()))
            }
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .maxSizePercent(0.02)
                    .build()
            }
            .build()
    }
}
