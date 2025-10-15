package com.example.tutorfinderapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
class TutorAdapter(
    private var tutors: List<TutorFinderFragment.TutorModel>,
    private val onRequest: (TutorFinderFragment.TutorModel) -> Unit
) : RecyclerView.Adapter<TutorAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTv: TextView = itemView.findViewById(R.id.tutorName)
        val subjectsTv: TextView = itemView.findViewById(R.id.tutorSubjects)
        val paymentTv: TextView = itemView.findViewById(R.id.tutorPayment)
        val availabilityTv: TextView = itemView.findViewById(R.id.tutorAvailability)
        val requestBtn: Button = itemView.findViewById(R.id.requestTutorBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.tutor_card, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val t = tutors[position]
        holder.nameTv.text = t.name
        holder.subjectsTv.text = "Subjects: ${t.subjects}"
        holder.paymentTv.text = "Payment: ${t.payment}"
        holder.availabilityTv.text = "Available: ${t.availability}"
        holder.requestBtn.setOnClickListener { onRequest(t) }
    }

    override fun getItemCount(): Int = tutors.size

    fun updateList(newList: List<TutorFinderFragment.TutorModel>) {
        tutors = newList
        notifyDataSetChanged()
    }
}
