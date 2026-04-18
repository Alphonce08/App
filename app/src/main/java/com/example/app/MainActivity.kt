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

    private lateinit var logEmail: EditText
    private lateinit var logPass: EditText
    private lateinit var loginBtn: Button
    private lateinit var txtForgotPassword: TextView
    private lateinit var signup: TextView

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        // Signup text
        setupSignupText()

        // ================= LOGIN =================
        loginBtn.setOnClickListener {

            val email = logEmail.text.toString().trim()
            val password = logPass.text.toString().trim()

            // 🔍 VALIDATION
            if (email.isEmpty()) {
                logEmail.error = "Enter email"
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showDialog("Invalid Email", "Enter a valid email address")
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                logPass.error = "Enter password"
                return@setOnClickListener
            }

            loginBtn.isEnabled = false

            // 🔐 FIREBASE LOGIN (NO PRE-CHECK)
            val emailClean = email.trim()

// ================= STEP 1: CHECK IF USER EXISTS =================
            mAuth.fetchSignInMethodsForEmail(emailClean)
                .addOnCompleteListener { checkTask ->

                    loginBtn.isEnabled = true

                    if (!checkTask.isSuccessful) {
                        Toast.makeText(this, "Network error", Toast.LENGTH_LONG).show()
                        return@addOnCompleteListener
                    }

                    val methods = checkTask.result?.signInMethods

                    // ❌ ACCOUNT NOT REGISTERED
//                    if (methods.isNullOrEmpty()) {
//                        Toast.makeText(this, "Account not registered", Toast.LENGTH_LONG).show()
//                        return@addOnCompleteListener
//                    }

                    // ================= STEP 2: LOGIN =================
                    mAuth.signInWithEmailAndPassword(emailClean, password)
                        .addOnCompleteListener { task ->

                            if (task.isSuccessful) {

                                val user = mAuth.currentUser

                                if (user != null && user.isEmailVerified) {

                                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                                    startActivity(Intent(this, ViewActivity::class.java))
                                    finish()

                                } else {

                                    Toast.makeText(this, "Email is not verified", Toast.LENGTH_LONG).show()
                                    mAuth.signOut()
                                }

                            } else {

                                // ❌ WRONG PASSWORD (NOW GUARANTEED)
                                Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_LONG).show()
                            }
                        }
                }
        }
    }

    // ================= AUTO LOGIN =================
    override fun onStart() {
        super.onStart()

        val user = mAuth.currentUser

        if (user != null && user.isEmailVerified) {
            startActivity(Intent(this, OmActivity::class.java))
            finish()
        }
    }

    // ================= SIGNUP TEXT =================
    private fun setupSignupText() {
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

    // ================= DIALOG =================
    private fun showDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}