package com.capstone101.bebas.ui.main.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.capstone101.bebas.R
import com.capstone101.bebas.databinding.ActivityDetailDangerBinding
import com.capstone101.core.domain.model.Danger
import com.capstone101.core.utils.Function.glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.koin.android.ext.android.inject

class DetailDangerActivity : AppCompatActivity(), OnMapReadyCallback {
    private var _bind: ActivityDetailDangerBinding? = null
    private val bind get() = _bind
    private val args by navArgs<DetailDangerActivityArgs>()

    private val viewModel: DetailDangerViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_BeBaS)
        super.onCreate(savedInstanceState)
        _bind = ActivityDetailDangerBinding.inflate(layoutInflater)
        setContentView(_bind?.root)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapView) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun setupUI(danger: Danger) {
        bind?.apply {
            with(danger) {
                with(args.user) {
                    tvName.text = name ?: username
                    tvUsername.text = username
                    this@apply.root.glide(
                        photoURL ?: "",
                        svProfile,
                        if (gender == false) R.drawable.ic_male_avatar else R.drawable.ic_female_avatar
                    )
                }
                audioPlayer.setAudioTarget(record)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        viewModel.getLatestDanger(args.user).observe(this) { danger ->
            setupUI(danger)
            googleMap?.apply {
                danger.place?.apply {
                    val loc = LatLng(latitude, longitude)
                    addMarker(
                        MarkerOptions().position(loc).title("${args.user.username} Location")
                    )
                    moveCamera(
                        CameraUpdateFactory.newLatLng(loc)
                    )
                }
            }
        }
    }
}