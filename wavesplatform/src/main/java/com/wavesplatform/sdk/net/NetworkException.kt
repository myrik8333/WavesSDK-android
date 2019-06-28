/*
 * Created by Eduard Zaydel on 1/4/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.net

import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

class NetworkException internal constructor(
    message: String?,
    /**
     * The request URL which produced the error.
     */
    val url: String?,
    /**
     * Response object containing status code, headers, body, etc.
     */
    val response: Response<*>?,
    /**
     * The event kind which triggered this error.
     */
    val kind: Kind,
    exception: Throwable?,
    /**
     * The Retrofit this request was executed on
     */
    private val retrofit: Retrofit?
) : RuntimeException(message, exception) {

    /**
     * Identifies the event kind which triggered a [NetworkException].
     */
    enum class Kind {
        /**
         * An [IOException] occurred while communicating to the server.
         */
        NETWORK,
        /**
         * A non-200 HTTP status code was received from the server.
         */
        HTTP,
        /**
         * An internal error occurred while attempting to execute a request. It is best practice to
         * re-throw this exception so your application crashes.
         */
        UNEXPECTED
    }

    companion object {

        fun httpError(url: String, response: Response<*>, retrofit: Retrofit): NetworkException {
            val message = response.code().toString() + " " + response.message()
            return NetworkException(message, url, response, Kind.HTTP, null, retrofit)
        }

        fun networkError(exception: IOException): NetworkException {
            return NetworkException(exception.message, null, null, Kind.NETWORK, exception, null)
        }

        fun unexpectedError(exception: Throwable): NetworkException {
            return NetworkException(exception.message, null, null, Kind.UNEXPECTED, exception, null)
        }
    }
}