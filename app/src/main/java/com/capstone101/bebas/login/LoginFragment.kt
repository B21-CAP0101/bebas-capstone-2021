package com.capstone101.bebas.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.capstone101.bebas.databinding.FragmentLoginBinding
import com.capstone101.core.data.Status
import org.koin.android.ext.android.inject

class LoginFragment : Fragment() {
    private var binding: FragmentLoginBinding? = null
    private val bind get() = binding!!

    val viewModel: LoginViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.btnLoginGo.setOnClickListener {
            viewModel.getUser(bind.etUsername.text.toString(), bind.etPassword.text.toString())
                .observe(viewLifecycleOwner) {
                    when (it) {
                        is Status.Success -> {
                            // TODO: LOADING GONE
                            Toast.makeText(requireContext(), "Login Berhasil", Toast.LENGTH_LONG)
                                .show()
                        }
                        is Status.Loading -> {
                            println("LOADING")
                            // TODO: LOADING SHOW
                        }
                        is Status.Error -> {
                            // TODO: LOADING GONE
                            Toast.makeText(requireContext(), it.error, Toast.LENGTH_LONG).show()
                        }
                    }
                }
        }

        bind.imageButton.setOnClickListener {
            it.findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}