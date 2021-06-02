package com.capstone101.bebas.ui.main.relative

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.capstone101.bebas.R
import com.capstone101.bebas.databinding.FragmentAddBinding
import com.capstone101.core.domain.model.User
import com.capstone101.core.utils.Function.glideGender
import com.capstone101.core.utils.Function.visibility
import org.koin.android.ext.android.inject

class RelativeAddFragment : Fragment(R.layout.fragment_add) {
    private var _bind: FragmentAddBinding? = null
    private val bind get() = _bind!!
    private val viewModel: RelativeViewModel by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _bind = FragmentAddBinding.bind(view)

        subscribeToObserver()
        setupToolbar()
        setHasOptionsMenu(true)
    }


    private fun subscribeToObserver() {
        with(bind) {
            ibSearch.setOnClickListener {
                viewModel.searchUser(etQuery.text.toString()).observe(viewLifecycleOwner)
                { user ->
                    setupUI(user[0])
                }
            }

            // TODO: TAMBAHIN VIEWMODELNYA RUD
            toggleAdd.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // TODO: TAMBAHIN USER KEDALAM RELATIVE
                } else {
                    // TODO : REMOVE USER DARI DALAM RELATIVE
                }
            }

        }
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


    private fun setupUI(user: User?) {
        with(bind) {
            user?.apply {
                listOf(tvTextIllustration, illustration).visibility(false)
                listOf(tvName, svPhotoProfile, tvUsername, toggleAdd).visibility(true)

                tvName.text = name
                tvUsername.text = username

                requireView().glideGender(photoURL ?: "", svPhotoProfile, gender)
            }
        }
    }

    override fun onDestroy() {
        _bind = null
        super.onDestroy()
    }
}