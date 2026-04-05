package com.example.app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.FirebaseDatabase

class CustomAdapter(
    var context: Context,
    var data: ArrayList<Occurrence>   // ✅ FIXED HERE (VERY IMPORTANT)
) : BaseAdapter() {

    override fun getCount(): Int = data.size

    override fun getItem(position: Int): Any = data[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.view_layout, parent, false)

        val date = view.findViewById<TextView>(R.id.date)
        val obNum = view.findViewById<TextView>(R.id.obNum)
        val timeTxt = view.findViewById<TextView>(R.id.time)
        val occurBk = view.findViewById<TextView>(R.id.occurrence)
        val signTxt = view.findViewById<TextView>(R.id.sign)
        val btnDelete = view.findViewById<Button>(R.id.saveBtn)

        val item = data[position]   // ✅ This is now Occurrence

        // ✅ Set data safely

        date.text = "Date:  ${item.date ?: ""}"
        obNum.text = "OB Number:  ${item.obNumber ?: ""}"
        timeTxt.text = "Time:  ${item.time ?: ""}"
        occurBk.text = "Occurrence Details:  ${item.occurence ?: ""}"
        signTxt.text = "Signature:  ${item.sign ?: ""}"

        // ✅ Delete from Firebase
        btnDelete.setOnClickListener {

            if (item.rec_id != null) {
                val ref = FirebaseDatabase.getInstance()
                    .getReference("occurrences")
                    .child(item.rec_id!!)

                ref.removeValue().addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return view
    }
}