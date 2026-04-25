package com.example.app

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class EditActivity : AppCompatActivity() {

    private lateinit var date: EditText
    private lateinit var time: EditText
    private lateinit var occurrence: EditText
    private lateinit var sign: EditText
    private lateinit var updateBtn: Button
    private lateinit var backBtn: Button

    private lateinit var ref: DatabaseReference
    private var id: String? = null
    private var obNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        // Link UI
        date = findViewById(R.id.date)
        time = findViewById(R.id.time)
        occurrence = findViewById(R.id.occurrence)
        sign = findViewById(R.id.sign)
        updateBtn = findViewById(R.id.updateBtn)
        backBtn = findViewById(R.id.backBtn)

        // Get data from Intent
        id = intent.getStringExtra("id")
        obNumber = intent.getStringExtra("ob")

        date.setText(intent.getStringExtra("date"))
        time.setText(intent.getStringExtra("time"))
        occurrence.setText(intent.getStringExtra("occ"))
        sign.setText(intent.getStringExtra("sign"))

        backBtn.setOnClickListener {
            startActivity(Intent(this, OmActivity::class.java))
        }

        val calendar = Calendar.getInstance()

        // 📅 DATE PICKER
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

        // ⏰ TIME PICKER (24-hour)
        time.setOnClickListener {

            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(this, { _, h, m ->

                val formattedTime = String.format("%02d:%02d", h, m)
                time.setText(formattedTime)

            }, hour, minute, true)

            timePicker.show()
        }

        // Firebase reference
        ref = FirebaseDatabase.getInstance().getReference("occurrences")

        // 🔄 UPDATE BUTTON
        updateBtn.setOnClickListener {

            val newDate = date.text.toString()
            val newTime = time.text.toString()
            val newOcc = occurrence.text.toString()
            val newSign = sign.text.toString()

            // ✅ Validation
            if (newDate.isEmpty() || newTime.isEmpty() || newOcc.isEmpty() || newSign.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ Signature must be letters only
            if (!newSign.matches(Regex("^[a-zA-Z ]+$"))) {
                Toast.makeText(this, "Signature must be letters only", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (id == null) {
                Toast.makeText(this, "Error: ID missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ Generate OB from edited date
            val parts = newDate.split("/")

            if (parts.size < 3) {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val formattedDate = parts[0] + parts[1] + parts[2]

            // Get entry number from old OB
            val entryNumber = obNumber?.split("/")?.get(0) ?: "001"

            // New OB number
            val newOB = "$entryNumber/$formattedDate"

            // Create updated object
            val updated = Occurrence(
                date = newDate,
                time = newTime,
                obNumber = newOB,
                occurence = newOcc,
                sign = newSign,
                rec_id = id
            )

            // Update Firebase
            ref.child(id!!).setValue(updated)
                .addOnSuccessListener {
                    Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}