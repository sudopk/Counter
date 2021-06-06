package com.sudopk.counter

import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.sudopk.counter.ui.theme.CounterTheme
import com.sudopk.kandroid.notFoundByTag
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.absoluteValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private val TAG = CounterMainActivity::class.simpleName

private const val COUNT_IN_A_ROUND = 108

private val TIME_FORMATTER = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH)

class CounterMainActivity : AppCompatActivity() {
  private var mediaPlayer: MediaPlayer? = null
  private var vibrator: Vibrator? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(/*context=*/this)
    setContent {
      CounterTheme(window) {
        Surface(color = MaterialTheme.colors.background) {
          val coroutine = rememberCoroutineScope()
          CounterApp(sharedPreferences, onShowSettings = {
            supportFragmentManager.notFoundByTag(SettingsDialogFragment.TAG) { tag ->
              SettingsDialogFragment().show(supportFragmentManager, tag)
            }
          }) { count ->
            coroutine.launch {
              if (sharedPreferences.getBoolean("short_audio", false)) {
                while (mediaPlayer?.isPlaying == true) {
                  delay(50)
                }
                mediaPlayer?.start() ?: Log.d(TAG, "MediaPlayer not ready")
              }

              if (count > 0 && count % COUNT_IN_A_ROUND == 0) {
                if (sharedPreferences.getBoolean("long_vibrate", false)) {
                  vibrator?.vibrateCompat(700)
                }
              } else {
                if (sharedPreferences.getBoolean("short_vibrate", false)) {
                  delay(50)
                  vibrator?.vibrateCompat(30)
                }
              }
            }
          }
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    lifecycleScope.launch {
      vibrator = getSystemService()
      mediaPlayer = MediaPlayer.create(applicationContext, R.raw.button_click)
    }
  }

  override fun onPause() {
    super.onPause()
    lifecycleScope.launch {
      mediaPlayer?.release()
      mediaPlayer = null
    }
  }
}

@Composable
fun CounterApp(
  preferences: SharedPreferences,
  onShowSettings: () -> Unit,
  onCounterChange: (count: Int) -> Unit
) {
  val count = rememberSaveable(key = "count") { mutableStateOf(0) }
  val startTime = rememberSaveable(key = "startTime") {
    mutableStateOf(Calendar.getInstance())
  }
  if (count.value.absoluteValue == 1) {
    startTime.value = Calendar.getInstance()
  }
  Scaffold(
    topBar = {
      TopAppBar({ Text(text = stringResource(R.string.app_name)) }, actions = {
        IconButton(onClick = onShowSettings) {
          Icon(Icons.Filled.Settings, contentDescription = "Settings")
        }
      })
    },
    bottomBar = { CounterBottomBar(count, onCounterChange) },
  ) {
    Box {
      val lastClickTimeMs = remember { mutableStateOf(System.currentTimeMillis()) }
      TextButton(
        onClick = {
          val minClickInterval =
            preferences.getString("min_click_interval_ms", "500")?.toIntOrNull() ?: 500
          if (System.currentTimeMillis() < lastClickTimeMs.value + minClickInterval) {
            Log.d(TAG, "Ignoring double click")
          } else {
            lastClickTimeMs.value = System.currentTimeMillis()
            count.value++
            onCounterChange(count.value)
          }
        },
        modifier = Modifier.fillMaxSize(),
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface)
      ) {
        val currentRoundCount = count.value % COUNT_IN_A_ROUND
        Text(currentRoundCount.toString(), style = MaterialTheme.typography.h1)
      }
      Column(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(16.dp),
        horizontalAlignment = Alignment.End
      ) {
        Text("Rounds: ${count.value / COUNT_IN_A_ROUND}", style = MaterialTheme.typography.h5)
        if (count.value != 0) {
          Text(
            "Started at: ${TIME_FORMATTER.format(startTime.value.time)}",
            style = MaterialTheme.typography.subtitle1
          )
        }
      }
    }
  }
}

@Composable
private fun CounterBottomBar(count: MutableState<Int>, onCounterChange: (count: Int) -> Unit) {
  Row(
    Modifier
      .padding(16.dp)
      .fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceEvenly
  ) {
    OutlinedButton(onClick = {
      count.value--
      onCounterChange(count.value)
    }, Modifier.weight(4f)) {
      val contentDescription = "Subtract"
      Icon(painterResource(R.drawable.ic_baseline_remove_circle_outline_24), contentDescription)
      Spacer(Modifier.width(4.dp))
      Text(contentDescription)
    }
    Spacer(Modifier.weight(1f))
    OutlinedButton(onClick = {
      if (count.value != 0) {
        count.value = 0
        onCounterChange(count.value)
      }
    }, Modifier.weight(4f)) {
      Row {
        val contentDescription = "Reset"
        Icon(painterResource(R.drawable.ic_baseline_restore_24), contentDescription)
        Spacer(Modifier.width(4.dp))
        Text(contentDescription)
      }
    }
  }
}

fun Vibrator.vibrateCompat(milliSeconds: Long) {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    vibrate(VibrationEffect.createOneShot(milliSeconds, VibrationEffect.DEFAULT_AMPLITUDE))
  } else {
    @Suppress("DEPRECATION")
    vibrate(milliSeconds)
  }
}