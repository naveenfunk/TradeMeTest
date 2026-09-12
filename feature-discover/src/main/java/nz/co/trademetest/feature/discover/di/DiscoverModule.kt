package nz.co.trademetest.feature.discover.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import nz.co.trademetest.feature.discover.data.FakeDiscoverRepository
import nz.co.trademetest.feature.discover.data.NetworkDiscoverRepository
import nz.co.trademetest.feature.discover.data.remote.DiscoverApi
import nz.co.trademetest.feature.discover.domain.DiscoverRepository
import nz.co.trademetest.feature.discover.domain.NzdPriceFormatter
import nz.co.trademetest.feature.discover.domain.PriceFormatter
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface DiscoverModule {

    @Binds
    fun bindDiscoverRepository(networkDiscoverRepository: NetworkDiscoverRepository): DiscoverRepository

    @Binds
    fun bindPriceFormatter(nzdPriceFormatter: NzdPriceFormatter): PriceFormatter

    companion object {
        @Provides
        @Singleton
        fun provideDiscoverApi(retrofit: Retrofit): DiscoverApi = retrofit.create(DiscoverApi::class.java)
    }
}
