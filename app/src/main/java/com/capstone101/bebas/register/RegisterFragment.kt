package com.capstone101.bebas.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.capstone101.bebas.R
import com.capstone101.bebas.databinding.FragmentRegisterBinding
import org.koin.android.ext.android.inject

class RegisterFragment : Fragment() {
    private var binding: FragmentRegisterBinding? = null
    private val bind get() = binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return bind.root
    }

    private val viewModel: RegisViewModel by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.btnRegisterGo.setOnClickListener {
            // TODO: Loading Screen ON
            viewModel.insertToFs(
                bind.etUsername.text.toString(),
                bind.etPassword.text.toString(),
                bind.etEmail.text.toString()
            )
        }

        bind.imageButton.setOnClickListener {
            it.findNavController().navigateUp()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.condition.observe(this) {
            when (it) {
                true -> {
                    // TODO: Loading Screen OFF
                    findNavController().navigate(R.id.loginFragment)
                }
                null -> Toast.makeText(
                    requireContext(), "User sudah terdaftar", Toast.LENGTH_LONG
                ).show()
                else -> Toast.makeText(
                    requireContext(), "Terjadi kesalahan", Toast.LENGTH_LONG
                ).show()
            }
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