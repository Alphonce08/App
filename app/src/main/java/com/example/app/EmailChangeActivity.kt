package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class EmailChangeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var newEmail: EditText
    private lateinit var backBtn: ImageView
    private lateinit var btnUpdateEmail: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_change)

        auth = FirebaseAuth.getInstance()

        newEmail = findViewById(R.id.newEmail)
        btnUpdateEmail = findViewById(R.id.btnUpdateEmail)
        backBtn = findViewById(R.id.backBtn)

        btnUpdateEmail.setOnClickListener {
            changeEmailAndLogout()
        }
        backBtn.setOnClickListener {
            startActivity(Intent(this, OmActivity::class.java))
        }
    }

    private fun changeEmailAndLogout() {

        val user = auth.currentUser

        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val email = newEmail.text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(this, "Enter new email", Toast.LENGTH_SHORT).show()
            return
        }

        user.verifyBeforeUpdateEmail(email)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Verification email sent. Please check inbox.",
                    Toast.LENGTH_LONG
                ).show()

                // 🔴 Logout user
                auth.signOut()

                // 🔴 Redirect to Login
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.message ?: "Failed", Toast.LENGTH_SHORT).show()
            }
    }
}