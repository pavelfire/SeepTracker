package com.example.android.trackmysleepquality.sleeptracker

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.TextItemViewHolder
import com.example.android.trackmysleepquality.database.SleepNight

class SleepNightAdapter : RecyclerView.Adapter<TextItemViewHolder>(){
    var data = listOf<SleepNight>()
    set(value){
        field = value
        notifyDataSetChanged()
    }
    //Called when RecyclerView needs to know the size of the list.
    override fun getItemCount() = data.size
    //Called when RecyclerView needs to show an item.
    //The ViewHolder passed may be recycled, so make sure that this sets any properties that
    //may have been set previously.
    override fun onBindViewHolder(holder: TextItemViewHolder, position: Int) {
        val item = data[position]
        if(item.sleepQuality <= 1) {
            holder.textView.setTextColor(Color.RED)
        }else if(item.sleepQuality == 3){
            holder.textView.setTextColor(Color.GREEN)
        }else if(item.sleepQuality == 4){
            holder.textView.setTextColor(Color.MAGENTA)
        }else{
            //reset
            holder.textView.setTextColor(Color.BLUE)
        }
        holder.textView.text = item.sleepQuality.toString()
    }
    //Part of the RecyclerView adapter, called when RecyclerView needs a new [ViewHolder].
    //A ViewHolder holds a view for the [RecyclerView] as well as providing additional information
    //to the RecyclerView such as where on the screen it was last drawn during scrolling.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.support_simple_spinner_dropdown_item,
         parent, false) as TextView
        return TextItemViewHolder(view)
    }

    class ViewHolder()
}