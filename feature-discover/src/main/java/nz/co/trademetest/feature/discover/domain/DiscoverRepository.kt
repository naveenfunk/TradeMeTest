package nz.co.trademetest.feature.discover.domain

import kotlinx.coroutines.flow.Flow

interface DiscoverRepository {
    fun getDiscoverItems(): Flow<List<DiscoverItem>>
}
