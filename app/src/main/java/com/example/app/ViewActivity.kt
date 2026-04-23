package com.example.app

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ViewActivity : AppCompatActivity() {

    private lateinit var newOB: ImageView
    private lateinit var listView: ListView
    private lateinit var dataList: ArrayList<Occurrence>
    private lateinit var adapter: CustomAdapter
    private lateinit var ref: DatabaseReference

    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)

        // Firebase
        mAuth = FirebaseAuth.getInstance()

        // UI
        listView = findViewById(R.id.listView)
        newOB = findViewById(R.id.newOB)

        dataList = ArrayList()

        // Firebase DB reference
        ref = FirebaseDatabase.getInstance().getReference("occurrences")


        // Load data
        loadData()

        // Navigate to HomeActivity
        newOB.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }

    // 🔒 PROTECT ACTIVITY (must be OUTSIDE onCreate)
    override fun onStart() {
        super.onStart()

        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun loadData() {

        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                dataList.clear()

                for (snap in snapshot.children) {

                    val item = snap.getValue(Occurrence::class.java)

                    if (item != null) {
                        item.rec_id = snap.key
                        dataList.add(item)
                    }
                }

                dataList.reverse()

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





