package nz.co.trademetest.feature.discover.data.remote

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ListingMapperTest {

    private val mapper = ListingMapper()

    @Test
    fun `server PriceDisplay is carried through verbatim`() {
        val dto = baseDto.copy(priceDisplay = "Asking price", startPrice = 150.0)

        val result = mapper.map(dto)

        assertEquals("Asking price", result.priceDisplayRaw)
    }

    @Test
    fun `blank PriceDisplay is carried through as-is, unformatted`() {
        val dto = baseDto.copy(priceDisplay = "", startPrice = 899.0)

        val result = mapper.map(dto)

        assertEquals("", result.priceDisplayRaw)
        assertEquals(899.0, result.startPrice, 0.0)
    }

    @Test
    fun `null PriceDisplay is carried through as null`() {
        val dto = baseDto.copy(priceDisplay = null, startPrice = 1000.0)

        val result = mapper.map(dto)

        assertNull(result.priceDisplayRaw)
        assertEquals(1000.0, result.startPrice, 0.0)
    }

    @Test
    fun `buy now price and hasBuyNow are carried through raw, unsuppressed`() {
        val dto = baseDto.copy(hasBuyNow = true, buyNowPrice = 899.0)

        val result = mapper.map(dto)

        assertEquals(899.0, result.buyNowPrice)
        assertTrue(result.hasBuyNow)
    }

    @Test
    fun `hasBuyNow false is carried through raw, not suppressed here`() {
        val dto = baseDto.copy(hasBuyNow = false, buyNowPrice = 899.0)

        val result = mapper.map(dto)

        assertEquals(899.0, result.buyNowPrice)
        assertEquals(false, result.hasBuyNow)
    }

    @Test
    fun `zero buy now price is carried through raw, not suppressed here`() {
        val dto = baseDto.copy(hasBuyNow = true, buyNowPrice = 0.0)

        val result = mapper.map(dto)

        assertEquals(0.0, result.buyNowPrice)
    }

    @Test
    fun `null buy now price is carried through as null`() {
        val dto = baseDto.copy(hasBuyNow = true, buyNowPrice = null)

        val result = mapper.map(dto)

        assertNull(result.buyNowPrice)
    }

    @Test
    fun `isClassified is carried through unchanged`() {
        assertTrue(mapper.map(baseDto.copy(isClassified = true)).isClassified)
        assertEquals(false, mapper.map(baseDto.copy(isClassified = false)).isClassified)
    }

    @Test
    fun `null title and picture href map to empty strings`() {
        val dto = baseDto.copy(title = null, pictureHref = null)

        val result = mapper.map(dto)

        assertEquals("", result.title)
        assertEquals("", result.imageUrl)
    }

    @Test
    fun `suburb is preferred over region for location when both are present`() {
        val dto = baseDto.copy(suburb = "Ponsonby", region = "Auckland")

        val result = mapper.map(dto)

        assertEquals("Ponsonby", result.location)
    }

    @Test
    fun `region is used for location when suburb is blank or absent`() {
        val dto = baseDto.copy(suburb = null, region = "Auckland")

        val result = mapper.map(dto)

        assertEquals("Auckland", result.location)
    }

    @Test
    fun `listingId is mapped to id as a string`() {
        val result = mapper.map(baseDto.copy(listingId = 12345L))

        assertEquals("12345", result.id)
    }

    @Test
    fun `mapping a list preserves order`() {
        val first = baseDto.copy(listingId = 1L)
        val second = baseDto.copy(listingId = 2L)

        val result = mapper.map(listOf(first, second))

        assertEquals(listOf("1", "2"), result.map { it.id })
    }

    private companion object {
        val baseDto = ListingDto(
            listingId = 1L,
            title = "Vintage leather armchair",
            pictureHref = "https://example.com/image.jpg",
            region = "Auckland",
            suburb = "Ponsonby",
            priceDisplay = "$150",
            startPrice = 150.0,
            buyNowPrice = null,
            isClassified = false,
            hasBuyNow = false,
        )
    }
}
