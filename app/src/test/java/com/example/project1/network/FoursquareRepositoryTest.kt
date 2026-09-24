package com.example.project1.network

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class FoursquareRepositoryTest {

    @Test
    fun searchRestaurants_success_parsesRestaurantsAndSetsCorrectRequestParameters(): Unit = runBlocking {
        var interceptedRequest: Request? = null

        val jsonResponse = """
            {
              "results": [
                {
                  "fsq_place_id": "place_001",
                  "name": "Monterey Seafood Grill"
                },
                {
                  "fsq_id": "place_002",
                  "name": "Cannery Row Bistro"
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
        val restaurants = repository.searchRestaurants(address = "Monterey, CA", _distanceMiles = 10)

        // Verify parsed restaurant list
        assertEquals(2, restaurants.size)
        assertEquals("place_001", restaurants[0].id)
        assertEquals("Monterey Seafood Grill", restaurants[0].name)
        assertEquals("place_002", restaurants[1].id)
        assertEquals("Cannery Row Bistro", restaurants[1].name)

        // Verify request details
        val request = requireNotNull(interceptedRequest)
        val url = request.url

        assertEquals("places-api.foursquare.com", url.host)
        assertEquals("/places/search", url.encodedPath)
        assertEquals("restaurant", url.queryParameter("query"))
        assertEquals("Monterey, CA", url.queryParameter("near"))
        assertEquals("20", url.queryParameter("limit"))
        assertEquals("fsq_place_id,name", url.queryParameter("fields"))

        assertEquals("application/json", request.header("Accept"))
        assertTrue(request.header("Authorization")?.startsWith("Bearer ") == true)
        assertEquals("2025-06-17", request.header("X-Places-Api-Version"))
    }

    @Test
    fun searchRestaurants_emptyResults_returnsEmptyList(): Unit = runBlocking {
        val jsonResponse = """{ "results": [] }"""

        val mockClient = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
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
        val restaurants = repository.searchRestaurants("Unknown Location", 5)

        assertTrue(restaurants.isEmpty())
    }

    @Test
    fun searchRestaurants_skipsInvalidItemsWithoutIdOrName(): Unit = runBlocking {
        val jsonResponse = """
            {
              "results": [
                { "fsq_place_id": "", "name": "No ID Cafe" },
                { "fsq_place_id": "valid_001", "name": "" },
                { "fsq_place_id": "valid_002", "name": "Valid Diner" }
              ]
            }
        """.trimIndent()

        val mockClient = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
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
        assertEquals("valid_002", restaurants[0].id)
        assertEquals("Valid Diner", restaurants[0].name)
    }

    @Test
    fun searchRestaurants_httpError_throwsException(): Unit = runBlocking {
        val mockClient = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(400)
                    .message("Bad Request")
                    .body("""{"error": "Invalid location"}""".toResponseBody("application/json".toMediaType()))
                    .build()
            })
            .build()

        val repository = FoursquareRepository(client = mockClient)

        try {
            repository.searchRestaurants("Bad Address", 10)
            fail("Expected an IllegalStateException on HTTP error")
        } catch (e: IllegalStateException) {
            assertTrue(e.message?.contains("Foursquare request failed (400)") == true)
        }
    }
}
