package com.syed.weatherapp.domain

import com.syed.weatherapp.data.model.WeatherAPIResponse
import kotlinx.coroutines.CoroutineDispatcher

interface WeatherRepository {
    suspend fun getWeather(dispatcher: CoroutineDispatcher, cityName: String): WeatherAPIResponse
}