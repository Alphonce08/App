package com.example.app

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var newEmail: TextView
    private lateinit var newPass: TextView
    private lateinit var logout: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings2)

        auth = FirebaseAuth.getInstance()

        newEmail = findViewById(R.id.newEmail)
        newPass = findViewById(R.id.newPass)
        logout = findViewById(R.id.logout)

        // Go to Email Change page
        newEmail.setOnClickListener {
            startActivity(Intent(this, EmailChangeActivity::class.java))
        }

        // Go to Password Change page
        newPass.setOnClickListener {
            startActivity(Intent(this, PassChangeActivity::class.java))
        }

        // Logout
        logout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                auth.signOut()
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}