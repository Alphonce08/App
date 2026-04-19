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
    private lateinit var settingsMenu: ImageView
    private lateinit var homeMenu: ImageView
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_om)

        initViews()
        initFirebase()
        setupNavigation()
        setupMenu()
        handleInsets()
    }

    // 🔗 Bind Views
    private fun initViews() {
        menuline = findViewById(R.id.menuline)
        homeMenu = findViewById(R.id.homeMenu)
        addMenu = findViewById(R.id.addMenu)
        settingsMenu = findViewById(R.id.settingsMenu)
    }

    // 🔑 Firebase Init
    private fun initFirebase() {
        mAuth = FirebaseAuth.getInstance()
    }

    // 🏠 Navigation Clicks
    private fun setupNavigation() {

        homeMenu.setOnClickListener {
            startActivity(Intent(this, ViewActivity::class.java))
        }

        addMenu.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

    }

    // ☰ Dropdown Menu
    private fun setupMenu() {

        menuline.setOnClickListener {

            val popup = PopupMenu(this, menuline)
            popup.menuInflater.inflate(R.menu.menu_main, popup.menu)

            // 🔥 Force icons to show (optional)
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