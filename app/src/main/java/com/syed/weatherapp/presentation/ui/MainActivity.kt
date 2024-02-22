package com.syed.weatherapp.presentation.ui

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.syed.weatherapp.R
import com.syed.weatherapp.data.api.WeatherApiClient
import com.syed.weatherapp.data.model.Main
import com.syed.weatherapp.data.model.Weather
import com.syed.weatherapp.data.model.WeatherAPIResponse
import com.syed.weatherapp.data.repository.WeatherRepositoryImpl
import com.syed.weatherapp.data.util.ResultState
import com.syed.weatherapp.presentation.ui.theme.WeatherAppTheme
import com.syed.weatherapp.presentation.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: WeatherViewModel by viewModels<WeatherViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherNavHost(
                        viewModel = viewModel,
                        startDestination = "search-view"
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String,
    viewModel: WeatherViewModel
) {
    val inputCity = remember { mutableStateOf("") }
    inputCity.value = LocalContext.current
        .getSharedPreferences("appSharedPres", Context.MODE_PRIVATE)
        .getString("userLastSearch", "").orEmpty()

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(route = "search-view") {
            MainScreen(
                viewModel = viewModel,
                navController = navController,
                inputCity = inputCity
            )
        }
        composable(route = "detail-view") {
            DetailView(
                response = viewModel.weatherResponse.value,
                navController = navController,
                temperature = viewModel.temperature
            )
        }

    }
}

@Composable
fun MainScreen(
    viewModel: WeatherViewModel,
    navController: NavHostController = rememberNavController(),
    inputCity: MutableState<String>
) {
    when (viewModel.resultResponse.value) {
        is ResultState.Error -> {
            Toast.makeText(
                LocalContext.current,
                "Error getting weather data",
                Toast.LENGTH_SHORT
            ).show()
        }

        is ResultState.Loading -> {

        }

        is ResultState.Success -> {
            LocalContext.current
                .getSharedPreferences("appSharedPres", Context.MODE_PRIVATE).edit()
                .putString("userLastSearch", inputCity.value)
                .apply()
            navController.navigate("detail-view")
            viewModel.resultResponse.value = null
        }

        else -> {}
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Image(
            painter = painterResource(id = R.drawable.weathericon),
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
        )

        TextField(
            value = inputCity.value,
            onValueChange = { inputCity.value = it },
            maxLines = 1
        )

        Button(
            onClick = {
                viewModel.makeWeatherRequest(inputCity.value)
            }
        ) {
            Text(text = "Search")
        }

        if (viewModel.resultResponse.value == ResultState.Loading) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun DetailView(
    response: WeatherAPIResponse?,
    navController: NavHostController,
    temperature: Double
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.Top
        ) {
            IconButton(
                onClick = {
                    navController.navigate("search-view")
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = response?.name.orEmpty(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        WeatherDetailsCard(
            details = response?.main,
            weather = response?.weather,
            temperature = temperature,
        )
    }
}

@Composable
fun WeatherDetailsCard(details: Main?, weather: List<Weather>?, temperature: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = "https://openweathermap.org/img/wn/${weather?.get(0)?.icon}@2x.png"
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Temperature: ${temperature}°F",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Humidity: ${details?.humidity}%",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Pressure: ${details?.pressure} hPa",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Max Temperature: ${details?.temp_max}°F",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Description: ${weather?.get(0)?.description}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Text(
                        text = "Feels Like: ${details?.feels_like}°F",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Text(
                        text = "Min Temperature: ${details?.temp_min}°F",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    WeatherAppTheme {
        WeatherNavHost(
            startDestination = "search-view",
            viewModel = WeatherViewModel(WeatherRepositoryImpl(WeatherApiClient()))
        )
    }
}