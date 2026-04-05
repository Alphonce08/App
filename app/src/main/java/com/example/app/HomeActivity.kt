package com.example.app

import android.content.Intent
import android.os.Bundle
import android.view.View
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
    private lateinit var view: ImageView

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // Link UI
        date = findViewById(R.id.date)
        time = findViewById(R.id.time)
        obNumber = findViewById(R.id.obNumber)
        occurrence = findViewById(R.id.occurrence)
        sign = findViewById(R.id.sign)
        saveBtn = findViewById(R.id.saveBtn)
        view = findViewById(R.id.view)

        // Firebase reference
        database = FirebaseDatabase.getInstance().getReference("occurrences")

        // Save button click
        saveBtn.setOnClickListener {
            saveData()
        }

        // Navigate to ViewActivity (long press)
        saveBtn.setOnLongClickListener {
            startActivity(Intent(this, ViewActivity::class.java))
            true
        }

    }

    private fun saveData() {

        val sDate = date.text.toString()
        val sTime = time.text.toString()
        val sOb = obNumber.text.toString()
        val sOcc = occurrence.text.toString()
        val sSign = sign.text.toString()

        // Validation
        if (sDate.isEmpty() || sOb.isEmpty() || sTime.isEmpty() || sOcc.isEmpty() || sSign.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val ref = database

        val id = ref.push().key!!

        val data = Occurrence(
            date = sDate,
            time = sTime,
            obNumber = sOb,
            occurence = sOcc,
            sign = sSign,
            rec_id = id
        )

        // Save to Firebase
        ref.child(id).setValue(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed: ${it.message}", Toast.LENGTH_LONG).show()
            }
        view.setOnClickListener {
            startActivity(Intent(applicationContext, ViewActivity::class.java))

        }
    }

    private fun clearFields() {
        date.text.clear()
        time.text.clear()
        obNumber.text.clear()
        occurrence.text.clear()
        sign.text.clear()
    }
}