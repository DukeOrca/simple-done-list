package com.duke.orca.android.kotlin.simpledonelist.base.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.duke.orca.android.kotlin.simpledonelist.application.Application
import com.duke.orca.android.kotlin.simpledonelist.application.BLANK
import com.duke.orca.android.kotlin.simpledonelist.application.hide
import com.duke.orca.android.kotlin.simpledonelist.databinding.FragmentConfirmationDialogBinding

class ConfirmationDialogFragment : BaseDialogFragment<FragmentConfirmationDialogBinding>() {
    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): FragmentConfirmationDialogBinding {
        return FragmentConfirmationDialogBinding.inflate(inflater, container, false)
    }

    interface OnButtonClickListener {
        fun onNegativeButtonClick(dialogFragment: DialogFragment)
        fun onPositiveButtonClick(dialogFragment: DialogFragment)
    }

    private var title = BLANK
    private var message = BLANK
    private var negativeButtonText = BLANK
    private var positiveButtonText = BLANK

    private var onButtonClickListener: OnButtonClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(Key.TITLE, BLANK)
            message = it.getString(Key.MESSAGE, BLANK)
            negativeButtonText = it.getString(Key.NEGATIVE_BUTTON_TEXT, BLANK)
            positiveButtonText = it.getString(Key.POSITIVE_BUTTON_TEXT, BLANK)
        }

        parentFragment?.let {
            if (it is OnButtonClickListener) {
                this.onButtonClickListener = it
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)

        if (title.isBlank()) {
            viewBinding.textViewTitle.hide()
        } else {
            viewBinding.textViewTitle.text = title
        }

        if (message.isBlank()) {
            viewBinding.textViewMessage.hide()
        } else {
            viewBinding.textViewMessage.text = message
        }

        viewBinding.textViewNegative.text = negativeButtonText
        viewBinding.textViewNegative.setOnClickListener {
            onButtonClickListener?.onNegativeButtonClick(this@ConfirmationDialogFragment)
        }

        viewBinding.textViewPositive.text = positiveButtonText
        viewBinding.textViewPositive.setOnClickListener {
            onButtonClickListener?.onPositiveButtonClick(this@ConfirmationDialogFragment)
        }

        return viewBinding.root
    }

    companion object {
        private const val PACKAGE_NAME = "${Application.PACKAGE_NAME}.base.views"

        private object Key {
            const val TITLE = "$PACKAGE_NAME.TITLE"
            const val MESSAGE = "$PACKAGE_NAME.MESSAGE"
            const val NEGATIVE_BUTTON_TEXT = "$PACKAGE_NAME.NEGATIVE_BUTTON_TEXT"
            const val POSITIVE_BUTTON_TEXT = "$PACKAGE_NAME.POSITIVE_BUTTON_TEXT"
        }

        fun newInstance(
            title: String,
            message: String,
            negativeButtonText: String,
            positiveButtonText: String
        ): ConfirmationDialogFragment {
            return ConfirmationDialogFragment().apply {
                arguments = bundleOf(
                    Key.TITLE to title,
                    Key.MESSAGE to message,
                    Key.NEGATIVE_BUTTON_TEXT to negativeButtonText,
                    Key.POSITIVE_BUTTON_TEXT to positiveButtonText
                )
            }
        }
    }
}