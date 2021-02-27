package com.example.localweatherapp

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.localweatherapp.model.WeatherIcons
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class WeatherViewModel(application: Application): ViewModel() {
    private var openWeatherApiKey = BuildConfig.OPEN_WEATHER_API_KEY
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient()

    fun getLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            Log.i("Weather", "location: $location")
            val lat = location?.latitude
            val lon = location?.longitude

            location?.time?.let {
                val date = Date(it)
                val dateFormat = SimpleDateFormat("EEE, MMMM d")
                binding.date.text = dateFormat.format(date)
            }

            GlobalScope.launch(Dispatchers.IO) {
                if (lat != null && lon != null) {
                    try {
                        val currentWeatherResponse = WeatherApiAdapter.weatherApi.getCurrentWeather(
                            lat = lat,
                            lon = lon,
                            exclude = "minutely",
                            appid = openWeatherApiKey
                        )

                        if (currentWeatherResponse.isSuccessful && currentWeatherResponse.body() != null) {
                            val data = currentWeatherResponse.body()
                            withContext(Dispatchers.Main) {
                                binding.cityName.text = data?.name
                                binding.weatherDescription.text = data?.weather?.get(0)?.description
                                val iconId = data?.weather?.get(0)?.id
                                val icon = data?.weather?.get(0)?.icon

                                Log.i("Weather", "weather data: $data")

                                if (iconId != null && icon != null) {
                                    val iconDrawable = WeatherIcons.getIcon(iconId, icon).drawable
                                    binding.weatherIcon.setImageResource(iconDrawable)
                                }

                                val temp = data?.main?.temp

                                if (temp != null) {
                                    val tempInFahrenheit = kelvinToFahrenheit(temp)
                                    binding.temp.text = tempInFahrenheit
                                    binding.unit.text = getString(R.string.fahrenheit)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("Weather", e.toString())
                    }

                    try {
                        val weatherResponse = WeatherApiAdapter.weatherApi.getWeatherByType(
                            lat = lat,
                            lon = lon,
                            exclude = "minutely",
                            appid = openWeatherApiKey
                        )

                        if (weatherResponse.isSuccessful && weatherResponse.body() != null) {
                            val weatherResponseData = weatherResponse.body()
                            val hourlyWeather = weatherResponseData?.hourly?.take(5)
                            withContext(Dispatchers.Main) {
                                weatherListAdapter.submitList(hourlyWeather)
                            }
                            Log.i("Weather", "weather response: $weatherResponseData")
                            Log.i("Weather", "hourly weather response: $hourlyWeather")
                        }
                    } catch (e: Exception) {
                        Log.e("Weather", e.toString())
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.i("Weather", requestCode.toString())
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("Weather", "WeatherViewModel cleared!")
    }
}