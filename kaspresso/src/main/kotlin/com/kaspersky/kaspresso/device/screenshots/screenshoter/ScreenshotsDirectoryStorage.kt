package com.kaspersky.kaspresso.device.screenshots.screenshoter

import android.content.Context
import android.os.Build
import android.os.Environment
import com.kaspersky.kaspresso.internal.extensions.other.createDirectoryRWX
import java.io.File

class ScreenshotsDirectoryStorage {

    private val clearedOutputDirectories = HashSet<File>()

    /**
     * Returns the directory for screenshots resolved by storage's root.
     * If the directory doesn't exist, it will be created.
     *
     * @param context a context to get directory on pre-lollipop.
     * @param screenshotDir desired root directory.
     * @return [File] which represents an existing directory.
     */
    fun getRootScreenshotDirectory(
        context: Context,
        screenshotDir: File
    ): File {
        val screenshotDeviceDir: File = getScreenshotDirectory(context, screenshotDir)
        if (!screenshotDeviceDir.exists()) {
            screenshotDeviceDir.createDirectoryRWX()
        }
        return screenshotDeviceDir
    }

    /**
     * Returns directory for a particular test.
     * If the directory exists, it will be deleted on the first method call.
     * If the directory doesn't exist, it will be created.
     *
     * @param screenshotTestDir desired directory resolved by the root dir.
     * @return [File] which represents an existing directory.
     */
    fun obtainDirectory(
        screenshotTestDir: File
    ): File {

        if (!clearedOutputDirectories.contains(screenshotTestDir)) {
            deletePath(screenshotTestDir, inclusive = false)
            clearedOutputDirectories.add(screenshotTestDir)
        }

        if (!screenshotTestDir.exists()) {
            screenshotTestDir.createDirectoryRWX()
        }

        return screenshotTestDir
    }

    private fun getScreenshotDirectory(context: Context, screenshotDir: File): File {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Use external storage.
            Environment.getExternalStorageDirectory().resolve(screenshotDir)
        } else {
            // Use internal storage.
            context.getDir(screenshotDir.canonicalPath, Context.MODE_WORLD_READABLE)
        }
    }

    private fun deletePath(path: File, inclusive: Boolean) {
        if (path.isDirectory) {
            path.listFiles()?.run {
                forEach { child -> deletePath(child, inclusive = true) }
            }
        }

        if (inclusive) path.delete()
    }
}