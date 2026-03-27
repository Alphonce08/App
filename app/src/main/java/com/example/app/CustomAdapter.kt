package com.example.walletapp


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.firebase.database.FirebaseDatabase
import java.time.format.DateTimeFormatter as DateTimeFormatter1


class CustomAdapter(var context: Context, var data:ArrayList<View>):BaseAdapter() {
    private class ViewHolder(row:View?){

        lateinit var mTxtDate: DatePicker
        var obNum: EditText
        var time: EditText
        var occurBk: EditText
        var sign: EditText
        var btn_save: Button

        init {

            this.mTxtDate = row?.findViewById(R.id.mTxtamount) as DatePicker
            this.obNum = row?.findViewById(R.id.obNum) as TextView
            this.time = row?.findViewById(R.id.time) as TextView
            this.occurBk = row?.findViewById(R.id.occurBk) as EditText
            this.sign = row?.findViewById(R.id.sign) as EditText
            this.btn_save = row?.findViewById(R.id.btn_save) as Button

        }
    }
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view:View?
        var viewHolder:ViewHolder
        if (convertView == null){
            var layout = LayoutInflater.from(context)
            view = layout.inflate(R.layout.view_layout,parent,false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }else{
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        var item:View = getItem(position) as View
        var format = DateTimeFormatter1.ofPattern("Mmm-dd-yyyy")
        var formattedDate = item.date.format(format)

        //viewHolder.mTxtDate.date = item.date
        viewHolder.obNum.text = item.TextView
        viewHolder.occurBk.text = item.EditText
        viewHolder.sign.text = item.EditText
        viewHolder.btn_save.setOnClickListener {

            var ref = FirebaseDatabase.getInstance().getReference().child("cars/"+item.rec_id)

            //toast a message to delete item
            ref.removeValue().addOnCompleteListener {
                if (it.isSuccessful) {

                    Toast.makeText(context, "Item has been Deleted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to delete item", Toast.LENGTH_SHORT).show()
                }

            }

        }


        return view as View
    }

    override fun getItem(position: Int): Any {
        return  data.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return data.count()
    }
}

