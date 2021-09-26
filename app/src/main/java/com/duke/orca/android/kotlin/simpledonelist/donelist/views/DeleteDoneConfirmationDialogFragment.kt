package com.duke.orca.android.kotlin.simpledonelist.donelist.views

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
import com.duke.orca.android.kotlin.simpledonelist.donelist.model.Done

class DeleteDoneConfirmationDialogFragment : BaseDialogFragment<FragmentConfirmationDialogBinding>() {
    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): FragmentConfirmationDialogBinding {
        return FragmentConfirmationDialogBinding.inflate(inflater, container, false)
    }

    interface OnButtonClickListener {
        fun onNegativeButtonClick(dialogFragment: DialogFragment)
        fun onPositiveButtonClick(dialogFragment: DialogFragment, done: Done)
    }

    private val done by lazy { arguments?.getParcelable<Done>(Key.DONE) }
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

        viewBinding.textViewTitle.text = getString(R.string.delete_things_done)
        viewBinding.textViewMessage.text = getString(R.string.delete_the_things_done)

        viewBinding.textViewNegative.text = getString(R.string.cancel)
        viewBinding.textViewNegative.setOnClickListener {
            onButtonClickListener?.onNegativeButtonClick(this@DeleteDoneConfirmationDialogFragment)
        }

        viewBinding.textViewPositive.text = getString(R.string.delete)
        viewBinding.textViewPositive.setOnClickListener {
            done?.let { done ->
                onButtonClickListener?.onPositiveButtonClick(this@DeleteDoneConfirmationDialogFragment, done)
            }
        }

        return viewBinding.root
    }

    companion object {
        private const val PACKAGE_NAME = "${Application.PACKAGE_NAME}.donelist.views"

        private object Key {
            const val DONE = "$PACKAGE_NAME.DONE"
        }

        fun newInstance(done: Done): DeleteDoneConfirmationDialogFragment {
            return DeleteDoneConfirmationDialogFragment().apply {
                arguments = bundleOf(Key.DONE to done)
            }
        }
    }
}