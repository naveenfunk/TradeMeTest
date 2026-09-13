package nz.co.trademetest.feature.discover.ui.home

import javax.inject.Inject

internal fun interface PriceFormatter {
    fun format(dollars: Double): String
}

internal class NzdPriceFormatter @Inject constructor() : PriceFormatter {

    override fun format(dollars: Double): String {
        require(dollars >= 0) { "dollars must be non-negative, was $dollars" }
        val cents = Math.round(dollars * 100)
        val whole = cents / 100
        val remainder = (cents % 100).toInt()
        val grouped = groupThousands(whole)
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
