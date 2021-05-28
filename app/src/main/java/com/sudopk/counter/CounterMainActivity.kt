package com.sudopk.counter

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.sudopk.counter.ui.theme.CounterTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CounterMainActivity : ComponentActivity() {
  private var mediaPlayer: MediaPlayer? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      CounterTheme {
        Surface(color = MaterialTheme.colors.background) {
          val coroutine = rememberCoroutineScope()
          CounterApp {
            coroutine.launch {
              while (mediaPlayer?.isPlaying == true) {
                delay(50)
              }
              mediaPlayer?.start() ?: Log.d(
                CounterMainActivity::class.simpleName,
                "MediaPlayer not ready"
              )
            }
          }
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    lifecycleScope.launch {
      mediaPlayer = MediaPlayer.create(applicationContext, R.raw.button_click)
    }
  }

  override fun onPause() {
    super.onPause()
    mediaPlayer?.release()
    mediaPlayer = null
  }
}

@Composable
fun CounterApp(onCounterChange: () -> Unit) {
  val count = rememberSaveable(key = "count") { mutableStateOf(0) }
  Log.d(CounterMainActivity::class.simpleName, "Count value: ${count.value}")
  Scaffold(
    topBar = { CounterTopBar(count, onCounterChange) },
    bottomBar = {
      Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        Button(onClick = {
          count.value--
          onCounterChange()
        },Modifier.width(150.dp)) {
          Text("Subtract")
        }
        Spacer(Modifier.width(16.dp))
        Button(onClick = {
          if (count.value != 0) {
            count.value = 0
            onCounterChange()
          }
        },Modifier.width(150.dp)) {
          Text("Reset")
        }
      }
    }
  ) {
    Button(
      {
        count.value++
        onCounterChange()
      },
      Modifier
        .fillMaxSize(),
      colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface)
    ) {
      Text(count.value.toString(), fontSize = 100.sp)
    }
  }
}

@Composable
private fun CounterTopBar(count: MutableState<Int>, onCounterChange: () -> Unit) {
  TopAppBar(title = { Text(text = stringResource(R.string.app_name)) },
            actions = {
              // RowScope here, so these icons will be placed horizontally
              IconButton(onClick = {
                if (count.value != 0) {
                  count.value = 0
                  onCounterChange()
                }
              }) {
                Icon(
                  painterResource(R.drawable.ic_baseline_restore_24),
                  contentDescription = "Reset",
                )
              }
              IconButton(onClick = {
                if (count.value > 0) {
                  count.value--
                  onCounterChange()
                }
              }) {
                Icon(
                  painterResource(R.drawable.ic_baseline_remove_circle_outline_24),
                  contentDescription = "Subtract",
                )
              }
            }
  )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
  CounterTheme {
//    CounterApp(mp)
  }
}