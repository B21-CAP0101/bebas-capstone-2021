package com.capstone101.bebas.welcome.login

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.capstone101.bebas.R
import com.capstone101.bebas.databinding.FragmentLoginBinding
import com.capstone101.bebas.util.Function.createSnackBar
import com.capstone101.bebas.util.Function.hideKeyboard
import com.capstone101.bebas.util.Function.setOnPressEnter
import com.capstone101.bebas.util.Function.showKeyboard
import com.capstone101.core.data.Status
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

    private fun setupActionLogin() {
        with(bind) {
            btnLoginGo.setOnClickListener {
                if (etPassword.text.toString().isNotEmpty() && etUsername.text.toString()
                        .isNotEmpty()
                ) {
                    viewModel.getUser(etUsername.text.toString(), etPassword.text.toString())
                        .observe(viewLifecycleOwner) {
                            when (it) {
                                is Status.Success -> {
                                    layoutLoading.MKLoader.isVisible = false
                                    layoutLoading.tvStatusLogin.text =
                                        resources.getString(R.string.success)
                                    setupNavigateToHome()
                                }
                                is Status.Loading -> {
                                    btnLoginGo.isVisible = false
                                    requireView().hideKeyboard()
                                    layoutLoading.root.isVisible = true
                                }
                                is Status.Error -> {
                                    etUsername.text.clear()
                                    etPassword.text.clear()
                                    btnLoginGo.isVisible = true
                                    layoutLoading.root.isVisible = false
                                    requireView().createSnackBar(it.error.toString(), 500)
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
            // TODO : Create Main Activity

        }
    }


    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}