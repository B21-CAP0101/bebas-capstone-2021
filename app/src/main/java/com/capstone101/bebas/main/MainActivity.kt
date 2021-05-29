package com.capstone101.bebas.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone101.bebas.R
import com.capstone101.bebas.databinding.ActivityMainBinding
import com.capstone101.bebas.databinding.ActivityWelcomeBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_BeBaS)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}