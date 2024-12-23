package com.example.triplog.network

import com.example.triplog.travel.data.GeocodingSearchResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MapboxApiService {
    @GET("geocoding/v5/mapbox.places/{place}.json")
    suspend fun searchPlace(
        @Path("place") place: String,
        @Query("access_token") accessToken: String,
        @Query("limit") limit: Int = 1
    ): Response<GeocodingSearchResponse>

    @GET("styles/v1/mapbox/streets-v12/static/{marker}/{lon},{lat},12,0/300x200@2x")
    suspend fun getStaticMap(
        @Path("lon") lon: Double,
        @Path("lat") lat: Double,
        @Path("marker") marker: String,
        @Query("access_token") accessToken: String
    ): Response<ResponseBody>
}

class MapboxClient {
    private val url = "https://api.mapbox.com/"
    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val mapboxService: MapboxApiService =
        retrofit.create(MapboxApiService::class.java)

    suspend fun getStaticMapUrl(
        longitude: Double?,
        latitude: Double?,
        marker: String,
        accessToken: String
    ): String {
        if (longitude != null && latitude != null) {
            val response =
                mapboxService.getStaticMap(
                    lon = longitude,
                    lat = latitude,
                    marker = marker,
                    accessToken = accessToken
                )
            if (response.isSuccessful) {
                return response.raw().request.url.toString()
            } else {
                throw Exception("Failed to fetch static map: ${response.errorBody()?.string()}")
            }
        }
        return ""
    }
}