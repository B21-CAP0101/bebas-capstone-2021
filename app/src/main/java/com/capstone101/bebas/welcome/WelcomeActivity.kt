package com.capstone101.bebas.welcome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone101.bebas.R
import com.capstone101.bebas.databinding.ActivityWelcomeBinding
import com.capstone101.bebas.home.HomeActivity
import com.capstone101.core.utils.SessionManager
import org.koin.android.ext.android.inject

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    private val session: SessionManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_BeBaS)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        println(session.isLogin.toString() + " TEST")
        if (!session.isLogin) setContentView(binding.root)
        else {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}