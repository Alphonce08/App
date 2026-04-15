package com.example.app

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    lateinit var stname: EditText
    lateinit var edtEmail: EditText
    lateinit var edtPass: EditText
    lateinit var edtPass2: EditText
    lateinit var reg: Button
    lateinit var progress: ProgressBar

    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        stname = findViewById(R.id.stname)
        edtEmail = findViewById(R.id.edtEmail)
        edtPass = findViewById(R.id.edtPass)
        edtPass2 = findViewById(R.id.edtPass2) // ✅ FIXED
        reg = findViewById(R.id.reg)
        progress = findViewById(R.id.progressBar)

        reg.setOnClickListener {

            val name = stname.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val password = edtPass.text.toString().trim()
            val confirmPassword = edtPass2.text.toString().trim()

            // 🔍 VALIDATION
            if (name.isEmpty()) {
                stname.error = "Enter your name"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                edtEmail.error = "Enter email"
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.error = "Invalid email format"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                edtPass.error = "Enter password"
                return@setOnClickListener
            }

            if (password.length < 6) {
                edtPass.error = "Minimum 6 characters"
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                edtPass2.error = "Passwords do not match"
                return@setOnClickListener
            }

            progress.visibility = View.VISIBLE

            // 🔐 CREATE USER
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        val user = mAuth.currentUser

                        // 📩 SEND EMAIL VERIFICATION (OTP)
                        user?.sendEmailVerification()
                            ?.addOnCompleteListener { verifyTask ->

                                progress.visibility = View.GONE

                                if (verifyTask.isSuccessful) {

                                    // ✅ SAVE USER ONLY AFTER SUCCESS
                                    val db = FirebaseDatabase.getInstance().reference
                                    val safeEmail = email.replace(".", "_")

                                    val userMap = mapOf(
                                        "email" to email,
                                        "name" to name
                                    )

                                    db.child("users").child(safeEmail).setValue(userMap)

                                    Toast.makeText(
                                        this,
                                        "Verification email sent. Check your inbox.",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    // 🔁 Go to Login (NOT MainActivity)
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finish()

                                } else {
                                    showMessage("Error", "Failed to send verification email")
                                }
                            }

                    } else {
                        progress.visibility = View.GONE
                        showMessage("Registration Failed", task.exception?.message ?: "Unknown error")
                    }
                }
        }
    }

    private fun showMessage(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}