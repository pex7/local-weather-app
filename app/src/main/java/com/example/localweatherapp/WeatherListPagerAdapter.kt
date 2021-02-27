package com.example.localweatherapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class WeatherListPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val tabCount: Int
) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = tabCount

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> HourlyWeatherFragment()
            else -> DailyWeatherFragment()
        }
}