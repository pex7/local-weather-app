package com.example.localweatherapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localweatherapp.databinding.WeatherListBinding
import com.example.localweatherapp.recyclerview.WeatherListAdapter

class HourlyWeatherFragment : Fragment(R.layout.weather_list) {
    private val weatherListAdapter = WeatherListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = WeatherListBinding.inflate(inflater, container, false)

        binding.weatherList.apply {
            adapter = weatherListAdapter
            layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        }

        return binding.root
    }
}