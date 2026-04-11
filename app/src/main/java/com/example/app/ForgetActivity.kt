package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONObject

class ForgetActivity : AppCompatActivity() {

    lateinit var forget: EditText
    lateinit var otp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forget)

        forget = findViewById(R.id.forget)
        otp = findViewById(R.id.otp)

        otp.setOnClickListener {

            val email = forget.text.toString().trim()

            // ❌ Validate email
            if (email.isEmpty()) {
                forget.error = "Enter email"
                return@setOnClickListener
            }

            if (!email.endsWith("@gmail.com")) {
                forget.error = "Use Gmail only"
                return@setOnClickListener
            }

            val db = FirebaseDatabase.getInstance().reference
            val safeEmail = email.replace(".", "_")

            // 🔍 Check if user is registered
            db.child("users").child(safeEmail).get()
                .addOnSuccessListener { snapshot ->

                    if (!snapshot.exists()) {

                        // ❌ Not registered
                        AlertDialog.Builder(this)
                            .setTitle("Account not found")
                            .setMessage("This email is not registered. Do you want to create an account?")
                            .setPositiveButton("Sign Up") { _, _ ->
                                startActivity(Intent(this, RegisterActivity::class.java))
                            }
                            .setNegativeButton("Cancel", null)
                            .show()

                    } else {

                        // ✅ Generate OTP
                        val generatedOtp = (1000..9999).random().toString()

                        // 🔥 Save OTP in Firebase
                        db.child("otp").child(safeEmail).setValue(generatedOtp)

                        // 🌐 Send OTP via backend email API
                        val url = "http://YOUR_IP:3000/send-otp" // 🔁 CHANGE THIS

                        val queue = Volley.newRequestQueue(this)

                        val json = JSONObject()
                        json.put("email", email)
                        json.put("otp", generatedOtp)

                        val request = JsonObjectRequest(
                            Request.Method.POST,
                            url,
                            json,
                            { _ ->

                                Toast.makeText(
                                    this,
                                    "OTP sent to email",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // 🚀 Go to OTP screen
                                val intent = Intent(this, OtpActivity::class.java)
                                intent.putExtra("email", email)
                                startActivity(intent)
                            },
                            { error ->
                                Toast.makeText(
                                    this,
                                    "Failed: ${error.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )

                        queue.add(request)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Database error", Toast.LENGTH_SHORT).show()
                }
            otp.setOnClickListener {
                startActivity(Intent(this, OtpActivity::class.java))
            }
        }

        // 📱 Window insets handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}