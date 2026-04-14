package com.example.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ForgetActivity : AppCompatActivity() {

    private lateinit var forget: EditText
    private lateinit var resetBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget)

        forget = findViewById(R.id.forget)
        resetBtn = findViewById(R.id.otp)

        val auth = FirebaseAuth.getInstance()

        resetBtn.setOnClickListener {

            val email = forget.text.toString().trim()

            if (email.isEmpty()) {
                forget.error = "Enter email"
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                forget.error = "Enter valid email"
                return@setOnClickListener
            }

            resetBtn.isEnabled = false
            resetBtn.text = "Sending..."

            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {

                    resetBtn.isEnabled = true
                    resetBtn.text = "Send Reset Link"

                    Toast.makeText(
                        this,
                        "Reset link sent to your email",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .addOnFailureListener {

                    resetBtn.isEnabled = true
                    resetBtn.text = "Send Reset Link"

                    Toast.makeText(
                        this,
                        "Failed: ${it.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }
}






