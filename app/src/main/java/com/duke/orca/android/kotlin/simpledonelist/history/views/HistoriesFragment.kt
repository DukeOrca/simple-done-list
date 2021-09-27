package com.duke.orca.android.kotlin.simpledonelist.history.views

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.duke.orca.android.kotlin.simpledonelist.R
import com.duke.orca.android.kotlin.simpledonelist.application.hide
import com.duke.orca.android.kotlin.simpledonelist.application.show
import com.duke.orca.android.kotlin.simpledonelist.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.simpledonelist.base.views.BaseNavigationFragment
import com.duke.orca.android.kotlin.simpledonelist.databinding.FragmentHistoriesBinding
import com.duke.orca.android.kotlin.simpledonelist.history.adapter.HistoryAdapter
import com.duke.orca.android.kotlin.simpledonelist.history.models.History
import com.duke.orca.android.kotlin.simpledonelist.history.viewmodel.HistoryViewModel
import com.duke.orca.android.kotlin.simpledonelist.main.viewmodel.MainViewModel
import com.duke.orca.android.kotlin.simpledonelist.settings.PreferencesKeys
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoriesFragment : BaseNavigationFragment<FragmentHistoriesBinding>(),
    DeleteHistoryConfirmationDialogFragment.OnButtonClickListener,
    HistoryAdapter.OnItemClickListener,
    HistoryAdapter.OnItemLongClickListener,
    HistoryOptionsDialogFragment.OnOptionsItemSelectedListener
{
    override fun inflate(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHistoriesBinding {
        return FragmentHistoriesBinding.inflate(inflater, container, false)
    }

    override val toolbar by lazy { viewBinding.toolbar }

    private val activityViewModel by activityViewModels<MainViewModel>()
    private val viewModel by viewModels<HistoryViewModel>()
    private val historyAdapter = HistoryAdapter()
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
        activityViewModel.settings.observe(viewLifecycleOwner, {
            val fontSize = it[PreferencesKeys.Display.fontSize] ?: 16F

            if (historyAdapter.getFontSize() != fontSize) {
                historyAdapter.setFontSize(fontSize, true)
            }
        })

        viewModel.histories.observe(viewLifecycleOwner, { list ->
            if (list.isNullOrEmpty()) {
                viewBinding.recyclerView.hide()
                viewBinding.textView.show()
            } else {
                viewBinding.recyclerView.show()
                viewBinding.textView.hide()
            }

            with(list.filter {
                it.doneList.isNotEmpty()
            }) {
                historyAdapter.submitList(this)
            }

            with(list.filterNot { it.doneList.isNotEmpty() }) {
                forEach {
                    viewModel.delete(it)
                }
            }
        })
    }

    private fun bind() {
        historyAdapter.setOnItemClickListener(this)
        historyAdapter.setOnItemLongClickListener(this)

        viewBinding.recyclerView.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManagerWrapper(requireContext())
            setHasFixedSize(true)
        }
    }

    override fun onStop() {
        with(requireActivity().window) {
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.status_bar)
            navigationBarColor = ContextCompat.getColor(requireContext(), R.color.navigation_bar)
        }

        super.onStop()
    }

    override fun onItemClick(item: History) {
        HistoryFragment.newInstance(item).also {
            childFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view, it, it.tag)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onItemLongClick(item: History): Boolean {
        HistoryOptionsDialogFragment.newInstance(item, options).also {
            it.show(childFragmentManager, it.tag)
            return true
        }
    }

    override fun onOptionsItemSelected(
        dialogFragment: DialogFragment,
        option: String,
        history: History
    ) {
        when(option) {
            options[0] -> {
                dialogFragment.dismiss()
                DeleteHistoryConfirmationDialogFragment.newInstance(history).also {
                    it.show(childFragmentManager, it.tag)
                }
            }
        }
    }

    override fun onNegativeButtonClick(dialogFragment: DialogFragment) {
        dialogFragment.dismiss()
    }

    override fun onPositiveButtonClick(dialogFragment: DialogFragment, history: History) {
        dialogFragment.dismiss()
        viewModel.delete(history)
    }
}