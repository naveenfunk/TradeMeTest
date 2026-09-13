package nz.co.trademetest.feature.discover.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import nz.co.trademetest.core.network.di.IoDispatcher
import nz.co.trademetest.feature.discover.data.remote.DiscoverApi
import nz.co.trademetest.feature.discover.data.remote.ListingMapper
import nz.co.trademetest.feature.discover.domain.DiscoverItem
import nz.co.trademetest.feature.discover.domain.DiscoverRepository
import javax.inject.Inject

internal class NetworkDiscoverRepository @Inject constructor(
    private val api: DiscoverApi,
    private val mapper: ListingMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : DiscoverRepository {

    override fun getDiscoverItems(): Flow<List<DiscoverItem>> = flow {
        val response = api.getLatestListings()
        emit(mapper.map(response.list))
    }.flowOn(ioDispatcher)
}
