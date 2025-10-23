package presentation

import androidx.navigation.NavHostController

fun NavHostController.navigateAndReplaceAll(route: Route) {
    navigate(route) {
        popUpTo(graph.startDestinationId) { inclusive = true }
        launchSingleTop = true
    }
}
