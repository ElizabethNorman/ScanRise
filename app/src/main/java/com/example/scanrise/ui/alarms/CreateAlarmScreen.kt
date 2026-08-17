package com.example.scanrise.ui.alarms


import android.app.TimePickerDialog
import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.scanrise.data.RepeatDay
import com.example.scanrise.data.ScanObjectEntity
import com.example.scanrise.data.isDaySelected
import com.example.scanrise.data.toggleRepeatDay
import com.example.scanrise.ui.theme.Brass
import com.example.scanrise.ui.theme.Night
import com.example.scanrise.ui.theme.NightRaised
import com.example.scanrise.ui.theme.Parchment
import com.example.scanrise.ui.theme.SoftBrass
import com.example.scanrise.ui.theme.Slate

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateAlarmScreen(
    isEditing: Boolean,
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
            .background(Night)
            .padding(24.dp),
        verticalArrangement =
            Arrangement.spacedBy(20.dp)
    ) {

        item {

            Text(
                text = if (isEditing) "Edit Alarm" else "Create Alarm",
                style = MaterialTheme.typography.headlineLarge,
                color = Parchment
            )

            Text(
                text = "Set the time and what you'll scan to dismiss it.",
                style = MaterialTheme.typography.bodyLarge,
                color = Slate
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(76.dp),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, Slate.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = NightRaised,
                    contentColor = Parchment
                )
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
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Parchment,
                    unfocusedTextColor = Parchment,
                    focusedContainerColor = NightRaised,
                    unfocusedContainerColor = NightRaised,
                    focusedBorderColor = Brass,
                    unfocusedBorderColor = Slate,
                    focusedLabelColor = Brass,
                    unfocusedLabelColor = Slate
                )
            )
        }

        item {

            Column {

                Text(
                    text = "Repeat",
                    style = MaterialTheme.typography.titleMedium,
                    color = Parchment
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
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = NightRaised,
                                labelColor = Slate,
                                selectedContainerColor = SoftBrass,
                                selectedLabelColor = Night
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isDaySelected(repeatDays, day),
                                borderColor = Slate,
                                selectedBorderColor = SoftBrass
                            )
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
                    style = MaterialTheme.typography.titleMedium,
                    color = Parchment
                )

                TextButton(
                    onClick = onManageObjects
                ) {
                    Text("Manage objects", color = Brass)
                }
            }
        }

        if (objects.isEmpty()) {

            item {

                Text(
                    text =
                        "Add an object before saving this alarm.",
                    color = Slate
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
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NightRaised),
                    border = BorderStroke(1.dp, Slate.copy(alpha = 0.45f))
                ) {

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                        horizontalArrangement =
                            Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {

                        Text(
                            text =
                                "${scanObject.emoji}  ${scanObject.name}",
                            color = Parchment,
                            style = MaterialTheme.typography.bodyLarge
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SoftBrass,
                    contentColor = Night,
                    disabledContainerColor = NightRaised,
                    disabledContentColor = Slate
                )
            ) {
                Text(if (isEditing) "Save Changes" else "Save Alarm")
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            TextButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel", color = Slate)
            }
        }
    }
}
