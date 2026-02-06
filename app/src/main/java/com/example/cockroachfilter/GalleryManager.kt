package com.example.cockroachfilter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File

/**
 * Gallery and media management for sharing and accessing saved media
 */
class GalleryManager(private val context: Context) {
    companion object {
        private const val TAG = "GalleryManager"
    }

    /**
     * Open gallery to view saved media
     */
    fun openGallery() {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                type = "image/*"
                data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            context.startActivity(Intent.createChooser(intent, "View Gallery"))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open gallery", e)
        }
    }

    /**
     * Share media file
     */
    fun shareMedia(file: File, mimeType: String = "*/*") {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(intent, "Share with"))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to share media", e)
        }
    }

    /**
     * Share to specific social media platform
     */
    fun shareToSocialMedia(file: File, platform: SocialMediaPlatform) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = if (file.extension == "mp4") "video/*" else "image/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                when (platform) {
                    SocialMediaPlatform.TIKTOK -> {
                        `package` = "com.ss.android.ugc.tiktok"
                    }
                    SocialMediaPlatform.INSTAGRAM -> {
                        `package` = "com.instagram.android"
                    }
                    SocialMediaPlatform.FACEBOOK -> {
                        `package` = "com.facebook.katana"
                    }
                    SocialMediaPlatform.TWITTER -> {
                        `package` = "com.twitter.android"
                    }
                    SocialMediaPlatform.WHATSAPP -> {
                        `package` = "com.whatsapp"
                    }
                }
            }

            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to share to ${platform.name}", e)
        }
    }

    /**
     * Get list of saved videos
     */
    fun getSavedVideos(): List<File> {
        return try {
            val videosDir = context.getExternalFilesDir("videos") ?: return emptyList()
            videosDir.listFiles { file ->
                file.extension == "mp4"
            }?.toList() ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get saved videos", e)
            emptyList()
        }
    }

    /**
     * Get list of saved screenshots
     */
    fun getSavedScreenshots(): List<File> {
        return try {
            val picturesDir = context.getExternalFilesDir("pictures") ?: return emptyList()
            picturesDir.listFiles { file ->
                file.extension in listOf("jpg", "jpeg", "png")
            }?.toList() ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get saved screenshots", e)
            emptyList()
        }
    }

    /**
     * Delete media file
     */
    fun deleteMedia(file: File): Boolean {
        return try {
            file.delete()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete media", e)
            false
        }
    }

    /**
     * Get total media size
     */
    fun getTotalMediaSize(): Long {
        return try {
            val videosDir = context.getExternalFilesDir("videos")
            val picturesDir = context.getExternalFilesDir("pictures")

            var totalSize = 0L
            videosDir?.listFiles()?.forEach { totalSize += it.length() }
            picturesDir?.listFiles()?.forEach { totalSize += it.length() }

            totalSize
        } catch (e: Exception) {
            Log.e(TAG, "Failed to calculate media size", e)
            0L
        }
    }

    enum class SocialMediaPlatform {
        TIKTOK,
        INSTAGRAM,
        FACEBOOK,
        TWITTER,
        WHATSAPP
    }
}
