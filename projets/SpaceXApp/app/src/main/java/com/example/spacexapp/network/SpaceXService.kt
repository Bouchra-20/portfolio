package com.example.spacexapp.network

import com.example.spacexapp.model.Launch
import retrofit2.http.GET

/**
 * Retrofit interface defining the SpaceX API endpoints.
 */
interface SpaceXService {
    /**
     * Fetches all launches from the SpaceX V5 API endpoint.
     */
    @GET("v5/launches")
    suspend fun getLaunches(): List<Launch>
}