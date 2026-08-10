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

import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ScanRiseTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    val context = LocalContext.current

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = {
                                if (canScheduleExactAlarm(context)) {

                                    Toast.makeText(
                                        context,
                                        "Exact alarm permission: YES",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    scheduleTestAlarm(context)

                                } else {

                                    Toast.makeText(
                                        context,
                                        "Exact alarm permission: NO",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    requestExactAlarmPermission(context)
                                }
                            }
                        ) {
                            Text("TEST ALARM IN 60 SECONDS")
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