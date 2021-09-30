package com.duke.orca.android.kotlin.simpledonelist.donelist.tooltip

import android.view.View
import androidx.annotation.UiThread
import com.duke.orca.android.kotlin.simpledonelist.R
import com.duke.orca.android.kotlin.simpledonelist.application.Duration

import it.sephiroth.android.library.xtooltip.ClosePolicy
import it.sephiroth.android.library.xtooltip.Tooltip

class Tooltip {
    private var tooltip: Tooltip? = null

    fun show(
        view: View,
        text: String,
        gravity: Tooltip.Gravity,
        @UiThread doOnHidden: (() -> Unit)? = null
    ) {
        val context = view.context
        val animation = Tooltip.Animation.DEFAULT
        val maxWidth = context.resources.getDimensionPixelSize(R.dimen.width_160dp)
        val styleId = R.style.ToolTipStyle

        tooltip = Tooltip.Builder(context)
            .anchor(view, 0, 0, false)
            .arrow(true)
            .closePolicy(getClosePolicy())
            .fadeDuration(Duration.MEDIUM)
            .floatingAnimation(animation)
            .maxWidth(maxWidth)
            .overlay(true)
            .styleId(styleId)
            .text(text)
            .create()

        tooltip?.doOnHidden {
            tooltip = null
            doOnHidden?.invoke()
        }?.doOnFailure {

        }?.doOnShown {

        }?.show(view, gravity, true)
    }

    fun hide() {
        tooltip?.hide()
    }

    private fun getClosePolicy(): ClosePolicy {
        return ClosePolicy.Builder().apply {
            consume(true)
            inside(true)
            outside(true)
        }.build()
    }
}