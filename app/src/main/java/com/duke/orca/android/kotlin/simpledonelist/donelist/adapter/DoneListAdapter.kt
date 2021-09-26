package com.duke.orca.android.kotlin.simpledonelist.donelist.adapter

import android.content.Context
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.viewbinding.ViewBinding
import com.duke.orca.android.kotlin.simpledonelist.R
import com.duke.orca.android.kotlin.simpledonelist.admob.AdLoader
import com.duke.orca.android.kotlin.simpledonelist.databinding.DoneBinding
import com.duke.orca.android.kotlin.simpledonelist.databinding.NativeAdBinding
import com.duke.orca.android.kotlin.simpledonelist.donelist.model.Done
import com.google.android.gms.ads.nativead.NativeAd
import java.util.*

class DoneListAdapter : ListAdapter<AdapterItem, DoneListAdapter.ViewHolder>(DiffCallback()) {
    private var inflater: LayoutInflater? = null
    private var simpleDateFormat: SimpleDateFormat? = null

    private var fontSize = 16F
    private var isTextColorWhite = false

    private var onItemClickListener: OnItemClickListener? = null
    private var onItemLongClickListener: OnItemLongClickListener? = null

    private object ViewType {
        const val DONE = 0
        const val NATIVE_AD = 1
    }

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

    inner class ViewHolder(private val viewBinding: ViewBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun populateNativeAdView(item: AdapterItem.NativeAdWrapper) {
            if (viewBinding is NativeAdBinding) {
                AdLoader.populateNativeAdView(viewBinding, item.nativeAd)
            }
        }

        fun bind(item: AdapterItem.DoneWrapper) {
            if (viewBinding is DoneBinding) {
                val context = viewBinding.root.context
                val done = item.done

                if (isTextColorWhite) {
                    viewBinding.textViewContent.setTextColor(Color.WHITE)
                    viewBinding.textViewWrittenTime.setTextColor(Color.WHITE)
                }

                viewBinding.textViewContent.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize)

                viewBinding.textViewWrittenTime.text = done.writtenTime.format(context)
                viewBinding.textViewContent.text = done.content

                onItemClickListener?.let { onItemClickListener ->
                    viewBinding.root.setOnClickListener {
                        onItemClickListener.onItemClick(done)
                    }
                }

                onItemLongClickListener?.let { onItemLongClickListener ->
                    viewBinding.root.setOnLongClickListener {
                        onItemLongClickListener.onItemLongClick(done)
                    }
                }
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        with(recyclerView.itemAnimator) {
            if (this is SimpleItemAnimator) {
                this.supportsChangeAnimations = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = this.inflater ?: LayoutInflater.from(parent.context)
        this.inflater = inflater

        val viewBinding = if (viewType == ViewType.NATIVE_AD) {
            NativeAdBinding.inflate(inflater, parent, false)
        } else {
            DoneBinding.inflate(inflater, parent, false)
        }

        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(val item = getItem(position)) {
            is AdapterItem.NativeAdWrapper -> holder.populateNativeAdView(item)
            is AdapterItem.DoneWrapper -> holder.bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is AdapterItem.DoneWrapper -> ViewType.DONE
            is AdapterItem.NativeAdWrapper -> ViewType.NATIVE_AD
        }
    }

    private fun Long.format(context: Context): String {
        val pattern = context.getString(R.string.format_12_hour2)
        val simpleDateFormat = simpleDateFormat
            ?: SimpleDateFormat(pattern, Locale.getDefault()).also {
                simpleDateFormat = it
            }

        return simpleDateFormat.format(this)
    }
}

sealed class AdapterItem {
    abstract val id: Long

    class DoneWrapper(
        val done: Done,
        override val id: Long = done.id,
    ) : AdapterItem()

    class NativeAdWrapper(
        val nativeAd: NativeAd,
        override val id: Long = -1L
    ) : AdapterItem()
}

class DiffCallback: DiffUtil.ItemCallback<AdapterItem>() {
    override fun areItemsTheSame(oldItem: AdapterItem, newItem: AdapterItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: AdapterItem, newItem: AdapterItem): Boolean {
        return oldItem == newItem
    }
}