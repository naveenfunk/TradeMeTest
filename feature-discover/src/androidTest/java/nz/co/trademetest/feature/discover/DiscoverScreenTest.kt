package nz.co.trademetest.feature.discover

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class DiscoverScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun browseTitleIsDisplayed() {
        composeTestRule.setContent {
            DiscoverScreen(state = DiscoverUiState, onIntent = {})
        }

        composeTestRule.onNodeWithText("Browse").assertExists()
    }

    @Test
    fun cartAndSearchIconsAreDisplayed() {
        composeTestRule.setContent {
            DiscoverScreen(state = DiscoverUiState, onIntent = {})
        }

        composeTestRule.onNodeWithContentDescription("Cart").assertExists()
        composeTestRule.onNodeWithContentDescription("Search").assertExists()
    }

    @Test
    fun clickingCartDispatchesCartClickedIntent() {
        val dispatchedIntents = mutableListOf<DiscoverIntent>()
        composeTestRule.setContent {
            DiscoverScreen(state = DiscoverUiState, onIntent = { dispatchedIntents += it })
        }

        composeTestRule.onNodeWithContentDescription("Cart").performClick()

        assert(dispatchedIntents == listOf(DiscoverIntent.CartClicked)) {
            "Expected [CartClicked] but got $dispatchedIntents"
        }
    }

    @Test
    fun clickingSearchDispatchesSearchClickedIntent() {
        val dispatchedIntents = mutableListOf<DiscoverIntent>()
        composeTestRule.setContent {
            DiscoverScreen(state = DiscoverUiState, onIntent = { dispatchedIntents += it })
        }

        composeTestRule.onNodeWithContentDescription("Search").performClick()

        assert(dispatchedIntents == listOf(DiscoverIntent.SearchClicked)) {
            "Expected [SearchClicked] but got $dispatchedIntents"
        }
    }
}
