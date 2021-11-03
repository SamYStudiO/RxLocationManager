@file:Suppress("unused")

package net.samystudio.rxlocationmanager.altitude

import org.json.JSONObject

/**
 * https://developers.google.com/maps/documentation/elevation/intro
 */
class GoogleElevationApi(private val apiKey: String) : RemoteServiceAltitude {
    override fun getUrl(latitude: Double, longitude: Double) =
        "https://maps.googleapis.com/maps/api/elevation/json?locations=$latitude,$longitude&key=$apiKey"

    override fun parseAltitude(serverResponse: String) =
        JSONObject(serverResponse)
            .getJSONArray("results")
            .getJSONObject(0)
            .getString("elevation")
            .toDouble()
}
