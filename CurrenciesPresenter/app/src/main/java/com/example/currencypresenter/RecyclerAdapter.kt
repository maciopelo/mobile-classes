package com.example.currencypresenter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.lang.Integer.parseInt
import android.content.Context
import android.graphics.Color
import android.graphics.ColorSpace
import android.os.Build
import androidx.annotation.RequiresApi



class RecyclerAdapter(var dataList: List<RecyclerCurrencyItem>, private val listener: OnItemClickListener, val context:Context) : RecyclerView.Adapter<BaseViewHolder<*>>(){


    companion object{
        fun flagEmojiFromUnicode(unicode:String): String{
            val flagUnicodeSequence = unicode.split(' ')
            val firstPartOfUnicode = parseInt(flagUnicodeSequence[0].substring(2),16)
            val secondPartOfUnicode = parseInt(flagUnicodeSequence[1].substring(2),16)
            return String(Character.toChars(firstPartOfUnicode)) + String(Character.toChars(secondPartOfUnicode))
        }
    }

    // default holder when necessary data achieved
    inner class RecyclerViewHolder(itemView: View) : BaseViewHolder<RecyclerCurrencyItem>(itemView),View.OnClickListener {
        private val currencyFlag: TextView = itemView.findViewById(R.id.currencyFlag)
        private val currencyCode: TextView = itemView.findViewById(R.id.currencyCode)
        private val currencyRate: TextView = itemView.findViewById(R.id.currencyRate)
        private var isHigherThanPrev: TextView = itemView.findViewById(R.id.arrow)

        init{
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }
        }


        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun bind(item: RecyclerCurrencyItem) {

            currencyFlag.text = flagEmojiFromUnicode(context.getString(item.currencyCode))
            currencyCode.text = item.currencyCode
            currencyRate.text = item.currencyRate.toString()
            if (item.isHigherThanPrev) {
                isHigherThanPrev.setTextColor(Color.parseColor("#33cc33"))
                isHigherThanPrev.text = String(Character.toChars(parseInt(context.getString(R.string.up_arrow),16)))

            }else{
                isHigherThanPrev.setTextColor(Color.parseColor("#ff0000"))
                isHigherThanPrev.text = String(Character.toChars(parseInt(context.getString(R.string.down_arrow),16)))
            }

        }

    }

    // empty holder class in case of lack of necessary data
    inner class EmptyViewHolder(itemView: View): BaseViewHolder<String>(itemView){
        private val emptyTextView: TextView = itemView.findViewById(R.id.emptyText)


        override fun bind(item: String) {
            emptyTextView.text = item
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> = when(viewType) {

        0 -> EmptyViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.empty_layout,
                        parent, false))

        1 -> RecyclerViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.currency_item_layout,
                        parent, false))

        else -> throw IllegalArgumentException("Invalid ViewHolder")

    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {

        if(position != RecyclerView.NO_POSITION){
            when(holder){
                is EmptyViewHolder -> holder.bind(context.getString(R.string.empty_text))
                is RecyclerViewHolder -> {
                    holder.bind(dataList[position])
                }
                else -> throw IllegalArgumentException("Invalid ViewHolder")
            }
        }


    }

    override fun getItemCount(): Int {
        return when (val count = dataList.size) {
            0 -> 1
            else -> count
        }
    }

    override fun getItemViewType(position: Int): Int = if (dataList.count() == 0) 0 else 1



    private fun Context.getString(name: String): String {
        return resources.getString(resources.getIdentifier(name, "string", packageName))
    }


    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
}




