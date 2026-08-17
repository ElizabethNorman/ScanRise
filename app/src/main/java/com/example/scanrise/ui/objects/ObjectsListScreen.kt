package com.example.scanrise.ui.objects


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
            .padding(24.dp)
    ) {

        Text(
            text = "Objects",
            style = MaterialTheme.typography.headlineLarge
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

                Text(
                    text = "No objects yet"
                )
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
                        modifier = Modifier.fillMaxWidth()
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
                                        MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = scanObject.barcodeValue,
                                    style =
                                        MaterialTheme.typography.bodySmall
                                )
                            }

                            TextButton(
                                onClick = {
                                    objectPendingDeletion = scanObject
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
            onClick = onAddObject,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("+ Add Object")
        }
    }
}
