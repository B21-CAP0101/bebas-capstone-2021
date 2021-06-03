package com.capstone101.bebas.ui.main.relative

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.capstone101.bebas.R
import com.capstone101.bebas.databinding.FragmentMainRelativeBinding
import com.capstone101.bebas.ui.main.MainViewModel
import com.capstone101.core.adapters.RelativeAdapter
import com.capstone101.core.utils.Constant.TYPE_PURE
import org.koin.android.ext.android.inject

class RelativeFragmentMain : Fragment(R.layout.fragment_main_relative) {
    private var _bind: FragmentMainRelativeBinding? = null
    private val bind get() = _bind!!
    private val viewModel: MainViewModel by inject()
    private lateinit var relativeAdapter: RelativeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main_relative, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bind = FragmentMainRelativeBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        setupAdapters()
        setupRecyclerView()
        subscribeToViewModel()
        setupToolbar()
        navigateToAnotherFragment()
        setHasOptionsMenu(true)
    }

    private fun setupAdapters() {
        relativeAdapter = RelativeAdapter(TYPE_PURE, { _, _ -> }) { _, _ -> }
    }

    private fun setupRecyclerView() {
        with(bind) {
            rvRelative.adapter = relativeAdapter
        }
    }

    private fun subscribeToViewModel() {
        viewModel.getRelative { relatives ->
            viewModel.getUserInfoByRelative(relatives)
                .observe(requireActivity()) { pureUser ->
                    relativeAdapter.differ.submitList(pureUser)
                    handleEmptyData()
                }
        }
    }

    private fun handleEmptyData() {
        bind.rvRelative.isVisible = relativeAdapter.differ.currentList.size > 0
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity?)!!.setSupportActionBar(bind.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun navigateToAnotherFragment() {
        with(bind) {
            chipInvitation.setOnClickListener {
                findNavController().navigate(RelativeFragmentMainDirections.actionRelativeFragmentMainToRelativeInvitingFragment())
            }
            chipInvited.setOnClickListener {
                findNavController().navigate(RelativeFragmentMainDirections.actionRelativeFragmentMainToRelativeInvitedFragment())
            }
        }
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