package com.example.app

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.*

class MainActivity : AppCompatActivity() {

    lateinit var logEmail: EditText
    lateinit var logPass: EditText
    lateinit var loginBtn: Button
    lateinit var txtForgotPassword: TextView
    lateinit var signup: TextView

    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Firebase init
        mAuth = FirebaseAuth.getInstance()

        // UI
        logEmail = findViewById(R.id.logEmail)
        logPass = findViewById(R.id.logPass)
        loginBtn = findViewById(R.id.loginBtn)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        signup = findViewById(R.id.signup)

        // Forgot password
        txtForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgetActivity::class.java))
        }

        // ================= LOGIN =================
        loginBtn.setOnClickListener {

            val email = logEmail.text.toString().trim()
            val password = logPass.text.toString().trim()

            val gmailPattern = Regex("^[A-Za-z0-9._%+-]+@gmail\\.com$")

            // 1. Email empty
            if (email.isEmpty()) {
                logEmail.error = "Enter email"
                return@setOnClickListener
            }

            // 2. Email format check (Gmail only)
            if (!gmailPattern.matches(email)) {
                AlertDialog.Builder(this)
                    .setTitle("Invalid Email")
                    .setMessage("Only Gmail addresses allowed (example@gmail.com)")
                    .setPositiveButton("OK", null)
                    .show()
                return@setOnClickListener
            }

            // 3. Password empty
            if (password.isEmpty()) {
                logPass.error = "Enter password"
                return@setOnClickListener
            }

            loginBtn.isEnabled = false

            // ================= FIREBASE LOGIN =================
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    loginBtn.isEnabled = true

                    if (task.isSuccessful) {

                        // ✅ REGISTERED USER
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, ViewActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                        startActivity(intent)
                        finish()

                    } else {

                        when (task.exception) {

                            // ❌ USER NOT REGISTERED
                            is FirebaseAuthInvalidUserException -> {
                                AlertDialog.Builder(this)
                                    .setTitle("Access Denied")
                                    .setMessage("This account is not registered. Please sign up first.")
                                    .setPositiveButton("Sign Up") { _, _ ->
                                        startActivity(Intent(this, RegisterActivity::class.java))
                                    }
                                    .setNegativeButton("Cancel", null)
                                    .show()
                            }

                            // ❌ WRONG PASSWORD
                            is FirebaseAuthInvalidCredentialsException -> {
                                AlertDialog.Builder(this)
                                    .setTitle("Login Failed")
                                    .setMessage("Incorrect email or password.")
                                    .setPositiveButton("OK", null)
                                    .show()
                            }

                            // ❌ OTHER ERRORS
                            else -> {
                                AlertDialog.Builder(this)
                                    .setTitle("Error")
                                    .setMessage("Something went wrong. Try again.")
                                    .setPositiveButton("OK", null)
                                    .show()
                            }
                        }
                    }
                }
        }

        // ================= SIGNUP TEXT =================
        val text = "Don’t have an account? Sign up"
        val spannable = SpannableString(text)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = Color.BLUE
            }
        }

        val start = text.indexOf("Sign up")
        val end = start + "Sign up".length

        spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(
            ForegroundColorSpan(Color.BLUE),
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        signup.text = spannable
        signup.movementMethod = LinkMovementMethod.getInstance()
        signup.highlightColor = Color.TRANSPARENT
    }
}