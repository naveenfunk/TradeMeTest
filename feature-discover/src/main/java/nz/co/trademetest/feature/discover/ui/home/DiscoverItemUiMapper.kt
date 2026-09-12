package nz.co.trademetest.feature.discover.ui.home

import nz.co.trademetest.feature.discover.domain.DiscoverItem
import nz.co.trademetest.feature.discover.domain.PriceFormatter
import javax.inject.Inject

/**
 * Maps raw [DiscoverItem]s to render-ready [DiscoverItemUi]s.
 */
internal class DiscoverItemUiMapper @Inject constructor(
    private val priceFormatter: PriceFormatter,
) {

    fun map(item: DiscoverItem): DiscoverItemUi = DiscoverItemUi(
        id = item.id,
        imageUrl = item.imageUrl,
        location = item.location,
        title = item.title,
        priceDisplay = priceFormatter.format(item.priceDisplayCents),
        buyNowPrice = item.buyNowPriceCents
            ?.takeUnless { item.isClassified }
            ?.let(priceFormatter::format),
        isClassified = item.isClassified,
    )

    fun map(items: List<DiscoverItem>): List<DiscoverItemUi> = items.map(::map)
}
