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

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (AuthManager.isLoggedIn(this)) {
            goToMain()
            return
        }

        val etEmail = findViewById<EditText>(R.id.etLoginEmail)
        val etPassword = findViewById<EditText>(R.id.etLoginPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val tvGoSignup = findViewById<TextView>(R.id.tvGoSignup)

        btnLogin.setOnClickListener {
            when (val result = AuthManager.login(
                this,
                etEmail.text?.toString().orEmpty(),
                etPassword.text?.toString().orEmpty()
            )) {
                is AuthResult.Success -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                    goToMain()
                }
                is AuthResult.Error -> Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
            }
        }

        tvGoSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}