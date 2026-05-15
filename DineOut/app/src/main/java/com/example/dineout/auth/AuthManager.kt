package com.example.dineout.auth

import android.content.Context
import android.util.Patterns
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.util.Locale

data class AuthUser(
    val name: String,
    val email: String,
    val passwordHash: String,
)

sealed class AuthResult {
    data class Success(val message: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

object AuthManager {
    private const val PREFS_NAME = "dineout_auth_prefs"
    private const val KEY_USERS = "users"
    private const val KEY_CURRENT_EMAIL = "current_email"
    private const val KEY_CURRENT_NAME = "current_name"

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isLoggedIn(context: Context): Boolean {
        return prefs(context).getString(KEY_CURRENT_EMAIL, null) != null
    }

    fun currentUserEmail(context: Context): String {
        return prefs(context).getString(KEY_CURRENT_EMAIL, "") ?: ""
    }

    fun currentUserName(context: Context): String {
        return prefs(context).getString(KEY_CURRENT_NAME, "") ?: ""
    }

    fun currentStorageKey(context: Context): String {
        val email = currentUserEmail(context)
        if (email.isBlank()) return "guest"
        return hashPassword(email).take(16)
    }

    fun login(context: Context, email: String, password: String): AuthResult {
        val normalizedEmail = email.trim().lowercase(Locale.getDefault())
        if (normalizedEmail.isEmpty() || password.isBlank()) {
            return AuthResult.Error("Enter email and password.")
        }

        val user = loadUsers(context).firstOrNull { it.email.equals(normalizedEmail, ignoreCase = true) }
            ?: return AuthResult.Error("No account found for this email.")

        if (user.passwordHash != hashPassword(password)) {
            return AuthResult.Error("Incorrect password.")
        }

        saveSession(context, user)
        return AuthResult.Success("Welcome back, ${user.name}!")
    }

    fun signup(context: Context, name: String, email: String, password: String, confirmPassword: String): AuthResult {
        val cleanName = name.trim()
        val cleanEmail = email.trim().lowercase(Locale.getDefault())

        if (cleanName.isEmpty() || cleanEmail.isEmpty() || password.isBlank() || confirmPassword.isBlank()) {
            return AuthResult.Error("Please fill in all fields.")
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            return AuthResult.Error("Enter a valid email address.")
        }
        if (password.length < 6) {
            return AuthResult.Error("Password must be at least 6 characters.")
        }
        if (password != confirmPassword) {
            return AuthResult.Error("Passwords do not match.")
        }

        val users = loadUsers(context)
        if (users.any { it.email.equals(cleanEmail, ignoreCase = true) }) {
            return AuthResult.Error("An account with this email already exists.")
        }

        val user = AuthUser(cleanName, cleanEmail, hashPassword(password))
        users.add(user)
        saveUsers(context, users)
        saveSession(context, user)

        return AuthResult.Success("Account created successfully.")
    }

    fun logout(context: Context) {
        prefs(context).edit()
            .remove(KEY_CURRENT_EMAIL)
            .remove(KEY_CURRENT_NAME)
            .apply()
    }

    private fun saveSession(context: Context, user: AuthUser) {
        prefs(context).edit()
            .putString(KEY_CURRENT_EMAIL, user.email)
            .putString(KEY_CURRENT_NAME, user.name)
            .apply()
    }

    private fun loadUsers(context: Context): MutableList<AuthUser> {
        val raw = prefs(context).getString(KEY_USERS, null).orEmpty()
        if (raw.isBlank()) return mutableListOf()

        return runCatching {
            val users = mutableListOf<AuthUser>()
            val array = JSONArray(raw)
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                users.add(
                    AuthUser(
                        name = item.optString("name"),
                        email = item.optString("email"),
                        passwordHash = item.optString("passwordHash")
                    )
                )
            }
            users
        }.getOrDefault(mutableListOf())
    }

    private fun saveUsers(context: Context, users: List<AuthUser>) {
        val array = JSONArray()
        users.forEach { user ->
            array.put(
                JSONObject().apply {
                    put("name", user.name)
                    put("email", user.email)
                    put("passwordHash", user.passwordHash)
                }
            )
        }

        prefs(context).edit()
            .putString(KEY_USERS, array.toString())
            .apply()
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}