package nz.co.trademetest.feature.discover.data.remote

import nz.co.trademetest.feature.discover.domain.DiscoverItem
import javax.inject.Inject

internal class ListingMapper @Inject constructor() {

    fun map(dto: ListingDto): DiscoverItem = DiscoverItem(
        id = dto.listingId.toString(),
        imageUrl = dto.photoUrls.firstOrNull() ?: dto.pictureHref.orEmpty(),
        location = dto.suburb?.takeIf(String::isNotBlank)
            ?: dto.region?.takeIf(String::isNotBlank).orEmpty(),
        title = dto.title.orEmpty(),
        startPrice = dto.startPrice,
        priceDisplayRaw = dto.priceDisplay,
        buyNowPrice = dto.buyNowPrice,
        hasBuyNow = dto.hasBuyNow,
        isClassified = dto.isClassified,
    )

    fun map(dtos: List<ListingDto>): List<DiscoverItem> = dtos.map(::map)
}
