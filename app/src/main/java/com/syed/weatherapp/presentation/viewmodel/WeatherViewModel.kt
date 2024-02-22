package com.syed.weatherapp.presentation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syed.weatherapp.data.model.WeatherAPIResponse
import com.syed.weatherapp.data.util.ResultState
import com.syed.weatherapp.domain.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {
    private val _resultResponse: MutableState<ResultState?> = mutableStateOf(null)
    val resultResponse = _resultResponse

    private val _weatherResponse = MutableLiveData<WeatherAPIResponse>()
    val weatherResponse = _weatherResponse

    var temperature: Double = 0.0

    fun makeWeatherRequest(cityName: String) {
        _resultResponse.value = ResultState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getWeather(dispatcher, cityName)
                _resultResponse.value = ResultState.Success(response)
                _weatherResponse.value = response
                handleTemperature(response.main.temp)
            } catch (e: Exception) {
                _resultResponse.value = ResultState.Error("Unknown Error")
            }
        }
    }
    private fun handleTemperature(temp: Double) {
        temperature = ((temp - 273.15) * 9/5) + 32
        temperature = (temperature * 100.0).roundToInt() / 100.0
    }
}