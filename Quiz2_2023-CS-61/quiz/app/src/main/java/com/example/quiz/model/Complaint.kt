package com.example.quiz.model

import com.google.firebase.Timestamp

data class Complaint(
    var id: String = "",
    val studentName: String = "",
    val rollNumber: String = "",
    val complaintTitle: String = "",
    val complaintCategory: String = "",
    val priorityLevel: String = "",
    val complaintDescription: String = "",
    val status: String = "Pending",
    val timestamp: Timestamp? = null
)
