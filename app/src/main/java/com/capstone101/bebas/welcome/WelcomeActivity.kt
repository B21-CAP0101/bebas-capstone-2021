package com.capstone101.bebas.welcome

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone101.bebas.R
import com.capstone101.bebas.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_BeBaS)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}