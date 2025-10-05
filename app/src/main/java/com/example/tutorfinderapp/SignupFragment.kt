package com.example.tutorfinderapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tutorfinderapp.databinding.FragmentSignupBinding

class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private var role : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = arguments?.getString("ROLE")
    }

    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savedInstanceState : Bundle?
    ): View? {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        val role = role?.replaceFirstChar { c: Char -> c.uppercaseChar() }
        val headingTxt = role + " Sign up"
        binding.roleTxt.text = headingTxt
        // name, age, and email will be asked for both student and tutor
        // Only for tutor:
        // payment amount expected, subject to teach, homework amount, and hours available
        // Only for student:
        //
        if (role.equals("tutor", ignoreCase = true)) {
            binding.paymentTxt.visibility = View.VISIBLE
            binding.subjectTxt.visibility = View.VISIBLE
            binding.hwTxt.visibility = View.VISIBLE
            binding.timeText.visibility = View.VISIBLE
        }
        if (role.equals("student", ignoreCase = true)) {
            binding.paymentTxt.visibility = View.GONE
            binding.subjectTxt.visibility = View.GONE
            binding.hwTxt.visibility = View.GONE
            binding.timeText.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}