package com.example.quiz

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.firebase.FirebaseHelper
import com.google.android.material.appbar.MaterialToolbar

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var rvCategories: RecyclerView
    private lateinit var rvPriorities: RecyclerView
    private val categoryList = mutableListOf<String>()
    private val priorityList = mutableListOf<String>()
    private lateinit var categoryAdapter: AdminItemAdapter
    private lateinit var priorityAdapter: AdminItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val etCategory = findViewById<EditText>(R.id.etNewCategory)
        val btnAddCategory = findViewById<Button>(R.id.btnAddCategory)
        val etPriority = findViewById<EditText>(R.id.etNewPriority)
        val btnAddPriority = findViewById<Button>(R.id.btnAddPriority)
        val btnSeedData = findViewById<Button>(R.id.btnSeedData)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val toolbar = findViewById<MaterialToolbar>(R.id.adminToolbar)

        toolbar.setNavigationOnClickListener { finish() }

        rvCategories = findViewById(R.id.rvCategories)
        rvPriorities = findViewById(R.id.rvPriorities)

        setupRecyclerViews()
        fetchData()

        btnAddCategory.setOnClickListener {
            val name = etCategory.text.toString().trim()
            if (name.isNotEmpty()) {
                val data = mapOf("name" to name)
                FirebaseHelper.db.collection("categories").add(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Category Added", Toast.LENGTH_SHORT).show()
                        etCategory.text.clear()
                    }
            }
        }

        btnAddPriority.setOnClickListener {
            val name = etPriority.text.toString().trim()
            if (name.isNotEmpty()) {
                val data = mapOf("name" to name)
                FirebaseHelper.db.collection("priorities").add(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Priority Added", Toast.LENGTH_SHORT).show()
                        etPriority.text.clear()
                    }
            }
        }

        btnSeedData.setOnClickListener {
            seedDefaultData()
        }

        btnLogout.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerViews() {
        categoryAdapter = AdminItemAdapter(categoryList) { name ->
            deleteItem("categories", name)
        }
        rvCategories.layoutManager = LinearLayoutManager(this)
        rvCategories.adapter = categoryAdapter

        priorityAdapter = AdminItemAdapter(priorityList) { name ->
            deleteItem("priorities", name)
        }
        rvPriorities.layoutManager = LinearLayoutManager(this)
        rvPriorities.adapter = priorityAdapter
    }

    private fun fetchData() {
        // Real-time categories
        FirebaseHelper.db.collection("categories")
            .addSnapshotListener { snapshots, _ ->
                categoryList.clear()
                snapshots?.forEach { doc -> categoryList.add(doc.getString("name") ?: "") }
                categoryAdapter.notifyDataSetChanged()
            }

        // Real-time priorities
        FirebaseHelper.db.collection("priorities")
            .addSnapshotListener { snapshots, _ ->
                priorityList.clear()
                snapshots?.forEach { doc -> priorityList.add(doc.getString("name") ?: "") }
                priorityAdapter.notifyDataSetChanged()
            }
    }

    private fun deleteItem(collection: String, name: String) {
        FirebaseHelper.db.collection(collection)
            .whereEqualTo("name", name)
            .get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    doc.reference.delete()
                }
                Toast.makeText(this, "Item Deleted", Toast.LENGTH_SHORT).show()
            }
    }

    private fun seedDefaultData() {
        val categories = listOf("IT", "Library", "Transport", "Hostel", "Accounts", "Examination", "Cafeteria", "Administration")
        val priorities = listOf("Low", "Medium", "High", "Urgent")

        categories.forEach { cat ->
            FirebaseHelper.db.collection("categories")
                .whereEqualTo("name", cat)
                .get()
                .addOnSuccessListener { result ->
                    if (result.isEmpty) {
                        FirebaseHelper.db.collection("categories").add(mapOf("name" to cat))
                    }
                }
        }

        priorities.forEach { prio ->
            FirebaseHelper.db.collection("priorities")
                .whereEqualTo("name", prio)
                .get()
                .addOnSuccessListener { result ->
                    if (result.isEmpty) {
                        FirebaseHelper.db.collection("priorities").add(mapOf("name" to prio))
                    }
                }
        }
        Toast.makeText(this, "Seeding default data...", Toast.LENGTH_SHORT).show()
    }
}
