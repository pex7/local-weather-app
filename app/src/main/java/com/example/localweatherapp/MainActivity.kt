package com.example.localweatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import com.example.localweatherapp.databinding.ActivityMainBinding
import com.example.localweatherapp.model.WeatherIcons
import com.example.localweatherapp.recyclerview.WeatherListAdapter
import com.google.android.gms.location.*
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var openWeatherApiKey = BuildConfig.OPEN_WEATHER_API_KEY
    private val weatherListAdapter = WeatherListAdapter()
    private lateinit var weatherViewModel: WeatherViewModel

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            var weatherListPagerAdapter = WeatherListPagerAdapter(
                this@MainActivity,
                weatherListTabLayout.tabCount
            )
            weatherListViewPager.adapter = weatherListPagerAdapter
            TabLayoutMediator(weatherListTabLayout, weatherListViewPager) { _, _ ->

            }.attach()
        }

        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (hasPermission()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
            return
        }

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

    private fun hasPermission(): Boolean =
        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED

}