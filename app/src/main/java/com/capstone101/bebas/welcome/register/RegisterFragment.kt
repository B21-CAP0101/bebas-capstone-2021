package com.capstone101.bebas.welcome.register

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.capstone101.bebas.R
import com.capstone101.bebas.databinding.FragmentRegisterBinding
import com.capstone101.bebas.util.Function
import com.capstone101.bebas.util.Function.clearWelcomeActivityAndCreateMainActivity
import com.capstone101.bebas.util.Function.createSnackBar
import com.capstone101.bebas.util.Function.showKeyboard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class RegisterFragment : Fragment(R.layout.fragment_register) {
    private var binding: FragmentRegisterBinding? = null
    private val bind get() = binding!!

    private val viewModel: RegisViewModel by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)

        startFocus()
        setupActionRegister()

        bind.ibBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.condition.observe(this) {
            when (it) {
                true -> navigateToHome()
                null -> {
                    clearAllText()
                    requireView().createSnackBar("username already exist", 1000)
                }
                else -> {
                    clearAllText()
                    requireView().createSnackBar("something wrong occurred", 1000)
                }
            }
        }
    }


    private fun setupActionRegister() {
        with(bind) {
            btnRegisterGo.setOnClickListener {
                layoutLoading.root.isVisible = true
                it.isVisible = false

                val username = etUsername.text.toString()
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    viewModel.insertToFs(
                        username,
                        password,
                        email
                    )
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

    private fun navigateToHome() {
        lifecycleScope.launch(Dispatchers.Main) {
            bind.layoutLoading.tvStatusLogin.text =
                resources.getString(R.string.success)
            bind.layoutLoading.MKLoader.isVisible = false

            delay(500)
            startActivity(clearWelcomeActivityAndCreateMainActivity(requireActivity()))
        }
    }

    private fun clearAllText() {
        with(bind) {
            etUsername.text.clear()
            etEmail.text.clear()
            etPassword.text.clear()
        }
    }


    private fun startFocus() {
        with(bind) {
            etUsername.showKeyboard()
            Function.setOnPressEnter(etPassword, btnRegisterGo)
        }
    }

    override fun onStop() {
        viewModel.condition.removeObservers(this)
        super.onStop()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}