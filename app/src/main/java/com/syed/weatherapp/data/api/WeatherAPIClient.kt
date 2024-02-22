package com.syed.weatherapp.data.api

import com.syed.weatherapp.data.util.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class WeatherApiClient @Inject constructor(){
    val api: WeatherAPIService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherAPIService::class.java)
}