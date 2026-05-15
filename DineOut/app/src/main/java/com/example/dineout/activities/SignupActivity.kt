package com.example.dineout.activities

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dineout.R
import com.example.dineout.auth.AuthManager
import com.example.dineout.auth.AuthResult
import com.google.android.material.button.MaterialButton

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        if (AuthManager.isLoggedIn(this)) {
            goToMain()
            return
        }

        val etName = findViewById<EditText>(R.id.etSignupName)
        val etEmail = findViewById<EditText>(R.id.etSignupEmail)
        val etPassword = findViewById<EditText>(R.id.etSignupPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etSignupConfirmPassword)
        val btnSignup = findViewById<MaterialButton>(R.id.btnSignup)
        val tvGoLogin = findViewById<TextView>(R.id.tvGoLogin)

        btnSignup.setOnClickListener {
            when (val result = AuthManager.signup(
                this,
                etName.text?.toString().orEmpty(),
                etEmail.text?.toString().orEmpty(),
                etPassword.text?.toString().orEmpty(),
                etConfirmPassword.text?.toString().orEmpty()
            )) {
                is AuthResult.Success -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                    goToMain()
                }
                is AuthResult.Error -> Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
            }
        }

        tvGoLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}