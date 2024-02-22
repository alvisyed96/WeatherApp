package com.syed.weatherapp.data.util

import com.syed.weatherapp.data.model.WeatherAPIResponse
sealed class ResultState {
    class Success(val response: WeatherAPIResponse): ResultState()
    class Error(val message: String): ResultState()
    object Loading: ResultState()
}