package nz.co.trademetest.feature.discover.domain

/**
 * Formats a price given in cents into a display-ready string.
 */
fun interface PriceFormatter {
    fun format(cents: Long): String
}
