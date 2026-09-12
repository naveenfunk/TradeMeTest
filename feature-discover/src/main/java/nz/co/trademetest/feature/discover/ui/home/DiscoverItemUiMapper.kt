package nz.co.trademetest.feature.discover.ui.home

import nz.co.trademetest.feature.discover.DiscoverPriceFormatter
import nz.co.trademetest.feature.discover.domain.DiscoverItem
import javax.inject.Inject

/**
 * Maps raw [DiscoverItem]s to render-ready [DiscoverItemUi]s.
 */
internal class DiscoverItemUiMapper @Inject constructor() {

    fun map(item: DiscoverItem): DiscoverItemUi = DiscoverItemUi(
        id = item.id,
        imageUrl = item.imageUrl,
        location = item.location,
        title = item.title,
        priceDisplay = DiscoverPriceFormatter.format(item.priceDisplayCents),
        buyNowPrice = item.buyNowPriceCents
            ?.takeUnless { item.isClassified }
            ?.let(DiscoverPriceFormatter::format),
        isClassified = item.isClassified,
    )

    fun map(items: List<DiscoverItem>): List<DiscoverItemUi> = items.map(::map)
}
