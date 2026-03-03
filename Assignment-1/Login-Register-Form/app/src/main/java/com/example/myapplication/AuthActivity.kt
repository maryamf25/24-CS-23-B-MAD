package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AuthActivity : ComponentActivity() {

    private var isLoginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val titleTextView = findViewById<TextView>(R.id.authTitleTextView)
        val fullNameEditText = findViewById<EditText>(R.id.fullNameEditText)
        val confirmPasswordEditText = findViewById<EditText>(R.id.confirmPasswordEditText)
        val genderLabelTextView = findViewById<TextView>(R.id.genderLabelTextView)
        val genderRadioGroup = findViewById<RadioGroup>(R.id.genderRadioGroup)
        val cityLabelTextView = findViewById<TextView>(R.id.cityLabelTextView)
        val citySpinner = findViewById<Spinner>(R.id.citySpinner)
        val termsCheckBox = findViewById<CheckBox>(R.id.termsCheckBox)
        
        val actionButton = findViewById<Button>(R.id.actionButton)
        val switchModeTextView = findViewById<TextView>(R.id.switchModeTextView)

        val cities = arrayOf("New York", "London", "Tokyo", "Paris", "Berlin")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cities)
        citySpinner.adapter = adapter

        fun updateUI() {
            if (isLoginMode) {
                titleTextView.text = getString(R.string.login)
                actionButton.text = getString(R.string.btn_login)
                switchModeTextView.text = getString(R.string.switch_to_register)

                fullNameEditText.visibility = View.GONE
                confirmPasswordEditText.visibility = View.GONE
                genderLabelTextView.visibility = View.GONE
                genderRadioGroup.visibility = View.GONE
                cityLabelTextView.visibility = View.GONE
                citySpinner.visibility = View.GONE
                termsCheckBox.visibility = View.GONE
            } else {
                titleTextView.text = getString(R.string.register_title)
                actionButton.text = getString(R.string.btn_register)
                switchModeTextView.text = getString(R.string.switch_to_login)

                fullNameEditText.visibility = View.VISIBLE
                confirmPasswordEditText.visibility = View.VISIBLE
                genderLabelTextView.visibility = View.VISIBLE
                genderRadioGroup.visibility = View.VISIBLE
                cityLabelTextView.visibility = View.VISIBLE
                citySpinner.visibility = View.VISIBLE
                termsCheckBox.visibility = View.VISIBLE
            }
        }

        switchModeTextView.setOnClickListener {
            isLoginMode = !isLoginMode
            updateUI()
        }

        actionButton.setOnClickListener {
            val intent = android.content.Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        
        updateUI()
    }
}