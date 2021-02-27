package com.example.localweatherapp.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Weather(
    @Json(name = "id") val id: Int,
    @Json(name = "main") val main: String,
    @Json(name = "description") val description: String,
    @Json(name = "icon") val icon: String
)

@JsonClass(generateAdapter = true)
data class Temp(
    @Json(name = "day") val day: Double,
    @Json(name = "min") val min: Double,
    @Json(name = "max") val max: Double
)

@JsonClass(generateAdapter = true)
data class HourlyWeather(
    @Json(name = "dt") val dt: Int,
    @Json(name = "temp") val temp: Double,
    @Json(name = "weather") val weather: List<Weather>
)

@JsonClass(generateAdapter = true)
data class DailyWeather(
    @Json(name = "dt") val dt: Int,
    @Json(name = "temp") val temp: Temp,
    @Json(name = "weather") val weather: List<Weather>
)

@JsonClass(generateAdapter = true)
data class WeatherByType(
    @Json(name = "hourly") val hourly: List<HourlyWeather>,
    @Json(name = "daily") val daily: List<DailyWeather>
)

@JsonClass(generateAdapter = true)
data class Main(
    @Json(name = "temp") val temp: Double,
)

@JsonClass(generateAdapter = true)
data class CurrentWeather(
    @field:Json(name = "weather") val weather: List<Weather>,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "main") val main: Main,
)
