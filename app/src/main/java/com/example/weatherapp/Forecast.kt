package com.example.weatherapp
import com.example.weatherapp.ForecastResponse

data class ForecastResponse(
    val list: List<Forecast>
)
data class Forecast(
    val dt_txt: String,
    val main: Main,
    val  weather: List<Weather>
)