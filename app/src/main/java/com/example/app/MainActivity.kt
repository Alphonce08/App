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
import com.google.firebase.auth.FirebaseAuthInvalidUserException

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

        txtForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgetActivity::class.java))
        }

        // LOGIN BUTTON
        loginBtn.setOnClickListener {

            val email = logEmail.text.toString().trim()
            val password = logPass.text.toString().trim()

            if (email.isEmpty()) {
                logEmail.error = "Please enter email"
                logEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                logPass.error = "Please enter password"
                logPass.requestFocus()
                return@setOnClickListener
            }

            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, ViewActivity::class.java))
                        finish()

                    } else {

                        val exception = task.exception

                        when (exception) {

                            is FirebaseAuthInvalidUserException -> {

                                // Optional: account not found → suggest signup
                                AlertDialog.Builder(this)
                                    .setTitle("Account not found")
                                    .setMessage("This email is not registered. Do you want to create an account?")
                                    .setPositiveButton("Sign Up") { _, _ ->
                                        startActivity(Intent(this, RegisterActivity::class.java))
                                    }
                                    .setNegativeButton("Cancel", null)
                                    .show()
                            }

                            else -> {
                                // 🔥 GENERAL ERROR (your request)
                                AlertDialog.Builder(this)
                                    .setTitle("Login Failed")
                                    .setMessage("Wrong email or password. Please try again.")
                                    .setPositiveButton("OK", null)
                                    .show()
                            }
                        }
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