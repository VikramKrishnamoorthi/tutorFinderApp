package com.example.tutorfinderapp

import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tutorfinderapp.app.DBHelper
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyTutorsFragment : Fragment() {

    private lateinit var dbHelper: DBHelper
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: MyTutorsAdapter
    private var studentEmail: String = "student@example.com"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_my_tutors, container, false)
        recycler = v.findViewById(R.id.myTutorsRecycler)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = MyTutorsAdapter(listOf())
        recycler.adapter = adapter
        dbHelper = DBHelper(requireContext(), null)
        arguments?.getString("STUDENT_EMAIL")?.let { studentEmail = it }

        loadMyTutors()
        return v
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
                        val names = tutorsStr.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        names.forEach { name -> result.add(TutorSimple(name)) }
                    }
                    break
                }
            }
            cursor.close()
            withContext(Dispatchers.Main) { adapter.updateList(result) }
        }
    }

    data class TutorSimple(val name: String)

    class MyTutorsAdapter(private var items: List<TutorSimple>) : RecyclerView.Adapter<MyTutorsAdapter.VH>() {
        inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameTv: TextView = itemView.findViewById(R.id.myTutorName)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_my_tutor, parent, false)
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
