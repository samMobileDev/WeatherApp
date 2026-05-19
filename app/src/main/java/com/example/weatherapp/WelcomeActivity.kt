package com.example.weatherapp


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appIcon.post {
            binding.appIcon.alpha = 0f
            binding.appIcon.animate()
                .alpha(1f)
                .setDuration(1500)
                .start()
        }
        binding.welcomeText.post {
            binding.welcomeText.alpha = 0f
            binding.welcomeText.animate()
                .alpha(1f)
                .setDuration(1500)
                .start()
        }
        binding.subtitle.post {
            binding.subtitle.alpha = 0f
            binding.subtitle.animate()
                .alpha(1f)
                .setDuration(1500)
                .start()
        }
        binding.layout.post {
            binding.layout.alpha = 0f
            binding.layout.animate()
                .alpha(1f)
                .setDuration(1500)
                .start()
        }




        val sharedPreferences = getSharedPreferences("WeatherPrefs", MODE_PRIVATE)

        val isFirstTime = sharedPreferences.getBoolean("isFirstTime", false)
        if (isFirstTime) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.welcomeBtn.setOnClickListener {
            sharedPreferences.edit()
                .putBoolean("isFirstTime", true)
                .apply()
            val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

