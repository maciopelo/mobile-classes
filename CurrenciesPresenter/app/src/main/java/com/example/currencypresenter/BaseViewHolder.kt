package com.example.currencypresenter

import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T>(itemView: View):RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item:T)
}