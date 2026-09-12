package nz.co.trademetest.feature.discover.ui.home

import androidx.compose.runtime.Immutable

@Immutable
internal data class DiscoverItemUi(
    val id: String,
    val imageUrl: String,
    val location: String,
    val title: String,
    val priceDisplay: String,
    val buyNowPrice: String?,
    val isClassified: Boolean,
)
