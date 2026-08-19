package com.example.scanrise.ui.alarms

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.scanrise.data.AlarmWithObjects
import com.example.scanrise.data.RepeatDay
import com.example.scanrise.data.isDaySelected
import com.example.scanrise.ui.theme.Brass
import com.example.scanrise.ui.theme.Night
import com.example.scanrise.ui.theme.NightRaised
import com.example.scanrise.ui.theme.Parchment
import com.example.scanrise.ui.theme.SoftBrass
import com.example.scanrise.ui.theme.Slate
import java.util.Calendar

@Composable
fun AlarmsListScreen(
    alarms: List<AlarmWithObjects>,
    onAddAlarm: () -> Unit,
    onEditAlarm: (Long) -> Unit,
    onEnabledChanged: (Long, Boolean) -> Unit,
    onDeleteAlarm: (Long) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Night)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(28.dp))

        Text(
            text = "Your alarms",
            style = MaterialTheme.typography.headlineLarge,
            color = Parchment
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

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No alarms yet",
                        style = MaterialTheme.typography.titleLarge,
                        color = Parchment
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Create one when you're ready.",
                        color = Slate
                    )
                }
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEditAlarm(alarm.id) },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = NightRaised),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate.copy(alpha = 0.35f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                                                .displayLarge,
                                        color = if (alarm.enabled) Parchment else Slate
                                    )

                                    if (
                                        alarm.label.isNotBlank()
                                    ) {

                                        Text(
                                            text = alarm.label,
                                            style =
                                                MaterialTheme
                                                    .typography
                                                    .titleMedium,
                                            color = Slate
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
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Brass,
                                        uncheckedThumbColor = Slate,
                                        uncheckedTrackColor = Night,
                                        uncheckedBorderColor = Slate
                                    )
                                )
                            }

                            Spacer(
                                modifier =
                                    Modifier.height(8.dp)
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AlarmTag(repeatDaysText(alarm.repeatDays))
                                alarmWithObjects.objects.take(2).forEach {
                                    AlarmTag("${it.emoji} ${it.name}")
                                }
                            }

                            Spacer(
                                modifier =
                                    Modifier.height(8.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = {
                                        onEditAlarm(alarm.id)
                                    }
                                ) {
                                    Text("Edit", color = Brass)
                                }

                                TextButton(
                                    onClick = {
                                        onDeleteAlarm(alarm.id)
                                    }
                                ) {
                                    Text("Delete", color = Slate)
                                }
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
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SoftBrass,
                contentColor = Night
            )
        ) {
            Text("Add alarm", fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun AlarmTag(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = Slate,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Night)
            .border(1.dp, Slate.copy(alpha = 0.4f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    )
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
