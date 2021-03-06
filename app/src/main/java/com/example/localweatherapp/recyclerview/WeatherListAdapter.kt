package com.example.localweatherapp.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.localweatherapp.R
import com.example.localweatherapp.databinding.WeatherListDailyTabBinding
import com.example.localweatherapp.databinding.WeatherListHourlyTabBinding
import com.example.localweatherapp.model.HourWeather
import com.example.localweatherapp.model.WeatherModel
import java.util.*

class WeatherListAdapter : ListAdapter<WeatherModel, RecyclerView.ViewHolder>(diffUtil) {
    companion object {
        private val diffUtil = object: DiffUtil.ItemCallback<WeatherModel>() {
            override fun areItemsTheSame(oldItem: WeatherModel, newItem: WeatherModel) = true

            override fun areContentsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
                return false
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is HourWeather -> R.layout.weather_list_hourly_tab
            else -> R.layout.weather_list_daily_tab
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when(viewType) {
            is R.layout.weather_list_hourly_tab -> {
                val binding = WeatherListHourlyTabBinding.inflate(inflater, parent, false)
                HourWeatherViewHolder(binding)
            }
            else -> {
                val binding = WeatherListDailyTabBinding.inflate(inflater, parent, false)
                DayWeatherViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        holder.onBind(item)
    }

    class HourWeatherViewHolder(
        private val binding: WeatherListHourlyTabBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(model: WeatherModel) {

        }
    }

    class DayWeatherViewHolder(
        private val binding: WeatherListDailyTabBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(model: WeatherModel) {

        }
    }

//    class WeatherViewHolder(
//        private val binding: ItemWeatherInfoBinding
//    ) : RecyclerView.ViewHolder(binding.root) {
//        fun onBind(weatherData: WeatherModel) {
//            val iconId = weatherData.weather?.get(0)?.id
//            val icon = weatherData.weather?.get(0)?.icon
//            val iconDrawable = WeatherIcons.getIcon(iconId, icon).drawable
//            val date = Date((weatherData.dt * 1000).toLong())
//            val dateFormat = SimpleDateFormat("H:mm")
//            val tempInFahrenheit = kelvinToFahrenheit(weatherData.temp)
//
//            binding.apply {
//                dateTime.text = dateFormat.format(date)
//                dateWeatherIcon.setImageResource(iconDrawable)
//                dateTemp.text = tempInFahrenheit
//            }
//        }
//    }
}