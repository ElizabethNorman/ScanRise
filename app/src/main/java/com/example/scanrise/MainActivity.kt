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
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.scanrise.alarm.AlarmScheduler
import com.example.scanrise.data.ScanRiseDatabase
import com.example.scanrise.data.ScanObjectEntity

import com.example.scanrise.data.AlarmEntity
import com.example.scanrise.ui.alarms.AlarmsListScreen
import com.example.scanrise.ui.alarms.CreateAlarmScreen
import com.example.scanrise.ui.objects.ObjectDetailsScreen
import com.example.scanrise.ui.objects.ObjectsListScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
enum class AppScreen {
    ALARMS,
    OBJECTS,
    OBJECT_SCANNER,
    OBJECT_DETAILS,
    CREATE_ALARM
}

class MainActivity : ComponentActivity() {

    private var onCameraPermissionGranted: (() -> Unit)? = null

    private val requestCameraPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                onCameraPermissionGranted?.invoke()
            }

            onCameraPermissionGranted = null
        }

    private fun requestCameraAccess(
        onGranted: () -> Unit
    ) {
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            onGranted()
        } else {
            onCameraPermissionGranted = onGranted

            requestCameraPermission.launch(
                Manifest.permission.CAMERA
            )
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val database = ScanRiseDatabase.getDatabase(applicationContext)
        val scanObjectDao = database.scanObjectDao()
        val alarmDao = database.alarmDao()

        lifecycleScope.launch(Dispatchers.IO) {
            AlarmScheduler.restoreEnabledAlarms(
                applicationContext
            )
        }

        setContent {
            ScanRiseTheme {
                val context = LocalContext.current
                val objects by scanObjectDao
                    .getAll()
                    .collectAsState(initial = emptyList())

                val alarms by alarmDao.getAllWithObjects().collectAsState(initial = emptyList())

                var currentScreen by remember {
                    mutableStateOf(AppScreen.ALARMS)
                }

                var scannedBarcode by remember {
                    mutableStateOf<String?>(null)
                }

                var scannedBarcodeFormat by remember {
                    mutableIntStateOf(0)
                }

                var objectName by remember {
                    mutableStateOf("")
                }

                var objectEmoji by remember {
                    mutableStateOf("")
                }

                var errorMessage by remember {
                    mutableStateOf<String?>(null)
                }

                var alarmHour by remember {
                    mutableIntStateOf(7)
                }

                var alarmMinute by remember {
                    mutableIntStateOf(0)
                }

                var alarmLabel by remember {
                    mutableStateOf("")
                }

                var alarmRepeatDays by remember {
                    mutableIntStateOf(0)
                }

                var selectedObjectIds by remember {
                    mutableStateOf<Set<Long>>(
                        emptySet()
                    )
                }

                var editingAlarmId by remember {
                    mutableStateOf<Long?>(null)
                }

                fun resetAlarmForm() {

                    alarmHour = 7
                    alarmMinute = 0
                    alarmLabel = ""
                    alarmRepeatDays = 0
                    selectedObjectIds = emptySet()
                    editingAlarmId = null
                }

                val scope = rememberCoroutineScope()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),

                    bottomBar = {

                        if (
                            currentScreen == AppScreen.ALARMS ||
                            currentScreen == AppScreen.OBJECTS
                        ) {

                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.background,
                                tonalElevation = 0.dp
                            ) {

                                NavigationBarItem(
                                    selected =
                                        currentScreen ==
                                                AppScreen.ALARMS,

                                    onClick = {
                                        currentScreen =
                                            AppScreen.ALARMS
                                    },

                                    icon = {
                                        Text("⏰")
                                    },

                                    label = {
                                        Text("Alarms")
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = MaterialTheme.colorScheme.surface
                                    )
                                )

                                NavigationBarItem(
                                    selected =
                                        currentScreen ==
                                                AppScreen.OBJECTS,

                                    onClick = {
                                        currentScreen =
                                            AppScreen.OBJECTS
                                    },

                                    icon = {
                                        Text("📦")
                                    },

                                    label = {
                                        Text("Objects")
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = MaterialTheme.colorScheme.surface
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    when (currentScreen) {

                        AppScreen.ALARMS -> {

                            AlarmsListScreen(
                                alarms = alarms,
                                modifier =
                                    Modifier.padding(innerPadding),

                                onAddAlarm = {
                                    resetAlarmForm()

                                    currentScreen =
                                        AppScreen.CREATE_ALARM
                                },

                                onEditAlarm = { alarmId ->
                                    val alarmWithObjects =
                                        alarms.firstOrNull {
                                            it.alarm.id == alarmId
                                        }

                                    if (alarmWithObjects != null) {
                                        val alarm = alarmWithObjects.alarm
                                        editingAlarmId = alarm.id
                                        alarmHour = alarm.hour
                                        alarmMinute = alarm.minute
                                        alarmLabel = alarm.label
                                        alarmRepeatDays = alarm.repeatDays
                                        selectedObjectIds =
                                            alarmWithObjects.objects
                                                .map { it.id }
                                                .toSet()
                                        currentScreen = AppScreen.CREATE_ALARM
                                    }
                                },

                                onEnabledChanged = {
                                        alarmId,
                                        enabled ->

                                    scope.launch {

                                        alarmDao.setEnabled(
                                            alarmId,
                                            enabled
                                        )

                                        if (enabled) {

                                            val alarm =
                                                alarmDao
                                                    .getByIdWithObjects(
                                                        alarmId
                                                    )
                                                    ?.alarm

                                            if (alarm != null) {

                                                AlarmScheduler.scheduleNext(
                                                    context,
                                                    alarm.copy(
                                                        enabled = true
                                                    )
                                                )
                                            }

                                        } else {

                                            AlarmScheduler.cancel(
                                                context,
                                                alarmId
                                            )
                                        }
                                    }
                                },
                                onDeleteAlarm = { alarmId ->

                                    scope.launch {

                                        AlarmScheduler.cancel(
                                            context,
                                            alarmId
                                        )

                                        alarmDao.delete(
                                            alarmId
                                        )
                                    }
                                },
                                onDebugAlarm = {
                                    if (canScheduleExactAlarm(context)) {
                                        scheduleTestAlarm(context)
                                    } else {
                                        requestExactAlarmPermission(context)
                                    }
                                }
                            )
                        }

                        AppScreen.OBJECTS -> {

                            ObjectsListScreen(
                                objects = objects,
                                modifier =
                                    Modifier.padding(innerPadding),

                                onAddObject = {

                                    scannedBarcode = null
                                    objectName = ""
                                    objectEmoji = ""
                                    errorMessage = null

                                    requestCameraAccess {

                                        currentScreen =
                                            AppScreen.OBJECT_SCANNER
                                    }
                                },

                                onDeleteObject = { scanObject ->
                                    scope.launch {
                                        scanObjectDao.delete(scanObject)
                                    }
                                }
                            )
                        }

                        AppScreen.OBJECT_SCANNER -> {

                            BarcodeScannerView(
                                modifier = Modifier.fillMaxSize(),

                                onBarcodeScanned = {
                                        barcode,
                                        format ->

                                    scannedBarcode = barcode
                                    scannedBarcodeFormat = format

                                    currentScreen =
                                        AppScreen.OBJECT_DETAILS
                                }
                            )
                        }

                        AppScreen.OBJECT_DETAILS -> {

                            ObjectDetailsScreen(
                                barcode =
                                    scannedBarcode ?: "",

                                name = objectName,
                                emoji = objectEmoji,
                                errorMessage = errorMessage,

                                modifier =
                                    Modifier.padding(innerPadding),

                                onNameChanged = {
                                    objectName = it
                                },

                                onEmojiChanged = {
                                    objectEmoji = it
                                },

                                onSave = {

                                    val barcode =
                                        scannedBarcode
                                            ?: return@ObjectDetailsScreen

                                    scope.launch {

                                        val existing =
                                            scanObjectDao
                                                .getByBarcode(barcode)

                                        if (existing != null) {

                                            errorMessage =
                                                "That barcode is already saved as ${existing.emoji} ${existing.name}"

                                            return@launch
                                        }

                                        scanObjectDao.insert(
                                            ScanObjectEntity(
                                                name =
                                                    objectName.trim(),
                                                emoji =
                                                    objectEmoji.trim(),
                                                barcodeValue =
                                                    barcode,
                                                barcodeFormat =
                                                    scannedBarcodeFormat
                                            )
                                        )

                                        currentScreen =
                                            AppScreen.OBJECTS
                                    }
                                },

                                onCancel = {
                                    currentScreen =
                                        AppScreen.OBJECTS
                                }
                            )
                        }

                        AppScreen.CREATE_ALARM -> {

                            CreateAlarmScreen(
                                isEditing = editingAlarmId != null,
                                hour = alarmHour,
                                minute = alarmMinute,

                                label = alarmLabel,

                                repeatDays =
                                    alarmRepeatDays,

                                objects = objects,

                                selectedObjectIds =
                                    selectedObjectIds,

                                modifier =
                                    Modifier.padding(innerPadding),

                                onTimeChanged = {
                                        hour,
                                        minute ->

                                    alarmHour = hour
                                    alarmMinute = minute
                                },

                                onLabelChanged = {
                                    alarmLabel = it
                                },

                                onRepeatDaysChanged = {
                                    alarmRepeatDays = it
                                },

                                onObjectToggle = { objectId ->

                                    selectedObjectIds =
                                        if (
                                            objectId in
                                            selectedObjectIds
                                        ) {

                                            selectedObjectIds -
                                                    objectId

                                        } else {

                                            selectedObjectIds +
                                                    objectId
                                        }
                                },

                                onSave = {

                                    scope.launch {

                                        val alarmIdToEdit = editingAlarmId

                                        if (alarmIdToEdit != null) {
                                            val existingAlarm =
                                                alarmDao.getByIdWithObjects(
                                                    alarmIdToEdit
                                                )?.alarm

                                            if (existingAlarm != null) {
                                                val updatedAlarm =
                                                    existingAlarm.copy(
                                                        hour = alarmHour,
                                                        minute = alarmMinute,
                                                        label = alarmLabel.trim(),
                                                        repeatDays = alarmRepeatDays
                                                    )

                                                alarmDao.updateAlarmWithObjects(
                                                    alarm = updatedAlarm,
                                                    objectIds = selectedObjectIds
                                                )

                                                AlarmScheduler.cancel(
                                                    context,
                                                    alarmIdToEdit
                                                )

                                                if (updatedAlarm.enabled) {
                                                    AlarmScheduler.scheduleNext(
                                                        context,
                                                        updatedAlarm
                                                    )
                                                }
                                            }

                                            resetAlarmForm()
                                            currentScreen = AppScreen.ALARMS
                                            return@launch
                                        }

                                        val alarm =
                                            AlarmEntity(
                                                hour = alarmHour,
                                                minute = alarmMinute,
                                                label = alarmLabel.trim(),
                                                enabled = true,
                                                repeatDays = alarmRepeatDays
                                            )

                                        val alarmId =
                                            alarmDao.insertAlarmWithObjects(
                                                alarm = alarm,
                                                objectIds = selectedObjectIds
                                            )

                                        val savedAlarm =
                                            alarm.copy(
                                                id = alarmId
                                            )

                                        AlarmScheduler.scheduleNext(
                                            context,
                                            savedAlarm
                                        )

                                        resetAlarmForm()

                                        currentScreen =
                                            AppScreen.ALARMS
                                    }
                                },

                                onCancel = {

                                    resetAlarmForm()

                                    currentScreen =
                                        AppScreen.ALARMS
                                },

                                onManageObjects = {

                                    currentScreen =
                                        AppScreen.OBJECTS
                                }
                            )
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
