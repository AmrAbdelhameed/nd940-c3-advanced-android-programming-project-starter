package com.udacity.ui

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.utils.ButtonState
import com.udacity.R
import com.udacity.utils.createChannel
import com.udacity.utils.sendNotification
import com.udacity.utils.Constants.URL_GLIDE
import com.udacity.utils.Constants.URL_LOAD_APP
import com.udacity.utils.Constants.URL_RETROFIT
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private var baseUrl: String? = null
    private var fileName: String? = null

    private val notificationManager by lazy {
        ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        //creates notification channel
        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )

        custom_button.setOnClickListener {
            when (rgDownloadOptions.checkedRadioButtonId) {
                R.id.rbGlide -> {
                    baseUrl = URL_GLIDE
                    fileName = getString(R.string.glide_option)
                }
                R.id.rbLoadApp -> {
                    baseUrl = URL_LOAD_APP
                    fileName = getString(R.string.load_app_option)
                }
                R.id.rbRetrofit -> {
                    baseUrl = URL_RETROFIT
                    fileName = getString(R.string.retrofit_option)
                }
            }

            if (baseUrl != null) {
                custom_button.buttonState = ButtonState.Clicked
                download()
            } else Toast.makeText(this, getString(R.string.invalid_item), Toast.LENGTH_SHORT).show()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            custom_button.buttonState = ButtonState.Completed

            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val query = DownloadManager.Query()
                query.setFilterById(id)
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            notificationManager.sendNotification(
                                fileName.toString(),
                                getString(R.string.success_status),
                                applicationContext
                            )
                        }
                        DownloadManager.STATUS_FAILED -> {
                            notificationManager.sendNotification(
                                fileName.toString(),
                                getString(R.string.fail_status),
                                applicationContext
                            )
                        }
                    }
                }
            }
        }
    }

    private fun download() {
        custom_button.buttonState = ButtonState.Loading
        val request = DownloadManager.Request(Uri.parse(baseUrl))
            .setTitle(getString(R.string.app_name))
            .setDescription(getString(R.string.app_description))
            .setRequiresCharging(false)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)
    }
}