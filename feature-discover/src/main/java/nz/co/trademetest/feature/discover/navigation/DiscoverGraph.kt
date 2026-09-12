package nz.co.trademetest.feature.discover.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import nz.co.trademetest.core.navigation.DiscoverDetail
import nz.co.trademetest.core.navigation.DiscoverGraphRoute
import nz.co.trademetest.core.navigation.DiscoverHome
import nz.co.trademetest.feature.discover.ui.detail.DiscoverDetailScreen
import nz.co.trademetest.feature.discover.ui.home.DiscoverScreen

fun NavGraphBuilder.discoverGraph(
    navController: NavController,
    contentPadding: PaddingValues,
) {
    navigation<DiscoverGraphRoute>(startDestination = DiscoverHome) {
        composable<DiscoverHome> {
            DiscoverScreen(
                onNavigateToDetail = { id -> navController.navigate(DiscoverDetail(id)) },
                contentPadding = contentPadding,
            )
        }
        composable<DiscoverDetail> { backStackEntry ->
            val route: DiscoverDetail = backStackEntry.toRoute()
            DiscoverDetailScreen(
                id = route.id,
                contentPadding = contentPadding,
            )
        }
    }
}
