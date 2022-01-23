package com.example.test

    import android.app.Notification
    import android.app.NotificationChannel
    import android.app.NotificationManager
    import android.app.PendingIntent
    import android.app.Service
    import android.content.Intent
    import android.hardware.Sensor
    import android.hardware.SensorEvent
    import android.hardware.SensorEventListener
    import android.hardware.SensorManager
    import android.hardware.camera2.CameraAccessException
    import android.hardware.camera2.CameraManager
    import android.os.Build
    import android.os.IBinder
    import android.util.Log
    import androidx.annotation.RequiresApi
    import com.example.test.Constants.Ahmed_ID


class Notification: Service() , SensorEventListener {
    private val CHANNEL_ID="The Flash Light ON"
    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor?=null


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showNotfication()
        return START_STICKY
    }

    private fun showNotfication() {
        val notificationIntent=Intent(this, MainActivity::class.java)
        val padding=PendingIntent.getActivity(
            this, 0, notificationIntent, 0
        )
        val notification = Notification
            .Builder(this, CHANNEL_ID)
            .setContentText("Run Flash")
            .setContentTitle("Flash")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(padding)
            .build()


        startForeground(Ahmed_ID, notification)

    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel=NotificationChannel(
                CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager=getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)


            sensorManager=getSystemService(SENSOR_SERVICE) as SensorManager
            val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
            for (sensor in deviceSensors) {
                Log.d("sensorsList", sensor.name)
            }
            if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
                Log.d("isSensorFound", "Sensor found")
                lightSensor=sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
            } else {
                Log.d("isSensorFound", "Sensor not found")
            }
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }


    override fun onSensorChanged(event: SensorEvent?) {
        val camera=getSystemService(CAMERA_SERVICE) as CameraManager
        val cameraListId=camera.cameraIdList[0]


        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            val lightValue=event.values[0].toInt()

            if (lightValue == 0) {
                try {
                    startService(Intent(this,Notification::class.java))
                    camera.setTorchMode(cameraListId, true)
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }

            } else {

                try {
                    stopService(Intent(this, Notification::class.java))
                    camera.setTorchMode(cameraListId, false)
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onDestroy() {
        stopForeground(true)
        stopSelf()
        super.onDestroy()

    }
}
