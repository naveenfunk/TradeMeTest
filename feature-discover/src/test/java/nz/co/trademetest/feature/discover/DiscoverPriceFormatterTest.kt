package nz.co.trademetest.feature.discover

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class DiscoverPriceFormatterTest {

    private val defaultLocale: Locale = Locale.getDefault()

    @After
    fun tearDown() {
        Locale.setDefault(defaultLocale)
    }

    @Test
    fun `zero cents formats as dollar zero`() {
        assertEquals("$0", DiscoverPriceFormatter.format(0))
    }

    @Test
    fun `whole dollar amount suppresses decimals`() {
        assertEquals("$1", DiscoverPriceFormatter.format(100))
    }

    @Test
    fun `cents are zero-padded`() {
        assertEquals("$1.05", DiscoverPriceFormatter.format(105))
    }

    @Test
    fun `cents that are a round ten still show two digits`() {
        assertEquals("$1.50", DiscoverPriceFormatter.format(150))
    }

    @Test
    fun `amount under one dollar`() {
        assertEquals("$0.99", DiscoverPriceFormatter.format(99))
    }

    @Test
    fun `thousands are comma grouped`() {
        assertEquals("$1,000", DiscoverPriceFormatter.format(100000))
    }

    @Test
    fun `ten thousands do not emit a leading comma`() {
        assertEquals("$10,000", DiscoverPriceFormatter.format(1000000))
    }

    @Test
    fun `large amount groups multiple thousands with cents`() {
        assertEquals("$1,234,567.89", DiscoverPriceFormatter.format(123456789))
    }

    @Test
    fun `output is unaffected by a non-US default locale`() {
        Locale.setDefault(Locale.GERMANY)

        assertEquals("$1,234,567.89", DiscoverPriceFormatter.format(123456789))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `negative cents throws`() {
        DiscoverPriceFormatter.format(-1)
    }
}
