package com.example.tutorfinderapp

import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tutorfinderapp.app.DBHelper
import com.example.tutorfinderapp.databinding.FragmentTutorFinderBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TutorFinderFragment : Fragment() {

    private lateinit var dbHelper: DBHelper
    private lateinit var adapter: TutorAdapter
    private var allTutors = mutableListOf<TutorModel>()
    private var studentEmail: String = "student@example.com"

    private var _binding: FragmentTutorFinderBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTutorFinderBinding.inflate(inflater, container, false)

        dbHelper = DBHelper(requireContext(), null)

        // RecyclerView setup
        binding.tutorRecycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = TutorAdapter(listOf()) { tutor ->
            // Request tutor
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val existingStudents = getTutorStudentsString(tutor.email)
                    val updatedStudents = if (existingStudents.isBlank()) studentEmail else "$existingStudents,$studentEmail"
                    dbHelper.updateTutorStudents(tutor.email, updatedStudents)

                    val studentExistingTutors = getStudentTutorsString(studentEmail)
                    val updatedTutors = if (studentExistingTutors.isBlank()) tutor.name else "$studentExistingTutors,${tutor.name}"
                    dbHelper.updateStudentTutors(studentEmail, updatedTutors)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Requested ${tutor.name}", Toast.LENGTH_SHORT).show()
                        loadTutorsAndShow()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error requesting tutor: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        binding.tutorRecycler.adapter = adapter

        binding.backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        val subjectSpinner: Spinner = binding.subjectFilter
        val paymentSpinner: Spinner = binding.paymentFilter
        val availabilitySpinner: Spinner = binding.availabilityFilter

        binding.resetFab.setOnClickListener {
            subjectSpinner.setSelection(0)
            paymentSpinner.setSelection(0)
            availabilitySpinner.setSelection(0)
            applyFilters()
        }

        arguments?.getString("STUDENT_EMAIL")?.let { studentEmail = it }

        subjectSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.subject_options)).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        paymentSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.payment_options)).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        availabilitySpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.availability_options)).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        val listener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, pos: Int, id: Long) {
                applyFilters()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }

        subjectSpinner.onItemSelectedListener = listener
        paymentSpinner.onItemSelectedListener = listener
        availabilitySpinner.onItemSelectedListener = listener

        // initial load
        loadTutorsAndShow()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getTutorStudentsString(tutorEmail: String): String {
        val c: Cursor = dbHelper.getTutors()
        var students = ""
        while (c.moveToNext()) {
            val email = c.getString(c.getColumnIndexOrThrow(DBHelper.TUTOR_EMAIL))
            if (email == tutorEmail) {
                students = c.getString(c.getColumnIndexOrThrow(DBHelper.TUTOR_STUDENTS)) ?: ""
                break
            }
        }
        c.close()
        return students ?: ""
    }

    private fun getStudentTutorsString(studentEmail: String): String {
        val c = dbHelper.getStudents()
        var tutors = ""
        while (c.moveToNext()) {
            val email = c.getString(c.getColumnIndexOrThrow(DBHelper.STUDENT_EMAIL))
            if (email == studentEmail) {
                tutors = c.getString(c.getColumnIndexOrThrow(DBHelper.STUDENT_TUTORS)) ?: ""
                break
            }
        }
        c.close()
        return tutors ?: ""
    }

    private fun loadTutorsAndShow() {
        lifecycleScope.launch(Dispatchers.IO) {
            val list = mutableListOf<TutorModel>()
            val cursor = dbHelper.getTutors()
            while (cursor.moveToNext()) {
                val t = TutorModel(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.TUTOR_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TUTOR_NAME)),
                    subjects = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TUTOR_SUBJECTS)) ?: "",
                    payment = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TUTOR_PAYMENT)) ?: "",
                    availability = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TUTOR_AVAILABILITY)) ?: "",
                    email = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TUTOR_EMAIL)) ?: ""
                )
                list.add(t)
            }
            cursor.close()
            allTutors = list
            withContext(Dispatchers.Main) {
                adapter.updateList(allTutors)
            }
        }
    }

    private fun applyFilters() {
        val subject = binding.subjectFilter.selectedItem.toString()
        val payment = binding.paymentFilter.selectedItem.toString()
        val availability = binding.availabilityFilter.selectedItem.toString()

        val filtered = allTutors.filter { t ->
            val okSubject = (subject == "All") || t.subjects.contains(subject, ignoreCase = true)
            val okPayment = (payment == "All") || t.payment.contains(payment.replace("$",""), ignoreCase = true)
            val okAvailability = (availability == "All") || t.availability.contains(availability, ignoreCase = true)
            okSubject && okPayment && okAvailability
        }
        adapter.updateList(filtered)
    }

    data class TutorModel(
        val id: Int,
        val name: String,
        val subjects: String,
        val payment: String,
        val availability: String,
        val email: String
    )
}
