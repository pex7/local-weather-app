package com.example.localweatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.localweatherapp.databinding.ActivityMainBinding
import com.example.localweatherapp.model.WeatherModel
import com.example.localweatherapp.recyclerview.WeatherListAdapter
import com.google.android.gms.location.*
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val weatherListAdapter = WeatherListAdapter()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            var weatherListPagerAdapter = WeatherListPagerAdapter(
                this@MainActivity,
                2
            )
            weatherListViewPager.adapter = weatherListPagerAdapter
            TabLayoutMediator(weatherListTabLayout, weatherListViewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.today)
                    else -> tab.text = getString(R.string.weekly)
                }
            }.attach()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        Log.i("Weather Permissions", (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED).toString());

        val weatherViewModel: WeatherViewModel by lazy {
            ViewModelProvider(
                this,
                createWithFactory {
                    WeatherViewModel(LocationProvider(fusedLocationClient, this))
                }
            ).get(WeatherViewModel::class.java)
        }

        weatherViewModel.dateString.observe(this) {
            binding.date.text = it
        }

        weatherViewModel.cityName.observe(this) {
            binding.cityName.text = it
        }

        weatherViewModel.weatherDescription.observe(this) {
            binding.weatherDescription.text = it
        }

        weatherViewModel.tempString.observe(this) {
            binding.temp.text = it
        }

        weatherViewModel.weatherIcon.observe(this) {
            binding.weatherIcon.setImageResource(it)
        }

        weatherViewModel.unit.observe(this) {
            binding.unit.text = it
        }

        weatherViewModel.weatherList.observe(this) {
            weatherListAdapter.submitList(it as List<WeatherModel>)
        }

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
    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        Log.i("Weather", requestCode.toString())
//    }

    private fun hasPermission(): Boolean =
        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED

    private fun createWithFactory(
        create: () -> ViewModel
    ): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")// Casting T as ViewModel
                return create.invoke() as T
            }
        }
    }
}