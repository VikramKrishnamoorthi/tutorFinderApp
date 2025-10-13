package com.example.tutorfinderapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tutorfinderapp.app.DBHelper
import com.example.tutorfinderapp.databinding.FragmentSignupBinding

class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private var role: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = arguments?.getString("ROLE")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dbHelper = DBHelper(requireContext(), null)

        // Set header text (e.g. "Tutor Sign Up")
        val displayRole = role?.replaceFirstChar { it.uppercaseChar() }
        binding.roleTxt.text = "$displayRole Sign Up"

        // Hide tutor-only fields for students
        if (role.equals("student", ignoreCase = true)) {
            binding.paymentTxt.visibility = View.GONE
            binding.subjectTxt.visibility = View.GONE
            binding.givesHomeworkSwitch.visibility = View.GONE
            binding.timeText.visibility = View.GONE
        }

        // Back button
        binding.backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.signupSubmitBtn.setOnClickListener {
            val name = binding.nameTxt.text.toString().trim()
            val ageRange = binding.ageTxt.text.toString().trim()
            val email = binding.emailTxt.text.toString().trim()
            val phone = binding.editTextPhone.text.toString().trim()
            val payment = binding.paymentTxt.text.toString().trim()
            val subjects = binding.subjectTxt.text.toString().trim()
            val availability = binding.timeText.text.toString().trim()
            val givesHomework = binding.givesHomeworkSwitch.isChecked.toString()

            if (name.isBlank() || ageRange.isBlank() || email.isBlank()) {
                Toast.makeText(requireContext(), "Please fill out all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //gets the role from the drop down from earliar and sees wheather a tutor or student is submitting
            if (role.equals("tutor", ignoreCase = true)) {
                dbHelper.addTutor(
                    name = name,
                    ageRange = ageRange,
                    payment = payment.ifBlank { "N/A" },
                    subjects = subjects.ifBlank { "N/A" },
                    availability = availability.ifBlank { "N/A" },
                    givesHomework = givesHomework,
                    contactPhone = phone.ifBlank { "N/A" },
                    contactEmail = email,
                    students = ""
                )
                Toast.makeText(requireContext(), "Tutor registered successfully!", Toast.LENGTH_SHORT).show()
            } else {
                dbHelper.addStudent(
                    name = name,
                    email = email
                )
                Toast.makeText(requireContext(), "Student registered successfully!", Toast.LENGTH_SHORT).show()
            }
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
