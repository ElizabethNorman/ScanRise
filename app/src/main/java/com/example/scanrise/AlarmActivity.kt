package com.example.scanrise

import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
import android.content.Intent
import com.example.scanrise.ui.theme.Brass
import com.example.scanrise.ui.theme.Hairline
import com.example.scanrise.ui.theme.Ink
import com.example.scanrise.ui.theme.Paper
import com.example.scanrise.ui.theme.Parchment
import com.example.scanrise.ui.theme.ScanRiseTheme
import com.example.scanrise.ui.theme.Slate
class AlarmActivity : ComponentActivity() {

    private var alarmWithObjects by
    mutableStateOf<AlarmWithObjects?>(null)

    private var loadFailed by
    mutableStateOf(false)


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



        setContent {
            ScanRiseTheme {

            if (loadFailed) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Parchment)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Unable to load alarm",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Ink
                    )

                    Spacer(
                        modifier = Modifier.height(32.dp)
                    )

                    Button(
                        onClick = {
                            stopAlarm()
                            finish()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Ink,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Stop alarm")
                    }
                }

                return@ScanRiseTheme
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
                        text = "Point the camera at your saved object",
                        style = MaterialTheme.typography.titleMedium,
                        color = Ink,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(24.dp)
                            .background(Paper, RoundedCornerShape(50))
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                }

            } else {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Parchment)
                        .padding(horizontal = 28.dp, vertical = 36.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    val currentAlarm =
                        alarmWithObjects

                    Text(
                        text = "GOOD MORNING",
                        style = MaterialTheme.typography.labelLarge,
                        color = Brass
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text =
                            currentAlarm
                                ?.alarm
                                ?.label
                                ?.takeIf {
                                    it.isNotBlank()
                                }
                                ?: "Time to rise",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Ink
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )



                    if (currentAlarm != null) {

                        Text(
                            text = "Scan one of these to dismiss",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Slate
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        currentAlarm.objects.forEach { scanObject ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Paper),
                                border = BorderStroke(1.dp, Hairline)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(18.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(scanObject.emoji, fontSize = 32.sp)
                                    Spacer(Modifier.width(14.dp))
                                    Text(
                                        scanObject.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Ink
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(32.dp)
                    )

                    scannedBarcode?.let {
                        Text(
                            text = "Scanned: $it",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Slate
                        )

                        Spacer(
                            modifier = Modifier.height(24.dp)
                        )
                    }

                    Button(
                        onClick = {
                            openScanner()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Ink,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Open scanner",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(48.dp)
                    )

                    OutlinedButton(
                        onClick = {
                            stopAlarm()
                            finish()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, Hairline),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Slate
                        )
                    ) {
                        Text(
                            text = "Stop alarm · testing only",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
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




    private fun stopAlarm() {

        val serviceIntent =
            Intent(
                this,
                AlarmService::class.java
            )

        stopService(serviceIntent)

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

}
