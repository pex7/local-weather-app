package com.example.localweatherapp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localweatherapp.model.CurrentWeather
import com.example.localweatherapp.model.HourlyWeather
import com.example.localweatherapp.model.WeatherByType
import com.example.localweatherapp.model.WeatherIcons
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class WeatherViewModel(private val locationProvider: LocationProvider): ViewModel() {
    private var openWeatherApiKey = BuildConfig.OPEN_WEATHER_API_KEY

    var dateString: MutableLiveData<String> = MutableLiveData("")
    var cityName: MutableLiveData<String> = MutableLiveData("")
    var weatherDescription: MutableLiveData<String> = MutableLiveData("")
    var tempString: MutableLiveData<String> = MutableLiveData("")
    var unit: MutableLiveData<String> = MutableLiveData("")
    var weatherIcon: MutableLiveData<Int> = MutableLiveData()
    var weatherList: MutableLiveData<List<HourlyWeather>> = MutableLiveData()

    fun getLocation() {
        locationProvider.addOnSuccessListener { location ->
            val lat = location.latitude
            val lon = location.longitude

            val date = Date(location.time)
            val dateFormat = SimpleDateFormat("EEE, MMMM d")
            dateString.value = dateFormat.format(date)

            viewModelScope.launch(Dispatchers.IO) {
                if (lat == null || lon == null) {
                    return@launch
                }

                try {
                    val currentWeatherResponse = WeatherApiAdapter.weatherApi.getCurrentWeather(
                        lat = lat,
                        lon = lon,
                        exclude = "minutely",
                        appid = openWeatherApiKey
                    )

                    setWeatherData(currentWeatherResponse)
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

                    setWeatherListData(weatherResponse)
                } catch (e: Exception) {
                    Log.e("Weather", e.toString())
                }
            }
        }
    }

    private suspend fun setWeatherData(currentWeatherResponse: Response<CurrentWeather>) {
        if (currentWeatherResponse.isSuccessful && currentWeatherResponse.body() != null) {
            val data = currentWeatherResponse.body()
            withContext(Dispatchers.Main) {
                cityName.value = data?.name
                weatherDescription.value = data?.weather?.get(0)?.description
                val iconId = data?.weather?.get(0)?.id
                val icon = data?.weather?.get(0)?.icon

                Log.i("Weather", "weather data: $data")

                if (iconId != null && icon != null) {
                    val iconDrawable = WeatherIcons.getIcon(iconId, icon).drawable
                    weatherIcon.value = iconDrawable
                }

                val temp = data?.main?.temp

                if (temp != null) {
                    val tempInFahrenheit = kelvinToFahrenheit(temp)
                    tempString.value = tempInFahrenheit
                  unit.value = "°F"
                }
            }
        }
    }

    private suspend fun setWeatherListData(weatherResponse: Response<WeatherByType>) {
        if (weatherResponse.isSuccessful && weatherResponse.body() != null) {
            val weatherResponseData = weatherResponse.body()
            val hourlyWeather = weatherResponseData?.hourly?.take(5)
            withContext(Dispatchers.Main) {
                weatherList.value = hourlyWeather
            }
            Log.i("Weather", "weather response: $weatherResponseData")
            Log.i("Weather", "hourly weather response: $hourlyWeather")
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("Weather", "WeatherViewModel cleared!")
    }
}