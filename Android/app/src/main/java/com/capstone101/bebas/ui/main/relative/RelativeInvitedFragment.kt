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
import com.capstone101.bebas.databinding.FragmentInvitedBinding
import com.capstone101.core.adapters.RelativeAdapter
import com.capstone101.core.domain.model.Relatives
import com.capstone101.core.utils.Constant.TYPE_INVITED
import org.koin.android.ext.android.inject

class RelativeInvitedFragment : Fragment() {
    private var _bind: FragmentInvitedBinding? = null
    private val bind get() = _bind
    private lateinit var relativeAdapter: RelativeAdapter

    private val viewModel: RelativeViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _bind = FragmentInvitedBinding.inflate(layoutInflater)
        return _bind?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapters()
        subscribeToViewModel()
        setupToolbar()
        setHasOptionsMenu(true)
    }

    private fun setupAdapters() {
        relativeAdapter = RelativeAdapter(TYPE_INVITED)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        bind?.rvRelative?.adapter = relativeAdapter
    }

    private fun subscribeToViewModel() {
        viewModel.getRelative { relative ->

            relativeAdapter.setAddCancelCallBack { user, condition ->
                viewModel.invite(relative, user, condition)

                setupAdapters()
            }

            relativeAdapter.setConfirmDenyCallBack { user, condition ->
                viewModel.confirm(relative, user, condition)

                setupAdapters()
            }

            parentFragment?.viewLifecycleOwner?.let {
                viewModel.getUserInfoByRelative(relative, Relatives.INVITED)
                    .observe(it) { users ->
                        relativeAdapter.differ.submitList(users)
                        handleEmptyData()
                    }
            }
        }
    }

    private fun handleEmptyData() {
        bind?.rvRelative?.isVisible = relativeAdapter.differ.currentList.size > 0
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity?)!!.setSupportActionBar(bind?.toolbar)
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