package com.example.tutorfinderapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
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
        var goodemailabletoUse = ""
        //Idk why I have to use a dummy variable, something about the compiler not like that the string might be null
        // it kept saying "String? = String" so i made this var to get around that
        tutorEmail?.let {
            val data = dbHelper.getTutorByEmail(it)
            loadTutorData(it)
            goodemailabletoUse = it
        }

        binding.saveBtn.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val ageRange = binding.ageRangeEditText.text.toString()
            val payment = binding.paymentEditText.text.toString()
            val subjects = binding.subjectsEditText.text.toString()
            val availability = binding.availabilityEditText.text.toString()
            val givesHomework = binding.givesHomeworkEditText.text.toString()
            val phone = binding.phoneEditText.text.toString()
            val students = binding.studentsTxt.text.toString()

            val success = dbHelper.updateTutor(
                email = goodemailabletoUse,
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTutorProfileBinding.inflate(inflater, container, false)
        dbHelper = DBHelper(requireContext(), null)
        return binding.root
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

                val students = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TUTOR_STUDENTS))
                binding.studentsTxt.text = students

                break
            }
        }
        cursor.close()
    }
}
