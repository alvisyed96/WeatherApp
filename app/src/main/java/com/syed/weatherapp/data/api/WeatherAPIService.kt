package com.syed.weatherapp.data.api

import com.syed.weatherapp.data.model.WeatherAPIResponse
import com.syed.weatherapp.data.util.API_KEY
import com.syed.weatherapp.data.util.END_POINT
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPIService {
    @GET(END_POINT)
    suspend fun getWeather(
        @Query("q")
        cityName: String,
        @Query("appid")
        apiKey: String = API_KEY
    ): WeatherAPIResponse
}