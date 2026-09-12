package nz.co.trademetest.feature.discover.ui.home

import androidx.compose.runtime.Immutable

/**
 * Render-ready Discover row. All formatting is precomputed by
 * [DiscoverItemUiMapper] so the composable performs no business logic.
 */
@Immutable
data class DiscoverItemUi(
    val id: String,
    val imageUrl: String,
    val location: String,
    val title: String,
    val priceDisplay: String,
    val buyNowPrice: String?,
    val isClassified: Boolean,
)
