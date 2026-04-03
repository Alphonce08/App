package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class HomeActivity : AppCompatActivity() {

    private lateinit var date: EditText
    private lateinit var obNumber: EditText
    private lateinit var time: EditText
    private lateinit var occurrence: EditText
    private lateinit var sign: EditText
    private lateinit var saveBtn: Button

    private lateinit var database: DatabaseReference   // ✅ FIXED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // Link UI
        date = findViewById(R.id.date)
        obNumber = findViewById(R.id.obNumber)
        time = findViewById(R.id.time)
        occurrence = findViewById(R.id.occurrence)
        sign = findViewById(R.id.sign)
        saveBtn = findViewById(R.id.saveBtn)

        // ✅ Firebase reference
        database = FirebaseDatabase.getInstance().getReference("occurrences")

        saveBtn.setOnClickListener {
            saveData()
        }
    }

    private fun saveData() {

        val sDate = date.text.toString()
        val sOb = obNumber.text.toString()
        val sTime = time.text.toString()
        val sOcc = occurrence.text.toString()
        val sSign = sign.text.toString()

        if (sDate.isEmpty() || sOb.isEmpty() || sTime.isEmpty() || sOcc.isEmpty() || sSign.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val id = database.push().key!!   // ✅ generate ID

        val ob = Occurrence(id, sDate, sTime, sOcc, sSign)  // ✅ use correct model

        database.child(id).setValue(ob).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show()
                clearFields()

                // ✅ Move to ViewActivity AFTER saving
                startActivity(Intent(this, ViewActivity::class.java))
            } else {
                Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearFields() {
        date.text.clear()
        obNumber.text.clear()
        time.text.clear()
        occurrence.text.clear()
        sign.text.clear()
    }
}