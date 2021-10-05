package com.example.taiwantouristspots.retrofitmodel

import com.example.taiwantouristspots.spotsmodel.Spots
import com.example.taiwantouristspots.weathermodel.Weathers
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface TouristSpotsApi {

    @Headers("Authorization:CWB-FAB67117-0A14-4BE6-87D8-3B8003A37085")
    @GET("v1/rest/datastore/F-C0032-001")
    suspend fun getWeather(): Response<Weathers>

    @GET("XMLReleaseALL_public/scenic_spot_C_f.json")
    suspend fun getSpots(): Response<Spots>
}