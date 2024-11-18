package com.example.runningtrakerapp.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.runningtrakerapp.R
import com.example.runningtrakerapp.utill.Constants.ACTION_PAUSE_SERVICE
import com.example.runningtrakerapp.utill.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningtrakerapp.utill.Constants.ACTION_STOP_SERVICE
import com.example.runningtrakerapp.utill.Constants.FASTEST_LOCATION_INTERVAL
import com.example.runningtrakerapp.utill.Constants.LOCATION_UPDATE_INTERVAL
import com.example.runningtrakerapp.utill.Constants.NOTIFICATION_CHANNEL_ID
import com.example.runningtrakerapp.utill.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.runningtrakerapp.utill.Constants.NOTIFICATION_ID
import com.example.runningtrakerapp.utill.Constants.TIMER_UPDATE_INTERVAL
import com.example.runningtrakerapp.utill.TrackingUtility
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

@AndroidEntryPoint
class TrackingServices : LifecycleService() {

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder
    private lateinit var curNotificationBuilder: NotificationCompat.Builder

    private var serviceKilled = false
    private var isFirstRun = true
    private val timeRunInSeconds = MutableLiveData<Long>()

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()
        val timeRunInMillis = MutableLiveData<Long>()
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        curNotificationBuilder = baseNotificationBuilder
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        isTracking.observe(this) {
            updateLocationTracking(it)
            updateNotificationTrackingState(it)
        }
//        isTracking.observe(this, Observer {
//            updateLocationTracking(it)
//            updateNotificationTrackingState(it)
//        })
    }

    private fun postInitialValues() {
        isTracking.postValue(false)
        pathPoints.value = mutableListOf()
        timeRunInMillis.postValue(0L)
        timeRunInSeconds.postValue(0L)

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        startForegroundService()
                        isFirstRun = false

                    } else {
                        Log.d("TrackingService", "RESUME_SERVICE")
                        startForegroundService()
                    }
                }

                ACTION_PAUSE_SERVICE -> {
                    pauseService()
                    Log.d("TrackingService", "PAUSE_SERVICE")
                }

                ACTION_STOP_SERVICE -> {
                    killService()
                    Log.d("TrackingService", "STOP_SERVICE")
                }

                else -> {}
            }
        }
        return super.onStartCommand(intent, flags, startId)


    }


    private var isTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L

    private fun startRun() {
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                lapTime = System.currentTimeMillis() - timeStarted

                timeRunInMillis.postValue(timeRun + lapTime)

                if (timeRunInMillis.value!! >= lastSecondTimestamp + 1000L) {
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
    }

    @SuppressLint("MissingPermission")
    fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (TrackingUtility.hasLocationPermissions(this)) {

                val request = LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    LOCATION_UPDATE_INTERVAL
                )
                    .setMinUpdateIntervalMillis(FASTEST_LOCATION_INTERVAL)
                    .build()
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
                Log.d("TrackingService", "Started location updates.")

            } else {
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                Log.d("TrackingService", "Stopped location updates.")

            }
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            if (isTracking.value!!) {
                locationResult.locations.let { locations ->
                    for (location in locations) {
                        addPathPoint(location)
                        Log.d(
                            "TrackingService",
                            "${location.latitude}, ${location.longitude}"
                        )
                    }
                }
            }
        }
    }

    fun addPathPoint(location: Location) {
        location.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }

    }

    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun startForegroundService() {
        startRun()
        isTracking.postValue(true)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)

        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        timeRunInSeconds.observe(this) {
            if (!serviceKilled) {
                val notification = curNotificationBuilder
                    .setContentText(TrackingUtility.getFormattedStopWatchTime(it * 1000L))
                notificationManager.notify(NOTIFICATION_ID, notification.build())
            }
        }
    }

    private fun pauseService() {
        isTracking.postValue(false)
        isTimerEnabled = false
    }

    private fun killService() {
        serviceKilled = true
        isFirstRun = true
        pauseService()
        postInitialValues()
//        stopForeground(this)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun updateNotificationTrackingState(isTracking: Boolean) {
        val notificationAction = NotificationCompat.Action(
            if (isTracking) R.drawable.ic_pause_black_24dp else R.drawable.ic_run,
            if (isTracking) "pause" else "resume", // Use string resources
            createPendingIntent(isTracking)
        )

        val notificationBuilder = baseNotificationBuilder.apply {
            clearActions()
            addAction(notificationAction)
        }

        if (!serviceKilled) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        if (!serviceKilled) {
            notificationManager.notify(
                NOTIFICATION_ID,
                notificationBuilder.build()
            )
        }
    }
    fun createPendingIntent(isTracking: Boolean): PendingIntent {
        val intent = Intent(this, TrackingServices::class.java).apply {
            action = if (isTracking) ACTION_PAUSE_SERVICE else ACTION_START_OR_RESUME_SERVICE
        }
        return PendingIntent.getService(this, if (isTracking) 1 else 2, intent, FLAG_IMMUTABLE)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(notificationChannel)
    }


}