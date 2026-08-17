package com.example.scanrise.ui.objects

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
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
    val emojiFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val canSave =
        name.isNotBlank() &&
                isSingleEmoji(emoji) &&
                barcode.isNotBlank()

    LaunchedEffect(Unit) {
        emojiFocusRequester.requestFocus()
        keyboardController?.show()
    }

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
            onValueChange = { value ->
                if (value.isEmpty() || isSingleEmoji(value)) {
                    onEmojiChanged(value)
                }
            },
            label = {
                Text("Emoji")
            },
            supportingText = {
                Text("Choose one emoji")
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(emojiFocusRequester)
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

internal fun isSingleEmoji(value: String): Boolean {
    if (value.isBlank()) return false

    val codePoints = value.codePoints().toArray()
    val emojiBases = codePoints.filter(::isEmojiBase)
    if (emojiBases.isEmpty()) return false

    if (codePoints.any { !isEmojiBase(it) && !isEmojiComponent(it) }) {
        return false
    }

    val hasJoiner = 0x200D in codePoints
    val hasKeycap = 0x20E3 in codePoints
    val hasKeycapBase = emojiBases.any {
        it in 0x30..0x39 || it == 0x23 || it == 0x2A
    }
    val isFlag =
        emojiBases.size == 2 &&
            emojiBases.all { it in 0x1F1E6..0x1F1FF }

    if (hasKeycapBase && !hasKeycap) return false

    return emojiBases.size == 1 || hasJoiner || isFlag
}

private fun isEmojiBase(codePoint: Int): Boolean =
    codePoint in 0x1F000..0x1FAFF ||
        codePoint in 0x2600..0x27BF ||
        codePoint in 0x2300..0x23FF ||
        codePoint in 0x2B00..0x2BFF ||
        codePoint in 0x1F1E6..0x1F1FF ||
        codePoint in 0x30..0x39 ||
        codePoint == 0x23 ||
        codePoint == 0x2A ||
        codePoint == 0x00A9 ||
        codePoint == 0x00AE

private fun isEmojiComponent(codePoint: Int): Boolean =
    codePoint == 0x200D ||
        codePoint == 0x20E3 ||
        codePoint in 0xFE00..0xFE0F ||
        codePoint in 0x1F3FB..0x1F3FF ||
        codePoint in 0xE0020..0xE007F
