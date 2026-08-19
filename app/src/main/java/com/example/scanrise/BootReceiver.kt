package com.example.scanrise

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.scanrise.alarm.AlarmScheduler
import com.example.scanrise.data.ScanRiseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent?
    ) {

        Log.d(
            "ScanRiseBoot",
            "BOOT RECEIVER FIRED: ${intent?.action}"
        )

        val supportedAction =
            intent?.action ==
                    Intent.ACTION_BOOT_COMPLETED ||
                    intent?.action ==
                    Intent.ACTION_MY_PACKAGE_REPLACED

        if (!supportedAction) {
            return
        }

        val pendingResult =
            goAsync()

        CoroutineScope(
            Dispatchers.IO
        ).launch {

            try {

                AlarmScheduler
                    .restoreEnabledAlarms(
                        context.applicationContext
                    )

            } catch (error: Exception) {

                Log.e(
                    "ScanRiseBoot",
                    "Failed to restore alarms after ${intent.action}",
                    error
                )

            } finally {

                pendingResult.finish()
            }
        }
    }
}
