package com.example.app

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.util.Calendar

class HomeActivity : AppCompatActivity() {

    private lateinit var date: EditText
    private lateinit var time: EditText
    private lateinit var occurrence: EditText
    private lateinit var sign: EditText
    private lateinit var saveBtn: Button

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // 🔗 Link UI
        date = findViewById(R.id.date)
        time = findViewById(R.id.time)
        occurrence = findViewById(R.id.occurrence)
        sign = findViewById(R.id.sign)
        saveBtn = findViewById(R.id.saveBtn)

        val calendar = Calendar.getInstance()

        // ✅ Default DATE
        date.setText(String.format(
            "%02d/%02d/%04d",
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR)
        ))

        // ✅ Default TIME
        time.setText(String.format(
            "%02d:%02d",
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE)
        ))

        // 📅 DATE PICKER
        date.setOnClickListener {
            val cal = Calendar.getInstance()

            DatePickerDialog(this,
                { _, year, month, day ->
                    date.setText(String.format("%02d/%02d/%04d", day, month + 1, year))
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // ⏰ TIME PICKER
        time.setOnClickListener {
            val cal = Calendar.getInstance()

            TimePickerDialog(this,
                { _, hour, minute ->
                    time.setText(String.format("%02d:%02d", hour, minute))
                },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }

        // 🔥 Firebase
        database = FirebaseDatabase.getInstance().getReference("occurrences")

        saveBtn.setOnClickListener {
            saveData()
        }

        saveBtn.setOnLongClickListener {
            startActivity(Intent(this, ViewActivity::class.java))
            true
        }
    }

    private fun saveData() {

        val sDate = date.text.toString().trim()
        val sTime = time.text.toString().trim()
        val sOcc = occurrence.text.toString().trim()
        val sSign = sign.text.toString().trim()

        // ✅ Validation
        if (sDate.isEmpty() || sTime.isEmpty() || sOcc.isEmpty() || sSign.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (!sSign.matches(Regex("^[a-zA-Z ]+$"))) {
            Toast.makeText(this, "Signature must contain letters only", Toast.LENGTH_SHORT).show()
            return
        }

        val parts = sDate.split("/")
        if (parts.size < 3) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show()
            return
        }

        // 🔢 Generate OB (hidden from UI)
        database.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val count = snapshot.childrenCount + 1
                val entryNumber = String.format("%03d", count)

                val formattedDate = parts[0] + parts[1] + parts[2]
                val generatedOB = "$entryNumber/$formattedDate"

                val id = database.push().key!!

                val data = Occurrence(
                    date = sDate,
                    time = sTime,
                    obNumber = generatedOB, // ✅ saved but NOT shown
                    occurence = sOcc,
                    sign = sSign,
                    rec_id = id
                )

                database.child(id).setValue(data)
                    .addOnSuccessListener {

                        // ✅ Show reference instead of field
                        Toast.makeText(
                            this@HomeActivity,
                            "Saved. Ref: $generatedOB",
                            Toast.LENGTH_LONG
                        ).show()

                        clearFields()

                        startActivity(Intent(this@HomeActivity, ViewActivity::class.java))
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@HomeActivity, "Failed: ${it.message}", Toast.LENGTH_LONG).show()
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 🧹 Reset form
    private fun clearFields() {

        val cal = Calendar.getInstance()

        date.setText(String.format("%02d/%02d/%04d",
            cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.YEAR)))

        time.setText(String.format("%02d:%02d",
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE)))

        occurrence.text.clear()
        sign.text.clear()
    }
}