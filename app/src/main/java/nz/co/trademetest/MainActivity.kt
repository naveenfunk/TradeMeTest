package nz.co.trademetest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import nz.co.trademetest.core.navigation.DiscoverGraphRoute
import nz.co.trademetest.core.navigation.DiscoverHome
import nz.co.trademetest.core.navigation.TOP_LEVEL_ROUTES
import nz.co.trademetest.core.navigation.TopLevelRoute
import nz.co.trademetest.core.navigation.graphRoute
import nz.co.trademetest.core.theme.TradeMeTestTheme
import nz.co.trademetest.feature.discover.navigation.discoverGraph
import nz.co.trademetest.feature.profile.navigation.profileGraph
import nz.co.trademetest.feature.watchlist.navigation.watchlistGraph

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TradeMeTestTheme {
                TradeMeTestApp()
            }
        }
    }
}

@Composable
fun TradeMeTestApp(
    navController: NavHostController = rememberNavController(),
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    val currentTopLevelRoute = TOP_LEVEL_ROUTES.firstOrNull { topLevel ->
        currentBackStackEntry?.destination?.hierarchy?.any {
            it.hasRoute(topLevel.graphRoute::class)
        } == true
    } ?: DiscoverHome

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            for (destination in TOP_LEVEL_ROUTES) {
                item(
                    icon = {
                        Icon(
                            painterResource(destination.iconRes),
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(destination.labelRes)) },
                    selected = destination == currentTopLevelRoute,
                    onClick = {
                        if (destination == currentTopLevelRoute) {
                            // Re-tapping the active tab clears its inner stack back to the tab's home screen.
                            navController.popBackStack(route = destination, inclusive = false)
                        } else {
                            navController.navigateToTopLevel(
                                route = destination,
                                popUpToGraphOf = currentTopLevelRoute,
                            )
                        }
                    }
                )
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = DiscoverGraphRoute,
        ) {
            discoverGraph(navController = navController, contentPadding = PaddingValues())
            watchlistGraph(contentPadding = PaddingValues())
            profileGraph(contentPadding = PaddingValues())
        }
    }
}

private fun NavHostController.navigateToTopLevel(
    route: TopLevelRoute,
    popUpToGraphOf: TopLevelRoute,
) {
    navigate(route.graphRoute) {
        popUpTo(popUpToGraphOf.graphRoute) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@PreviewScreenSizes
@Preview(showBackground = true)
@Composable
fun TradeMeTestAppPreview() {
    TradeMeTestTheme {
        TradeMeTestApp()
    }
}
