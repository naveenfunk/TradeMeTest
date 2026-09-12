package nz.co.trademetest.feature.discover

import nz.co.trademetest.feature.discover.domain.DiscoverItem
import nz.co.trademetest.feature.discover.ui.home.DiscoverItemUiMapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DiscoverItemUiMapperTest {

    private val mapper = DiscoverItemUiMapper()

    @Test
    fun `classified item never shows Buy Now even when buyNowPrice is present`() {
        val item = baseItem.copy(isClassified = true, buyNowPrice = "$250")

        val result = mapper.map(item)

        assertNull(result.buyNowPrice)
    }

    @Test
    fun `classified item without a buy now price also has no Buy Now`() {
        val item = baseItem.copy(isClassified = true, buyNowPrice = null)

        val result = mapper.map(item)

        assertNull(result.buyNowPrice)
    }

    @Test
    fun `auction item with a buy now price shows both prices`() {
        val item = baseItem.copy(isClassified = false, priceDisplay = "$450", buyNowPrice = "$899")

        val result = mapper.map(item)

        assertEquals("$450", result.priceDisplay)
        assertEquals("$899", result.buyNowPrice)
    }

    @Test
    fun `auction item without a buy now price shows null`() {
        val item = baseItem.copy(isClassified = false, buyNowPrice = null)

        val result = mapper.map(item)

        assertNull(result.buyNowPrice)
    }

    @Test
    fun `price display is copied verbatim`() {
        val item = baseItem.copy(priceDisplay = "$150")

        val result = mapper.map(item)

        assertEquals("$150", result.priceDisplay)
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
        assertEquals(item.isClassified, result.isClassified)
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
            priceDisplay = "$150",
            buyNowPrice = null,
            isClassified = false,
        )
    }
}
