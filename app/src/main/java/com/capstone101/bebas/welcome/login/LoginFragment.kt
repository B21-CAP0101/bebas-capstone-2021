package com.capstone101.bebas.welcome.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.capstone101.core.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var bind: FragmentLoginBinding
    private val viewModel: LoginViewModel by inject()
    private val session: SessionManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentLoginBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startFocus()
        setupActionLogin()

        bind.ibBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }


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
                    is Status.Loading -> {
                        /* NO-OP */
                    }
                }
            }
        }

    }

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
            etUsername.requestFocus()

            bind.layoutLoading.root.isVisible = false
            btnLoginGo.isVisible = true


            requireView().createSnackBar(message, 1000)
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
            findNavController().navigate(R.id.action_loginFragment_to_mainActivity)
            requireActivity().finish()
        }
    }
}