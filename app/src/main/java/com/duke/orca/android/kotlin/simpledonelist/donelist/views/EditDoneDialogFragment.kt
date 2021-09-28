package com.duke.orca.android.kotlin.simpledonelist.donelist.views

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.duke.orca.android.kotlin.simpledonelist.R
import com.duke.orca.android.kotlin.simpledonelist.application.*
import com.duke.orca.android.kotlin.simpledonelist.base.views.BaseDialogFragment
import com.duke.orca.android.kotlin.simpledonelist.databinding.FragmentEditDoneDialogBinding
import com.duke.orca.android.kotlin.simpledonelist.donelist.model.Done
import com.duke.orca.android.kotlin.simpledonelist.donelist.viewmodels.EditDoneDialogViewModel

class EditDoneDialogFragment : BaseDialogFragment<FragmentEditDoneDialogBinding>(),
    DeleteDoneConfirmationDialogFragment.OnButtonClickListener {
    override fun inflate(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditDoneDialogBinding {
        return FragmentEditDoneDialogBinding.inflate(inflater, container, false)
    }

    object Mode {
        const val EDIT = 0
        const val INSERT = 1
        const val READ = 2
    }

    private val viewModel by viewModels<EditDoneDialogViewModel>()
    private val isEditable by lazy { arguments?.getBoolean(Key.IS_EDITABLE) ?: true }
    private val showSoftKeyboard by lazy { arguments?.getBoolean(Key.SHOW_SOFT_KEYBOARD) ?: false }
    private var content = BLANK
    private var done: Done? = null
    private val mode by lazy { arguments?.getInt(Key.MODE) ?: Mode.INSERT }
    private var onButtonClickListener: OnButtonClickListener? = null

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
            viewModel.setContent(s.toString())
        }
    }

    interface OnButtonClickListener {
        fun onDeleteButtonClick(dialogFragment: DialogFragment, done: Done)
        fun onSaveButtonClick(dialogFragment: DialogFragment, done: Done)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()

        parentFragment?.let {
            if (it is OnButtonClickListener) {
                this.onButtonClickListener = it
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        observe()
        bind()

        return viewBinding.root
    }

    private fun initData() {
        arguments?.getParcelable<Done>(Key.DONE)?.let {
            viewModel.existingContent = it.content
            content = it.content
            viewModel.setContent(content)
            viewModel.setDone(it)
        } ?: let {
            viewModel.setContent(BLANK)
        }
    }

    private fun observe() {
        viewModel.content.observe(viewLifecycleOwner, {
            content = it
            viewBinding.textViewSave.isEnabled =
                it.isNotBlank() && (viewModel.existingContent != content)
        })

        if (mode == Mode.EDIT || mode == Mode.READ) {
            viewModel.done.observe(viewLifecycleOwner, {
                done = it
            })
        }
    }

    private fun bind() {
        when(mode) {
            Mode.EDIT -> {
                if (isEditable) {
                    viewBinding.imageViewEdit.show()
                    viewBinding.imageViewEdit.setOnClickListener {
                        if (viewBinding.editText.isFocusable.not()) {
                            viewBinding.editText.isFocusable = true
                            viewBinding.editText.isFocusableInTouchMode = true
                            viewBinding.editText.requestFocus()
                            showSoftKeyboard(viewBinding.editText)

                            try {
                                viewBinding.editText.setSelection(content.length)
                            } catch (e: IndexOutOfBoundsException) {
                                viewBinding.editText.setSelection(0)
                            }
                        }
                    }
                } else {
                    viewBinding.imageViewEdit.hide()
                }

                viewBinding.imageViewDelete.show()
                viewBinding.imageViewDelete.setOnClickListener {
                    done?.let { done ->
                        DeleteDoneConfirmationDialogFragment.newInstance(done).also {
                            it.show(childFragmentManager, it.tag)
                        }
                    } ?: let {
                        showErrorToast()
                        dismiss()
                    }
                }

                viewBinding.editText.setText(content)

                if (showSoftKeyboard) {
                    viewBinding.editText.requestFocus()
                    viewBinding.editText.post {
                        showSoftKeyboard(viewBinding.editText)
                    }
                } else {
                    viewBinding.editText.isFocusable = false
                }
            }
            Mode.INSERT -> {
                viewBinding.imageViewEdit.hide()
                viewBinding.imageViewDelete.hide()
                viewBinding.editText.requestFocus()
                viewBinding.editText.post {
                    showSoftKeyboard(viewBinding.editText)
                }
            }
            Mode.READ -> {
                viewBinding.imageViewEdit.hide()

                viewBinding.imageViewDelete.show()
                viewBinding.imageViewDelete.setOnClickListener {
                    done?.let { done ->
                        DeleteDoneConfirmationDialogFragment.newInstance(done).also {
                            it.show(childFragmentManager, it.tag)
                        }
                    } ?: let {
                        showErrorToast()
                        dismiss()
                    }
                }

                viewBinding.editText.setText(content)
                viewBinding.editText.isFocusable = false

                viewBinding.textViewCancel.setText(R.string.close)
                viewBinding.textViewSave.hide()
            }
        }

        viewBinding.editText.addTextChangedListener(textWatcher)

        viewBinding.textViewCancel.setOnClickListener {
            dismiss()
        }

        viewBinding.textViewSave.setOnClickListener {
            onButtonClickListener?.onSaveButtonClick(
                this,
                when(mode) {
                    Mode.EDIT -> Done(
                        id = done?.id ?: 0L,
                        content = content,
                        julianDay = viewModel.getJulianDay(),
                        writtenTime = viewModel.getWrittenTime()
                    )
                    Mode.INSERT -> Done(
                        content = content,
                        julianDay = viewModel.getJulianDay(),
                        writtenTime = viewModel.getWrittenTime()
                    )
                    else -> throw IllegalStateException()
                }
            )
        }
    }

    override fun onDestroyView() {
        viewBinding.editText.removeTextChangedListener(textWatcher)
        super.onDestroyView()
    }

    override fun onNegativeButtonClick(dialogFragment: DialogFragment) {
        dialogFragment.dismiss()
    }

    override fun onPositiveButtonClick(dialogFragment: DialogFragment, done: Done) {
        dialogFragment.dismiss()
        onButtonClickListener?.onDeleteButtonClick(this, done)
    }

    companion object {
        private const val PACKAGE_NAME = "${Application.PACKAGE_NAME}.donelist.views"

        private object Key {
            const val DONE = "$PACKAGE_NAME.DONE"
            const val IS_EDITABLE = "$PACKAGE_NAME.IS_EDITABLE"
            const val MODE = "$PACKAGE_NAME.MODE"
            const val SHOW_SOFT_KEYBOARD = "$PACKAGE_NAME.SHOW_SOFT_KEYBOARD"
        }

        fun newInstance(
            mode: Int,
            done: Done? = null,
            isEditable: Boolean = true,
            showSoftKeyboard: Boolean = false
        ): EditDoneDialogFragment {
            return EditDoneDialogFragment().apply {
                arguments = bundleOf(
                    Key.MODE to mode,
                    Key.DONE to done,
                    Key.IS_EDITABLE to isEditable,
                    Key.SHOW_SOFT_KEYBOARD to showSoftKeyboard
                )
            }
        }
    }
}