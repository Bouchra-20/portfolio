package com.example.spacexapp.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Singleton object providing a configured Retrofit instance.
 * * Sets up the base URL and the Moshi converter factory for JSON parsing.
 */
object RetrofitInstance {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    /**
     * API service implementation for SpaceX endpoints.
     */
    val api: SpaceXService = Retrofit.Builder()
        .baseUrl("https://api.spacexdata.com/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(SpaceXService::class.java)
}