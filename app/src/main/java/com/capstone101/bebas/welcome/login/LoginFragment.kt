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
                if (etPassword.text.toString().isNotEmpty() && etUsername.text.toString()
                        .isNotEmpty()
                ) {
                    viewModel.login(etUsername.text.toString(), etPassword.text.toString())
                        .observe(viewLifecycleOwner) { data ->
                            when (data) {
                                is Status.Success -> {
                                    println("SUCCESS TOOL")
                                    layoutLoading.MKLoader.isVisible = false
                                    layoutLoading.tvStatusLogin.text =
                                        resources.getString(R.string.success)
                                    session.createLogin()
                                    setupNavigateToHome()
                                }
                                is Status.Loading -> {
                                    btnLoginGo.isVisible = false
                                    requireView().hideKeyboard()
                                    layoutLoading.root.isVisible = true
                                }
                                is Status.Error -> {
                                    println("ERROR TOOL")
                                    etUsername.text.clear()
                                    etPassword.text.clear()
                                    btnLoginGo.isVisible = true
                                    layoutLoading.root.isVisible = false
                                    requireView().createSnackBar(data.error.toString(), 500)
                                }
                            }
                        }
                } else {
                    if (etUsername.text.toString().isEmpty()) {
                        etUsername.error = "cannot be empty"
                    } else {
                        etPassword.error = "cannot be empty"
                    }
                }
            }
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