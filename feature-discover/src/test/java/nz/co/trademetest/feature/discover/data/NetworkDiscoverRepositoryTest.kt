package nz.co.trademetest.feature.discover.data

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import mockwebserver3.MockResponse
import mockwebserver3.junit4.MockWebServerRule
import nz.co.trademetest.feature.discover.data.remote.DiscoverApi
import nz.co.trademetest.feature.discover.data.remote.ListingMapper
import nz.co.trademetest.feature.discover.domain.NzdPriceFormatter
import okhttp3.MediaType.Companion.toMediaType
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class NetworkDiscoverRepositoryTest {

    @get:Rule
    val serverRule = MockWebServerRule()

    private fun createRepository(): NetworkDiscoverRepository {
        val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
        val retrofit = Retrofit.Builder()
            .baseUrl(serverRule.server.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        val api = retrofit.create(DiscoverApi::class.java)
        return NetworkDiscoverRepository(api = api, mapper = ListingMapper(NzdPriceFormatter()))
    }

    @Test
    fun `maps a successful response into domain items, tolerating unknown fields`() = runTest {
        serverRule.server.enqueue(
            MockResponse(
                body = """
                {
                  "TotalCount": 1,
                  "Page": 1,
                  "PageSize": 1,
                  "SomeFieldWeDontModel": "ignored",
                  "List": [
                    {
                      "ListingId": 42,
                      "Title": "Vintage leather armchair",
                      "PictureHref": "https://example.com/image.jpg",
                      "Suburb": "Ponsonby",
                      "Region": "Auckland",
                      "PriceDisplay": "$150",
                      "StartPrice": 150.0,
                      "BuyNowPrice": 899.0,
                      "HasBuyNow": true,
                      "IsClassified": false,
                      "AnotherUnmodeledField": 12345
                    }
                  ]
                }
                """.trimIndent(),
            ),
        )
        val repository = createRepository()

        val items = repository.getDiscoverItems().let { flow ->
            var result: List<nz.co.trademetest.feature.discover.domain.DiscoverItem> = emptyList()
            flow.collect { result = it }
            result
        }

        assertEquals(1, items.size)
        val item = items.first()
        assertEquals("42", item.id)
        assertEquals("Vintage leather armchair", item.title)
        assertEquals("Ponsonby", item.location)
        assertEquals("$150", item.priceDisplay)
        assertEquals("$899", item.buyNowPrice)
        assertEquals(false, item.isClassified)
    }

    @Test
    fun `an empty listings array maps to an empty list`() = runTest {
        serverRule.server.enqueue(
            MockResponse(body = """{"TotalCount": 0, "Page": 1, "PageSize": 0, "List": []}"""),
        )
        val repository = createRepository()

        val items = repository.getDiscoverItems().let { flow ->
            var result: List<nz.co.trademetest.feature.discover.domain.DiscoverItem>? = null
            flow.collect { result = it }
            result
        }

        assertEquals(emptyList<Any>(), items)
    }
}
