package com.example.app

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.FirebaseDatabase

class CustomAdapter(
    private val context: Context,
    private val data: ArrayList<Occurrence>
) : BaseAdapter() {

    override fun getCount(): Int = data.size
    override fun getItem(position: Int): Any = data[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.view_layout, parent, false)

        val item = data[position]

        // 🔗 Views
        val dateTxt = view.findViewById<TextView>(R.id.date)
        val timeTxt = view.findViewById<TextView>(R.id.time)
        val obNumTxt = view.findViewById<TextView>(R.id.obNum)
        val occTxt = view.findViewById<TextView>(R.id.occurrence)
        val toggleView = view.findViewById<TextView>(R.id.toggleView)
        val signTxt = view.findViewById<TextView>(R.id.sign)
        val statusTxt = view.findViewById<TextView>(R.id.status)
        val menuBtn = view.findViewById<ImageView>(R.id.menuBtn)

        // 🔹 Safe values
        val fullOcc = item.occurence ?: ""

        val isLongText = fullOcc.length > 80

        val shortOcc = if (isLongText) {
            fullOcc.take(80) + "..."
        } else {
            fullOcc
        }

        // 🔹 Default state (IMPORTANT for scrolling fix)
        occTxt.text = shortOcc
        occTxt.tag = false

        // show/hide toggle
        if (isLongText) {
            toggleView.visibility = View.VISIBLE
            toggleView.text = "View more"
        } else {
            toggleView.visibility = View.GONE
        }

        // 🔄 Toggle expand/collapse
        toggleView.setOnClickListener {
            val expanded = occTxt.tag as Boolean

            if (expanded) {
                occTxt.text = shortOcc
                toggleView.text = "View more"
                occTxt.tag = false
            } else {
                occTxt.text = fullOcc
                toggleView.text = "View less"
                occTxt.tag = true
            }
        }

        // 🔹 Bind other fields
        dateTxt.text = "Date: ${item.date ?: ""}"
        timeTxt.text = "Time: ${item.time ?: ""}"
        obNumTxt.text = item.obNumber ?: ""
        occTxt.text = "Occurence Details: ${item.occurence ?: ""}"
        signTxt.text = "Signed by: ${item.sign ?: ""}"

        // 🔹 Status
        val status = item.status ?: "pending"
        statusTxt.text = status.uppercase()

        statusTxt.setTextColor(
            if (status == "complete")
                Color.parseColor("#4CAF50")
            else
                Color.parseColor("#FF5722")
        )

        // 🔽 Menu
        menuBtn.setOnClickListener {

            val popup = PopupMenu(context, menuBtn)
            popup.inflate(R.menu.item_menu)

            popup.setOnMenuItemClickListener { menuItem ->

                when (menuItem.itemId) {

                    R.id.edit -> {
                        val intent = Intent(context, EditActivity::class.java)
                        intent.putExtra("id", item.rec_id ?: "")
                        intent.putExtra("date", item.date)
                        intent.putExtra("time", item.time)
                        intent.putExtra("ob", item.obNumber)
                        intent.putExtra("occ", item.occurence)
                        intent.putExtra("sign", item.sign)
                        context.startActivity(intent)
                        true
                    }

                    R.id.delete -> {

                        val id = item.rec_id ?: ""

                        if (id.isNotEmpty()) {

                            AlertDialog.Builder(context)
                                .setTitle("Delete Record")
                                .setMessage("Are you sure you want to delete this record?")
                                .setPositiveButton("Yes") { _, _ ->

                                    FirebaseDatabase.getInstance()
                                        .getReference("occurrences")
                                        .child(id)
                                        .removeValue()
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show()
                                        }
                                }
                                .setNegativeButton("No", null)
                                .show()
                        }

                        true
                    }

                    else -> false
                }
            }

            popup.show()
        }

        return view
    }
}