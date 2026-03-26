package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.graphics.Color
import android.view.View
import android.text.TextPaint
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase



class MainActivity : AppCompatActivity() {
    lateinit var logEmail: EditText

    lateinit var logPass: EditText
    lateinit var LoginBtn: Button

    lateinit var txtForgotPassword: TextView

    lateinit var signup: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        logEmail = findViewById(R.id.logEmail)
        logPass = findViewById(R.id.logPass)
        LoginBtn = findViewById(R.id.LoginBtn)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        signup = findViewById(R.id.signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        LoginBtn.setOnClickListener {
            startActivity(Intent(applicationContext, RegisterActivity::class.java))
        }
        txtForgotPassword.setOnClickListener {
            startActivity(Intent(applicationContext, ForgetActivity::class.java))
        }
        signup.setOnClickListener {
            startActivity(Intent(applicationContext, RegisterActivity::class.java))
        }
        val text = "Don’t have an account? Sign up"
        val spannable = SpannableString(text)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false   // ❌ remove underline
                ds.color = Color.BLUE        // keep it blue (optional)
            }
        }



        val start = text.indexOf("Sign up")
        val end = start + "Sign up".length

        spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

// Make "Sign up" blue
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
