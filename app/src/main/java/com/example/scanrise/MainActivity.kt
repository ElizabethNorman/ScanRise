package com.example.scanrise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.scanrise.ui.theme.ScanRiseTheme
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.material3.Button
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.scanrise.data.ScanRiseDatabase
import com.example.scanrise.data.ScanObjectEntity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val database = ScanRiseDatabase.getDatabase(applicationContext)
        val scanObjectDao = database.scanObjectDao()
        setContent {
            ScanRiseTheme {

                val objects by scanObjectDao
                    .getAll()
                    .collectAsState(initial = emptyList())

                val scope = rememberCoroutineScope()

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Text(
                            text = "Objects in Room: ${objects.size}"
                        )

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        objects.forEach { scanObject ->
                            Text(
                                text = "${scanObject.emoji} ${scanObject.name} — ${scanObject.barcodeValue}"
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(24.dp)
                        )

                        Button(
                            onClick = {
                                scope.launch {
                                    scanObjectDao.insert(
                                        ScanObjectEntity(
                                            name = "Test Notebook",
                                            emoji = "📓",
                                            barcodeValue = "882709993490",
                                            barcodeFormat = 0
                                        )
                                    )
                                }
                            }
                        ) {
                            Text("INSERT TEST OBJECT")
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    scanObjectDao.deleteAll()
                                }
                            }
                        ) {
                            Text("DELETE ALL TEST OBJECTS")
                        }
                    }
                }
            }
        }


    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

private fun scheduleTestAlarm(context: Context) {

    val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, AlarmReceiver::class.java)

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        1234,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val triggerTime = System.currentTimeMillis() + 60_000

    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        triggerTime,
        pendingIntent
    )

    Toast.makeText(
        context,
        "Alarm scheduled for 60 seconds from now",
        Toast.LENGTH_LONG
    ).show()

    Log.d("ScanRise", "Alarm scheduled at $triggerTime")
}
private fun canScheduleExactAlarm(context: Context): Boolean {

    val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        alarmManager.canScheduleExactAlarms()
    } else {
        true
    }
}
private fun requestExactAlarmPermission(context: Context) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val intent = Intent(
            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
            Uri.parse("package:${context.packageName}")
        )

        context.startActivity(intent)
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ScanRiseTheme {
        Greeting("Android")

    }
}