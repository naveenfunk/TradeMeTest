package nz.co.trademetest.feature.discover.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import nz.co.trademetest.feature.discover.data.FakeDiscoverRepository
import nz.co.trademetest.feature.discover.domain.DiscoverRepository
import nz.co.trademetest.feature.discover.domain.NzdPriceFormatter
import nz.co.trademetest.feature.discover.domain.PriceFormatter

@Module
@InstallIn(SingletonComponent::class)
internal interface DiscoverModule {

    @Binds
    fun bindDiscoverRepository(fake: FakeDiscoverRepository): DiscoverRepository

    @Binds
    fun bindPriceFormatter(impl: NzdPriceFormatter): PriceFormatter
}
