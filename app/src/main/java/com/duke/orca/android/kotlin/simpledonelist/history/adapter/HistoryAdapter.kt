package com.duke.orca.android.kotlin.simpledonelist.history.adapter

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.duke.orca.android.kotlin.simpledonelist.R
import com.duke.orca.android.kotlin.simpledonelist.databinding.HistoryBinding
import com.duke.orca.android.kotlin.simpledonelist.history.models.History
import java.util.*

class HistoryAdapter : ListAdapter<History, HistoryAdapter.ViewHolder>(DiffCallback()) {
    private var inflater: LayoutInflater? = null
    private var simpleDateFormat: SimpleDateFormat? = null

    private var fontSize = 16F

    private var onItemClickListener: OnItemClickListener? = null
    private var onItemLongClickListener: OnItemLongClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(item: History)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(item: History): Boolean
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener
    }

    fun setFontSize(fontSize: Float, notifyItemRangeChanged: Boolean) {
        this.fontSize = fontSize

        if (notifyItemRangeChanged) {
            notifyItemRangeChanged(0, itemCount)
        }
    }

    fun getFontSize() = fontSize

    inner class ViewHolder(private val viewBinding: HistoryBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: History) {
            val context = viewBinding.root.context
            val date = Calendar.getInstance().apply {
                set(Calendar.JULIAN_DAY, item.julianDay.value)
            }.timeInMillis.format(context)

            val historyCount = "(${item.doneList.count()})"

            viewBinding.textViewHistoryCount.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize)

            viewBinding.textViewDate.text = date
            viewBinding.textViewHistoryCount.text = historyCount

            onItemClickListener?.let { onItemClickListener ->
                viewBinding.root.setOnClickListener {
                    onItemClickListener.onItemClick(item)
                }
            }

            onItemLongClickListener?.let { onItemLongClickListener ->
                viewBinding.root.setOnLongClickListener {
                    onItemLongClickListener.onItemLongClick(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = this.inflater ?: LayoutInflater.from(parent.context)
        this.inflater = inflater

        return ViewHolder(HistoryBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun Long.format(context: Context): String {
        val pattern = context.getString(R.string.format_date3)
        val simpleDateFormat = simpleDateFormat
            ?: SimpleDateFormat(pattern, Locale.getDefault()).also {
                simpleDateFormat = it
            }

        return simpleDateFormat.format(this)
    }

    class DiffCallback: DiffUtil.ItemCallback<History>() {
        override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
            return oldItem.julianDay.value == newItem.julianDay.value
        }

        override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
            return oldItem == newItem
        }
    }
}