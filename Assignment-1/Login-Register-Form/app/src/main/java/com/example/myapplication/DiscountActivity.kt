package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DiscountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_discount)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userInput = findViewById<EditText>(R.id.userInput)
        val calculateButton = findViewById<Button>(R.id.CalculateButton)
        val clearButton = findViewById<Button>(R.id.clearButton)
        val originalTextView = findViewById<TextView>(R.id.originalTextView)
        val discountTextView = findViewById<TextView>(R.id.discountTextView)
        val switchButton = findViewById<Button>(R.id.switchButton)

        switchButton.setOnClickListener {
            // Because MainActivity launched this, finish() returns back to MainActivity
            finish()
        }

        calculateButton.setOnClickListener {
            val inputStr = userInput.text.toString()
            if (inputStr.isNotEmpty()) {
                val originalPrice = inputStr.toDoubleOrNull()
                if (originalPrice != null) {
                    val discountPrice = originalPrice - (originalPrice * 0.20) // 20% discount
                    originalTextView.text = "Original Price: " + String.format("%.2f", originalPrice)
                    discountTextView.text = "Discount Price: " + String.format("%.2f", discountPrice)
                    originalTextView.visibility = android.view.View.VISIBLE
                    discountTextView.visibility = android.view.View.VISIBLE
                } else {
                    Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a price", Toast.LENGTH_SHORT).show()
            }
        }

        clearButton.setOnClickListener {
            userInput.text.clear()
            originalTextView.text = "Original Price:"
            discountTextView.text = "Discount Price:"
            originalTextView.visibility = android.view.View.GONE
            discountTextView.visibility = android.view.View.GONE
        }
    }
}