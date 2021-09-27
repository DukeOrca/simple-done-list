package com.duke.orca.android.kotlin.simpledonelist.history.views

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.duke.orca.android.kotlin.simpledonelist.application.Application
import com.duke.orca.android.kotlin.simpledonelist.base.views.SingleChoiceDialogFragment
import com.duke.orca.android.kotlin.simpledonelist.databinding.SingleChoiceItemBinding
import com.duke.orca.android.kotlin.simpledonelist.history.models.History

class HistoryOptionsDialogFragment : SingleChoiceDialogFragment<String>() {
    interface OnOptionsItemSelectedListener {
        fun onOptionsItemSelected(dialogFragment: DialogFragment, option: String, history: History)
    }

    private val history by lazy { arguments?.getParcelable<History>(Key.HISTORY) }
    private var onOptionsItemSelectedListener: OnOptionsItemSelectedListener? = null

    override val items: Array<String> by lazy { arguments?.getStringArray(Key.OPTIONS) ?: emptyArray() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parentFragment?.let {
            if (it is OnOptionsItemSelectedListener) {
                this.onOptionsItemSelectedListener = it
            }
        }
    }

    override fun bind(viewBinding: SingleChoiceItemBinding, item: String) {
        viewBinding.root.text = item
        viewBinding.root.setOnClickListener {
            history?.let {
                onOptionsItemSelectedListener?.onOptionsItemSelected(this, item, it)
            } ?: let {
                showErrorToast()
                dismiss()
            }
        }
    }

    companion object {
        private const val PACKAGE_NAME = "${Application.PACKAGE_NAME}.history.views"

        private object Key {
            const val HISTORY = "$PACKAGE_NAME.HISTORY"
            const val OPTIONS = "$PACKAGE_NAME.OPTIONS"
        }

        fun newInstance(history: History, options: Array<String>): HistoryOptionsDialogFragment {
            return HistoryOptionsDialogFragment().apply {
                arguments = bundleOf(
                    Key.HISTORY to history,
                    Key.OPTIONS to options
                )
            }
        }
    }
}