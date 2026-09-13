package nz.co.trademetest.feature.discover.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import nz.co.trademetest.core.navigation.DiscoverGraphRoute
import nz.co.trademetest.core.navigation.DiscoverHome
import nz.co.trademetest.feature.discover.ui.home.DiscoverScreen

fun NavGraphBuilder.discoverGraph(
    contentPadding: PaddingValues,
) {
    navigation<DiscoverGraphRoute>(startDestination = DiscoverHome) {
        composable<DiscoverHome> {
            DiscoverScreen(
                contentPadding = contentPadding,
            )
        }
    }
}
