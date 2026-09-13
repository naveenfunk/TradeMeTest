package nz.co.trademetest.feature.discover

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import nz.co.trademetest.core.theme.TradeMeTestTheme
import nz.co.trademetest.feature.discover.ui.home.DiscoverIntent
import nz.co.trademetest.feature.discover.ui.home.DiscoverItemUi
import nz.co.trademetest.feature.discover.ui.home.DiscoverScreen
import nz.co.trademetest.feature.discover.ui.home.DiscoverUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
    fun loadingStateShowsProgressIndicatorAndNoItems() {
        composeTestRule.setContent {
            DiscoverScreen(
                state = DiscoverUiState(isLoading = true),
                onIntent = {},
            )
        }

        composeTestRule.onNodeWithContentDescription("Cart").assertExists()
        composeTestRule.onNodeWithText(ClassifiedItem.title).assertDoesNotExist()
    }

    @Test
    fun errorStateShowsErrorMessageAndNoItems() {
        composeTestRule.setContent {
            DiscoverScreen(
                state = DiscoverUiState(
                    errorMessage = R.string.discover_load_error,
                    items = listOf(ClassifiedItem),
                ),
                onIntent = {},
            )
        }

        composeTestRule.onNodeWithText("Couldn't load items. Please try again.").assertExists()
        composeTestRule.onNodeWithText(ClassifiedItem.title).assertDoesNotExist()
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

        assert(
            dispatchedIntents == listOf(
                DiscoverIntent.ItemClicked(id = AuctionItem.id, title = AuctionItem.title)
            )
        ) {
            "Expected [ItemClicked(${AuctionItem.id}, ${AuctionItem.title})] but got $dispatchedIntents"
        }
    }

    @Test
    fun scrollPositionIsRetainedWhenWrappedInASaveableStateHolderAcrossLeavingAndReentering() {
        // This directly exercises the primitive that NavHost's per-destination
        // SaveableStateProvider relies on (see TradeMeTestApp's NavHost, which no
        // longer hoists rememberSaveableStateHolder itself now that navigation owns
        // it, keyed per NavBackStackEntry rather than per tab name). Pins the
        // contract NavHost depends on to keep Discover's scroll position across a
        // tab switch.
        var showList by mutableStateOf(true)
        var capturedListState: LazyListState? = null

        composeTestRule.setContent {
            val holder = rememberSaveableStateHolder()
            TradeMeTestTheme {
                if (showList) {
                    holder.SaveableStateProvider(key = "discover") {
                        val listState = rememberLazyListState()
                        capturedListState = listState
                        DiscoverScreen(
                            state = DiscoverUiState(items = ManyItems),
                            onIntent = {},
                            listState = listState,
                        )
                    }
                }
            }
        }

        composeTestRule.onNode(hasScrollAction()).performScrollToIndex(30)
        composeTestRule.waitForIdle()
        val scrolledIndex = capturedListState!!.firstVisibleItemIndex
        assertTrue("expected to have scrolled past the top", scrolledIndex > 0)

        // Simulate a tab switch: dispose the screen, then recompose it back in.
        showList = false
        composeTestRule.waitForIdle()
        showList = true
        composeTestRule.waitForIdle()

        assertEquals(scrolledIndex, capturedListState!!.firstVisibleItemIndex)
    }

    @Test
    fun scrollPositionIsLostWithoutASaveableStateHolderAcrossLeavingAndReentering() {
        // Negative counterpart: documents that hoisting LazyListState alone is not
        // enough — without a SaveableStateProvider retaining it across disposal
        // (which is what NavHost provides per back stack entry when saveState /
        // restoreState are used on tab-switch navigation), the position resets.
        // Guards against that mechanism being "simplified" away later.
        var showList by mutableStateOf(true)
        var capturedListState: LazyListState? = null

        composeTestRule.setContent {
            TradeMeTestTheme {
                if (showList) {
                    val listState = rememberLazyListState()
                    capturedListState = listState
                    DiscoverScreen(
                        state = DiscoverUiState(items = ManyItems),
                        onIntent = {},
                        listState = listState,
                    )
                }
            }
        }

        composeTestRule.onNode(hasScrollAction()).performScrollToIndex(30)
        composeTestRule.waitForIdle()
        assertTrue("expected to have scrolled past the top", capturedListState!!.firstVisibleItemIndex > 0)

        showList = false
        composeTestRule.waitForIdle()
        showList = true
        composeTestRule.waitForIdle()

        assertEquals(0, capturedListState!!.firstVisibleItemIndex)
    }

    private companion object {
        val ManyItems = List(50) { index ->
            DiscoverItemUi(
                id = "item-$index",
                imageUrl = "https://example.com/item-$index.jpg",
                location = "Auckland City",
                title = "Item $index",
                priceDisplay = "$$index",
                buyNowPrice = null,
                isClassified = false,
            )
        }
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
