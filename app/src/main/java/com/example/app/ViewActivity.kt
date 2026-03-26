package com.example.app

import android.os.Bundle
import android.widget.DatePicker
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ViewActivity : AppCompatActivity() {
    lateinit var mTxtDate: DatePicker
    lateinit var obNum: TextView
    lateinit var time: TextView
    lateinit var occurBk: TextView
    lateinit var sign: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view)
        mTxtDate = findViewById(R.id.mTxtDate)
        obNum = findViewById(R.id.obNum)
        time = findViewById(R.id.time)
        occurBk = findViewById(R.id.occurBk)
        sign = findViewById(R.id.sign)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}