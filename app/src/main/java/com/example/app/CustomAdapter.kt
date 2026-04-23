package com.example.app

import android.content.Context
import android.content.Intent
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

        val dateTxt = view.findViewById<TextView>(R.id.date)
        val obNumTxt = view.findViewById<TextView>(R.id.obNum)
        val timeTxt = view.findViewById<TextView>(R.id.time)
        val occTxt = view.findViewById<TextView>(R.id.occurrence)
        val signTxt = view.findViewById<TextView>(R.id.sign)
        val menuBtn = view.findViewById<ImageView>(R.id.menuBtn)

        // ✅ SAFE BINDING
        dateTxt.text = item.date ?: ""
        obNumTxt.text = item.obNumber ?: ""
        timeTxt.text = item.time ?: ""
        occTxt.text = item.occurence ?: ""
        signTxt.text = item.sign ?: ""

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
                                .setMessage("Are you sure?")
                                .setPositiveButton("Yes") { _, _ ->

                                    FirebaseDatabase.getInstance()
                                        .getReference("occurrences")
                                        .child(id)
                                        .removeValue()
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
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