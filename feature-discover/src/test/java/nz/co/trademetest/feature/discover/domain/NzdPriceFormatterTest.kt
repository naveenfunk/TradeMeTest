package nz.co.trademetest.feature.discover.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class NzdPriceFormatterTest {

    private val formatter = NzdPriceFormatter()

    @Test
    fun `whole dollar amount has no decimal part`() {
        assertEquals("$899", formatter.format(899.0))
    }

    @Test
    fun `amount with cents keeps two decimal places`() {
        assertEquals("$123.45", formatter.format(123.45))
    }

    @Test
    fun `amount over a thousand is grouped with a comma`() {
        assertEquals("$1,000", formatter.format(1000.0))
    }

    @Test
    fun `small amount under a dollar is formatted correctly`() {
        assertEquals("$1.05", formatter.format(1.05))
    }

    @Test
    fun `zero is formatted as dollar zero`() {
        assertEquals("$0", formatter.format(0.0))
    }

    @Test
    fun `negative amounts are rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            formatter.format(-1.0)
        }
    }
}
