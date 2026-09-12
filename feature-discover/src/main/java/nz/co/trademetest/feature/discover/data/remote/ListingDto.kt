package nz.co.trademetest.feature.discover.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class LatestListingsResponse(
    @SerialName("TotalCount") val totalCount: Int = 0,
    @SerialName("Page") val page: Int = 1,
    @SerialName("PageSize") val pageSize: Int = 0,
    @SerialName("List") val list: List<ListingDto> = emptyList(),
)

@Serializable
internal data class ListingDto(
    @SerialName("ListingId") val listingId: Long,
    @SerialName("Title") val title: String? = null,
    @SerialName("PictureHref") val pictureHref: String? = null,
    @SerialName("Region") val region: String? = null,
    @SerialName("Suburb") val suburb: String? = null,
    @SerialName("PriceDisplay") val priceDisplay: String? = null,
    @SerialName("StartPrice") val startPrice: Double = 0.0,
    @SerialName("BuyNowPrice") val buyNowPrice: Double? = null,
    @SerialName("IsClassified") val isClassified: Boolean = false,
    @SerialName("HasBuyNow") val hasBuyNow: Boolean = false,
)
