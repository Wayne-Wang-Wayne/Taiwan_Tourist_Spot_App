package com.example.taiwantouristspots.retrofitmodel

import com.example.taiwantouristspots.spotsmodel.Spots
import com.example.taiwantouristspots.weathermodel.Weathers
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val weathersApi: TouristSpotsApi by lazy {
        Retrofit
            .Builder()
            .baseUrl("https://opendata.cwb.gov.tw/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TouristSpotsApi::class.java)
    }

    val spotsApi: TouristSpotsApi by lazy {
        Retrofit
            .Builder()
            .baseUrl("https://gis.taiwan.net.tw/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TouristSpotsApi::class.java)
    }

    suspend fun getWeather(): Response<Weathers> {
        return weathersApi.getWeather()
    }

    suspend fun getSpots():Response<Spots>{
        return spotsApi.getSpots()
    }

}