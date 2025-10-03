package com.example.tutorfinderapp

import android.annotation.SuppressLint
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


class MainActivity : AppCompatActivity() {
    private lateinit var tutorList : ArrayList<Tutor>
    private lateinit var budgetBar : SeekBar
    private lateinit var searchButton : Button
    private lateinit var searchOutput : ListView
    private lateinit var subjectInput : EditText
    private lateinit var intensityInput : Spinner
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
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dataList)
        searchOutput.adapter = adapter
        tutorList.add(Tutor("placeholder",18, 30, arrayListOf("calculus", "physics"), "Monday 5-10 pm", "low", "loremIpsum@fakeMail.abc", "1234567890"))
        tutorList.add(Tutor("another",18, 45, arrayListOf("english", "composition"), "Monday 5-10 pm", "medium", "loremIpsum@fakeMail.abc", "1234567890"))
        tutorList.add(Tutor("three",18, 25, arrayListOf("abcde", "fghij"), "Monday 5-10 pm", "high", "loremIpsum@fakeMail.abc", "1234567890"))
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
}