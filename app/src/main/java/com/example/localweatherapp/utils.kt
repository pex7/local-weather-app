package com.example.localweatherapp

fun kelvinToFahrenheit(temp: Double): String = (temp * 9/5 - 459.67).toInt().toString()