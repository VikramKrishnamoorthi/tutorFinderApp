package com.example.tutorfinderapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tutorfinderapp.app.DBHelper
import com.example.tutorfinderapp.databinding.FragmentMyStudentsBinding

class MyStudentsFragment : Fragment() {

    private lateinit var dbHelper: DBHelper
    private lateinit var studentListView: ListView
    private var tutorEmail: String? = null

    private var _binding: FragmentMyStudentsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tutorEmail = arguments?.getString("TUTOR_EMAIL")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyStudentsBinding.inflate(inflater, container, false)
        val view = binding.root

        dbHelper = DBHelper(requireContext(), null)
        studentListView = view.findViewById(R.id.studentListView)
        loadStudents()
        binding.backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    private fun loadStudents() {
        if (tutorEmail == null) {
            Toast.makeText(requireContext(), "Tutor email not found", Toast.LENGTH_SHORT).show()
            return
        }

        val students = dbHelper.studentsofTutor(tutorEmail!!)

        if (students.isEmpty()) {
            Toast.makeText(requireContext(), "No students found", Toast.LENGTH_SHORT).show()
            return
        }
        val studentNames = students.map { pair ->
            "${pair.second} (${pair.first})"
        }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            studentNames
        )

        studentListView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
