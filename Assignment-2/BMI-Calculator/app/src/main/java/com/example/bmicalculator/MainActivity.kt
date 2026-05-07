package com.example.bmicalculator

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val etName = findViewById<EditText>(R.id.etName)
        val etAge = findViewById<EditText>(R.id.etAge)
        val etWeight = findViewById<EditText>(R.id.etWeight)
        val etHeight = findViewById<EditText>(R.id.etHeight)
        val btnCalculate = findViewById<Button>(R.id.btnCalculate)

        btnCalculate.setOnClickListener {
            val name = etName.text.toString()
            val age = etAge.text.toString()
            val weight = etWeight.text.toString()
            val height = etHeight.text.toString()

            if(name.isEmpty() || age.isEmpty() || weight.isEmpty() || height.isEmpty()){
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else{
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("name", name)
                intent.putExtra("age", age)
                intent.putExtra("weight", weight)
                intent.putExtra("height", height)
                startActivity(intent)
            }
        }
    }
}