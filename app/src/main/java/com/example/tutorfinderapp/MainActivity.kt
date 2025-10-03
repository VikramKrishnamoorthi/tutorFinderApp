package com.example.tutorfinderapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {
    private lateinit var tutorList : ArrayList<Tutor>
    private lateinit var budgetBar : SeekBar
    private lateinit var searchButton : Button
    private lateinit var searchOutput : RecyclerView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        budgetBar = findViewById<SeekBar>(R.id.budgetSelector)
        searchButton = findViewById<Button>(R.id.searchButton)
        searchOutput = findViewById<RecyclerView>(R.id.searchOutput)
        tutorList = ArrayList<Tutor>()
        val dataList = ArrayList<String>()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dataList)
        tutorList.add(Tutor("placeholder",18, 30, arrayListOf("calculus", "physics"), "Monday 5-10 pm", "low", "loremIpsum@fakeMail.abc", "1234567890"))
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        searchButton.setOnClickListener {
            System.out.println(budgetBar.progress)
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
}