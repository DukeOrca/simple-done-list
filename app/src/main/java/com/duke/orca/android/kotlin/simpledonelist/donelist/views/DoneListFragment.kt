package com.duke.orca.android.kotlin.simpledonelist.donelist.views

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.duke.orca.android.kotlin.simpledonelist.R
import com.duke.orca.android.kotlin.simpledonelist.application.*
import com.duke.orca.android.kotlin.simpledonelist.application.annotation.MainFragment
import com.duke.orca.android.kotlin.simpledonelist.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.simpledonelist.base.views.BaseFragment
import com.duke.orca.android.kotlin.simpledonelist.camera.Camera
import com.duke.orca.android.kotlin.simpledonelist.databinding.FragmentDoneListBinding
import com.duke.orca.android.kotlin.simpledonelist.datastore.DataStore
import com.duke.orca.android.kotlin.simpledonelist.devicecredential.DeviceCredential
import com.duke.orca.android.kotlin.simpledonelist.devicecredential.DeviceCredential.confirmDeviceCredential
import com.duke.orca.android.kotlin.simpledonelist.devicecredential.annotation.RequireDeviceCredential
import com.duke.orca.android.kotlin.simpledonelist.donelist.adapter.DoneListAdapter
import com.duke.orca.android.kotlin.simpledonelist.donelist.adapter.itemdecoration.ItemDecoration
import com.duke.orca.android.kotlin.simpledonelist.donelist.model.Done
import com.duke.orca.android.kotlin.simpledonelist.donelist.tooltip.Tooltip
import com.duke.orca.android.kotlin.simpledonelist.donelist.viewmodels.DoneListViewModel
import com.duke.orca.android.kotlin.simpledonelist.donelist.views.DoneListFragment.Unlock.endRange
import com.duke.orca.android.kotlin.simpledonelist.history.models.JulianDay
import com.duke.orca.android.kotlin.simpledonelist.main.viewmodel.MainViewModel
import com.duke.orca.android.kotlin.simpledonelist.settings.PreferencesKeys
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

@AndroidEntryPoint
@MainFragment
@RequireDeviceCredential
class DoneListFragment : BaseFragment<FragmentDoneListBinding>(),
    EditDoneDialogFragment.OnButtonClickListener,
    DeleteDoneConfirmationDialogFragment.OnButtonClickListener,
    DoneListAdapter.OnItemClickListener,
    DoneListAdapter.OnItemLongClickListener,
    DoneOptionsDialogFragment.OnOptionsItemSelectedListener
{
    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): FragmentDoneListBinding {
        return FragmentDoneListBinding.inflate(layoutInflater, container, false)
    }

    private object Unlock {
        const val endRange = 600F
        var x = 0F
        var y = 0F
        var outOfEndRange = false
    }

    private val activityViewModel by activityViewModels<MainViewModel>()
    private val viewModel by viewModels<DoneListViewModel>()
    private val cameraApplicationLaunchIntent by lazy { Camera.getLaunchIntent(requireContext()) }
    private val doneListAdapter = DoneListAdapter()
    private val duration = Duration.MEDIUM
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            with(DataStore.getBoolean(requireContext(), PreferencesKeys.LockScreen.unlockWithBackKey, false)) {
                if (this) {
                    requireActivity().finish()
                } else {
                    viewBinding.frameLayoutUnlock.animateRipple()
                }
            }
        }
    }

    private val options by lazy { arrayOf(getString(R.string.edit), getString(R.string.delete)) }
    private val smoothScroller: SmoothScroller by lazy {
        object : LinearSmoothScroller(requireContext()) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
    }

    private val tooltip = Tooltip()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        observe()
        bind()

        if (DataStore.getIsFirstTime(requireContext())) {
            showTooltips()
        }

        activityViewModel.setBannerAdViewVisibility(View.VISIBLE)

        return viewBinding.root
    }

    override fun onDestroyView() {
        activityViewModel.setBannerAdViewVisibility(View.GONE)
        super.onDestroyView()
    }

    override fun onDetach() {
        onBackPressedCallback.remove()
        super.onDetach()
    }

    private fun observe() {
        activityViewModel.settings.observe(viewLifecycleOwner, {
            val fontSize = it[PreferencesKeys.Display.fontSize] ?: 16F

            if (doneListAdapter.getFontSize() != fontSize) {
                doneListAdapter.setFontSize(fontSize, true)
            }
        })

        viewModel.getDoneList(Calendar.getInstance().get(Calendar.JULIAN_DAY)).observe(viewLifecycleOwner, {
            viewModel.setDoneList(it)
        })

        viewModel.adapterItems.observe(viewLifecycleOwner, {
            doneListAdapter.submitList(it)
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun bind() {
        doneListAdapter.setFontSize(DataStore.Display.getFontSize(requireContext()), false)
        doneListAdapter.setIsTextColorWhite(true)
        doneListAdapter.setOnItemClickListener(this)
        doneListAdapter.setOnItemLongClickListener(this)
        doneListAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                viewBinding.recyclerView.scrollToPosition(positionStart)
            }
        })

        viewBinding.imageViewHistory.setOnClickListener {
            lifecycleScope.launch {
                findNavController().navigate(DoneListFragmentDirections.actionDoneListFragmentToHistoriesFragment())
            }
        }

        cameraApplicationLaunchIntent?.let { intent ->
            viewBinding.imageViewCamera.setOnClickListener {
                lifecycleScope.launch {
                    delay(Duration.SHORT)
                    startActivity(intent)
                }
            }
        } ?: let {
            viewBinding.imageViewCamera.hide()
        }

        viewBinding.imageViewSettings.setOnClickListener {
            lifecycleScope.launch {
                findNavController().navigate(DoneListFragmentDirections.actionDoneListFragmentToSettingsFragment())
            }
        }

        viewBinding.imageViewAdd.setOnClickListener {
            EditDoneDialogFragment.newInstance(EditDoneDialogFragment.Mode.INSERT).also {
                it.show(childFragmentManager, it.tag)
            }
        }

        viewBinding.recyclerView.apply {
            adapter = doneListAdapter
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManagerWrapper(requireContext())
            addItemDecoration(ItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_1dp)))
            setHasFixedSize(true)
        }

        viewBinding.linearLayoutUnlock.setOnTouchListener { _, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    Unlock.x = event.x
                    Unlock.y = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    viewBinding.frameLayoutUnlock.showRipple()

                    val distance = sqrt((Unlock.x - event.x).pow(2) + (Unlock.y - event.y).pow(2))
                    var scale = abs(endRange - distance * 0.45F) / endRange

                    when {
                        scale >= 1.0F -> scale = 1.0F
                        scale < 0.75F -> scale = 0.75F
                    }

                    val alpha = (scale - 0.75F) * 4.0F

                    viewBinding.linearLayout.alpha = alpha
                    viewBinding.linearLayout.scaleX = scale
                    viewBinding.linearLayout.scaleY = scale
                    viewBinding.imageViewUnlock.alpha = alpha
                    viewBinding.imageViewUnlock.scaleX = scale
                    viewBinding.imageViewUnlock.scaleY = scale
                    viewBinding.divider.alpha = alpha

                    Unlock.outOfEndRange = distance * 1.25F > endRange * 0.75F
                }
                MotionEvent.ACTION_UP -> {
                    if (Unlock.outOfEndRange) {
                        if (DeviceCredential.requireUnlock(requireContext()))
                            confirmDeviceCredential()
                        else {
                            requireActivity().finish()
                        }
                    } else {
                        restoreVisibility()
                    }
                }
            }

            true
        }
    }

    private fun restoreVisibility() {
        viewBinding.frameLayoutUnlock.hideRipple()
        viewBinding.imageViewUnlock.scale(1.0F, duration)
        viewBinding.linearLayout.scale(1.0F, duration)
    }

    private fun confirmDeviceCredential() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            confirmDeviceCredential(requireActivity(), object : KeyguardManager.KeyguardDismissCallback() {
                override fun onDismissCancelled() {
                    super.onDismissCancelled()
                    restoreVisibility()
                }

                override fun onDismissError() {
                    super.onDismissError()
                    restoreVisibility()
                }

                override fun onDismissSucceeded() {
                    super.onDismissSucceeded()
                    requireActivity().finish()
                }
            })
        } else {
            confirmDeviceCredential(
                this,
                getActivityResultLauncher(Key.DEVICE_CREDENTIAL)
            )

            restoreVisibility()
        }
    }

    private fun showTooltips() {
        viewBinding.imageViewAdd.post {
            tooltip.show(
                viewBinding.imageViewAdd,
                getString(R.string.tooltip_000),
                it.sephiroth.android.library.xtooltip.Tooltip.Gravity.BOTTOM
            ) {
                viewBinding.imageViewHistory.post {
                    tooltip.show(
                        viewBinding.imageViewHistory,
                        getString(R.string.tooltip_001),
                        it.sephiroth.android.library.xtooltip.Tooltip.Gravity.BOTTOM
                    ) {
                        DataStore.putFirstTime(requireContext(), false)
                    }
                }
            }
        }
    }

    override fun onItemClick(item: Done) {
        EditDoneDialogFragment.newInstance(EditDoneDialogFragment.Mode.EDIT, item).also {
            it.show(childFragmentManager, it.tag)
        }
    }

    override fun onItemLongClick(item: Done): Boolean {
        DoneOptionsDialogFragment.newInstance(item, options).also {
            it.show(childFragmentManager, it.tag)
            return true
        }
    }

    override fun onDeleteButtonClick(dialogFragment: DialogFragment, done: Done) {
        viewModel.delete(done)
        dialogFragment.dismiss()
    }

    override fun onSaveButtonClick(dialogFragment: DialogFragment, done: Done) {
        viewModel.insert(done)
        viewModel.insertJulianDay(JulianDay(done.julianDay))
        dialogFragment.dismiss()
    }

    override fun onOptionsItemSelected(dialogFragment: DialogFragment, option: String, done: Done) {
        when(option) {
            options[0] -> {
                dialogFragment.dismiss()
                EditDoneDialogFragment.newInstance(EditDoneDialogFragment.Mode.EDIT, done, showSoftKeyboard = true).also {
                    it.show(childFragmentManager, it.tag)
                }
            }
            options[1] -> {
                dialogFragment.dismiss()
                DeleteDoneConfirmationDialogFragment.newInstance(done).also {
                    it.show(childFragmentManager, it.tag)
                }
            }
        }
    }

    override fun onNegativeButtonClick(dialogFragment: DialogFragment) {
        dialogFragment.dismiss()
    }

    override fun onPositiveButtonClick(dialogFragment: DialogFragment, done: Done) {
        viewModel.delete(done)
        dialogFragment.dismiss()
    }

    companion object {
        private const val PACKAGE_NAME = "${Application.PACKAGE_NAME}.donelist.views"

        private object Key {
            const val DEVICE_CREDENTIAL = "$PACKAGE_NAME.DEVICE_CREDENTIAL"
        }
    }
}