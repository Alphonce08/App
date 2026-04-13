package com.example.app

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ViewActivity : AppCompatActivity() {

    private lateinit var newOB: Button
    private lateinit var listView: ListView
    private lateinit var dataList: ArrayList<Occurrence>
    private lateinit var adapter: CustomAdapter
    private lateinit var ref: DatabaseReference

    lateinit var btnLogout: Button
    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)

        // Firebase
        mAuth = FirebaseAuth.getInstance()

        // UI
        btnLogout = findViewById(R.id.btnLogout)
        listView = findViewById(R.id.listView)
        newOB = findViewById(R.id.newOB)

        dataList = ArrayList()

        // Firebase DB reference
        ref = FirebaseDatabase.getInstance().getReference("occurrences")

        // 🚪 LOGOUT
        btnLogout.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes") { _, _ ->

                    mAuth.signOut()

                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intent)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

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