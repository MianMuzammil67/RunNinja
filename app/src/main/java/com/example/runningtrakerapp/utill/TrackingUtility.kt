package com.example.runningtrakerapp.utill

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.runningtrakerapp.services.Polyline
import java.util.Locale
import java.util.concurrent.TimeUnit

object TrackingUtility {

    fun hasLocationPermissions(context: Context) =


        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    fun calculatePolylineLength(polyline: Polyline): Float {
        var distance = 0f
        for (i in 0..polyline.size - 2) {
            val position1 = polyline[i]
            val position2 = polyline[i + 1]

            val result = FloatArray(1)

            Location.distanceBetween(
                position1.latitude,
                position1.longitude,
                position2.latitude,
                position2.longitude,
                result
            )
            distance += result[0]
        }
        return distance
    }


    // Method to check if location services are enabled
    fun canGetLocation(context: Context): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        var networkEnabled = false

        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
            // Handle exception if necessary
        }

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
            // Handle exception if necessary
        }

        return gpsEnabled && networkEnabled
    }

    // Method to show alert dialog if location services are not enabled
    fun AppCompatActivity.showSettingsAlert() {
        AlertDialog.Builder(this).apply {
            // Setting Dialog Title
            setTitle("Error!")

            // Setting Dialog Message
            setMessage("Please enable location services.")

            // On pressing Settings button
            setPositiveButton("ok") { dialog, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }

            // Optional: On pressing Cancel button (if needed)
            setNegativeButton("cancel") { dialog, _ ->
                dialog.dismiss()
            }

            show()
        }
    }


    fun getFormattedStopWatchTime(ms: Long, includeMillis: Boolean = false): String {
        val builder = StringBuilder()
        var remaining = ms

        val hours = TimeUnit.MILLISECONDS.toHours(remaining)
        remaining -= TimeUnit.HOURS.toMillis(hours)
        builder.append(String.format(Locale.ENGLISH, "%02d:", hours))

        val minutes = TimeUnit.MILLISECONDS.toMinutes(remaining)
        remaining -= TimeUnit.MINUTES.toMillis(minutes)
        builder.append(String.format("%02d:", minutes))

        val seconds = TimeUnit.MILLISECONDS.toSeconds(remaining)
        remaining -= TimeUnit.SECONDS.toMillis(seconds)
        builder.append(String.format("%02d", seconds))

        if (includeMillis) {
            val millis = remaining / 10
            builder.append(":${String.format("%02d", millis)}")
        }

        return builder.toString()
    }


//    fun getFormattedStopWatchTime(ms: Long, includeMillis: Boolean = false): String {
//        var remaining = ms
//
//        val hours = TimeUnit.MILLISECONDS.toHours(remaining)
//        remaining -= TimeUnit.HOURS.toMillis(hours)
//
//        val minutes = TimeUnit.MILLISECONDS.toMinutes(remaining)
//        remaining -= TimeUnit.MINUTES.toMillis(minutes)
//
//        val seconds = TimeUnit.MILLISECONDS.toSeconds(remaining)
//        remaining -= TimeUnit.SECONDS.toMillis(seconds)
//
//        val millis = if (includeMillis) remaining / 10 else 0
//
//        return String.format(
//            "%02d:%02d:%02d%s",
//            hours,
//            minutes,
//            seconds,
//            if (includeMillis) ":${millis}" else ""
//        )
//    }


    /*

    //        ContextCompat.checkSelfPermission(
    //            context,
    //            Manifest.permission.ACCESS_FINE_LOCATION
    //        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
    //            context,
    //            Manifest.permission.ACCESS_COARSE_LOCATION
    //        ) == PackageManager.PERMISSION_GRANTED && (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || ContextCompat.checkSelfPermission(
    //            context,
    //            Manifest.permission.ACCESS_BACKGROUND_LOCATION
    //        ) == PackageManager.PERMISSION_GRANTED)

    //    fun checkPermissions(context: Context) {
    //        if (ContextCompat.checkSelfPermission(
    //                context,
    //                Manifest.permission.ACCESS_FINE_LOCATION
    //            ) == PackageManager.PERMISSION_GRANTED
    //        ) {
    //            // Fine Location permission is granted
    //            // Check if current android version >= 11, if >= 11 check for Background Location permission
    //            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
    //                if (ContextCompat.checkSelfPermission(
    //                        context,
    //                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    //                    ) == PackageManager.PERMISSION_GRANTED
    //                ) {
    //                    // Background Location Permission is granted so do your work here
    //                } else {
    //                    // Ask for Background Location Permission
    //                    askPermissionForBackgroundUsage()
    //                }
    //            }
    //        } else {
    //            // Fine Location Permission is not granted so ask for permission
    //            askForLocationPermission(context)
    //        }
    //    }
    //
    //    private fun askForLocationPermission(context: Context) {
    //        if (ActivityCompat.shouldShowRequestPermissionRationale(
    //                context as Activity,
    //                Manifest.permission.ACCESS_FINE_LOCATION
    //            )
    //        ) {
    //            Builder(this)
    //                .setTitle("Permission Needed!")
    //                .setMessage("Location Permission Needed!")
    //                .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
    //                    ActivityCompat.requestPermissions(
    //                        context,
    //                        arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
    //                        1
    ////                        LOCATION_PERMISSION_CODE
    //                    )
    //                })
    //                .setNegativeButton("CANCEL", DialogInterface.OnClickListener { dialog, which ->
    //                    // Permission is denied by the user
    //                })
    //                .create().show()
    //        } else {
    //            ActivityCompat.requestPermissions(
    //                context,
    //                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION), 1
    //            )
    //        }
    //    }
    //
    //    fun askPermissionForBackgroundUsage(context: Context) {
    //        if (ActivityCompat.shouldShowRequestPermissionRationale(
    //                context as Activity,
    //                Manifest.permission.ACCESS_BACKGROUND_LOCATION
    //            )
    //        ) {
    //            Builder(this)
    //                .setTitle("Permission Needed!")
    //                .setMessage("Background Location Permission Needed!, tap \"Allow all time in the next screen\"")
    //                .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
    //                    ActivityCompat.requestPermissions(
    //                        context,
    //                        arrayOf<String>(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
    ////                        BACKGROUND_LOCATION_PERMISSION_CODE
    //                        2
    //                    )
    //                })
    //                .setNegativeButton("CANCEL", DialogInterface.OnClickListener { dialog, which ->
    //                    // User declined for Background Location Permission.
    //                })
    //                .create().show()
    //        } else {
    //            ActivityCompat.requestPermissions(
    //                this,
    //                arrayOf<String>(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
    //                2
    //            )
    //        }
    //    }

    //    fun onRequestPermissionsResult(
    //        requestCode: Int,
    //        permissions: Array<String?>,
    //        grantResults: IntArray
    //    ) {
    //        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    //
    //        if (requestCode == 1) {
    //            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
    //                // User granted location permission
    //                // Now check if android version >= 11, if >= 11 check for Background Location Permission
    //                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
    //                    if (ContextCompat.checkSelfPermission(
    //                            this@MainActivity,
    //                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
    //                        ) == PackageManager.PERMISSION_GRANTED
    //                    ) {
    //                        // Background Location Permission is granted so do your work here
    //                    } else {
    //                        // Ask for Background Location Permission
    //                        askPermissionForBackgroundUsage()
    //                    }
    //                }
    //            } else {
    //                // User denied location permission
    //            }
    //        } else if (requestCode == 2) {
    //            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
    //                // User granted for Background Location Permission.
    //            } else {
    //                // User declined for Background Location Permission.
    //            }
    //        }
    //    }

















    //    fun hasLocationPermissions(context: Context) =
    //        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
    //            EasyPermissions.hasPermissions(
    //                context,
    //                Manifest.permission.ACCESS_FINE_LOCATION,
    //                Manifest.permission.ACCESS_COARSE_LOCATION,
    //
    //                )
    //        } else {
    //            EasyPermissions.hasPermissions(
    //                context,
    //                Manifest.permission.ACCESS_FINE_LOCATION,
    //                Manifest.permission.ACCESS_COARSE_LOCATION,
    //                Manifest.permission.ACCESS_BACKGROUND_LOCATION
    //
    //            ) && hasBackgroundLocationPermission(context)
    //        }

    //    private fun hasBackgroundLocationPermission(context: Context): Boolean {
    //        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    //            EasyPermissions.hasPermissions(
    //                context,
    //                Manifest.permission.ACCESS_BACKGROUND_LOCATION
    //            )
    //        } else {
    //            true
    //        }
    //    }
        */
}