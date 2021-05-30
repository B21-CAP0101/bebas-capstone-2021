package com.capstone101.bebas.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.capstone101.bebas.R
import com.capstone101.bebas.databinding.ActivityMainBinding
import com.capstone101.bebas.main.home.HomeFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        @Volatile
        var isRecording = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_BeBaS)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permission = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO
        )
        if (requestCode != HomeFragment.PERMISSION_CODE) {
            if (ActivityCompat
                    .checkSelfPermission(this, permission[0]) != PackageManager.PERMISSION_GRANTED
            )
                ActivityCompat.requestPermissions(
                    this, arrayOf(permission[0], permission[1]),
                    HomeFragment.PERMISSION_CODE
                )
            if (ActivityCompat
                    .checkSelfPermission(this, permission[2]) != PackageManager.PERMISSION_GRANTED
            )
                ActivityCompat.requestPermissions(
                    this, arrayOf(permission[2]), HomeFragment.PERMISSION_CODE
                )
        }
    }

    override fun onBackPressed() {
        if (!isRecording) super.onBackPressed()
    }
}