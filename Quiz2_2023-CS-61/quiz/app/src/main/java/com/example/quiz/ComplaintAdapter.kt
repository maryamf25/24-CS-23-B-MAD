package com.example.quiz

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.model.Complaint

class ComplaintAdapter(
    private val complaints: List<Complaint>,
    private val onItemClick: (Complaint) -> Unit
) : RecyclerView.Adapter<ComplaintAdapter.ComplaintViewHolder>() {

    class ComplaintViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvItemTitle)
        val tvStudent: TextView = view.findViewById(R.id.tvItemStudent)
        val tvCategory: TextView = view.findViewById(R.id.tvItemCategory)
        val tvPriority: TextView = view.findViewById(R.id.tvItemPriority)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplaintViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_complaint, parent, false)
        return ComplaintViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComplaintViewHolder, position: Int) {
        val complaint = complaints[position]
        holder.tvTitle.text = complaint.complaintTitle
        holder.tvStudent.text = "${complaint.studentName} (${complaint.rollNumber})"
        holder.tvCategory.text = complaint.complaintCategory
        holder.tvPriority.text = complaint.priorityLevel
        holder.tvPriority.setBackgroundResource(R.drawable.priority_badge_white)
        val drawable = holder.tvPriority.background as GradientDrawable
        
        // Use primary color for text on white badge
        holder.tvPriority.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.primary))

        when (complaint.priorityLevel?.lowercase()) {
            "low" -> drawable.setStroke(2, ContextCompat.getColor(holder.itemView.context, R.color.priority_low))
            "medium" -> drawable.setStroke(2, ContextCompat.getColor(holder.itemView.context, R.color.priority_medium))
            "high" -> drawable.setStroke(2, ContextCompat.getColor(holder.itemView.context, R.color.priority_high))
            "urgent" -> drawable.setStroke(2, ContextCompat.getColor(holder.itemView.context, R.color.priority_urgent))
        }
        holder.itemView.setOnClickListener { onItemClick(complaint) }
    }

    override fun getItemCount() = complaints.size
}
