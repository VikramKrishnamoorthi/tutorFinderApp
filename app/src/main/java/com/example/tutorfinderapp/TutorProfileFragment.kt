package com.example.tutorfinderapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tutorfinderapp.app.DBHelper
import com.example.tutorfinderapp.databinding.FragmentTutorProfileBinding

class TutorProfileFragment : Fragment() {

    private var _binding: FragmentTutorProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DBHelper
    private var tutorEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tutorEmail = arguments?.getString("TUTOR_EMAIL")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTutorProfileBinding.inflate(inflater, container, false)
        dbHelper = DBHelper(requireContext(), null)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        tutorEmail?.let {
            loadTutorData(it)
        }

        binding.saveBtn.setOnClickListener {
            val email = tutorEmail ?: return@setOnClickListener
            val name = binding.nameEditText.text.toString()
            val ageRange = binding.ageRangeEditText.text.toString()
            val payment = binding.paymentEditText.text.toString()
            val subjects = binding.subjectsEditText.text.toString()
            val availability = binding.availabilityEditText.text.toString()
            val givesHomework = binding.givesHomeworkEditText.text.toString()
            val phone = binding.phoneEditText.text.toString()
            val students = binding.studentsTxt.text.toString()

            val success = dbHelper.updateTutor(
                email = email,
                name = name,
                ageRange = ageRange,
                payment = payment,
                subjects = subjects,
                availability = availability,
                givesHomework = givesHomework,
                phone = phone,
                students = students
            )

            if (success) {
                Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Update failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadTutorData(email: String) {
        val cursor = dbHelper.getTutors()
        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TUTOR_EMAIL)) == email) {
                binding.nameEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TUTOR_NAME)))
                binding.ageRangeEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TUTOR_AGE_RANGE)))
                binding.paymentEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TUTOR_PAYMENT)))
                binding.subjectsEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TUTOR_SUBJECTS)))
                binding.availabilityEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TUTOR_AVAILABILITY)))
                binding.givesHomeworkEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TUTOR_GIVES_HOMEWORK)))
                binding.phoneEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TUTOR_PHONE)))
                binding.emailEditText.setText(email)
                binding.studentsTxt.text = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TUTOR_STUDENTS))
                break
            }
        }
        cursor.close()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
