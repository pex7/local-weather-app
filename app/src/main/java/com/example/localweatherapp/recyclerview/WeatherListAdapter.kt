package com.example.localweatherapp.recyclerview

import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.localweatherapp.databinding.ItemWeatherInfoBinding
import com.example.localweatherapp.kelvinToFahrenheit
import com.example.localweatherapp.model.HourlyWeather
import com.example.localweatherapp.model.WeatherIcons
import java.text.SimpleDateFormat
import java.util.*

class WeatherListAdapter : ListAdapter<HourlyWeather, WeatherListAdapter.WeatherViewHolder>(diffUtil) {
    companion object {
        private val diffUtil = object: DiffUtil.ItemCallback<HourlyWeather>() {
            override fun areItemsTheSame(oldItem: HourlyWeather, newItem: HourlyWeather) = true

            override fun areContentsTheSame(oldItem: HourlyWeather, newItem: HourlyWeather): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWeatherInfoBinding.inflate(inflater)
        return WeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val item = getItem(position)
        holder.onBind(item)
    }

    class WeatherViewHolder(
        private val binding: ItemWeatherInfoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(weatherData: HourlyWeather) {
            val iconId = weatherData.weather?.get(0)?.id
            val icon = weatherData.weather?.get(0)?.icon
            val iconDrawable = WeatherIcons.getIcon(iconId, icon).drawable
            val date = Date((weatherData.dt * 1000).toLong())
            val dateFormat = SimpleDateFormat("H:mm")
            val tempInFahrenheit = kelvinToFahrenheit(weatherData.temp)

            binding.apply {
                dateTime.text = dateFormat.format(date)
                dateWeatherIcon.setImageResource(iconDrawable)
                dateTemp.text = tempInFahrenheit
            }
        }
    }
}