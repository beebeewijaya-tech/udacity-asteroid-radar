package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.udacity.asteroidradar.Constants.BASE_URL
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

interface AsteroidService {
    @Scalar
    @GET("/neo/rest/v1/feed")
    fun getAsteroidListAsync(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") apiKey: String
    ): Deferred<String>


    @Json
    @GET("/planetary/apod")
    fun getPictureOfTheDayAsync(
        @Query("api_key") apiKey: String
    ): Deferred<NetworkPictureOfDay>
}

object AsteroidApi {
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(ConverterScalarOrJson.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(BASE_URL)
        .build()

    val retrofitService: AsteroidService by lazy {
        retrofit.create(AsteroidService::class.java)
    }
}