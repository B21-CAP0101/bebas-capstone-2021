package com.capstone101.bebas.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.capstone101.bebas.R
import com.capstone101.bebas.databinding.ActivityMainBinding
import com.capstone101.core.domain.model.Danger
import com.capstone101.core.utils.MapVal
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.android.inject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    private var binding: ActivityMainBinding? = null
    private val bind get() = binding!!

    private var granted: Int = 0
    private lateinit var permissions: Array<String>
    private lateinit var manager: LocationManager
    private lateinit var listener: LocationListener
    private val danger = Danger()
    private val viewModel: MainViewModel by inject()
    private lateinit var data: String
    private var isRecording = false

    companion object {
        const val PERMISSION_CODE = 123456
        const val ACTION_RECORD = "com.capstone101.bebas.home.recordingStart"

        @Volatile
        var count = 0
    }

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
                    location()
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
            if (it != null) {
                MapVal.user = it.apply { it.inDanger = false }
                viewModel.updateUserStatus()
                // TODO: BUAT USER PROFILE
                bind.profileGo.text =
                    "Username: ${it?.username}\nEmail: ${it?.email}\nInDanger: ${it?.inDanger}"

                viewModel.getRelative.observe(this) { relative ->
                    bind.relativeGo.text = "Invited: ${relative.invited.size}\n" +
                            "Inviting: ${relative.inviting.size}\n" +
                            "Confirmed: ${relative.pure.size}\n"
                }
                viewModel.getUser.removeObservers(this)
            }
        }

        manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                println("Latitude: ${location.latitude}\nLongitude: ${location.longitude}")
                viewModel.setCondition.value =
                    viewModel.setCondition.value?.apply { this[1] = true }
                danger.place = GeoPoint(location.latitude, location.longitude)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit

            override fun onProviderEnabled(provider: String) = Unit

            override fun onProviderDisabled(provider: String) = Unit
        }
        viewModel.condition.observe(this) {
            if (it[0] && it[1]) {
                viewModel.insertDanger(danger)
                viewModel.setCondition.value = mutableListOf(false, false)
            }
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
        val fileName = formatter.format(date)

        data = "$folder/$fileName.mp3"

        danger.id = fileName
        danger.time = Timestamp(date)
        danger.record = "${MapVal.user!!.username}/$fileName.mp3"

        if (!folder.exists()) folder.mkdir()
    }

    private fun location() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(applicationContext, "Harap accept permission", Toast.LENGTH_LONG).show()
            permissionCheck()
            return
        }
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 5F, listener)
        Handler(Looper.getMainLooper()).postDelayed({
            manager.removeUpdates(listener)
        }, 60000)
    }

    private fun recording() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(applicationContext, "Harap accept permission", Toast.LENGTH_LONG).show()
            permissionCheck()
            return
        }

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
            isRecording = false
            bind.recordMain.isEnabled = true
            count = 0
            bind.recordMain.setImageResource(R.drawable.record)
            viewModel.setCondition.value = viewModel.setCondition.value?.apply { this[0] = true }
        }, 11000)
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

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}