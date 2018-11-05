package net.samystudio.rxlocationmanager.altitude

import org.json.JSONException
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class GoogleElevationApiTest {
    private val googleElevationApi = GoogleElevationApi("123456789")

    @Test
    fun getUrl() {
        assertEquals(
            "https://maps.googleapis.com/maps/api/elevation/json?locations=20.1,10.2&key=123456789",
            googleElevationApi.getUrl(20.1, 10.2)
        )
    }

    @Test
    fun parseAltitude() {
        val response = "{\n" +
                "   \"results\" : [\n" +
                "      {\n" +
                "         \"elevation\" : 1608.637939453125,\n" +
                "         \"location\" : {\n" +
                "            \"lat\" : 39.73915360,\n" +
                "            \"lng\" : -104.98470340\n" +
                "         },\n" +
                "         \"resolution\" : 4.771975994110107\n" +
                "      }\n" +
                "   ],\n" +
                "   \"status\" : \"OK\"\n" +
                "}"

        assertEquals(1608.637939453125, googleElevationApi.parseAltitude(response), 0.000001)
    }

    @Test(expected = JSONException::class)
    fun parseAltitudeException() {
        googleElevationApi.parseAltitude("")
    }
}