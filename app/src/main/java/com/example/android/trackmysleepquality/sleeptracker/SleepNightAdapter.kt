package com.example.android.trackmysleepquality.sleeptracker

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.TextItemViewHolder
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight
//An adapter that provides a list of [SllepNight] to a RecyclerView.
//Setting [data] will cause the displayed list to update.
class SleepNightAdapter : RecyclerView.Adapter<SleepNightAdapter.ViewHolder>(){
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
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        val res = holder.itemView.context.resources
        holder.sleepLength.text=convertDurationToFormatted(item.startTimeMilli,item.endTimeMilli,res)
        holder.quality.text = convertNumericQualityToString(item.sleepQuality, res)
        holder.qualityImage.setImageResource(when (item.sleepQuality){
            0 -> R.drawable.ic_sleep_0
            1 -> R.drawable.ic_sleep_1
            2 -> R.drawable.ic_sleep_2
            3 -> R.drawable.ic_sleep_3
            4 -> R.drawable.ic_sleep_4
            5 -> R.drawable.ic_sleep_5
            else -> R.drawable.ic_sleep_active
        })
        /*
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
        holder.textView.text = item.sleepQuality.toString()*/
    }
    //Part of the RecyclerView adapter, called when RecyclerView needs a new [ViewHolder].
    //A ViewHolder holds a view for the [RecyclerView] as well as providing additional information
    //to the RecyclerView such as where on the screen it was last drawn during scrolling.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.list_item_sleep_night,
         parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val sleepLength: TextView = itemView.findViewById(R.id.sleep_length)
        val quality: TextView = itemView.findViewById(R.id.quality_string)
        val qualityImage: ImageView = itemView.findViewById(R.id.quality_image)
    }
}