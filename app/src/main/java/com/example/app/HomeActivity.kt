package com.example.app



import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


class HomeActivity : AppCompatActivity() {

    private lateinit var date: EditText
    private lateinit var obNumber: EditText
    private lateinit var time: EditText
    private lateinit var occurrence: EditText
    private lateinit var sign: EditText
    private lateinit var saveBtn: Button

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

        // Firebase reference
        // database = FirebaseDatabase.getInstance().getReference("OB_Records")

        // Button click

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
        saveBtn.setOnClickListener{
            startActivity(Intent(this, ViewActivity::class.java))

        }

//        val id = database.push().key!!
//
//        val ob = OBModel(sDate, sOb, sTime, sOcc, sSign)
//
//        database.child(id).setValue(ob).addOnCompleteListener {
//            if (it.isSuccessful) {
//                Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show()
//                clearFields()
//            } else {
//                Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    private fun clearFields() {
        date.text.clear()
        obNumber.text.clear()
        time.text.clear()
        occurrence.text.clear()
        sign.text.clear()
    }
}
