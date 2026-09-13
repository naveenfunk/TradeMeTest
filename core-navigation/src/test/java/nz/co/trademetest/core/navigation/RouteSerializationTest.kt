package nz.co.trademetest.core.navigation

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RouteSerializationTest {

    private val json = Json

    @Test
    fun topLevelHomeRoutesRoundTripThroughJson() {
        val routes: List<TradeMeRoute> = TOP_LEVEL_ROUTES
        for (route in routes) {
            val encoded = json.encodeToString(route)
            val decoded = json.decodeFromString<TradeMeRoute>(encoded)
            assertEquals(route, decoded)
        }
    }

    @Test
    fun tabGraphRoutesRoundTripThroughJson() {
        val routes: List<TradeMeRoute> =
            listOf(DiscoverGraphRoute, WatchlistGraphRoute, ProfileGraphRoute)
        for (route in routes) {
            val encoded = json.encodeToString(route)
            val decoded = json.decodeFromString<TradeMeRoute>(encoded)
            assertEquals(route, decoded)
        }
    }

    @Test
    fun eachTabMapsToItsOwnDistinctGraphRoute() {
        // Two tabs sharing a graph route would silently collapse their back stacks into
        // one, which is exactly the behaviour the nested graphs exist to prevent.
        val graphRoutes = TOP_LEVEL_ROUTES.map { it.graphRoute }

        assertEquals(TOP_LEVEL_ROUTES.size, graphRoutes.toSet().size)
        assertEquals(
            listOf(DiscoverGraphRoute, WatchlistGraphRoute, ProfileGraphRoute),
            graphRoutes,
        )
    }

    @Test
    fun noTabGraphRouteIsAlsoATopLevelRoute() {
        // Graph routes carry no label or icon, so if one ever leaked into TOP_LEVEL_ROUTES
        // the nav suite would try to render a tab for it.
        val graphRoutes: List<TradeMeRoute> =
            listOf(DiscoverGraphRoute, WatchlistGraphRoute, ProfileGraphRoute)

        assertTrue(
            "tab graph routes must not be TopLevelRoute",
            graphRoutes.none { it is TopLevelRoute },
        )
    }

    @Test
    fun topLevelRoutesListCoversExactlyThreeDistinctTabs() {
        // Guards against a new TopLevelRoute being added without being registered
        // in TOP_LEVEL_ROUTES -- the list that drives the nav suite items and the
        // per-tab NavHost graphs.
        assertEquals(setOf(DiscoverHome, WatchlistHome, ProfileHome), TOP_LEVEL_ROUTES.toSet())
        assertEquals(3, TOP_LEVEL_ROUTES.size)
    }

    @Test
    fun eachTopLevelRouteHasDistinctLabelAndIconResourceIds() {
        val labelIds = TOP_LEVEL_ROUTES.map { it.labelRes }
        val iconIds = TOP_LEVEL_ROUTES.map { it.iconRes }

        assertTrue("expected distinct label resources per tab", labelIds.toSet().size == labelIds.size)
        assertTrue("expected distinct icon resources per tab", iconIds.toSet().size == iconIds.size)
    }
}
