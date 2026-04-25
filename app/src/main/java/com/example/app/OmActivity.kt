package com.example.app

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class OmActivity : AppCompatActivity() {

    private lateinit var menuline: ImageView
    private lateinit var newMenu: ImageView
    private lateinit var viewReport: ImageView
    private lateinit var pendingReport: ImageView
    private lateinit var completeReport: ImageView

    private lateinit var txtPendingCount: TextView
    private lateinit var txtCompleteCount: TextView

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_om)

        initViews()
        initFirebase()
        setupNavigation()
        setupMenu()
        loadReportCounts()
    }

    private fun initViews() {
        menuline = findViewById(R.id.menuline)
        newMenu = findViewById(R.id.newMenu)
        viewReport = findViewById(R.id.viewReport)
        pendingReport = findViewById(R.id.pendingReport)
        completeReport = findViewById(R.id.completeReport)

        txtPendingCount = findViewById(R.id.txtPendingCount)
        txtCompleteCount = findViewById(R.id.txtCompleteCount)
    }

    private fun initFirebase() {
        mAuth = FirebaseAuth.getInstance()
    }

    private fun loadReportCounts() {

        val dbRef = FirebaseDatabase.getInstance().getReference("occurrences")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var pending = 0
                var complete = 0

                for (data in snapshot.children) {

                    val status = data.child("status")
                        .getValue(String::class.java)
                        ?.lowercase() ?: "pending"

                    when (status) {
                        "pending" -> pending++
                        "complete" -> complete++
                    }
                }

                // ✅ IMPORTANT: UPDATE UI
                txtPendingCount.text = "$pending"
                txtCompleteCount.text = "$complete"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@OmActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupNavigation() {

        newMenu.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        viewReport.setOnClickListener {
            startActivity(Intent(this, ViewActivity::class.java).apply {
                putExtra("filter", "all")
            })        }

        pendingReport.setOnClickListener {
            startActivity(Intent(this, ViewActivity::class.java).apply {
                putExtra("filter", "pending")
            })
        }

        completeReport.setOnClickListener {
            startActivity(Intent(this, ViewActivity::class.java).apply {
                putExtra("filter", "complete")
            })
        }

    }

    private fun setupMenu() {

        menuline.setOnClickListener {

            val popup = PopupMenu(this, menuline)
            popup.menuInflater.inflate(R.menu.menu_main, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {

                    R.id.homeMenu -> {
                        startActivity(Intent(this, ViewActivity::class.java))
                        true
                    }

                    R.id.pendingReport -> {
                        startActivity(
                            Intent(this, ViewActivity::class.java).apply {
                                putExtra("filter", "pending")
                            }
                        )
                        true
                    }

                    R.id.completeReport -> {
                        startActivity(
                            Intent(this, ViewActivity::class.java).apply {
                                putExtra("filter", "complete")
                            }
                        )
                        true
                    }

                    R.id.settingTxt -> {
                        startActivity(Intent(this, SettingsActivity::class.java))
                        true
                    }

                    R.id.logout -> {
                        showLogoutDialog()
                        true
                    }

                    else -> false
                }
            }

            popup.show()
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                mAuth.signOut()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}