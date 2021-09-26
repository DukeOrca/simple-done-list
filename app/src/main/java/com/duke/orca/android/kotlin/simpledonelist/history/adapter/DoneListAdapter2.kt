package com.duke.orca.android.kotlin.simpledonelist.history.adapter

import android.content.Context
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.duke.orca.android.kotlin.simpledonelist.R
import com.duke.orca.android.kotlin.simpledonelist.databinding.Done2Binding
import com.duke.orca.android.kotlin.simpledonelist.donelist.model.Done
import java.util.*

class DoneListAdapter2 : ListAdapter<Done, DoneListAdapter2.ViewHolder>(DiffCallback()) {
    private var inflater: LayoutInflater? = null
    private var simpleDateFormat: SimpleDateFormat? = null

    private var fontSize = 16F
    private var isTextColorWhite = false

    private var onItemClickListener: OnItemClickListener? = null
    private var onItemLongClickListener: OnItemLongClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(item: Done)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(item: Done): Boolean
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

    fun setIsTextColorWhite(isTextColorWhite: Boolean) {
        this.isTextColorWhite = isTextColorWhite
    }

    fun getFontSize() = fontSize

    inner class ViewHolder(private val viewBinding: Done2Binding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: Done) {
            val context = viewBinding.root.context

            if (isTextColorWhite) {
                viewBinding.textViewContent.setTextColor(Color.WHITE)
                viewBinding.textViewWrittenTime.setTextColor(Color.WHITE)
            }

            viewBinding.textViewContent.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize)

            viewBinding.textViewWrittenTime.text = item.writtenTime.format(context)
            viewBinding.textViewContent.text = item.content

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

        return ViewHolder(Done2Binding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun Long.format(context: Context): String {
        val pattern = context.getString(R.string.format_12_hour2)
        val simpleDateFormat = simpleDateFormat
            ?: SimpleDateFormat(pattern, Locale.getDefault()).also {
                simpleDateFormat = it
            }

        return simpleDateFormat.format(this)
    }

    class DiffCallback: DiffUtil.ItemCallback<Done>() {
        override fun areItemsTheSame(oldItem: Done, newItem: Done): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Done, newItem: Done): Boolean {
            return oldItem == newItem
        }
    }
}