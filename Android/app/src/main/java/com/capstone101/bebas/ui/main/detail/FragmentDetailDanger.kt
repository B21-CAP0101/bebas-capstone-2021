package com.capstone101.bebas.ui.main.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.capstone101.bebas.R
import com.capstone101.bebas.databinding.FragmentDetailDangerBinding
import org.koin.android.ext.android.inject

class FragmentDetailDanger : Fragment(R.layout.fragment_detail_danger) {
    private var _bind: FragmentDetailDangerBinding? = null
    private val bind get() = _bind

    private val viewModel: DetailDangerViewModel by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _bind = FragmentDetailDangerBinding.bind(view)

        // TODO: ... diganti dengan data user yang dipencet Bram
//        viewModel.getLatestDanger(...).observe(viewLifecycleOwner) {
//
//        }
    }

    override fun onDestroy() {
        _bind = null
        super.onDestroy()
    }
}