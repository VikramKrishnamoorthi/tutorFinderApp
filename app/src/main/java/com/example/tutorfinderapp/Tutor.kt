package com.example.tutorfinderapp

import android.provider.ContactsContract

data class Tutor(val name : String, val age : Int, val pricing : Int, val subjects : ArrayList<String>, val availability : String, val homeworkIntensity : String, val email : String, val phoneNumber : String)