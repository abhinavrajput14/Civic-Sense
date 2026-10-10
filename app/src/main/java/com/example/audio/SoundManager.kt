package com.example.audio

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log

class SoundManager {
  @Volatile
  private var toneGenerator: ToneGenerator? = null

  init {
    Thread {
      try {
        toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 75)
      } catch (e: Throwable) {
        Log.w("SoundManager", "ToneGenerator could not be initialized", e)
      }
    }.start()
  }

  fun playClick(enabled: Boolean = true) {
    if (!enabled) return
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 40)
    } catch (e: Exception) {
      Log.d("SoundManager", "Audio note", e)
    }
  }

  fun playCoin(enabled: Boolean = true) {
    if (!enabled) return
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 80)
    } catch (e: Exception) {
      Log.d("SoundManager", "Audio note", e)
    }
  }

  fun playSuccess(enabled: Boolean = true) {
    if (!enabled) return
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_CDMA_CONFIRM, 180)
    } catch (e: Exception) {
      Log.d("SoundManager", "Audio note", e)
    }
  }

  fun playFail(enabled: Boolean = true) {
    if (!enabled) return
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 250)
    } catch (e: Exception) {
      Log.d("SoundManager", "Audio note", e)
    }
  }

  fun playFanfare(enabled: Boolean = true) {
    if (!enabled) return
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 300)
    } catch (e: Exception) {
      Log.d("SoundManager", "Audio note", e)
    }
  }

  /**
   * Cheerful, polished victory jingle for level completion.
   * Plays a quick, pleasant musical sequence (~1.2s total) using ToneGenerator in a background thread.
   * Safe, lightweight, game-like, satisfying and child/teen friendly.
   */
  fun playVictoryJingle(enabled: Boolean = true) {
    if (!enabled) return
    Thread {
      try {
        val tg = toneGenerator ?: return@Thread
        // Tone 1: Bright rising start (Do)
        tg.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
        Thread.sleep(140)
        // Tone 2: Upward step (Mi)
        tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 140)
        Thread.sleep(160)
        // Tone 3: Bright affirmation (Sol)
        tg.startTone(ToneGenerator.TONE_CDMA_CONFIRM, 180)
        Thread.sleep(200)
        // Tone 4: Resonant high triumph finish! (High Do)
        tg.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 450)
      } catch (e: Exception) {
        Log.d("SoundManager", "Victory jingle note", e)
      }
    }.start()
  }

  fun release() {
    try {
      toneGenerator?.release()
      toneGenerator = null
    } catch (e: Exception) {
      // Ignored
    }
  }
}
