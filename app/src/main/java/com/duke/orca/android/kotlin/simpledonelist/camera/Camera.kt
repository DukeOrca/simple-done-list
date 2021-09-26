package com.duke.orca.android.kotlin.simpledonelist.camera

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.provider.MediaStore
import timber.log.Timber

object Camera {
    fun getLaunchIntent(context: Context): Intent? {
        val cameraApplicationPackageName = findCameraApplicationPackageName(context) ?: return null

        return try {
            context.packageManager.getLaunchIntentForPackage(cameraApplicationPackageName)?.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
        } catch (ignored: Exception) {
            Timber.e(ignored)
            null
        }
    }

    private fun findCameraApplicationPackageName(context: Context): String? {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val packageManager = context.packageManager
        val resolveInfoList = packageManager.queryIntentActivities(intent, 0)

        if (resolveInfoList.isEmpty()) return null

        for (resolveInfo in resolveInfoList) {
            if (isSystemApplication(context, resolveInfo.activityInfo.packageName)) {
                return resolveInfo.activityInfo.packageName
            }
        }

        return resolveInfoList[0].activityInfo.packageName
    }

    private fun isSystemApplication(context: Context, packageName: String): Boolean {
        val applicationInfo = try {
            context.packageManager.getApplicationInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.e(e)
            return false
        }

        val mask = ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP

        return applicationInfo.flags and mask != 0
    }
}