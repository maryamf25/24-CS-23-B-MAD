package com.example.appointment_booking_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ConfirmationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        val tvBookingSummary = findViewById<TextView>(R.id.tvBookingSummary)
        val btnReturnHome = findViewById<Button>(R.id.btnReturnHome)

        val name = intent.getStringExtra("USER_NAME")
        val phone = intent.getStringExtra("USER_PHONE")
        val email = intent.getStringExtra("USER_EMAIL")
        val type = intent.getStringExtra("APPT_TYPE")
        val date = intent.getStringExtra("APPT_DATE")
        val time = intent.getStringExtra("APPT_TIME")
        val gender = intent.getStringExtra("USER_GENDER")

        val summaryText = """
            Patient Name: $name
            Phone: $phone
            Email: $email
            Gender: $gender
            
            Appointment Type: $type
            Date: $date
            Time: $time
        """.trimIndent()

        tvBookingSummary.text = summaryText

        btnReturnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
