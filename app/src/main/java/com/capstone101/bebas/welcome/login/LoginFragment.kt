package com.capstone101.bebas.welcome.login

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.capstone101.bebas.R
import com.capstone101.bebas.databinding.FragmentLoginBinding
import com.capstone101.bebas.util.Function.clearWelcomeActivityAndCreateMainActivity
import com.capstone101.bebas.util.Function.createSnackBar
import com.capstone101.bebas.util.Function.hideKeyboard
import com.capstone101.bebas.util.Function.setOnPressEnter
import com.capstone101.bebas.util.Function.showKeyboard
import com.capstone101.core.data.Status
import com.capstone101.core.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var binding: FragmentLoginBinding? = null
    private val bind get() = binding!!
    private val viewModel: LoginViewModel by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        startFocus()
        setupActionLogin()

        bind.ibBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private val session: SessionManager by inject()
    private fun setupActionLogin() {
        with(bind) {
            btnLoginGo.setOnClickListener {
                val username = etUsername.text.toString()
                val password = etPassword.text.toString()

                if (username.isNotEmpty() && password.isNotEmpty()
                ) {
                    subscribeToViewModel()
                } else {
                    if (username.isEmpty()) {
                        etUsername.error = "cannot be empty"
                    } else {
                        etPassword.error = "cannot be empty"
                    }
                }
            }
        }
    }

    private fun subscribeToViewModel() {
        handleLoading()

        val username = bind.etUsername.text.toString()
        val password = bind.etPassword.text.toString()

        lifecycleScope.launch {
            viewModel.login(username, password).collectLatest { result ->

                when (result) {
                    is Status.Success -> handleSuccess()
                    is Status.Error -> handleError(result.error.toString())
                    is Status.Loading -> { /* NO-OP */ }
                }
            }
        }

    }


//    private fun subscribeToViewModel1() {
//        with(bind) {
//            viewModel.login(etUsername.text.toString(), etPassword.text.toString())
//                .observe(viewLifecycleOwner) { status ->
//                    when (status) {
//                        is Status.Success -> {
//                            layoutLoading.MKLoader.isVisible = false
//                            layoutLoading.tvStatusLogin.text =
//                                resources.getString(R.string.success)
//                            session.createLogin()
//                            Log.e("status", "success")
//                            setupNavigateToHome()
//
//
//                        }
//                        is Status.Loading -> {
//                            btnLoginGo.isVisible = false
//                            requireView().hideKeyboard()
//                            layoutLoading.root.isVisible = true
//
//                            Log.e("status", "loading")
//                        }
//                        is Status.Error -> {
//                            etUsername.text.clear()
//                            etPassword.text.clear()
//                            startFocus()
//                            btnLoginGo.isVisible = true
//                            layoutLoading.root.isVisible = false
////                            requireView().createSnackBar(it.error.toString(), 500)
//
//                            Log.e("status", "error")
//                        }
//                    }
//
//                }
//        }
//    }


    private fun handleSuccess() {
        with(bind) {
            session.createLogin()

            layoutLoading.MKLoader.isVisible = false
            layoutLoading.tvStatusLogin.text = resources.getString(R.string.success)

            setupNavigateToHome()
        }
    }

    private fun handleLoading() {
        with(bind) {
            btnLoginGo.isVisible = false
            layoutLoading.root.isVisible = true

            requireView().hideKeyboard()
        }
    }

    private fun handleError(message: String) {
        with(bind) {
            etPassword.text.clear()
            etUsername.text.clear()

            bind.layoutLoading.root.isVisible = false
            btnLoginGo.isVisible = true

            etUsername.requestFocus()
            etUsername.showKeyboard()

            requireView().createSnackBar(message, 500)
        }
    }

    private fun startFocus() {
        with(bind) {
            etUsername.showKeyboard()
            setOnPressEnter(etPassword, btnLoginGo)
        }
    }

    private fun setupNavigateToHome() {
        lifecycleScope.launch(Dispatchers.Main) {
            delay(500)
            startActivity(clearWelcomeActivityAndCreateMainActivity(requireActivity()))
            requireActivity().finish()
        }
    }


    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}