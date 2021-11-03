package net.samystudio.rxlocationmanager.altitude

import java.net.HttpURLConnection
import java.net.URL

interface RemoteServiceAltitude {
    /**
     * Get remote service [String] url with the specified [latitude] and [longitude].
     */
    fun getUrl(latitude: Double, longitude: Double): String

    /**
     * Get [HttpURLConnection] used to make the request. This is a default implementation, override
     * to do something more specific.
     */
    fun getHttpURLConnection(latitude: Double, longitude: Double): HttpURLConnection {
        val url = URL(getUrl(latitude, longitude))
        val httpConnection = url.openConnection() as HttpURLConnection
        httpConnection.requestMethod = "GET"
        return httpConnection
    }

    /**
     * Get a valid [Double] altitude or throw an [Exception] if [serverResponse] couldn't be parsed.
     */
    fun parseAltitude(serverResponse: String): Double
}
