package nz.co.trademetest.feature.discover.data.remote

import nz.co.trademetest.feature.discover.domain.DiscoverItem
import nz.co.trademetest.feature.discover.domain.PriceFormatter
import javax.inject.Inject

internal class ListingMapper @Inject constructor(
    private val priceFormatter: PriceFormatter,
) {

    fun map(dto: ListingDto): DiscoverItem = DiscoverItem(
        id = dto.listingId.toString(),
        imageUrl = dto.photoUrls.firstOrNull() ?: dto.pictureHref.orEmpty(),
        location = listOfNotNull(
            dto.region?.takeIf(String::isNotBlank)).firstOrNull().orEmpty(),
        title = dto.title.orEmpty(),
        priceDisplay = dto.priceDisplay?.takeIf(String::isNotBlank)
            ?: priceFormatter.format(dto.startPrice),
        buyNowPrice = dto.buyNowPrice
            ?.takeIf { dto.hasBuyNow && it > 0.0 }
            ?.let(priceFormatter::format),
        isClassified = dto.isClassified,
    )

    fun map(dtos: List<ListingDto>): List<DiscoverItem> = dtos.map(::map)
}
