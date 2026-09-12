package nz.co.trademetest.core.network.auth

import java.io.IOException

class MissingCredentialsException : IOException("Trade Me API credentials are not configured")
