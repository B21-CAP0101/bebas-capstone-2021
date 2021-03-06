package com.capstone101.bebas.ui.main.relative

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.capstone101.bebas.databinding.FragmentAddBinding
import com.capstone101.core.domain.model.Relatives
import com.capstone101.core.domain.model.User
import com.capstone101.core.utils.Function.glideGender
import com.capstone101.core.utils.Function.setOnPressEnter
import com.capstone101.core.utils.Function.visibility
import com.capstone101.core.utils.MapVal
import org.koin.android.ext.android.inject

class RelativeAddFragment : Fragment() {
    private var _bind: FragmentAddBinding? = null
    private val bind get() = _bind
    private val viewModel: RelativeViewModel by inject()

    private val args: RelativeAddFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _bind = FragmentAddBinding.inflate(layoutInflater)
        return _bind?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObserver()
        handleDeepLink()
        setupToolbar()
        setHasOptionsMenu(true)
    }

    private fun handleDeepLink() {
        val username = args.username ?: ""
        if (username.isNotEmpty()) {
            bind?.apply {
                etQuery.setText(username)
                ibSearch.performClick()
            }
        }
    }

    private var user: User? = null
    private var relatives: Relatives = Relatives(
        MapVal.user?.username ?: "", listOf(), listOf(), listOf()
    )

    private fun subscribeToObserver() {
        bind?.apply {
            ibSearch.setOnClickListener {
                val username = etQuery.text.toString()
                parentFragment?.viewLifecycleOwner?.let { owner ->
                    if (username.isNotEmpty()) {
                        viewModel.searchUser(username)
                            .observe(owner) { users ->
                                if (users.isNotEmpty()) {
                                    relatives = args.relative ?: Relatives(
                                        MapVal.user?.username ?: "", listOf(), listOf(), listOf()
                                    )
                                    if (users[0].username != MapVal.user?.username && (!relatives.pure.contains(
                                            users[0].username
                                        )) && (!relatives.invited.contains(
                                            users[0].username
                                        ))
                                    ) {
                                        user = users[0]
                                        toggleAdd.isChecked =
                                            relatives.inviting.contains(user?.username)

                                        handleUserNotFound(false)
                                        setupUI(users[0])
                                    } else {
                                        tvNotFound.text =
                                            StringBuilder("this user already your relative")
                                        handleUserNotFound(true)
                                    }
                                } else {
                                    tvNotFound.text =
                                        StringBuilder("user not found")
                                    handleUserNotFound(true)
                                }
                            }
                    }
                }
            }
            toggleAdd.setOnClickListener {
                if (toggleAdd.isChecked)
                    viewModel.invite(relatives, user!!, true)
                else
                    viewModel.invite(relatives, user!!, false)
            }

        }
    }


    private fun setupToolbar() {
        bind?.apply {
            setOnPressEnter(etQuery, ibSearch)
        }
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

    private fun handleUserNotFound(visible: Boolean) {
        bind?.apply {
            listOf(tvTextIllustration, illustration).visibility(!visible)
            listOf(tvName, svPhotoProfile, tvUsername, toggleAdd).visibility(!visible)

            listOf(lottieNotFound, tvNotFound).visibility(visible)
        }
    }

    private fun setupUI(user: User?) {
        bind?.apply {
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