package nz.co.trademetest.feature.discover.domain

data class DiscoverItem(
    val id: String,
    val imageUrl: String,
    val location: String,
    val title: String,
    val startPrice: Double,
    val priceDisplayRaw: String?,
    val buyNowPrice: Double?,
    val hasBuyNow: Boolean,
    val isClassified: Boolean,
)
