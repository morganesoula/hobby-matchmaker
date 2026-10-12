package com.msoula.hobbymatchmaker.presentation

import coil3.PlatformContext
import com.msoula.hobbymatchmaker.presentation.navigation.appModule
import io.kotzilla.generated.monitoring
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun initKoin() {
    startKoin {
        modules(
            module {
                single<PlatformContext> { PlatformContext.INSTANCE }
            },
            *appModule().toTypedArray()
        )
        monitoring()
    }
}
