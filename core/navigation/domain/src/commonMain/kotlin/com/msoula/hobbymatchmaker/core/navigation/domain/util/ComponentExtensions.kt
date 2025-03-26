package com.msoula.hobbymatchmaker.core.navigation.domain.util

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

fun ComponentContext.coroutineScope(): CoroutineScope {
    return instanceKeeper.getOrCreate {
        object : InstanceKeeper.Instance {
            val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

            override fun onDestroy() {
                scope.cancel()
            }
        }
    }.scope
}
