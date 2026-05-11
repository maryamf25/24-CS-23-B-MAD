package com.example.quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdminItemAdapter(
    private val items: List<String>,
    private val onDelete: (String) -> Unit
) : RecyclerView.Adapter<AdminItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvAdminItemName)
        val btnDelete: ImageButton = view.findViewById(R.id.btnAdminItemDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_data, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val name = items[position]
        holder.tvName.text = name
        holder.btnDelete.setOnClickListener { onDelete(name) }
    }

    override fun getItemCount() = items.size
}
