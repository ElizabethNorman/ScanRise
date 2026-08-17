package com.example.scanrise

import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.scanrise.alarm.AlarmScheduler
import com.example.scanrise.data.AlarmWithObjects
import com.example.scanrise.data.ScanRiseDatabase
class AlarmActivity : ComponentActivity() {

    private var alarmWithObjects by
    mutableStateOf<AlarmWithObjects?>(null)

    private var loadFailed by
    mutableStateOf(false)
    private var ringtone: Ringtone? = null

    private var alarmId: Long = -1L

    private var scannedBarcode by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        alarmId =
            intent.getLongExtra(
                AlarmScheduler.EXTRA_ALARM_ID,
                -1L
            )

        if (alarmId == -1L) {

            loadFailed = true

        } else {

            lifecycleScope.launch {

                val database =
                    ScanRiseDatabase.getDatabase(
                        applicationContext
                    )

                alarmWithObjects =
                    database
                        .alarmDao()
                        .getByIdWithObjects(
                            alarmId
                        )

                if (alarmWithObjects == null) {
                    loadFailed = true
                }
            }
        }

        startAlarmSound()

        setContent {

            DisposableEffect(Unit) {
                onDispose {
                    ringtone?.stop()
                }
            }

            if (showScanner) {

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {

                    BarcodeScannerView(
                        modifier = Modifier.fillMaxSize(),
                        onBarcodeScanned = {
                                barcode,
                                _ ->

                            scannedBarcode = barcode

                            val currentAlarm =
                                alarmWithObjects

                            val matches =
                                currentAlarm
                                    ?.objects
                                    ?.any { scanObject ->

                                        scanObject.barcodeValue ==
                                                barcode
                                    }
                                    ?: false

                            if (matches) {

                                stopAlarm()
                                finish()

                            } else {

                                showScanner = false
                            }
                        }
                    )

                    Text(
                        text = "SCAN A BARCODE",
                        fontSize = 24.sp,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(32.dp)
                    )
                }

            } else {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    val currentAlarm =
                        alarmWithObjects

                    Text(
                        text =
                            currentAlarm
                                ?.alarm
                                ?.label
                                ?.takeIf {
                                    it.isNotBlank()
                                }
                                ?: "ScanRise Alarm",
                        fontSize = 28.sp
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    if (currentAlarm != null) {

                        Text(
                            text = "Scan one of:"
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                currentAlarm.objects
                                    .joinToString("   ") {
                                        "${it.emoji} ${it.name}"
                                    },
                            fontSize = 18.sp
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(32.dp)
                    )

                    scannedBarcode?.let {
                        Text(
                            text = "Scanned: $it",
                            fontSize = 18.sp
                        )

                        Spacer(
                            modifier = Modifier.height(24.dp)
                        )
                    }

                    Button(
                        onClick = {
                            openScanner()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "SCAN TO DISMISS",
                            fontSize = 20.sp
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(48.dp)
                    )

                    Button(
                        onClick = {
                            stopAlarm()
                            finish()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    ) {
                        Text(
                            text = "STOP ALARM\nTESTING ONLY",
                            fontSize = 24.sp
                        )
                    }
                }
            }
        }
    }

    private var showScanner by mutableStateOf(false)

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                showScanner = true
            }
        }

    private fun openScanner() {
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showScanner = true
        } else {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startAlarmSound() {
        val alarmUri =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        ringtone = RingtoneManager.getRingtone(this, alarmUri).apply {
            audioAttributes = android.media.AudioAttributes.Builder()
                .setUsage(android.media.AudioAttributes.USAGE_ALARM)
                .build()

            if (android.os.Build.VERSION.SDK_INT >= 28) {
                isLooping = true
            }

            play()
        }
    }

    private fun stopAlarm() {

        ringtone?.stop()
        ringtone = null

        if (alarmId != -1L) {

            NotificationManagerCompat
                .from(this)
                .cancel(
                    AlarmReceiver.notificationId(
                        alarmId
                    )
                )
        }
    }

    override fun onDestroy() {
        ringtone?.stop()
        super.onDestroy()
    }
}