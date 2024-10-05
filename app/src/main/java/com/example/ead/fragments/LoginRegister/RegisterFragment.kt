package com.example.ead.fragments.LoginRegister

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ead.R
import com.example.ead.databinding.FragmentRegisterBinding
import com.example.ead.models.Customer
import com.example.ead.util.Resource
import com.example.ead.viewmodel.RegisterViewModel

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDoYouHaveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.buttonRegisterRegister.setOnClickListener {
            val fullName = binding.edFullNameRegister.text.toString().trim()
            val email = binding.edEmailRegister.text.toString().trim()
            val password = binding.edPasswordRegister.text.toString().trim()
            val confirmPassword = binding.edRePasswordRegister.text.toString().trim()
            val mobileNumber = binding.edMobileNumberRegister.text.toString().trim()
            val address = binding.edAddressRegister.text.toString().trim()

            // Validate inputs before proceeding
            if (viewModel.validateInputs(
                    requireContext(),
                    email,
                    password,
                    confirmPassword,
                    mobileNumber
                )
            ) {
                val customer = Customer(
                    fullName = fullName,
                    email = email,
                    password = password,
                    mobileNumber = mobileNumber.toInt(),
                    address = address
                )

                // Call ViewModel to register the customer
                viewModel.createAccountWithEmailAndPassword(requireContext(), customer) {
                    // Navigate to the login page on success
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }
            }
        }


    }
}