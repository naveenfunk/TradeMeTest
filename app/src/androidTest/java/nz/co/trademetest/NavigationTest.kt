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
import nz.co.trademetest.core.navigation.DiscoverHome
import nz.co.trademetest.core.theme.TradeMeTestTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

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
}
