package com.example.tutorfinderapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SeekBar
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.tutorfinderapp.app.DBHelper
import com.example.tutorfinderapp.app.DBHelper.Companion.ID_COL


class MainActivity : AppCompatActivity() {
    private lateinit var tutorList : ArrayList<Tutor>
    private lateinit var budgetBar : SeekBar
    private lateinit var searchButton : Button
    private lateinit var searchOutput : ListView
    private lateinit var subjectInput : EditText
    private lateinit var intensityInput : Spinner
    private lateinit var db : DBHelper
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        budgetBar = findViewById<SeekBar>(R.id.budgetSelector)
        searchButton = findViewById<Button>(R.id.searchButton)
        searchOutput = findViewById<ListView>(R.id.searchOutput)
        subjectInput = findViewById<EditText>(R.id.subjectInput)
        tutorList = ArrayList<Tutor>()
        intensityInput = findViewById<Spinner>(R.id.homeworkIntensityInput)
        val intensities = arrayListOf<String>("low", "medium", "high", "any")
        val intensityAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, intensities)
        intensityInput.adapter = intensityAdapter
        intensityAdapter.notifyDataSetChanged()
        val dataList = ArrayList<String>()
        db = DBHelper(this, null)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dataList)
        searchOutput.adapter = adapter
        // sample data from GPT
//        db.addTutor(
//            name = "Alice Johnson",
//            ageRange = "29",
//            payment = "25",
//            subjects = "Math, Physics",
//            availability = "Mon 3-7 PM",
//            givesHomework = "medium",
//            contactPhone = "555-123-4567",
//            contactEmail = "alice.johnson@example.com",
//            students = ""
//        )
        db.addTutor("test","25","30","Math","Mondays","none","123-456-7890","loremipsum@fakemail.com","")

//        tutorList.add(
//            Tutor(
//                name = "Brian Smith",
//                age = 34,
//                pricing = 30,
//                subjects = arrayListOf("English", "History"),
//                availability = "Weekends, 10 AM - 4 PM",
//                homeworkIntensity = "low",
//                email = "brian.smith@example.com",
//                phoneNumber = "555-987-6543"
//            )
//        )
//
//        tutorList.add(
//            Tutor(
//                name = "Carla Ramirez",
//                age = 27,
//                pricing = 20,
//                subjects = arrayListOf("Biology", "Chemistry"),
//                availability = "Tue & Thu, 5-9 PM",
//                homeworkIntensity = "high",
//                email = "carla.ramirez@example.com",
//                phoneNumber = "555-456-7890"
//            )
//        )
//
//        tutorList.add(
//            Tutor(
//                name = "David Kim",
//                age = 40,
//                pricing = 35,
//                subjects = arrayListOf("Computer Science", "Math"),
//                availability = "Mon-Sat, 6-10 PM",
//                homeworkIntensity = "medium",
//                email = "david.kim@example.com",
//                phoneNumber = "555-111-2222"
//            )
//        )
//
//        tutorList.add(
//            Tutor(
//                name = "Emma Wilson",
//                age = 31,
//                pricing = 28,
//                subjects = arrayListOf("Art", "English"),
//                availability = "Wed & Fri, 2-6 PM",
//                homeworkIntensity = "low",
//                email = "emma.wilson@example.com",
//                phoneNumber = "555-333-4444"
//            )
//        )
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        searchButton.setOnClickListener {
            System.out.println(budgetBar.progress)
            dataList.clear()
            for (item in filterByPricing(0, budgetBar.progress, subjectFilter(subjectInput.text.toString(), homeworkIntensityFilter(intensityInput.selectedItem.toString(), tutorList)))){
                dataList.add(item.name)
            }
            adapter.notifyDataSetChanged()
        }
        getTutors()
        addStudent(tutorList[0],"lorem ipsum")
    }
    fun filterByPricing(lowerLimit : Int, upperLimit : Int, arrayList : ArrayList<Tutor>) : ArrayList<Tutor>{
        val result = ArrayList<Tutor>()
        for(item in arrayList){
            if (item.pricing >= lowerLimit && item.pricing <= upperLimit){
                result.add(item)
            }
        }
        return result
    }
    fun subjectFilter(subject : String, tutorList : ArrayList<Tutor>) : ArrayList<Tutor>{
        val results = ArrayList<Tutor>()
        for(item in tutorList){
            if(item.subjects.contains(subject)){
                results.add(item)
            }
        }
        return results
    }
    fun homeworkIntensityFilter(intensity : String, tutorList: ArrayList<Tutor>) : ArrayList<Tutor>{
        val results = ArrayList<Tutor>()
        for (item in tutorList) {
            if (item.homeworkIntensity.equals(intensity) || intensity.equals("any")) {
                results.add(item)
            }
        }
        return results
    }
    fun getTutors(){
        val cursor = db.getTutors()
        cursor.moveToFirst()
        cursor.use {
            while (it.moveToNext()) {
                tutorList.add(Tutor(it.getString(it.getColumnIndexOrThrow("name")),it.getString(it.getColumnIndexOrThrow("age_range")).toInt(),it.getString(it.getColumnIndexOrThrow("payment")).toInt(),it.getString(it.getColumnIndexOrThrow("subjects")),it.getString(it.getColumnIndexOrThrow("availability")),it.getString(it.getColumnIndexOrThrow("gives_homework")),it.getString(it.getColumnIndexOrThrow("contact_email")),it.getString(it.getColumnIndexOrThrow("contact_phone")),it.getString(it.getColumnIndexOrThrow("students")),it.getInt(it.getColumnIndexOrThrow("id"))))
            }
        }
//        System.out.println(cursor.getString(cursor.getColumnIndexOrThrow("name")))
    }
    fun addStudent(tutor: Tutor, studentName : String){
        tutor.students = tutor.students + ", " + studentName
        val dataBaseWriter = db.writableDatabase
        val values = ContentValues().apply {
            put("students", tutor.students)
        }
        val selection = "$ID_COL = ?"
        val selectionArgs = arrayOf(tutor.id.toString())
        val count = dataBaseWriter.update(
            "tutor_table",
            values,
            selection,
            selectionArgs
        )
    }
}