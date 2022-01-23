package com.example.test

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity()  , SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
        for (sensor in deviceSensors) {
            Log.d("sensorsList", sensor.name)
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            Log.d("isSensorFound", "Sensor found")
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        } else {
            Log.d("isSensorFound", "Sensor not found") }
    }
    // Examination is END
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL) }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this) }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onSensorChanged(event: SensorEvent?) {

        val camera = getSystemService(CAMERA_SERVICE) as CameraManager
        val cameraListId = camera.cameraIdList[0]
        //Execution of the condition

        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            val lightValue = event.values[0].toInt()

            if (lightValue == 0) {
                try {
                    startService(Intent(this,Notification::class.java))
                    camera.setTorchMode(cameraListId, true)
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }

            } else {

                try {
                    stopService(Intent(this,Notification::class.java))
                    camera.setTorchMode(cameraListId, false)
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }
        }
    }



    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

}