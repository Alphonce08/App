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
    private lateinit var obNumber: EditText
    private lateinit var time: EditText
    private lateinit var occurrence: EditText
    private lateinit var sign: EditText
    private lateinit var saveBtn: Button

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val calendar = Calendar.getInstance()

        // Link UI
        date = findViewById(R.id.date)
        time = findViewById(R.id.time)
        obNumber = findViewById(R.id.obNumber)
        occurrence = findViewById(R.id.occurrence)
        sign = findViewById(R.id.sign)
        saveBtn = findViewById(R.id.saveBtn)

        // 📅 DATE PICKER
        date.setOnClickListener {
            val datePicker = DatePickerDialog(this, { _, y, m, d ->
                val formattedDate = String.format("%02d/%02d/%02d", d, m + 1, y % 100)
                date.setText(formattedDate)
            },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        // ⏰ TIME PICKER (24H)
        time.setOnClickListener {
            val timePicker = TimePickerDialog(this, { _, h, m ->
                val formattedTime = String.format("%02d:%02d", h, m)
                time.setText(formattedTime)
            },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePicker.show()
        }

        // Firebase
        database = FirebaseDatabase.getInstance().getReference("occurrences")

        // Save button
        saveBtn.setOnClickListener {
            saveData()
        }

        // Long press → View page
        saveBtn.setOnLongClickListener {
            startActivity(Intent(this, OmActivity::class.java))
            true
        }
    }

    private fun saveData() {

        val sDate = date.text.toString()
        val sTime = time.text.toString()
        val sOcc = occurrence.text.toString()
        val sSign = sign.text.toString()

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

        val ref = database

        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val count = snapshot.childrenCount + 1
                val entryNumber = String.format("%03d", count)

                val formattedDate = parts[0] + parts[1] + parts[2]
                val generatedOB = "$entryNumber/$formattedDate"

                val id = ref.push().key!!

                val data = Occurrence(
                    date = sDate,
                    time = sTime,
                    obNumber = generatedOB,
                    occurence = sOcc,
                    sign = sSign,
                    rec_id = id
                )

                ref.child(id).setValue(data)
                    .addOnSuccessListener {

                        Toast.makeText(this@HomeActivity, "Saved successfully", Toast.LENGTH_SHORT).show()

                        // ✅ Show OB BEFORE clearing
                        obNumber.setText(generatedOB)

                        clearFieldsExceptOB()

                        // ✅ Navigate ONCE
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

    // ✅ Clear everything EXCEPT OB
    private fun clearFieldsExceptOB() {
        date.text.clear()
        time.text.clear()
        occurrence.text.clear()
        sign.text.clear()
    }
}