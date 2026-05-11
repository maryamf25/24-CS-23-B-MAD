package com.example.quiz

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quiz.firebase.FirebaseHelper
import com.example.quiz.model.Complaint
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.Timestamp

class RegistrationActivity : AppCompatActivity() {

    private lateinit var etStudentName: EditText
    private lateinit var etRollNumber: EditText
    private lateinit var etComplaintTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerPriority: Spinner
    private lateinit var btnSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        etStudentName = findViewById(R.id.etStudentName)
        etRollNumber = findViewById(R.id.etRollNumber)
        etComplaintTitle = findViewById(R.id.etComplaintTitle)
        etDescription = findViewById(R.id.etDescription)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        spinnerPriority = findViewById(R.id.spinnerPriority)
        btnSubmit = findViewById(R.id.btnSubmit)
        val toolbar = findViewById<MaterialToolbar>(R.id.registrationToolbar)

        toolbar.setNavigationOnClickListener { finish() }

        fetchDynamicData()

        btnSubmit.setOnClickListener {
            validateAndSubmit()
        }
    }

    private fun fetchDynamicData() {
        // Fetch Categories
        FirebaseHelper.db.collection("categories")
            .get()
            .addOnSuccessListener { result ->
                val categories = mutableListOf<String>()
                for (document in result) {
                    categories.add(document.getString("name") ?: "")
                }
                if (categories.isEmpty()) {
                    // Fallback if none in DB
                    categories.addAll(resources.getStringArray(R.array.complaint_categories))
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCategory.adapter = adapter
            }

        // Fetch Priorities
        FirebaseHelper.db.collection("priorities")
            .get()
            .addOnSuccessListener { result ->
                val priorities = mutableListOf<String>()
                for (document in result) {
                    priorities.add(document.getString("name") ?: "")
                }
                if (priorities.isEmpty()) {
                    // Fallback if none in DB
                    priorities.addAll(resources.getStringArray(R.array.priority_levels))
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorities)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerPriority.adapter = adapter
            }
    }

    private fun validateAndSubmit() {
        val name = etStudentName.text.toString().trim()
        val roll = etRollNumber.text.toString().trim()
        val title = etComplaintTitle.text.toString().trim()
        val desc = etDescription.text.toString().trim()
        
        val categoryObj = spinnerCategory.selectedItem
        val priorityObj = spinnerPriority.selectedItem

        if (name.isEmpty() || roll.isEmpty() || title.isEmpty() || desc.isEmpty() || categoryObj == null || priorityObj == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val category = categoryObj.toString()
        val priority = priorityObj.toString()

        val complaint = Complaint(
            studentName = name,
            rollNumber = roll,
            complaintTitle = title,
            complaintCategory = category,
            priorityLevel = priority,
            complaintDescription = desc,
            status = "Pending",
            timestamp = Timestamp.now()
        )

        btnSubmit.isEnabled = false
        FirebaseHelper.db.collection("complaints")
            .add(complaint)
            .addOnSuccessListener {
                Toast.makeText(this, "Complaint Registered Successfully", Toast.LENGTH_SHORT).show()
                clearFields()
                btnSubmit.isEnabled = true
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                btnSubmit.isEnabled = true
            }
    }

    private fun clearFields() {
        etStudentName.text.clear()
        etRollNumber.text.clear()
        etComplaintTitle.text.clear()
        etDescription.text.clear()
    }
}
