package com.github.korblu.astrud.data.media

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

class AudioPermissionHelper(
    private val context: Context,
    private val activity: Activity,
    private val permissionLauncher: ActivityResultLauncher<String>,
    private val onGranted: () -> Unit,
    private val onDenied: () -> Unit,
    private val onDeniedPermanently: () -> Unit
) {
    private val permissionToRequest = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    fun checkAndRequest() {
        when {
            // Already Granted
            ContextCompat.checkSelfPermission(context, permissionToRequest) == PackageManager.PERMISSION_GRANTED -> {
                onGranted.invoke()
            }

            // Denied Previously
            activity.shouldShowRequestPermissionRationale(permissionToRequest) -> {
                showRationaleAndRequest()
            }

            // First Time Asking
            else -> {
                permissionLauncher.launch(permissionToRequest)
            }
        }
    }
    
    fun showRationaleAndRequest() {
        Toast.makeText(context, "WTH BRO WHY DID YOU DENY ACCEPT IT", Toast.LENGTH_SHORT).show()
        permissionLauncher.launch(permissionToRequest)
    }

    fun handleResult(isGranted : Boolean) {
        if(isGranted) {
            onGranted.invoke()
        } else{
            if(!activity.shouldShowRequestPermissionRationale(permissionToRequest)) {
                onDeniedPermanently.invoke()
            } else{
                onDenied.invoke()
            }
        }
    }
}