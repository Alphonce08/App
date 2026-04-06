package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import java.util.Calendar
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

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

        val calendar = Calendar.getInstance()

        setContentView(R.layout.activity_home)

        // Link UI
        date = findViewById(R.id.date)
        time = findViewById(R.id.time)
        obNumber = findViewById(R.id.obNumber)
        occurrence = findViewById(R.id.occurrence)
        sign = findViewById(R.id.sign)
        saveBtn = findViewById(R.id.saveBtn)



// DATE PICKER
        date.setOnClickListener {

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, y, m, d ->

                val formattedDate = String.format("%02d/%02d/%02d", d, m + 1, y % 100)
                date.setText(formattedDate)

            }, year, month, day)

            datePicker.show()
        }


// TIME PICKER (24 HOURS)
        time.setOnClickListener {

            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(this, { _, h, m ->

                val formattedTime = String.format("%02d:%02d", h, m)
                time.setText(formattedTime)

            }, hour, minute, true) // TRUE = 24-hour format

            timePicker.show()
        }


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
        val sOcc = occurrence.text.toString()
        val sSign = sign.text.toString()

        if (sDate.isEmpty() || sTime.isEmpty() || sOcc.isEmpty() || sSign.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (!sSign.matches(Regex("^[a-zA-Z ]+$"))) {
            Toast.makeText(this, "Signature must contain letters only", Toast.LENGTH_SHORT).show()
            return
        }

        val ref = database

        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val count = snapshot.childrenCount + 1

                val entryNumber = String.format("%03d", count)

                val parts = sDate.split("/")
                val formattedDate = parts[0] + parts[1] + parts[2]

                val generatedOB = "$entryNumber/$formattedDate"

                val id = ref.push().key!!

                val data = Occurrence(
                    date = sDate,
                    time = sTime,
                    obNumber = generatedOB,   // ✅ IMPORTANT
                    occurence = sOcc,
                    sign = sSign,
                    rec_id = id
                )

                ref.child(id).setValue(data)
                    .addOnSuccessListener {
                        Toast.makeText(this@HomeActivity, "Saved successfully", Toast.LENGTH_SHORT).show()
                        clearFields()

                        // Show generated OB
                        obNumber.setText(generatedOB)

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

    private fun clearFields() {
        date.text.clear()
        time.text.clear()
        obNumber.text.clear()
        occurrence.text.clear()
        sign.text.clear()
    }
}