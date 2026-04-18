package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class OmActivity : AppCompatActivity() {
    lateinit var menuline: ImageView
    lateinit var addMenu: ImageView
    lateinit var addSettings: ImageView
    lateinit var homeMenu: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_om)
        menuline = findViewById(R.id.menuline)
        homeMenu = findViewById(R.id.homeMenu)
        addMenu = findViewById(R.id.addMenu)
        addSettings = findViewById(R.id.addSettings)

        homeMenu.setOnClickListener {
            startActivity(Intent(this, ViewActivity::class.java))
        }
        addMenu.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}