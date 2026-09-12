package nz.co.trademetest.feature.discover.domain

import javax.inject.Inject

/**
 * NZD price formatter.
 */
internal class NzdPriceFormatter @Inject constructor() : PriceFormatter {

    override fun format(cents: Long): String {
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
