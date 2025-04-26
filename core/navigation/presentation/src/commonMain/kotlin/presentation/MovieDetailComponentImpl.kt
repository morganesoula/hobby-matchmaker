package presentation

import com.arkivanov.decompose.ComponentContext
import com.msoula.hobbymatchmaker.core.navigation.domain.MovieDetailComponent

class MovieDetailComponentImpl(
    componentContext: ComponentContext,
    override val movieId: Long
) : MovieDetailComponent, ComponentContext by componentContext

