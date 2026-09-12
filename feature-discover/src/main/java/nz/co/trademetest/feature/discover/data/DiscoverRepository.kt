package nz.co.trademetest.feature.discover.data

import kotlinx.coroutines.flow.Flow
import nz.co.trademetest.feature.discover.domain.DiscoverItem

interface DiscoverRepository {
    fun getDiscoverItems(): Flow<List<DiscoverItem>>
}