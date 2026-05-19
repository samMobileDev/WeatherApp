package com.example.weatherapp

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.HomeFragmentBinding
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar
import kotlin.math.roundToInt
class HomeFragment: Fragment(R.layout.home_fragment) {

    private var _binding: HomeFragmentBinding? = null
    private lateinit var apiService: ApiService

    private fun initRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    private val binding get() = _binding!!
    private fun loadWeather() {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)


        val city = requireContext().getSharedPreferences("WeatherPrefs", 0)
            .getString("city", "New York") ?: "New York"




        lifecycleScope.launch {
            try {
                val getFun = apiService.getWeather(
                    city = city,
                    apiKey = "2e672b2455dcf73be646a3b6408c2247"
                )
                if (getFun.isSuccessful) {
                    val weatherData = getFun.body()
                    if (weatherData != null) {
                        binding.imageWeather.post {

                            binding.imageWeather.alpha = 0f

                            binding.imageWeather.animate()
                                .alpha(1f)
                                .setDuration(800)
                                .start()
                        }

                        binding.Temp.post {

                            binding.Temp.alpha = 0f

                            binding.Temp.animate()
                                .alpha(1f)
                                .setDuration(800)
                                .start()
                        }

                        binding.city.post {

                            binding.city.alpha = 0f

                            binding.city.animate()
                                .alpha(1f)
                                .setDuration(800)
                                .start()
                        }

                        binding.Description.post {
                            binding.Description.alpha = 0f

                            binding.Description.animate()
                                .alpha(1f)
                                .setDuration(800)
                                .start()


                        }

                        binding.Humidity.post {

                            binding.Humidity.alpha = 0f

                            binding.Humidity.animate()
                                .alpha(1f)
                                .setDuration(800)
                                .start()
                        }

                        binding.recyclerViewHome.post {

                            binding.imageWeather.alpha = 0f

                            binding.imageWeather.animate()
                                .alpha(1f)
                                .setDuration(800)
                                .start()
                        }

                        binding.framelayout.post {
                            binding.framelayout.alpha = 0f
                            binding.framelayout.animate()
                                .alpha(1f)
                                .setDuration(800)
                                .start()
                        }




                        binding.city.text = weatherData.name
                        binding.Temp.text = "${weatherData.main.temp.roundToInt()}°"
                        binding.Description.text = weatherData.weather[0].description
                        binding.Humidity.text = weatherData.main.humidity.toString()
                    }

                    val icon = weatherData?.weather[0]?.icon
                    val isNight = icon?.contains("n")
                    val weatherMain = weatherData?.weather[0]?.main


                    when (weatherMain) {
                        "Clear" -> {
                            if (isNight == true) {
                                binding.imageWeather.setImageResource(R.drawable.moon_icon_home)
                                binding.root.setBackgroundResource(R.drawable.night_color)
                            } else {
                                binding.imageWeather.setImageResource(R.mipmap.sun_icon_home)
                                binding.root.setBackgroundResource(R.drawable.sun_color)
                            }
                        }

                        "Clouds" -> {
                            if (isNight == true) {
                                binding.imageWeather.setImageResource(R.drawable.nightcloud_icon_home)
                                binding.root.setBackgroundResource(R.drawable.night_color)
                            } else {
                                binding.imageWeather.setImageResource(R.mipmap.cloud_icon_home)
                                binding.root.setBackgroundResource(R.drawable.cloud_color)

                            }
                        }

                        "Rain" -> {
                            if (isNight == true) {
                                binding.imageWeather.setImageResource(R.drawable.nightrain_icon_home)
                                binding.root.setBackgroundResource(R.drawable.night_color)
                            } else {
                                binding.imageWeather.setImageResource(R.mipmap.rain_icon_home)
                                binding.root.setBackgroundResource(R.drawable.rain_color)
                            }
                        }

                        "Snow" -> {
                            if (isNight == true) {
                                binding.imageWeather.setImageResource(R.drawable.nightsnow_icon_home)
                                binding.root.setBackgroundResource(R.drawable.night_color)

                            } else {
                                binding.imageWeather.setImageResource(R.mipmap.snow_icon_home)
                                binding.root.setBackgroundResource(R.drawable.snow_color)
                            }
                        }

                        "Thunderstorm" -> {
                            if (isNight == true) {
                                binding.imageWeather.setImageResource(R.drawable.nightthunder_icon_home)
                                binding.root.setBackgroundResource(R.drawable.night_color)
                            } else {
                                binding.imageWeather.setImageResource(R.mipmap.thunder_icon_home)
                                binding.root.setBackgroundResource(R.drawable.thunder_color)
                            }
                        }


                    }
                }

            } catch (e: Exception) {
                when (e) {
                    is java.net.UnknownHostException -> {
                        val image = binding.imageWeather
                        val parmas = image.layoutParams
                        parmas.height = 500
                        parmas.width = 500
                        binding.imageWeather.layoutParams = parmas

                        binding.city.text = "No internet connection"
                        binding.Temp.text = ""
                        binding.Description.text = ""
                        binding.Humidity.text = ""
                        binding.imageWeather.setImageResource(R.mipmap.nointernet)
                        binding.recyclerViewHome.visibility = View.GONE
                        binding.framelayout.visibility = View.GONE


                    }

                }
            }
        }
    }

    private fun groupForecast(list: List<Forecast>): List<Forecast> {

        val result = mutableListOf<Forecast>()
        val seenDays = mutableSetOf<String>()

        for (item in list) {

            val day = item.dt_txt.split(" ")[0]

            if (!seenDays.contains(day)) {
                seenDays.add(day)
                result.add(item)
            }
        }

        return result
    }

    private fun loadForecast() {
        val city = requireContext()
            .getSharedPreferences("WeatherPrefs", 0)
            .getString("city", "New York") ?: "New York"
        lifecycleScope.launch {
            try {
                val getForcast = apiService.getForecast(
                    city = city,
                    apiKey = "2e672b2455dcf73be646a3b6408c2247"
                )
                if (getForcast.isSuccessful) {
                    val forecastData = getForcast.body()
                    if (forecastData != null) {
                        val filteredList = groupForecast(forecastData.list)
                        val adapter = SecondAdapter(filteredList)
                        binding.recyclerViewHome.layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL, false
                        )
                        binding.recyclerViewHome.adapter = adapter
                    }


                }
            } catch (e: Exception) {
            }
        }
    }


            override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)
                _binding = HomeFragmentBinding.bind(view)
                loadWeather()
                loadForecast()
                initRetrofit()
            }

        }


