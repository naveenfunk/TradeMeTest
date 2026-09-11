package nz.co.trademetest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import dagger.hilt.android.AndroidEntryPoint
import nz.co.trademetest.core.theme.TradeMeTestTheme
import nz.co.trademetest.feature.discover.DiscoverScreen
import nz.co.trademetest.feature.profile.ProfileScreen
import nz.co.trademetest.feature.watchlist.WatchlistScreen

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
fun TradeMeTestApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.DISCOVER) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            for (destination in AppDestinations.entries) {
                item(
                    icon = {
                        Icon(
                            painterResource(destination.icon),
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(destination.label)) },
                    selected = destination == currentDestination,
                    onClick = { currentDestination = destination }
                )
            }
        }
    ) {
        when (currentDestination) {
            AppDestinations.DISCOVER -> DiscoverScreen()
            AppDestinations.WATCHLIST -> WatchlistScreen()
            AppDestinations.PROFILE -> ProfileScreen()
        }
    }
}

enum class AppDestinations(
    @field:StringRes val label: Int,
    @field:DrawableRes val icon: Int,
) {
    DISCOVER(R.string.tab_discover, R.drawable.ic_search),
    WATCHLIST(R.string.tab_watchlist, R.drawable.ic_binoculars),
    PROFILE(R.string.tab_profile, R.drawable.ic_profile),
}

@PreviewScreenSizes
@Preview(showBackground = true)
@Composable
fun TradeMeTestAppPreview() {
    TradeMeTestTheme {
        TradeMeTestApp()
    }
}
