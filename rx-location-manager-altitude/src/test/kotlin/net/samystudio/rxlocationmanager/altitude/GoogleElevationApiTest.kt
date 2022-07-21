package net.samystudio.rxlocationmanager.altitude

import org.junit.Assert.assertEquals
import org.junit.Test

class GoogleElevationApiTest {
    private val googleElevationApi = GoogleElevationApi("123456789")

    @Test
    fun getUrl() {
        assertEquals(
            "https://maps.googleapis.com/maps/api/elevation/json?locations=20.1,10.2&key=123456789",
            googleElevationApi.getUrl(20.1, 10.2)
        )
    }
}
