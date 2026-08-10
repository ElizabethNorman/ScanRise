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

class AlarmActivity : ComponentActivity() {

    private var ringtone: Ringtone? = null

    private var scannedBarcode by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                        onBarcodeScanned = { barcode ->

                            scannedBarcode = barcode
                            showScanner = false

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

                    Text(
                        text = "🚨 SCANRISE TEST ALARM 🚨",
                        fontSize = 28.sp
                    )

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
    }

    override fun onDestroy() {
        ringtone?.stop()
        super.onDestroy()
    }
}