package com.example.quiz

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.firebase.FirebaseHelper
import com.example.quiz.model.Complaint
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var fabAdd: ExtendedFloatingActionButton
    private lateinit var adapter: ComplaintAdapter
    private lateinit var toolbar: MaterialToolbar
    private val complaintList = mutableListOf<Complaint>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        recyclerView = findViewById(R.id.recyclerView)
        tvEmpty = findViewById(R.id.tvEmpty)
        fabAdd = findViewById(R.id.fabAdd)
        toolbar = findViewById(R.id.toolbar)

        setupToolbar()

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ComplaintAdapter(complaintList) { complaint ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("studentName", complaint.studentName)
            intent.putExtra("rollNumber", complaint.rollNumber)
            intent.putExtra("complaintTitle", complaint.complaintTitle)
            intent.putExtra("complaintCategory", complaint.complaintCategory)
            intent.putExtra("priorityLevel", complaint.priorityLevel)
            intent.putExtra("complaintDescription", complaint.complaintDescription)
            intent.putExtra("status", complaint.status)
            intent.putExtra("timestamp", complaint.timestamp?.toDate()?.toString() ?: "N/A")
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        fabAdd.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        fetchComplaints()
    }

    private fun setupToolbar() {
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_admin -> {
                    startActivity(Intent(this, AdminLoginActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun fetchComplaints() {
        FirebaseHelper.db.collection("complaints")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                complaintList.clear()
                if (snapshots != null && !snapshots.isEmpty) {
                    for (doc in snapshots) {
                        val complaint = doc.toObject(Complaint::class.java)
                        complaint.id = doc.id
                        complaintList.add(complaint)
                    }
                    tvEmpty.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                } else {
                    tvEmpty.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
                adapter.notifyDataSetChanged()
            }
    }
}