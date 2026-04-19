package com.example.app

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class OmActivity : AppCompatActivity() {

    private lateinit var menuline: ImageView
    private lateinit var addMenu: ImageView
    private lateinit var addSettings: ImageView
    private lateinit var homeMenu: ImageView
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_om)

        // 🔑 Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // 🔗 Bind Views
        menuline = findViewById(R.id.menuline)
        homeMenu = findViewById(R.id.homeMenu)
        addMenu = findViewById(R.id.addMenu)
        addSettings = findViewById(R.id.addSettings)

        // 🏠 Navigation
        homeMenu.setOnClickListener {
            startActivity(Intent(this, ViewActivity::class.java))
        }

        addMenu.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        // ☰ Dropdown Menu (Logout inside)
        menuline.setOnClickListener {

            val popup = PopupMenu(this, menuline)
            popup.menuInflater.inflate(R.menu.menu_main, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {

                    R.id.logout -> {

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

                        true
                    }

                    else -> false
                }
            }

            popup.show()
        }

        // 📱 Handle Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}