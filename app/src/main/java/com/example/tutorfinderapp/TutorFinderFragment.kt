package com.example.tutorfinderapp

import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tutorfinderapp.app.DBHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TutorFinderFragment : Fragment() {

    private lateinit var dbHelper: DBHelper
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: TutorAdapter
    private var allTutors = mutableListOf<TutorModel>()
    private var studentEmail: String = "student@example.com" // fallback

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tutor_finder, container, false)
        dbHelper = DBHelper(requireContext(), null)
        recycler = view.findViewById(R.id.tutorRecycler)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = TutorAdapter(listOf()) { tutor ->
            // Request tutor: add relation student <-> tutor (on IO thread)
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    // 1) add student to tutor's students list
                    val tutorsCursor = dbHelper.getTutors()
                    // we will update by tutor email
                    val existingStudents = getTutorStudentsString(tutor.email)
                    val updatedStudents = if (existingStudents.isBlank()) studentEmail else "$existingStudents,$studentEmail"
                    dbHelper.updateTutorStudents(tutor.email, updatedStudents)

                    // 2) add tutor to student's tutors list
                    val studentExistingTutors = getStudentTutorsString(studentEmail)
                    val updatedTutors = if (studentExistingTutors.isBlank()) tutor.name else "$studentExistingTutors,${tutor.name}"
                    dbHelper.updateStudentTutors(studentEmail, updatedTutors)

                    // refresh UI on main
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Requested ${tutor.name}", Toast.LENGTH_SHORT).show()
                        loadTutorsAndShow() // refresh list to reflect changes if you want
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error requesting tutor: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        recycler.adapter = adapter

        // filters
        val subjectSpinner = view.findViewById<Spinner>(R.id.subjectFilter)
        val paymentSpinner = view.findViewById<Spinner>(R.id.paymentFilter)
        val availabilitySpinner = view.findViewById<Spinner>(R.id.availabilityFilter)
        val resetFab = view.findViewById<View>(R.id.resetFab)

        // student email from args (if provided by login)
        arguments?.getString("STUDENT_EMAIL")?.let { studentEmail = it }

        // populate spinners
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

        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                applyFilters()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        subjectSpinner.onItemSelectedListener = listener
        paymentSpinner.onItemSelectedListener = listener
        availabilitySpinner.onItemSelectedListener = listener

        resetFab.setOnClickListener {
            subjectSpinner.setSelection(0)
            paymentSpinner.setSelection(0)
            availabilitySpinner.setSelection(0)
            applyFilters()
        }

        // initial load
        loadTutorsAndShow()
        return view
    }

    private fun getTutorStudentsString(tutorEmail: String): String {
        // read cursor to find the tutor and return students column
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
        // read spinner values from UI thread
        val subject = view?.findViewById<Spinner>(R.id.subjectFilter)?.selectedItem.toString()
        val payment = view?.findViewById<Spinner>(R.id.paymentFilter)?.selectedItem.toString()
        val availability = view?.findViewById<Spinner>(R.id.availabilityFilter)?.selectedItem.toString()

        val filtered = allTutors.filter { t ->
            val okSubject = (subject == "All") || t.subjects.contains(subject, ignoreCase = true)
            val okPayment = (payment == "All") || t.payment.contains(payment.replace("$",""), ignoreCase = true)
            val okAvailability = (availability == "All") || t.availability.contains(availability, ignoreCase = true)
            okSubject && okPayment && okAvailability
        }
        adapter.updateList(filtered)
    }

    // lightweight model
    data class TutorModel(val id: Int, val name: String, val subjects: String, val payment: String, val availability: String, val email: String)
}
