package com.example.quiz

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val tvTitle: TextView = findViewById(R.id.tvDetailTitle)
        val tvStudent: TextView = findViewById(R.id.tvDetailStudent)
        val tvRoll: TextView = findViewById(R.id.tvDetailRoll)
        val tvCategory: TextView = findViewById(R.id.tvDetailCategory)
        val tvPriority: TextView = findViewById(R.id.tvDetailPriority)
        val tvStatus: TextView = findViewById(R.id.tvDetailStatus)
        val tvDate: TextView = findViewById(R.id.tvDetailDate)
        val tvDescription: TextView = findViewById(R.id.tvDetailDescription)
        val toolbar: MaterialToolbar = findViewById(R.id.detailToolbar)

        tvTitle.text = intent.getStringExtra("complaintTitle")
        tvStudent.text = "By: ${intent.getStringExtra("studentName")}"
        tvRoll.text = "Roll No: ${intent.getStringExtra("rollNumber")}"
        tvCategory.text = intent.getStringExtra("complaintCategory")
        tvPriority.text = intent.getStringExtra("priorityLevel")
        tvStatus.text = intent.getStringExtra("status")
        tvDate.text = intent.getStringExtra("timestamp")
        tvDescription.text = intent.getStringExtra("complaintDescription")

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}
