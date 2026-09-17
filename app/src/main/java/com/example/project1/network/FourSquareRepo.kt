package com.example.project1.network

import com.example.project1.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

data class Restaurant(
    val id: String,
    val name: String,
    val imageUrl: String?,
)

// Retrieves restaurant data from Foursquare
class FoursquareRepository(
    private val client: OkHttpClient = OkHttpClient(),
) {
    suspend fun searchRestaurants(address: String, distanceMiles: Int): List<Restaurant> =
        withContext(Dispatchers.IO) {
            // The key is intentionally checked at runtime so that app still builds
            // this is for the team members who have not added the API key to the local.properties file
            if (BuildConfig.FOURSQUARE_API_KEY.isBlank()) {
                error("Add FOURSQUARE_API_KEY to local.properties")
            }
                //Foursquare expects the radius in meters while the app has it in miles.
            val url = "https://places-api.foursquare.com/places/search".toHttpUrl()
                .newBuilder()
                .addQueryParameter("query", "restaurant")
                .addQueryParameter("near", address)
                .addQueryParameter("radius", (distanceMiles * 1609.34).toInt().coerceAtMost(100_000).toString())
                .addQueryParameter("limit", "20")
                .addQueryParameter("fields", "fsq_place_id,name,photos")
                .build()

            // This is where the service keys are sent as bearer tokens and the API version is explicit
            val request = Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Authorization", "Bearer ${BuildConfig.FOURSQUARE_API_KEY}")
                .header("X-Places-Api-Version", "2025-06-17")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) error("Foursquare request failed (${response.code})")
                val body = response.body?.string() ?: error("Foursquare returned an empty response")
                val results = JSONObject(body).optJSONArray("results") ?: return@withContext emptyList()

                // This coverts the JSON response into something smaller for the UI
                buildList {
                    for (index in 0 until results.length()) {
                        val place = results.optJSONObject(index) ?: continue
                        val id = place.optString("fsq_place_id").ifBlank { place.optString("fsq_id") }
                        val name = place.optString("name")
                        if (id.isBlank() || name.isBlank()) continue
                        //This is where Foursquare photo URLs are assembled and its based on prefix's are suffix's
                        val photo = place.optJSONArray("photos")?.optJSONObject(0)
                        val imageUrl = photo?.let {
                            val prefix = it.optString("prefix")
                            val suffix = it.optString("suffix")
                            if (prefix.isNotBlank() && suffix.isNotBlank()) {
                                "${prefix}original$suffix"
                            } else null
                        }
                        add(Restaurant(id, name, imageUrl))
                    }
                }
            }
        }
}