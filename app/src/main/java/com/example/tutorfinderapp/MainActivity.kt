package com.example.tutorfinderapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.tutorfinderapp.app.DBHelper

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val dbHelper = DBHelper(this, null)
        if (savedInstanceState == null) {
            replaceFragment(LoginSignupFragment(), false)
            populateFakeData(dbHelper)
        }
    }
    fun replaceFragment(fragment: Fragment, addToBackStack : Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, fragment)
//            .addToBackStack(null)
//            .commit()
    }
    private fun populateFakeData(dbHelper: DBHelper) {

        // Fake Tutors
        val fakeTutors = listOf(
            arrayOf(
                "Alice Johnson",
                "alice.johnson@example.com",
                "10-14",
                "Math, Science",
                "30",
                "Mon, Wed, Fri 3-6PM",
                "Yes",
                "555-1234"
            ),
            arrayOf(
                "Brian Lee",
                "brian.lee@example.com",
                "13-18",
                "English, History",
                "25",
                "Tue, Thu 4-7PM",
                "No",
                "555-5678"
            ),
            arrayOf(
                "Carla Smith",
                "carla.smith@example.com",
                "6-10",
                "Reading, Writing",
                "20",
                "Mon-Fri 2-5PM",
                "Yes",
                "555-9012"
            ),
            arrayOf(
                "David Kim",
                "david.kim@example.com",
                "14-18",
                "Physics, Math",
                "35",
                "Mon, Wed 5-8PM",
                "Yes",
                "555-3456"
            ),
            arrayOf(
                "Emma Brown",
                "emma.brown@example.com",
                "10-13",
                "Art, Music",
                "22",
                "Tue, Thu 3-6PM",
                "No",
                "555-7890"
            ),
            arrayOf(
                "Frank White",
                "frank.white@example.com",
                "12-16",
                "Chemistry, Biology",
                "28",
                "Mon-Fri 4-7PM",
                "Yes",
                "555-2345"
            ),
            arrayOf(
                "Grace Liu",
                "grace.liu@example.com",
                "9-12",
                "Math, English",
                "26",
                "Tue, Thu 2-5PM",
                "Yes",
                "555-6789"
            ),
            arrayOf(
                "Henry Adams",
                "henry.adams@example.com",
                "15-18",
                "History, Geography",
                "30",
                "Mon, Wed, Fri 5-8PM",
                "No",
                "555-1122"
            ),
            arrayOf(
                "Isabella Scott",
                "isabella.scott@example.com",
                "8-11",
                "Reading, Math",
                "24",
                "Mon-Fri 3-6PM",
                "Yes",
                "555-3344"
            ),
            arrayOf(
                "Jack Miller",
                "jack.miller@example.com",
                "13-17",
                "Physics, Math",
                "32",
                "Tue, Thu 4-7PM",
                "No",
                "555-5566"
            )
        )

        fakeTutors.forEach { tutor ->
            dbHelper.addTutor(
                name = tutor[0],
                ageRange = tutor[2],
                subjects = tutor[3],
                payment = tutor[4],
                availability = tutor[5],
                givesHomework = tutor[6],
                contactPhone = tutor[7],
                contactEmail = tutor[1],
                students = ""
            )
        }

        // Fake Students
        val fakeStudents = listOf(
            arrayOf(
                "Emily Davis",
                "student1@example.com",
                "12",
                "7",
                "Math, Reading",
                "555-1111",
                "alice.johnson@example.com,carla.smith@example.com"
            ),
            arrayOf(
                "Jacob Wilson",
                "student2@example.com",
                "15",
                "10",
                "English, History",
                "555-2222",
                "brian.lee@example.com,carla.smith@example.com"
            ),
            arrayOf(
                "Sofia Martinez",
                "student3@example.com",
                "11",
                "6",
                "Science, Math",
                "555-3333",
                "alice.johnson@example.com"
            ),
            arrayOf(
                "Liam Thompson",
                "student4@example.com",
                "16",
                "11",
                "Physics, Chemistry",
                "555-4444",
                "david.kim@example.com"
            ),
            arrayOf(
                "Olivia Garcia",
                "student5@example.com",
                "12",
                "7",
                "Art, Music",
                "555-5555",
                "emma.brown@example.com"
            ),
            arrayOf(
                "Noah Anderson",
                "student6@example.com",
                "14",
                "9",
                "Biology, Chemistry",
                "555-6666",
                "frank.white@example.com"
            ),
            arrayOf(
                "Ava Hernandez",
                "student7@example.com",
                "10",
                "5",
                "Math, English",
                "555-7777",
                "grace.liu@example.com"
            ),
            arrayOf(
                "William Robinson",
                "student8@example.com",
                "17",
                "12",
                "History, Geography",
                "555-8888",
                "henry.adams@example.com"
            ),
            arrayOf(
                "Mia Clark",
                "student9@example.com",
                "9",
                "4",
                "Reading, Math",
                "555-9999",
                "isabella.scott@example.com"
            ),
            arrayOf(
                "James Lewis",
                "student10@example.com",
                "15",
                "10",
                "Physics, Math",
                "555-0000",
                "jack.miller@example.com"
            )
        )

        fakeStudents.forEach { student ->
            dbHelper.addStudent(
                name = student[0],
                email = student[1]
            )
        }
    }
}
