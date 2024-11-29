package feature.movies.fakes

import com.msoula.hobbymatchmaker.core.common.FlowUseCase
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesErrors
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestDispatcher

class FakeObserveAllMoviesUseCase(dispatcher: TestDispatcher) :
    FlowUseCase<Parameters.StringParam, ObserveAllMoviesSuccess, ObserveAllMoviesErrors>(dispatcher) {

    private val _flow = MutableStateFlow<Result<ObserveAllMoviesSuccess, ObserveAllMoviesErrors>>(
        Result.Success(ObserveAllMoviesSuccess.Loading)
    )

    override fun execute(parameters: Parameters.StringParam): Flow<Result<ObserveAllMoviesSuccess, ObserveAllMoviesErrors>> {
        return _flow
    }

    fun emitSuccess(success: ObserveAllMoviesSuccess) {
        _flow.value = Result.Success(success)
    }

    fun emitFailure(failure: ObserveAllMoviesErrors) {
        _flow.value = Result.Failure(failure)
    }
}
