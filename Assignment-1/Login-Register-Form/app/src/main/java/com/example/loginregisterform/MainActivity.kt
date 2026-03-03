package com.example.loginregisterform

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.loginregisterform.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val userInput = findViewById<EditText>(R.id.userInput)
        val btnCalculate = findViewById<Button>(R.id.CalculateButton)
        val btnClear = findViewById<Button>(R.id.clearButton)
        val btnSwitch = findViewById<Button>(R.id.switchButton)
        val btnLogout = findViewById<Button>(R.id.logoutButton)

        btnSwitch.setOnClickListener {
            val intent = android.content.Intent(this, DiscountActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            val intent = android.content.Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnCalculate.setOnClickListener {
            val input = userInput.text.toString()
            if (input.isNotEmpty()) {
                val num = input.toInt()
                val result = num * 3
                userInput.setText(result.toString())
                Toast.makeText(this, "Answer is: $result", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Please enter a number", Toast.LENGTH_LONG).show()
            }
        }

        btnClear.setOnClickListener {
            userInput.text.clear()
        }
    }
}