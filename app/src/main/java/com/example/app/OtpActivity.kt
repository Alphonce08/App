package com.example.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase

class OtpActivity : AppCompatActivity() {

    lateinit var edtOtp: EditText
    lateinit var btnSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_otp)

        edtOtp = findViewById(R.id.edtOtp)
        btnSubmit = findViewById(R.id.btnSubmit)

        val email = intent.getStringExtra("email")
        val safeEmail = email?.replace(".", "_")

        btnSubmit.setOnClickListener {

            val enteredOtp = edtOtp.text.toString().trim()

            if (enteredOtp.isEmpty()) {
                edtOtp.error = "Enter OTP"
                return@setOnClickListener
            }

            val db = FirebaseDatabase.getInstance().reference

            db.child("otp").child(safeEmail!!).get()
                .addOnSuccessListener { snapshot ->

                    if (snapshot.exists()) {

                        val savedOtp = snapshot.child("code").value.toString()

                        if (enteredOtp == savedOtp) {

                            Toast.makeText(
                                this,
                                "OTP Verified Successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            // TODO: move to reset password screen
                        } else {

                            Toast.makeText(
                                this,
                                "Invalid OTP",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Toast.makeText(
                            this,
                            "OTP expired or not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}