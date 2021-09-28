package com.duke.orca.android.kotlin.simpledonelist.settings.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.duke.orca.android.kotlin.simpledonelist.R
import com.duke.orca.android.kotlin.simpledonelist.application.getVersionName
import com.duke.orca.android.kotlin.simpledonelist.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.simpledonelist.base.views.PreferenceFragment
import com.duke.orca.android.kotlin.simpledonelist.review.Review
import com.duke.orca.android.kotlin.simpledonelist.settings.adapter.AdapterItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SettingsFragment : PreferenceFragment() {
    override val toolbar by lazy { viewBinding.toolbar }
    override val toolbarTitleResId: Int = R.string.settings

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

    override fun onStop() {
        with(requireActivity().window) {
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.status_bar)
            navigationBarColor = ContextCompat.getColor(requireContext(), R.color.navigation_bar)
        }

        super.onStop()
    }

    private fun initData() {
        preferenceAdapter.submitList(
            arrayListOf(
                AdapterItem.Preference(
                    drawable = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_round_stay_primary_portrait_24
                    ),
                    summary = "${getString(R.string.dark_mode)}, ${getString(R.string.font_size)}",
                    onClick = {
                        addFragment(DisplaySettingsFragment())
                    },
                    title = getString(R.string.display)
                ),
                AdapterItem.Preference(
                    drawable = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_round_screen_lock_portrait_24
                    ),
                    summary = "${getString(R.string.lock_setting)}, ${getString(R.string.lock_type)}",
                    onClick = {
                        addFragment(LockScreenSettingsFragment())
                    },
                    title = getString(R.string.lock_screen)
                ),
                AdapterItem.Space(),
                AdapterItem.Content(
                    drawable = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_round_share_24
                    ),
                    onClick = {
                        shareApplication(requireContext())
                    },
                    title = getString(R.string.share_the_app)
                ),
                AdapterItem.Content(
                    drawable = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_round_rate_review_24
                    ),
                    onClick = {
                        Review.launchReviewFlow(requireActivity())
                    },
                    title = getString(R.string.write_review)
                ),
                AdapterItem.Content(
                    drawable = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_round_info_24
                    ),
                    isClickable = false,
                    onClick = {

                    },
                    summary = getVersionName(requireContext()),
                    title = getString(R.string.version)
                )
            )
        )
    }

    private fun bind() {
        viewBinding.recyclerView.apply {
            adapter = preferenceAdapter
            layoutManager = LinearLayoutManagerWrapper(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun addFragment(fragment: Fragment) {
        lifecycleScope.launch {
            childFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view, fragment, fragment.tag)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun shareApplication(context: Context) {
        val intent = Intent(Intent.ACTION_SEND)
        val text = "https://play.google.com/store/apps/details?id=${context.packageName}"

        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text)

        Intent.createChooser(intent, context.getString(R.string.share_the_app)).also {
            it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            context.startActivity(it)
        }
    }
}