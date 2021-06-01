package com.capstone101.bebas.ui.main.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.capstone101.bebas.R
import com.capstone101.bebas.databinding.FragmentDetailDangerBinding
import com.capstone101.core.domain.model.Danger
import com.capstone101.core.utils.Function.glide
import com.google.android.gms.maps.GoogleMap
import org.koin.android.ext.android.inject

class DetailDangerFragment : Fragment(R.layout.fragment_detail_danger) {
    private var _bind: FragmentDetailDangerBinding? = null
    private val bind get() = _bind
    private val args by navArgs<DetailDangerFragmentArgs>()
    private var map: GoogleMap? = null

    private val viewModel: DetailDangerViewModel by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _bind = FragmentDetailDangerBinding.bind(view)

        subscribeToViewModel()
        bind?.mapView?.onCreate(savedInstanceState)
    }

    private fun subscribeToViewModel() {
        viewModel.getLatestDanger(args.user).observe(viewLifecycleOwner) { danger ->
            setupUI(danger)
        }
    }

    private fun setupUI(danger: Danger) {
        bind?.apply {
            with(danger) {
                with(args.user) {
                    tvName.text = name ?: username
                    tvUsername.text = username
                    requireView().glide(
                        photoURL ?: "",
                        svProfile,
                        if (gender == false) R.drawable.ic_male_avatar else R.drawable.ic_female_avatar
                    )
                }
                audioPlayer.setAudioTarget(record)
            }
        }
    }

    override fun onDestroy() {
        _bind = null
        super.onDestroy()
    }
}