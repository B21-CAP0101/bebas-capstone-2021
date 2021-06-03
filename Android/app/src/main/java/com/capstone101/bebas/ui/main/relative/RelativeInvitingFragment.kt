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
import com.capstone101.bebas.databinding.FragmentInvitationBinding
import com.capstone101.core.adapters.RelativeAdapter
import com.capstone101.core.domain.model.Relatives
import com.capstone101.core.utils.Constant
import org.koin.android.ext.android.inject

class RelativeInvitingFragment : Fragment(R.layout.fragment_invitation) {
    private lateinit var bind: FragmentInvitationBinding
    private lateinit var relativeAdapter: RelativeAdapter

    private val viewModel: RelativeViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_invitation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bind = FragmentInvitationBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        subscribeToViewModel()
        setupToolbar()
        setHasOptionsMenu(true)
    }

    private fun setupAdapters(relatives: Relatives) {
        relativeAdapter = RelativeAdapter(Constant.TYPE_INVITATION, { user, condition ->
            viewModel.invite(relatives, user, condition)
        }) { user, condition -> viewModel.confirm(relatives, user, condition) }
    }

    private fun setupRecyclerView() {
        with(bind) {
            rvRelative.adapter = relativeAdapter
        }
    }

    private var relative: Relatives? = null
    private fun subscribeToViewModel() {
        viewModel.getRelative {
            relative = it
            setupAdapters(it)
            setupRecyclerView()
            viewModel.getUserInfoByRelative(it, Relatives.INVITING)
                .observe(requireActivity()) { users ->
                    relativeAdapter.differ.submitList(users)
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

        with(bind) {
            ibSearch.setOnClickListener {
                if (relative != null) {
                    val navigate =
                        RelativeInvitingFragmentDirections.actionRelativeInvitingFragmentToRelativeAddFragment(
                            relative
                        )
                    findNavController().navigate(navigate)
                }
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
}