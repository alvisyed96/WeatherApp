package com.syed.weatherapp.presentation.di

import com.syed.weatherapp.data.api.WeatherApiClient
import com.syed.weatherapp.data.repository.WeatherRepositoryImpl
import com.syed.weatherapp.domain.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object WeatherAppModule {

    @Provides
    fun provideWeatherApiClient(): WeatherApiClient {
        return WeatherApiClient()
    }

    @Provides
    fun provideWeatherRepository(weatherApiClient: WeatherApiClient): WeatherRepository {
        return WeatherRepositoryImpl(weatherApiClient)
    }

    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}