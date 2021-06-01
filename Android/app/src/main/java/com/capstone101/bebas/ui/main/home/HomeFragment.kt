package com.capstone101.bebas.ui.main.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.capstone101.bebas.R
import com.capstone101.bebas.databinding.FragmentHomeBinding
import com.capstone101.bebas.ui.main.MainActivity
import com.capstone101.bebas.ui.main.MainViewModel
import com.capstone101.bebas.ui.main.relative.RelativeActivity
import com.capstone101.bebas.util.Function.createSnackBar
import com.capstone101.bebas.util.Function.createToast
import com.capstone101.core.data.Status
import com.capstone101.core.domain.model.Danger
import com.capstone101.core.domain.model.Relatives
import com.capstone101.core.domain.model.User
import com.capstone101.core.utils.MapVal
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import org.koin.android.ext.android.inject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var bind: FragmentHomeBinding


    private var granted: Int = 0
    private lateinit var permissions: Array<String>
    private lateinit var manager: LocationManager
    private lateinit var listener: LocationListener
    private val danger = Danger()
    private val viewModel: MainViewModel by inject()
    private lateinit var data: String
    private var handlerAnimation = Handler(Looper.getMainLooper())

    companion object {
        const val PERMISSION_CODE = 123456

        const val ACTION_RECORD = "com.capstone101.bebas.home.recordingStart"

        @Volatile
        var count = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentHomeBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleLoading()
        permissionCheck()
        subscribeToViewModel()
        setupActionPanicButton()
        startPulse()
        navigateToRelative()

        manager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                Log.e("Latitude", "${location.latitude}\nLongitude: ${location.longitude}")
                viewModel.setCondition.value =
                    viewModel.setCondition.value?.apply { this[1] = true }
                danger.place = GeoPoint(location.latitude, location.longitude)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit

            override fun onProviderEnabled(provider: String) = Unit

            override fun onProviderDisabled(provider: String) = Unit
        }

        if (requireActivity().intent.action == ACTION_RECORD) recording()

        viewModel.condition.observe(viewLifecycleOwner) {
            // UNTUK CEK APAKAH SUDAH SELESAI RECORD DAN FETCH LOKASI
            if (it[0] && it[1]) {
                viewModel.insertDanger(danger)
                viewModel.setCondition.value = mutableListOf(false, false)
            }
        }
    }


    private fun navigateToRelative() {
        bind.tvSeeAll.setOnClickListener {
            startActivity(Intent(requireContext(), RelativeActivity::class.java))
        }
    }

    private var relative: Relatives? = null

    private fun subscribeToViewModel() {
        val inDangerCallback: (Status<List<User>>) -> Unit = {
            if (relative != null) {
                when (it) {
                    is Status.Success ->
                        viewModel.setUsers.value =
                            it.data?.filter { user -> user.username in relative!!.pure }
                    else -> requireView().createSnackBar(it.error!!, 1000)
                }
            }
        }
        viewModel.getUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                MapVal.user = user.apply { user.inDanger = false }
                viewModel.updateUserStatus()
                setupUI()
                viewModel.getRelative { relatives ->
                    relative = relatives
                    bind.layoutEmptyRelative.root.isVisible =
                        if (relatives.pure.isEmpty()) {
                            bind.tvSeeAll.text = StringBuilder("add")
                            true
                        } else {
                            bind.tvSeeAll.text = StringBuilder("more")
                            false
                        }
                    viewModel.checkInDanger(inDangerCallback)
                }
                viewModel.getUser.removeObservers(viewLifecycleOwner)
            }
        }
        viewModel.users.observe(viewLifecycleOwner) { users ->
            users.forEach { user ->
                viewModel.latestDanger(user).observe(viewLifecycleOwner) {
                    Log.e("danger", it.place.toString())
                }
            }
        }
    }

    private fun setupUI() {
        with(bind) {
            MapVal.user?.apply {
                cardUser.tvName.text = name
                cardUser.tvUsername.text = username

                Glide.with(requireView())
                    .load("")
                    .placeholder(R.drawable.ic_guard_small)
                    .error(R.drawable.ic_guard_small)
                    .centerInside()
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            layoutLoading.root.isVisible = false
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            layoutLoading.root.isVisible = false
                            return false
                        }
                    }).into(cardUser.svProfile)
            }
        }
    }

    private fun handleLoading() {
        with(bind) {
            layoutLoading.root.isVisible = true
            layoutLoading.tvStatusLogin.isVisible = false
        }
    }

    private fun setupActionPanicButton() {
        with(bind) {
            btnPanic.setOnClickListener {
                count++
                when (count) {
                    2 -> {
                        requireContext().createToast("start recording", 500)
                        location()
                        recording()
                        stopPulse()
                    }
                    1 -> requireContext().createToast("press 1 more time", 500)
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
            requireContext().createToast("please accept the permission", 1000)
            permissionCheck()
            return
        }

        checkFolder()
        MainActivity.isRecording = true
        bind.btnPanic.isEnabled = false

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
            MainActivity.isRecording = false
            bind.btnPanic.isEnabled = true
            count = 0
            requireContext().createToast("sending record", 1000)
            bind.btnPanic.text = resources.getString(R.string.txt_panic_btn)
            startPulse()
            viewModel.setCondition.value =
                viewModel.setCondition.value?.apply { this[0] = true }
        }, 11000)
    }

    private fun location() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requireContext().createToast("please accept the permission", 1000)
            permissionCheck()
            return
        }
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 5F, listener)
        Handler(Looper.getMainLooper()).postDelayed({
            manager.removeUpdates(listener)
        }, 60000)
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
            requireActivity(), permissions, PERMISSION_CODE
        )
    }

    private fun checkFolder() {
        val folder = File(requireContext().externalCacheDir?.absolutePath!!)
        val date = Date()
        val formatter = SimpleDateFormat("dd-MM-yyyy-HH-mm", Locale.getDefault())
        val fileName = formatter.format(date)

        data = "$folder/$fileName.mp3"

        danger.id = fileName
        danger.time = Timestamp(date)
        danger.record = "${MapVal.user!!.username}/$fileName.mp3"

        if (!folder.exists()) folder.mkdir()
    }

    private var runnable = object : Runnable {
        override fun run() {

            with(bind) {
                ivAnim1.animate().scaleX(1.5f).scaleY(1.5f).alpha(0f).setDuration(1000)
                    .withEndAction {
                        ivAnim1.scaleX = 1f
                        ivAnim1.scaleY = 1f
                        ivAnim1.alpha = 1f
                    }

                ivAnim2.animate().scaleX(1.5f).scaleY(1.5f).alpha(0f).setDuration(700)
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
}