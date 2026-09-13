package nz.co.trademetest.feature.discover.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

internal interface DiscoverApi {

    @GET("listings/latest.json")
    suspend fun getLatestListings(
        @Query("rows") rows: Int = 20,
        @Query("photo_size") photoSize: String = "Medium",
    ): LatestListingsResponse
}
