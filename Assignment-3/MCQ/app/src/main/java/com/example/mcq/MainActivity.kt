package com.example.mcq

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userName = intent.getStringExtra("NAME")?: "User"

        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val rgMcq1 = findViewById<RadioGroup>(R.id.rgMcq1)
        val rgMcq2 = findViewById<RadioGroup>(R.id.rgMcq2)
        val rgMcq3 = findViewById<RadioGroup>(R.id.rgMcq3)
        val rgMcq4 = findViewById<RadioGroup>(R.id.rgMcq4)
        val rgMcq5 = findViewById<RadioGroup>(R.id.rgMcq5)

        btnSubmit.isEnabled = false
        btnSubmit.alpha = 0.5f

        val checkAnswers: (RadioGroup, Int) -> Unit = { _, _ ->
            val allAnswered = rgMcq1.checkedRadioButtonId != -1 &&
                              rgMcq2.checkedRadioButtonId != -1 &&
                              rgMcq3.checkedRadioButtonId != -1 &&
                              rgMcq4.checkedRadioButtonId != -1 &&
                              rgMcq5.checkedRadioButtonId != -1
                              
            btnSubmit.isEnabled = allAnswered
            btnSubmit.alpha = if (allAnswered) 1.0f else 0.5f
        }

        rgMcq1.setOnCheckedChangeListener(checkAnswers)
        rgMcq2.setOnCheckedChangeListener(checkAnswers)
        rgMcq3.setOnCheckedChangeListener(checkAnswers)
        rgMcq4.setOnCheckedChangeListener(checkAnswers)
        rgMcq5.setOnCheckedChangeListener(checkAnswers)

        btnSubmit.setOnClickListener {

            var score = 0
            
            if (rgMcq1.checkedRadioButtonId == R.id.rb1_2) score++
            if (rgMcq2.checkedRadioButtonId == R.id.rb2_2) score++
            if (rgMcq3.checkedRadioButtonId == R.id.rb3_3) score++
            if (rgMcq4.checkedRadioButtonId == R.id.rb4_1) score++
            if (rgMcq5.checkedRadioButtonId == R.id.rb5_1) score++

            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("SCORE", score)
            intent.putExtra("NAME", userName)
            startActivity(intent)
        }
    }
}