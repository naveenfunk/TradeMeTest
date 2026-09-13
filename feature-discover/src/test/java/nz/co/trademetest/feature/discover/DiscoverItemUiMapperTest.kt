package nz.co.trademetest.feature.discover

import nz.co.trademetest.feature.discover.domain.DiscoverItem
import nz.co.trademetest.feature.discover.ui.home.DiscoverItemUiMapper
import nz.co.trademetest.feature.discover.ui.home.NzdPriceFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * The Auction-vs-Classified suppression rule already lives in
 * [nz.co.trademetest.feature.discover.domain.GetDiscoverItemsUseCase] by the time an
 * item reaches this mapper (see GetDiscoverItemsUseCaseTest), so this mapper is
 * responsible only for turning already-decided numeric prices into display strings.
 */
class DiscoverItemUiMapperTest {

    private val mapper = DiscoverItemUiMapper(priceFormatter = NzdPriceFormatter())

    @Test
    fun `server priceDisplayRaw is preferred verbatim over a locally formatted price`() {
        val item = baseItem.copy(priceDisplayRaw = "Asking price", startPrice = 150.0)

        val result = mapper.map(item)

        assertEquals("Asking price", result.priceDisplay)
    }

    @Test
    fun `blank priceDisplayRaw falls back to a locally formatted start price`() {
        val item = baseItem.copy(priceDisplayRaw = "", startPrice = 899.0)

        val result = mapper.map(item)

        assertEquals("$899", result.priceDisplay)
    }

    @Test
    fun `null priceDisplayRaw falls back to a locally formatted start price`() {
        val item = baseItem.copy(priceDisplayRaw = null, startPrice = 1000.0)

        val result = mapper.map(item)

        assertEquals("$1,000", result.priceDisplay)
    }

    @Test
    fun `a decided buy now price is formatted`() {
        val item = baseItem.copy(buyNowPrice = 899.0)

        val result = mapper.map(item)

        assertEquals("$899", result.buyNowPrice)
    }

    @Test
    fun `a null buy now price stays null`() {
        val item = baseItem.copy(buyNowPrice = null)

        val result = mapper.map(item)

        assertNull(result.buyNowPrice)
    }

    @Test
    fun `passthrough fields are copied unchanged`() {
        val item = baseItem.copy(
            id = "42",
            imageUrl = "https://example.com/image.jpg",
            location = "Auckland City",
            title = "Vintage leather armchair",
        )

        val result = mapper.map(item)

        assertEquals("42", result.id)
        assertEquals("https://example.com/image.jpg", result.imageUrl)
        assertEquals("Auckland City", result.location)
        assertEquals("Vintage leather armchair", result.title)
    }

    @Test
    fun `mapping a list preserves order`() {
        val first = baseItem.copy(id = "1")
        val second = baseItem.copy(id = "2")
        val third = baseItem.copy(id = "3")

        val result = mapper.map(listOf(first, second, third))

        assertEquals(listOf("1", "2", "3"), result.map { it.id })
    }

    private companion object {
        val baseItem = DiscoverItem(
            id = "1",
            imageUrl = "https://example.com/image.jpg",
            location = "Auckland City",
            title = "Vintage leather armchair",
            startPrice = 150.0,
            priceDisplayRaw = "$150",
            buyNowPrice = null,
            hasBuyNow = false,
            isClassified = false,
        )
    }
}
