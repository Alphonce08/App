package com.example.app

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsMenu: ImageView
    private lateinit var txtChangeEmail: TextView
    private lateinit var txtChangePassword: TextView
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        mAuth = FirebaseAuth.getInstance()

        initViews()
        setupClicks()
        setupDropdown()
    }

    // 🔗 Bind Views
    private fun initViews() {
        settingsMenu = findViewById(R.id.settingsMenu)
        txtChangeEmail = findViewById(R.id.cardChangeEmail)
        txtChangePassword = findViewById(R.id.cardChangePassword)
    }



    // ☰ Dropdown Menu
    private fun setupDropdown() {

        settingsMenu.setOnClickListener {
            Toast.makeText(this, "CLICK WORKS", Toast.LENGTH_SHORT).show()

            val popup = PopupMenu(this, settingsMenu)
            popup.menuInflater.inflate(R.menu.menu_settings, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {

                    R.id.change_email -> {
                        showChangeEmailDialog()
                        true
                    }

                    R.id.change_password -> {
                        showChangePasswordDialog()
                        true
                    }

                    else -> false
                }
            }

            popup.show()
        }
    }

    // 🔘 Clickable Items (Main UX)
    private fun setupClicks() {

        txtChangeEmail.setOnClickListener {
            showChangeEmailDialog()
        }

        txtChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }
    }

    // 📧 Change Email
    private fun showChangeEmailDialog() {

        val input = EditText(this)
        input.hint = "Enter new email"

        AlertDialog.Builder(this)
            .setTitle("Change Email")
            .setView(input)
            .setPositiveButton("Update") { _, _ ->

                val email = input.text.toString().trim()

                if (email.isEmpty()) {
                    Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                mAuth.currentUser?.updateEmail(email)
                    ?.addOnSuccessListener {
                        Toast.makeText(this, "Email updated", Toast.LENGTH_SHORT).show()
                    }
                    ?.addOnFailureListener {
                        Toast.makeText(this, "Failed: ${it.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    // 🔒 Change Password
    private fun showChangePasswordDialog() {

        val input = EditText(this)
        input.hint = "Enter new password"

        AlertDialog.Builder(this)
            .setTitle("Change Password")
            .setView(input)
            .setPositiveButton("Update") { _, _ ->

                val password = input.text.toString().trim()

                if (password.length < 6) {
                    Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                mAuth.currentUser?.updatePassword(password)
                    ?.addOnSuccessListener {
                        Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show()
                    }
                    ?.addOnFailureListener {
                        Toast.makeText(this, "Failed: ${it.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}