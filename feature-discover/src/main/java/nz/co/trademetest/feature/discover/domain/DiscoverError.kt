package nz.co.trademetest.feature.discover.domain

import nz.co.trademetest.core.network.auth.MissingCredentialsException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/** A classified reason [nz.co.trademetest.feature.discover.domain.GetDiscoverItemsUseCase] failed, used to pick a specific error message. */
internal sealed interface DiscoverError {
    /** No network connectivity, DNS failure, or the request timed out. */
    data object Offline : DiscoverError

    /** The Trade Me API consumer key/secret are not configured. */
    data object MissingCredentials : DiscoverError

    /** Any other failure: a non-2xx response, malformed body, or unexpected error. */
    data object Server : DiscoverError
}

internal fun Throwable.toDiscoverError(): DiscoverError = when (this) {
    is MissingCredentialsException -> DiscoverError.MissingCredentials
    is UnknownHostException, is ConnectException, is SocketTimeoutException -> DiscoverError.Offline
    else -> DiscoverError.Server
}
