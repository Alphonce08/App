package com.example.app


import android.R.attr.name
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    lateinit var edtEmail: EditText
    lateinit var edtPass: EditText
    lateinit var edtPass2: EditText
    lateinit var reg: Button

    lateinit var mAuth: FirebaseAuth
    lateinit var progress: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        edtEmail = findViewById(R.id.edtEmail)
        edtPass = findViewById(R.id.edtPass)
        edtPass2 = findViewById(R.id.edtPass)
        reg = findViewById(R.id.reg)
        progress = findViewById(R.id.progressBar)



        reg.setOnClickListener {

            val email = edtEmail.text.toString().trim()
            val password = edtPass.text.toString().trim()
            val confirmPassword = edtPass2.text.toString().trim()

            val gmailPattern = Regex("^[A-Za-z0-9._%+-]+@gmail\\.com$")

            val db = FirebaseDatabase.getInstance().reference
            val safeEmail = email.replace(".", "_")

            val userMap = mapOf(
                "email" to email,
                "name" to name
            )

            db.child("users").child(safeEmail).setValue(userMap)

            if (email.isEmpty()) {
                edtEmail.error = "Please enter your email"
                return@setOnClickListener
            }

            if (!gmailPattern.matches(email)) {
                edtEmail.error = "Enter a valid Gmail address"
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

            // ✅ Show loader
            progress.visibility = View.VISIBLE

            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {

                    // ✅ Hide loader
                    progress.visibility = View.GONE

                    if (it.isSuccessful) {
                        Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        showMessage("Error", it.exception?.message ?: "Unknown error")
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