package com.duke.orca.android.kotlin.simpledonelist.donelist.adapter.itemdecoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemDecoration(private val marginBottom: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = marginBottom
    }
}