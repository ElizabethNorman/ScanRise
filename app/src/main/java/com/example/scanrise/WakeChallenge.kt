package com.example.scanrise

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.sqrt
import kotlin.random.Random

internal object WakeChallengeSession {
    private fun prefs(context: Context) = context.getSharedPreferences("wake_challenges", Context.MODE_PRIVATE)
    fun start(context: Context, id: Long) {
        prefs(context).edit().putLong("seed_$id", Random.nextLong()).commit()
    }
    fun seed(context: Context, id: Long): Long {
        val prefs = prefs(context)
        if (!prefs.contains("seed_$id")) start(context, id)
        return prefs.getLong("seed_$id", 0)
    }
    fun clear(context: Context, id: Long) {
        prefs(context).edit().remove("seed_$id").apply()
    }
}

internal enum class ChallengeKind(val title: String) {
    MATH("Mental math"), WALK("Walk it off"), MEMORY("Memory sequence")
}

internal fun selectChallenge(seed: Long, canWalk: Boolean): ChallengeKind {
    val random = Random(seed)
    if (!canWalk) {
        return if (random.nextBoolean()) ChallengeKind.MATH else ChallengeKind.MEMORY
    }
    return when (random.nextInt(4)) {
        0, 1 -> ChallengeKind.WALK
        2 -> ChallengeKind.MATH
        else -> ChallengeKind.MEMORY
    }
}

@Composable
internal fun WakeChallenge(context: Context, seed: Long, onComplete: () -> Unit) {
    val manager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val canWalk = remember { manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null }
    val kind = remember(seed, canWalk) { selectChallenge(seed, canWalk) }
    Text(kind.title, style = MaterialTheme.typography.headlineSmall)
    Spacer(Modifier.height(16.dp))
    when (kind) {
        ChallengeKind.MATH -> MathChallenge(seed, onComplete)
        ChallengeKind.MEMORY -> MemoryChallenge(seed, onComplete)
        ChallengeKind.WALK -> WalkingChallenge(manager, seed, onComplete)
    }
}

@Composable
private fun MathChallenge(seed: Long, onComplete: () -> Unit) {
    var solved by rememberSaveable(seed) { mutableIntStateOf(0) }
    var answer by rememberSaveable(seed) { mutableStateOf("") }
    var incorrect by rememberSaveable(seed) { mutableStateOf(false) }
    val problem = remember(seed, solved) {
        val random = Random(seed + solved)
        if (random.nextBoolean()) {
            val a = random.nextInt(12, 60)
            val b = random.nextInt(12, 60)
            Pair("$a + $b", a + b)
        } else {
            val a = random.nextInt(3, 13)
            val b = random.nextInt(3, 13)
            Pair("$a × $b", a * b)
        }
    }
    Text("Solve 5 questions. ${solved.coerceAtMost(5)} / 5 complete")
    Text(problem.first, style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(16.dp))
    OutlinedTextField(answer, { answer = it.filter(Char::isDigit).take(4); incorrect = false },
        label = { Text("Your answer") }, singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), isError = incorrect)
    if (incorrect) Text("Try that one again.")
    Button(onClick = {
        if (answer.toIntOrNull() == problem.second) {
            solved++
            answer = ""
            if (solved >= 5) onComplete()
        } else incorrect = true
    }, enabled = answer.isNotEmpty()) { Text("Check answer") }
}

@Composable
private fun MemoryChallenge(seed: Long, onComplete: () -> Unit) {
    var round by rememberSaveable(seed) { mutableIntStateOf(0) }
    var attempt by rememberSaveable(seed) { mutableIntStateOf(0) }
    var entered by rememberSaveable(seed) { mutableIntStateOf(0) }
    var showing by rememberSaveable(seed) { mutableStateOf(true) }
    var lit by remember { mutableIntStateOf(-1) }
    var message by rememberSaveable(seed) { mutableStateOf("") }
    val sequence = remember(seed, round) {
        val random = Random(seed xor (round.toLong() + 100))
        List(round + 3) { random.nextInt(4) }
    }
    LaunchedEffect(round, attempt) {
        showing = true
        entered = 0
        lit = -1
        delay(800)
        for (tile in sequence) {
            lit = tile
            delay(650)
            lit = -1
            delay(250)
        }
        showing = false
    }
    Text("Round ${round + 1} of 3 · ${sequence.size} tiles")
    Text(if (showing) "Watch the tiles…" else "Repeat the sequence: $entered / ${sequence.size}")
    if (message.isNotBlank()) Text(message)
    for (row in 0..1) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            for (column in 0..1) {
                val tile = row * 2 + column
                Button(onClick = {
                    if (entered < sequence.size && tile == sequence[entered]) {
                        entered++
                        if (entered == sequence.size) {
                            if (round == 2) onComplete()
                            else { showing = true; message = "Good! Next round."; round++ }
                        }
                    } else {
                        message = "Try this round again."
                        attempt++
                        showing = true
                    }
                }, enabled = !showing,
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = if (lit == tile) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = if (lit == tile) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier.size(96.dp)) { Text("${tile + 1}") }
            }
        }
        Spacer(Modifier.height(12.dp))
    }
    TextButton(onClick = { attempt++; showing = true }, enabled = !showing) { Text("Replay sequence") }
}

@Composable
private fun WalkingChallenge(manager: SensorManager, seed: Long, onComplete: () -> Unit) {
    var steps by rememberSaveable(seed) { mutableIntStateOf(0) }
    var sensorFailed by remember { mutableStateOf(false) }
    val currentComplete by rememberUpdatedState(onComplete)
    DisposableEffect(manager) {
        val sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val listener = object : SensorEventListener {
            private val detector = WalkingStepDetector()
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
            override fun onSensorChanged(event: SensorEvent) {
                val magnitude = sqrt(event.values.sumOf { (it * it).toDouble() })
                if (detector.onSample(magnitude, event.timestamp) && steps < 50) {
                    steps++
                    if (steps == 50) currentComplete()
                }
            }
        }
        sensorFailed = sensor == null || !manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
        onDispose { manager.unregisterListener(listener) }
    }
    if (sensorFailed) {
        Text("Motion sensing is unavailable. Complete math instead.")
        MathChallenge(seed, onComplete)
    } else {
        Text("Get up and walk 50 steps with your phone in your hand or pocket.")
        Text("$steps / 50", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(24.dp))
        LinearProgressIndicator(progress = { steps / 50f }, modifier = Modifier.fillMaxWidth())
        Text("Steps are estimated from phone movement.", modifier = Modifier.padding(top = 12.dp))
    }
}

/** Counts a rising movement peak, followed by a release, at a plausible walking cadence. */
internal class WalkingStepDetector {
    private var ready = false
    private var lastStep: Long? = null
    fun onSample(magnitude: Double, timestamp: Long): Boolean {
        if (magnitude < 10.5) ready = true
        val previous = lastStep
        if (ready && magnitude > 12.5 && (previous == null || timestamp - previous > 350_000_000L)) {
            ready = false
            lastStep = timestamp
            return true
        }
        return false
    }
}
