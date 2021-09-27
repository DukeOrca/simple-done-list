package com.duke.orca.android.kotlin.simpledonelist.history.views

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.duke.orca.android.kotlin.simpledonelist.R
import com.duke.orca.android.kotlin.simpledonelist.application.Application
import com.duke.orca.android.kotlin.simpledonelist.application.BLANK
import com.duke.orca.android.kotlin.simpledonelist.application.hide
import com.duke.orca.android.kotlin.simpledonelist.application.show
import com.duke.orca.android.kotlin.simpledonelist.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.simpledonelist.base.views.BaseNavigationFragment
import com.duke.orca.android.kotlin.simpledonelist.databinding.FragmentHistoryBinding
import com.duke.orca.android.kotlin.simpledonelist.donelist.model.Done
import com.duke.orca.android.kotlin.simpledonelist.donelist.views.DeleteDoneConfirmationDialogFragment
import com.duke.orca.android.kotlin.simpledonelist.donelist.views.DoneOptionsDialogFragment
import com.duke.orca.android.kotlin.simpledonelist.donelist.views.EditDoneDialogFragment
import com.duke.orca.android.kotlin.simpledonelist.history.adapter.DoneListAdapter2
import com.duke.orca.android.kotlin.simpledonelist.history.models.History
import com.duke.orca.android.kotlin.simpledonelist.history.viewmodel.HistoryViewModel
import com.duke.orca.android.kotlin.simpledonelist.main.viewmodel.MainViewModel
import com.duke.orca.android.kotlin.simpledonelist.settings.PreferencesKeys
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class HistoryFragment : BaseNavigationFragment<FragmentHistoryBinding>(),
    EditDoneDialogFragment.OnButtonClickListener,
    DeleteDoneConfirmationDialogFragment.OnButtonClickListener,
    DoneListAdapter2.OnItemClickListener,
    DoneListAdapter2.OnItemLongClickListener,
    DoneOptionsDialogFragment.OnOptionsItemSelectedListener
{
    override val toolbar by lazy { viewBinding.toolbar }

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): FragmentHistoryBinding {
        return FragmentHistoryBinding.inflate(layoutInflater, container, false)
    }

    private val activityViewModel by activityViewModels<MainViewModel>()
    private val viewModel by viewModels<HistoryViewModel>()
    private val doneListAdapter = DoneListAdapter2()

    private val history by lazy { arguments?.getParcelable<History>(Key.HISTORY) }
    private val options by lazy { arrayOf(getString(R.string.delete)) }

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

    private fun observe() {
        history?.let { history ->
            viewModel.getDoneList(history.julianDay).asLiveData().observe(viewLifecycleOwner, {
                if (it.isNullOrEmpty()) {
                    viewBinding.recyclerView.hide()
                    viewBinding.textView.show()
                } else {
                    viewBinding.recyclerView.show()
                    viewBinding.textView.hide()
                }

                doneListAdapter.submitList(it)
            })
        } ?: showErrorToast()

        activityViewModel.settings.observe(viewLifecycleOwner, {
            val fontSize = it[PreferencesKeys.Display.fontSize] ?: 16F

            if (doneListAdapter.getFontSize() != fontSize) {
                doneListAdapter.setFontSize(fontSize, true)
            }
        })
    }

    private fun bind() {
        val pattern = getString(R.string.format_date3)
        val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())

        viewBinding.toolbar.title = history?.julianDay?.let {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.JULIAN_DAY, it.value)
            }

            simpleDateFormat.format(calendar.timeInMillis)
        } ?: BLANK

        doneListAdapter.setOnItemClickListener(this)
        doneListAdapter.setOnItemLongClickListener(this)

        viewBinding.recyclerView.apply {
            adapter = doneListAdapter
            layoutManager = LinearLayoutManagerWrapper(requireContext())
            setHasFixedSize(true)
        }
    }

    override fun onItemClick(item: Done) {
        EditDoneDialogFragment.newInstance(item, false, isReadMode = true).also {
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

    }

    override fun onOptionsItemSelected(dialogFragment: DialogFragment, option: String, done: Done) {
        when(option) {
            options[0] -> {
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
        private const val PACKAGE_NAME = "${Application.PACKAGE_NAME}.history.views"

        private object Key {
            const val HISTORY = "${PACKAGE_NAME}.HISTORY"
        }

        fun newInstance(history: History): HistoryFragment {
            return HistoryFragment().apply {
                arguments = bundleOf(Key.HISTORY to history)
            }
        }
    }
}