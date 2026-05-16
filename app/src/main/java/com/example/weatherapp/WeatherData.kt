package com.example.weatherapp

data class WeatherData(
    var name: String,
    var main: Main,
    var weather: List<Weather>
)
data class Main(
    var temp: Double,
    var humidity: Int
)
data class Weather(
    var description: String,
var main: String,
    var icon: String
)
