package com.capstone101.bebas.ui.main.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.capstone101.bebas.R
import com.capstone101.bebas.databinding.FramentDetailDangerBinding

class FragmentDetail : Fragment(R.layout.frament_detail_danger) {
    private var _bind: FramentDetailDangerBinding? = null
    private val bind get() = _bind

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _bind = FramentDetailDangerBinding.bind(view)


    }

    override fun onDestroy() {
        _bind = null
        super.onDestroy()
    }
}