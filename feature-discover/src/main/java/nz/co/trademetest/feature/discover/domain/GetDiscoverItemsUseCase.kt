package nz.co.trademetest.feature.discover.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDiscoverItemsUseCase @Inject constructor(
    private val repository: DiscoverRepository,
) {
    operator fun invoke(): Flow<List<DiscoverItem>> =
        repository.getDiscoverItems().map { items -> items.map(::applyPricingRules) }

    private fun applyPricingRules(item: DiscoverItem): DiscoverItem = item.copy(
        buyNowPrice = item.buyNowPrice
            ?.takeIf { item.hasBuyNow && it > 0.0 && !item.isClassified },
    )
}
