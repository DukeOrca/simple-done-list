package com.duke.orca.android.kotlin.simpledonelist.main.view

import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.duke.orca.android.kotlin.simpledonelist.R
import com.duke.orca.android.kotlin.simpledonelist.application.Duration
import com.duke.orca.android.kotlin.simpledonelist.application.fadeIn
import com.duke.orca.android.kotlin.simpledonelist.application.fadeOut
import com.duke.orca.android.kotlin.simpledonelist.application.show
import com.duke.orca.android.kotlin.simpledonelist.bottomnavigation.BottomNavigationPressedListener
import com.duke.orca.android.kotlin.simpledonelist.bottomnavigation.BottomNavigationWatcher
import com.duke.orca.android.kotlin.simpledonelist.lockscreen.service.LockScreenService
import com.duke.orca.android.kotlin.simpledonelist.main.viewmodel.MainViewModel
import com.duke.orca.android.kotlin.simpledonelist.permission.PermissionChecker
import com.duke.orca.android.kotlin.simpledonelist.permission.view.PermissionRationaleDialogFragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(),
    BottomNavigationPressedListener,
    PermissionRationaleDialogFragment.OnPermissionAllowClickListener {
    private val viewModel by viewModels<MainViewModel>()
    private val adView by lazy { findViewById<AdView>(R.id.ad_view) }
    private val bottomNavigationWatcher = BottomNavigationWatcher(this)
    private val localBroadcastManager: LocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(this)
    }

    private var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (PermissionRationaleDialogFragment.permissionsGranted(this).not()) {
            PermissionRationaleDialogFragment().also {
                it.show(supportFragmentManager, it.tag)
            }
        }

        startService()
        observe()
        loadBannerAd()
    }

    override fun onResume() {
        super.onResume()
        bottomNavigationWatcher.setOnNavigationBarPressedListener(this)
        bottomNavigationWatcher.startWatch()
    }

    override fun onPause() {
        bottomNavigationWatcher.stopWatch()
        super.onPause()
    }

    override fun onDestroy() {
        sendBroadcast(Intent(LockScreenService.Action.MAIN_ACTIVITY_DESTROYED))
        super.onDestroy()
    }

    override fun onHomeKeyPressed() {
        localBroadcastManager.sendBroadcastSync(Intent(LockScreenService.Action.HOME_KEY_PRESSED))
    }

    override fun onRecentAppsPressed() {
        localBroadcastManager.sendBroadcastSync(Intent(LockScreenService.Action.RECENT_APPS_PRESSED))
    }

    override fun onPermissionAllowClick() {
        checkManageOverlayPermission()
    }

    override fun onPermissionDenyClick() {
        if (PermissionChecker.hasManageOverlayPermission()) {
            startService()
        } else {
            finish()
        }
    }

    private fun startService() {
        val intent = Intent(applicationContext, LockScreenService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun checkManageOverlayPermission() {
        if (PermissionChecker.hasManageOverlayPermission())
            startService()
        else {
            val uri = Uri.fromParts("package", packageName, null)
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            }

            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

            handler = Handler(mainLooper)

            handler?.postDelayed(object : Runnable {
                override fun run() {
                    if (Settings.canDrawOverlays(this@MainActivity)) {
                        Intent(this@MainActivity, MainActivity::class.java).run {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            val customAnimation = ActivityOptions.makeCustomAnimation(
                                this@MainActivity,
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )

                            startActivity(this, customAnimation.toBundle())
                        }

                        handler = null
                        return
                    }

                    handler?.postDelayed(this, Duration.LONG)
                }
            }, Duration.LONG)
        }
    }

    private fun observe() {
        viewModel.bannerAdViewVisibility.observe(this, {
            if (it == View.VISIBLE) {
                adView.fadeIn(200L)
            } else {
                adView.fadeOut(200L)
            }
        })
    }

    private fun loadBannerAd() {
        val adRequest = AdRequest.Builder().build()

        adView.loadAd(adRequest)
    }
}