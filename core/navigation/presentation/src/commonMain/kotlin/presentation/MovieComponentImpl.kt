package presentation

import com.arkivanov.decompose.ComponentContext
import com.msoula.hobbymatchmaker.core.navigation.domain.MainComponent

class MovieComponentImpl(
    private val componentContext: ComponentContext,
    private val onNavigateToMovieDetail: (Long) -> Unit,
    private val onLoggedOut: () -> Unit
) : MainComponent {

    override fun onMovieClicked(movieId: Long) {
        onNavigateToMovieDetail(movieId)
    }

    override fun onLogout() {
        onLoggedOut()
    }
}
