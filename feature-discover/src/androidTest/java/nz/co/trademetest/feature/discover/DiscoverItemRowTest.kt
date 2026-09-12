package nz.co.trademetest.feature.discover

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import nz.co.trademetest.core.theme.TradeMeTestTheme
import nz.co.trademetest.feature.discover.ui.home.DiscoverItemRow
import nz.co.trademetest.feature.discover.ui.home.DiscoverItemUi
import org.junit.Rule
import org.junit.Test

class DiscoverItemRowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun titleAndLocationAreDisplayed() {
        composeTestRule.setContent {
            TradeMeTestTheme {
                DiscoverItemRow(item = ClassifiedItem, onClick = {})
            }
        }

        composeTestRule.onNodeWithText(ClassifiedItem.title).assertExists()
        composeTestRule.onNodeWithText(ClassifiedItem.location).assertExists()
    }

    @Test
    fun classifiedItemShowsOnlyOnePriceAndNoBuyNow() {
        composeTestRule.setContent {
            TradeMeTestTheme {
                DiscoverItemRow(item = ClassifiedItem, onClick = {})
            }
        }

        composeTestRule.onNodeWithText(ClassifiedItem.priceDisplay).assertExists()
        composeTestRule.onNodeWithText("Buy Now").assertDoesNotExist()
    }

    @Test
    fun auctionItemWithBuyNowShowsBothPrices() {
        composeTestRule.setContent {
            TradeMeTestTheme {
                DiscoverItemRow(item = AuctionItemWithBuyNow, onClick = {})
            }
        }

        composeTestRule.onNodeWithText(AuctionItemWithBuyNow.priceDisplay).assertExists()
        composeTestRule.onNodeWithText("Buy Now").assertExists()
    }

    @Test
    fun auctionItemWithoutBuyNowShowsOnlyCurrentBid() {
        composeTestRule.setContent {
            TradeMeTestTheme {
                DiscoverItemRow(item = AuctionItemWithoutBuyNow, onClick = {})
            }
        }

        composeTestRule.onNodeWithText(AuctionItemWithoutBuyNow.priceDisplay).assertExists()
        composeTestRule.onNodeWithText("Buy Now").assertDoesNotExist()
    }

    @Test
    fun tappingTheRowInvokesOnClick() {
        var clicked = false
        composeTestRule.setContent {
            TradeMeTestTheme {
                DiscoverItemRow(item = ClassifiedItem, onClick = { clicked = true })
            }
        }

        composeTestRule.onNodeWithText(ClassifiedItem.title).performClick()

        assert(clicked) { "Expected onClick to be invoked" }
    }

    private companion object {
        val ClassifiedItem = DiscoverItemUi(
            id = "1",
            imageUrl = "https://example.com/image.jpg",
            location = "Auckland City",
            title = "Espresso machine, barely used",
            priceDisplay = "$250",
            buyNowPrice = null,
            isClassified = true,
        )
        val AuctionItemWithBuyNow = DiscoverItemUi(
            id = "2",
            imageUrl = "https://example.com/image2.jpg",
            location = "Wellington Central",
            title = "Mountain bike, 29er, hydraulic brakes",
            priceDisplay = "$450",
            buyNowPrice = "$899",
            isClassified = false,
        )
        val AuctionItemWithoutBuyNow = DiscoverItemUi(
            id = "3",
            imageUrl = "https://example.com/image3.jpg",
            location = "Christchurch",
            title = "Vintage leather armchair",
            priceDisplay = "$150",
            buyNowPrice = null,
            isClassified = false,
        )
    }
}
