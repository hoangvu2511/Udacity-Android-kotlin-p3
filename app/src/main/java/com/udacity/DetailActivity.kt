package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import com.udacity.databinding.ActivityDetailBinding
import com.udacity.utils.NOTIFICATION_ID_KEY
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Exception

class DetailActivity : AppCompatActivity() {
    companion object {
        const val DOWNLOAD_KEY = "DOWNLOAD_KEY"
    }

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        handleCancelNotification()
        checkDownloadStatus()
        binding.fab.setOnClickListener {
            onBackPressed()
        }
    }

    private fun checkDownloadStatus() {
        val downloadService =
            getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager ?: return
        val downloadId = intent.getLongExtra(DOWNLOAD_KEY, -1L)
        if (downloadId != -1L) {
            val downloadQuery = DownloadManager.Query()
            downloadQuery.setFilterById(downloadId)
            with(downloadService.query(downloadQuery)) {
                if (moveToFirst()) {
                    val idx = getColumnIndex(DownloadManager.COLUMN_STATUS)
                    val nameIdx = getColumnIndex(DownloadManager.COLUMN_TITLE)
                    if (idx < 0 || nameIdx < 0) return
                    binding.contentDetail.tvFileName.text = this@DetailActivity.getString(
                        R.string.detail_format,
                        getString(nameIdx),
                        when (getInt(idx)) {
                            DownloadManager.STATUS_SUCCESSFUL -> this@DetailActivity.getString(R.string.file_download_success)
                            DownloadManager.STATUS_FAILED -> this@DetailActivity.getString(R.string.file_download_fail)
                            else -> ""
                        }
                    )
                }
            }
        }

    }

    private fun handleCancelNotification() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        val id = intent.getIntExtra(NOTIFICATION_ID_KEY, -1)
        if (id != -1) {
            notificationManager.cancel(NOTIFICATION_ID_KEY, id)
        }
    }
}
