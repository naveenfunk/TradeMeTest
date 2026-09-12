package nz.co.trademetest.feature.discover.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDiscoverItemsUseCase @Inject constructor(
    private val repository: DiscoverRepository,
) {
    operator fun invoke(): Flow<List<DiscoverItem>> = repository.getDiscoverItems()
}
