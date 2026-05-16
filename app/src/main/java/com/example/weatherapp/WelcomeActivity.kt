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

        binding.relative.post {
            binding.relative.alpha = 0f
            binding.relative.animate()
                .alpha(1f)
                .setDuration(1000)
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

