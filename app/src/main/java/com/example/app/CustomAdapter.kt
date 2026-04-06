package com.example.app

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.FirebaseDatabase

class CustomAdapter(
    var context: Context,
    var data: ArrayList<Occurrence>
) : BaseAdapter() {

    override fun getCount(): Int = data.size

    override fun getItem(position: Int): Any = data[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.view_layout, parent, false)

        // 🔗 Link UI (IDs MUST match XML)
        val date = view.findViewById<TextView>(R.id.date)
        val obNum = view.findViewById<TextView>(R.id.obNum)
        val timeTxt = view.findViewById<TextView>(R.id.time)
        val occurBk = view.findViewById<TextView>(R.id.occurrence)
        val signTxt = view.findViewById<TextView>(R.id.sign)
        val menuBtn = view.findViewById<ImageView>(R.id.menuBtn)

        val item = data[position]

        // ✅ Set values (labels already in XML)
        date.text = "Date: ${item.date ?: ""}"
        obNum.text = "OB Number: ${item.obNumber ?: ""}"
        timeTxt.text = "Time: ${item.time ?: ""}"
        occurBk.text = "Occurrence Details: ${item.occurence ?: ""}"
        signTxt.text = "Signature: ${item.sign ?: ""}"

        // 🔽 POPUP MENU (Edit & Delete)
        menuBtn.setOnClickListener {

            val popup = PopupMenu(context, menuBtn)
            popup.inflate(R.menu.item_menu)

            popup.setOnMenuItemClickListener {

                when (it.itemId) {

                    // 🗑 DELETE
                    R.id.delete -> {

                        if (item.rec_id != null) {
                            val ref = FirebaseDatabase.getInstance()
                                .getReference("occurrences")
                                .child(item.rec_id!!)

                            ref.removeValue().addOnSuccessListener {
                                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                            }.addOnFailureListener {
                                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                            }
                        }

                        true
                    }

                    // ✏️ EDIT
                    R.id.edit -> {

                        val intent = Intent(context, EditActivity::class.java)
                        intent.putExtra("id", item.rec_id)
                        intent.putExtra("date", item.date)
                        intent.putExtra("time", item.time)
                        intent.putExtra("ob", item.obNumber)
                        intent.putExtra("occ", item.occurence)
                        intent.putExtra("sign", item.sign)

                        context.startActivity(intent)

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