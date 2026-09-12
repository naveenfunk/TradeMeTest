package nz.co.trademetest.feature.discover.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import nz.co.trademetest.feature.discover.domain.DiscoverItem
import nz.co.trademetest.feature.discover.domain.DiscoverRepository
import javax.inject.Inject

internal class FakeDiscoverRepository @Inject constructor() : DiscoverRepository {
    override fun getDiscoverItems(): Flow<List<DiscoverItem>> = flowOf(MockDiscoverItems)
}
