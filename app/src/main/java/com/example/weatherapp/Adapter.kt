package com.example.weatherapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ItemBinding
import com.example.weatherapp.databinding.SecondItemBinding
import java.text.SimpleDateFormat
import java.util.Locale


class Adapter(private val forecastList: List<Forecast>) : RecyclerView.Adapter<Adapter.ViewHolder>() {
    class ViewHolder(val binding: ItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = forecastList[position]
        holder.binding.dayText.text = getDayName(item.dt_txt)
        holder.binding.tempText.text = "${item.main.temp.toInt()}°"

        when(item.weather[0].main){
            "Clear" -> {
                holder.binding.icon7days.setImageResource(R.mipmap.sun_icon_search) }
            "Clouds" -> {
                holder.binding.icon7days.setImageResource(R.mipmap.cloud_icon_search)}
            "Rain" -> {
                holder.binding.icon7days.setImageResource(R.mipmap.rain_icon_search)}
            "Snow" -> {
                holder.binding.icon7days.setImageResource(R.mipmap.snow_icon_search)}
            "Thunderstorm" -> {
                holder.binding.icon7days.setImageResource(R.mipmap.thunder_icon_search)}
        }
    }

    override fun getItemCount(): Int = forecastList.size

    private fun getDayName(dateStr: String): String {
        return try {
            val input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = input.parse(dateStr)
            val output = SimpleDateFormat("EEE", Locale.getDefault())
            output.format(date!!)
        } catch (e: Exception) {
            ""
        }
    }
}

class SecondAdapter(private val forecastList: List<Forecast>) : RecyclerView.Adapter<SecondAdapter.ViewHolder>() {

    class ViewHolder(val binding: SecondItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SecondItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = forecastList[position]
        holder.binding.dayTexts.text = getDayName(item.dt_txt)
        holder.binding.tempTexts.text = "${item.main.temp.toInt()}°"

        when (item.weather[0].main) {
            "Clear" -> {
                holder.binding.icon7days.setImageResource(R.mipmap.sun_icon_search)
            }

            "Clouds" -> {
                holder.binding.icon7days.setImageResource(R.mipmap.cloud_icon_search)
            }

            "Rain" -> {
                holder.binding.icon7days.setImageResource(R.mipmap.rain_icon_search)
            }

            "Snow" -> {
                holder.binding.icon7days.setImageResource(R.mipmap.snow_icon_search)
            }

            "Thunderstorm" -> {
                holder.binding.icon7days.setImageResource(R.mipmap.thunder_icon_search)
            }
        }
    }

    override fun getItemCount(): Int = forecastList.size

    private fun getDayName(dateStr: String): String {
        return try {
            val input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = input.parse(dateStr)
            val output = SimpleDateFormat("EEE", Locale.getDefault())
            output.format(date!!)
        } catch (e: Exception) {
            ""
        }
    }
}

