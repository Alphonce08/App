package com.example.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var newEmail: EditText
    private lateinit var newPassword: EditText
    private lateinit var btnUpdate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings2)

        auth = FirebaseAuth.getInstance()

        newEmail = findViewById(R.id.newEmail)
        newPassword = findViewById(R.id.newPassword)
        btnUpdate = findViewById(R.id.btnUpdate)

        btnUpdate.setOnClickListener {
            updateAccount()
        }
    }

    private fun updateAccount() {

        val user = auth.currentUser

        val email = newEmail.text.toString().trim()
        val password = newPassword.text.toString().trim()

        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // 🔹 Update Email
        if (email.isNotEmpty()) {
            user.updateEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(this, "Email updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
        }

        // 🔹 Update Password
        if (password.isNotEmpty()) {
            user.updatePassword(password)
                .addOnSuccessListener {
                    Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
        }
    }
}