package com.syed.weatherapp.data.repository

import com.syed.weatherapp.data.api.WeatherApiClient
import com.syed.weatherapp.data.model.WeatherAPIResponse
import com.syed.weatherapp.domain.WeatherRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(private val apiClient: WeatherApiClient) : WeatherRepository {
    override suspend fun getWeather(
        dispatcher: CoroutineDispatcher,
        cityName: String
    ): WeatherAPIResponse {
        return withContext(dispatcher) {
            val response = apiClient.api.getWeather(cityName = cityName)
            response
        }
    }
}