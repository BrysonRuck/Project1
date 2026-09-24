package com.example.project1.network

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FoursquareRepositoryInstrumentedTest {

    @Test
    fun searchRestaurants_parsesResponseOnDevice(): Unit = runBlocking {
        var interceptedRequest: Request? = null

        val jsonResponse = """
            {
              "results": [
                {
                  "fsq_place_id": "device_place_001",
                  "name": "Monterey Seafood Grill"
                }
              ]
            }
        """.trimIndent()

        val mockClient = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                interceptedRequest = chain.request()
                Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .body(jsonResponse.toResponseBody("application/json".toMediaType()))
                    .build()
            })
            .build()

        val repository = FoursquareRepository(client = mockClient)
        val restaurants = repository.searchRestaurants("Monterey, CA", 10)

        assertEquals(1, restaurants.size)
        assertEquals("device_place_001", restaurants[0].id)
        assertEquals("Monterey Seafood Grill", restaurants[0].name)

        val request = requireNotNull(interceptedRequest)
        assertEquals("places-api.foursquare.com", request.url.host)
        assertEquals("restaurant", request.url.queryParameter("query"))
        assertEquals("Monterey, CA", request.url.queryParameter("near"))
    }
}
