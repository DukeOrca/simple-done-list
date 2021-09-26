package com.duke.orca.android.kotlin.simpledonelist.base.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.duke.orca.android.kotlin.simpledonelist.R

abstract class BaseNavigationFragment<VB: ViewBinding> : BaseFragment<VB>() {
    abstract val toolbar: Toolbar

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        return viewBinding.root
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        with(requireActivity().window) {
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.status_bar2)
            navigationBarColor = ContextCompat.getColor(requireContext(), R.color.navigation_bar2)
        }
    }
}