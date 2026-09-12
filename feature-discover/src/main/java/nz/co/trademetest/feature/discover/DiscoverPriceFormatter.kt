package nz.co.trademetest.feature.discover

/**
 * Hand-rolled NZD price formatter — deliberately locale-*invariant*.
 *
 * `NumberFormat.getCurrencyInstance()` is locale-*variable* and shifts with the
 * JDK's bundled CLDR data (e.g. it emits `₹` on an `en_IN` default locale on this
 * machine). This formatter always renders `$`, comma thousands separators, and
 * suppresses decimals when the amount is a whole dollar figure.
 */
internal object DiscoverPriceFormatter {

    fun format(cents: Long): String {
        require(cents >= 0) { "cents must be non-negative, was $cents" }
        val grouped = groupThousands(cents / 100)
        val remainder = (cents % 100).toInt()
        return if (remainder == 0) {
            "$$grouped"
        } else {
            "$$grouped.${remainder.toString().padStart(2, '0')}"
        }
    }

    private fun groupThousands(value: Long): String {
        val digits = value.toString()
        val builder = StringBuilder()
        for (index in digits.indices) {
            val fromEnd = digits.length - index
            if (index != 0 && fromEnd % 3 == 0) {
                builder.append(',')
            }
            builder.append(digits[index])
        }
        return builder.toString()
    }
}
