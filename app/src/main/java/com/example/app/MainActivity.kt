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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

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

        // LOGIN BUTTON
        loginBtn.setOnClickListener {

            val email = logEmail.text.toString().trim()
            val password = logPass.text.toString().trim()

            // 1. Empty email
            if (email.isEmpty()) {
                logEmail.error = "Please enter email"
                logEmail.requestFocus()
                return@setOnClickListener
            }

            // 2. Email format check
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                AlertDialog.Builder(this)
                    .setTitle("Invalid Email")
                    .setMessage("Please enter a valid email address")
                    .setPositiveButton("OK", null)
                    .show()

                logEmail.requestFocus()
                return@setOnClickListener
            }

            // 3. Empty password
            if (password.isEmpty()) {
                logPass.error = "Please enter password"
                logPass.requestFocus()
                return@setOnClickListener
            }

            // 🔥 4. CHECK IF EMAIL EXISTS FIRST
            mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        val result = task.result
                        val methods = result?.signInMethods

                        if (methods.isNullOrEmpty()) {
                            // ❌ Email NOT registered
                            AlertDialog.Builder(this)
                                .setTitle("Account not found")
                                .setMessage("This email is not registered. Do you want to create an account?")
                                .setPositiveButton("Sign Up") { _, _ ->
                                    startActivity(Intent(this, RegisterActivity::class.java))
                                }
                                .setNegativeButton("Cancel", null)
                                .show()
                        } else {
                            // ✅ Email exists → proceed to log

                            mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { loginTask ->

                                    if (loginTask.isSuccessful) {

                                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this, ViewActivity::class.java))
                                        finish()

                                    } else {

                                        val exception = loginTask.exception

                                        when (exception) {

                                            is FirebaseAuthInvalidCredentialsException -> {
                                                AlertDialog.Builder(this)
                                                    .setTitle("Login Failed")
                                                    .setMessage("Incorrect password.")
                                                    .setPositiveButton("OK", null)
                                                    .show()
                                            }

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

                    } else {
                        // Error checking email
                        AlertDialog.Builder(this)
                            .setTitle("Error")
                            .setMessage("Failed to verify email. Check your internet connection.")
                            .setPositiveButton("OK", null)
                            .show()
                    }
                }
        }

        // SIGNUP CLICKABLE TEXT
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