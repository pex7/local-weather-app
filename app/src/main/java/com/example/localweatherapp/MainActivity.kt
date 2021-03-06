package com.example.localweatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.localweatherapp.databinding.ActivityMainBinding
import com.example.localweatherapp.recyclerview.WeatherListAdapter
import com.google.android.gms.location.*
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
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
            TabLayoutMediator(weatherListTabLayout, weatherListViewPager) { _, _ -> }.attach()
        }

        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

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
            weatherListAdapter.submitList(it)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.i("Weather", requestCode.toString())
    }

    private fun hasPermission(): Boolean =
        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED

}