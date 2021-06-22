package com.example.praktika.dialogs
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.praktika.R

class RcViewDialogSpinnerAdapter(private val dialog: AlertDialog, private val tvSelection: TextView): RecyclerView.Adapter<RcViewDialogSpinnerAdapter.SpViewHolder>()
{
    private val mainList = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sp_list_item, parent, false)
        return SpViewHolder(view, dialog, tvSelection)
    }

    override fun getItemCount(): Int
    {
        return mainList.size
    }

    override fun onBindViewHolder(holder: SpViewHolder, position: Int)
    {
        holder.setData(mainList[position])
    }

    fun updateAdapter(list: ArrayList<String>)
    {
        mainList.clear()
        mainList.addAll(list)
        notifyDataSetChanged()
    }

    class SpViewHolder(itemView: View, private val dialog: AlertDialog, private val tvSelection: TextView): RecyclerView.ViewHolder(itemView), View.OnClickListener
    {
        private var itemText = ""
        
        fun setData(text:String)
        {
            val tvSpItem = itemView.findViewById<TextView>(R.id.tvSpItem)
            tvSpItem.text = text
            itemText = text
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?)
        {
            tvSelection.text = itemText
            dialog.dismiss()
        }
    }
}

