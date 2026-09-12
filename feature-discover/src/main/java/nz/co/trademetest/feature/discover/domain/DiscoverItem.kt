package nz.co.trademetest.feature.discover.domain

data class DiscoverItem(
    val id: String,
    val imageUrl: String,
    val location: String,
    val title: String,
    val priceDisplay: String,
    val buyNowPrice: String?,
    val isClassified: Boolean,
)
