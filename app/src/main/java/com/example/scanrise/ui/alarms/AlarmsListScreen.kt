package com.example.scanrise.ui.alarms

import android.content.Context
import android.text.format.DateFormat
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scanrise.data.AlarmWithObjects
import com.example.scanrise.data.RepeatDay
import com.example.scanrise.data.isDaySelected
import java.util.Calendar

@Composable
fun AlarmsListScreen(
    alarms: List<AlarmWithObjects>,
    onAddAlarm: () -> Unit,
    onEnabledChanged: (Long, Boolean) -> Unit,
    onDeleteAlarm: (Long) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Alarms",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        if (alarms.isEmpty()) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {

                Text("No alarms yet")
            }

        } else {

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                items(
                    items = alarms,
                    key = { it.alarm.id }
                ) { alarmWithObjects ->

                    val alarm = alarmWithObjects.alarm

                    Card(
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier =
                                Modifier.padding(20.dp)
                        ) {

                            Row(
                                modifier =
                                    Modifier.fillMaxWidth(),
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Column(
                                    modifier =
                                        Modifier.weight(1f)
                                ) {

                                    Text(
                                        text =
                                            formatAlarmTime(
                                                alarm.hour,
                                                alarm.minute
                                            ),
                                        style =
                                            MaterialTheme
                                                .typography
                                                .headlineMedium
                                    )

                                    if (
                                        alarm.label.isNotBlank()
                                    ) {

                                        Text(
                                            text = alarm.label,
                                            style =
                                                MaterialTheme
                                                    .typography
                                                    .titleMedium
                                        )
                                    }
                                }

                                Switch(
                                    checked = alarm.enabled,
                                    onCheckedChange = {
                                        onEnabledChanged(
                                            alarm.id,
                                            it
                                        )
                                    }
                                )
                            }

                            Spacer(
                                modifier =
                                    Modifier.height(8.dp)
                            )

                            Text(
                                text =
                                    repeatDaysText(
                                        alarm.repeatDays
                                    ),
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(8.dp)
                            )

                            Text(
                                text =
                                    alarmWithObjects.objects
                                        .joinToString("  ") {
                                            "${it.emoji} ${it.name}"
                                        },
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(8.dp)
                            )

                            TextButton(
                                onClick = {
                                    onDeleteAlarm(alarm.id)
                                }
                            ) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            onClick = onAddAlarm,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("+ Add Alarm")
        }
    }
}

@Composable
private fun formatAlarmTime(
    hour: Int,
    minute: Int
): String {

    val context =
        androidx.compose.ui.platform.LocalContext.current

    val calendar =
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }

    return DateFormat
        .getTimeFormat(context)
        .format(calendar.time)
}

private fun repeatDaysText(
    repeatDays: Int
): String {

    if (repeatDays == 0) {
        return "Once"
    }

    return RepeatDay.entries
        .filter {
            isDaySelected(
                repeatDays,
                it
            )
        }
        .joinToString(" ") {
            it.shortName
        }
}