package top.easelink.framework.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

const val WRITE_EXTERNAL_CODE = 1

/**
 * should implement [OnRequestPermissionsResultCallback]#onRequestPermissionsResult(int, String[], int[]
 */
fun Activity.checkPermission() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            WRITE_EXTERNAL_CODE
        )
    }
}