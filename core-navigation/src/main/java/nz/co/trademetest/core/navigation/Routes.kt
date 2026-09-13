package nz.co.trademetest.core.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.serialization.Serializable

@Serializable
sealed interface TradeMeRoute

/**
 * A top-level destination shown as a tab.
 */
@Serializable
sealed interface TopLevelRoute : TradeMeRoute {
    @get:StringRes
    val labelRes: Int

    @get:DrawableRes
    val iconRes: Int
}

@Serializable
data object DiscoverHome : TopLevelRoute {
    override val labelRes: Int get() = R.string.nav_tab_discover
    override val iconRes: Int get() = R.drawable.nav_ic_search
}

@Serializable
data object WatchlistHome : TopLevelRoute {
    override val labelRes: Int get() = R.string.nav_tab_watchlist
    override val iconRes: Int get() = R.drawable.nav_ic_binoculars
}

@Serializable
data object ProfileHome : TopLevelRoute {
    override val labelRes: Int get() = R.string.nav_tab_profile
    override val iconRes: Int get() = R.drawable.nav_ic_profile
}

@Serializable
data object DiscoverGraphRoute : TradeMeRoute

@Serializable
data object WatchlistGraphRoute : TradeMeRoute

@Serializable
data object ProfileGraphRoute : TradeMeRoute

val TopLevelRoute.graphRoute: TradeMeRoute
    get() = when (this) {
        DiscoverHome -> DiscoverGraphRoute
        WatchlistHome -> WatchlistGraphRoute
        ProfileHome -> ProfileGraphRoute
    }

val TOP_LEVEL_ROUTES: List<TopLevelRoute> = listOf(DiscoverHome, WatchlistHome, ProfileHome)
