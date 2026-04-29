package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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
    private lateinit var backBtn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_change)

        auth = FirebaseAuth.getInstance()

        currentPassword = findViewById(R.id.currentPassword)
        newPassword = findViewById(R.id.newPassword)
        confirmPassword = findViewById(R.id.confirmPassword)
        btnUpdate = findViewById(R.id.btnUpdatePassword)
        backBtn = findViewById(R.id.backBtn)

        btnUpdate.setOnClickListener {
            changePassword()
        }

        backBtn.setOnClickListener {
            finish()
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
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
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

                        auth.signOut()

                        Toast.makeText(
                            this,
                            "Password updated successfully. Please log in again.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
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