package com.example.weatherapp
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService{
     @GET("weather")
     suspend fun getWeather(
         @Query("q")city: String,
         @Query("appid") apiKey: String,
         @Query("units") units: String = "metric"
     ): Response<WeatherData>

    @GET("forecast")
    suspend fun getForecast(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Response<ForecastResponse>
}
