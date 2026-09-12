package nz.co.trademetest.core.network.auth

import nz.co.trademetest.core.network.di.ConsumerKey
import nz.co.trademetest.core.network.di.ConsumerSecret
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

internal class TradeMeAuthInterceptor @Inject constructor(
    @ConsumerKey private val consumerKey: String,
    @ConsumerSecret private val consumerSecret: String,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (consumerKey.isBlank() || consumerSecret.isBlank()) {
            throw MissingCredentialsException()
        }

        val authorizedRequest = chain.request().newBuilder()
            .header(
                "Authorization",
                "OAuth oauth_consumer_key=\"$consumerKey\", " +
                    "oauth_signature_method=\"PLAINTEXT\", " +
                    "oauth_signature=\"$consumerSecret\"",
            )
            .build()

        return chain.proceed(authorizedRequest)
    }
}
