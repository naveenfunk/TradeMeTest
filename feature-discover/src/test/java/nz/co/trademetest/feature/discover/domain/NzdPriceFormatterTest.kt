package nz.co.trademetest.feature.discover.domain

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class NzdPriceFormatterTest {

    private val defaultLocale: Locale = Locale.getDefault()
    private val formatter = NzdPriceFormatter()

    @After
    fun tearDown() {
        Locale.setDefault(defaultLocale)
    }

    @Test
    fun `zero cents formats as dollar zero`() {
        assertEquals("$0", formatter.format(0))
    }

    @Test
    fun `whole dollar amount suppresses decimals`() {
        assertEquals("$1", formatter.format(100))
    }

    @Test
    fun `cents are zero-padded`() {
        assertEquals("$1.05", formatter.format(105))
    }

    @Test
    fun `cents that are a round ten still show two digits`() {
        assertEquals("$1.50", formatter.format(150))
    }

    @Test
    fun `amount under one dollar`() {
        assertEquals("$0.99", formatter.format(99))
    }

    @Test
    fun `thousands are comma grouped`() {
        assertEquals("$1,000", formatter.format(100000))
    }

    @Test
    fun `ten thousands do not emit a leading comma`() {
        assertEquals("$10,000", formatter.format(1000000))
    }

    @Test
    fun `large amount groups multiple thousands with cents`() {
        assertEquals("$1,234,567.89", formatter.format(123456789))
    }

    @Test
    fun `output is unaffected by a non-US default locale`() {
        Locale.setDefault(Locale.GERMANY)

        assertEquals("$1,234,567.89", formatter.format(123456789))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `negative cents throws`() {
        formatter.format(-1)
    }
}
