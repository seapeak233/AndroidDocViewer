package com.seapeak.docviewer.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * Android存储权限管理工具类
 * 适配Android各版本的存储权限变化
 */
object PermissionHelper {
    
    const val REQUEST_CODE_STORAGE_PERMISSION = 1001
    const val REQUEST_CODE_MANAGE_EXTERNAL_STORAGE = 1002
    
    /**
     * 获取当前Android版本需要的存储权限
     */
    fun getRequiredStoragePermissions(): Array<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                // Android 13+ 需要细分权限
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO
                )
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                // Android 11-12 仍使用READ_EXTERNAL_STORAGE
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            else -> {
                // Android 10及以下
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }
    
    /**
     * 检查是否有存储权限
     */
    fun hasStoragePermission(context: Context): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                // Android 11+ 检查是否有管理外部存储权限或基本读取权限
                Environment.isExternalStorageManager() || hasBasicStoragePermission(context)
            }
            else -> {
                hasBasicStoragePermission(context)
            }
        }
    }
    
    /**
     * 检查基本存储权限
     */
    private fun hasBasicStoragePermission(context: Context): Boolean {
        val permissions = getRequiredStoragePermissions()
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    /**
     * 请求存储权限 - Activity版本
     */
    fun requestStoragePermission(activity: Activity) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                // Android 11+ 可以选择请求MANAGE_EXTERNAL_STORAGE权限
                if (!Environment.isExternalStorageManager()) {
                    requestManageExternalStoragePermission(activity)
                } else {
                    requestBasicStoragePermission(activity)
                }
            }
            else -> {
                requestBasicStoragePermission(activity)
            }
        }
    }
    
    /**
     * 请求存储权限 - Fragment版本
     */
    fun requestStoragePermission(fragment: Fragment) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                // Android 11+ 可以选择请求MANAGE_EXTERNAL_STORAGE权限
                if (!Environment.isExternalStorageManager()) {
                    requestManageExternalStoragePermission(fragment)
                } else {
                    requestBasicStoragePermission(fragment)
                }
            }
            else -> {
                requestBasicStoragePermission(fragment)
            }
        }
    }
    
    /**
     * 请求基本存储权限 - Activity版本
     */
    private fun requestBasicStoragePermission(activity: Activity) {
        val permissions = getRequiredStoragePermissions()
        ActivityCompat.requestPermissions(
            activity,
            permissions,
            REQUEST_CODE_STORAGE_PERMISSION
        )
    }
    
    /**
     * 请求基本存储权限 - Fragment版本
     */
    private fun requestBasicStoragePermission(fragment: Fragment) {
        val permissions = getRequiredStoragePermissions()
        fragment.requestPermissions(
            permissions,
            REQUEST_CODE_STORAGE_PERMISSION
        )
    }
    
    /**
     * 请求管理外部存储权限 - Activity版本
     */
    private fun requestManageExternalStoragePermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = Uri.parse("package:${activity.packageName}")
            }
            activity.startActivityForResult(intent, REQUEST_CODE_MANAGE_EXTERNAL_STORAGE)
        }
    }
    
    /**
     * 请求管理外部存储权限 - Fragment版本
     */
    private fun requestManageExternalStoragePermission(fragment: Fragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = Uri.parse("package:${fragment.requireContext().packageName}")
            }
            fragment.startActivityForResult(intent, REQUEST_CODE_MANAGE_EXTERNAL_STORAGE)
        }
    }
    
    /**
     * 处理权限请求结果
     */
    fun handlePermissionResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit
    ) {
        when (requestCode) {
            REQUEST_CODE_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    onPermissionGranted()
                } else {
                    onPermissionDenied()
                }
            }
        }
    }
    
    /**
     * 处理Activity结果（用于MANAGE_EXTERNAL_STORAGE权限）
     */
    fun handleActivityResult(
        requestCode: Int,
        resultCode: Int,
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit
    ) {
        when (requestCode) {
            REQUEST_CODE_MANAGE_EXTERNAL_STORAGE -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        onPermissionGranted()
                    } else {
                        onPermissionDenied()
                    }
                }
            }
        }
    }
    
    /**
     * 检查是否应该显示权限说明
     */
    fun shouldShowRequestPermissionRationale(activity: Activity): Boolean {
        val permissions = getRequiredStoragePermissions()
        return permissions.any { permission ->
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        }
    }
    
    /**
     * 检查文件是否可访问
     */
    fun isFileAccessible(filePath: String): Boolean {
        return try {
            val cleanPath = filePath.removePrefix("file://")
            val file = java.io.File(cleanPath)
            file.exists() && file.canRead() && file.length() > 0
        } catch (e: Exception) {
            false
        }
    }
}