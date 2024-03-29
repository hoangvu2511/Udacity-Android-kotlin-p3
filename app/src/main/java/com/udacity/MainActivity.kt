package com.udacity

import android.Manifest
import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import com.udacity.databinding.ActivityMainBinding
import com.udacity.utils.sendNotify
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action


    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (!it) {
            Toast.makeText(
                this,
                "Permission denied, can not show notification after download done.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                receiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                RECEIVER_EXPORTED
            )
        } else {
            registerReceiver(
                receiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            )

        }

        requestNotificationPermission()

        // TODO: Implement code below
        binding.contentMain.customButton.setOnClickListener {
            if (binding.contentMain.radioButtonGroup.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Please choose option", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val urlToDownload = when (binding.contentMain.radioButtonGroup.checkedRadioButtonId) {
                binding.contentMain.radioButtonOption1.id -> getString(R.string.radio_button_option_1_value)
                binding.contentMain.radioButtonOption2.id -> getString(R.string.radio_button_option_2_value)
                else -> getString(R.string.radio_button_option_3_value)
            }
            download(urlToDownload)
        }
    }

    override fun onStart() {
        super.onStart()
        binding.contentMain.customButton.reset()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {
                binding.contentMain.customButton.onHasDownloadId()
                val destinationIntent = Intent(context, DetailActivity::class.java).apply {
                    putExtra(DetailActivity.DOWNLOAD_KEY, id)
                    putExtra(DetailActivity.FILE_NAME_DOWNLOADED, getDownloadedFileName())
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }

                sendNotify(CHANNEL_ID, "Download completed", destinationIntent)

                lifecycleScope.launch {
                    delay(3000)
                    binding.contentMain.customButton.reset()
                }
            }

        }
    }

    private fun getDownloadedFileName(): String {
        return when(binding.contentMain.radioButtonGroup.checkedRadioButtonId) {
            binding.contentMain.radioButtonOption1.id -> getString(R.string.radio_button_option_1)
            binding.contentMain.radioButtonOption2.id -> getString(R.string.radio_button_option_2)
            else -> getString(R.string.radio_button_option_3)
        }
    }

    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    url.split("/").last()
                )

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        binding.contentMain.customButton.onHasDownloadId(downloadID)
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }
}