package nz.co.trademetest.feature.discover.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Pins the single Buy Now business rule: a Buy Now price is surfaced whenever the listing
 * flags [DiscoverItem.hasBuyNow], regardless of listing type or price value. This is the one
 * place that rule is decided -- neither the data-layer mapper nor the UI mapper re-derive it.
 */
class GetDiscoverItemsUseCaseTest {

    private fun useCase(item: DiscoverItem): GetDiscoverItemsUseCase {
        val repository = object : DiscoverRepository {
            override fun getDiscoverItems(): Flow<List<DiscoverItem>> = flowOf(listOf(item))
        }
        return GetDiscoverItemsUseCase(repository)
    }

    private suspend fun mapSingle(item: DiscoverItem): DiscoverItem =
        useCase(item).invoke().let { flow ->
            var result: DiscoverItem? = null
            flow.collect { result = it.first() }
            result!!
        }

    @Test
    fun `classified item still shows Buy Now when the listing offers it`() = runTest {
        val result = mapSingle(baseItem.copy(isClassified = true, hasBuyNow = true, buyNowPrice = 899.0))

        assertEquals(899.0, result.buyNowPrice)
    }

    @Test
    fun `auction with hasBuyNow and a positive price shows Buy Now`() = runTest {
        val result = mapSingle(
            baseItem.copy(isClassified = false, hasBuyNow = true, buyNowPrice = 899.0),
        )

        assertEquals(899.0, result.buyNowPrice)
    }

    @Test
    fun `hasBuyNow false suppresses Buy Now regardless of price`() = runTest {
        val result = mapSingle(
            baseItem.copy(isClassified = false, hasBuyNow = false, buyNowPrice = 899.0),
        )

        assertNull(result.buyNowPrice)
    }

    @Test
    fun `zero buy now price is preserved when hasBuyNow is set`() = runTest {
        val result = mapSingle(
            baseItem.copy(isClassified = false, hasBuyNow = true, buyNowPrice = 0.0),
        )

        assertEquals(0.0, result.buyNowPrice)
    }

    @Test
    fun `null buy now price stays null`() = runTest {
        val result = mapSingle(
            baseItem.copy(isClassified = false, hasBuyNow = true, buyNowPrice = null),
        )

        assertNull(result.buyNowPrice)
    }

    @Test
    fun `passthrough fields are unaffected by the pricing rule`() = runTest {
        val result = mapSingle(baseItem)

        assertEquals(baseItem.id, result.id)
        assertEquals(baseItem.imageUrl, result.imageUrl)
        assertEquals(baseItem.location, result.location)
        assertEquals(baseItem.title, result.title)
        assertEquals(baseItem.startPrice, result.startPrice, 0.0)
        assertEquals(baseItem.priceDisplayRaw, result.priceDisplayRaw)
    }

    private companion object {
        val baseItem = DiscoverItem(
            id = "1",
            imageUrl = "https://example.com/image.jpg",
            location = "Auckland City",
            title = "Vintage leather armchair",
            startPrice = 150.0,
            priceDisplayRaw = null,
            buyNowPrice = null,
            hasBuyNow = false,
            isClassified = false,
        )
    }
}
