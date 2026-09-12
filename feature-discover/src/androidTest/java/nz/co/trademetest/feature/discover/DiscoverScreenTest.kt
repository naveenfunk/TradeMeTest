package nz.co.trademetest.feature.discover

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import nz.co.trademetest.core.theme.TradeMeTestTheme
import nz.co.trademetest.feature.discover.ui.home.DiscoverIntent
import nz.co.trademetest.feature.discover.ui.home.DiscoverItemUi
import nz.co.trademetest.feature.discover.ui.home.DiscoverScreen
import nz.co.trademetest.feature.discover.ui.home.DiscoverUiState
import org.junit.Rule
import org.junit.Test

class DiscoverScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun browseTitleIsDisplayed() {
        composeTestRule.setContent {
            DiscoverScreen(state = DiscoverUiState(), onIntent = {})
        }

        composeTestRule.onNodeWithText("Browse").assertExists()
    }

    @Test
    fun cartAndSearchIconsAreDisplayed() {
        composeTestRule.setContent {
            DiscoverScreen(state = DiscoverUiState(), onIntent = {})
        }

        composeTestRule.onNodeWithContentDescription("Cart").assertExists()
        composeTestRule.onNodeWithContentDescription("Search").assertExists()
    }

    @Test
    fun clickingCartDispatchesCartClickedIntent() {
        val dispatchedIntents = mutableListOf<DiscoverIntent>()
        composeTestRule.setContent {
            DiscoverScreen(state = DiscoverUiState(), onIntent = { dispatchedIntents += it })
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
            DiscoverScreen(state = DiscoverUiState(), onIntent = { dispatchedIntents += it })
        }

        composeTestRule.onNodeWithContentDescription("Search").performClick()

        assert(dispatchedIntents == listOf(DiscoverIntent.SearchClicked)) {
            "Expected [SearchClicked] but got $dispatchedIntents"
        }
    }

    @Test
    fun itemTitlesAreDisplayed() {
        composeTestRule.setContent {
            TradeMeTestTheme {
                DiscoverScreen(
                    state = DiscoverUiState(items = listOf(ClassifiedItem, AuctionItem)),
                    onIntent = {})
            }
        }

        composeTestRule.onNodeWithText(ClassifiedItem.title).assertExists()
        composeTestRule.onNodeWithText(AuctionItem.title).assertExists()
    }

    @Test
    fun classifiedItemShowsOnlyOnePrice() {
        composeTestRule.setContent {
            TradeMeTestTheme {
                DiscoverScreen(
                    state = DiscoverUiState(items = listOf(ClassifiedItem)),
                    onIntent = {})
            }
        }

        composeTestRule.onNodeWithText(ClassifiedItem.priceDisplay).assertExists()
        composeTestRule.onNodeWithText("Buy Now $899").assertDoesNotExist()
    }

    @Test
    fun auctionItemShowsCurrentBidAndBuyNow() {
        composeTestRule.setContent {
            TradeMeTestTheme {
                DiscoverScreen(state = DiscoverUiState(items = listOf(AuctionItem)), onIntent = {})
            }
        }

        composeTestRule.onNodeWithText(AuctionItem.priceDisplay).assertExists()
        composeTestRule.onNodeWithText("Buy Now ${AuctionItem.buyNowPrice}").assertExists()
    }

    @Test
    fun tappingAnItemDispatchesItemClickedIntent() {
        val dispatchedIntents = mutableListOf<DiscoverIntent>()
        composeTestRule.setContent {
            TradeMeTestTheme {
                DiscoverScreen(
                    state = DiscoverUiState(items = listOf(AuctionItem)),
                    onIntent = { dispatchedIntents += it },
                )
            }
        }

        composeTestRule.onNodeWithText(AuctionItem.title).performClick()

        assert(dispatchedIntents == listOf(DiscoverIntent.ItemClicked(AuctionItem.id))) {
            "Expected [ItemClicked(${AuctionItem.id})] but got $dispatchedIntents"
        }
    }

    private companion object {
        val ClassifiedItem = DiscoverItemUi(
            id = "classified-1",
            imageUrl = "https://example.com/classified.jpg",
            location = "Auckland City",
            title = "Espresso machine, barely used",
            priceDisplay = "$250",
            buyNowPrice = null,
            isClassified = true,
        )
        val AuctionItem = DiscoverItemUi(
            id = "auction-1",
            imageUrl = "https://example.com/auction.jpg",
            location = "Wellington Central",
            title = "Mountain bike, 29er, hydraulic brakes",
            priceDisplay = "$450",
            buyNowPrice = "$899",
            isClassified = false,
        )
    }
}
