package com.capstone101.bebas.main.home

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.capstone101.bebas.R
import com.capstone101.bebas.databinding.FragmentHomeBinding
import com.capstone101.bebas.main.MainViewModel
import com.capstone101.bebas.util.Function.createSnackBar
import com.capstone101.core.utils.MapVal
import org.koin.android.ext.android.inject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var binding: FragmentHomeBinding? = null
    private val bind get() = binding!!

    private var granted: Int = 0
    private lateinit var permissions: Array<String>
    private var handlerAnimation = Handler()

    companion object {
        const val PERMISSION_CODE = 123456
        const val ACTION_RECORD = "com.capstone101.bebas.home.recordingStart"

        @Volatile
        var count = 0
    }

    private val viewModel: MainViewModel by inject()
    private lateinit var data: String
    private var isRecording = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        subscribeToViewModel()
        setupActionPanicButton()
        startPulse()
        if (requireActivity().intent.action == ACTION_RECORD) recording()
    }


    private fun subscribeToViewModel() {
        viewModel.getUser.observe(viewLifecycleOwner) {
            MapVal.user = it
            // TODO: BUAT USER PROFILE
        }
    }

    private fun setupActionPanicButton() {
        with(bind) {
            btnPanic.setOnClickListener {
                count++
                when (count) {
                    3 -> {
                        requireView().createSnackBar("start recording", 1000)
                        recording()
                        stopPulse()
                    }
                    2 -> requireView().createSnackBar("press 1 more time", 1000)


                    1 -> requireView().createSnackBar("press 2 more time", 1000)
                }
                Handler(Looper.getMainLooper()).postDelayed({ count = 0 }, 3000)
            }
        }
    }

    private fun recording() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requireView().createSnackBar("please accept permission", 2000)
            permissionCheck()
            return
        }

        isRecording = true
        bind.btnPanic.isEnabled = false
        checkFolder()

        bind.btnPanic.text = StringBuilder("rec")
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
            bind.btnPanic.isEnabled = true
            count = 0
            bind.btnPanic.text = StringBuilder("danger")
            startPulse()
        }, 11000)
    }

    private fun permissionCheck() {
        granted = PackageManager.PERMISSION_GRANTED
        permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO
        )
        if (ActivityCompat.checkSelfPermission(
                requireContext(), permissions[0]
            ) != granted && ActivityCompat.checkSelfPermission(
                requireContext(), permissions[1]
            ) != granted && ActivityCompat.checkSelfPermission(
                requireContext(), permissions[2]
            ) != granted
        ) ActivityCompat.requestPermissions(
            requireActivity(),
            permissions,
            PERMISSION_CODE
        )
    }

    private fun checkFolder() {
        val folder = File(requireActivity().externalCacheDir?.absolutePath!!)
        val date = Date()
        val formatter = SimpleDateFormat("dd-MM-yyyy-HH-mm", Locale.getDefault())
        val fileName = "${formatter.format(date)}.mp3"

        data = "$folder/$fileName"

        if (!folder.exists()) folder.mkdir()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != PERMISSION_CODE) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(), permissions[0]
                ) != granted
            )
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(permissions[0], permissions[1]), PERMISSION_CODE
                )
            if (ActivityCompat.checkSelfPermission(
                    requireContext(), permissions[2]
                ) != granted
            )
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(permissions[2]), PERMISSION_CODE
                )
        }
    }

    private var runnable = object : Runnable {
        override fun run() {

          with(bind){
              ivAnim1.animate().scaleX(2f).scaleY(2f).alpha(0f).setDuration(1000)
                  .withEndAction {
                      ivAnim1.scaleX = 1f
                      ivAnim1.scaleY = 1f
                      ivAnim1.alpha = 1f
                  }

              ivAnim2.animate().scaleX(2f).scaleY(2f).alpha(0f).setDuration(700)
                  .withEndAction {
                      ivAnim2.scaleX = 1f
                      ivAnim2.scaleY = 1f
                      ivAnim2.alpha = 1f
                  }

          }

            handlerAnimation.postDelayed(this, 1500)
        }
    }

    private fun startPulse() {
        runnable.run()
    }

    private fun stopPulse() {
        handlerAnimation.removeCallbacks(runnable)
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}