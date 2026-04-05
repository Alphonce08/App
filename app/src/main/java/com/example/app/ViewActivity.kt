package com.example.app

import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class ViewActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var dataList: ArrayList<Occurrence>
    private lateinit var adapter: CustomAdapter
    private lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)

        // Link ListView
        listView = findViewById(R.id.listView)

        // Initialize list
        dataList = ArrayList()

        // Firebase reference (MUST match HomeActivity)
        ref = FirebaseDatabase.getInstance().getReference("occurrences")

        // Load data
        loadData()
    }

    private fun loadData() {

        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                dataList.clear()

                for (snap in snapshot.children) {

                    val item = snap.getValue(Occurrence::class.java)

                    if (item != null) {
                        item.rec_id = snap.key   // IMPORTANT for delete
                        dataList.add(item)
                    }
                }

                // Set adapter only once
                if (!::adapter.isInitialized) {
                    adapter = CustomAdapter(this@ViewActivity, dataList)
                    listView.adapter = adapter
                } else {
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ViewActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}