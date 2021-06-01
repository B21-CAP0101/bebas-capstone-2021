package com.capstone101.bebas.ui.welcome.register

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.capstone101.bebas.R
import com.capstone101.bebas.databinding.FragmentRegisterBinding
import com.capstone101.core.utils.Function.createSnackBar
import com.capstone101.core.utils.Function.hideKeyboard
import com.capstone101.core.utils.Function.setOnPressEnter
import com.capstone101.core.utils.Function.showKeyboard
import com.capstone101.core.data.Status
import com.capstone101.core.utils.SessionManager
import org.koin.android.ext.android.inject

class RegisterFragment : Fragment(R.layout.fragment_register) {
    private lateinit var bind: FragmentRegisterBinding
    private val viewModel: RegisViewModel by inject()
    private val session: SessionManager by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind = FragmentRegisterBinding.bind(view)

        startFocus()
        setupActionRegister()
        subscribeToViewModel()

        bind.ibBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun setupActionRegister() {
        with(bind) {
            btnRegisterGo.setOnClickListener {
                val username = etUsername.text.toString()
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    handleLoading()
                    viewModel.insertToFs(username, password, email)
                } else {
                    when {
                        username.isEmpty() -> {
                            etUsername.error = "cannot be empty"
                        }
                        email.isEmpty() -> {
                            etEmail.error = "cannot be empty"
                        }
                        else -> {
                            etPassword.error = "cannot bet empty"
                        }
                    }
                }

            }
        }
    }


    private fun subscribeToViewModel() {
        viewModel.condition.observe(viewLifecycleOwner) { result ->
            when (result) {
                true -> handleSuccess()
                null -> handleError("username already exist")
                else -> handleError("something wrong occurred")
            }
        }
    }


    private fun navigateToHome() {
        findNavController().navigate(R.id.action_registerFragment_to_mainActivity)
        requireActivity().finish()
    }


    private fun handleSuccess() {
        viewModel.login(bind.etUsername.text.toString(), bind.etPassword.text.toString())
            .observe(viewLifecycleOwner) {
                when (it) {
                    is Status.Success -> {
                        session.createLogin()
                        navigateToHome()
                    }
                    is Status.Loading -> Unit
                    is Status.Error -> requireView().createSnackBar(
                        "Something's wrong ${it.error}",
                        2000
                    )
                }
            }
    }

    private fun handleLoading() {
        with(bind) {
            requireView().hideKeyboard()
            layoutLoading.root.isVisible = true
            btnRegisterGo.isVisible = false
        }
    }

    private fun handleError(message: String) {
        with(bind) {
            etEmail.text.clear()
            etPassword.text.clear()
            etUsername.text.clear()
            etUsername.requestFocus()

            bind.layoutLoading.root.isVisible = false
            btnRegisterGo.isVisible = true

            requireView().createSnackBar(message, 1000)
        }
    }


    private fun startFocus() {
        with(bind) {
            etUsername.showKeyboard()
            setOnPressEnter(etPassword, btnRegisterGo)
        }
    }

    override fun onStop() {
        viewModel.condition.removeObservers(this)
        super.onStop()
    }
}