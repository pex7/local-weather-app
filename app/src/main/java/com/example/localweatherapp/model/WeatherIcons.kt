package com.example.localweatherapp.model

import com.example.localweatherapp.R

enum class WeatherIcons(val drawable: Int) {
    THUNDERSTORM(R.drawable.thunderstorm),
    DRIZZLE(R.drawable.drizzle),
    RAIN(R.drawable.rain),
    SNOW(R.drawable.snow),
    ATMOSPHERE(R.drawable.atmosphere),
    CLEAR_DAY(R.drawable.clear_day),
    CLEAR_NIGHT(R.drawable.clear_night),
    CLOUDS_DAY(R.drawable.cloudy_day),
    CLOUDS_NIGHT(R.drawable.cloudy_night);

    companion object {
        fun getIcon(id: Int, icon: String): WeatherIcons = when(id) {
            in 200..299 -> THUNDERSTORM
            in 300..399 -> DRIZZLE
            in 500..599 -> RAIN
            in 600..699 -> SNOW
            in 700..799 -> ATMOSPHERE
            800 -> getTimeOfDayIcon(icon)
            in 801..804 -> getTimeOfDayIcon(icon)
            else -> CLEAR_DAY
        }

        private fun getTimeOfDayIcon(icon: String): WeatherIcons {
            val iconCode = icon.take(2)
            val timeOfDay = icon.takeLast(1)

            return when (iconCode) {
                "01" -> if (timeOfDay == "d") CLEAR_DAY else CLEAR_NIGHT
                else -> if (timeOfDay == "d") CLOUDS_DAY else CLOUDS_NIGHT
            }
        }
    }
}