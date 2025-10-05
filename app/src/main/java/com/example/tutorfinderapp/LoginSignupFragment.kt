package com.example.tutorfinderapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContentProviderCompat
import androidx.fragment.app.Fragment
import com.example.tutorfinderapp.databinding.FragmentLoginsignupBinding

class LoginSignupFragment : Fragment() {
    private var _binding: FragmentLoginsignupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginsignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val roles = listOf("Student", "Tutor")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.roleSpinner.adapter = adapter

        binding.loginBtn.setOnClickListener {
            val selectedRole = binding.roleSpinner.selectedItem.toString().lowercase()
            val fragment = LoginFragment().apply {
                arguments = Bundle().apply {
                    putString("ROLE", selectedRole)
                }
            }
            (activity as MainActivity).replaceFragment(fragment)
        }
        binding.signupBtn.setOnClickListener {
            val selectedRole = binding.roleSpinner.selectedItem.toString().lowercase()
            val fragment = SignupFragment().apply {
                arguments = Bundle().apply {
                    putString("ROLE", selectedRole)
                }
            }
            (activity as MainActivity).replaceFragment(fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}