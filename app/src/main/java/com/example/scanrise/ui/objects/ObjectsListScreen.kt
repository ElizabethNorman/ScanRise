package com.example.scanrise.ui.objects

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scanrise.data.ScanObjectEntity
import com.example.scanrise.ui.theme.Night
import com.example.scanrise.ui.theme.NightRaised
import com.example.scanrise.ui.theme.Parchment
import com.example.scanrise.ui.theme.Slate
import com.example.scanrise.ui.theme.SoftBrass

@Composable
fun ObjectsListScreen(
    objects: List<ScanObjectEntity>,
    onAddObject: () -> Unit,
    onDeleteObject: (ScanObjectEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var objectPendingDeletion by remember {
        mutableStateOf<ScanObjectEntity?>(null)
    }

    objectPendingDeletion?.let { scanObject ->
        AlertDialog(
            onDismissRequest = {
                objectPendingDeletion = null
            },
            title = {
                Text("Delete ${scanObject.emoji} ${scanObject.name}?")
            },
            text = {
                Text("This object will also be removed from any alarms that use it.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteObject(scanObject)
                        objectPendingDeletion = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        objectPendingDeletion = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Night)
            .padding(horizontal = 20.dp)
    ) {

        Spacer(Modifier.height(28.dp))

        Text(
            text = "Objects",
            style = MaterialTheme.typography.headlineLarge,
            color = Parchment
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        if (objects.isEmpty()) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No objects yet",
                        style = MaterialTheme.typography.titleLarge,
                        color = Parchment
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Add something with a barcode to get started.",
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
                    items = objects,
                    key = { it.id }
                ) { scanObject ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = NightRaised),
                        border = BorderStroke(1.dp, Slate.copy(alpha = 0.35f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Text(
                                text = scanObject.emoji,
                                fontSize = 36.sp
                            )

                            Spacer(
                                modifier = Modifier.width(16.dp)
                            )

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = scanObject.name,
                                    style =
                                        MaterialTheme.typography.titleMedium,
                                    color = Parchment
                                )

                                Text(
                                    text = scanObject.barcodeValue,
                                    style =
                                        MaterialTheme.typography.bodySmall,
                                    color = Slate
                                )
                            }

                            TextButton(
                                onClick = {
                                    objectPendingDeletion = scanObject
                                }
                            ) {
                                Text("Delete", color = Slate)
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
            onClick = onAddObject,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SoftBrass,
                contentColor = Night
            )
        ) {
            Text("Add object")
        }

        Spacer(Modifier.height(16.dp))
    }
}
