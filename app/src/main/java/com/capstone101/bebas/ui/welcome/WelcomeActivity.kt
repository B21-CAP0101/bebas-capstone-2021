package com.capstone101.bebas.ui.welcome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone101.bebas.R
import com.capstone101.bebas.databinding.ActivityWelcomeBinding
import com.capstone101.bebas.ui.main.MainActivity
import com.capstone101.core.utils.SessionManager
import org.koin.android.ext.android.inject

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    private val session: SessionManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_BeBaS)
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        if (!session.isLogin) setContentView(binding.root)
        else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}