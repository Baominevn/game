package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.anticheat.AntiCheatEngine
import com.example.model.FighterStats
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("CyberStrike", appName)
  }

  @Test
  fun `verify anti cheat state signature integrity`() {
    val antiCheat = AntiCheatEngine()
    val stats = FighterStats(level = 1, credits = 250, nanites = 30)
    val signature = antiCheat.generateStateSignature(stats)
    assertTrue("Signature must not be empty", signature.isNotEmpty())
    assertTrue("Verification must pass for valid stats", antiCheat.verifyStateIntegrity(stats, signature))
  }
}

