package nz.co.trademetest.core.network.auth

import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import mockwebserver3.junit4.MockWebServerRule
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test

class TradeMeAuthInterceptorTest {

    @get:Rule
    val serverRule = MockWebServerRule()

    private val server: MockWebServer get() = serverRule.server

    @Test
    fun `adds a PLAINTEXT OAuth header with the consumer key and secret`() {
        server.enqueue(MockResponse(body = "{}"))
        val client = clientWith(consumerKey = "my-key", consumerSecret = "my-secret")

        client.newCall(Request.Builder().url(server.url("/listings/latest.json")).build()).execute().use {
            val recorded = server.takeRequest()
            assertEquals(
                "OAuth oauth_consumer_key=\"my-key\", " +
                    "oauth_signature_method=\"PLAINTEXT\", " +
                    "oauth_signature=\"my-secret\"",
                recorded.headers["Authorization"],
            )
        }
    }

    @Test
    fun `throws MissingCredentialsException when the consumer key is blank`() {
        val client = clientWith(consumerKey = "", consumerSecret = "my-secret")

        assertThrows(MissingCredentialsException::class.java) {
            client.newCall(Request.Builder().url(server.url("/listings/latest.json")).build()).execute()
        }
    }

    @Test
    fun `throws MissingCredentialsException when the consumer secret is blank`() {
        val client = clientWith(consumerKey = "my-key", consumerSecret = "")

        assertThrows(MissingCredentialsException::class.java) {
            client.newCall(Request.Builder().url(server.url("/listings/latest.json")).build()).execute()
        }
    }

    private fun clientWith(consumerKey: String, consumerSecret: String): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(TradeMeAuthInterceptor(consumerKey = consumerKey, consumerSecret = consumerSecret))
            .build()
}
