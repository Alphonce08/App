package com.example.app

import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.*

class ViewActivity : AppCompatActivity() {

    lateinit var mTxtDate: TextView
    lateinit var obNum: TextView
    lateinit var time: TextView
    lateinit var occurBk: TextView
    lateinit var sign: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view)

        mTxtDate = findViewById(R.id.date)
        obNum = findViewById(R.id.obNum)
        time = findViewById(R.id.time)
        occurBk = findViewById(R.id.occurBk)
        sign = findViewById(R.id.sign)



        retrieveData()   // 👈 VERY IMPORTANT (call function)
    }

    // ✅ FUNCTION MUST BE OUTSIDE onCreate
    private fun retrieveData() {

        val database = FirebaseDatabase.getInstance().getReference("occurrences")

        database.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                for (dataSnap in snapshot.children) {

                    val occurrence = dataSnap.getValue(Occurrence::class.java)
                    if (occurrence != null) {

                        val date = occurrence?.date
                        val timeVal = occurrence?.time
                        val details = occurrence?.occurrence
                        val signature = occurrence?.sign

                        // ✅ Show in UI
                        time.text = timeVal
                        occurBk.text = details
                        sign.text = signature

                        // DatePicker set (optional)
                        Log.d("DATA", "Date: $date Time: $timeVal")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ERROR", error.message)
            }
        })
    }
}