package com.example.tutorfinderapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tutorfinderapp.databinding.FragmentTutorHomeBinding

class TutorHomeFragment : Fragment() {
    private var _binding: FragmentTutorHomeBinding? = null
    private val binding get() = _binding!!
    private var tutorEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tutorEmail = arguments?.getString("TUTOR_EMAIL")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTutorHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tutorWelcomeTxt.text = "Welcome, $tutorEmail!"

        binding.editProfileBtn.setOnClickListener {
            val fragment = TutorProfileFragment()
            val bundle = Bundle().apply {
                putString("TUTOR_EMAIL", tutorEmail)
            }
            fragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

//        binding.viewStudentsBtn.setOnClickListener {
//            val fragment = TutorStudentsFragment()
//            val bundle = Bundle().apply {
//                putString("TUTOR_EMAIL", tutorEmail)
//            }
//            fragment.arguments = bundle
//
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, fragment)
//                .addToBackStack(null)
//                .commit()
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
