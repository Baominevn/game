package com.example.sensors

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.BatteryManager
import com.example.model.RealWorldSensorData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sqrt

/**
 * Bridges real-world physical telemetry (ambient light, kinetic accelerometer,
 * battery power, and geo-sector coordinates) directly into the game combat matrix.
 */
class RealWorldSensorManager(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val lightSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)
    private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _sensorData = MutableStateFlow(RealWorldSensorData())
    val sensorData: StateFlow<RealWorldSensorData> = _sensorData.asStateFlow()

    private var lastAccX = 0f
    private var lastAccY = 0f
    private var lastAccZ = 0f
    private var kineticAccumulator = 0f

    var onPhysicalShakeDetected: (() -> Unit)? = null

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctxt: Context?, intent: Intent?) {
            intent?.let {
                val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val status = it.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL
                val batteryPct = if (level >= 0 && scale > 0) (level * 100) / scale else 85

                _sensorData.value = _sensorData.value.copy(
                    batteryLevel = batteryPct,
                    isCharging = isCharging
                )
            }
        }
    }

    fun startListening() {
        lightSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        accelerometer?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        try {
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            context.registerReceiver(batteryReceiver, filter)
        } catch (_: Exception) {}
    }

    fun stopListening() {
        sensorManager?.unregisterListener(this)
        try {
            context.unregisterReceiver(batteryReceiver)
        } catch (_: Exception) {}
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        when (event.sensor.type) {
            Sensor.TYPE_LIGHT -> {
                val lux = event.values.getOrNull(0) ?: 50f
                val desc = when {
                    lux < 25f -> "SHADOW STEALTH (+25% Crit & Evasion)"
                    lux > 400f -> "SOLAR OVERCHARGE (+30% Energy Regen)"
                    else -> "Standard Ambient Illumination"
                }
                _sensorData.value = _sensorData.value.copy(
                    ambientLux = lux,
                    lightBonusDescription = desc
                )
            }
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values.getOrNull(0) ?: 0f
                val y = event.values.getOrNull(1) ?: 0f
                val z = event.values.getOrNull(2) ?: 0f

                val deltaX = kotlin.math.abs(x - lastAccX)
                val deltaY = kotlin.math.abs(y - lastAccY)
                val deltaZ = kotlin.math.abs(z - lastAccZ)
                val totalMovement = sqrt((deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ).toDouble()).toFloat()

                lastAccX = x
                lastAccY = y
                lastAccZ = z

                if (totalMovement > 2.0f) {
                    kineticAccumulator = (kineticAccumulator + (totalMovement * 0.4f)).coerceAtMost(100f)
                    _sensorData.value = _sensorData.value.copy(
                        kineticEnergyCharge = kineticAccumulator,
                        isPhysicalMoving = true
                    )
                } else {
                    _sensorData.value = _sensorData.value.copy(
                        isPhysicalMoving = false
                    )
                }

                // Shake detection for quick defensive counter-parry
                if (totalMovement > 18.0f) {
                    onPhysicalShakeDetected?.invoke()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun consumeKineticCharge(): Float {
        val charge = kineticAccumulator
        kineticAccumulator = 0f
        _sensorData.value = _sensorData.value.copy(kineticEnergyCharge = 0f)
        return charge
    }

    fun scanRealWorldGeoSector(lat: Double = 21.0285, lng: Double = 105.8542): String {
        val sectorCode = "Sector-${(kotlin.math.abs(lat * 100)).toInt() % 99}-${(kotlin.math.abs(lng * 100)).toInt() % 99}"
        _sensorData.value = _sensorData.value.copy(
            geoSector = sectorCode,
            latitude = lat,
            longitude = lng
        )
        return sectorCode
    }
}
