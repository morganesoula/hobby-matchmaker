package presentation

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.msoula.hobbymatchmaker.core.navigation.domain.SplashRootComponent
import com.msoula.hobbymatchmaker.core.navigation.domain.util.coroutineScope
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveIsConnectedUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashRootComponentImpl(
    componentContext: ComponentContext,
    private val observeIsConnectedUseCase: ObserveIsConnectedUseCase,
    private val onFinished: (Boolean) -> Unit
) : SplashRootComponent, ComponentContext by componentContext {

    override val dummyStack: Value<ChildStack<*, *>> =
        MutableValue(
            ChildStack(
                active = Child.Created(
                    configuration = Unit,
                    instance = Unit
                ),
                backStack = emptyList()
            )
        )

    init {
        componentContext.coroutineScope().launch {
            delay(1500)
            val isConnected = observeIsConnectedUseCase().first()
            onFinished(isConnected)
        }
    }
}
