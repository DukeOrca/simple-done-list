package com.duke.orca.android.kotlin.simpledonelist.base.views

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.duke.orca.android.kotlin.simpledonelist.R

abstract class BaseDialogFragment<VB : ViewBinding> : DialogFragment() {
    private var _viewBinding: VB? = null
    protected val viewBinding: VB
        get() = _viewBinding!!

    abstract fun inflate(inflater: LayoutInflater, container: ViewGroup?): VB

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = inflate(inflater, container)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        return viewBinding.root
    }

    @CallSuper
    override fun onDestroyView() {
        dialog?.window?.setWindowAnimations(R.style.WindowAnimation_DialogFragment)
        _viewBinding = null
        super.onDestroyView()
    }

    protected fun showToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(requireContext(), text, duration).show()
    }

    protected fun showErrorToast() {
        showToast(getString(R.string.an_error_has_occurred), Toast.LENGTH_LONG)
    }
}