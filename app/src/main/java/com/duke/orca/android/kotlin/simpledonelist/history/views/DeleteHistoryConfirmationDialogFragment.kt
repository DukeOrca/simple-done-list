package com.duke.orca.android.kotlin.simpledonelist.history.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.duke.orca.android.kotlin.simpledonelist.R
import com.duke.orca.android.kotlin.simpledonelist.application.Application
import com.duke.orca.android.kotlin.simpledonelist.base.views.BaseDialogFragment
import com.duke.orca.android.kotlin.simpledonelist.databinding.FragmentConfirmationDialogBinding
import com.duke.orca.android.kotlin.simpledonelist.history.models.History

class DeleteHistoryConfirmationDialogFragment : BaseDialogFragment<FragmentConfirmationDialogBinding>() {
    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): FragmentConfirmationDialogBinding {
        return FragmentConfirmationDialogBinding.inflate(inflater, container, false)
    }

    interface OnButtonClickListener {
        fun onNegativeButtonClick(dialogFragment: DialogFragment)
        fun onPositiveButtonClick(dialogFragment: DialogFragment, history: History)
    }

    private val history by lazy { arguments?.getParcelable<History>(Key.HISTORY) }
    private var onButtonClickListener: OnButtonClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentFragment?.let {
            if (it is OnButtonClickListener) {
                this.onButtonClickListener = it
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)

        viewBinding.textViewTitle.text = getString(R.string.delete_history)
        viewBinding.textViewMessage.text = getString(R.string.delete_the_history)

        viewBinding.textViewNegative.text = getString(R.string.cancel)
        viewBinding.textViewNegative.setOnClickListener {
            onButtonClickListener?.onNegativeButtonClick(this@DeleteHistoryConfirmationDialogFragment)
        }

        viewBinding.textViewPositive.text = getString(R.string.delete)
        viewBinding.textViewPositive.setOnClickListener {
            history?.let { history ->
                onButtonClickListener?.onPositiveButtonClick(this@DeleteHistoryConfirmationDialogFragment, history)
            }
        }

        return viewBinding.root
    }

    companion object {
        private const val PACKAGE_NAME = "${Application.PACKAGE_NAME}.history.views"

        private object Key {
            const val HISTORY = "$PACKAGE_NAME.HISTORY"
        }

        fun newInstance(history: History): DeleteHistoryConfirmationDialogFragment {
            return DeleteHistoryConfirmationDialogFragment().apply {
                arguments = bundleOf(Key.HISTORY to history)
            }
        }
    }
}