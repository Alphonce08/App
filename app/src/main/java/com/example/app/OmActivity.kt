package com.example.app

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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
    private lateinit var mAuth: FirebaseAuth

    private lateinit var txtPendingCount: TextView
    private lateinit var txtCompleteCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_om)

        initViews()
        initFirebase()
        setupNavigation()
        setupMenu()
        handleInsets()
        loadReportCounts() // ✅ Firebase count
    }

    // 🔗 Bind Views
    private fun initViews() {
        menuline = findViewById(R.id.menuline)
        newMenu = findViewById(R.id.newMenu)
        viewReport = findViewById(R.id.viewReport)
        pendingReport = findViewById(R.id.pendingReport)
        completeReport = findViewById(R.id.completeReport)

        txtPendingCount = findViewById(R.id.txtPendingCount)
        txtCompleteCount = findViewById(R.id.txtCompleteCount)
    }

    // 🔑 Firebase Init
    private fun initFirebase() {
        mAuth = FirebaseAuth.getInstance()
    }

    // 📊 Load Report Counts
    private fun loadReportCounts() {

        val dbRef = FirebaseDatabase.getInstance().getReference("Reports")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var pending = 0
                var complete = 0

                for (data in snapshot.children) {
                    val status = data.child("status").getValue(String::class.java)

                    if (status == "pending") {
                        pending++
                    } else if (status == "complete") {
                        complete++
                    }
                }

                txtPendingCount.text = pending.toString()
                txtCompleteCount.text = complete.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@OmActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 🏠 Navigation Clicks
    private fun setupNavigation() {

        newMenu.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        viewReport.setOnClickListener {
            startActivity(Intent(this, ViewActivity::class.java))
        }
    }

    // ☰ Dropdown Menu
    private fun setupMenu() {

        menuline.setOnClickListener {

            val popup = PopupMenu(this, menuline)
            popup.menuInflater.inflate(R.menu.menu_main, popup.menu)

            // 🔥 Force icons to show
            try {
                val field = popup.javaClass.getDeclaredField("mPopup")
                field.isAccessible = true
                val menuHelper = field.get(popup)
                val classPopupHelper = Class.forName(menuHelper.javaClass.name)
                val setForceIcons = classPopupHelper.getMethod(
                    "setForceShowIcon",
                    Boolean::class.java
                )
                setForceIcons.invoke(menuHelper, true)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {

                    R.id.homeMenu -> {
                        startActivity(Intent(this, ViewActivity::class.java))
                        true
                    }

                    R.id.addMenu -> {
                        startActivity(Intent(this, HomeActivity::class.java))
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

    // 🚪 Logout Dialog
    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->

                mAuth.signOut()

                val intent = Intent(this, MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // 📱 Handle Insets
    private fun handleInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
    }
}