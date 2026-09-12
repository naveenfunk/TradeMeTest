package nz.co.trademetest.feature.watchlist.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import nz.co.trademetest.core.navigation.WatchlistGraphRoute
import nz.co.trademetest.core.navigation.WatchlistHome
import nz.co.trademetest.feature.watchlist.WatchlistScreen

fun NavGraphBuilder.watchlistGraph(
    contentPadding: PaddingValues,
) {
    navigation<WatchlistGraphRoute>(startDestination = WatchlistHome) {
        composable<WatchlistHome> {
            WatchlistScreen(contentPadding = contentPadding)
        }
    }
}
