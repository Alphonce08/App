package com.example.app

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    lateinit var edtEmail: EditText
    lateinit var edtPass: EditText
    lateinit var edtPass2: EditText
    lateinit var Reg: Button

    lateinit var mAuth: FirebaseAuth
    lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Link UI
        edtEmail = findViewById(R.id.edtEmail)
        edtPass = findViewById(R.id.edtPass)
        edtPass2 = findViewById(R.id.edtPass2)
        Reg = findViewById(R.id.Reg)

        Reg.setOnClickListener {

            val email = edtEmail.text.toString().trim()
            val password = edtPass.text.toString().trim()
            val confirmPassword = edtPass2.text.toString().trim()

            // Validation
            if (email.isEmpty()) {
                edtEmail.error = "Please enter your email"
                edtEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                edtPass.error = "Please enter password"
                edtPass.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 6) {
                edtPass.error = "Password must be at least 6 characters"
                edtPass.requestFocus()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                edtPass2.error = "Passwords do not match"
                edtPass2.requestFocus()
                return@setOnClickListener
            }

            // Show loading
            progress.visibility = ProgressBar.VISIBLE

            // Register user
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {

                    progress.visibility = ProgressBar.GONE

                    if (it.isSuccessful) {

                        Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show()

                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()

                    } else {
                        showMessage("Error", it.exception?.message ?: "Unknown error")
                    }
                }
        }
    }

    // ✅ Properly placed function
    private fun showMessage(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}