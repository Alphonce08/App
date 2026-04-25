package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ViewActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var newOB: ImageView
    private lateinit var backBtn: ImageView

    private lateinit var dataList: ArrayList<Occurrence>
    private lateinit var adapter: CustomAdapter
    private lateinit var ref: DatabaseReference

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)

        mAuth = FirebaseAuth.getInstance()
        ref = FirebaseDatabase.getInstance().getReference("occurrences")

        listView = findViewById(R.id.listView)
        newOB = findViewById(R.id.newOB)
        backBtn = findViewById(R.id.backBtn)


        dataList = ArrayList()

        loadData()

        newOB.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        backBtn.setOnClickListener {
            startActivity(Intent(this, OmActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()

        if (mAuth.currentUser == null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun loadData() {

        val filter = intent.getStringExtra("filter")

        if (filter == "pending") {
            // show only pending
        } else if (filter == "complete") {
            // show only complete
        } else {
            // show all
        }
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                dataList.clear()

                for (snap in snapshot.children) {

                    val item = snap.getValue(Occurrence::class.java)

                    if (item != null) {

                        val status = item.status?.lowercase() ?: "pending"

                        // ✅ FILTER LOGIC HERE
                        if (filter == null || filter == "all" || status == filter) {
                            item.rec_id = snap.key
                            dataList.add(item)
                        }
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