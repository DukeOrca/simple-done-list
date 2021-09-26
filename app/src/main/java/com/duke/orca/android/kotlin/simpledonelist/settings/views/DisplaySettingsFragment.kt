package com.duke.orca.android.kotlin.simpledonelist.settings.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.duke.orca.android.kotlin.simpledonelist.R
import com.duke.orca.android.kotlin.simpledonelist.application.DataStore
import com.duke.orca.android.kotlin.simpledonelist.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.simpledonelist.base.views.PreferenceFragment
import com.duke.orca.android.kotlin.simpledonelist.settings.PreferencesKeys
import com.duke.orca.android.kotlin.simpledonelist.settings.adapter.AdapterItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DisplaySettingsFragment : PreferenceFragment(), FontSizeChoiceDialogFragment.OnItemClickListener {
    private object Id {
        const val FONT_SIZE = 0L
    }

    override val toolbar by lazy { viewBinding.toolbar }
    override val toolbarTitleResId: Int = R.string.display

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        initData()
        bind()

        return viewBinding.root
    }

    private fun initData() {
        val fontSize = DataStore.getFloat(requireContext(), PreferencesKeys.Display.fontSize, 16F)
        val isDarkMode = DataStore.getBoolean(requireContext(), PreferencesKeys.Display.isDarkMode, true)

        val list: List<AdapterItem> = arrayListOf(
            AdapterItem.SwitchPreference(
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_round_dark_mode_24),
                isChecked = isDarkMode,
                onCheckedChange = { isChecked ->
                    lifecycleScope.launch {
                        val darkMode = if (isChecked) {
                            AppCompatDelegate.MODE_NIGHT_YES
                        } else {
                            AppCompatDelegate.MODE_NIGHT_NO
                        }

                        withContext(Dispatchers.IO) {
                            DataStore.Display.putDarkMode(requireContext(), isChecked)
                            delay(300L)
                        }

                        AppCompatDelegate.setDefaultNightMode(darkMode)
                    }
                },
                title = getString(R.string.dark_mode)
            ),
            AdapterItem.Preference(
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_round_format_size_24),
                id = Id.FONT_SIZE,
                summary = "${fontSize}dp",
                onClick = {
                    FontSizeChoiceDialogFragment().also {
                        it.show(childFragmentManager, it.tag)
                    }
                },
                title = getString(R.string.font_size)
            ),
        )

        preferenceAdapter.submitList(list)
    }

    private fun bind() {
        viewBinding.recyclerView.apply {
            adapter = preferenceAdapter
            layoutManager = LinearLayoutManagerWrapper(requireContext())
            setHasFixedSize(true)
        }
    }

    override fun onItemClick(dialogFragment: DialogFragment, item: Float) {
        lifecycleScope.launch(Dispatchers.IO) {
            DataStore.Display.putFontSize(requireContext(), item)
        }

        preferenceAdapter.updateSummary(Id.FONT_SIZE, "${item}dp")
        dialogFragment.dismiss()
    }
}