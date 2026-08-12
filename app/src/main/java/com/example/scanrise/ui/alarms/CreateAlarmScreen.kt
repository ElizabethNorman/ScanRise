package com.example.scanrise.ui.alarms


import android.app.TimePickerDialog
import android.text.format.DateFormat
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.scanrise.data.RepeatDay
import com.example.scanrise.data.ScanObjectEntity
import com.example.scanrise.data.isDaySelected
import com.example.scanrise.data.toggleRepeatDay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateAlarmScreen(
    hour: Int,
    minute: Int,
    label: String,
    repeatDays: Int,
    objects: List<ScanObjectEntity>,
    selectedObjectIds: Set<Long>,
    onTimeChanged: (Int, Int) -> Unit,
    onLabelChanged: (String) -> Unit,
    onRepeatDaysChanged: (Int) -> Unit,
    onObjectToggle: (Long) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onManageObjects: () -> Unit,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    val canSave =
        selectedObjectIds.isNotEmpty()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement =
            Arrangement.spacedBy(20.dp)
    ) {

        item {

            Text(
                text = "Create Alarm",
                style =
                    MaterialTheme.typography.headlineLarge
            )
        }

        item {

            OutlinedButton(
                onClick = {

                    TimePickerDialog(
                        context,
                        { _, selectedHour, selectedMinute ->

                            onTimeChanged(
                                selectedHour,
                                selectedMinute
                            )
                        },
                        hour,
                        minute,
                        DateFormat.is24HourFormat(context)
                    ).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text =
                        String.format(
                            "%02d:%02d",
                            hour,
                            minute
                        )
                )
            }
        }

        item {

            OutlinedTextField(
                value = label,
                onValueChange = onLabelChanged,
                label = {
                    Text("Label")
                },
                placeholder = {
                    Text("Morning")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {

            Column {

                Text(
                    text = "Repeat",
                    style =
                        MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                FlowRow(
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    RepeatDay.entries.forEach { day ->

                        FilterChip(
                            selected =
                                isDaySelected(
                                    repeatDays,
                                    day
                                ),
                            onClick = {

                                onRepeatDaysChanged(
                                    toggleRepeatDay(
                                        repeatDays,
                                        day
                                    )
                                )
                            },
                            label = {
                                Text(day.shortName)
                            }
                        )
                    }
                }
            }
        }

        item {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text = "Scan objects",
                    style =
                        MaterialTheme.typography.titleMedium
                )

                TextButton(
                    onClick = onManageObjects
                ) {
                    Text("Manage Objects")
                }
            }
        }

        if (objects.isEmpty()) {

            item {

                Text(
                    text =
                        "You need at least one saved object before this alarm can be enabled."
                )
            }

        } else {

            items(
                items = objects,
                key = { it.id }
            ) { scanObject ->

                val selected =
                    scanObject.id in selectedObjectIds

                Card(
                    onClick = {
                        onObjectToggle(scanObject.id)
                    },
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        Text(
                            text =
                                "${scanObject.emoji} ${scanObject.name}"
                        )

                        Checkbox(
                            checked = selected,
                            onCheckedChange = {
                                onObjectToggle(
                                    scanObject.id
                                )
                            }
                        )
                    }
                }
            }
        }

        item {

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Button(
                onClick = onSave,
                enabled = canSave,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Alarm")
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            TextButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }
        }
    }
}