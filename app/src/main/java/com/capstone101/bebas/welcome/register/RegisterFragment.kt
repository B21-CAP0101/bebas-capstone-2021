package com.capstone101.bebas.welcome.register

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.capstone101.bebas.R
import com.capstone101.bebas.databinding.FragmentRegisterBinding
import com.capstone101.bebas.util.Function.clearWelcomeActivityAndCreateMainActivity
import com.capstone101.bebas.util.Function.createSnackBar
import com.capstone101.bebas.util.Function.setOnPressEnter
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
                true -> handleSuccess()
                null -> handleError("username already exist")
                else -> handleError("something wrong occurred")
            }
        }
    }


    private fun setupActionRegister() {
        with(bind) {
            btnRegisterGo.setOnClickListener {
                val username = etUsername.text.toString()
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    layoutLoading.root.isVisible = true  // TODO: AKU COBA PINDAH, BETULKAH INI?
                    it.isVisible = false
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

    private fun navigateToHome() {
        lifecycleScope.launch(Dispatchers.Main) {
            delay(500)
            startActivity(clearWelcomeActivityAndCreateMainActivity(requireActivity()))
            requireActivity().finish()
        }
    }


    private fun handleSuccess() {
        with(bind) {
            layoutLoading.tvStatusLogin.text = resources.getString(R.string.success)
            layoutLoading.MKLoader.isVisible = false

            navigateToHome()
        }
    }

    private fun handleError(message: String) {
        with(bind) {
            etEmail.text.clear()
            etPassword.text.clear()
            etUsername.text.clear()

            bind.layoutLoading.root.isVisible = false
            btnRegisterGo.isVisible = true

            etUsername.requestFocus()
            etUsername.showKeyboard()

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

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}