package com.example.tutorfinderapp

import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tutorfinderapp.app.DBHelper
import com.example.tutorfinderapp.databinding.FragmentMyTutorsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyTutorsFragment : Fragment() {

    private var _binding: FragmentMyTutorsBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DBHelper
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: MyTutorsAdapter
    private var studentEmail: String = "student@example.com"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyTutorsBinding.inflate(inflater, container, false)
        dbHelper = DBHelper(requireContext(), null)

        recycler = binding.myTutorsRecycler
        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = MyTutorsAdapter(listOf())
        recycler.adapter = adapter

        // Back button
        binding.backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Get student email from args
        arguments?.getString("STUDENT_EMAIL")?.let { studentEmail = it }

        // Load tutors
        loadMyTutors()

        return binding.root
    }

    private fun loadMyTutors() {
        lifecycleScope.launch(Dispatchers.IO) {
            val result = mutableListOf<TutorSimple>()
            val cursor: Cursor = dbHelper.getStudents()
            while (cursor.moveToNext()) {
                val email = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.STUDENT_EMAIL))
                if (email == studentEmail) {
                    val tutorsStr = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.STUDENT_TUTORS)) ?: ""
                    if (tutorsStr.isNotBlank()) {
                        tutorsStr.split(",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }
                            .forEach { name -> result.add(TutorSimple(name)) }
                    }
                    break
                }
            }
            cursor.close()
            withContext(Dispatchers.Main) { adapter.updateList(result) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class TutorSimple(val name: String)

    class MyTutorsAdapter(private var items: List<TutorSimple>) :
        RecyclerView.Adapter<MyTutorsAdapter.VH>() {

        inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameTv: TextView = itemView.findViewById(R.id.myTutorName)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_my_tutor, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.nameTv.text = items[position].name
        }

        override fun getItemCount() = items.size

        fun updateList(newItems: List<TutorSimple>) {
            items = newItems
            notifyDataSetChanged()
        }
    }
}
