package com.duke.orca.android.kotlin.simpledonelist.base.views

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.duke.orca.android.kotlin.simpledonelist.R

abstract class BaseFragment<VB: ViewBinding> : Fragment() {
    private var _viewBinding: VB? = null
    protected val viewBinding: VB
        get() = _viewBinding!!

    private val activityResultLauncherHashMap = hashMapOf<String, ActivityResultLauncher<Intent>>()

    protected fun putActivityResultLauncher(key: String, value: ActivityResultLauncher<Intent>) {
        activityResultLauncherHashMap[key] = value
    }

    protected fun getActivityResultLauncher(key: String) = activityResultLauncherHashMap[key]

    abstract fun inflate(inflater: LayoutInflater, container: ViewGroup?): VB

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = inflate(inflater, container)

        return viewBinding.root
    }

    @CallSuper
    override fun onDestroyView() {
        _viewBinding = null
        super.onDestroyView()
    }

    protected fun showErrorToast() {
        Toast.makeText(requireContext(), getString(R.string.an_error_has_occurred), Toast.LENGTH_LONG).show()
    }
}