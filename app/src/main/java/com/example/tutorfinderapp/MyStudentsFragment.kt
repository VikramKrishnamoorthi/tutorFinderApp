package com.example.tutorfinderapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tutorfinderapp.app.DBHelper
import com.example.tutorfinderapp.databinding.FragmentMyStudentsBinding

class MyStudentsFragment : Fragment() {

    private var _binding: FragmentMyStudentsBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DBHelper
    private var tutorEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tutorEmail = arguments?.getString("TUTOR_EMAIL")
    }

    override fun onCreateView(
        inflater: android.view.LayoutInflater, container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View {
        _binding = FragmentMyStudentsBinding.inflate(inflater, container, false)
        dbHelper = DBHelper(requireContext(), null)

        // Back button
        binding.backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Load students safely
        try {
            loadStudents()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error loading students: ${e.message}", Toast.LENGTH_LONG).show()
        }

        return binding.root
    }

    private fun loadStudents() {
        val email = tutorEmail
        if (email.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Tutor email not provided", Toast.LENGTH_SHORT).show()
            return
        }

        // Safe call to DBHelper
        val students = try {
            dbHelper.studentsofTutor(email) ?: emptyList()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "DB error: ${e.message}", Toast.LENGTH_LONG).show()
            return
        }

        if (students.isEmpty()) {
            Toast.makeText(requireContext(), "No students found", Toast.LENGTH_SHORT).show()
            binding.studentListView.adapter = null
            return
        }

        val studentNames = students.map { pair -> "${pair.second} (${pair.first})" }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            studentNames
        )

        binding.studentListView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
