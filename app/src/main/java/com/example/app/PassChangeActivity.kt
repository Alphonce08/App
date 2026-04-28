package com.example.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class PassChangeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var currentPassword: EditText
    private lateinit var newPassword: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var btnUpdate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_change)

        auth = FirebaseAuth.getInstance()

        currentPassword = findViewById(R.id.currentPassword)
        newPassword = findViewById(R.id.newPassword)
        btnUpdate = findViewById(R.id.btnUpdatePassword)
        confirmPassword = findViewById(R.id.confirmPassword)

        btnUpdate.setOnClickListener {
            changePassword()
        }
    }

    private fun changePassword() {

        val user = auth.currentUser

        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val currentPass = currentPassword.text.toString().trim()
        val newPass = newPassword.text.toString().trim()
        val confirmPass = confirmPassword.text.toString().trim()

        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPass != confirmPass) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPass.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val email = user.email

        if (email == null) {
            Toast.makeText(this, "No email found", Toast.LENGTH_SHORT).show()
            return
        }

        val credential = EmailAuthProvider.getCredential(email, currentPass)

        user.reauthenticate(credential)
            .addOnSuccessListener {

                user.updatePassword(newPass)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Password updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            e.message ?: "Password update failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Current password is incorrect",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}