package nz.co.trademetest

import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import nz.co.trademetest.core.navigation.DiscoverDetail
import nz.co.trademetest.core.navigation.DiscoverHome
import nz.co.trademetest.core.theme.TradeMeTestTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.reflect.KClass

/**
 * Pins the three tab back-stack requirements:
 *
 *  1. back never switches tabs;
 *  2. back at a tab root exits the app;
 *  3. each tab keeps an independent back stack.
 *
 * Requirements 1 and 2 are both expressed here as "only one composable destination is live
 * once a tab switch settles" -- that is the precise condition under which NavHost disables
 * its own back handler and lets the event fall through to the Activity, which is what makes
 * the app exit rather than cross tabs. Asserting the stack shape is more robust than trying
 * to assert on process death from inside the process that is dying.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        hiltRule.inject()
        composeTestRule.activity.runOnUiThread {
            composeTestRule.activity.setContent {
                navController = TestNavHostController(composeTestRule.activity).apply {
                    navigatorProvider.addNavigator(ComposeNavigator())
                }
                TradeMeTestTheme {
                    TradeMeTestApp(navController = navController)
                }
            }
        }
        composeTestRule.waitForIdle()
    }

    /**
     * Number of live *composable* destinations. Nested graph entries are excluded because
     * they belong to a different navigator and do not affect whether NavHost's back
     * handler is enabled -- which is the property these tests actually care about.
     */
    private fun liveDestinationCount(): Int =
        navController.currentBackStack.value.count { it.destination !is NavGraph }

    private fun selectTab(label: String) {
        composeTestRule.onNodeWithText(label).performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun switchingTabsDoesNotAccumulateEntries() {
        // Guards the defect where popUpTo silently did nothing on every switch after the
        // first, leaving tabs stacked on top of each other.
        for (tab in listOf("Watchlist", "My Trade Me", "Discover", "Watchlist", "Discover")) {
            selectTab(tab)
            assertEquals(
                "after selecting $tab the stack was:\n" + dumpBackStack(),
                1,
                liveDestinationCount(),
            )
        }
    }

    private fun dumpBackStack(): String =
        navController.currentBackStack.value.joinToString("\n") { entry ->
            val destination = entry.destination
            "  ${if (destination is NavGraph) "GRAPH" else "DEST "}  ${destination.route}"
        }

    @Test
    fun atATabRootNoPreviousTabRemainsOnTheBackStack() {
        // Requirements 1 + 2: with a single live destination NavHost disables its back
        // handler, so back leaves the app instead of returning to the previous tab.
        selectTab("Watchlist")

        assertEquals(1, liveDestinationCount())
        assertTrue(
            "no Discover destination should survive beneath Watchlist",
            navController.currentBackStack.value.none {
                it.destination.hasRoute(DiscoverHome::class)
            },
        )
    }

    @Test
    fun eachTabKeepsItsOwnIndependentBackStack() {
        // Requirement 3: leave Discover on a detail screen, go away, come back to it.
        composeTestRule.runOnUiThread {
            navController.navigate(DiscoverDetail("auction-1"))
        }
        composeTestRule.waitForIdle()
        assertTrue(currentDestinationHasRoute(DiscoverDetail::class))

        selectTab("Watchlist")
        assertEquals(1, liveDestinationCount())

        selectTab("Discover")
        assertTrue(
            "returning to Discover should restore its detail screen",
            currentDestinationHasRoute(DiscoverDetail::class),
        )
    }

    @Test
    fun backFromADetailReturnsToItsOwnTabRoot() {
        composeTestRule.runOnUiThread {
            navController.navigate(DiscoverDetail("auction-1"))
        }
        composeTestRule.waitForIdle()
        assertEquals(2, liveDestinationCount())

        composeTestRule.runOnUiThread { navController.popBackStack() }
        composeTestRule.waitForIdle()

        assertTrue(currentDestinationHasRoute(DiscoverHome::class))
        assertEquals(1, liveDestinationCount())
    }

    @Test
    fun reTappingTheActiveTabPopsItToItsRoot() {
        composeTestRule.runOnUiThread {
            navController.navigate(DiscoverDetail("auction-1"))
        }
        composeTestRule.waitForIdle()

        selectTab("Discover")

        assertTrue(currentDestinationHasRoute(DiscoverHome::class))
        assertEquals(1, liveDestinationCount())
    }

    private fun currentDestinationHasRoute(route: KClass<*>): Boolean =
        navController.currentBackStackEntry?.destination?.hasRoute(route) == true
}
