package com.example.tutorfinderapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class StudentHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_home, container, false)

        val myTutorsBtn = view.findViewById<Button>(R.id.myTutorsButton)
        val findTutorsBtn = view.findViewById<Button>(R.id.findTutorsButton)

        myTutorsBtn.setOnClickListener {
            val args = Bundle()
            arguments?.getString("STUDENT_EMAIL")?.let { args.putString("STUDENT_EMAIL", it) }
            val frag = MyTutorsFragment()
            frag.arguments = args
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, frag)
                .addToBackStack(null)
                .commit()
        }

        findTutorsBtn.setOnClickListener {
            val args = Bundle()
            arguments?.getString("STUDENT_EMAIL")?.let { args.putString("STUDENT_EMAIL", it) }
            val frag = TutorFinderFragment()
            frag.arguments = args
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, frag)
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}
