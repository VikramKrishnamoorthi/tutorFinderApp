package com.example.tutorfinderapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.tutorfinderapp.app.DBHelper
import com.example.tutorfinderapp.databinding.FragmentLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var role: String? = null
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = arguments?.getString("ROLE")
    }

    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savedInstanceState : Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        dbHelper = DBHelper(requireContext(), null)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val formattedRole = role?.replaceFirstChar { c: Char -> c.uppercaseChar() } ?: "User"
        binding.roleTxt.text = "$formattedRole Login"

        binding.submit.setOnClickListener {
            val name = binding.nameTxt.text.toString().trim()
            val email = binding.emailTxt.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                if (role.equals("student", ignoreCase = true)) {
                    handleStudentLogin(name, email)
                } else if (role.equals("tutor", ignoreCase = true)) {
                    handleTutorLogin(name, email)
                }
            }
        }
    }

    private suspend fun handleStudentLogin(name: String, email: String) {
        var exists = false
        val cursor = dbHelper.getStudents()
        var ID = ""
        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.STUDENT_EMAIL)) == email) {
                exists = true
                ID = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.STUDENT_ID))
                break
            }
        }
        cursor.close()

        if (!exists) {
            failedLogin(binding.root)
        }

        withContext(Dispatchers.Main) {
            Toast.makeText(requireContext(), "Welcome, Student!", Toast.LENGTH_SHORT).show()
            openStudentHome(email,ID)
        }
    }

    private suspend fun handleTutorLogin(name: String, email: String) {
        var exists = false
        val cursor = dbHelper.getTutors()
        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TUTOR_EMAIL)) == email) {
                exists = true
                break
            }
        }
        cursor.close()

        if (!exists) {
            failedLogin(binding.root)
        }

        withContext(Dispatchers.Main) {
            Toast.makeText(requireContext(), "Welcome, Tutor!", Toast.LENGTH_SHORT).show()
            openTutorHome(email)
        }
    }

    private fun openStudentHome(studentEmail: String, ID: String) {
        val fragment = StudentHomeFragment()
        val bundle = Bundle().apply {
            putString("STUDENT_EMAIL", studentEmail)
        }
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun openTutorHome(tutorEmail: String) {
        val fragment = TutorHomeFragment()
        val bundle = Bundle().apply {
            putString("TUTOR_EMAIL", tutorEmail)
        }
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }


    private fun failedLogin(view: View){
        clearFields(view)
        Toast.makeText(requireContext(), "This user doesn't seem to exist", Toast.LENGTH_SHORT).show()
    }

    private fun clearFields(view: View){
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                clearFields(view.getChildAt(i))
            }
        } else if (view is android.widget.EditText) {
            view.text?.clear()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
