package com.example.dineout.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.dineout.R
import com.example.dineout.auth.AuthManager

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val nextScreen = if (AuthManager.isLoggedIn(this)) MainActivity::class.java else LoginActivity::class.java
            startActivity(Intent(this, nextScreen))
            finish()
        }, 2500)
    }
}