package com.duke.orca.android.kotlin.simpledonelist.settings.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import com.duke.orca.android.kotlin.simpledonelist.R
import com.duke.orca.android.kotlin.simpledonelist.application.DataStore
import com.duke.orca.android.kotlin.simpledonelist.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.simpledonelist.base.views.PreferenceFragment
import com.duke.orca.android.kotlin.simpledonelist.settings.PreferencesKeys
import com.duke.orca.android.kotlin.simpledonelist.settings.adapter.AdapterItem
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class LockScreenSettingsFragment : PreferenceFragment() {
    private object Id {
        const val DISPLAY_AFTER_UNLOCKING = 0L
    }

    override val toolbar by lazy { viewBinding.toolbar }
    override val toolbarTitleResId: Int = R.string.lock_screen

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
        val displayAfterUnlocking = DataStore.getBoolean(requireContext(), PreferencesKeys.LockScreen.displayAfterUnlocking, false)
        val showOnLockScreen = DataStore.getBoolean(requireContext(), PreferencesKeys.LockScreen.showOnLockScreen, true)
        val unlockWithBackKey = DataStore.getBoolean(requireContext(), PreferencesKeys.LockScreen.unlockWithBackKey, false)

        val list: List<AdapterItem> = arrayListOf(
            AdapterItem.SwitchPreference(
                drawable = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_unlock_90px
                ),
                isChecked = showOnLockScreen,
                onCheckedChange = { isChecked ->
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            dataStore.edit { mutablePreferences ->
                                mutablePreferences[PreferencesKeys.LockScreen.showOnLockScreen] = isChecked
                            }
                        }

                        val adapterItem = preferenceAdapter.getItem(Id.DISPLAY_AFTER_UNLOCKING)
                        val position = preferenceAdapter.getPosition(Id.DISPLAY_AFTER_UNLOCKING)

                        adapterItem?.isClickable = isChecked
                        adapterItem?.isVisible = isChecked

                        if (position != -1) {
                            preferenceAdapter.notifyItemChanged(position)
                        }
                    }
                },
                title = getString(R.string.show_on_lock_screen)
            ),
            AdapterItem.SwitchPreference(
                drawable = null,
                id = Id.DISPLAY_AFTER_UNLOCKING,
                isChecked = displayAfterUnlocking,
                isClickable = showOnLockScreen,
                isVisible = showOnLockScreen,
                onCheckedChange = { isChecked ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        dataStore.edit { mutablePreferences ->
                            mutablePreferences[PreferencesKeys.LockScreen.displayAfterUnlocking] = isChecked
                        }
                    }
                },
                title = getString(R.string.display_after_unlocking)
            ),
            AdapterItem.SwitchPreference(
                drawable = null,
                isChecked = unlockWithBackKey,
                onCheckedChange = { isChecked ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        dataStore.edit { mutablePreferences ->
                            mutablePreferences[PreferencesKeys.LockScreen.unlockWithBackKey] = isChecked
                        }
                    }
                },
                title = getString(R.string.unlock_with_back_button)
            )
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
}