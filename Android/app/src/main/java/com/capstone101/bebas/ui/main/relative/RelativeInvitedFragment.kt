package com.capstone101.bebas.ui.main.relative

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.capstone101.bebas.R
import com.capstone101.bebas.databinding.FragmentInvitedBinding
import com.capstone101.core.adapters.RelativeAdapter
import com.capstone101.core.utils.Constant.TYPE_INVITED

class RelativeInvitedFragment : Fragment(R.layout.fragment_invited) {
    private var _bind: FragmentInvitedBinding? = null
    private val bind get() = _bind!!
    private lateinit var relativeAdapter: RelativeAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _bind = FragmentInvitedBinding.bind(view)

        setupAdapters()
        setupRecyclerView()
        subscribeToViewModel()
        setupToolbar()
        setHasOptionsMenu(true)
    }

    private fun setupAdapters() {
        relativeAdapter = RelativeAdapter(TYPE_INVITED)
    }

    private fun setupRecyclerView() {
        with(bind) {
            rvRelative.adapter = relativeAdapter
        }
    }

    private fun subscribeToViewModel() {
        //TODO GET DATA INVITED
        // masukin ke relativeAdapter.differ.submitList({data invited})
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity?)!!.setSupportActionBar(bind.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        _bind = null
        super.onDestroy()
    }
}