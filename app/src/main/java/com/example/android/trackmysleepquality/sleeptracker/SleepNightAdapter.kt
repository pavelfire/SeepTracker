package com.example.android.trackmysleepquality.sleeptracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ListItemSleepNightBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1
//An adapter that provides a list of [SllepNight] to a RecyclerView.
//Setting [data] will cause the displayed list to update.
class SleepNightAdapter(val clickListener:SleepNightListener) : ListAdapter<DataItem,
        RecyclerView.ViewHolder>(SleepNightDiffCallback()){

    private val adapterScope = CoroutineScope(Dispatchers.Default)
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
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //val item = getItem(position)//data[position]
        //holder.bind(item)
        when(holder){
            is ViewHolder -> {
                val nightItem = getItem(position) as DataItem.SleepNightItem
                holder.bind(nightItem.sleepNight, clickListener)
            }
        }

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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            ITEM_VIEW_TYPE_HEADER -> TextViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw ClassCastException("Uknown viewType ${viewType}")
        }
    }
    override fun getItemViewType(position: Int): Int{
        return when (getItem(position)){
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.SleepNightItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    fun addHeaderAndSubmitList(list: List<SleepNight>?){
        adapterScope.launch {
            val items = when(list){
                null -> listOf(DataItem.Header)
                else -> listOf(DataItem.Header) + list.map{ DataItem.SleepNightItem(it)}
            }
            withContext(Dispatchers.Main){
                submitList(items)
            }
        }
    }

    class ViewHolder private constructor(val binding: ListItemSleepNightBinding):
            RecyclerView.ViewHolder(binding.root){

        fun bind(item: SleepNight, clickListener: SleepNightListener) {
            binding.sleep = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
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
class SleepNightDiffCallback : DiffUtil.ItemCallback<DataItem>(){
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

class SleepNightListener(val clickListener : (sleepId: Long) -> Unit){
    fun onClick(night: SleepNight) = clickListener(night.nightId)
}

sealed class DataItem{
    data class SleepNightItem(val sleepNight: SleepNight): DataItem(){
        override val id = sleepNight.nightId
    }
    object Header: DataItem(){
        override val id = Long.MIN_VALUE
    }

    abstract val id: Long
}

class TextViewHolder(view: View): RecyclerView.ViewHolder(view) {
    companion object {
        fun from(parent: ViewGroup): TextViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.header, parent, false)
            return TextViewHolder(view)
        }
    }
}