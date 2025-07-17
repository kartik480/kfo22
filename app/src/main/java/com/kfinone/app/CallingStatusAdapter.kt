package com.kfinone.app

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CallingStatusAdapter(private val callingStatuses: MutableList<CallingStatusItem>) : 
    RecyclerView.Adapter<CallingStatusAdapter.ViewHolder>() {
    
    private val TAG = "CallingStatusAdapter"

    init {
        Log.d(TAG, "Adapter created with ${callingStatuses.size} items")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d(TAG, "Creating new ViewHolder")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calling_status, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = callingStatuses[position]
        Log.d(TAG, "Binding item at position $position: ${item.callingStatus}")
        holder.callingStatusText.text = item.callingStatus
        holder.statusText.text = "Active"
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount called, returning: ${callingStatuses.size}")
        return callingStatuses.size
    }

    fun addCallingStatus(callingStatus: CallingStatusItem) {
        Log.d(TAG, "Adding calling status: ${callingStatus.callingStatus}")
        callingStatuses.add(callingStatus)
        notifyItemInserted(callingStatuses.size - 1)
    }

    fun updateCallingStatuses(newCallingStatuses: List<CallingStatusItem>) {
        Log.d(TAG, "Updating calling statuses with ${newCallingStatuses.size} items")
        callingStatuses.clear()
        callingStatuses.addAll(newCallingStatuses)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val callingStatusText: TextView = itemView.findViewById(R.id.callingStatusText)
        val statusText: TextView = itemView.findViewById(R.id.statusText)
    }
} 