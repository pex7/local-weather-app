package com.example.localweatherapp

import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


const val OPEN_WEATHER_API_BASE_URL = "https://api.openweathermap.org/data/2.5/"

val moshi: Moshi = Moshi.Builder().build()

object WeatherApiAdapter {
    val weatherApi: WeatherApi = Retrofit.Builder()
        .baseUrl(OPEN_WEATHER_API_BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(WeatherApi::class.java)
}