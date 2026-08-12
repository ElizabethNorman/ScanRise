package com.example.scanrise.ui.objects

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ObjectDetailsScreen(
    barcode: String,
    name: String,
    emoji: String,
    errorMessage: String?,
    onNameChanged: (String) -> Unit,
    onEmojiChanged: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {

    val canSave =
        name.isNotBlank() &&
                emoji.isNotBlank() &&
                barcode.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Add Object",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        Text(
            text = "Barcode"
        )

        Text(
            text = barcode,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        OutlinedTextField(
            value = emoji,
            onValueChange = onEmojiChanged,
            label = {
                Text("Emoji")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = onNameChanged,
            label = {
                Text("Name")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        errorMessage?.let {

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        Button(
            onClick = onSave,
            enabled = canSave,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Object")
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        TextButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
    }
}