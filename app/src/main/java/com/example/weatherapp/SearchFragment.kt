package com.example.weatherapp

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.databinding.SearchFragmentBinding
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.math.roundToInt
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*

class SearchFragment : Fragment(R.layout.search_fragment) {

    private var weatherData: WeatherData? = null
    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!
    private fun getCityTime(timezone: Int): String {
        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

        utc.add(Calendar.SECOND, timezone)

        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(utc.time)
    }

    private lateinit var apiService: ApiService

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = SearchFragmentBinding.bind(view)

        initRetrofit()
        setupCityDropdown()
        setupListeners()
    }


    private fun initRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    private fun setupListeners() {

        binding.searchButton.setOnClickListener {

            binding.forecastTitle.post {
                binding.forecastTitle.alpha = 0f

                binding.forecastTitle.animate()
                    .alpha(1f)
                    .setDuration(800)
                    .start()
            }

            binding.CardView.post {
                binding.CardView.alpha = 0f

                binding.CardView.animate()
                    .alpha(1f)
                    .setDuration(800)
                    .start()
            }
            binding.recyclerViewSearch.post {
                binding.recyclerViewSearch.alpha = 0f

                binding.recyclerViewSearch.animate()
                    .alpha(1f)
                    .setDuration(800)
                    .start()
            }
            binding.addBtn.post {
                binding.addBtn.alpha = 0f

                binding.addBtn.animate()
                    .alpha(1f)
                    .setDuration(800)
                    .start()
            }

            val city = binding.cityInput.text.toString().trim()

            if (city.isEmpty()) return@setOnClickListener

            searchWeather(city)
            loadForecast(city)
        }

        binding.addBtn.setOnClickListener {

            val cityName = weatherData?.name ?: return@setOnClickListener

            val prefs = requireContext()
                .getSharedPreferences("WeatherPrefs", 0)

            prefs.edit()
                .putString("city", cityName)
                .apply()

            requireActivity().supportFragmentManager.popBackStack()

            animateAddButton()
        }
    }




    private fun searchWeather(city: String) {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            binding.textinputError.visibility = View.GONE
            binding.addBtn.apply {
                isEnabled = true
                text =  "ADD CITY"
                setIconResource(R.drawable.home)

            }


            val cleanCity = city.trim()

            if (cleanCity.isEmpty()) {
                binding.progressBar.visibility = View.GONE
                binding.cityInput.error = "Please enter a city"
                return@launch
            }
            if (cleanCity.length < 2) {
                binding.progressBar.visibility = View.GONE
                binding.cityInput.error = "Too short city name"
                return@launch
            }
            if (cleanCity.length > 30) {
                binding.progressBar.visibility = View.GONE
                binding.cityInput.error = "Too long city name"
                return@launch
            }
            if (cleanCity.contains("  ")) {
                binding.progressBar.visibility = View.GONE
                binding.cityInput.error = "City name cannot contain spaces"
                return@launch
            }
            if (cleanCity.all { it.isDigit() }) {
                binding.textinputError.visibility = View.GONE
                binding.cityInput.error = "City name cannot be a number"
                return@launch
            }


            try {
                val response = apiService.getWeather(
                    city = cleanCity,
                    apiKey = "2e672b2455dcf73be646a3b6408c2247"
                )
                if (response.isSuccessful) {
                    binding.progressBar.visibility = View.GONE
                    binding.forecastTitle.visibility = View.VISIBLE

                    val weather = response.body() ?: return@launch
                    weatherData = weather

                    showWeather(weather)
                    updateIcon(weather)
                    showViews()
                } else {
                    binding.progressBar.visibility = View.GONE
                    binding.textinputError.text = "Something went wrong"
                    binding.forecastTitle.visibility = View.GONE

                    when (response.code()) {
                        404 -> binding.cityInput.error = "City not found"
                        429 -> binding.cityInput.error = "Too many requests"
                        500 -> binding.cityInput.error = "Server error"
                        401 -> binding.cityInput.error = "Invalid API key"
                        else -> binding.cityInput.error = "Unknown error"
                    }
                }

            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                when (e) {
                    is UnknownHostException -> {
                        binding.textinputError.text =
                            "No internet connection"
                    }

                    is SocketTimeoutException -> {
                        binding.textinputError.text =
                            "Connection timed out"
                    }
                    is IOException -> {
                        binding.textinputError.text =
                            "Network error"
                    }
                    else -> {
                        binding.textinputError.text =
                            "Unexpected error"
                    }


                }
            }
        }
    }

    private fun loadForecast(city: String) {
        lifecycleScope.launch {
            try {
                val response = apiService.getForecast(
                    city = city,
                    apiKey = "2e672b2455dcf73be646a3b6408c2247"
                )

                if (response.isSuccessful) {
                    binding.cityTime.text = getCityTime(weatherData!!.timezone)
                    val forecast = response.body() ?: return@launch

                    val filtered = groupForecast(forecast.list)

                    binding.recyclerViewSearch.layoutManager =
                        LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )

                    binding.recyclerViewSearch.adapter = Adapter(filtered)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    private fun showWeather(weather: WeatherData) {
        binding.cityName.text = weather.name
        binding.cityTemp.text = "${weather.main.temp.roundToInt()}°"
        binding.cityDescription.text = weather.weather[0].description
    }

    private fun showViews() {
        binding.textinputError.visibility = View.GONE
        binding.recyclerViewSearch.visibility = View.VISIBLE
        binding.addBtn.visibility = View.VISIBLE
        binding.CardView.visibility = View.VISIBLE
    }

    private fun updateIcon(weather: WeatherData) {
        val icon = weather.weather[0].icon
        val isNight = icon.contains("n")
        val main = weather.weather[0].main

        when (main) {

            "Clear" -> {
                binding.icons.setImageResource(
                    if (isNight) R.mipmap.moon_icon_search
                    else R.mipmap.sun_icon_search
                )
            }

            "Clouds" -> {
                binding.icons.setImageResource(
                    if (isNight) R.mipmap.nightcloud_icon_search
                    else R.mipmap.cloud_icon_search
                )
            }


            "Rain" -> {
                binding.icons.setImageResource(
                    if (isNight) R.mipmap.nightrain_icon_search
                    else R.mipmap.rain_icon_search
                )
            }

            "Snow" -> {
                binding.icons.setImageResource(
                    if (isNight) R.mipmap.nightsnow_icon_search
                    else R.mipmap.snow_icon_search
                )
            }

            "Thunderstorm" -> {
                binding.icons.setImageResource(
                    if (isNight) R.mipmap.nightthunder_icon_search
                    else R.mipmap.thunder_icon_search
                )
            }
        }
    }

    private fun animateAddButton() {
        binding.addBtn.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(80)
            .withEndAction {
                binding.addBtn.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(80)
                    .start()

                binding.addBtn.apply {
                    isEnabled = false
                    text = "Added"
                    setIconResource(R.mipmap.chekmark)
                }
            }
            .start()
    }



    private fun setupCityDropdown() {
        val cityList = listOf(
            // Uzbekistan
            "Tashkent", "Samarkand", "Bukhara", "Andijan", "Namangan",
            "Fergana", "Nukus", "Khiva", "Jizzakh", "Termiz",
            "Navoiy", "Gulistan", "Urgench",

            // South Korea
            "Seoul", "Busan", "Incheon", "Daegu", "Daejeon",
            "Gwangju", "Suwon", "Ulsan", "Changwon", "Goyang",

            // Japan
            "Tokyo", "Osaka", "Kyoto", "Nagoya", "Sapporo",
            "Fukuoka", "Yokohama", "Kobe", "Hiroshima", "Sendai",

            // China
            "Beijing", "Shanghai", "Shenzhen", "Guangzhou", "Hong Kong",
            "Chengdu", "Wuhan", "Xi'an", "Hangzhou", "Nanjing",

            // Southeast Asia
            "Bangkok", "Phuket", "Chiang Mai", "Pattaya",
            "Singapore", "Kuala Lumpur", "Jakarta", "Bali", "Surabaya",
            "Manila", "Cebu", "Hanoi", "Ho Chi Minh City", "Da Nang",

            // South Asia
            "Delhi", "Mumbai", "Bangalore", "Chennai", "Kolkata",
            "Karachi", "Lahore", "Islamabad", "Dhaka", "Kathmandu",
            "Colombo",

            // Middle East
            "Dubai", "Abu Dhabi", "Doha", "Riyadh", "Jeddah",
            "Kuwait City", "Muscat", "Tehran", "Mashhad", "Tel Aviv",

            // Turkey / Central Asia
            "Istanbul", "Ankara", "Izmir", "Antalya", "Bursa",
            "Almaty", "Astana", "Tashkent", "Bishkek", "Dushanbe",

            // Europe
            "London", "Manchester", "Liverpool", "Birmingham", "Leeds",
            "Paris", "Lyon", "Marseille", "Nice", "Berlin",
            "Munich", "Hamburg", "Frankfurt", "Cologne", "Stuttgart",
            "Madrid", "Barcelona", "Valencia", "Seville", "Lisbon",
            "Rome", "Milan", "Naples", "Florence", "Venice",
            "Amsterdam", "Rotterdam", "Brussels", "Vienna", "Prague",
            "Budapest", "Warsaw", "Athens", "Stockholm", "Oslo",

            // Russia / CIS
            "Moscow", "Saint Petersburg", "Kazan", "Sochi",
            "Kyiv", "Lviv", "Minsk",

            // USA
            "New York", "Los Angeles", "Chicago", "Houston", "Miami",
            "San Francisco", "Seattle", "Boston", "Washington", "Dallas",
            "Las Vegas", "Atlanta", "Philadelphia",

            // Canada
            "Toronto", "Vancouver", "Montreal", "Ottawa", "Calgary",

            // Latin America
            "Mexico City", "Cancun", "Havana", "Rio de Janeiro",
            "Sao Paulo", "Buenos Aires", "Santiago", "Lima",

            // Africa
            "Cairo", "Alexandria", "Casablanca", "Marrakesh",
            "Nairobi", "Lagos", "Johannesburg", "Cape Town",


            // Oceania
            "Sydney", "Melbourne", "Brisbane", "Perth", "Adelaide",
            "Auckland", "Wellington"
        )


        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            cityList
        )

        binding.cityInput.setAdapter(adapter)
    }



    private fun groupForecast(list: List<Forecast>): List<Forecast> {
        val result = mutableListOf<Forecast>()
        val seenDays = mutableSetOf<String>()

        for (item in list) {
            val day = item.dt_txt.split(" ")[0]

            if (day !in seenDays) {
                seenDays.add(day)
                result.add(item)
            }
        }

        return result
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
