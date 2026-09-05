package com.example.ai

import com.example.BuildConfig
import com.example.model.EnemyFighter
import com.example.model.FighterStats
import com.example.model.RealWorldSensorData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiAiService {

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun getCombatTacticalAdvice(
        playerStats: FighterStats,
        enemy: EnemyFighter,
        sensorData: RealWorldSensorData,
        recentAction: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        // If no API key or placeholder, use the instant heuristic neural engine
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateHeuristicTacticalAdvice(playerStats, enemy, sensorData, recentAction)
        }

        try {
            val prompt = """
                You are CYBER-NEURAL CO-PILOT, a sci-fi tactical AI inside a combat android suit.
                Brief the operative in 1-2 punchy tactical sentences.
                Player HP: ${playerStats.currentHp}/${playerStats.maxHp}, Shield: ${playerStats.currentShield}/${playerStats.maxShield}, Energy: ${playerStats.currentEnergy}
                Enemy: ${enemy.name} (${enemy.title}), HP: ${enemy.currentHp}/${enemy.maxHp}, Shield: ${enemy.currentShield}/${enemy.maxShield}.
                Weakness: ${enemy.weakness}
                Real-World Telemetry: Lux=${sensorData.ambientLux} (${sensorData.lightBonusDescription}), Kinetic Charge=${sensorData.kineticEnergyCharge.toInt()}%.
                Recent Combat Action: $recentAction.
                Give immediate action directive.
            """.trimIndent()

            val requestJson = JSONObject().apply {
                val contents = JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                }
                put("contents", contents)
            }

            val requestBody = requestJson.toString().toRequestBody(jsonMediaType)
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                val jsonResponse = JSONObject(responseBody)
                val candidates = jsonResponse.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val content = firstCandidate?.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val text = parts?.optJSONObject(0)?.optString("text")

                if (!text.isNullOrBlank()) {
                    return@withContext text.trim()
                }
            }
        } catch (_: Exception) {
            // Fall back smoothly to heuristic AI
        }

        return@withContext generateHeuristicTacticalAdvice(playerStats, enemy, sensorData, recentAction)
    }

    private fun generateHeuristicTacticalAdvice(
        playerStats: FighterStats,
        enemy: EnemyFighter,
        sensorData: RealWorldSensorData,
        recentAction: String
    ): String {
        return when {
            enemy.currentShield > 50 -> {
                "TACTICAL ADVISOR: Target barrier frequency high! Fire EMP Shockwave to short-circuit ${enemy.name}'s energy shield!"
            }
            playerStats.currentHp < 120 -> {
                "WARNING: Structural armor breached! Inject Nano Stimpack immediately and activate defensive parry!"
            }
            playerStats.currentEnergy >= 60 -> {
                "OVERDRIVE READY: Maximum capacitor charge attained. Unleash Quantum Overdrive to pulverize the target!"
            }
            sensorData.ambientLux < 25f -> {
                "SHADOW PROTOCOL: Real-world darkness detected! Flank the target under stealth cover for +25% critical strike."
            }
            sensorData.kineticEnergyCharge > 50f -> {
                "KINETIC SURGE: Real physical movement detected (${sensorData.kineticEnergyCharge.toInt()}%)! Discharge kinetic burst into next attack!"
            }
            enemy.weakness.contains("Plasma", ignoreCase = true) -> {
                "WEAKNESS SCANNED: ${enemy.name} is vulnerable to thermal overload. Prioritize Plasma Blast!"
            }
            else -> {
                "TARGET LOCKED: Countering ${enemy.aiStrategy}. Maintain combo rhythm with Cyber Slash and monitor shield integrity."
            }
        }
    }
}
