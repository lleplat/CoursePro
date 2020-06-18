package com.example.coursepro.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coursepro.R
import com.example.coursepro.lists.ItemToDo
import java.lang.IllegalArgumentException

class ItemAdapter(private val actionListener: ItemAdapter.ActionListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val dataSet : MutableList<ItemToDo> = mutableListOf()


    fun setData(newDataSet : List<ItemToDo>?) {
        dataSet.clear()
        if (newDataSet != null) {
            for (itemData in newDataSet) {
                if (itemData.header && itemData.description != "Divers") {
                    dataSet.add(itemData)
                    for (item in newDataSet) {
                        if (item.headerName == itemData.description) {
                            dataSet.add(item)
                        }
                    }
                }
            }
            // Add remaining items
            if (newDataSet.size != dataSet.size) {
                dataSet.add(ItemToDo("Divers", header = true))
                for (item in newDataSet) {
                    if (!(item in dataSet) && item.description != "Divers") {
                        dataSet.add(item)
                    }
                }
            }
        }
        notifyDataSetChanged()
    }



    override fun getItemCount(): Int = dataSet.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            HEADER -> {
                HeaderItemViewHolder(inflater.inflate(R.layout.item_header, parent, false))
            }
            ITEM -> {
                ItemViewHolder(inflater.inflate(R.layout.item, parent, false))
            }
            else -> {
                throw IllegalArgumentException("View type $viewType not supported")
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (dataSet[position].header) {
            true -> HEADER
            false -> ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderItemViewHolder -> {
                holder.bind(dataSet[position])
            }
            is ItemViewHolder -> {
                holder.bind(dataSet[position])
            }
        }
    }

    inner class ItemViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        private val descItem : CheckBox = itemView.findViewById(R.id.descItem)

        init {
            val checkItemView : CheckBox = (itemView as ViewGroup).getChildAt(0) as CheckBox
            checkItemView.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
                val listPosition = adapterPosition
                if (listPosition != RecyclerView.NO_POSITION) {
                    val clickedList = dataSet[listPosition]
                    actionListener.onItemClicked(clickedList, b)
                }
            }
            val listPosition = adapterPosition
            if (listPosition != RecyclerView.NO_POSITION) {
                checkItemView.isChecked = dataSet[adapterPosition].fait
            }
        }

        fun bind(itemToDo : ItemToDo) {
            descItem.text = itemToDo.description
            descItem.isChecked = itemToDo.fait
        }
    }

    inner class HeaderItemViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        private val title : TextView = itemView.findViewById(R.id.titleHeader)

        init {

        }

        fun bind(itemToDo : ItemToDo) {
            title.text = itemToDo.description
        }
    }


    interface ActionListener {
        fun onItemClicked(itemToDo : ItemToDo, value : Boolean)
    }

    companion object {
        private const val HEADER = 1
        private const val ITEM = 2

    }

}
