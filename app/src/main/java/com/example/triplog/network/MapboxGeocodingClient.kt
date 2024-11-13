package com.example.triplog.network

import com.example.triplog.travel.data.GeocodingSearchResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MapboxGeocodingApiService {
    @GET("mapbox.places/{place}.json")
    suspend fun searchPlace(
        @Path("place") place: String,
        @Query("access_token") accessToken: String,
        @Query("limit") limit: Int = 1
    ): Response<GeocodingSearchResponse>
}

class MapboxGeocodingClient {
    private val url = "https://api.mapbox.com/geocoding/v5/"
    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val mapboxGeocodingService: MapboxGeocodingApiService =
        retrofit.create(MapboxGeocodingApiService::class.java)
}