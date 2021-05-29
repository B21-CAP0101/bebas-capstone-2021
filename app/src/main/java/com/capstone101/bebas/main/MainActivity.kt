package com.capstone101.bebas.main

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.capstone101.bebas.R
import com.capstone101.bebas.databinding.ActivityMainBinding
import org.koin.android.ext.android.inject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private val bind get() = binding!!

    private var granted: Int = 0
    private lateinit var permissions: Array<String>

    companion object {
        const val PERMISSION_CODE = 123456
        const val ACTION_RECORD = "com.capstone101.bebas.home.recordingStart"

        @Volatile
        var count = 0
    }

    private val viewModel: MainViewModel by inject()
    private lateinit var data: String
    private var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        permissionCheck()

        bind.recordMain.setOnClickListener {
            count++
            when (count) {
                3 -> {
                    Toast.makeText(applicationContext, "Berhasil", Toast.LENGTH_SHORT).show()
                    recording()
                }
                2 -> Toast.makeText(applicationContext, "Tekan sekali lagi", Toast.LENGTH_SHORT)
                    .show()
                1 -> Toast.makeText(applicationContext, "Tekan dua kali lagi", Toast.LENGTH_SHORT)
                    .show()
            }
            Handler(Looper.getMainLooper()).postDelayed({ count = 0 }, 3000)
        }

        if (intent.action == ACTION_RECORD) recording()

        viewModel.getUser.observe(this) {
            // TODO: BUAT USER PROFILE
        }
    }

    private fun permissionCheck() {
        granted = PackageManager.PERMISSION_GRANTED
        permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO
        )
        if (ActivityCompat.checkSelfPermission(
                this, permissions[0]
            ) != granted && ActivityCompat.checkSelfPermission(
                this, permissions[1]
            ) != granted && ActivityCompat.checkSelfPermission(
                this, permissions[2]
            ) != granted
        ) ActivityCompat.requestPermissions(this, permissions, PERMISSION_CODE)
    }

    private fun checkFolder() {
        val folder = File(externalCacheDir?.absolutePath!!)
        val date = Date()
        val formatter = SimpleDateFormat("dd-MM-yyyy-HH-mm", Locale.getDefault())
        val fileName = "${formatter.format(date)}.mp3"

        data = "$folder/$fileName"

        if (!folder.exists()) folder.mkdir()
    }

    private fun recording() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        isRecording = true
        bind.recordMain.isEnabled = false
        checkFolder()

        bind.recordMain.setImageResource(R.drawable.recording)
        val recording = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(data)
            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
            try {
                prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            start()
        }
        Handler(Looper.getMainLooper()).postDelayed({
            recording.apply {
                stop()
                release()
            }
            bind.recordMain.isEnabled = true
            count = 0
            bind.recordMain.setImageResource(R.drawable.record)
        }, 10000)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != PERMISSION_CODE) {
            if (ActivityCompat.checkSelfPermission(
                    this, permissions[0]
                ) != granted
            )
                ActivityCompat.requestPermissions(
                    this, arrayOf(permissions[0], permissions[1]), PERMISSION_CODE
                )
            if (ActivityCompat.checkSelfPermission(
                    this, permissions[2]
                ) != granted
            )
                ActivityCompat.requestPermissions(
                    this, arrayOf(permissions[2]), PERMISSION_CODE
                )
        }
    }

    override fun onBackPressed() {
        if (!isRecording) super.onBackPressed()
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}