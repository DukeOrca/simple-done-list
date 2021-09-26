package com.duke.orca.android.kotlin.simpledonelist.donelist.views

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.duke.orca.android.kotlin.simpledonelist.application.Application
import com.duke.orca.android.kotlin.simpledonelist.base.views.SingleChoiceDialogFragment
import com.duke.orca.android.kotlin.simpledonelist.databinding.SingleChoiceItemBinding
import com.duke.orca.android.kotlin.simpledonelist.donelist.model.Done

class DoneOptionsDialogFragment : SingleChoiceDialogFragment<String>() {
    interface OnOptionsItemSelectedListener {
        fun onOptionsItemSelected(dialogFragment: DialogFragment, option: String, done: Done)
    }

    private val done by lazy { arguments?.getParcelable<Done>(Key.DONE) }
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
            done?.let {
                onOptionsItemSelectedListener?.onOptionsItemSelected(this, item, it)
            } ?: let {
                dismiss()
            }
        }
    }

    companion object {
        private const val PACKAGE_NAME = "${Application.PACKAGE_NAME}.donelist.views"

        private object Key {
            const val DONE = "$PACKAGE_NAME.DONE"
            const val OPTIONS = "$PACKAGE_NAME.OPTIONS"
        }

        fun newInstance(done: Done, options: Array<String>): DoneOptionsDialogFragment {
            return DoneOptionsDialogFragment().apply {
                arguments = bundleOf(
                    Key.DONE to done,
                    Key.OPTIONS to options
                )
            }
        }
    }
}