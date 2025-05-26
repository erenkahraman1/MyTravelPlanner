package com.erenkahraman.mytravelplanner

// API integration methods developed with AI guidance for debugging
// URL formatting and JSON parsing logic assisted by AI

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder

const val GEOAPIFY_API_KEY = "42931a168b4d428f900b6c8f5e6479ff"

object GeoapifyService {

    suspend fun getCitiesByCountry(countryCode: String, apiKey: String): List<String> {
        return withContext(Dispatchers.IO) {
            try {

                val url = "https://api.geoapify.com/v1/geocode/search?country=$countryCode&type=city&limit=10&format=json&apiKey=$apiKey"

                val response = URL(url).readText()
                val json = JSONObject(response)

                val features = json.getJSONArray("features")

                List(features.length()) { i ->
                    val properties = features.getJSONObject(i).getJSONObject("properties")
                    properties.getString("city") + ", " + properties.getString("country")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    suspend fun getCoordinatesForPlace(place: String, apiKey: String): Pair<Double, Double> {
        return withContext(Dispatchers.IO) {
            try {
                val encodedQuery = URLEncoder.encode(place, "UTF-8")
                val url = "https://api.geoapify.com/v1/geocode/search?text=$encodedQuery&format=json&apiKey=$apiKey"

                println("Coordinate API URL: $url")

                val response = URL(url).readText()
                val json = JSONObject(response)
                val features = json.getJSONArray("results")

                if (features.length() > 0) {
                    val location = features.getJSONObject(0)
                    val lat = location.getDouble("lat")
                    val lon = location.getDouble("lon")
                    Pair(lat, lon)
                } else {
                    Pair(0.0, 0.0)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Pair(0.0, 0.0)
            }
        }
    }

    suspend fun getPlacesByCity(city: String, apiKey: String): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val encodedQuery = URLEncoder.encode(city, "UTF-8")

                val url = "https://api.geoapify.com/v1/geocode/autocomplete?text=$encodedQuery&type=poi&limit=10&format=json&apiKey=$apiKey"

                val response = URL(url).readText()
                val json = JSONObject(response)
                val features = json.getJSONArray("features")

                List(features.length()) { i ->
                    val properties = features.getJSONObject(i).getJSONObject("properties")
                    properties.getString("name")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    suspend fun searchCountries(query: String, apiKey: String): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val encodedQuery = URLEncoder.encode(query, "UTF-8")
                val url = "https://api.geoapify.com/v1/geocode/search?text=$encodedQuery&format=json&apiKey=$apiKey"

                val response = URL(url).readText()
                val json = JSONObject(response)
                val features = json.getJSONArray("results")

                val countries = mutableSetOf<String>()
                for (i in 0 until features.length()) {
                    val result = features.getJSONObject(i)
                    val country = result.optString("country", "")
                    if (country.isNotEmpty() && country.contains(query, ignoreCase = true)) {
                        countries.add(country)
                    }
                }
                countries.toList().take(5)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    suspend fun searchCitiesByCountry(countryName: String, cityQuery: String, apiKey: String): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val encodedQuery = URLEncoder.encode(cityQuery, "UTF-8")
                val url = "https://api.geoapify.com/v1/geocode/search?text=$encodedQuery&type=city&format=json&apiKey=$apiKey"

                println("City API URL: $url")

                val response = URL(url).readText()
                val json = JSONObject(response)
                val features = json.getJSONArray("results")

                val cities = mutableListOf<String>()
                for (i in 0 until features.length()) {
                    val result = features.getJSONObject(i)
                    val city = result.optString("city", "")
                    val country = result.optString("country", "")

                    if (city.isNotEmpty() && country.equals(countryName, ignoreCase = true)) {
                        cities.add(city)
                    }
                }
                cities.distinct().take(10)
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    suspend fun searchPOIByCity(cityName: String, poiQuery: String, apiKey: String): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val encodedQuery = URLEncoder.encode("$poiQuery $cityName", "UTF-8")
                val url = "https://api.geoapify.com/v1/geocode/search?text=$encodedQuery&format=json&apiKey=$apiKey"

                println("POI API URL: $url")

                val response = URL(url).readText()
                val json = JSONObject(response)
                val features = json.getJSONArray("results")

                val pois = mutableListOf<String>()
                for (i in 0 until features.length()) {
                    val result = features.getJSONObject(i)
                    val name = result.optString("name", "")
                    val formatted = result.optString("formatted", "")

                    if (name.isNotEmpty()) {
                        pois.add(name)
                    } else if (formatted.isNotEmpty()) {
                        pois.add(formatted)
                    }
                }
                pois.distinct().take(10)
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }
}

