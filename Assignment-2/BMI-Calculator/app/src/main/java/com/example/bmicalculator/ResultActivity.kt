package com.example.bmicalculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ResultActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n", "DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val name = intent.getStringExtra("name") ?: ""
        val age = intent.getStringExtra("age") ?: ""
        val weightStr = intent.getStringExtra("weight") ?: ""
        val heightStr = intent.getStringExtra("height") ?: ""

        try {
            val weight = weightStr.toDouble()
            val heightCm = heightStr.toDouble()

            val heightM = heightCm / 100.0

            val bmi = weight / (heightM * heightM)

            val category = when {
                bmi < 18.5 -> "Underweight"
                bmi < 25.0 -> "Normal weight"
                bmi < 30.0 -> "Overweight"
                else -> "Obese"
            }

            findViewById<TextView>(R.id.tvName)?.text = "Name: $name"
            findViewById<TextView>(R.id.tvAge)?.text = "Age: $age years"
            findViewById<TextView>(R.id.tvBMI)?.text = String.format("BMI: %.2f", bmi)
            findViewById<TextView>(R.id.tvCategory)?.text = "Category: $category"
        } catch (_: NumberFormatException) {
            findViewById<TextView>(R.id.tvBMI)?.text = "Error: Invalid input"
        }
    }
}