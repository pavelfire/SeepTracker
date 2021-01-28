package com.example.android.trackmysleepquality.sleeptracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ListItemSleepNightBinding

//An adapter that provides a list of [SllepNight] to a RecyclerView.
//Setting [data] will cause the displayed list to update.
class SleepNightAdapter : ListAdapter<SleepNight, SleepNightAdapter.ViewHolder>(SleepNightDiffCallback()){
    //List of SleepNight that this adapter is adapting to be used by RecyclerView
    //var data = listOf<SleepNight>()
    //set(value){
    //    field = value
        //tell RecyclerView that the entire list has been updated
        //this will cause every item to redraw, and is expencive
    //    notifyDataSetChanged()
    //}
    //Called when RecyclerView needs to know the size of the list.
    //override fun getItemCount() = data.size
    //Called when RecyclerView needs to show an item.
    //The ViewHolder passed may be recycled, so make sure that this sets any properties that
    //may have been set previously.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)//data[position]
        holder.bind(item)
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

        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemSleepNightBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(item: SleepNight) {
            val res = itemView.context.resources
            binding.sleepLength.text = convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, res)
            binding.qualityString.text = convertNumericQualityToString(item.sleepQuality, res)
            binding.qualityImage.setImageResource(when (item.sleepQuality) {
                0 -> R.drawable.ic_sleep_0
                1 -> R.drawable.ic_sleep_1
                2 -> R.drawable.ic_sleep_2
                3 -> R.drawable.ic_sleep_3
                4 -> R.drawable.ic_sleep_4
                5 -> R.drawable.ic_sleep_5
                else -> R.drawable.ic_sleep_active
            })
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)

                val binding = ListItemSleepNightBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}
class SleepNightDiffCallback : DiffUtil.ItemCallback<SleepNight>(){
    override fun areItemsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem.nightId == newItem.nightId
    }

    override fun areContentsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem == newItem
    }

}